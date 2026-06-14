package com.financewallet.auth.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.financewallet.auth.application.service.TokenService;

public class TokenServiceImpTest {
    private TokenService tokenService = new TokenServiceImp("test-secret-key");

    @Test
    @DisplayName("should generate a jwt token")
    public void shouldGenerateJwtToken() {
        String token = tokenService.generate("TEST_TYPE", Instant.now().plus(5, ChronoUnit.MINUTES));
        assertNotNull(token);
    }

    @Test
    @DisplayName("should validate a valid jwt token")
    public void shouldValidateJwtToken() {
        String token = tokenService.generate("TEST_TYPE", Instant.now().plus(5, ChronoUnit.MINUTES));
        assertNotNull(tokenService.validate(token, "TEST_TYPE"));
    }

    @Test
    @DisplayName("should throw exception for invalid token")
    public void shouldThrowExceptionForInvalidToken() {
        assertThrows(JWTVerificationException.class, () -> tokenService.validate("invalid-token", "TEST_TYPE"));
    }

    @Test
    @DisplayName("should throw exception for invalid token type")
    public void shouldThrowExceptionForInvalidTokenType() {
        String token = tokenService.generate("TEST_TYPE", Instant.now().plus(5, ChronoUnit.MINUTES));
        assertThrows(JWTVerificationException.class, () -> tokenService.validate(token, "WRONG_TYPE"));
    }
}
