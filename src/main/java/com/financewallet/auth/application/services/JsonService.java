package com.financewallet.auth.application.services;

public interface JsonService {
    String toJson(Object object);
    <T> T fromJson(String json, Class<T> classOfT);
}
