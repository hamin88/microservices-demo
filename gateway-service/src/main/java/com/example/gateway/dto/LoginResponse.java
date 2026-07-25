package com.example.gateway.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username,
        List<String> roles,
        List<String> permissions
) {
}
