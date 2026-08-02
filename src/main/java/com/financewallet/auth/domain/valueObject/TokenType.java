package com.financewallet.auth.domain.valueObject;

public enum TokenType {
    ACCESS_TOKEN("access"),
    SIGNUP_SESSION_TOKEN("signup_session"),
    REFRESH_TOKEN("refresh");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
