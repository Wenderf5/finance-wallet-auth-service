package com.financewallet.auth.infrastructure.persistence.repository;

import com.financewallet.auth.domain.entity.User;
import com.financewallet.auth.domain.repository.UserRepository;
import com.financewallet.auth.infrastructure.persistence.entity.UserEntity;

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
            user.getCreatedAt()
        );
        userJpaRepository.save(userEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
            .map(entity -> new User(
                entity.getId(),
                entity.getUserName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getPhotoUrl(),
                entity.getCreatedAt())
            );
    }
}
