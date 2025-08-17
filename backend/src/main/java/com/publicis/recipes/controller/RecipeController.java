package com.publicis.recipes.controller;

import com.publicis.recipes.dto.RecipeDto;
import com.publicis.recipes.service.DataLoadingService;
import com.publicis.recipes.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for Recipe operations.
 * 
 * This controller provides RESTful endpoints for:
 * - Searching recipes with full-text search
 * - Retrieving individual recipes by ID
 * - Loading data from external APIs
 * - Getting recipe statistics and suggestions
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/recipes")
@Validated
@Tag(name = "Recipe Management", description = "APIs for recipe search and management")
public class RecipeController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeService recipeService;
    private final DataLoadingService dataLoadingService;

    public RecipeController(RecipeService recipeService, DataLoadingService dataLoadingService) {
        this.recipeService = recipeService;
        this.dataLoadingService = dataLoadingService;
    }

    /**
     * Searches recipes based on query parameters.
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search recipes",
        description = "Search recipes by name and cuisine using full-text search. Requires at least 3 characters for optimal results."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<RecipeDto>> searchRecipes(
            @Parameter(description = "Search query for recipe name and cuisine", example = "pizza")
            @RequestParam(required = false) String q,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sort,
            
            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Search request - query: '{}', page: {}, size: {}, sort: {} {}", 
                   q, page, size, sort, direction);

        try {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            Page<RecipeDto> recipes = recipeService.searchRecipes(q, pageable);

            logger.info("Search completed - found {} recipes out of {} total", 
                       recipes.getNumberOfElements(), recipes.getTotalElements());

            return ResponseEntity.ok(recipes);

        } catch (Exception e) {
            logger.error("Error during recipe search", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets a specific recipe by ID.
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get recipe by ID",
        description = "Retrieve a specific recipe by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recipe found",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = RecipeDto.class))),
        @ApiResponse(responseCode = "404", description = "Recipe not found"),
        @ApiResponse(responseCode = "400", description = "Invalid recipe ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RecipeDto> getRecipeById(
            @Parameter(description = "Recipe ID", example = "1", required = true)
            @PathVariable Long id) {

        logger.info("Get recipe request for ID: {}", id);

        try {
            if (id == null || id <= 0) {
                logger.warn("Invalid recipe ID: {}", id);
                return ResponseEntity.badRequest().build();
            }

            return recipeService.findById(id)
                .map(recipe -> {
                    logger.info("Recipe found: {}", recipe.getName());
                    return ResponseEntity.ok(recipe);
                })
                .orElseGet(() -> {
                    logger.info("Recipe not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });

        } catch (Exception e) {
            logger.error("Error retrieving recipe with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Loads recipes from external API.
     */
    @PostMapping("/load")
    @Operation(
        summary = "Load recipes from external API",
        description = "Asynchronously loads recipe data from external API (dummyjson.com/recipes) into the database"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Data loading initiated"),
        @ApiResponse(responseCode = "409", description = "Loading already in progress"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> loadRecipes() {
        logger.info("Recipe loading request received");

        try {
            if (dataLoadingService.isLoadingInProgress()) {
                logger.warn("Recipe loading already in progress");
                return ResponseEntity.status(409).body(Map.of(
                    "message", "Recipe loading is already in progress",
                    "timestamp", LocalDateTime.now()
                ));
            }

            CompletableFuture<DataLoadingService.DataLoadResult> future = 
                dataLoadingService.loadRecipesFromExternalApi();

            logger.info("Recipe loading initiated asynchronously");

            return ResponseEntity.accepted().body(Map.of(
                "message", "Recipe loading initiated",
                "timestamp", LocalDateTime.now(),
                "status", "in_progress"
            ));

        } catch (Exception e) {
            logger.error("Error initiating recipe loading", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Failed to initiate recipe loading",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    /**
     * Gets the current data loading status.
     */
    @GetMapping("/load/status")
    @Operation(
        summary = "Get data loading status",
        description = "Retrieve the current status of data loading operations"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DataLoadingService.DataLoadStatus> getLoadingStatus() {
        logger.debug("Loading status request received");

        try {
            DataLoadingService.DataLoadStatus status = dataLoadingService.getLoadingStatus();
            return ResponseEntity.ok(status);

        } catch (Exception e) {
            logger.error("Error retrieving loading status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets search suggestions based on partial input.
     */
    @GetMapping("/suggestions")
    @Operation(
        summary = "Get search suggestions",
        description = "Get search suggestions based on partial recipe name input"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid query parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<String>> getSearchSuggestions(
            @Parameter(description = "Partial search query", example = "piz")
            @RequestParam String q,
            
            @Parameter(description = "Maximum number of suggestions", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int limit) {

        logger.debug("Search suggestions request - query: '{}', limit: {}", q, limit);

        try {
            if (q == null || q.trim().length() < 2) {
                logger.warn("Query too short for suggestions: '{}'", q);
                return ResponseEntity.badRequest().build();
            }

            List<String> suggestions = recipeService.getSearchSuggestions(q.trim(), limit);
            
            logger.debug("Returning {} suggestions for query: '{}'", suggestions.size(), q);
            
            return ResponseEntity.ok(suggestions);

        } catch (Exception e) {
            logger.error("Error getting search suggestions for query: '{}'", q, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets recipe statistics.
     */
    @GetMapping("/statistics")
    @Operation(
        summary = "Get recipe statistics",
        description = "Retrieve various statistics about recipes in the database"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getRecipeStatistics() {
        logger.debug("Recipe statistics request received");

        try {
            Map<String, Object> statistics = recipeService.getRecipeStatistics();
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            logger.error("Error retrieving recipe statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets recipes by cuisine.
     */
    @GetMapping("/cuisine/{cuisine}")
    @Operation(
        summary = "Get recipes by cuisine",
        description = "Retrieve recipes filtered by cuisine type"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recipes retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid cuisine parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<RecipeDto>> getRecipesByCuisine(
            @Parameter(description = "Cuisine type", example = "Italian", required = true)
            @PathVariable String cuisine,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        logger.info("Get recipes by cuisine request - cuisine: '{}', page: {}, size: {}", 
                   cuisine, page, size);

        try {
            if (cuisine == null || cuisine.trim().isEmpty()) {
                logger.warn("Empty cuisine parameter");
                return ResponseEntity.badRequest().build();
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
            Page<RecipeDto> recipes = recipeService.findByCuisine(cuisine, pageable);

            logger.info("Found {} recipes for cuisine: '{}'", recipes.getTotalElements(), cuisine);

            return ResponseEntity.ok(recipes);

        } catch (Exception e) {
            logger.error("Error retrieving recipes by cuisine: '{}'", cuisine, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets top-rated recipes.
     */
    @GetMapping("/top-rated")
    @Operation(
        summary = "Get top-rated recipes",
        description = "Retrieve recipes sorted by rating in descending order"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top-rated recipes retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<RecipeDto>> getTopRatedRecipes(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        logger.info("Get top-rated recipes request - page: {}, size: {}", page, size);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<RecipeDto> recipes = recipeService.getTopRatedRecipes(pageable);

            logger.info("Retrieved {} top-rated recipes", recipes.getNumberOfElements());

            return ResponseEntity.ok(recipes);

        } catch (Exception e) {
            logger.error("Error retrieving top-rated recipes", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}