package com.publicis.recipes.external;

/**
 * Custom exception for external API related errors.
 * 
 * This exception is thrown when there are issues communicating with
 * external APIs, such as network timeouts, server errors, or invalid responses.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
public class ExternalApiException extends RuntimeException {

    /**
     * Constructs a new ExternalApiException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ExternalApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new ExternalApiException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ExternalApiException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public ExternalApiException(Throwable cause) {
        super(cause);
    }
}