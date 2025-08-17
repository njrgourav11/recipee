package com.publicis.recipes.controller;

import com.publicis.recipes.dto.RecipeDto;
import com.publicis.recipes.service.DataLoadingService;
import com.publicis.recipes.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @Mock
    private DataLoadingService dataLoadingService;

    @InjectMocks
    private RecipeController recipeController;

    private RecipeDto testRecipeDto;
    private List<RecipeDto> testRecipeDtos;

    @BeforeEach
    void setUp() {
        testRecipeDto = new RecipeDto();
        testRecipeDto.setId(1L);
        testRecipeDto.setName("Test Recipe");
        testRecipeDto.setCuisine("Italian");
        testRecipeDto.setDifficulty("Medium");
        testRecipeDto.setRating(4.5);
        testRecipeDto.setIngredients(Arrays.asList("ingredient1", "ingredient2"));
        testRecipeDto.setInstructions(Arrays.asList("step1", "step2"));

        testRecipeDtos = Arrays.asList(testRecipeDto);
    }

    @Test
    void searchRecipes_WithQuery_ReturnsResults() {
        Pageable pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("name"));
        Page<RecipeDto> recipePage = new PageImpl<>(testRecipeDtos, pageable, 1);
        
        when(recipeService.searchRecipes(eq("pasta"), any(Pageable.class))).thenReturn(recipePage);

        ResponseEntity<Page<RecipeDto>> response = recipeController.searchRecipes("pasta", 0, 20, "name", "asc");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(recipeService).searchRecipes(eq("pasta"), any(Pageable.class));
    }

    @Test
    void getRecipeById_ExistingId_ReturnsRecipe() {
        when(recipeService.findById(1L)).thenReturn(Optional.of(testRecipeDto));

        ResponseEntity<RecipeDto> response = recipeController.getRecipeById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Recipe", response.getBody().getName());
        verify(recipeService).findById(1L);
    }

    @Test
    void getRecipeById_NonExistingId_ReturnsNotFound() {
        when(recipeService.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<RecipeDto> response = recipeController.getRecipeById(999L);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        verify(recipeService).findById(999L);
    }

    @Test
    void getRecipeById_InvalidId_ReturnsBadRequest() {
        ResponseEntity<RecipeDto> response = recipeController.getRecipeById(0L);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        verify(recipeService, never()).findById(any());
    }

    @Test
    void loadRecipes_WhenNotInProgress_InitiatesLoading() {
        when(dataLoadingService.isLoadingInProgress()).thenReturn(false);
        when(dataLoadingService.loadRecipesFromExternalApi())
                .thenReturn(CompletableFuture.completedFuture(
                        new DataLoadingService.DataLoadResult(10, "Success", true)));

        ResponseEntity<Map<String, Object>> response = recipeController.loadRecipes();

        assertNotNull(response);
        assertEquals(202, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Recipe loading initiated", response.getBody().get("message"));
        verify(dataLoadingService).isLoadingInProgress();
        verify(dataLoadingService).loadRecipesFromExternalApi();
    }

    @Test
    void loadRecipes_WhenInProgress_ReturnsConflict() {
        when(dataLoadingService.isLoadingInProgress()).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = recipeController.loadRecipes();

        assertNotNull(response);
        assertEquals(409, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").toString().contains("already in progress"));
        verify(dataLoadingService).isLoadingInProgress();
        verify(dataLoadingService, never()).loadRecipesFromExternalApi();
    }

    @Test
    void getSearchSuggestions_ValidQuery_ReturnsSuggestions() {
        List<String> suggestions = Arrays.asList("Pizza Margherita", "Pizza Pepperoni");
        when(recipeService.getSearchSuggestions("piz", 10)).thenReturn(suggestions);

        ResponseEntity<List<String>> response = recipeController.getSearchSuggestions("piz", 10);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Pizza Margherita", response.getBody().get(0));
        verify(recipeService).getSearchSuggestions("piz", 10);
    }

    @Test
    void getSearchSuggestions_ShortQuery_ReturnsBadRequest() {
        ResponseEntity<List<String>> response = recipeController.getSearchSuggestions("a", 10);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        verify(recipeService, never()).getSearchSuggestions(any(), anyInt());
    }

    @Test
    void getRecipeStatistics_ReturnsStatistics() {
        Map<String, Object> statistics = Map.of(
                "totalRecipes", 100L,
                "cuisineDistribution", Map.of("Italian", 25L, "Mexican", 20L),
                "difficultyDistribution", Map.of("Easy", 40L, "Medium", 35L),
                "searchIndexReady", false
        );
        when(recipeService.getRecipeStatistics()).thenReturn(statistics);

        ResponseEntity<Map<String, Object>> response = recipeController.getRecipeStatistics();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().get("totalRecipes"));
        verify(recipeService).getRecipeStatistics();
    }

    @Test
    void getRecipesByCuisine_ValidCuisine_ReturnsRecipes() {
        Pageable pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("name"));
        Page<RecipeDto> recipePage = new PageImpl<>(testRecipeDtos, pageable, 1);
        
        when(recipeService.findByCuisine(eq("Italian"), any(Pageable.class))).thenReturn(recipePage);

        ResponseEntity<Page<RecipeDto>> response = recipeController.getRecipesByCuisine("Italian", 0, 20);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(recipeService).findByCuisine(eq("Italian"), any(Pageable.class));
    }

    @Test
    void getRecipesByCuisine_EmptyCuisine_ReturnsBadRequest() {
        ResponseEntity<Page<RecipeDto>> response = recipeController.getRecipesByCuisine(" ", 0, 20);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        verify(recipeService, never()).findByCuisine(any(), any());
    }

    @Test
    void getTopRatedRecipes_ReturnsTopRecipes() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<RecipeDto> recipePage = new PageImpl<>(testRecipeDtos, pageable, 1);
        
        when(recipeService.getTopRatedRecipes(any(Pageable.class))).thenReturn(recipePage);

        ResponseEntity<Page<RecipeDto>> response = recipeController.getTopRatedRecipes(0, 20);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(recipeService).getTopRatedRecipes(any(Pageable.class));
    }
}