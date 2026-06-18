package com.example.user.service;

import com.example.user.dto.LoginRequest;
import com.example.user.dto.LoginResponse;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository, PasswordService passwordService, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .filter(User::isEnabled)
                .filter(candidate -> passwordService.matches(
                        request.password(),
                        candidate.getPasswordSalt(),
                        candidate.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        return new LoginResponse(
                jwtTokenService.createToken(user),
                "Bearer",
                jwtTokenService.getExpirationSeconds(),
                user.getUsername(),
                jwtTokenService.roles(user),
                jwtTokenService.permissions(user));
    }
}
