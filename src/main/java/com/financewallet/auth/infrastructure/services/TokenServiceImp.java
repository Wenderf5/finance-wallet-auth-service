package com.financewallet.auth.infrastructure.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.financewallet.auth.application.services.TokenService;

@Service
public class TokenServiceImp implements TokenService {
    private static final String ISSUER = "finance-wallet-auth";
    private final String secretKey;

    public TokenServiceImp(@Value("${JWT_SECRET_KEY}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String generate() {
        Instant now = Instant.now();

        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(5, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(secretKey));
    }

    @Override
    public boolean validate(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secretKey))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }
}
