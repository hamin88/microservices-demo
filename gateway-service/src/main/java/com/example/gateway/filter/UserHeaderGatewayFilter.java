package com.example.gateway.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

public final class UserHeaderGatewayFilter {

    private static final String USER_HEADER = "X-Authenticated-User-Id";

    private UserHeaderGatewayFilter() {
    }

    public static HandlerFilterFunction<ServerResponse, ServerResponse> extractUserHeader() {

        return (request, next) -> {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String userId = "anonymous";

            if (authentication != null
                    && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof Jwt jwt) {

                userId = jwt.getClaimAsString("user_id");

                if (userId == null || userId.isBlank()) {
                    userId = jwt.getSubject();
                }
            }

            ServerRequest modifiedRequest = ServerRequest.from(request)
                    .headers(headers -> headers.remove(USER_HEADER))
                    .header(USER_HEADER, userId)
                    .build();

            return next.handle(modifiedRequest);
        };
    }
}