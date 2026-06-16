package com.example.gateway.service;

import com.example.gateway.dto.CurrentUserResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrentUserService {

    public CurrentUserResponse fromJwt(Jwt jwt) {
        return new CurrentUserResponse(
                jwt.getSubject(),
                username(jwt),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("name"),
                realmRoles(jwt),
                clientRoles(jwt)
        );
    }

    private String username(Jwt jwt) {
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        if (preferredUsername != null && !preferredUsername.isBlank()) {
            return preferredUsername;
        }
        return jwt.getSubject();
    }

    private List<String> realmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return List.of();
        }

        Object roles = realmAccess.get("roles");
        if (!(roles instanceof List<?> roleList)) {
            return List.of();
        }

        return roleList.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .sorted()
                .toList();
    }

    private Map<String, List<String>> clientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) {
            return Map.of();
        }

        Map<String, List<String>> rolesByClient = new LinkedHashMap<>();
        resourceAccess.forEach((clientId, value) -> {
            if (!(value instanceof Map<?, ?> clientAccess)) {
                return;
            }

            Object roles = clientAccess.get("roles");
            if (!(roles instanceof List<?> roleList)) {
                return;
            }

            List<String> clientRoleNames = roleList.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .sorted()
                    .toList();

            if (!clientRoleNames.isEmpty()) {
                rolesByClient.put(clientId, clientRoleNames);
            }
        });

        return Collections.unmodifiableMap(rolesByClient);
    }
}
