package com.financewallet.auth.infrastructure.services;

import com.financewallet.auth.application.gateways.TokenService;

public class JwtService implements TokenService {
    public String generate() {
        return "token";
    }

    public boolean validate(String token) {
        return false;
    }
}
