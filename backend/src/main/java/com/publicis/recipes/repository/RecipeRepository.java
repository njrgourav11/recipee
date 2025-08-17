package com.publicis.recipes.repository;

import com.publicis.recipes.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository interface for Recipe entity.
 * 
 * This repository provides standard CRUD operations and custom query methods
 * for Recipe entities. It works in conjunction with RecipeSearchRepository
 * for full-text search capabilities.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Finds recipes by name containing the given text (case-insensitive).
     * 
     * @param name the name to search for
     * @param pageable pagination information
     * @return page of recipes matching the name
     */
    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Finds recipes by cuisine containing the given text (case-insensitive).
     * 
     * @param cuisine the cuisine to search for
     * @param pageable pagination information
     * @return page of recipes matching the cuisine
     */
    Page<Recipe> findByCuisineContainingIgnoreCase(String cuisine, Pageable pageable);

    /**
     * Finds recipes by difficulty level.
     * 
     * @param difficulty the difficulty level
     * @param pageable pagination information
     * @return page of recipes with the specified difficulty
     */
    Page<Recipe> findByDifficulty(String difficulty, Pageable pageable);

    /**
     * Finds recipes by name or cuisine containing the given query (case-insensitive).
     * 
     * @param query the search query
     * @param pageable pagination information
     * @return page of recipes matching the query
     */
    @Query("SELECT r FROM Recipe r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Recipe> findByNameOrCuisineContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    /**
     * Finds recipes with cook time less than or equal to the specified minutes.
     * 
     * @param maxCookTime maximum cook time in minutes
     * @param pageable pagination information
     * @return page of recipes within the cook time limit
     */
    Page<Recipe> findByCookTimeMinutesLessThanEqual(Integer maxCookTime, Pageable pageable);

    /**
     * Finds recipes with servings greater than or equal to the specified number.
     * 
     * @param minServings minimum number of servings
     * @param pageable pagination information
     * @return page of recipes with at least the specified servings
     */
    Page<Recipe> findByServingsGreaterThanEqual(Integer minServings, Pageable pageable);

    /**
     * Finds recipes with rating greater than or equal to the specified value.
     * 
     * @param minRating minimum rating
     * @param pageable pagination information
     * @return page of recipes with at least the specified rating
     */
    Page<Recipe> findByRatingGreaterThanEqual(Double minRating, Pageable pageable);

    /**
     * Finds recipes containing any of the specified tags.
     * 
     * @param tags list of tags to search for
     * @param pageable pagination information
     * @return page of recipes containing any of the specified tags
     */
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.tags t WHERE t IN :tags")
    Page<Recipe> findByTagsIn(@Param("tags") List<String> tags, Pageable pageable);

    /**
     * Finds recipes by multiple criteria.
     * 
     * @param name name filter (optional)
     * @param cuisine cuisine filter (optional)
     * @param difficulty difficulty filter (optional)
     * @param maxCookTime maximum cook time filter (optional)
     * @param minRating minimum rating filter (optional)
     * @param pageable pagination information
     * @return page of recipes matching the criteria
     */
    @Query("SELECT r FROM Recipe r WHERE " +
           "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:cuisine IS NULL OR LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :cuisine, '%'))) AND " +
           "(:difficulty IS NULL OR r.difficulty = :difficulty) AND " +
           "(:maxCookTime IS NULL OR r.cookTimeMinutes <= :maxCookTime) AND " +
           "(:minRating IS NULL OR r.rating >= :minRating)")
    Page<Recipe> findByMultipleCriteria(
        @Param("name") String name,
        @Param("cuisine") String cuisine,
        @Param("difficulty") String difficulty,
        @Param("maxCookTime") Integer maxCookTime,
        @Param("minRating") Double minRating,
        Pageable pageable
    );

    /**
     * Finds the top recipes by rating.
     * 
     * @param pageable pagination information
     * @return page of top-rated recipes
     */
    @Query("SELECT r FROM Recipe r WHERE r.rating IS NOT NULL ORDER BY r.rating DESC, r.reviewCount DESC")
    Page<Recipe> findTopRatedRecipes(Pageable pageable);

    /**
     * Finds recently added recipes.
     * 
     * @param pageable pagination information
     * @return page of recently added recipes
     */
    @Query("SELECT r FROM Recipe r ORDER BY r.createdAt DESC")
    Page<Recipe> findRecentRecipes(Pageable pageable);

    /**
     * Counts recipes by cuisine.
     * 
     * @return list of cuisine counts
     */
    @Query("SELECT r.cuisine, COUNT(r) FROM Recipe r GROUP BY r.cuisine ORDER BY COUNT(r) DESC")
    List<Object[]> countRecipesByCuisine();

    /**
     * Counts recipes by difficulty.
     * 
     * @return list of difficulty counts
     */
    @Query("SELECT r.difficulty, COUNT(r) FROM Recipe r GROUP BY r.difficulty ORDER BY COUNT(r) DESC")
    List<Object[]> countRecipesByDifficulty();

    /**
     * Finds recipes with missing images.
     * 
     * @param pageable pagination information
     * @return page of recipes without images
     */
    Page<Recipe> findByImageIsNull(Pageable pageable);

    /**
     * Finds recipes with images.
     * 
     * @param pageable pagination information
     * @return page of recipes with images
     */
    Page<Recipe> findByImageIsNotNull(Pageable pageable);

    /**
     * Checks if a recipe exists with the given name and cuisine.
     * 
     * @param name recipe name
     * @param cuisine recipe cuisine
     * @return true if recipe exists, false otherwise
     */
    boolean existsByNameAndCuisine(String name, String cuisine);

    /**
     * Finds a recipe by name and cuisine.
     * 
     * @param name recipe name
     * @param cuisine recipe cuisine
     * @return optional recipe
     */
    Optional<Recipe> findByNameAndCuisine(String name, String cuisine);
}