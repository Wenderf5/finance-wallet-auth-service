package com.financewallet.auth.infrastructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financewallet.auth.application.service.JsonService;
import com.google.gson.Gson;

@Service
public class JsonServiceImp implements JsonService {
    private Gson gson;

    @Autowired
    public JsonServiceImp(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String toJson(Object object) {
        return this.gson.toJson(object);
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return this.gson.fromJson(json, classOfT);
    }
}
