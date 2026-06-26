package com.financewallet.auth.infrastructure.adapter.in.controller.auth;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financewallet.auth.application.usercase.CompleteUserRegistrationUseCase;
import com.financewallet.auth.application.usercase.StartUserRegistrationUseCase;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.CompleteUserRegistrationRequest;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.StartUserRegistrationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final StartUserRegistrationUseCase startUserRegistrationUseCase;
    private final CompleteUserRegistrationUseCase completeUserRegistrationUseCase;

    @Autowired
    public AuthController(
        StartUserRegistrationUseCase startUserRegistrationUseCase,
        CompleteUserRegistrationUseCase completeUserRegistrationUseCase
    ){
        this.startUserRegistrationUseCase = startUserRegistrationUseCase;
        this.completeUserRegistrationUseCase = completeUserRegistrationUseCase;
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
    public ResponseEntity<Void> completeUserRegistration(
        @Valid
        @RequestBody
        CompleteUserRegistrationRequest body,
        @CookieValue(name = "signup_session", required = false)
        String signupSessionToken
    ){
        String accessToken = this.completeUserRegistrationUseCase.execute(
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

        ResponseCookie accessCookie = ResponseCookie
            .from("access_cookie", accessToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(Duration.ofDays(7)) 
            .sameSite("Lax")
            .build();

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, signUpSessionCookie.toString())
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .build();
    }
}
