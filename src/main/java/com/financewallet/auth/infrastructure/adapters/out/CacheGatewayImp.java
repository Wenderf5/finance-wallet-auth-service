package com.financewallet.auth.infrastructure.adapters.out;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.financewallet.auth.application.gateways.CacheGateway;
import com.financewallet.auth.infrastructure.exceptions.CacheOperationException;

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
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new CacheOperationException("Error retrieving value from cache for key: " + key, e);
        }
    }

    @Override
    public void save(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new CacheOperationException("Error saving value to cache for key: " + key, e);
        }
    }

    @Override
    public void save(String key, String value, Long ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new CacheOperationException("Error saving value to cache for key: " + key, e);
        }
    }

    @Override
    public Long getTtl(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new CacheOperationException("Error retrieving TTL from cache for key: " + key, e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new CacheOperationException("Error deleting value from cache for key: " + key, e);
        }
    }
}
