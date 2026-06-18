package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserHeaderGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    Object principal = authentication.getPrincipal();
                    if (!(principal instanceof Jwt jwt)) {
                        return chain.filter(exchange);
                    }

                    String username = getUsername(jwt);
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-Authenticated-User", username)
                            .header("X-Authenticated-Subject", jwt.getSubject())
                            .header("X-Authenticated-Roles", String.join(",", claimList(jwt, "roles")))
                            .header("X-Authenticated-Permissions", String.join(",", claimList(jwt, "permissions")))
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private String getUsername(Jwt jwt) {
        String username = jwt.getClaimAsString("username");
        if (username != null && !username.isBlank()) {
            return username;
        }
        return jwt.getSubject();
    }

    private java.util.List<String> claimList(Jwt jwt, String claimName) {
        java.util.List<String> values = jwt.getClaimAsStringList(claimName);
        return values == null ? java.util.List.of() : values;
    }
}
