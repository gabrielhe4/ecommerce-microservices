# E-Commerce Microservices Platform

A portfolio project demonstrating a microservices-based e-commerce backend built with Spring Boot, featuring synchronous and asynchronous inter-service communication, public-key JWT authentication, and containerized deployment.

---

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Technology Stack](#technology-stack)
- [High-Level Architecture](#high-level-architecture)
- [Services](#services)
- [Communication Patterns](#communication-patterns)
- [Security Model](#security-model)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Key Design Decisions](#key-design-decisions)
- [Known Limitations & Future Work](#known-limitations--future-work)

---

## Overview

This project implements the backend of an e-commerce platform as a set of independent microservices. Each service owns its own database, communicates with others through well-defined boundaries (REST for synchronous calls, a message broker for asynchronous events), and sits behind a single API gateway that handles routing and authentication.

The system demonstrates several patterns common to production microservice architectures: database-per-service isolation, synchronous inter-service calls, event-driven communication, price snapshotting for order immutability, and centralized authentication with role-based access control.

---

## Requirements

### Functional

- **Product catalog** — create, read, update, and delete products, organized by category. Products support time-bounded percentage discounts.
- **Categories** — classify products; a category cannot be deleted while it still has products.
- **Orders** — authenticated users can place orders. Each order captures a snapshot of product names and prices at purchase time, so later price or discount changes do not alter historical orders.
- **Inventory** — stock levels per product, automatically decremented when an order is placed.
- **Authentication** — users can register and log in. The system issues signed JWTs and enforces role-based access (regular users vs. administrators).
- **Access control** — product and category browsing is public; creating or modifying catalog data requires an administrator; placing an order requires an authenticated user.

### Non-Functional

- Each service is independently deployable and owns its own database.
- Services communicate without direct database sharing.
- The system tolerates a downstream consumer being temporarily unavailable (events are held until the consumer recovers).
- A single entry point (the gateway) fronts all services.
- The platform is prepared to serve a browser-based frontend (CORS configured for Angular/React development origins).

---

## Technology Stack

| Concern | Technology |
|---|---|
| Language / Runtime | Java 17 |
| Framework | Spring Boot |
| API Gateway | Spring Cloud Gateway (Server WebMVC) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL 16 (one database per service) |
| Messaging | RabbitMQ |
| Authentication | JWT (RS256, public/private key pair) |
| Password Hashing | BCrypt |
| Containerization | Docker / Podman + Compose |

---

## High-Level Architecture

```
                          ┌──────────────┐
                          │   Client     │
                          │ (curl / SPA) │
                          └──────┬───────┘
                                 │  HTTPS (single entry point)
                                 ▼
                          ┌──────────────┐
                          │   Gateway    │  :8080
                          │ - routing    │
                          │ - JWT verify │
                          │ - identity   │
                          │   forwarding │
                          │ - CORS       │
                          └──────┬───────┘
             ┌──────────────┬────┴─────┬──────────────┐
             ▼              ▼          ▼              ▼
      ┌────────────┐ ┌────────────┐ ┌──────────┐ ┌────────────┐
      │  Product   │ │   Order    │ │Inventory │ │    Auth    │
      │  :8081     │ │  :8082     │ │  :8083   │ │   :8084    │
      └─────┬──────┘ └──┬─────┬───┘ └────┬─────┘ └─────┬──────┘
            │           │     │          │             │
            │      (sync│REST)│          │             │
            │           │     │ (async event)          │
            │           │     ▼          ▲             │
            │           │  ┌─────────────┴──┐          │
            │           │  │   RabbitMQ     │          │
            │           │  │  order.exchange│          │
            │           │  └────────────────┘          │
            ▼           ▼                  ▼            ▼
      ┌──────────┐ ┌──────────┐     ┌───────────┐ ┌──────────┐
      │ products │ │  orders  │     │ inventory │ │   auth   │
      │    DB    │ │    DB    │     │    DB     │ │    DB    │
      └──────────┘ └──────────┘     └───────────┘ └──────────┘
                    PostgreSQL (one database per service)
```

**Flow of a typical order:**

1. Client authenticates via the gateway (`POST /auth/login`) and receives a JWT.
2. Client places an order (`POST /api/orders`) with the token attached.
3. The gateway validates the token's signature with the public key, then forwards the request to the Order service, injecting the user's identity as headers (`X-User-Id`, `X-User-Role`).
4. Order calls Product (synchronous REST) to fetch current name and price, snapshotting them onto the order line.
5. Order persists the order, then publishes an `OrderPlacedEvent` to RabbitMQ.
6. Inventory consumes the event asynchronously and decrements stock. If Inventory is down, the event waits in the queue until it recovers.

---

## Services

### Gateway (`:8080`)

The single externally exposed entry point. Responsibilities:

- Routes requests to backend services (defined programmatically via the Java Routes API).
- Validates incoming JWTs using the public key (acts as an OAuth2 resource server).
- Enforces route-level authorization (public reads, admin-only writes, authenticated orders).
- Extracts identity from the validated token and forwards it downstream as `X-User-Id` / `X-User-Role` headers.
- Applies CORS configuration for browser clients.

### Product Service (`:8081`)

Owns the product catalog and categories.

- Products belong to a category (many-to-one).
- Prices use `BigDecimal` to avoid floating-point rounding errors.
- Discounts are stored as a percentage with an optional validity window; the effective price is computed on demand (never persisted stale).
- Category deletion is blocked while products reference it.

### Order Service (`:8082`)

Handles order placement and retrieval.

- Calls Product synchronously to validate products and capture current prices.
- Snapshots product name and unit price onto each order line, so orders are immutable historical records.
- Publishes an `OrderPlacedEvent` after persistence.
- Reads the ordering user's identity from the gateway-forwarded `X-User-Id` header, not from the request body.

### Inventory Service (`:8083`)

Tracks stock levels per product.

- Consumes `OrderPlacedEvent` from RabbitMQ and decrements stock accordingly.
- Stock is keyed by product ID (a cross-service reference, no foreign key).

### Auth Service (`:8084`)

Issues and signs authentication tokens.

- Stores users with BCrypt-hashed passwords.
- Exposes `/auth/register` and `/auth/login`.
- Signs JWTs with an RSA private key (RS256). Only this service holds the private key.

---

## Communication Patterns

### Synchronous (REST)

Order → Product, when an order is placed. Order needs the current product name and price immediately to build the order, so a blocking call is appropriate. This introduces temporal coupling: if Product is unavailable, order placement fails.

### Asynchronous (Events)

Order → RabbitMQ → Inventory, after an order is placed. Order publishes an event and moves on without waiting for Inventory. The two services are decoupled: Order does not know or care whether Inventory is running. If Inventory is offline, the event is retained in the queue and processed when it recovers. This is the resilience benefit of event-driven communication.

**RabbitMQ topology:**

- Exchange: `order.exchange` (topic)
- Queue: `order.placed.inventory.queue` (durable)
- Routing key: `order.placed`
- Binding connects the queue to the exchange for that routing key.

The exchange-in-the-middle design allows additional consumers (e.g. a future notifications service) to subscribe to the same events without any change to the Order service.

---

## Security Model

### Authentication

- The Auth service signs JWTs with an **RSA private key** (RS256).
- The Gateway verifies signatures with the corresponding **public key**.
- Because verification only needs the public key, the gateway can validate tokens but can never forge them. Only the Auth service can issue tokens.
- Tokens carry the user ID (`sub` claim) and role (`role` claim). Access tokens are short-lived (15 minutes).

### Authorization

Enforced at the gateway per route:

| Route | Access |
|---|---|
| `GET /api/products/**`, `GET /api/categories/**` | Public |
| `/api/admin/**` | `ADMIN` role required |
| `POST /api/orders`, `/api/orders/**` | Any authenticated user |
| `/auth/**` | Public (login / register) |

- **401 Unauthorized** — no valid token.
- **403 Forbidden** — valid token, insufficient role.

### Identity Propagation

The gateway extracts identity from the validated token and forwards it to services as `X-User-Id` and `X-User-Role` headers. Services trust these headers rather than re-validating the token. This keeps backend services simple, at the cost of relying on network isolation (see Limitations).

### Network Isolation

Only the gateway is published to the host. Backend services are reachable only over the internal container network, so requests cannot bypass the gateway to reach a service directly.

### Auth Flow (Sequence)

The authentication flow has two phases: obtaining a token (login), and using it (authenticated request). Note the asymmetry — the Auth service **signs** with the private key, while the Gateway **verifies** with the public key.

```
LOGIN PHASE
───────────
Client            Gateway           Auth Service
  │                  │                    │
  │ POST /auth/login │                    │
  │─────────────────>│                    │
  │                  │  routes to auth    │
  │                  │───────────────────>│
  │                  │                    │ verify password (BCrypt)
  │                  │                    │ sign JWT (private key)
  │                  │   signed JWT       │
  │<─────────────────┼────────────────────│
  │ (stores token)   │                    │


AUTHENTICATED REQUEST PHASE
───────────────────────────
Client            Gateway          Backend Service
  │                  │                    │
  │ request +        │                    │
  │ Bearer <token>   │                    │
  │─────────────────>│                    │
  │                  │ verify signature   │
  │                  │ (public key)       │
  │                  │ check role vs route│
  │                  │                    │
  │                  │ forward request +  │
  │                  │ X-User-Id/Role     │
  │                  │───────────────────>│
  │                  │                    │ trusts identity header,
  │                  │                    │ handles request
  │                  │      response      │
  │<─────────────────┼────────────────────│
  │                  │                    │
```

**Login phase:** the client authenticates once; the Auth service validates the password against its BCrypt hash and returns a JWT signed with the RSA private key.

**Authenticated request phase:** on every subsequent request, the Gateway verifies the token's signature with the public key and checks the caller's role against the route's authorization rules. It then forwards the request to the backend service with the user's identity attached as headers, which the service trusts. A request with no token is rejected with 401; a valid token lacking the required role is rejected with 403.

---

## Getting Started

### Prerequisites

- Docker or Podman with Compose
- Java 17 and Maven (only if running services outside containers)
- `openssl` (to generate the JWT key pair)

### 1. Generate the RSA key pair

```bash
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem
openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_pkcs8.pem -nocrypt
```

Place the keys:

- `private_pkcs8.pem` → `auth-service/src/main/resources/keys/`
- `public.pem` → `api-gateway/src/main/resources/keys/`

> The private key must never be committed in a real project. For production it would be supplied via a secrets manager, not baked into the image.

### 2. Ensure databases are declared

`init/init-databases.sql`:

```sql
CREATE DATABASE ecommerce_products;
CREATE DATABASE ecommerce_orders;
CREATE DATABASE ecommerce_inventory;
CREATE DATABASE ecommerce_auth;
```

### 3. Start the stack

```bash
docker compose down -v      # -v needed on first run so the init script creates the databases
docker compose up --build
```

(Substitute `podman-compose` if using Podman.)

### 4. Verify

```bash
# Register and log in
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}'

# Browse products (public)
curl http://localhost:8080/api/products
```

### Running services from the IDE

A databases-only compose file (Postgres + RabbitMQ, no service builds) is available for local development. Services then connect to `localhost` and can be debugged directly in the IDE. Application configs default to `localhost`; the full compose file overrides these with container names via environment variables.

---

## API Reference

### Auth

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register a new user (role: USER) |
| POST | `/auth/login` | Public | Authenticate, returns access token |

### Products & Categories

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/products` | Public | List products |
| GET | `/api/products/{id}` | Public | Get a product |
| POST | `/api/admin/products` | ADMIN | Create a product |
| PUT | `/api/admin/products/{id}` | ADMIN | Update a product |
| DELETE | `/api/admin/products/{id}` | ADMIN | Delete a product |
| GET | `/api/categories` | Public | List categories |
| POST | `/api/admin/categories` | ADMIN | Create a category |
| PUT | `/api/admin/categories/{id}` | ADMIN | Update a category |
| DELETE | `/api/admin/categories/{id}` | ADMIN | Delete a category |

### Orders

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/orders` | User | Place an order (userId from token) |
| GET | `/api/orders/{id}` | User | Get an order |
| GET | `/api/orders` | User | List the current user's orders |

### Inventory

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/stock/{productId}` | (see note) | Get stock for a product |
| POST | `/api/stock` | (see note) | Set/seed stock |

> Inventory access rules should be reviewed now that the service is only reachable through the gateway — seeding stock likely belongs behind an admin route.

---

## Key Design Decisions

- **Database per service.** Each service owns its schema; no cross-service joins. Services reference each other by ID, not by foreign key.
- **`BigDecimal` for money.** Avoids floating-point rounding errors on prices and totals.
- **Computed discount price.** The effective price is derived from the base price and an active discount window at read time, never persisted, so it can never go stale.
- **Price snapshotting on orders.** Order lines store the product name and price at purchase time, making orders immutable records independent of later catalog changes.
- **Public/private key JWTs (RS256).** Only the Auth service can sign; any service can verify with the public key. Stronger separation of powers than a shared secret.
- **Identity from token, not request body.** The user ID for an order comes from the verified token via the gateway, preventing a client from impersonating another user.
- **Programmatic gateway routes (Java Routes API).** Chosen over YAML routes to attach an identity-forwarding filter that reliably propagates headers through the proxy.
- **Event-driven inventory.** Order placement and stock decrement are decoupled through RabbitMQ, so inventory processing does not block or fail order placement.

---

## Known Limitations & Future Work

### Security hardening

- **Services trust gateway-forwarded headers.** Backend services currently trust `X-User-Id` / `X-User-Role` without independently validating the JWT. This relies on network isolation. A production-grade hardening step is **per-service JWT validation** (defense in depth), so a service stays secure even if reached directly on the internal network.
- **Refresh tokens.** Only short-lived access tokens are issued. Adding refresh tokens would let a frontend keep users logged in without frequent re-login, while keeping access tokens short-lived.
- **Admin provisioning.** New registrations are always `USER`; administrators are promoted manually in the database. A proper admin-provisioning flow is needed.
- **Private key handling.** Keys are baked into images for convenience. Production would load them from a secrets manager.

### Data & correctness

- **Transactional outbox.** The `OrderPlacedEvent` is published inside the order transaction after save. If publishing succeeds but the transaction rolls back, an event could exist for a non-existent order. The transactional outbox pattern (or publishing after commit) would close this gap.
- **Stock validation.** Orders are not currently rejected when they exceed available stock; stock simply floors at zero. Real inventory reservation and rejection logic is a natural extension.
- **Schema migrations.** Services currently use Hibernate `ddl-auto: update` for convenience. Reintroducing Flyway (with `ddl-auto: validate`) would give explicit, version-controlled schema migrations — the production-appropriate approach.

### Platform & operations

- **Resilience patterns.** Synchronous Order → Product calls have no retry or circuit breaker. Adding Resilience4j (retries, circuit breakers, timeouts) would handle transient downstream failures gracefully.
- **Service discovery.** Services are addressed by fixed container names. A discovery mechanism (e.g. Eureka) would be needed for dynamic scaling.
- **Observability.** Actuator health/metrics are exposed, but centralized logging, distributed tracing, and metrics aggregation (e.g. Prometheus/Grafana) are not yet in place.
- **Cart service.** A persistent shopping cart (mutable, pre-order) is a planned addition, likely as its own service.

### Frontend

- **SPA integration.** CORS is configured for Angular (`:4200`) and React (`:3000`) development origins. A frontend application has not yet been built.

### Alternative implementations

- **Kafka branch.** The event-driven flow is implemented with RabbitMQ. A parallel Kafka implementation is planned to contrast the queue model with the event-log model.
