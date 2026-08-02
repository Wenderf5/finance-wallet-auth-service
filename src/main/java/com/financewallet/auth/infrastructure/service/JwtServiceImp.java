package com.financewallet.auth.infrastructure.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.financewallet.auth.application.service.JwtService;
import com.financewallet.auth.domain.valueObject.TokenType;

@Service
public class JwtServiceImp implements JwtService {
    private final Algorithm algorithm;

    public JwtServiceImp(
        @Value("${jwt.private-key}") String privateKeyPem,
        @Value("${jwt.public-key}") String publicKeyPem
    ) throws Exception {
        this.algorithm = Algorithm.RSA256 (
            loadPublicKey(publicKeyPem),
            loadPrivateKey(privateKeyPem)
        );
    }

    @Override
    public String generate(TokenType type, Instant exp) {
        return JWT.create()
            .withIssuedAt(Instant.now())
            .withExpiresAt(exp)
            .withClaim("type", type.getValue())
            .sign(algorithm);
    }

    @Override
    public String validate(String token, TokenType type) {
        DecodedJWT decodedJWT = JWT.require(algorithm)
            .withClaim("type", type.getValue())
            .build()
            .verify(token);

        return new String(
            Base64.getUrlDecoder().decode(decodedJWT.getPayload()),
            StandardCharsets.UTF_8
        );
    }

    private RSAPrivateKey loadPrivateKey(String privateKeyPem) throws Exception {
        String cleanedKey = privateKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(cleanedKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

    private RSAPublicKey loadPublicKey(String publicKeyPem) throws Exception {
        String cleanedKey = publicKeyPem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(cleanedKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }
}
