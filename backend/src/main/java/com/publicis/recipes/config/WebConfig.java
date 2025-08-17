package com.publicis.recipes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${external.api.recipes.timeout:5000}")
    private int apiTimeout;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000",
                    "http://localhost:3001",
                    "http://localhost:5173",
                    "http://localhost:5174",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:3001",
                    "http://127.0.0.1:5173",
                    "http://127.0.0.1:5174"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(apiTimeout))
                .setReadTimeout(Duration.ofMillis(apiTimeout))
                .build();
    }
}