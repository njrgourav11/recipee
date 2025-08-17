package com.publicis.recipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application class for Recipe Management System.
 *
 * This application provides RESTful APIs for recipe management with features including:
 * - Full-text search using Hibernate Search
 * - External API integration with resilience patterns
 * - H2 in-memory database for fast data access
 * - Comprehensive validation and exception handling
 * - Asynchronous data loading capabilities
 *
 * @author Recipe Management Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableRetry
@EnableAsync
public class RecipeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeApplication.class, args);
    }
}