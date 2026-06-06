package com.financewallet.auth.domain.repositories;

import java.util.Optional;

import com.financewallet.auth.domain.entities.User;

public interface UserRepository {
    void save(User user);
    Optional<User> findByEmail(String email);
}
