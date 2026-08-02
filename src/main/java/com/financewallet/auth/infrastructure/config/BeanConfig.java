package com.financewallet.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.financewallet.auth.application.gateway.CacheGateway;
import com.financewallet.auth.application.gateway.EmailGateway;
import com.financewallet.auth.application.service.CodeGeneratorService;
import com.financewallet.auth.application.service.JsonService;
import com.financewallet.auth.application.service.JwtService;
import com.financewallet.auth.application.usercase.CompleteUserRegistrationUseCase;
import com.financewallet.auth.application.usercase.RefreshTokenUseCase;
import com.financewallet.auth.application.usercase.StartUserRegistrationUseCase;
import com.financewallet.auth.application.usercase.ValidateSignUpSessionUseCase;
import com.financewallet.auth.domain.repository.UserRepository;
import com.google.gson.Gson;

@Configuration
public class BeanConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public StartUserRegistrationUseCase startUserRegistrationUseCase(
        UserRepository userRepository,
        JwtService tokenService,
        CodeGeneratorService codeGeneratorService,
        CacheGateway cacheGateway,
        JsonService jsonService,
        EmailGateway emailGateway,
        BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        return new StartUserRegistrationUseCase(
            userRepository,
            tokenService,
            codeGeneratorService,
            cacheGateway,
            jsonService,
            emailGateway,
            bCryptPasswordEncoder
        );
    }
  
    @Bean
    public ValidateSignUpSessionUseCase validateSignUpSessionUseCase(JwtService tokenService){
        return new ValidateSignUpSessionUseCase(tokenService);
    }

    @Bean
    public CompleteUserRegistrationUseCase completeUserRegistrationUseCase(
        CacheGateway cacheGateway,
        JsonService jsonService, 
        UserRepository userRepository,
        JwtService tokenService
    ) {
        return new CompleteUserRegistrationUseCase(
            cacheGateway,
            jsonService,
            userRepository,
            tokenService
        );
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(JwtService jwtService) {
        return new RefreshTokenUseCase(jwtService);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
