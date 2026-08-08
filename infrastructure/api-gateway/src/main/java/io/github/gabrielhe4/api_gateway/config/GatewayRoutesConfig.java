package io.github.gabrielhe4.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;


import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    @Value("${PRODUCT_URI:http://localhost:8081}")
    private String productUri;
    @Value("${ORDER_URI:http://localhost:8082}")
    private String orderUri;
    @Value("${INVENTORY_URI:http://localhost:8083}")
    private String inventoryUri;
    @Value("${AUTH_URI:http://localhost:8084}")
    private String authUri;

    // ADDS identity headers from the validated JWT; forwards correctly through the proxy
    private ServerRequest addIdentity(ServerRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return ServerRequest.from(request)
                .header("X-User-Id", jwt.getSubject())
                .header("X-User-Role", jwt.getClaimAsString("role"))
                .build();
        }
        return request;
    }

    @Bean
    RouterFunction<ServerResponse> productRoute() {
        return route("product-service")
            .route(path("/api/products/**")
                .or(path("/api/admin/products/**"))
                .or(path("/api/categories/**"))
                .or(path("/api/admin/categories/**")), http())
            .before(uri(productUri))
            .before(this::addIdentity)
            .build();
    }

    @Bean
    RouterFunction<ServerResponse> orderRoute() {
        return route("order-service")
            .route(path("/api/orders/**"), http())
            .before(uri(orderUri))
            .before(this::addIdentity)
            .build();
    }

    @Bean
    RouterFunction<ServerResponse> inventoryRoute() {
        return route("inventory-service")
            .route(path("/api/stock/**").or(path("/api/admin/stock/**")), http())
            .before(uri(inventoryUri))
            .before(this::addIdentity)
            .build();
    }

    @Bean
    RouterFunction<ServerResponse> authRoute() {
        return route("auth-service")
            .route(path("/auth/**"), http())
            .before(uri(authUri))
            .build();   // no identity header on auth routes — no token yet at login
    }

}
