package com.example.gateway.controller;

import com.example.gateway.dto.CurrentUserResponse;
import com.example.gateway.dto.KeycloakUserResponse;
import com.example.gateway.service.CurrentUserService;
import com.example.gateway.service.KeycloakAdminService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/keycloak")
public class KeycloakController {

    private final CurrentUserService currentUserService;
    private final KeycloakAdminService keycloakAdminService;

    public KeycloakController(CurrentUserService currentUserService, KeycloakAdminService keycloakAdminService) {
        this.currentUserService = currentUserService;
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping("/me")
    public Mono<CurrentUserResponse> getCurrentUser(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        return Mono.just(currentUserService.fromJwt(jwt));
    }

    @GetMapping("/users")
    public Flux<KeycloakUserResponse> getUsers() {
        return keycloakAdminService.getUsers();
    }
}
