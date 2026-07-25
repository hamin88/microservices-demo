package com.example.gateway.controller;

import com.example.gateway.dto.CurrentUserResponse;
import com.example.gateway.dto.LoginRequest;
import com.example.gateway.dto.LoginResponse;
import com.example.gateway.service.AuthService;
import com.example.gateway.service.CurrentUserService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CurrentUserService currentUserService;
    private final AuthService authService;

    public AuthController(CurrentUserService currentUserService, AuthService authService) {
        this.currentUserService = currentUserService;
        this.authService = authService ;
    }

    @GetMapping("/me")
    public Mono<CurrentUserResponse> getCurrentUser(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        return Mono.just(currentUserService.fromJwt(jwt));
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

}
