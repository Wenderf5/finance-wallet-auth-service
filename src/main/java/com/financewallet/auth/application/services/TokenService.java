package com.financewallet.auth.application.services;

public interface TokenService {
    String generate();
    boolean validate(String token);
}
