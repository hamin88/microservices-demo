package com.example.common.utils;
import java.util.List;
public record JwtUser(
        String userId,
        String username,
        List<String> roles,
        List<String> permissions
) {}