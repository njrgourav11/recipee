package com.publicis.recipes.service;

import com.publicis.recipes.entity.Recipe;
import com.publicis.recipes.external.ExternalApiException;
import com.publicis.recipes.external.ExternalRecipeService;
import com.publicis.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoadingServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ExternalRecipeService externalRecipeService;

    @InjectMocks
    private DataLoadingService dataLoadingService;

    private Recipe testRecipe1;
    private Recipe testRecipe2;
    private List<Recipe> testRecipes;

    @BeforeEach
    void setUp() {
        testRecipe1 = new Recipe();
        testRecipe1.setId(1L);
        testRecipe1.setName("Test Recipe 1");
        testRecipe1.setCuisine("Italian");
        testRecipe1.setDifficulty("Medium");
        testRecipe1.setRating(4.5);
        testRecipe1.setIngredients(Arrays.asList("ingredient1", "ingredient2"));
        testRecipe1.setInstructions(Arrays.asList("step1", "step2"));

        testRecipe2 = new Recipe();
        testRecipe2.setId(2L);
        testRecipe2.setName("Test Recipe 2");
        testRecipe2.setCuisine("Mexican");
        testRecipe2.setDifficulty("Easy");
        testRecipe2.setRating(4.0);
        testRecipe2.setIngredients(Arrays.asList("ingredient3", "ingredient4"));
        testRecipe2.setInstructions(Arrays.asList("step3", "step4"));

        testRecipes = Arrays.asList(testRecipe1, testRecipe2);
    }

    @Test
    void loadRecipesFromExternalApi_WhenApiAccessible_LoadsDataSuccessfully() throws Exception {
        when(externalRecipeService.isApiAccessible()).thenReturn(true);
        when(externalRecipeService.fetchAllRecipes()).thenReturn(testRecipes);
        when(recipeRepository.count()).thenReturn(0L);
        when(recipeRepository.saveAll(anyList())).thenReturn(testRecipes);

        CompletableFuture<DataLoadingService.DataLoadResult> future = dataLoadingService.loadRecipesFromExternalApi();
        DataLoadingService.DataLoadResult result = future.get();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getCount());
        assertEquals("Successfully loaded recipes", result.getMessage());

        verify(externalRecipeService).isApiAccessible();
        verify(externalRecipeService).fetchAllRecipes();
        verify(recipeRepository).count();
        verify(recipeRepository).saveAll(testRecipes);
    }

    @Test
    void loadRecipesFromExternalApi_WhenApiNotAccessible_ReturnsError() throws Exception {
        when(externalRecipeService.isApiAccessible()).thenReturn(false);

        CompletableFuture<DataLoadingService.DataLoadResult> future = dataLoadingService.loadRecipesFromExternalApi();
        DataLoadingService.DataLoadResult result = future.get();

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(0, result.getCount());
        assertEquals("External API is not accessible", result.getMessage());

        verify(externalRecipeService).isApiAccessible();
        verify(externalRecipeService, never()).fetchAllRecipes();
        verify(recipeRepository, never()).saveAll(anyList());
    }

    @Test
    void loadRecipesFromExternalApi_WhenNoRecipesReceived_ReturnsWarning() throws Exception {
        when(externalRecipeService.isApiAccessible()).thenReturn(true);
        when(externalRecipeService.fetchAllRecipes()).thenReturn(Collections.emptyList());

        CompletableFuture<DataLoadingService.DataLoadResult> future = dataLoadingService.loadRecipesFromExternalApi();
        DataLoadingService.DataLoadResult result = future.get();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getCount());
        assertEquals("No recipes received from external API", result.getMessage());

        verify(externalRecipeService).isApiAccessible();
        verify(externalRecipeService).fetchAllRecipes();
        verify(recipeRepository, never()).saveAll(anyList());
    }

    @Test
    void loadRecipesFromExternalApi_WhenExternalServiceThrowsException_HandlesError() throws Exception {
        when(externalRecipeService.isApiAccessible()).thenReturn(true);
        when(externalRecipeService.fetchAllRecipes()).thenThrow(new ExternalApiException("API Error"));

        CompletableFuture<DataLoadingService.DataLoadResult> future = dataLoadingService.loadRecipesFromExternalApi();
        DataLoadingService.DataLoadResult result = future.get();

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(0, result.getCount());
        assertTrue(result.getMessage().contains("External API error"));

        verify(externalRecipeService).isApiAccessible();
        verify(externalRecipeService).fetchAllRecipes();
        verify(recipeRepository, never()).saveAll(anyList());
    }

    @Test
    void loadRecipesFromExternalApi_WithExistingData_ClearsAndLoads() throws Exception {
        when(externalRecipeService.isApiAccessible()).thenReturn(true);
        when(externalRecipeService.fetchAllRecipes()).thenReturn(testRecipes);
        when(recipeRepository.count()).thenReturn(5L);
        when(recipeRepository.saveAll(anyList())).thenReturn(testRecipes);

        CompletableFuture<DataLoadingService.DataLoadResult> future = dataLoadingService.loadRecipesFromExternalApi();
        DataLoadingService.DataLoadResult result = future.get();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getCount());

        verify(externalRecipeService).isApiAccessible();
        verify(externalRecipeService).fetchAllRecipes();
        verify(recipeRepository).count();
        verify(recipeRepository).deleteAll();
        verify(recipeRepository).saveAll(testRecipes);
    }

    @Test
    void getLoadingStatus_WhenNeverLoaded_ReturnsCorrectStatus() {
        when(recipeRepository.count()).thenReturn(0L);

        DataLoadingService.DataLoadStatus status = dataLoadingService.getLoadingStatus();

        assertNotNull(status);
        assertFalse(status.isLoading());
        assertEquals(0L, status.getTotalRecipesInDatabase());
        assertEquals("Never loaded", status.getLastLoadStatus());
        assertNull(status.getLastLoadTime());
        assertEquals(0, status.getLastLoadCount());
    }

    @Test
    void getLoadingStatus_WithExistingData_ReturnsCorrectStatus() {
        when(recipeRepository.count()).thenReturn(25L);

        DataLoadingService.DataLoadStatus status = dataLoadingService.getLoadingStatus();

        assertNotNull(status);
        assertFalse(status.isLoading());
        assertEquals(25L, status.getTotalRecipesInDatabase());
    }

    @Test
    void isLoadingInProgress_InitiallyFalse() {
        assertFalse(dataLoadingService.isLoadingInProgress());
    }

    @Test
    void getTotalRecipeCount_ReturnsRepositoryCount() {
        when(recipeRepository.count()).thenReturn(42L);

        long count = dataLoadingService.getTotalRecipeCount();

        assertEquals(42L, count);
        verify(recipeRepository).count();
    }

}