package com.financewallet.auth.application.usecase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.financewallet.auth.application.exception.UnauthorizedException;
import com.financewallet.auth.application.service.TokenService;
import com.financewallet.auth.application.usercase.ValidateSignUpSessionUseCase;

@ExtendWith(MockitoExtension.class)
public class ValidateSignUpSessionUseCaseTest {
    private final String SIGN_UP_SESSION_TOKEN_TYPE = "sign-up-session";

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ValidateSignUpSessionUseCase validateSignUpSessionUseCase;

    @Test
    @DisplayName("Should validate the token successfully")
    public void shouldValidateTokenSuccessfully(){
        when(tokenService.validate("valid-token", SIGN_UP_SESSION_TOKEN_TYPE)).thenReturn("Decoded-token");

        assertDoesNotThrow(() -> {
            this.validateSignUpSessionUseCase.execute("valid-token");
        });
    }


    @Test
    @DisplayName("Should throw UnauthorizedException when token is invalid")
    public void shouldThrowUnauthorizedExceptionWhenTokenIsInvalid(){
        when(tokenService.validate("invalid-token", SIGN_UP_SESSION_TOKEN_TYPE)).thenThrow(JWTVerificationException.class);

        assertThrows(UnauthorizedException.class, () -> {
            this.validateSignUpSessionUseCase.execute("invalid-token");
        });
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when token is null")
    public void shouldThrowUnauthorizedExceptionWhenTokenIsNull(){
        assertThrows(UnauthorizedException.class, () -> {
            this.validateSignUpSessionUseCase.execute(null);
        });

        verify(this.tokenService, never()).validate(null, SIGN_UP_SESSION_TOKEN_TYPE);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when token is empty")
    public void shouldThrowUnauthorizedExceptionWhenTokenIsEmpty(){
        assertThrows(UnauthorizedException.class, () -> {
            this.validateSignUpSessionUseCase.execute("");
        });

        verify(this.tokenService, never()).validate("", SIGN_UP_SESSION_TOKEN_TYPE);
    }
}
