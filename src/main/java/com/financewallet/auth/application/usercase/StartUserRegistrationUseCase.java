package com.financewallet.auth.application.usercase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.financewallet.auth.application.dto.UserRegistrationDataCache;
import com.financewallet.auth.application.exception.EmailAlreadyInUseException;
import com.financewallet.auth.application.gateway.CacheGateway;
import com.financewallet.auth.application.gateway.EmailGateway;
import com.financewallet.auth.application.service.CodeGeneratorService;
import com.financewallet.auth.application.service.JsonService;
import com.financewallet.auth.application.service.TokenService;
import com.financewallet.auth.domain.entity.User;
import com.financewallet.auth.domain.repository.UserRepository;
import com.financewallet.auth.application.dto.Email;

public class StartUserRegistrationUseCase {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final CodeGeneratorService codeGeneratorService;
    private final CacheGateway cacheGateway;
    private final JsonService jsonService;
    private final EmailGateway emailGateway;
    private final String SIGN_UP_SESSION_TOKEN_TYPE = "sign-up-session";
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public StartUserRegistrationUseCase(
        UserRepository userRepository,
        TokenService tokenService,
        CodeGeneratorService codeGeneratorService,
        CacheGateway cacheGateway,
        JsonService jsonService,
        EmailGateway emailGateway,
        BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.codeGeneratorService = codeGeneratorService;
        this.cacheGateway = cacheGateway;
        this.jsonService = jsonService;
        this.emailGateway = emailGateway;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
            String signupSessionToken = this.tokenService.generate(
                SIGN_UP_SESSION_TOKEN_TYPE,
                Instant.now().plus(5, ChronoUnit.MINUTES)
            );
            UserRegistrationDataCache userRegistrationDataCache = new UserRegistrationDataCache(
                userName,
                email,
                this.bCryptPasswordEncoder.encode(password),
                emailConfirmationCode
            );
            this.cacheGateway.save(
                signupSessionToken,
                this.jsonService.toJson(userRegistrationDataCache),
                300L
            );

            // Generate and send the code to the provided email address
            Email emailConfirmation = new Email(
                email,
                "Finance Wallet confirmation code",
                "The confirmation code is: " + emailConfirmationCode
            );
            this.emailGateway.send(emailConfirmation);

            return signupSessionToken;
        } catch (Exception e) {
            throw new RuntimeException("Error registering user");
        }
    }
}
