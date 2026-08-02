package com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto;

public class RefreshAccessTokenResponse {
    private String accessToken;

    public RefreshAccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
