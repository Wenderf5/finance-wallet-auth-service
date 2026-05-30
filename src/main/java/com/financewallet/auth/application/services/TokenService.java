package com.financewallet.auth.application.services;

import java.time.Instant;

public interface TokenService {
    String generate(String type, Instant exp);
    String validate(String token, String type);
}
