package com.financewallet.auth.domain.repository;

import java.util.Optional;

import com.financewallet.auth.domain.entity.User;

public interface UserRepository {
    void save(User user);
    Optional<User> findByEmail(String email);
}
