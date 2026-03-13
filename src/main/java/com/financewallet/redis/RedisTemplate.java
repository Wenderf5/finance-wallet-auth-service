package com.financewallet.redis;

import com.financewallet.exceptions.RedisOperationException;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RedisTemplate {
    @Inject
    private RedisClient client;

    public void set(String key, String value) {
        try {
            StatefulRedisConnection<String, String> connection = this.client.connect();
            RedisCommands<String, String> redisCommands = connection.sync();
            redisCommands.set(key, value);
            connection.close();
        } catch (Exception e) {
            throw new RedisOperationException(e.getMessage());
        }
    }

    public void set(String key, String value, Long ttl) {
        try {
            StatefulRedisConnection<String, String> connection = this.client.connect();
            RedisCommands<String, String> redisCommands = connection.sync();
            redisCommands.setex(key, ttl, value);
            connection.close();
        } catch (Exception e) {
            throw new RedisOperationException(e.getMessage());
        }
    }

    public String get(String key) {
        try {
            StatefulRedisConnection<String, String> connection = this.client.connect();
            RedisCommands<String, String> redisCommands = connection.sync();

            String result = redisCommands.get(key);
            if (result == null) {
                throw new Exception("The " + key + " key was not found.");
            }

            connection.close();

            return result;
        } catch (Exception e) {
            throw new RedisOperationException(e.getMessage());
        }
    }

    public Long getTtl(String key) {
        try {
            StatefulRedisConnection<String, String> connection = this.client.connect();
            RedisCommands<String, String> redisCommands = connection.sync();

            Long ttl = redisCommands.ttl(key);
            if (ttl == -1L) {
                connection.close();
                throw new Exception("The '" + key + "' key has not ttl.");
            }
            if (ttl == -2L) {
                connection.close();
                throw new Exception("The '" + key + "' key was not found.");
            }

            connection.close();

            return ttl;
        } catch (Exception e) {
            throw new RedisOperationException(e.getMessage());
        }
    }
}
