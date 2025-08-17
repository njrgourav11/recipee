package com.publicis.recipes.exception;

/**
 * Exception thrown when search service operations fail.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
public class SearchServiceException extends RuntimeException {

    /**
     * Constructs a new SearchServiceException with the specified detail message.
     * 
     * @param message the detail message
     */
    public SearchServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new SearchServiceException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public SearchServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SearchServiceException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public SearchServiceException(Throwable cause) {
        super(cause);
    }
}