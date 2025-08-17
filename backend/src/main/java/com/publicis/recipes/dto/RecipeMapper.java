package com.publicis.recipes.dto;

import com.publicis.recipes.entity.Recipe;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

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

    public List<RecipeDto> toDtoList(List<Recipe> recipes) {
        if (recipes == null) {
            return null;
        }

        return recipes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Recipe> toEntityList(List<RecipeDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Recipe updateEntity(Recipe existingRecipe, RecipeDto dto) {
        if (existingRecipe == null || dto == null) {
            return existingRecipe;
        }

        Long originalId = existingRecipe.getId();
        var originalCreatedAt = existingRecipe.getCreatedAt();

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

        existingRecipe.setId(originalId);
        existingRecipe.setCreatedAt(originalCreatedAt);

        return existingRecipe;
    }
}