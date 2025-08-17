package com.publicis.recipes.service;

import com.publicis.recipes.dto.RecipeDto;
import com.publicis.recipes.dto.RecipeMapper;
import com.publicis.recipes.entity.Recipe;
import com.publicis.recipes.repository.RecipeRepository;
import com.publicis.recipes.repository.RecipeSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Recipe business logic operations.
 * 
 * This service provides comprehensive recipe management functionality including:
 * - Full-text search with Hibernate Search
 * - CRUD operations with validation
 * - Advanced filtering and sorting
 * - Statistics and analytics
 * - Data transformation between entities and DTOs
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    // private final RecipeSearchRepository searchRepository; // Temporarily disabled
    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository,
                        RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
    }

    /**
     * Searches recipes using full-text search or database queries.
     * 
     * @param query the search query (optional)
     * @param pageable pagination information
     * @return page of recipe DTOs matching the search criteria
     */
    public Page<RecipeDto> searchRecipes(String query, Pageable pageable) {
        logger.debug("Searching recipes with query: '{}', page: {}, size: {}", 
                    query, pageable.getPageNumber(), pageable.getPageSize());

        Page<Recipe> recipePage;

        if (StringUtils.hasText(query)) {
            // Use database search (Hibernate Search temporarily disabled)
            logger.debug("Using database search for query: '{}'", query);
            recipePage = recipeRepository.findByNameOrCuisineContainingIgnoreCase(query.trim(), pageable);
        } else {
            // Return all recipes when no query is provided
            logger.debug("No search query provided, returning all recipes");
            recipePage = recipeRepository.findAll(pageable);
        }

        logger.debug("Search returned {} recipes out of {} total", 
                    recipePage.getNumberOfElements(), recipePage.getTotalElements());

        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Finds a recipe by its ID.
     * 
     * @param id the recipe ID
     * @return optional recipe DTO
     */
    public Optional<RecipeDto> findById(Long id) {
        logger.debug("Finding recipe by ID: {}", id);
        
        if (id == null) {
            logger.warn("Recipe ID is null");
            return Optional.empty();
        }

        Optional<Recipe> recipe = recipeRepository.findById(id);
        
        if (recipe.isPresent()) {
            logger.debug("Found recipe: {}", recipe.get().getName());
        } else {
            logger.debug("Recipe not found with ID: {}", id);
        }

        return recipe.map(recipeMapper::toDto);
    }

    /**
     * Gets all recipes with pagination and sorting.
     * 
     * @param pageable pagination and sorting information
     * @return page of recipe DTOs
     */
    public Page<RecipeDto> getAllRecipes(Pageable pageable) {
        logger.debug("Getting all recipes with pagination: page {}, size {}", 
                    pageable.getPageNumber(), pageable.getPageSize());

        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        
        logger.debug("Retrieved {} recipes out of {} total", 
                    recipePage.getNumberOfElements(), recipePage.getTotalElements());

        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Finds recipes by cuisine.
     * 
     * @param cuisine the cuisine type
     * @param pageable pagination information
     * @return page of recipe DTOs matching the cuisine
     */
    public Page<RecipeDto> findByCuisine(String cuisine, Pageable pageable) {
        logger.debug("Finding recipes by cuisine: '{}'", cuisine);

        if (!StringUtils.hasText(cuisine)) {
            return Page.empty(pageable);
        }

        // Use database search (Hibernate Search temporarily disabled)
        Page<Recipe> recipePage = recipeRepository.findByCuisineContainingIgnoreCase(cuisine.trim(), pageable);

        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Finds recipes by difficulty level.
     * 
     * @param difficulty the difficulty level (Easy, Medium, Hard)
     * @param pageable pagination information
     * @return page of recipe DTOs matching the difficulty
     */
    public Page<RecipeDto> findByDifficulty(String difficulty, Pageable pageable) {
        logger.debug("Finding recipes by difficulty: '{}'", difficulty);

        if (!StringUtils.hasText(difficulty)) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipePage = recipeRepository.findByDifficulty(difficulty.trim(), pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Finds recipes with cook time less than or equal to specified minutes.
     * 
     * @param maxCookTime maximum cook time in minutes
     * @param pageable pagination information
     * @return page of recipe DTOs within the cook time limit
     */
    public Page<RecipeDto> findByMaxCookTime(Integer maxCookTime, Pageable pageable) {
        logger.debug("Finding recipes with max cook time: {} minutes", maxCookTime);

        if (maxCookTime == null || maxCookTime < 0) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipePage = recipeRepository.findByCookTimeMinutesLessThanEqual(maxCookTime, pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Finds recipes with minimum rating.
     * 
     * @param minRating minimum rating (0.0 to 5.0)
     * @param pageable pagination information
     * @return page of recipe DTOs with at least the specified rating
     */
    public Page<RecipeDto> findByMinRating(Double minRating, Pageable pageable) {
        logger.debug("Finding recipes with min rating: {}", minRating);

        if (minRating == null || minRating < 0.0 || minRating > 5.0) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipePage = recipeRepository.findByRatingGreaterThanEqual(minRating, pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Finds recipes containing any of the specified tags.
     * 
     * @param tags list of tags to search for
     * @param pageable pagination information
     * @return page of recipe DTOs containing any of the specified tags
     */
    public Page<RecipeDto> findByTags(List<String> tags, Pageable pageable) {
        logger.debug("Finding recipes by tags: {}", tags);

        if (tags == null || tags.isEmpty()) {
            return Page.empty(pageable);
        }

        List<String> cleanTags = tags.stream()
            .filter(StringUtils::hasText)
            .map(String::trim)
            .collect(Collectors.toList());

        if (cleanTags.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipePage = recipeRepository.findByTagsIn(cleanTags, pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Gets top-rated recipes.
     * 
     * @param pageable pagination information
     * @return page of top-rated recipe DTOs
     */
    public Page<RecipeDto> getTopRatedRecipes(Pageable pageable) {
        logger.debug("Getting top-rated recipes");

        Page<Recipe> recipePage = recipeRepository.findTopRatedRecipes(pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Gets recently added recipes.
     * 
     * @param pageable pagination information
     * @return page of recently added recipe DTOs
     */
    public Page<RecipeDto> getRecentRecipes(Pageable pageable) {
        logger.debug("Getting recent recipes");

        Page<Recipe> recipePage = recipeRepository.findRecentRecipes(pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    /**
     * Gets search suggestions based on partial input.
     * 
     * @param partialQuery partial search query
     * @param maxSuggestions maximum number of suggestions
     * @return list of search suggestions
     */
    public List<String> getSearchSuggestions(String partialQuery, int maxSuggestions) {
        logger.debug("Getting search suggestions for: '{}'", partialQuery);

        if (!StringUtils.hasText(partialQuery) || partialQuery.trim().length() < 2) {
            return List.of();
        }

        // Use database-based suggestions (Hibernate Search temporarily disabled)
        Pageable pageable = PageRequest.of(0, maxSuggestions, Sort.by("name"));
        Page<Recipe> recipes = recipeRepository.findByNameContainingIgnoreCase(partialQuery.trim(), pageable);
        return recipes.getContent().stream()
            .map(Recipe::getName)
            .distinct()
            .limit(maxSuggestions)
            .collect(Collectors.toList());
    }

    /**
     * Gets recipe statistics.
     * 
     * @return map containing various recipe statistics
     */
    public Map<String, Object> getRecipeStatistics() {
        logger.debug("Getting recipe statistics");

        long totalRecipes = recipeRepository.count();
        List<Object[]> cuisineCounts = recipeRepository.countRecipesByCuisine();
        List<Object[]> difficultyCounts = recipeRepository.countRecipesByDifficulty();

        Map<String, Long> cuisineStats = cuisineCounts.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));

        Map<String, Long> difficultyStats = difficultyCounts.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));

        return Map.of(
            "totalRecipes", totalRecipes,
            "cuisineDistribution", cuisineStats,
            "difficultyDistribution", difficultyStats,
            "searchIndexReady", false // Hibernate Search temporarily disabled
        );
    }

    /**
     * Gets the total count of recipes in the database.
     * 
     * @return total recipe count
     */
    public long getTotalRecipeCount() {
        long count = recipeRepository.count();
        logger.debug("Total recipe count: {}", count);
        return count;
    }

    /**
     * Checks if recipes exist in the database.
     * 
     * @return true if recipes exist, false otherwise
     */
    public boolean hasRecipes() {
        boolean exists = recipeRepository.count() > 0;
        logger.debug("Recipes exist in database: {}", exists);
        return exists;
    }
}