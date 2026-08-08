GET  /api/products          → public (anyone browses)
GET  /api/products/{id}     → public
GET  /api/categories        → public

POST   /api/admin/products      → admin only
PUT    /api/admin/products/{id} → admin only
DELETE /api/admin/products/{id} → admin only
POST   /api/admin/categories    → admin only

## Running Docker compose 

Go to directory Docker and execute:

`docker compose up --build`

Note: to wipe any old volume so init-databases.sql runs:

`docker compose down -v `

## For podman users:

`podman compose up --build`

for previous versions of podman use:

`podman-compose up --build`