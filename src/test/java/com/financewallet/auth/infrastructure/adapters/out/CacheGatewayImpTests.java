package com.financewallet.auth.infrastructure.adapters.out;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.financewallet.auth.infrastructure.exceptions.CacheOperationException;

@ExtendWith(MockitoExtension.class)
public class CacheGatewayImpTests {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> ValueOperations;

    @InjectMocks
    private CacheGatewayImp cacheGatewayImp;

    @Test
    @DisplayName("Should return a register from cache")
    public void shouldReturnRegisterFromCache() {
        String testKey = "testKey";
        String testValue = "testValue";

        when(redisTemplate.opsForValue()).thenReturn(ValueOperations);
        when(redisTemplate.opsForValue().get(testKey)).thenReturn(testValue);

        String result = cacheGatewayImp.get(testKey);
        assertEquals(testValue, result);
    }

    @Test
    @DisplayName("Should throw CacheOperationException when key does not exist")
    public void shouldThrowCacheOperationExceptionWhenKeyDoesNotExistInGet() {
        String testKey = "testKey";

        when(redisTemplate.opsForValue()).thenReturn(ValueOperations);
        when(redisTemplate.opsForValue().get(testKey)).thenReturn(null);

        assertThrows(CacheOperationException.class, () -> cacheGatewayImp.get(testKey));
    }

    @Test
    @DisplayName("Should throw CacheOperationException when Redis throws an exception")
    public void shouldThrowCacheOperationExceptionWhenRedisThrows() {
        String testKey = "testKey";

        when(redisTemplate.opsForValue()).thenThrow(new RedisConnectionFailureException("Connection refused"));

        assertThrows(CacheOperationException.class, () -> cacheGatewayImp.get(testKey));
    }

    @Test
    @DisplayName("Should save value in cache")
    public void shouldSaveValueInCache() {
        String testKey = "testKey";
        String testValue = "testValue";

        when(redisTemplate.opsForValue()).thenReturn(ValueOperations);
        cacheGatewayImp.save(testKey, testValue);
        verify(ValueOperations).set(testKey, testValue);
    }

    @Test
    @DisplayName("Should throw CacheOperationException when Redis throws on save")
    public void shouldThrowCacheOperationExceptionWhenRedisThrowsOnSave() {
        String testKey = "testKey";
        String testValue = "testValue";

        when(redisTemplate.opsForValue()).thenReturn(ValueOperations);
        doThrow(new RedisConnectionFailureException("Connection refused"))
                .when(ValueOperations).set(testKey, testValue);

        assertThrows(CacheOperationException.class, () -> cacheGatewayImp.save(testKey, testValue));
    }

    @Test
    @DisplayName("Should save value in cache with TTL")
    public void shouldSaveValueInCacheWithTtl() {
        String testKey = "testKey";
        String testValue = "testValue";
        Long ttl = 60L;

        when(redisTemplate.opsForValue()).thenReturn(ValueOperations);
        cacheGatewayImp.save(testKey, testValue, ttl);
        verify(ValueOperations).set(testKey, testValue, ttl, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Should throw CacheOperationException when Redis throws on save with TTL")
    public void shouldThrowCacheOperationExceptionWhenRedisThrowsOnSaveWithTtl() {
        String testKey = "testKey";
        String testValue = "testValue";
        Long ttl = 60L;

        when(redisTemplate.opsForValue()).thenReturn(ValueOperations);
        doThrow(new RedisConnectionFailureException("Connection refused"))
                .when(ValueOperations).set(testKey, testValue, ttl, TimeUnit.SECONDS);

        assertThrows(CacheOperationException.class, () -> cacheGatewayImp.save(testKey, testValue, ttl));
    }

    @Test
    @DisplayName("Should return TTL for a key")
    public void shouldReturnTtlForKey() {
        String testKey = "testKey";
        Long expectedTtl = 120L;

        when(redisTemplate.getExpire(testKey, TimeUnit.SECONDS)).thenReturn(expectedTtl);

        Long result = cacheGatewayImp.getTtl(testKey);
        assertEquals(expectedTtl, result);
    }

    @Test
    @DisplayName("Should throw CacheOperationException when key has no TTL (-1)")
    public void shouldThrowCacheOperationExceptionWhenKeyHasNoTtl() {
        String testKey = "testKey";

        when(redisTemplate.getExpire(testKey, TimeUnit.SECONDS)).thenReturn(-1L);

        CacheOperationException exception = assertThrows(CacheOperationException.class,
                () -> cacheGatewayImp.getTtl(testKey));
        assertEquals("The key testKey does not have TTL", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw CacheOperationException when key does not exist (-2)")
    public void shouldThrowCacheOperationExceptionWhenKeyDoesNotExist() {
        String testKey = "testKey";

        when(redisTemplate.getExpire(testKey, TimeUnit.SECONDS)).thenReturn(-2L);

        CacheOperationException exception = assertThrows(CacheOperationException.class,
                () -> cacheGatewayImp.getTtl(testKey));
        assertEquals("The key testKey does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw CacheOperationException when Redis throws on getTtl")
    public void shouldThrowCacheOperationExceptionWhenRedisThrowsOnGetTtl() {
        String testKey = "testKey";

        when(redisTemplate.getExpire(testKey, TimeUnit.SECONDS))
                .thenThrow(new RedisConnectionFailureException("Connection refused"));

        assertThrows(CacheOperationException.class, () -> cacheGatewayImp.getTtl(testKey));
    }

    @Test
    @DisplayName("Should delete key from cache")
    public void shouldDeleteKeyFromCache() {
        String testKey = "testKey";

        when(redisTemplate.delete(testKey)).thenReturn(true);

        cacheGatewayImp.delete(testKey);
        verify(redisTemplate).delete(testKey);
    }

    @Test
    @DisplayName("Should throw CacheOperationException when key does not exist on delete")
    public void shouldThrowCacheOperationExceptionWhenKeyDoesNotExistOnDelete() {
        String testKey = "testKey";

        when(redisTemplate.delete(testKey)).thenReturn(false);

        CacheOperationException exception = assertThrows(CacheOperationException.class,
                () -> cacheGatewayImp.delete(testKey));
        assertEquals("The key testKey does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw CacheOperationException when Redis throws on delete")
    public void shouldThrowCacheOperationExceptionWhenRedisThrowsOnDelete() {
        String testKey = "testKey";

        when(redisTemplate.delete(testKey))
                .thenThrow(new RedisConnectionFailureException("Connection refused"));

        assertThrows(CacheOperationException.class, () -> cacheGatewayImp.delete(testKey));
    }
}
