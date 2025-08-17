package com.publicis.recipes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Swagger documentation.
 * 
 * This class configures the OpenAPI specification for the Recipe Management System,
 * providing comprehensive API documentation with interactive UI.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Creates the OpenAPI configuration bean.
     * 
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI recipeManagementOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local development server");

        Contact contact = new Contact()
                .name("Recipe Management Team")
                .email("support@publicis.com")
                .url("https://github.com/publicis/recipe-management");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Recipe Management System API")
                .version("1.0.0")
                .description("""
                    A comprehensive RESTful API for recipe management with full-text search capabilities.
                    
                    ## Features
                    - **Full-text search** on recipe names and cuisines using Hibernate Search
                    - **External API integration** with resilience patterns
                    - **H2 in-memory database** for fast data access
                    - **Comprehensive validation** and exception handling
                    - **Real-time data loading** from external recipe sources
                    
                    ## Getting Started
                    1. Load recipe data: `POST /api/recipes/load`
                    2. Search recipes: `GET /api/recipes/search?q=pizza`
                    3. Get specific recipe: `GET /api/recipes/{id}`
                    
                    ## Search Tips
                    - Use at least 3 characters for search queries
                    - Search works on recipe names and cuisine types
                    - Results are paginated (default: 20 items per page)
                    """)
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}