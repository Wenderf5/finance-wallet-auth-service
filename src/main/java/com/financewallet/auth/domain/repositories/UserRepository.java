package com.financewallet.auth.domain.repositories;

import com.financewallet.auth.domain.entities.User;

public interface UserRepository {
    void save(User user);
}
