package com.example.gateway.service;

import com.example.common.utils.JwtUser;
import com.example.common.utils.PasswordService;
import com.example.common.utils.JwtTokenService;
import com.example.gateway.dto.LoginRequest;
import com.example.gateway.dto.LoginResponse;
import com.example.gateway.model.Permission;
import com.example.gateway.model.Role;
import com.example.gateway.model.User;
import com.example.gateway.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository,
                       PasswordService passwordService,
                       JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid username or password"));

        if (!user.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User account is disabled");
        }

        if (!passwordService.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        List<String> permissions = user.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();

        JwtUser jwtUser = new JwtUser(
                String.valueOf(user.getId()),
                user.getUsername(),
                roles,
                permissions
        );

        String accessToken = jwtTokenService.generateToken(jwtUser);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtTokenService.getExpirationSeconds(),
                jwtUser.username(),
                jwtUser.roles(),
                jwtUser.permissions()
        );
    }
}