package com.example.gateway.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.gateway.dto.KeycloakUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class KeycloakAdminService {

    private static final ParameterizedTypeReference<List<KeycloakUserRepresentation>> USER_LIST_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient webClient;
    private final String realm;
    private final String adminRealm;
    private final String adminClientId;
    private final String adminUsername;
    private final String adminPassword;

    public KeycloakAdminService(
            WebClient.Builder webClientBuilder,
            @Value("${keycloak.base-url:http://localhost:8180}") String baseUrl,
            @Value("${keycloak.realm:microservices-demo}") String realm,
            @Value("${keycloak.admin.realm:master}") String adminRealm,
            @Value("${keycloak.admin.client-id:admin-cli}") String adminClientId,
            @Value("${keycloak.admin.username:admin}") String adminUsername,
            @Value("${keycloak.admin.password:admin}") String adminPassword) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.realm = realm;
        this.adminRealm = adminRealm;
        this.adminClientId = adminClientId;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public Flux<KeycloakUserResponse> getUsers() {
        return adminToken()
                .flatMapMany(token -> webClient.get()
                        .uri("/admin/realms/{realm}/users", realm)
                        .headers(headers -> headers.setBearerAuth(token.accessToken()))
                        .retrieve()
                        .bodyToMono(USER_LIST_TYPE)
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(user -> withRealmRoles(token.accessToken(), user)));
    }

    private Mono<KeycloakAdminToken> adminToken() {
        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", adminRealm)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", adminClientId)
                        .with("username", adminUsername)
                        .with("password", adminPassword))
                .retrieve()
                .bodyToMono(KeycloakAdminToken.class);
    }

    private Mono<KeycloakUserResponse> withRealmRoles(String token, KeycloakUserRepresentation user) {
        return webClient.get()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, user.id())
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToFlux(KeycloakRoleRepresentation.class)
                .map(KeycloakRoleRepresentation::name)
                .sort()
                .collectList()
                .defaultIfEmpty(List.of())
                .map(roles -> new KeycloakUserResponse(
                        user.id(),
                        user.username(),
                        user.firstName(),
                        user.lastName(),
                        user.email(),
                        user.enabled(),
                        user.emailVerified(),
                        roles
                ));
    }

    private record KeycloakAdminToken(@JsonProperty("access_token") String accessToken) {
    }

    private record KeycloakUserRepresentation(
            String id,
            String username,
            String firstName,
            String lastName,
            String email,
            boolean enabled,
            boolean emailVerified
    ) {
    }

    private record KeycloakRoleRepresentation(String name) {
    }
}
