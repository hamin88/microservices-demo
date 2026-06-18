package com.example.gateway.dto;

import java.util.List;

public record CurrentUserResponse(
        String subject,
        String username,
        String email,
        String name,
        List<String> roles,
        List<String> permissions
) {
}
