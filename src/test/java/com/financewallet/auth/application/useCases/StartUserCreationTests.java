package com.financewallet.auth.application.useCases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class StartUserCreationTests {

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
    private StartUserCreation startUserCreation;

    private Email validEmail;
    private Password validPassword;
    private String userName;
    private String generatedCode;
    private String generatedToken;
    private String serializedJson;

    @BeforeEach
    void setUp() {
        validEmail = new Email("user@email.com");
        validPassword = new Password("Password1");
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
        when(tokenService.generate(eq("EMAIL_CONFIRMATION"), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserCreationDataCache.class))).thenReturn(serializedJson);

        String result = startUserCreation.execute(userName, validEmail, validPassword);

        assertEquals(generatedToken, result);
    }

    @Test
    @DisplayName("should throw EmailAlreadyInUseException when email is already registered")
    void shouldThrowEmailAlreadyInUseExceptionWhenEmailExists() {
        User existingUser = new User("Existing User", validEmail, validPassword, null);
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(existingUser));

        EmailAlreadyInUseException exception = assertThrows(
                EmailAlreadyInUseException.class,
                () -> startUserCreation.execute(userName, validEmail, validPassword)
        );

        assertEquals("A user with this email address already exists", exception.getMessage());
    }

    @Test
    @DisplayName("should not interact with other services when email already exists")
    void shouldNotInteractWithOtherServicesWhenEmailExists() {
        User existingUser = new User("Existing User", validEmail, validPassword, null);
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyInUseException.class,
                () -> startUserCreation.execute(userName, validEmail, validPassword));

        verifyNoInteractions(codeGeneratorService);
        verifyNoInteractions(tokenService);
        verifyNoInteractions(cacheGateway);
        verifyNoInteractions(jsonService);
        verifyNoInteractions(emailGateway);
    }

    @Test
    @DisplayName("should generate email confirmation code")
    void shouldGenerateEmailConfirmationCode() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq("EMAIL_CONFIRMATION"), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserCreationDataCache.class))).thenReturn(serializedJson);

        startUserCreation.execute(userName, validEmail, validPassword);

        verify(codeGeneratorService).generate();
    }

    @Test
    @DisplayName("should generate token with EMAIL_CONFIRMATION type")
    void shouldGenerateTokenWithEmailConfirmationType() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq("EMAIL_CONFIRMATION"), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserCreationDataCache.class))).thenReturn(serializedJson);

        startUserCreation.execute(userName, validEmail, validPassword);

        verify(tokenService).generate(eq("EMAIL_CONFIRMATION"), any(Instant.class));
    }

    @Test
    @DisplayName("should save user data in cache with correct token key and TTL of 300 seconds")
    void shouldSaveUserDataInCacheWithCorrectTokenAndTtl() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq("EMAIL_CONFIRMATION"), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserCreationDataCache.class))).thenReturn(serializedJson);

        startUserCreation.execute(userName, validEmail, validPassword);

        verify(cacheGateway).save(generatedToken, serializedJson, 300L);
    }

    @Test
    @DisplayName("should serialize UserCreationDataCache with correct data")
    void shouldSerializeUserCreationDataCacheWithCorrectData() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq("EMAIL_CONFIRMATION"), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserCreationDataCache.class))).thenReturn(serializedJson);

        startUserCreation.execute(userName, validEmail, validPassword);

        ArgumentCaptor<UserCreationDataCache> captor = ArgumentCaptor.forClass(UserCreationDataCache.class);
        verify(jsonService).toJson(captor.capture());

        UserCreationDataCache captured = captor.getValue();
        assertEquals(userName, captured.getUserName());
        assertEquals(validEmail.getEmail(), captured.getEmail());
        assertEquals(validPassword.getPassword(), captured.getPassword());
        assertEquals(generatedCode, captured.getEmailCode());
    }

    @Test
    @DisplayName("should send confirmation email to the provided address")
    void shouldSendConfirmationEmailToProvidedAddress() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq("EMAIL_CONFIRMATION"), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserCreationDataCache.class))).thenReturn(serializedJson);

        startUserCreation.execute(userName, validEmail, validPassword);

        ArgumentCaptor<com.financewallet.auth.application.dto.Email> emailCaptor =
                ArgumentCaptor.forClass(com.financewallet.auth.application.dto.Email.class);
        verify(emailGateway).send(emailCaptor.capture());

        com.financewallet.auth.application.dto.Email sentEmail = emailCaptor.getValue();
        assertEquals(validEmail.getEmail(), sentEmail.getTo());
        assertEquals("Finance Wallet confirmation code", sentEmail.getSubject());
        assertEquals("O código de confirmação é: " + generatedCode, sentEmail.getBody());
    }

    @Test
    @DisplayName("should call findByEmail on repository before any other operation")
    void shouldCallFindByEmailFirst() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(codeGeneratorService.generate()).thenReturn(generatedCode);
        when(tokenService.generate(eq("EMAIL_CONFIRMATION"), any(Instant.class))).thenReturn(generatedToken);
        when(jsonService.toJson(any(UserCreationDataCache.class))).thenReturn(serializedJson);

        startUserCreation.execute(userName, validEmail, validPassword);

        var inOrder = inOrder(userRepository, codeGeneratorService, tokenService, cacheGateway, emailGateway);
        inOrder.verify(userRepository).findByEmail(validEmail);
        inOrder.verify(codeGeneratorService).generate();
        inOrder.verify(tokenService).generate(eq("EMAIL_CONFIRMATION"), any(Instant.class));
        inOrder.verify(cacheGateway).save(eq(generatedToken), eq(serializedJson), eq(300L));
        inOrder.verify(emailGateway).send(any(com.financewallet.auth.application.dto.Email.class));
    }
}
