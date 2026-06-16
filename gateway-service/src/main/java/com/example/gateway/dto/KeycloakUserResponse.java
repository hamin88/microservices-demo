package com.example.gateway.dto;

import java.util.List;

public record KeycloakUserResponse(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        boolean enabled,
        boolean emailVerified,
        List<String> realmRoles
) {
}
