package com.example.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean matches(String rawPassword, String expectedHash) {
        //return hash(rawPassword, salt).equalsIgnoreCase(expectedHash);
        return passwordEncoder.matches(rawPassword, expectedHash);
    }

    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
