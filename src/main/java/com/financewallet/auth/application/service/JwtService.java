package com.financewallet.auth.application.service;

import java.time.Instant;

import com.financewallet.auth.domain.valueObject.TokenType;

public interface JwtService {
    String generate(TokenType type, Instant exp);
    String validate(String token, TokenType type);
}
