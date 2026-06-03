package com.financewallet.auth.infrastructure.persistence.repositories;

import com.financewallet.auth.domain.entities.User;
import com.financewallet.auth.domain.repositories.UserRepository;
import com.financewallet.auth.domain.valueObjects.Email;
import com.financewallet.auth.domain.valueObjects.Password;
import com.financewallet.auth.infrastructure.persistence.entities.UserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.getEmail())
                .map(entity -> new User(
                        entity.getId(),
                        entity.getUserName(),
                        new Email(entity.getEmail()),
                        new Password(entity.getPassword()),
                        entity.getPhotoUrl(),
                        entity.getCreatedAt()));
    }
}
