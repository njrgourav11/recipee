package com.publicis.recipes.dto;

import com.publicis.recipes.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeMapperTest {

    private RecipeMapper recipeMapper;
    private Recipe testRecipe;

    @BeforeEach
    void setUp() {
        recipeMapper = new RecipeMapper();
        
        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setName("Test Recipe");
        testRecipe.setCuisine("Italian");
        testRecipe.setDifficulty("Medium");
        testRecipe.setRating(4.5);
        testRecipe.setIngredients(Arrays.asList("ingredient1", "ingredient2", "ingredient3"));
        testRecipe.setInstructions(Arrays.asList("step1", "step2", "step3"));
        testRecipe.setImage("https://example.com/image.jpg");
        testRecipe.setCreatedAt(LocalDateTime.of(2023, 1, 1, 12, 0));
        testRecipe.setUpdatedAt(LocalDateTime.of(2023, 1, 2, 12, 0));
    }

    @Test
    void toDto_WithCompleteRecipe_MapsAllFields() {
        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals(testRecipe.getId(), result.getId());
        assertEquals(testRecipe.getName(), result.getName());
        assertEquals(testRecipe.getCuisine(), result.getCuisine());
        assertEquals(testRecipe.getDifficulty(), result.getDifficulty());
        assertEquals(testRecipe.getRating(), result.getRating());
        assertEquals(testRecipe.getIngredients(), result.getIngredients());
        assertEquals(testRecipe.getInstructions(), result.getInstructions());
        assertEquals(testRecipe.getImage(), result.getImage());
        assertEquals(testRecipe.getCreatedAt(), result.getCreatedAt());
        assertEquals(testRecipe.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void toDto_WithNullRecipe_ReturnsNull() {
        RecipeDto result = recipeMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void toDto_WithMinimalRecipe_MapsAvailableFields() {
        Recipe minimalRecipe = new Recipe();
        minimalRecipe.setId(2L);
        minimalRecipe.setName("Minimal Recipe");

        RecipeDto result = recipeMapper.toDto(minimalRecipe);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Minimal Recipe", result.getName());
        assertNull(result.getCuisine());
        assertNull(result.getDifficulty());
        assertNull(result.getRating());
        assertNotNull(result.getIngredients());
        assertNotNull(result.getInstructions());
        assertNull(result.getImage());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void toDto_WithEmptyLists_MapsEmptyLists() {
        testRecipe.setIngredients(Collections.emptyList());
        testRecipe.setInstructions(Collections.emptyList());

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertNotNull(result.getIngredients());
        assertNotNull(result.getInstructions());
        assertTrue(result.getIngredients().isEmpty());
        assertTrue(result.getInstructions().isEmpty());
    }

    @Test
    void toDto_WithNullLists_MapsNullLists() {
        testRecipe.setIngredients(null);
        testRecipe.setInstructions(null);

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertNotNull(result.getIngredients());
        assertNotNull(result.getInstructions());
        assertTrue(result.getIngredients().isEmpty());
        assertTrue(result.getInstructions().isEmpty());
    }

    @Test
    void toDto_WithZeroRating_MapsCorrectly() {
        testRecipe.setRating(0.0);

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals(0.0, result.getRating());
    }

    @Test
    void toDto_WithMaxRating_MapsCorrectly() {
        testRecipe.setRating(5.0);

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals(5.0, result.getRating());
    }

    @Test
    void toDto_WithNullRating_MapsCorrectly() {
        testRecipe.setRating(null);

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertNull(result.getRating());
    }

    @Test
    void toDto_WithEmptyStrings_MapsEmptyStrings() {
        testRecipe.setName("");
        testRecipe.setCuisine("");
        testRecipe.setDifficulty("");
        testRecipe.setImage("");

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals("", result.getCuisine());
        assertEquals("", result.getDifficulty());
        assertEquals("", result.getImage());
    }

    @Test
    void toDto_WithSingleItemLists_MapsCorrectly() {
        testRecipe.setIngredients(Arrays.asList("single ingredient"));
        testRecipe.setInstructions(Arrays.asList("single instruction"));

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals(1, result.getIngredients().size());
        assertEquals(1, result.getInstructions().size());
        assertEquals("single ingredient", result.getIngredients().get(0));
        assertEquals("single instruction", result.getInstructions().get(0));
    }

    @Test
    void toDto_WithLargeLists_MapsCorrectly() {
        List<String> manyIngredients = Arrays.asList(
            "ingredient1", "ingredient2", "ingredient3", "ingredient4", "ingredient5",
            "ingredient6", "ingredient7", "ingredient8", "ingredient9", "ingredient10"
        );
        List<String> manyInstructions = Arrays.asList(
            "step1", "step2", "step3", "step4", "step5",
            "step6", "step7", "step8", "step9", "step10"
        );
        
        testRecipe.setIngredients(manyIngredients);
        testRecipe.setInstructions(manyInstructions);

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals(10, result.getIngredients().size());
        assertEquals(10, result.getInstructions().size());
        assertEquals(manyIngredients, result.getIngredients());
        assertEquals(manyInstructions, result.getInstructions());
    }

    @Test
    void toDto_WithSpecialCharacters_MapsCorrectly() {
        testRecipe.setName("Recipe with Special Characters: àáâãäåæçèéêë");
        testRecipe.setCuisine("Cuisine with accents: Français");
        testRecipe.setDifficulty("Medium");

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals("Recipe with Special Characters: àáâãäåæçèéêë", result.getName());
        assertEquals("Cuisine with accents: Français", result.getCuisine());
        assertEquals("Medium", result.getDifficulty());
    }

    @Test
    void toDto_WithVeryLongStrings_MapsCorrectly() {
        String longTitle = "A".repeat(1000);
        String longDescription = "B".repeat(2000);
        
        testRecipe.setName(longTitle);
        testRecipe.setDifficulty("Easy");

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals(longTitle, result.getName());
        assertEquals("Easy", result.getDifficulty());
        assertEquals(1000, result.getName().length());
    }

    @Test
    void toDto_WithNegativeId_MapsCorrectly() {
        testRecipe.setId(-1L);

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals(-1L, result.getId());
    }

    @Test
    void toDto_WithNullId_MapsCorrectly() {
        testRecipe.setId(null);

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertNull(result.getId());
    }

    @Test
    void toDto_PreservesListOrder() {
        List<String> orderedIngredients = Arrays.asList("first", "second", "third", "fourth");
        List<String> orderedInstructions = Arrays.asList("step1", "step2", "step3", "step4");
        
        testRecipe.setIngredients(orderedIngredients);
        testRecipe.setInstructions(orderedInstructions);

        RecipeDto result = recipeMapper.toDto(testRecipe);

        assertNotNull(result);
        assertEquals("first", result.getIngredients().get(0));
        assertEquals("second", result.getIngredients().get(1));
        assertEquals("third", result.getIngredients().get(2));
        assertEquals("fourth", result.getIngredients().get(3));
        
        assertEquals("step1", result.getInstructions().get(0));
        assertEquals("step2", result.getInstructions().get(1));
        assertEquals("step3", result.getInstructions().get(2));
        assertEquals("step4", result.getInstructions().get(3));
    }
}