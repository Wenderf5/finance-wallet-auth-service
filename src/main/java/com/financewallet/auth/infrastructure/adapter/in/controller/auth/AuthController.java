package com.financewallet.auth.infrastructure.adapter.in.controller.auth;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financewallet.auth.application.dto.CompleteUserRegistrationUseCaseResponse;
import com.financewallet.auth.application.dto.RefreshTokenUseCaseResponse;
import com.financewallet.auth.application.usercase.CompleteUserRegistrationUseCase;
import com.financewallet.auth.application.usercase.RefreshTokenUseCase;
import com.financewallet.auth.application.usercase.StartUserRegistrationUseCase;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.RefreshAccessTokenResponse;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.CompleteUserRegistrationRequest;
import com.financewallet.auth.application.usercase.ValidateSignUpSessionUseCase;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.CompleteUserRegistrationResponse;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.StartUserRegistrationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final StartUserRegistrationUseCase startUserRegistrationUseCase;
    private final ValidateSignUpSessionUseCase validateSignUpSessionUseCase;
    private final CompleteUserRegistrationUseCase completeUserRegistrationUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Autowired
    public AuthController(
        StartUserRegistrationUseCase startUserRegistrationUseCase,
        ValidateSignUpSessionUseCase validateSignUpSessionUseCase,
        CompleteUserRegistrationUseCase completeUserRegistrationUseCase,
        RefreshTokenUseCase refreshTokenUseCase
    ){
        this.startUserRegistrationUseCase = startUserRegistrationUseCase;
        this.validateSignUpSessionUseCase = validateSignUpSessionUseCase;
        this.completeUserRegistrationUseCase = completeUserRegistrationUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> startUserRegistration(
        @Valid
        @RequestBody
        StartUserRegistrationRequest body
    ){
        String signUpSessionToken = this.startUserRegistrationUseCase.execute(
            body.getUserName(),
            body.getEmail(),
            body.getPassword()
        );

        ResponseCookie signUpSessionCookie = ResponseCookie
            .from("signup_session", signUpSessionToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(300) 
            .sameSite("Lax")
            .build();

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .header(HttpHeaders.SET_COOKIE, signUpSessionCookie.toString())
            .build();
    }

    @PostMapping("/sign-up/confirm")
    public ResponseEntity<CompleteUserRegistrationResponse> completeUserRegistration(
        @Valid
        @RequestBody
        CompleteUserRegistrationRequest body,
        @CookieValue(name = "signup_session", required = false)
        String signupSessionToken
    ){
        CompleteUserRegistrationUseCaseResponse completeUserRegistrationUseCaseResponse = this.completeUserRegistrationUseCase.execute(
            body.getEmailCode(),
            signupSessionToken
        );

        ResponseCookie signUpSessionCookie = ResponseCookie
            .from("signup_session")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
            .from("refresh_token", completeUserRegistrationUseCaseResponse.getRefreshToken())
            .httpOnly(true)
            .secure(false)
            .path("/api/v1/auth/refresh")
            .maxAge(Duration.ofDays(7))
            .sameSite("Lax")
            .build();

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, signUpSessionCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(new CompleteUserRegistrationResponse(completeUserRegistrationUseCaseResponse.getAccessToken()));
    }

    @GetMapping("/sign-up/session")
    public ResponseEntity<Void> validateSignUpSession(@CookieValue(value = "signup_session", required = false) String signUpSessionToken){
        this.validateSignUpSessionUseCase.execute(signUpSessionToken);

        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshAccessTokenResponse> refreshToken(
        @CookieValue(name = "refresh_token", required = false)
        String refreshToken
    ){
        RefreshTokenUseCaseResponse refreshTokenUseCaseResponse = this.refreshTokenUseCase.execute(refreshToken);

        ResponseCookie newRefreshTokenCookie = ResponseCookie
            .from("refresh_token", refreshTokenUseCaseResponse.getRefreshToken())
            .httpOnly(true)
            .secure(false)
            .path("/api/v1/auth/refresh")
            .maxAge(Duration.ofDays(7))
            .sameSite("Lax")
            .build();

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString())
            .body(new RefreshAccessTokenResponse(refreshTokenUseCaseResponse.getAccessToken()));
    }
}
