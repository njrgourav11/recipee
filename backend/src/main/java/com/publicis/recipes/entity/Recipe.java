package com.publicis.recipes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
// import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Recipe entity representing a cooking recipe with full-text search capabilities.
 * 
 * This entity is indexed by Hibernate Search for efficient full-text searching
 * on recipe names and cuisines. It includes comprehensive validation and
 * proper JPA mappings for H2 database storage.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@Entity
@Table(name = "recipes", indexes = {
    @Index(name = "idx_recipe_name", columnList = "name"),
    @Index(name = "idx_recipe_cuisine", columnList = "cuisine"),
    @Index(name = "idx_recipe_difficulty", columnList = "difficulty"),
    @Index(name = "idx_recipe_cook_time", columnList = "cook_time_minutes")
})
// @Indexed
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @FullTextField(analyzer = "standard")
    @Column(nullable = false, length = 255)
    @NotBlank(message = "Recipe name is required")
    @Size(min = 2, max = 255, message = "Recipe name must be between 2 and 255 characters")
    private String name;

    // @FullTextField(analyzer = "standard")
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Cuisine is required")
    @Size(min = 2, max = 100, message = "Cuisine must be between 2 and 100 characters")
    private String cuisine;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Difficulty level is required")
    @Pattern(regexp = "Easy|Medium|Hard", message = "Difficulty must be Easy, Medium, or Hard")
    private String difficulty;

    @Column(name = "prep_time_minutes")
    @Min(value = 0, message = "Preparation time cannot be negative")
    @Max(value = 1440, message = "Preparation time cannot exceed 24 hours")
    private Integer prepTimeMinutes;

    @Column(name = "cook_time_minutes")
    @Min(value = 0, message = "Cook time cannot be negative")
    @Max(value = 1440, message = "Cook time cannot exceed 24 hours")
    private Integer cookTimeMinutes;

    @Column(name = "servings")
    @Min(value = 1, message = "Servings must be at least 1")
    @Max(value = 50, message = "Servings cannot exceed 50")
    private Integer servings;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "recipe_ingredients",
        joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "ingredient", length = 500)
    @Size(min = 1, message = "Recipe must have at least one ingredient")
    private List<String> ingredients = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "recipe_instructions",
        joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "instruction", length = 1000)
    @OrderColumn(name = "step_order")
    @Size(min = 1, message = "Recipe must have at least one instruction")
    private List<String> instructions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "recipe_tags",
        joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "tag", length = 50)
    private List<String> tags = new ArrayList<>();

    @Column(name = "image", length = 500)
    @Pattern(regexp = "^(https?://.*\\.(jpg|jpeg|png|gif|webp))?$", 
             message = "Image must be a valid URL ending with jpg, jpeg, png, gif, or webp")
    private String image;

    @Column(name = "rating")
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double rating;

    @Column(name = "review_count")
    @Min(value = 0, message = "Review count cannot be negative")
    private Integer reviewCount;

    @Column(name = "calories_per_serving")
    @Min(value = 0, message = "Calories cannot be negative")
    @Max(value = 5000, message = "Calories per serving seems unrealistic")
    private Integer caloriesPerServing;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public Recipe() {}

    // Constructor with essential fields
    public Recipe(String name, String cuisine, String difficulty) {
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
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions != null ? instructions : new ArrayList<>();
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
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

    // Utility methods
    public void addIngredient(String ingredient) {
        if (ingredient != null && !ingredient.trim().isEmpty()) {
            this.ingredients.add(ingredient.trim());
        }
    }

    public void addInstruction(String instruction) {
        if (instruction != null && !instruction.trim().isEmpty()) {
            this.instructions.add(instruction.trim());
        }
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !this.tags.contains(tag.trim())) {
            this.tags.add(tag.trim());
        }
    }

    public Integer getTotalTimeMinutes() {
        int prep = prepTimeMinutes != null ? prepTimeMinutes : 0;
        int cook = cookTimeMinutes != null ? cookTimeMinutes : 0;
        return prep + cook;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(id, recipe.id) &&
               Objects.equals(name, recipe.name) &&
               Objects.equals(cuisine, recipe.cuisine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cuisine);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cuisine='" + cuisine + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", prepTimeMinutes=" + prepTimeMinutes +
                ", cookTimeMinutes=" + cookTimeMinutes +
                ", servings=" + servings +
                ", rating=" + rating +
                ", reviewCount=" + reviewCount +
                ", caloriesPerServing=" + caloriesPerServing +
                '}';
    }
}