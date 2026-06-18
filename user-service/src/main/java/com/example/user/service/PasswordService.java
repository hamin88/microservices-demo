package com.example.user.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Service
public class PasswordService {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    public boolean matches(String rawPassword, String salt, String expectedHash) {
        return hash(rawPassword, salt).equalsIgnoreCase(expectedHash);
    }

    public String hash(String rawPassword, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(
                    rawPassword.toCharArray(),
                    salt.getBytes(StandardCharsets.UTF_8),
                    ITERATIONS,
                    KEY_LENGTH);
            byte[] bytes = factory.generateSecret(spec).getEncoded();
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new IllegalStateException("PBKDF2 password hashing is not available", ex);
        }
    }
}
