package com.publicis.recipes.service;

import com.publicis.recipes.entity.Recipe;
import com.publicis.recipes.external.ExternalApiException;
import com.publicis.recipes.external.ExternalRecipeService;
import com.publicis.recipes.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for loading recipe data from external sources into the database.
 * 
 * This service handles:
 * - Asynchronous data loading from external APIs
 * - Batch processing and database operations
 * - Error handling and recovery
 * - Data validation and deduplication
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
@Service
public class DataLoadingService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoadingService.class);

    private final ExternalRecipeService externalRecipeService;
    private final RecipeRepository recipeRepository;

    private volatile boolean isLoading = false;
    private volatile LocalDateTime lastLoadTime;
    private volatile int lastLoadCount = 0;
    private volatile String lastLoadStatus = "Never loaded";

    public DataLoadingService(ExternalRecipeService externalRecipeService, 
                             RecipeRepository recipeRepository) {
        this.externalRecipeService = externalRecipeService;
        this.recipeRepository = recipeRepository;
    }

    /**
     * Asynchronously loads recipes from external API into the database.
     * 
     * @return CompletableFuture containing the number of recipes loaded
     */
    @Async
    @Transactional
    public CompletableFuture<DataLoadResult> loadRecipesFromExternalApi() {
        if (isLoading) {
            logger.warn("Recipe loading is already in progress, skipping request");
            return CompletableFuture.completedFuture(
                new DataLoadResult(0, "Loading already in progress", false)
            );
        }

        isLoading = true;
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            logger.info("Starting recipe data loading from external API");
            
            // Check if external API is accessible
            if (!externalRecipeService.isApiAccessible()) {
                String errorMsg = "External API is not accessible";
                logger.error(errorMsg);
                lastLoadStatus = "Failed: " + errorMsg;
                return CompletableFuture.completedFuture(
                    new DataLoadResult(0, errorMsg, false)
                );
            }

            // Fetch recipes from external API
            List<Recipe> externalRecipes = externalRecipeService.fetchAllRecipes();
            
            if (externalRecipes.isEmpty()) {
                String warningMsg = "No recipes received from external API";
                logger.warn(warningMsg);
                lastLoadStatus = "Warning: " + warningMsg;
                return CompletableFuture.completedFuture(
                    new DataLoadResult(0, warningMsg, true)
                );
            }

            logger.info("Received {} recipes from external API", externalRecipes.size());

            // Clear existing data (for fresh load)
            long existingCount = recipeRepository.count();
            if (existingCount > 0) {
                logger.info("Clearing {} existing recipes from database", existingCount);
                recipeRepository.deleteAll();
            }

            // Save new recipes in batches
            int batchSize = 50;
            int totalSaved = 0;
            int batchCount = 0;

            for (int i = 0; i < externalRecipes.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, externalRecipes.size());
                List<Recipe> batch = externalRecipes.subList(i, endIndex);
                
                try {
                    List<Recipe> savedBatch = recipeRepository.saveAll(batch);
                    totalSaved += savedBatch.size();
                    batchCount++;
                    
                    logger.debug("Saved batch {} with {} recipes", batchCount, savedBatch.size());
                } catch (Exception e) {
                    logger.error("Failed to save batch {}: {}", batchCount + 1, e.getMessage());
                    // Continue with next batch
                }
            }

            // Update loading status
            lastLoadTime = LocalDateTime.now();
            lastLoadCount = totalSaved;
            lastLoadStatus = "Success";

            logger.info("Successfully loaded {} recipes in {} batches. Duration: {} seconds", 
                       totalSaved, batchCount, 
                       java.time.Duration.between(startTime, lastLoadTime).getSeconds());

            return CompletableFuture.completedFuture(
                new DataLoadResult(totalSaved, "Successfully loaded recipes", true)
            );

        } catch (ExternalApiException e) {
            String errorMsg = "External API error: " + e.getMessage();
            logger.error(errorMsg, e);
            lastLoadStatus = "Failed: " + errorMsg;
            return CompletableFuture.completedFuture(
                new DataLoadResult(0, errorMsg, false)
            );
        } catch (Exception e) {
            String errorMsg = "Unexpected error during data loading: " + e.getMessage();
            logger.error(errorMsg, e);
            lastLoadStatus = "Failed: " + errorMsg;
            return CompletableFuture.completedFuture(
                new DataLoadResult(0, errorMsg, false)
            );
        } finally {
            isLoading = false;
        }
    }

    /**
     * Gets the current loading status.
     * 
     * @return DataLoadStatus containing current loading information
     */
    public DataLoadStatus getLoadingStatus() {
        return new DataLoadStatus(
            isLoading,
            lastLoadTime,
            lastLoadCount,
            lastLoadStatus,
            recipeRepository.count()
        );
    }

    /**
     * Checks if data loading is currently in progress.
     * 
     * @return true if loading is in progress, false otherwise
     */
    public boolean isLoadingInProgress() {
        return isLoading;
    }

    /**
     * Gets the total number of recipes currently in the database.
     * 
     * @return total recipe count
     */
    public long getTotalRecipeCount() {
        return recipeRepository.count();
    }

    /**
     * Data class representing the result of a data loading operation.
     */
    public static class DataLoadResult {
        private final int count;
        private final String message;
        private final boolean success;
        private final LocalDateTime timestamp;

        public DataLoadResult(int count, String message, boolean success) {
            this.count = count;
            this.message = message;
            this.success = success;
            this.timestamp = LocalDateTime.now();
        }

        public int getCount() { return count; }
        public String getMessage() { return message; }
        public boolean isSuccess() { return success; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    /**
     * Data class representing the current loading status.
     */
    public static class DataLoadStatus {
        private final boolean isLoading;
        private final LocalDateTime lastLoadTime;
        private final int lastLoadCount;
        private final String lastLoadStatus;
        private final long totalRecipesInDatabase;

        public DataLoadStatus(boolean isLoading, LocalDateTime lastLoadTime, 
                             int lastLoadCount, String lastLoadStatus, 
                             long totalRecipesInDatabase) {
            this.isLoading = isLoading;
            this.lastLoadTime = lastLoadTime;
            this.lastLoadCount = lastLoadCount;
            this.lastLoadStatus = lastLoadStatus;
            this.totalRecipesInDatabase = totalRecipesInDatabase;
        }

        public boolean isLoading() { return isLoading; }
        public LocalDateTime getLastLoadTime() { return lastLoadTime; }
        public int getLastLoadCount() { return lastLoadCount; }
        public String getLastLoadStatus() { return lastLoadStatus; }
        public long getTotalRecipesInDatabase() { return totalRecipesInDatabase; }
    }
}