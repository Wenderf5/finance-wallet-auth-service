package com.financewallet.auth.infrastructure.adapters.in.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financewallet.auth.application.useCases.StartUserCreationUseCase;
import com.financewallet.auth.infrastructure.adapters.in.controllers.user.dto.StartUserCreationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final StartUserCreationUseCase startUserCreationUseCase;

    @Autowired
    public UserController(StartUserCreationUseCase startUserCreationUseCase){
        this.startUserCreationUseCase = startUserCreationUseCase;
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> startUserCreation(
        @Valid
        @RequestBody
        StartUserCreationRequest body
    ){
        String token = this.startUserCreationUseCase.execute(
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
