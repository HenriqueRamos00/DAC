package com.ufpr.bantads.auth.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    public String encode(String rawPassword) {
        byte[] salt = generateSalt();
        byte[] hash = hashWithSalt(rawPassword, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public boolean matches(String rawPassword, String storedPassword) {
        // suporta senhas em texto puro (seed do banco) e senhas com hash
        if (!storedPassword.contains(":")) {
            return rawPassword.equals(storedPassword);
        }
        String[] parts = storedPassword.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);
        byte[] computedHash = hashWithSalt(rawPassword, salt);
        return MessageDigest.isEqual(storedHash, computedHash);
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private byte[] hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo de hash não encontrado", e);
        }
    }
}
