package com.financewallet.auth.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootMessage implements CommandLineRunner {
    @Value("${server.port}")
    private String port;

    @Override
    public void run(String... args) {
        System.out.println("Server is running on port " + port + "!");
    }
}
