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
import com.financewallet.auth.application.dto.UserRegistrationDataCache;
import com.financewallet.auth.application.exception.EmailCodeException;
import com.financewallet.auth.infrastructure.exception.CacheOperationException;
import com.financewallet.auth.application.gateway.CacheGateway;
import com.financewallet.auth.application.service.JsonService;
import com.financewallet.auth.application.service.TokenService;
import com.financewallet.auth.application.usercase.CompleteUserRegistrationUseCase;
import com.financewallet.auth.domain.entity.User;
import com.financewallet.auth.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CompleteUserRegistrationUseCaseTest {
    @Mock
    private CacheGateway cacheGateway;

    @Mock
    private JsonService jsonService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private CompleteUserRegistrationUseCase completeUserRegistrationUseCase;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String ACCESS_TOKEN_TYPE = "access_token";

    @Test
    @DisplayName("Should return access token when everything is ok")
    public void shouldReturnAccessToken() throws JsonProcessingException{
        String testToken = "testToken";
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
        when(this.tokenService.generate(eq(ACCESS_TOKEN_TYPE), any(Instant.class))).thenReturn(testToken);

        String result = this.completeUserRegistrationUseCase.execute(userRegistrationDataCache.getEmailCode(), testKey);
        assertEquals(testToken, result);
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
    @DisplayName("Should throw CacheOperationException when cacheGateway cannot find the record in cache")
    public void shouldThrowCacheOperationExceptionWhenCacheIsMissing() {
        String testKey = "testKey";

        doThrow(new CacheOperationException("The key testKey does not exist"))
            .when(this.cacheGateway).get(testKey);

        CacheOperationException exception = assertThrows(CacheOperationException.class, () -> {
            this.completeUserRegistrationUseCase.execute("123456", testKey);
        });

        assertEquals("The key testKey does not exist", exception.getMessage());
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
