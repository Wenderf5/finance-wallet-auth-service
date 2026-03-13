package com.financewallet.services;

import com.financewallet.DTOs.CreateUserDTO;
import com.financewallet.dataBase.entities.UserEntity;
import com.financewallet.dataBase.repositories.UserRepository;
import com.financewallet.exceptions.EmailAlreadyExistException;
import com.financewallet.exceptions.UnauthorizedException;
import com.financewallet.redis.RedisTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import com.financewallet.DTOs.EmailDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {
    @Inject
    private UserRepository userRepository;

    @Inject
    private RedisTemplate redisTemplate;

    @Inject
    private EmailService emailService;

    public Optional<UserEntity> isUserExist(String email) {
        return this.userRepository.findByEmail(email);
    }

    public String generateEmailCodeConfirmation() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(secureRandom.nextInt(10));
        }

        return code.toString();
    }

    public String generateSaveAndSendEmailCode(CreateUserDTO user) {
        Optional<UserEntity> isUserExist = isUserExist(user.getEmail());
        if (isUserExist.isPresent()) {
            throw new EmailAlreadyExistException("This email address is already in use.");
        }

        try {
            String createUserSessionToken = UUID.randomUUID().toString();
            String emailCode = generateEmailCodeConfirmation();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", user.getEmail());
            jsonObject.addProperty("password", user.getPassword());
            jsonObject.addProperty("userName", user.getUserName());
            jsonObject.addProperty("emailCode", emailCode);

            String jsonValue = new Gson().toJson(jsonObject);
            redisTemplate.set(createUserSessionToken, jsonValue, 300L);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            EmailDTO emailDTO = new EmailDTO(
                    user.getEmail(),
                    "Código de Confirmação - Finance Wallet",
                    "Olá " + user.getUserName() + ",\n\nSeu código de confirmação de E-mail é: " + emailCode
                            + "\n\nAtenciosamente Finance Wallet!");

            this.emailService
                    .getSession(props, System.getenv("EMAIL_SERVICE_USERNAME"), System.getenv("EMAIL_SERVICE_PASSWORD"))
                    .setEmail(emailDTO)
                    .send();

            return createUserSessionToken;
        } catch (Exception e) {
            throw new RuntimeException("Error registering user.");
        }
    }

    @Transactional
    public void createUser(CreateUserDTO user) {
        try {
            UserEntity newUser = new UserEntity(
                    user.getEmail(),
                    user.getPassword(),
                    user.getUserName(),
                    "test-photo-link");

            this.userRepository.save(newUser);
        } catch (Exception e) {
            if (isConstraintViolation(e)) {
                throw new EmailAlreadyExistException("This email address is already in use.");
            }
            throw new RuntimeException("Error registering user.");
        }
    }

    private boolean isConstraintViolation(Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            String className = cause.getClass().getSimpleName();
            String message = cause.getMessage() == null ? "" : cause.getMessage().toLowerCase();
            if (className.contains("ConstraintViolationException") ||
                    className.contains("SQLIntegrityConstraintViolationException") ||
                    message.contains("duplicate") ||
                    message.contains("violates unique constraint")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    public Long verifyCreateUserSessionToken(String key) {
        try {
            Long ttl = this.redisTemplate.getTtl(key);
            return ttl;
        } catch (Exception e) {
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
