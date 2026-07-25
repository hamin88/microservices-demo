package com.example.gateway.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

public class UserHeaderGatewayFilter {

    /**
     * Static factory utility compatible with Spring Cloud Gateway 4.1.0 MVC.
     * Uses standard synchronous thread contexts (No WebFlux/Mono/Flux).
     */
    public static HandlerFilterFunction<ServerResponse, ServerResponse> extractUserHeader() {
        return (request, next) -> {
            // 1. Extract thread-bound security data (Fully Servlet/Tomcat safe)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String userId = "anonymous";

            // 2. Safely parse claims if a JWT is active
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                userId = jwt.getClaimAsString("user_id");
                if (userId == null) {
                    userId = jwt.getSubject();
                }
            }

            // 3. Mutate the request context to downstream destinations
            ServerRequest modifiedRequest = ServerRequest.from(request)
                    .header("X-Authenticated-User-Id", userId)
                    .build();

            return next.handle(modifiedRequest);
        };
    }
}
