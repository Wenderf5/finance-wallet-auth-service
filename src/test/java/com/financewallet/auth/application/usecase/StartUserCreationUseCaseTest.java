package com.financewallet.auth.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financewallet.auth.application.dto.UserRegistrationDataCache;
import com.financewallet.auth.application.exception.EmailAlreadyInUseException;
import com.financewallet.auth.application.dto.Email;
import com.financewallet.auth.application.gateway.CacheGateway;
import com.financewallet.auth.application.gateway.EmailGateway;
import com.financewallet.auth.application.service.CodeGeneratorService;
import com.financewallet.auth.application.service.JsonService;
import com.financewallet.auth.application.service.TokenService;
import com.financewallet.auth.application.usercase.StartUserRegistrationUseCase;
import com.financewallet.auth.domain.entity.User;
import com.financewallet.auth.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class StartUserRegistrationUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private CodeGeneratorService codeGeneratorService;

    @Mock
    private CacheGateway cacheGateway;

    @Mock
    private JsonService jsonService;

    @Mock
    private EmailGateway emailGateway;

    @InjectMocks
    private StartUserRegistrationUseCase startUserRegistrationUseCase;

    private String validEmail;
    private String validPassword;
    private String userName;
    private String generatedCode;
    private String generatedToken;
    private String serializedJson;
    private final String SIGN_UP_SESSION_TOKEN_TYPE = "sign-up-session";

    @BeforeEach
    void setUp() {
        validEmail = "user@email.com";
        validPassword = "Password1";
        userName = "John Doe";
        generatedCode = "123456";
        generatedToken = "jwt-token-example";
        serializedJson = "{\"userName\":\"John Doe\",\"email\":\"user@email.com\",\"password\":\"Password1\",\"emailCode\":\"123456\"}";
    }

    @Test
    @DisplayName("should create user successfully and return token")
    void shouldCreateUserSuccessfullyAndReturnToken() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq(SIGN_UP_SESSION_TOKEN_TYPE), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserRegistrationDataCache.class))).thenReturn(serializedJson);

        String result = startUserRegistrationUseCase.execute(userName, validEmail, validPassword);

        assertEquals(generatedToken, result);
    }

    @Test
    @DisplayName("should throw EmailAlreadyInUseException when email is already registered")
    void shouldThrowEmailAlreadyInUseExceptionWhenEmailExists() {
        User existingUser = new User("Existing User", validEmail, validPassword, null);
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(existingUser));

        EmailAlreadyInUseException exception = assertThrows(
                EmailAlreadyInUseException.class,
                () -> startUserRegistrationUseCase.execute(userName, validEmail, validPassword)
        );

        assertEquals("A user with this email address already exists", exception.getMessage());
        
        verifyNoInteractions(codeGeneratorService);
        verifyNoInteractions(tokenService);
        verifyNoInteractions(cacheGateway);
        verifyNoInteractions(jsonService);
        verifyNoInteractions(emailGateway);
    }

    @Test
    @DisplayName("should throw error if saving in cache fails")
    void shouldThrowErrorIfSavingInCacheFails() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq(SIGN_UP_SESSION_TOKEN_TYPE), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserRegistrationDataCache.class))).thenReturn(serializedJson);
        
        doThrow(new RuntimeException("Cache error")).when(cacheGateway).save(anyString(), anyString(), anyLong());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> startUserRegistrationUseCase.execute(userName, validEmail, validPassword)
        );

        assertEquals("Error registering user", exception.getMessage());
        
        verifyNoInteractions(emailGateway);
    }

    @Test
    @DisplayName("should throw error if email sending fails")
    void shouldThrowErrorIfEmailSendingFails() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq(SIGN_UP_SESSION_TOKEN_TYPE), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserRegistrationDataCache.class))).thenReturn(serializedJson);
        
        doThrow(new RuntimeException("Email error")).when(emailGateway).send(any(Email.class));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> startUserRegistrationUseCase.execute(userName, validEmail, validPassword)
        );

        assertEquals("Error registering user", exception.getMessage());
    }
}
