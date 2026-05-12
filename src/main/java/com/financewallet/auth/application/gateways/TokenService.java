package com.financewallet.auth.application.gateways;

public interface TokenService {
    String genare();
    boolean validade(String token);
}
