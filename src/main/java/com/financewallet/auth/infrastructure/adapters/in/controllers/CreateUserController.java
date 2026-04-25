package com.financewallet.auth.infrastructure.adapters.in.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sign-up")
public class CreateUserController {
    @GetMapping
    public ResponseEntity<String> startUserCreation() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Hello world!");
    }
}
