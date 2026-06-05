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
import com.financewallet.auth.application.dto.Email;

public class StartUserCreationUseCase {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final CodeGeneratorService codeGeneratorService;
    private final CacheGateway cacheGateway;
    private final JsonService jsonService;
    private final EmailGateway emailGateway;

    public StartUserCreationUseCase(
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

    public String execute(String userName, String email, String password) {
        // Check if the email address is already in use
        Optional<User> isUserExist = this.userRepository.findByEmail(email);
        if (isUserExist.isPresent()) {
            throw new EmailAlreadyInUseException(409, "A user with this email address already exists");
        }

        try {
            // Temporarily saves the user data that will be created in the cache while the
            // email code is being confirmed
            String emailConfirmationCode = this.codeGeneratorService.generate();
            String emailConfirmationToken = this.tokenService.generate(
                "EMAIL_CONFIRMATION",
                Instant.now().plus(5, ChronoUnit.MINUTES)
            );
            UserCreationDataCache userCreationDataCache = new UserCreationDataCache(
                userName,
                email,
                password,
                emailConfirmationCode
            );
            this.cacheGateway.save(
                emailConfirmationToken,
                this.jsonService.toJson(userCreationDataCache),
                300L
            );

            // Generate and send the code to the provided email address
            Email emailConfirmation = new Email(
                email,
                "Finance Wallet confirmation code",
                "The confirmation code is: " + emailConfirmationCode
            );
            this.emailGateway.send(emailConfirmation);

            return emailConfirmationToken;
        } catch (Exception e) {
            throw new RuntimeException("Error registering user");
        }
    }
}
