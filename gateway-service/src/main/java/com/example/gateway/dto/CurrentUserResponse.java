package com.example.gateway.dto;

import java.util.List;
import java.util.Map;

public record CurrentUserResponse(
        String subject,
        String username,
        String email,
        String name,
        List<String> realmRoles,
        Map<String, List<String>> clientRoles
) {
}
