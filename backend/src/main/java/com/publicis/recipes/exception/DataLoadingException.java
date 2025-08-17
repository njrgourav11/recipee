package com.publicis.recipes.exception;

/**
 * Exception thrown when data loading operations fail.
 * 
 * @author Recipe Management Team
 * @version 1.0.0
 */
public class DataLoadingException extends RuntimeException {

    /**
     * Constructs a new DataLoadingException with the specified detail message.
     * 
     * @param message the detail message
     */
    public DataLoadingException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataLoadingException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DataLoadingException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public DataLoadingException(Throwable cause) {
        super(cause);
    }
}