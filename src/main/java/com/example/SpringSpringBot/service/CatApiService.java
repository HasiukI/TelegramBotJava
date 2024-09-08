package com.example.SpringSpringBot.service;


import com.example.SpringSpringBot.config.AppConfig;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatApiService {

    private final RestTemplate restTemplate;
    private final AppConfig config;

    @Autowired
    public CatApiService(RestTemplateBuilder restTemplateBuilder, AppConfig config) {
        this.restTemplate = restTemplateBuilder.build();
        this.config = config;
    }

    public String getRandomCatImage() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", config.getApiKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                config.getApiUrl(),
                HttpMethod.GET,
                entity,
                String.class
        );

        // Парсинг JSON відповіді для отримання URL зображення
        String imageUrl = new JSONArray(response.getBody()).getJSONObject(0).getString("url");
        return imageUrl;
    }
}
