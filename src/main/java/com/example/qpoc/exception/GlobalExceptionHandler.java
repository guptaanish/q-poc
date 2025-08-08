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

@Slf4j
@RestControllerAdvice
@Hidden // Hide from Swagger documentation
public class GlobalExceptionHandler {

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
