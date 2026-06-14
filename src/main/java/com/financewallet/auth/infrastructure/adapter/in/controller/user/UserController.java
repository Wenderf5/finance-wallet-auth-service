package com.financewallet.auth.infrastructure.adapter.in.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financewallet.auth.application.usercase.StartUserRegistrationUseCase;
import com.financewallet.auth.infrastructure.adapter.in.controller.user.dto.StartUserRegistrationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final StartUserRegistrationUseCase startUserRegistrationUseCase;

    @Autowired
    public UserController(StartUserRegistrationUseCase startUserRegistrationUseCase){
        this.startUserRegistrationUseCase = startUserRegistrationUseCase;
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> startUserRegistration(
        @Valid
        @RequestBody
        StartUserRegistrationRequest body
    ){
        String token = this.startUserRegistrationUseCase.execute(
            body.getUserName(),
            body.getEmail(),
            body.getPassword()
        );

        ResponseCookie cookie = ResponseCookie
            .from("email_confirmation_token", token)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(300) 
            .sameSite("Lax")
            .build();

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build();
    }
}
