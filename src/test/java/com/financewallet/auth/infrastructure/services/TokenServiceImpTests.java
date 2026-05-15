package com.financewallet.auth.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TokenServiceImpTests {
    private TokenServiceImp tokenServiceImp = new TokenServiceImp("test-secret-key");

    @Test
    @DisplayName("should generate a jwt token")
    public void shouldGenerateJwtToken() {
        String token = tokenServiceImp.generate();
        assertNotNull(token);
    }

    @Test
    @DisplayName("should validate a valid jwt token")
    public void shouldValidateJwtToken() {
        String token = tokenServiceImp.generate();
        assertTrue(tokenServiceImp.validate(token));
    }

    @Test
    @DisplayName("should return false for invalid token")
    public void shouldReturnFalseForInvalidToken() {
        assertFalse(tokenServiceImp.validate("invalid-token"));
    }
}
