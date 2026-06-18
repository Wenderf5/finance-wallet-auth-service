package com.financewallet.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financewallet.auth.application.gateway.CacheGateway;
import com.financewallet.auth.application.gateway.EmailGateway;
import com.financewallet.auth.application.service.CodeGeneratorService;
import com.financewallet.auth.application.service.JsonService;
import com.financewallet.auth.application.service.TokenService;
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
        TokenService tokenService,
        CodeGeneratorService codeGeneratorService,
        CacheGateway cacheGateway,
        JsonService jsonService,
        EmailGateway emailGateway
    ) {
        return new StartUserRegistrationUseCase(
            userRepository,
            tokenService,
            codeGeneratorService,
            cacheGateway,
            jsonService,
            emailGateway
        );
    }

    @Bean
    public ValidateSignUpSessionUseCase validateSignUpSessionUseCase(TokenService tokenService){
        return new ValidateSignUpSessionUseCase(tokenService);
    }
}
