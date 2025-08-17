package com.publicis.recipes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Root controller to handle requests to the application root.
 * Provides basic information about the API.
 */
@RestController
public class RootController {

    /**
     * Handle requests to the root path
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Recipe Management System API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("apiBase", "/api");
        response.put("endpoints", Map.of(
            "recipes", "/api/recipes/search",
            "statistics", "/api/recipes/statistics",
            "load", "/api/recipes/load",
            "swagger", "/swagger-ui.html"
        ));
        return ResponseEntity.ok(response);
    }
}