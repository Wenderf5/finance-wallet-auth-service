package com.financewallet.auth.infrastructure.services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.financewallet.auth.application.services.TokenService;

@Service
public class TokenServiceImp implements TokenService {
    private final Algorithm algorithm;

    public TokenServiceImp(@Value("${JWT_SECRET_KEY}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    @Override
    public String generate(String type, Instant exp) {
        return JWT.create()
            .withIssuedAt(Instant.now())
            .withExpiresAt(exp)
            .withClaim("type", type)
            .sign(algorithm);
    }

    @Override
    public String validate(String token, String type) {
        DecodedJWT decodedJWT = JWT.require(algorithm)
            .withClaim("type", type)
            .build()
            .verify(token);

        return new String(
            Base64.getUrlDecoder().decode(decodedJWT.getPayload()),
            StandardCharsets.UTF_8
        );
    }
}
