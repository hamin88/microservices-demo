package com.example.gateway.config;

import com.example.gateway.filter.UserHeaderGatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> customGatewayRoutes() {
        return route("user_management_service")
                .route(path("/api/users/**"), http()) // 4.1.x targets are resolved natively via downstream configs
                .filter(UserHeaderGatewayFilter.extractUserHeader())
                .build();
    }
}
