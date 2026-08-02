package com.financewallet.auth.application.usercase;

import com.financewallet.auth.application.exception.UnauthorizedException;
import com.financewallet.auth.application.service.JwtService;
import com.financewallet.auth.domain.valueObject.TokenType;

public class ValidateSignUpSessionUseCase {
    private final JwtService tokenService;

    public ValidateSignUpSessionUseCase(JwtService tokenService) {
        this.tokenService = tokenService;
    }

    public void execute(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new UnauthorizedException(401, "Invalid session token");
        }

        try {
            this.tokenService.validate(token, TokenType.SIGNUP_SESSION_TOKEN);
        } catch (Exception e) {
            throw new UnauthorizedException(401, "Invalid session token");
        }
    }
}
