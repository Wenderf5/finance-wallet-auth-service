package com.financewallet.auth.infrastructure.adapters.in.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.financewallet.auth.infrastructure.adapters.in.controllers.dto.StartUserCreationRequest;
import com.financewallet.auth.infrastructure.adapters.in.controllers.dto.StartUserCreationResponse;

@RestController
@RequestMapping("/users")
public class StartUserCreation {
    @PostMapping
    public ResponseEntity<StartUserCreationResponse> startUserCreation(@Valid @RequestBody StartUserCreationRequest body) {

        System.out.println(body.getUserName());
        System.out.println(body.getEmail());
        System.out.println(body.getPassword());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
