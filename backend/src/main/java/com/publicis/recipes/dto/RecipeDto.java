package com.publicis.recipes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Recipe entity.
 * 
 * This DTO is used for API responses and requests, providing a clean
 * interface for recipe data without exposing internal entity details.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@Schema(description = "Recipe information")
public class RecipeDto {

    @Schema(description = "Unique identifier of the recipe", example = "1")
    private Long id;

    @Schema(description = "Name of the recipe", example = "Classic Margherita Pizza", required = true)
    @NotBlank(message = "Recipe name is required")
    @Size(min = 2, max = 255, message = "Recipe name must be between 2 and 255 characters")
    private String name;

    @Schema(description = "Cuisine type", example = "Italian", required = true)
    @NotBlank(message = "Cuisine is required")
    @Size(min = 2, max = 100, message = "Cuisine must be between 2 and 100 characters")
    private String cuisine;

    @Schema(description = "Difficulty level", example = "Easy", allowableValues = {"Easy", "Medium", "Hard"}, required = true)
    @NotBlank(message = "Difficulty level is required")
    @Pattern(regexp = "Easy|Medium|Hard", message = "Difficulty must be Easy, Medium, or Hard")
    private String difficulty;

    @Schema(description = "Preparation time in minutes", example = "20")
    @JsonProperty("prep_time_minutes")
    @Min(value = 0, message = "Preparation time cannot be negative")
    @Max(value = 1440, message = "Preparation time cannot exceed 24 hours")
    private Integer prepTimeMinutes;

    @Schema(description = "Cooking time in minutes", example = "15")
    @JsonProperty("cook_time_minutes")
    @Min(value = 0, message = "Cook time cannot be negative")
    @Max(value = 1440, message = "Cook time cannot exceed 24 hours")
    private Integer cookTimeMinutes;

    @Schema(description = "Number of servings", example = "4")
    @Min(value = 1, message = "Servings must be at least 1")
    @Max(value = 50, message = "Servings cannot exceed 50")
    private Integer servings;

    @Schema(description = "List of ingredients", example = "[\"Pizza dough\", \"Tomato sauce\", \"Mozzarella cheese\"]")
    @Size(min = 1, message = "Recipe must have at least one ingredient")
    private List<String> ingredients;

    @Schema(description = "Cooking instructions", example = "[\"Preheat oven to 475°F\", \"Roll out dough\", \"Add toppings\"]")
    @Size(min = 1, message = "Recipe must have at least one instruction")
    private List<String> instructions;

    @Schema(description = "Recipe tags", example = "[\"Pizza\", \"Italian\", \"Vegetarian\"]")
    private List<String> tags;

    @Schema(description = "Recipe image URL", example = "https://example.com/pizza.jpg")
    @Pattern(regexp = "^(https?://.*\\.(jpg|jpeg|png|gif|webp))?$", 
             message = "Image must be a valid URL ending with jpg, jpeg, png, gif, or webp")
    private String image;

    @Schema(description = "Average rating", example = "4.5")
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double rating;

    @Schema(description = "Number of reviews", example = "120")
    @JsonProperty("review_count")
    @Min(value = 0, message = "Review count cannot be negative")
    private Integer reviewCount;

    @Schema(description = "Calories per serving", example = "285")
    @JsonProperty("calories_per_serving")
    @Min(value = 0, message = "Calories cannot be negative")
    @Max(value = 5000, message = "Calories per serving seems unrealistic")
    private Integer caloriesPerServing;

    @Schema(description = "Total cooking time in minutes", example = "35")
    @JsonProperty("total_time_minutes")
    private Integer totalTimeMinutes;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public RecipeDto() {}

    // Constructor with essential fields
    public RecipeDto(String name, String cuisine, String difficulty) {
        this.name = name;
        this.cuisine = cuisine;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Integer getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public void setCookTimeMinutes(Integer cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getCaloriesPerServing() {
        return caloriesPerServing;
    }

    public void setCaloriesPerServing(Integer caloriesPerServing) {
        this.caloriesPerServing = caloriesPerServing;
    }

    public Integer getTotalTimeMinutes() {
        return totalTimeMinutes;
    }

    public void setTotalTimeMinutes(Integer totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}