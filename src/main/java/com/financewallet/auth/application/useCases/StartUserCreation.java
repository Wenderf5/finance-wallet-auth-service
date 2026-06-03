package com.financewallet.auth.application.useCases;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.financewallet.auth.application.dto.UserCreationDataCache;
import com.financewallet.auth.application.exceptions.EmailAlreadyInUseException;
import com.financewallet.auth.application.gateways.CacheGateway;
import com.financewallet.auth.application.gateways.EmailGateway;
import com.financewallet.auth.application.services.CodeGeneratorService;
import com.financewallet.auth.application.services.JsonService;
import com.financewallet.auth.application.services.TokenService;
import com.financewallet.auth.domain.entities.User;
import com.financewallet.auth.domain.repositories.UserRepository;
import com.financewallet.auth.domain.valueObjects.Email;
import com.financewallet.auth.domain.valueObjects.Password;

public class StartUserCreation {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final CodeGeneratorService codeGeneratorService;
    private final CacheGateway cacheGateway;
    private final JsonService jsonService;
    private final EmailGateway emailGateway;

    public StartUserCreation(
            UserRepository userRepository,
            TokenService tokenService,
            CodeGeneratorService codeGeneratorService,
            CacheGateway cacheGateway,
            JsonService jsonService,
            EmailGateway emailGateway
    ) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.codeGeneratorService = codeGeneratorService;
        this.cacheGateway = cacheGateway;
        this.jsonService = jsonService;
        this.emailGateway = emailGateway;
    }

    public String execute(String userName, Email email, Password password) {
        // Check if the email address is already in use
        Optional<User> isUserExist = this.userRepository.findByEmail(email);
        if (isUserExist.isPresent()) {
            throw new EmailAlreadyInUseException("A user with this email address already exists");
        }

        // Temporarily saves the user data that will be created in the cache while the
        // email code is being confirmed
        String emailCode = this.codeGeneratorService.generate();
        String token = this.tokenService.generate(
                "EMAIL_CONFIRMATION",
                Instant.now().plus(5, ChronoUnit.MINUTES)
        );
        UserCreationDataCache userCreationDataCache = new UserCreationDataCache(
                userName,
                email.getEmail(),
                password.getPassword(),
                emailCode
        );
        this.cacheGateway.save(token, this.jsonService.toJson(userCreationDataCache), 300L);

        // Generate and send the code to the provided email address
        com.financewallet.auth.application.dto.Email emailConfirmation = new com.financewallet.auth.application.dto.Email(
                email.getEmail(),
                "Finance Wallet confirmation code",
                "O código de confirmação é: " + emailCode
        );
        this.emailGateway.send(emailConfirmation);

        return token;
    }
}
