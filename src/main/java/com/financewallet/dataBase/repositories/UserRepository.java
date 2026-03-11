package com.financewallet.dataBase.repositories;

import java.util.Optional;

import com.financewallet.dataBase.entities.UserEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext(unitName = "financeWalletPU")
    private EntityManager entityManager;

    public void save(UserEntity user) {
        this.entityManager.persist(user);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email",
                UserEntity.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }
}
