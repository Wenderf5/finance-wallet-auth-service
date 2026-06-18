package com.financewallet.auth.infrastructure.adapter.in.controller.auth;

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

import com.financewallet.auth.application.usercase.StartUserRegistrationUseCase;
import com.financewallet.auth.application.usercase.ValidateSignUpSessionUseCase;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.StartUserRegistrationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final StartUserRegistrationUseCase startUserRegistrationUseCase;
    private final ValidateSignUpSessionUseCase validateSignUpSessionUseCase;

    @Autowired
    public AuthController(
        StartUserRegistrationUseCase startUserRegistrationUseCase,
        ValidateSignUpSessionUseCase validateSignUpSessionUseCase
    ){
        this.startUserRegistrationUseCase = startUserRegistrationUseCase;
        this.validateSignUpSessionUseCase = validateSignUpSessionUseCase;
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
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, signUpSessionCookie.toString())
            .build();
    }

    @GetMapping("/sign-up/session")
    public ResponseEntity<Void> validateSignUpSession(@CookieValue(value = "signup_session", required = false) String signUpSessionToken){
        this.validateSignUpSessionUseCase.execute(signUpSessionToken);
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}
