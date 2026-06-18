package com.financewallet.auth.application.usercase;

import com.financewallet.auth.application.exception.UnauthorizedException;
import com.financewallet.auth.application.service.TokenService;

public class ValidateSignUpSession {
    private final TokenService tokenService;
    private final String SIGN_UP_SESSION_TOKEN_TYPE = "sign-up-session";

    public ValidateSignUpSession(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void execute(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new UnauthorizedException(401, "Invalid session token");
        }

        try {
            this.tokenService.validate(token, SIGN_UP_SESSION_TOKEN_TYPE);
        } catch (Exception e) {
            throw new UnauthorizedException(401, "Invalid session token");
        }
    }
}
