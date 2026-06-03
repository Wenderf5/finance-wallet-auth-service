package com.financewallet.auth.infrastructure.services;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.financewallet.auth.application.services.CodeGeneratorService;

@Service
public class CodeGeneratorServiceImp implements CodeGeneratorService {
    private Random random = new Random();

    public String generate() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(0, 10));
        }

        return code.toString();
    }
}
