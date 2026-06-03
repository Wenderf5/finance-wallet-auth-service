package com.financewallet.auth.domain.repositories;

import java.util.Optional;

import com.financewallet.auth.domain.entities.User;
import com.financewallet.auth.domain.valueObjects.Email;

public interface UserRepository {
    void save(User user);
    Optional<User> findByEmail(Email email);
}
