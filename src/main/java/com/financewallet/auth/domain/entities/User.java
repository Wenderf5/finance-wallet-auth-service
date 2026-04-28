package com.financewallet.auth.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private UUID id;
    private String userName;
    private String email;
    private String password;
    private String photoUrl;
    private LocalDateTime createdAt;

    public User(UUID id, String userName, String email, String password, String photoUrl, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
    }

    public User(String userName, String email, String password, String photoUrl) {
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
