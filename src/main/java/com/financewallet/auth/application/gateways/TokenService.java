package com.financewallet.auth.application.gateways;

public interface TokenService {
    String generate();
    boolean validate(String token);
}
