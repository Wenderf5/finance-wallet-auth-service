package com.financewallet.auth.infrastructure.adapter.out;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.financewallet.auth.application.gateway.CacheGateway;
import com.financewallet.auth.infrastructure.exception.CacheOperationException;

@Service
public class CacheGatewayImp implements CacheGateway {
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public CacheGatewayImp(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String get(String key) {
        try {
            String result = redisTemplate.opsForValue().get(key);
            if (result == null) {
                throw new RuntimeException("The key " + key + " does not exist");
            }
            return result;
        } catch (Exception e) {
            throw new CacheOperationException(e.getMessage(), e);
        }
    }

    @Override
    public void save(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new CacheOperationException(e.getMessage(), e);
        }
    }

    @Override
    public void save(String key, String value, Long ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new CacheOperationException(e.getMessage(), e);
        }
    }

    @Override
    public Long getTtl(String key) {
        try {
            Long result = redisTemplate.getExpire(key, TimeUnit.SECONDS);

            if (result.equals(-1L)) {
                throw new RuntimeException("The key " + key + " does not have TTL");
            }
            if (result.equals(-2L)) {
                throw new RuntimeException("The key " + key + " does not exist");
            }

            return result;
        } catch (Exception e) {
            throw new CacheOperationException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            boolean result = redisTemplate.delete(key);
            if (result == false) {
                throw new RuntimeException("The key " + key + " does not exist");
            }
        } catch (Exception e) {
            throw new CacheOperationException(e.getMessage(), e);
        }
    }
}
