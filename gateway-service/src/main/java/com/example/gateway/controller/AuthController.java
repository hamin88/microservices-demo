package com.example.gateway.controller;

import com.example.gateway.dto.CurrentUserResponse;
import com.example.gateway.service.CurrentUserService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CurrentUserService currentUserService;

    public AuthController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public Mono<CurrentUserResponse> getCurrentUser(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        return Mono.just(currentUserService.fromJwt(jwt));
    }
}
