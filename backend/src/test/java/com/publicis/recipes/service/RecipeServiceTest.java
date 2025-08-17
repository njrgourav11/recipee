package com.publicis.recipes.service;

import com.publicis.recipes.dto.RecipeDto;
import com.publicis.recipes.dto.RecipeMapper;
import com.publicis.recipes.entity.Recipe;
import com.publicis.recipes.repository.RecipeRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe testRecipe;
    private RecipeDto testRecipeDto;
    private List<Recipe> testRecipes;

    @BeforeEach
    void setUp() {
        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setName("Test Recipe");
        testRecipe.setCuisine("Italian");
        testRecipe.setDifficulty("Medium");
        testRecipe.setRating(4.5);
        testRecipe.setIngredients(Arrays.asList("ingredient1", "ingredient2"));
        testRecipe.setInstructions(Arrays.asList("step1", "step2"));

        testRecipeDto = new RecipeDto();
        testRecipeDto.setId(1L);
        testRecipeDto.setName("Test Recipe");
        testRecipeDto.setCuisine("Italian");
        testRecipeDto.setDifficulty("Medium");
        testRecipeDto.setRating(4.5);
        testRecipeDto.setIngredients(Arrays.asList("ingredient1", "ingredient2"));
        testRecipeDto.setInstructions(Arrays.asList("step1", "step2"));

        testRecipes = Arrays.asList(testRecipe);
    }

    @Test
    void searchRecipes_WithQuery_ReturnsSearchResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findByNameOrCuisineContainingIgnoreCase("pasta", pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.searchRecipes("pasta", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findByNameOrCuisineContainingIgnoreCase("pasta", pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void searchRecipes_WithoutQuery_ReturnsAllRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findAll(pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.searchRecipes(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findAll(pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void searchRecipes_WithEmptyQuery_ReturnsAllRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findAll(pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.searchRecipes("", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findAll(pageable);
    }

    @Test
    void findById_ExistingId_ReturnsRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Optional<RecipeDto> result = recipeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testRecipeDto, result.get());
        verify(recipeRepository).findById(1L);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<RecipeDto> result = recipeService.findById(999L);

        assertFalse(result.isPresent());
        verify(recipeRepository).findById(999L);
        verify(recipeMapper, never()).toDto(any());
    }

    @Test
    void findById_NullId_ReturnsEmpty() {
        Optional<RecipeDto> result = recipeService.findById(null);

        assertFalse(result.isPresent());
        verify(recipeRepository, never()).findById(any());
        verify(recipeMapper, never()).toDto(any());
    }

    @Test
    void getAllRecipes_ReturnsAllRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findAll(pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.getAllRecipes(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findAll(pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void findByCuisine_ExistingCuisine_ReturnsRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findByCuisineContainingIgnoreCase("Italian", pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.findByCuisine("Italian", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findByCuisineContainingIgnoreCase("Italian", pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void findByCuisine_EmptyCuisine_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<RecipeDto> result = recipeService.findByCuisine("", pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(recipeRepository, never()).findByCuisineContainingIgnoreCase(any(), any());
    }

    @Test
    void findByDifficulty_ExistingDifficulty_ReturnsRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findByDifficulty("Medium", pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.findByDifficulty("Medium", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findByDifficulty("Medium", pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void findByMaxCookTime_ValidTime_ReturnsRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findByCookTimeMinutesLessThanEqual(30, pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.findByMaxCookTime(30, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findByCookTimeMinutesLessThanEqual(30, pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void findByMaxCookTime_NegativeTime_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<RecipeDto> result = recipeService.findByMaxCookTime(-1, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(recipeRepository, never()).findByCookTimeMinutesLessThanEqual(any(), any());
    }

    @Test
    void findByMinRating_ValidRating_ReturnsRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findByRatingGreaterThanEqual(4.0, pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.findByMinRating(4.0, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findByRatingGreaterThanEqual(4.0, pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void findByMinRating_InvalidRating_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<RecipeDto> result = recipeService.findByMinRating(6.0, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(recipeRepository, never()).findByRatingGreaterThanEqual(any(), any());
    }

    @Test
    void findByTags_ValidTags_ReturnsRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        List<String> tags = Arrays.asList("Italian", "Pasta");
        
        when(recipeRepository.findByTagsIn(tags, pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.findByTags(tags, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findByTagsIn(tags, pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void findByTags_EmptyTags_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<RecipeDto> result = recipeService.findByTags(Collections.emptyList(), pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(recipeRepository, never()).findByTagsIn(any(), any());
    }

    @Test
    void getTopRatedRecipes_ReturnsTopRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findTopRatedRecipes(pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.getTopRatedRecipes(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findTopRatedRecipes(pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void getRecentRecipes_ReturnsRecentRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findRecentRecipes(pageable)).thenReturn(recipePage);
        when(recipeMapper.toDto(testRecipe)).thenReturn(testRecipeDto);

        Page<RecipeDto> result = recipeService.getRecentRecipes(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRecipeDto, result.getContent().get(0));
        verify(recipeRepository).findRecentRecipes(pageable);
        verify(recipeMapper).toDto(testRecipe);
    }

    @Test
    void getSearchSuggestions_ValidQuery_ReturnsSuggestions() {
        Pageable pageable = PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("name"));
        Page<Recipe> recipePage = new PageImpl<>(testRecipes, pageable, 1);
        
        when(recipeRepository.findByNameContainingIgnoreCase("test", pageable)).thenReturn(recipePage);

        List<String> result = recipeService.getSearchSuggestions("test", 5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Recipe", result.get(0));
        verify(recipeRepository).findByNameContainingIgnoreCase("test", pageable);
    }

    @Test
    void getSearchSuggestions_ShortQuery_ReturnsEmpty() {
        List<String> result = recipeService.getSearchSuggestions("a", 5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void getRecipeStatistics_ReturnsCorrectStatistics() {
        when(recipeRepository.count()).thenReturn(100L);
        when(recipeRepository.countRecipesByCuisine()).thenReturn(Arrays.asList(
                new Object[]{"Italian", 25L},
                new Object[]{"Mexican", 20L}
        ));
        when(recipeRepository.countRecipesByDifficulty()).thenReturn(Arrays.asList(
                new Object[]{"Easy", 40L},
                new Object[]{"Medium", 35L}
        ));

        Map<String, Object> result = recipeService.getRecipeStatistics();

        assertNotNull(result);
        assertEquals(100L, result.get("totalRecipes"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> cuisineStats = (Map<String, Long>) result.get("cuisineDistribution");
        assertEquals(25L, cuisineStats.get("Italian"));
        assertEquals(20L, cuisineStats.get("Mexican"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> difficultyStats = (Map<String, Long>) result.get("difficultyDistribution");
        assertEquals(40L, difficultyStats.get("Easy"));
        assertEquals(35L, difficultyStats.get("Medium"));
        
        assertEquals(false, result.get("searchIndexReady"));
    }

    @Test
    void getTotalRecipeCount_ReturnsCount() {
        when(recipeRepository.count()).thenReturn(42L);

        long result = recipeService.getTotalRecipeCount();

        assertEquals(42L, result);
        verify(recipeRepository).count();
    }

    @Test
    void hasRecipes_WithRecipes_ReturnsTrue() {
        when(recipeRepository.count()).thenReturn(5L);

        boolean result = recipeService.hasRecipes();

        assertTrue(result);
        verify(recipeRepository).count();
    }

    @Test
    void hasRecipes_WithoutRecipes_ReturnsFalse() {
        when(recipeRepository.count()).thenReturn(0L);

        boolean result = recipeService.hasRecipes();

        assertFalse(result);
        verify(recipeRepository).count();
    }
}