package com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto;

public class CompleteUserRegistrationResponse {
    private String accessToken;

    public CompleteUserRegistrationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
