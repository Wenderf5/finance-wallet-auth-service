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
}
