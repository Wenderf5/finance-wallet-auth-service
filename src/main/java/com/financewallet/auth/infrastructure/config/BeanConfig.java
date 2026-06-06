package com.financewallet.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financewallet.auth.application.gateways.CacheGateway;
import com.financewallet.auth.application.gateways.EmailGateway;
import com.financewallet.auth.application.services.CodeGeneratorService;
import com.financewallet.auth.application.services.JsonService;
import com.financewallet.auth.application.services.TokenService;
import com.financewallet.auth.application.useCases.StartUserCreationUseCase;
import com.financewallet.auth.domain.repositories.UserRepository;
import com.google.gson.Gson;

@Configuration
public class BeanConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public StartUserCreationUseCase startUserCreationUseCase(
        UserRepository userRepository,
        TokenService tokenService,
        CodeGeneratorService codeGeneratorService,
        CacheGateway cacheGateway,
        JsonService jsonService,
        EmailGateway emailGateway
    ) {
        return new StartUserCreationUseCase(
            userRepository,
            tokenService,
            codeGeneratorService,
            cacheGateway,
            jsonService,
            emailGateway
        );
    }
}
