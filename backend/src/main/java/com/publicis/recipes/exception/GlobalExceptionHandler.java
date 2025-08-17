package com.publicis.recipes.exception;

import com.publicis.recipes.external.ExternalApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            "Invalid input data provided",
            request.getDescription(false),
            errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        logger.warn("Constraint violation: {}", ex.getMessage());

        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Constraint violation",
            "Request parameters do not meet validation requirements",
            request.getDescription(false),
            errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, WebRequest request) {
        
        logger.warn("Missing request parameter: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Missing parameter",
            String.format("Required parameter '%s' is missing", ex.getParameterName()),
            request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        logger.warn("Method argument type mismatch: {}", ex.getMessage());

        String message = String.format("Parameter '%s' should be of type %s", 
            ex.getName(), ex.getRequiredType().getSimpleName());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid parameter type",
            message,
            request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        logger.warn("HTTP message not readable: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Malformed JSON",
            "Request body contains invalid JSON",
            request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecipeNotFoundException(
            RecipeNotFoundException ex, WebRequest request) {
        
        logger.info("Recipe not found: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Recipe not found",
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(
            ExternalApiException ex, WebRequest request) {
        
        logger.error("External API error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "External service error",
            "Unable to fetch data from external service. Please try again later.",
            request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(DataLoadingException.class)
    public ResponseEntity<ErrorResponse> handleDataLoadingException(
            DataLoadingException ex, WebRequest request) {
        
        logger.error("Data loading error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Data loading error",
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(SearchServiceException.class)
    public ResponseEntity<ErrorResponse> handleSearchServiceException(
            SearchServiceException ex, WebRequest request) {
        
        logger.error("Search service error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Search service error",
            "Search functionality is temporarily unavailable. Please try again later.",
            request.getDescription(false)
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid argument",
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal server error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false)
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;

        public ErrorResponse(int status, String error, String message, String path) {
            this.timestamp = LocalDateTime.now();
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        public ErrorResponse(int status, String error, String message, String path,
                           Map<String, String> validationErrors) {
            this(status, error, message, path);
            this.validationErrors = validationErrors;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
        public Map<String, String> getValidationErrors() { return validationErrors; }

        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public void setStatus(int status) { this.status = status; }
        public void setError(String error) { this.error = error; }
        public void setMessage(String message) { this.message = message; }
        public void setPath(String path) { this.path = path; }
        public void setValidationErrors(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
        }
    }
}