package com.example.SpringSpringBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class AppConfig {

    @Value("${bot.name}")
    String botName;
    @Value("${bot.key}")
    String token;

    @Value("${cat.api.url}")
    private String apiUrl;

    @Value("${cat.api.key}")
    private String apiKey;

    @Value("${mono.api}")
    private String apiMono;
}
