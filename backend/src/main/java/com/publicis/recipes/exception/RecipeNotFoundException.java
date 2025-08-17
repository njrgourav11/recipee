package com.publicis.recipes.exception;

/**
 * Exception thrown when a requested recipe is not found.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
public class RecipeNotFoundException extends RuntimeException {

    /**
     * Constructs a new RecipeNotFoundException with the specified detail message.
     * 
     * @param message the detail message
     */
    public RecipeNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new RecipeNotFoundException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public RecipeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new RecipeNotFoundException for a specific recipe ID.
     * 
     * @param recipeId the ID of the recipe that was not found
     */
    public RecipeNotFoundException(Long recipeId) {
        super("Recipe not found with ID: " + recipeId);
    }
}