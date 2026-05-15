package com.financewallet.auth.application.gateways;

public interface CacheGateway {
    String get(String key);

    void save(String key, String value);

    void save(String key, String value, Long ttl);

    Long getTtl(String key);

    void delete(String key);
}
