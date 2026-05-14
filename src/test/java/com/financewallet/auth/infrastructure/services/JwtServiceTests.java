package com.financewallet.auth.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JwtServiceTests {
    private JwtService jwtService = new JwtService("test-secret-key");

    @Test
    @DisplayName("should generate a jwt token")
    public void shouldGenerateJwtToken() {
        String token = jwtService.generate();
        assertNotNull(token);
    }

    @Test
    @DisplayName("should validate a valid jwt token")
    public void shouldValidateJwtToken() {
        String token = jwtService.generate();
        assertTrue(jwtService.validate(token));
    }

    @Test
    @DisplayName("should return false for invalid token")
    public void shouldReturnFalseForInvalidToken() {
        assertFalse(jwtService.validate("invalid-token"));
    }
}
