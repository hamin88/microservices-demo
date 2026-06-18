package com.example.gateway.service;

import com.example.gateway.dto.CurrentUserResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentUserService {

    public CurrentUserResponse fromJwt(Jwt jwt) {
        return new CurrentUserResponse(
                jwt.getSubject(),
                username(jwt),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("name"),
                claimList(jwt, "roles"),
                claimList(jwt, "permissions")
        );
    }

    private String username(Jwt jwt) {
        String username = jwt.getClaimAsString("username");
        if (username != null && !username.isBlank()) {
            return username;
        }
        return jwt.getSubject();
    }

    private List<String> claimList(Jwt jwt, String claimName) {
        List<String> values = jwt.getClaimAsStringList(claimName);
        return values == null ? List.of() : values;
    }
}
