package com.financewallet.auth.application.usercase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.financewallet.auth.application.dto.CompleteUserRegistrationUseCaseResponse;
import com.financewallet.auth.application.dto.UserRegistrationDataCache;
import com.financewallet.auth.application.exception.EmailCodeException;
import com.financewallet.auth.application.exception.EmailCodeExpiredException;
import com.financewallet.auth.application.gateway.CacheGateway;
import com.financewallet.auth.application.service.JsonService;
import com.financewallet.auth.application.service.JwtService;
import com.financewallet.auth.domain.entity.User;
import com.financewallet.auth.domain.repository.UserRepository;
import com.financewallet.auth.domain.valueObject.TokenType;

public class CompleteUserRegistrationUseCase {
    private final CacheGateway cacheGateway;
    private final JsonService jsonService;
    private final UserRepository userRepository;
    private final JwtService tokenService;

    public CompleteUserRegistrationUseCase(
        CacheGateway cacheGateway,
        JsonService jsonService,
        UserRepository userRepository,
        JwtService tokenService
    ){
        this.cacheGateway = cacheGateway;
        this.jsonService = jsonService;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public CompleteUserRegistrationUseCaseResponse execute(String code, String key) {
        //Retrieves user data from cache
        String userRegistrationDataCache;
        try {
            userRegistrationDataCache = this.cacheGateway.get(key);
        } catch (Exception e) {
           throw new EmailCodeExpiredException(400, "Verification code has expired");
        }

        UserRegistrationDataCache userData = this.jsonService.fromJson(userRegistrationDataCache, UserRegistrationDataCache.class);

        //it verifies the email code
        if (!userData.getEmailCode().equals(code)) {
            throw new EmailCodeException(400, "Invalid e-mail code");
        }

        //Persists a new user in data base
        User user = new User(
            userData.getUserName(),
            userData.getEmail(),
            userData.getPassword(),
            "test-photo"
        );
        this.userRepository.save(user);

        //Generates access token with 15 minutes expiration
        String accessToken = this.tokenService.generate(
            TokenType.ACCESS_TOKEN,
            Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        //Generates refresh token with 7 days expiration
        String refreshToken = this.tokenService.generate(
            TokenType.REFRESH_TOKEN,
            Instant.now().plus(7, ChronoUnit.DAYS)
        );

        //Clears temporary user data from cache
        this.cacheGateway.delete(key);

        return new CompleteUserRegistrationUseCaseResponse(accessToken, refreshToken);
    }
}
