package com.financewallet.redis;

import com.financewallet.exceptions.RedisOperationException;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisTemplate {
    private RedisClient client;

    public RedisTemplate() {
        this.client = RedisClient.create(System.getenv("REDIS_URL_CONNECTION"));
    }

    public void set(String key, String value) {
        try {
            StatefulRedisConnection<String, String> connection = client.connect();
            RedisCommands<String, String> redisCommands = connection.sync();
            redisCommands.set(key, value);
            connection.close();
        } catch (Exception e) {
            throw new RedisOperationException(e.getMessage());
        }
    }

    public String get(String key) {
        try {
            StatefulRedisConnection<String, String> connection = client.connect();
            RedisCommands<String, String> redisCommands = connection.sync();
            String result = redisCommands.get(key);
            connection.close();

            return result;
        } catch (Exception e) {
            throw new RedisOperationException(e.getMessage());
        }
    }
}
