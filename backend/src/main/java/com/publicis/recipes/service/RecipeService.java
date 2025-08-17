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

@Service
@Transactional(readOnly = true)
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository,
                        RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
    }

    public Page<RecipeDto> searchRecipes(String query, Pageable pageable) {
        logger.debug("Searching recipes with query: '{}', page: {}, size: {}",
                    query, pageable.getPageNumber(), pageable.getPageSize());

        Page<Recipe> recipePage;

        if (StringUtils.hasText(query)) {
            logger.debug("Using database search for query: '{}'", query);
            recipePage = recipeRepository.findByNameOrCuisineContainingIgnoreCase(query.trim(), pageable);
        } else {
            logger.debug("No search query provided, returning all recipes");
            recipePage = recipeRepository.findAll(pageable);
        }

        logger.debug("Search returned {} recipes out of {} total", 
                    recipePage.getNumberOfElements(), recipePage.getTotalElements());

        return recipePage.map(recipeMapper::toDto);
    }

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

    public Page<RecipeDto> getAllRecipes(Pageable pageable) {
        logger.debug("Getting all recipes with pagination: page {}, size {}", 
                    pageable.getPageNumber(), pageable.getPageSize());

        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        
        logger.debug("Retrieved {} recipes out of {} total", 
                    recipePage.getNumberOfElements(), recipePage.getTotalElements());

        return recipePage.map(recipeMapper::toDto);
    }

    public Page<RecipeDto> findByCuisine(String cuisine, Pageable pageable) {
        logger.debug("Finding recipes by cuisine: '{}'", cuisine);

        if (!StringUtils.hasText(cuisine)) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipePage = recipeRepository.findByCuisineContainingIgnoreCase(cuisine.trim(), pageable);

        return recipePage.map(recipeMapper::toDto);
    }

    public Page<RecipeDto> findByDifficulty(String difficulty, Pageable pageable) {
        logger.debug("Finding recipes by difficulty: '{}'", difficulty);

        if (!StringUtils.hasText(difficulty)) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipePage = recipeRepository.findByDifficulty(difficulty.trim(), pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    public Page<RecipeDto> findByMaxCookTime(Integer maxCookTime, Pageable pageable) {
        logger.debug("Finding recipes with max cook time: {} minutes", maxCookTime);

        if (maxCookTime == null || maxCookTime < 0) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipePage = recipeRepository.findByCookTimeMinutesLessThanEqual(maxCookTime, pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    public Page<RecipeDto> findByMinRating(Double minRating, Pageable pageable) {
        logger.debug("Finding recipes with min rating: {}", minRating);

        if (minRating == null || minRating < 0.0 || minRating > 5.0) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipePage = recipeRepository.findByRatingGreaterThanEqual(minRating, pageable);
        return recipePage.map(recipeMapper::toDto);
    }

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

    public Page<RecipeDto> getTopRatedRecipes(Pageable pageable) {
        logger.debug("Getting top-rated recipes");

        Page<Recipe> recipePage = recipeRepository.findTopRatedRecipes(pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    public Page<RecipeDto> getRecentRecipes(Pageable pageable) {
        logger.debug("Getting recent recipes");

        Page<Recipe> recipePage = recipeRepository.findRecentRecipes(pageable);
        return recipePage.map(recipeMapper::toDto);
    }

    public List<String> getSearchSuggestions(String partialQuery, int maxSuggestions) {
        logger.debug("Getting search suggestions for: '{}'", partialQuery);

        if (!StringUtils.hasText(partialQuery) || partialQuery.trim().length() < 2) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, maxSuggestions, Sort.by("name"));
        Page<Recipe> recipes = recipeRepository.findByNameContainingIgnoreCase(partialQuery.trim(), pageable);
        return recipes.getContent().stream()
            .map(Recipe::getName)
            .distinct()
            .limit(maxSuggestions)
            .collect(Collectors.toList());
    }

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
            "searchIndexReady", false
        );
    }

    public long getTotalRecipeCount() {
        long count = recipeRepository.count();
        logger.debug("Total recipe count: {}", count);
        return count;
    }

    public boolean hasRecipes() {
        boolean exists = recipeRepository.count() > 0;
        logger.debug("Recipes exist in database: {}", exists);
        return exists;
    }
}