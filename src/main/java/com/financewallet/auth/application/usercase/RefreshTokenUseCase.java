package com.financewallet.auth.application.usercase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.financewallet.auth.application.dto.RefreshTokenUseCaseResponse;
import com.financewallet.auth.application.exception.UnauthorizedException;
import com.financewallet.auth.application.service.JwtService;
import com.financewallet.auth.domain.valueObject.TokenType;

public class RefreshTokenUseCase {
    private final JwtService jwtService;

    public RefreshTokenUseCase(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public RefreshTokenUseCaseResponse execute(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new UnauthorizedException(401, "Invalid refresh token");
        }

        try {
            this.jwtService.validate(refreshToken, TokenType.REFRESH_TOKEN);
        } catch (Exception e) {
            throw new UnauthorizedException(401, "Invalid or expired refresh token");
        }

        String newAccessToken = this.jwtService.generate(
            TokenType.ACCESS_TOKEN,
            Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        String newRefreshToken = this.jwtService.generate(
            TokenType.REFRESH_TOKEN,
            Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return new RefreshTokenUseCaseResponse(newAccessToken, newRefreshToken);
    }
}
