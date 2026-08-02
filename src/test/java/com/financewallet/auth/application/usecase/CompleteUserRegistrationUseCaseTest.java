package com.financewallet.auth.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financewallet.auth.application.dto.CompleteUserRegistrationUseCaseResponse;
import com.financewallet.auth.application.dto.UserRegistrationDataCache;
import com.financewallet.auth.application.exception.EmailCodeException;
import com.financewallet.auth.application.exception.EmailCodeExpiredException;
import com.financewallet.auth.infrastructure.exception.CacheOperationException;
import com.financewallet.auth.application.gateway.CacheGateway;
import com.financewallet.auth.application.service.JsonService;
import com.financewallet.auth.application.service.JwtService;
import com.financewallet.auth.application.usercase.CompleteUserRegistrationUseCase;
import com.financewallet.auth.domain.entity.User;
import com.financewallet.auth.domain.repository.UserRepository;
import com.financewallet.auth.domain.valueObject.TokenType;

@ExtendWith(MockitoExtension.class)
public class CompleteUserRegistrationUseCaseTest {
    @Mock
    private CacheGateway cacheGateway;

    @Mock
    private JsonService jsonService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService tokenService;

    @InjectMocks
    private CompleteUserRegistrationUseCase completeUserRegistrationUseCase;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should return access token and refresh token when everything is ok")
    public void shouldReturnAccessTokenAndRefreshToken() throws JsonProcessingException{
        String testAccessToken = "testAccessToken";
        String testRefreshToken = "testRefreshToken";
        String testEmailCode = "123456";
        String testKey = "testKey";

        UserRegistrationDataCache userRegistrationDataCache = new UserRegistrationDataCache(
            "testName",
            "test@gmail.com",
            "testPassword",
            testEmailCode
        );
        String userRegistrationDataCacheString = this.objectMapper.writeValueAsString(userRegistrationDataCache);

        when(this.cacheGateway.get(testKey)).thenReturn(userRegistrationDataCacheString);
        when(this.jsonService.fromJson(userRegistrationDataCacheString, UserRegistrationDataCache.class)).thenReturn(userRegistrationDataCache);
        when(this.tokenService.generate(eq(TokenType.ACCESS_TOKEN), any(Instant.class))).thenReturn(testAccessToken);
        when(this.tokenService.generate(eq(TokenType.REFRESH_TOKEN), any(Instant.class))).thenReturn(testRefreshToken);

        CompleteUserRegistrationUseCaseResponse result = this.completeUserRegistrationUseCase.execute(userRegistrationDataCache.getEmailCode(), testKey);
        assertEquals(testAccessToken, result.getAccessToken());
        assertEquals(testRefreshToken, result.getRefreshToken());
    }

    @Test
    @DisplayName("Should throw EmailCodeException when email code is invalid")
    public void shouldThrowEmailCodeExceptionWhenEmailCodeIsInvalid() throws JsonProcessingException {
        String testEmailCode = "123456";
        String testKey = "testKey";

        UserRegistrationDataCache userRegistrationDataCache = new UserRegistrationDataCache(
            "testName",
            "test@gmail.com",
            "testPassword",
            testEmailCode
        );
        String userRegistrationDataCacheString = this.objectMapper.writeValueAsString(userRegistrationDataCache);

        when(this.cacheGateway.get(testKey)).thenReturn(userRegistrationDataCacheString);
        when(this.jsonService.fromJson(userRegistrationDataCacheString, UserRegistrationDataCache.class)).thenReturn(userRegistrationDataCache);

        EmailCodeException exception = assertThrows(EmailCodeException.class, () -> {
            this.completeUserRegistrationUseCase.execute("wrong_code", testKey);
        });

        assertEquals(400, exception.getStatus());
        assertEquals("Invalid e-mail code", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw EmailCodeExpiredException when cacheGateway cannot find the record in cache")
    public void shouldThrowEmailCodeExpiredExceptionWhenCacheIsMissing() {
        String testKey = "testKey";

        doThrow(new CacheOperationException("The key testKey does not exist"))
            .when(this.cacheGateway).get(testKey);

        EmailCodeExpiredException exception = assertThrows(EmailCodeExpiredException.class, () -> {
            this.completeUserRegistrationUseCase.execute("123456", testKey);
        });

        assertEquals("Verification code has expired", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when userRepository fails to save")
    public void shouldThrowExceptionWhenUserRepositoryFailsToSave() throws JsonProcessingException {
        String testEmailCode = "123456";
        String testKey = "testKey";

        UserRegistrationDataCache userRegistrationDataCache = new UserRegistrationDataCache(
            "testName",
            "test@gmail.com",
            "testPassword",
            testEmailCode
        );
        String userRegistrationDataCacheString = this.objectMapper.writeValueAsString(userRegistrationDataCache);

        when(this.cacheGateway.get(testKey)).thenReturn(userRegistrationDataCacheString);
        when(this.jsonService.fromJson(userRegistrationDataCacheString, UserRegistrationDataCache.class)).thenReturn(userRegistrationDataCache);
        doThrow(new RuntimeException("Database error")).when(this.userRepository).save(any(User.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.completeUserRegistrationUseCase.execute(testEmailCode, testKey);
        });

        assertEquals("Database error", exception.getMessage());
    }
}
