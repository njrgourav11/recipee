package com.publicis.recipes.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicis.recipes.entity.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for fetching recipe data from external API (dummyjson.com).
 * 
 * This service implements resilience patterns including:
 * - Retry mechanism with exponential backoff
 * - Comprehensive error handling
 * - Request/response logging
 * - Data transformation and validation
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@Service
public class ExternalRecipeService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalRecipeService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${external.api.recipes.url}")
    private String recipesApiUrl;

    public ExternalRecipeService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches all recipes from the external API with retry mechanism.
     * 
     * @return List of Recipe entities parsed from external API
     * @throws ExternalApiException if all retry attempts fail
     */
    @Retryable(
        retryFor = {ResourceAccessException.class, HttpServerErrorException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public List<Recipe> fetchAllRecipes() {
        logger.info("Fetching recipes from external API: {}", recipesApiUrl);
        
        try {
            String response = restTemplate.getForObject(recipesApiUrl, String.class);
            
            if (response == null || response.trim().isEmpty()) {
                logger.warn("Received empty response from external API");
                return new ArrayList<>();
            }

            logger.debug("Received response from external API, length: {} characters", response.length());
            
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode recipesNode = rootNode.get("recipes");
            
            if (recipesNode == null || !recipesNode.isArray()) {
                logger.warn("Invalid response format: 'recipes' array not found");
                return new ArrayList<>();
            }

            List<Recipe> recipes = new ArrayList<>();
            int processedCount = 0;
            int skippedCount = 0;

            for (JsonNode recipeNode : recipesNode) {
                try {
                    Recipe recipe = mapJsonToRecipe(recipeNode);
                    if (recipe != null) {
                        recipes.add(recipe);
                        processedCount++;
                    } else {
                        skippedCount++;
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse recipe from JSON: {}", e.getMessage());
                    skippedCount++;
                }
            }

            logger.info("Successfully processed {} recipes, skipped {} invalid recipes", 
                       processedCount, skippedCount);
            
            return recipes;

        } catch (HttpClientErrorException e) {
            logger.error("Client error while fetching recipes: {} - {}", e.getStatusCode(), e.getMessage());
            throw new ExternalApiException("Client error from external API: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            logger.error("Server error while fetching recipes: {} - {}", e.getStatusCode(), e.getMessage());
            throw new ExternalApiException("Server error from external API: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            logger.error("Network error while fetching recipes: {}", e.getMessage());
            throw new ExternalApiException("Network error accessing external API: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching recipes", e);
            throw new ExternalApiException("Unexpected error fetching recipes: " + e.getMessage(), e);
        }
    }

    /**
     * Maps a JSON node to a Recipe entity.
     * 
     * @param recipeNode the JSON node containing recipe data
     * @return Recipe entity or null if mapping fails
     */
    private Recipe mapJsonToRecipe(JsonNode recipeNode) {
        try {
            Recipe recipe = new Recipe();

            // Required fields
            String name = getTextValue(recipeNode, "name");
            String cuisine = getTextValue(recipeNode, "cuisine");
            
            if (name == null || name.trim().isEmpty() || cuisine == null || cuisine.trim().isEmpty()) {
                logger.debug("Skipping recipe with missing required fields: name={}, cuisine={}", name, cuisine);
                return null;
            }

            recipe.setName(name.trim());
            recipe.setCuisine(cuisine.trim());

            // Optional fields with defaults
            recipe.setDifficulty(getTextValue(recipeNode, "difficulty", "Medium"));
            recipe.setPrepTimeMinutes(getIntValue(recipeNode, "prepTimeMinutes"));
            recipe.setCookTimeMinutes(getIntValue(recipeNode, "cookTimeMinutes"));
            recipe.setServings(getIntValue(recipeNode, "servings"));
            recipe.setCaloriesPerServing(getIntValue(recipeNode, "caloriesPerServing"));
            recipe.setRating(getDoubleValue(recipeNode, "rating"));
            recipe.setReviewCount(getIntValue(recipeNode, "reviewCount"));
            recipe.setImage(getTextValue(recipeNode, "image"));

            // Handle arrays
            recipe.setIngredients(getStringList(recipeNode, "ingredients"));
            recipe.setInstructions(getStringList(recipeNode, "instructions"));
            recipe.setTags(getStringList(recipeNode, "tags"));

            logger.debug("Successfully mapped recipe: {}", recipe.getName());
            return recipe;

        } catch (Exception e) {
            logger.warn("Failed to map JSON to Recipe: {}", e.getMessage());
            return null;
        }
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return getTextValue(node, fieldName, null);
    }

    private String getTextValue(JsonNode node, String fieldName, String defaultValue) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull() && fieldNode.isTextual()) {
            String value = fieldNode.asText().trim();
            return value.isEmpty() ? defaultValue : value;
        }
        return defaultValue;
    }

    private Integer getIntValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull() && fieldNode.isNumber()) {
            return fieldNode.asInt();
        }
        return null;
    }

    private Double getDoubleValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull() && fieldNode.isNumber()) {
            return fieldNode.asDouble();
        }
        return null;
    }

    private List<String> getStringList(JsonNode node, String fieldName) {
        List<String> result = new ArrayList<>();
        JsonNode arrayNode = node.get(fieldName);
        
        if (arrayNode != null && arrayNode.isArray()) {
            for (JsonNode item : arrayNode) {
                if (item.isTextual()) {
                    String value = item.asText().trim();
                    if (!value.isEmpty()) {
                        result.add(value);
                    }
                }
            }
        }
        
        return result;
    }

    /**
     * Checks if the external API is accessible.
     * 
     * @return true if the API is accessible, false otherwise
     */
    public boolean isApiAccessible() {
        try {
            logger.debug("Checking API accessibility: {}", recipesApiUrl);
            restTemplate.headForHeaders(recipesApiUrl);
            logger.debug("API is accessible");
            return true;
        } catch (Exception e) {
            logger.warn("API is not accessible: {}", e.getMessage());
            return false;
        }
    }
}