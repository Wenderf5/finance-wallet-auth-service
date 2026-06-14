package com.financewallet.auth.application.service;

import java.time.Instant;

public interface TokenService {
    String generate(String type, Instant exp);
    String validate(String token, String type);
}
