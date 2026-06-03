package com.financewallet.auth.application.dto;

public class UserCreationDataCache {
    private String userName;
    private String email;
    private String password;
    private String emailCode;

    public UserCreationDataCache() {
    }

    public UserCreationDataCache(String userName, String email, String password, String emailCode) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.emailCode = emailCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}
