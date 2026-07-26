package com.example.common.utils;

import com.example.common.utils.JwtUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds) {

        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
    }


    public String generateToken(JwtUser user) {

        Instant now = Instant.now();

        Map<String, Object> claims = new HashMap<>();

        claims.put("user_id", user.userId());
        claims.put("preferred_username", user.username());
        claims.put("roles", user.roles());
        claims.put("permissions", user.permissions());


        JwtClaimsSet jwtClaims = JwtClaimsSet.builder()
                .issuer("gateway-service")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .subject(user.userId())
                .claims(c -> c.putAll(claims))
                .build();


        return jwtEncoder.encode(
                JwtEncoderParameters.from(jwtClaims)
        ).getTokenValue();
    }


    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}