package com.financewallet.auth.infrastructure.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import com.financewallet.auth.application.gateways.TokenService;

@Service
public class JwtService implements TokenService {
    private static final String ISSUER = "finance-wallet-auth";
    
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

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
