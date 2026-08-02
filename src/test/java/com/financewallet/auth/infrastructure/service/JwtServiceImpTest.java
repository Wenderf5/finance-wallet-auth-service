package com.financewallet.auth.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.financewallet.auth.application.service.JwtService;
import com.financewallet.auth.domain.valueObject.TokenType;

public class JwtServiceImpTest {
    private JwtService jwtService;

    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
        "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCEEvpjez8n61QN\n" +
        "3i6Vqi3uUD98AwACzt1I+wPwVRZrAuHhzA2hfjxWPAqDskwpV5eT44PtMfnAE62j\n" +
        "9kEYLcXIRKpoIR1ubOIBQBapH62mpdYReyojwi/KNOZlD1p2msfemZbWD5Pt7eYF\n" +
        "Ule+JrPf3JkfPbbEw9OGOPLS9xQUiB96+zEbXpIQ67zJ1VdJtGtVauhbATLM9rxA\n" +
        "+xGB/ZiELOPM0WyAfYU/9FRCFEZ+g6/1djeJFMk2pH8sZZSg5Pal05U1rHZMyYe2\n" +
        "nqNeQ79YHNmKTbztoNSI/++LgPgL70lV51xxU5rJx6kp08QSoJ/pfMNg68oSSogx\n" +
        "FH/HKYV3AgMBAAECggEABksaW014JGCImWLbONehZ4WzZUuqusCrAG2MexIcBsnw\n" +
        "XzvDqDpXra+yKbWAI5FSpyaIzaZf/D6GFxYGmcDrd8Jkug4VVPCQ/9Gui+P3HTN6\n" +
        "693A3N2hGDNFG88dxGoxas4BEEfoCKJijUZde2hktmfQmKPueQeCpV/UwAMHKDWF\n" +
        "Ebj1ed1rKEsXNI5xpx9QU/ArNLTUkqPVfYar8RT5EGN9d5729Zd/DxxGPYCrxSDc\n" +
        "IFvqL0wxp8YtMIvdgrLW1XZRnvXHc1VXmKgKbq6UArQKBp5DJTZ4ZOqvV7crDAxN\n" +
        "4xZRNGc9YdjVbnSC8Wu0LIgWru9uhZZlDHwH+RMLQQKBgQC6QJPK89rFN9CPW+FR\n" +
        "8AWC1Qf0D/rrQvXA2L5KtuG4gJ7/ty/dNxg1yrC3kXURwwA6p8uTKr7X6zghGlIS\n" +
        "KpIUxbJcn1hE9v+vmovtsa3ZhRhFL+0yOHUtxU5+6qHnEgyVhpYtyPjsVPFf2fRP\n" +
        "XEfk2m/ocIfTCojceb2kHbGF6QKBgQC1iINiLOYWfH/ipje3UjPlMKo6BKmcmoGa\n" +
        "kFJ8PRkTbh/wAj/R5kLHudnSgg4S84iaEcATQ9CMNXer44VbsyUVkOqhOXy/vfNL\n" +
        "dkbT2jjVRvOb9UPemcZuakAmpy1BhgA+GFRX/B7zOjHUuthElgqy+36z25se833o\n" +
        "AIOaXXm0XwKBgQCy7yuNxa0d37t8nvbfc0q4INXdQiT8NH5JYXGf8gb07IzUezd+\n" +
        "vVmDBFG2agYgpEjqER5hKqXiZlasZs8GKOPjFPOATI3O48VM/ZsFJrgwy88/WQX2\n" +
        "Q4tAB9ib0ertRcfVN1G8duG+mvSx84DlZXCkir8Mfl/e/rXxqvvoWaadUQKBgHXJ\n" +
        "NM5PqZ8vet750K7u9GMiV2E7wz2sdqluOUcJcarMyoY1P2mXCP+ElFPlNPKQ97JY\n" +
        "EBOv7bH7w0FRaCcertI2bDbeERZsTl9JSB2Pu06PMeOgmT/m8PyVjjhGQrQAGUox\n" +
        "CRDpsr9IpCqObWLbI+gMDKLwu86yWKBfLQTM3/nrAoGBAJZA4PUteM90llJd3vao\n" +
        "4lLqkCJ6P09ibFrQEu++QNtjCcPQXrZFxYKdt+rNl4p3OXe9H8+r9MPSVbLV7ZRs\n" +
        "T3i5cnCCAR8/gNYN9SUTvLCAZqf2zP7AuRvgzNmdRVESQ/nodHNCSx3US89OK8zw\n" +
        "uuEQ7K5GhnMQJPvYYAxo4T0o\n" +
        "-----END PRIVATE KEY-----";

    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhBL6Y3s/J+tUDd4ulaot\n" +
        "7lA/fAMAAs7dSPsD8FUWawLh4cwNoX48VjwKg7JMKVeXk+OD7TH5wBOto/ZBGC3F\n" +
        "yESqaCEdbmziAUAWqR+tpqXWEXsqI8IvyjTmZQ9adprH3pmW1g+T7e3mBVJXviaz\n" +
        "39yZHz22xMPThjjy0vcUFIgfevsxG16SEOu8ydVXSbRrVWroWwEyzPa8QPsRgf2Y\n" +
        "hCzjzNFsgH2FP/RUQhRGfoOv9XY3iRTJNqR/LGWUoOT2pdOVNax2TMmHtp6jXkO/\n" +
        "WBzZik287aDUiP/vi4D4C+9JVedccVOaycepKdPEEqCf6XzDYOvKEkqIMRR/xymF\n" +
        "dwIDAQAB\n" +
        "-----END PUBLIC KEY-----";

    @BeforeEach
    public void setUp() throws Exception {
        jwtService = new JwtServiceImp(PRIVATE_KEY, PUBLIC_KEY);
    }

    @Test
    @DisplayName("should generate a jwt token")
    public void shouldGenerateJwtToken() {
        String token = jwtService.generate(TokenType.ACCESS_TOKEN, Instant.now().plus(5, ChronoUnit.MINUTES));
        assertNotNull(token);
    }

    @Test
    @DisplayName("should validate a valid jwt token")
    public void shouldValidateJwtToken() {
        String token = jwtService.generate(TokenType.ACCESS_TOKEN, Instant.now().plus(5, ChronoUnit.MINUTES));
        assertNotNull(jwtService.validate(token, TokenType.ACCESS_TOKEN));
    }

    @Test
    @DisplayName("should throw exception for invalid token")
    public void shouldThrowExceptionForInvalidToken() {
        assertThrows(JWTVerificationException.class, () -> jwtService.validate("invalid-token", TokenType.ACCESS_TOKEN));
    }

    @Test
    @DisplayName("should throw exception for invalid token type")
    public void shouldThrowExceptionForInvalidTokenType() {
        String token = jwtService.generate(TokenType.ACCESS_TOKEN, Instant.now().plus(5, ChronoUnit.MINUTES));
        assertThrows(JWTVerificationException.class, () -> jwtService.validate(token, TokenType.SIGNUP_SESSION_TOKEN));
    }
}
