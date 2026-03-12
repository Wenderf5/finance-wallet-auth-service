package com.financewallet.redis;

import io.lettuce.core.RedisClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class RedisClientProducer {
    @Produces
    @ApplicationScoped
    public RedisClient redisClient() {
        return RedisClient.create(System.getenv("REDIS_URL_CONNECTION"));
    }
}
