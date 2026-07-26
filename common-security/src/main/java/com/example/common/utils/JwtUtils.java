package com.example.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

public final class JwtUtils {

    private JwtUtils() {
    }

    public static Optional<Jwt> getJwt() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt);
        }

        return Optional.empty();
    }

    public static String getSubject() {
        return getJwt()
                .map(Jwt::getSubject)
                .orElse(null);
    }

    public static String getUsername() {
        return getClaim("preferred_username");
    }

    public static String getUserId() {
        return getClaim("user_id");
    }

    public static String getEmail() {
        return getClaim("email");
    }

    public static String getClaim(String claimName) {

        return getJwt()
                .map(jwt -> jwt.getClaimAsString(claimName))
                .orElse(null);
    }

    public static List<String> getClaimAsList(String claimName) {

        return getJwt()
                .map(jwt -> jwt.getClaimAsStringList(claimName))
                .orElse(List.of());
    }

    public static boolean hasRole(String role) {
        return getClaimAsList("roles").contains(role);
    }

    public static boolean hasPermission(String permission) {
        return getClaimAsList("permissions").contains(permission);
    }
}