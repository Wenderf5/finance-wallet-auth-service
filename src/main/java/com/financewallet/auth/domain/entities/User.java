package com.financewallet.auth.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financewallet.auth.domain.valueObjects.Email;
import com.financewallet.auth.domain.valueObjects.Password;

public class User {
    private UUID id;
    private String userName;
    private Email email;
    private Password password;
    private String photoUrl;
    private LocalDateTime createdAt;

    public User(UUID id, String userName, Email email, Password password, String photoUrl, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
    }

    public User(String userName, Email email, Password password, String photoUrl) {
        this.id = UUID.randomUUID();
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email.getEmail();
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getPassword() {
        return password.getPassword();
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
