package com.example.qpoc.exception;

import com.example.qpoc.dto.ErrorResponse;
import com.example.qpoc.util.MDCUtil;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for centralized error handling across the application.
 * <p>
 * This controller advice provides centralized exception handling for all REST
 * controllers in the application. It ensures consistent error response formats,
 * proper logging of exceptions with MDC context, and appropriate HTTP status
 * codes for different error scenarios.
 * </p>
 * <p>
 * The handler supports multiple exception types including:
 * <ul>
 *   <li>Bean validation errors with detailed field-level error messages</li>
 *   <li>Runtime exceptions with full error context</li>
 *   <li>Generic exceptions with sanitized error messages</li>
 * </ul>
 * </p>
 * <p>
 * All error responses include MDC context information (request ID, timestamp)
 * to enable correlation with server logs for debugging purposes. The handler
 * is hidden from Swagger documentation to avoid cluttering the API specification.
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
@Hidden // Hide from Swagger documentation
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation exceptions with detailed field-level error information.
     * <p>
     * This method processes validation failures that occur when request bodies
     * or parameters fail Bean Validation constraints. It extracts all field-level
     * validation errors and formats them into a comprehensive error response
     * that clients can use to understand and correct validation issues.
     * </p>
     * <p>
     * The response includes:
     * <ul>
     *   <li>General validation failure message</li>
     *   <li>Map of field names to specific error messages</li>
     *   <li>Request ID for log correlation</li>
     *   <li>Timestamp and HTTP status code</li>
     * </ul>
     * </p>
     *
     * @param ex the MethodArgumentNotValidException containing validation errors.
     * @return ResponseEntity with detailed validation error information and BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validation error occurred: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("requestId", MDCUtil.getRequestId());
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        log.debug("Validation error response: {}", response);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles runtime exceptions with full error context and logging.
     * <p>
     * This method processes RuntimeException instances that occur during
     * request processing. It logs the full exception with stack trace
     * and returns a standardized error response with the original
     * exception message and contextual information.
     * </p>
     * <p>
     * Runtime exceptions typically indicate programming errors or
     * unexpected conditions that should be investigated and resolved.
     * The full exception details are logged for debugging while
     * returning appropriate error information to the client.
     * </p>
     *
     * @param ex the RuntimeException that occurred during processing.
     * @return ResponseEntity with error details and INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            MDCUtil.getRequestId(),
            System.currentTimeMillis(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    /**
     * Handles all other exceptions with sanitized error messages.
     * <p>
     * This method serves as a catch-all handler for any exceptions not
     * specifically handled by other exception handlers. It logs the full
     * exception details for debugging but returns a generic error message
     * to avoid exposing sensitive system information to clients.
     * </p>
     * <p>
     * This handler ensures that no exceptions go unhandled and that
     * clients always receive a consistent error response format,
     * even for unexpected error conditions.
     * </p>
     *
     * @param ex the Exception that occurred during processing.
     * @return ResponseEntity with generic error message and INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "An unexpected error occurred",
            MDCUtil.getRequestId(),
            System.currentTimeMillis(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
