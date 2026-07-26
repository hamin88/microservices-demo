package com.example.common.utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Hash a plain text password.
     */
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public String hash(String rawPassword) {
        return hash(rawPassword);
    }
    /**
     * Verify a plain text password against a hashed password.
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Check whether an existing hash should be upgraded
     * (for example, if BCrypt strength has increased).
     */
    public boolean upgradeEncoding(String encodedPassword) {
        return passwordEncoder.upgradeEncoding(encodedPassword);
    }
}