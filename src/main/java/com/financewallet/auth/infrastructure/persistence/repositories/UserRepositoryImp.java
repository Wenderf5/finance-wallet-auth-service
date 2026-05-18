package com.financewallet.auth.infrastructure.persistence.repositories;

import com.financewallet.auth.domain.entities.User;
import com.financewallet.auth.domain.repositories.UserRepository;
import com.financewallet.auth.infrastructure.persistence.entities.UserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryImp implements UserRepository {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Override
    public void save(User user) {
        UserEntity userEntity = new UserEntity(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhotoUrl(),
                user.getCreatedAt());
        userJpaRepository.save(userEntity);
    }
}
