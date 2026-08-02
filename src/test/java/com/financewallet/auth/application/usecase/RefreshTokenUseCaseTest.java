package com.financewallet.auth.application.usecase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.financewallet.auth.application.dto.RefreshTokenUseCaseResponse;
import com.financewallet.auth.application.exception.UnauthorizedException;
import com.financewallet.auth.application.service.JwtService;
import com.financewallet.auth.application.usercase.RefreshTokenUseCase;
import com.financewallet.auth.domain.valueObject.TokenType;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenUseCaseTest {
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenUseCase refreshTokenUseCase;

    @Test
    @DisplayName("Should return new tokens when refresh token is valid")
    public void shouldReturnNewTokensWhenRefreshTokenIsValid(){
        String validRefreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        when(this.jwtService.validate(validRefreshToken, TokenType.REFRESH_TOKEN)).thenReturn("Decoded-token");
        when(this.jwtService.generate(eq(TokenType.ACCESS_TOKEN), any(Instant.class))).thenReturn(newAccessToken);
        when(this.jwtService.generate(eq(TokenType.REFRESH_TOKEN), any(Instant.class))).thenReturn(newRefreshToken);

        RefreshTokenUseCaseResponse result = this.refreshTokenUseCase.execute(validRefreshToken);

        assertEquals(newAccessToken, result.getAccessToken());
        assertEquals(newRefreshToken, result.getRefreshToken());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token is invalid")
    public void shouldThrowUnauthorizedExceptionWhenRefreshTokenIsInvalid(){
        String invalidRefreshToken = "invalid-refresh-token";

        when(this.jwtService.validate(invalidRefreshToken, TokenType.REFRESH_TOKEN)).thenThrow(JWTVerificationException.class);

        assertThrows(UnauthorizedException.class, () -> {
            this.refreshTokenUseCase.execute(invalidRefreshToken);
        });
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token is null")
    public void shouldThrowUnauthorizedExceptionWhenRefreshTokenIsNull(){
        assertThrows(UnauthorizedException.class, () -> {
            this.refreshTokenUseCase.execute(null);
        });
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token is empty")
    public void shouldThrowUnauthorizedExceptionWhenRefreshTokenIsEmpty(){
        assertThrows(UnauthorizedException.class, () -> {
            this.refreshTokenUseCase.execute("");
        });
    }
}
