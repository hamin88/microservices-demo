package com.example.gateway.service;

import com.example.gateway.model.Permission;
import com.example.gateway.model.Role;
import com.example.gateway.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtTokenService {

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long expirationSeconds;

    public JwtTokenService(
            ObjectMapper objectMapper,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(User user) {
        Instant now = Instant.now();
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", user.getId().toString());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("roles", roles(user));
        claims.put("permissions", permissions(user));
        claims.put("iat", now.getEpochSecond());
        claims.put("exp", now.plusSeconds(expirationSeconds).getEpochSecond());

        String unsignedToken = encodeJson(header) + "." + encodeJson(claims);
        return unsignedToken + "." + sign(unsignedToken);
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    public List<String> roles(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .sorted()
                .toList();
    }

    public List<String> permissions(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .sorted()
                .toList();
    }

    private String encodeJson(Object value) {
        try {
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize JWT payload", ex);
        }
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign JWT", ex);
        }
    }
}
