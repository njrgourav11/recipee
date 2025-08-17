package com.publicis.recipes.dto;

import com.publicis.recipes.entity.Recipe;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility for converting between Recipe entity and RecipeDto.
 * 
 * This component provides methods to map Recipe entities to DTOs and vice versa,
 * ensuring clean separation between internal data models and API contracts.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@Component
public class RecipeMapper {

    /**
     * Converts a Recipe entity to RecipeDto.
     * 
     * @param recipe the Recipe entity to convert
     * @return RecipeDto representation of the entity
     */
    public RecipeDto toDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        RecipeDto dto = new RecipeDto();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setCuisine(recipe.getCuisine());
        dto.setDifficulty(recipe.getDifficulty());
        dto.setPrepTimeMinutes(recipe.getPrepTimeMinutes());
        dto.setCookTimeMinutes(recipe.getCookTimeMinutes());
        dto.setServings(recipe.getServings());
        dto.setIngredients(recipe.getIngredients());
        dto.setInstructions(recipe.getInstructions());
        dto.setTags(recipe.getTags());
        dto.setImage(recipe.getImage());
        dto.setRating(recipe.getRating());
        dto.setReviewCount(recipe.getReviewCount());
        dto.setCaloriesPerServing(recipe.getCaloriesPerServing());
        dto.setTotalTimeMinutes(recipe.getTotalTimeMinutes());
        dto.setCreatedAt(recipe.getCreatedAt());
        dto.setUpdatedAt(recipe.getUpdatedAt());

        return dto;
    }

    /**
     * Converts a RecipeDto to Recipe entity.
     * 
     * @param dto the RecipeDto to convert
     * @return Recipe entity representation of the DTO
     */
    public Recipe toEntity(RecipeDto dto) {
        if (dto == null) {
            return null;
        }

        Recipe recipe = new Recipe();
        recipe.setId(dto.getId());
        recipe.setName(dto.getName());
        recipe.setCuisine(dto.getCuisine());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setPrepTimeMinutes(dto.getPrepTimeMinutes());
        recipe.setCookTimeMinutes(dto.getCookTimeMinutes());
        recipe.setServings(dto.getServings());
        recipe.setIngredients(dto.getIngredients());
        recipe.setInstructions(dto.getInstructions());
        recipe.setTags(dto.getTags());
        recipe.setImage(dto.getImage());
        recipe.setRating(dto.getRating());
        recipe.setReviewCount(dto.getReviewCount());
        recipe.setCaloriesPerServing(dto.getCaloriesPerServing());
        recipe.setCreatedAt(dto.getCreatedAt());
        recipe.setUpdatedAt(dto.getUpdatedAt());

        return recipe;
    }

    /**
     * Converts a list of Recipe entities to a list of RecipeDtos.
     * 
     * @param recipes the list of Recipe entities to convert
     * @return list of RecipeDto representations
     */
    public List<RecipeDto> toDtoList(List<Recipe> recipes) {
        if (recipes == null) {
            return null;
        }

        return recipes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of RecipeDtos to a list of Recipe entities.
     * 
     * @param dtos the list of RecipeDtos to convert
     * @return list of Recipe entity representations
     */
    public List<Recipe> toEntityList(List<RecipeDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing Recipe entity with data from RecipeDto.
     * This method preserves the entity's ID and timestamps.
     * 
     * @param existingRecipe the existing Recipe entity to update
     * @param dto the RecipeDto containing updated data
     * @return the updated Recipe entity
     */
    public Recipe updateEntity(Recipe existingRecipe, RecipeDto dto) {
        if (existingRecipe == null || dto == null) {
            return existingRecipe;
        }

        // Preserve ID and timestamps
        Long originalId = existingRecipe.getId();
        var originalCreatedAt = existingRecipe.getCreatedAt();

        // Update fields from DTO
        existingRecipe.setName(dto.getName());
        existingRecipe.setCuisine(dto.getCuisine());
        existingRecipe.setDifficulty(dto.getDifficulty());
        existingRecipe.setPrepTimeMinutes(dto.getPrepTimeMinutes());
        existingRecipe.setCookTimeMinutes(dto.getCookTimeMinutes());
        existingRecipe.setServings(dto.getServings());
        existingRecipe.setIngredients(dto.getIngredients());
        existingRecipe.setInstructions(dto.getInstructions());
        existingRecipe.setTags(dto.getTags());
        existingRecipe.setImage(dto.getImage());
        existingRecipe.setRating(dto.getRating());
        existingRecipe.setReviewCount(dto.getReviewCount());
        existingRecipe.setCaloriesPerServing(dto.getCaloriesPerServing());

        // Restore preserved values
        existingRecipe.setId(originalId);
        existingRecipe.setCreatedAt(originalCreatedAt);

        return existingRecipe;
    }
}