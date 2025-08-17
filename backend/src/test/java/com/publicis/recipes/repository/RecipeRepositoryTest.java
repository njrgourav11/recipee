package com.publicis.recipes.repository;

import com.publicis.recipes.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RecipeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecipeRepository recipeRepository;

    private Recipe italianRecipe;
    private Recipe mexicanRecipe;
    private Recipe chineseRecipe;

    @BeforeEach
    void setUp() {
        italianRecipe = new Recipe();
        italianRecipe.setName("Spaghetti Carbonara");
        italianRecipe.setCuisine("Italian");
        italianRecipe.setDifficulty("Medium");
        italianRecipe.setRating(4.8);
        italianRecipe.setIngredients(Arrays.asList("spaghetti", "eggs", "bacon", "parmesan"));
        italianRecipe.setInstructions(Arrays.asList("Boil pasta", "Cook bacon", "Mix with eggs"));
        italianRecipe.setImage("https://example.com/carbonara.jpg");
        italianRecipe.setCreatedAt(LocalDateTime.now());
        italianRecipe.setUpdatedAt(LocalDateTime.now());

        mexicanRecipe = new Recipe();
        mexicanRecipe.setName("Chicken Tacos");
        mexicanRecipe.setCuisine("Mexican");
        mexicanRecipe.setDifficulty("Easy");
        mexicanRecipe.setRating(4.2);
        mexicanRecipe.setIngredients(Arrays.asList("chicken", "tortillas", "salsa", "cheese"));
        mexicanRecipe.setInstructions(Arrays.asList("Cook chicken", "Warm tortillas", "Assemble tacos"));
        mexicanRecipe.setImage("https://example.com/tacos.jpg");
        mexicanRecipe.setCreatedAt(LocalDateTime.now());
        mexicanRecipe.setUpdatedAt(LocalDateTime.now());

        chineseRecipe = new Recipe();
        chineseRecipe.setName("Fried Rice");
        chineseRecipe.setCuisine("Chinese");
        chineseRecipe.setDifficulty("Easy");
        chineseRecipe.setRating(3.9);
        chineseRecipe.setIngredients(Arrays.asList("rice", "eggs", "vegetables", "soy sauce"));
        chineseRecipe.setInstructions(Arrays.asList("Cook rice", "Scramble eggs", "Stir fry together"));
        chineseRecipe.setImage("https://example.com/friedrice.jpg");
        chineseRecipe.setCreatedAt(LocalDateTime.now());
        chineseRecipe.setUpdatedAt(LocalDateTime.now());

        entityManager.persistAndFlush(italianRecipe);
        entityManager.persistAndFlush(mexicanRecipe);
        entityManager.persistAndFlush(chineseRecipe);
    }

    @Test
    void findByCuisineContainingIgnoreCase_WithExistingCuisine_ReturnsRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Recipe> result = recipeRepository.findByCuisineContainingIgnoreCase("italian", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Spaghetti Carbonara", result.getContent().get(0).getName());
    }

    @Test
    void findByCuisineContainingIgnoreCase_WithNonExistingCuisine_ReturnsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Recipe> result = recipeRepository.findByCuisineContainingIgnoreCase("French", pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void findByDifficulty_ReturnsMatchingRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Recipe> result = recipeRepository.findByDifficulty("Easy", pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(r -> "Easy".equals(r.getDifficulty())));
    }

    @Test
    void countRecipesByCuisine_ReturnsCorrectCounts() {
        List<Object[]> result = recipeRepository.countRecipesByCuisine();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void countRecipesByDifficulty_ReturnsCorrectCounts() {
        List<Object[]> result = recipeRepository.countRecipesByDifficulty();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void save_NewRecipe_PersistsSuccessfully() {
        Recipe newRecipe = new Recipe();
        newRecipe.setName("New Recipe");
        newRecipe.setCuisine("Test");
        newRecipe.setDifficulty("Medium");
        newRecipe.setRating(4.0);
        newRecipe.setIngredients(Arrays.asList("ingredient1", "ingredient2"));
        newRecipe.setInstructions(Arrays.asList("step1", "step2"));
        newRecipe.setCreatedAt(LocalDateTime.now());
        newRecipe.setUpdatedAt(LocalDateTime.now());

        Recipe saved = recipeRepository.save(newRecipe);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("New Recipe", saved.getName());
        
        Optional<Recipe> found = recipeRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("New Recipe", found.get().getName());
    }

    @Test
    void findById_ExistingId_ReturnsRecipe() {
        Optional<Recipe> result = recipeRepository.findById(italianRecipe.getId());

        assertTrue(result.isPresent());
        assertEquals("Spaghetti Carbonara", result.get().getName());
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        Optional<Recipe> result = recipeRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_WithPagination_ReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 2);
        
        Page<Recipe> result = recipeRepository.findAll(pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void count_ReturnsCorrectCount() {
        long count = recipeRepository.count();

        assertEquals(3L, count);
    }

    @Test
    void delete_ExistingRecipe_RemovesFromDatabase() {
        Long recipeId = italianRecipe.getId();
        
        recipeRepository.delete(italianRecipe);
        entityManager.flush();

        Optional<Recipe> result = recipeRepository.findById(recipeId);
        assertFalse(result.isPresent());
        assertEquals(2L, recipeRepository.count());
    }

}