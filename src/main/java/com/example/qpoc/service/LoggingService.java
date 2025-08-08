package com.example.qpoc.service;

import com.example.qpoc.util.MDCUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service class providing comprehensive logging demonstrations and business logic operations.
 * <p>
 * This service demonstrates various logging scenarios including different log levels,
 * MDC context management, transaction tracking, and asynchronous processing with
 * context preservation. It serves as the primary business logic layer for the
 * Q-POC application's logging functionality demonstrations.
 * </p>
 * <p>
 * Key features include:
 * <ul>
 *   <li>Demonstration of all SLF4J logging levels (TRACE, DEBUG, INFO, WARN, ERROR)</li>
 *   <li>MDC context management and propagation</li>
 *   <li>Transaction tracking with unique identifiers</li>
 *   <li>Asynchronous processing with context preservation</li>
 *   <li>Business logic simulation with proper logging</li>
 * </ul>
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class LoggingService {

    /**
     * Demonstrates all available logging levels for testing and verification.
     * <p>
     * Triggers log messages at TRACE, DEBUG, INFO, WARN, and ERROR levels
     * to help verify logging configuration and demonstrate the different
     * log levels available in SLF4J. Useful for testing log level filtering
     * and output formatting.
     * </p>
     */
    public void demonstrateLogging() {
        log.trace("This is a TRACE level log message");
        log.debug("This is a DEBUG level log message");
        log.info("This is an INFO level log message");
        log.warn("This is a WARN level log message");
        log.error("This is an ERROR level log message");
    }

    /**
     * Processes input data with comprehensive logging and transaction tracking.
     * <p>
     * Demonstrates a complete business operation with proper logging at each step,
     * including input validation, data transformation, and error handling.
     * Uses MDC context to track the operation and transaction ID for
     * request traceability.
     * </p>
     *
     * @param input the data to process; must not be null.
     * @return the processed data result, typically transformed to uppercase.
     * @throws RuntimeException if processing fails due to validation or transformation errors.
     */
    public String processData(String input) {
        MDCUtil.setOperation("processData");
        String transactionId = MDCUtil.generateAndSetTransactionId();
        
        log.info("Processing data with input: {}", input);
        
        try {
            if (input == null || input.trim().isEmpty()) {
                log.warn("Received null or empty input");
                return "Invalid input";
            }
            
            // Simulate some processing steps
            log.debug("Step 1: Validating input");
            validateInput(input);
            
            log.debug("Step 2: Transforming input");
            String result = transformInput(input);
            
            log.debug("Step 3: Processing completed successfully");
            log.info("Data processing completed successfully for transaction: {}", transactionId);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error processing data for transaction: {}", transactionId, e);
            throw e;
        } finally {
            MDCUtil.clearTransactionContext();
        }
    }

    /**
     * Demonstrates comprehensive MDC context management functionality.
     * <p>
     * Shows various ways to manage MDC context including logging current context,
     * adding business-specific context values, and using context scoping with
     * automatic cleanup. Demonstrates both single value and map-based context
     * management approaches.
     * </p>
     */
    public void demonstrateMDCContext() {
        log.info("Demonstrating MDC context management");
        
        // Log current context
        MDCUtil.logCurrentContext();
        
        // Add some business context
        MDCUtil.withContext("businessProcess", "dataValidation", () -> {
            log.info("Executing business process");
            performBusinessLogic();
        });
        
        // Add multiple context values
        Map<String, String> contextMap = Map.of(
            "module", "reporting",
            "reportType", "daily",
            "format", "PDF"
        );
        
        MDCUtil.withContext(contextMap, () -> {
            log.info("Generating report with specific context");
            generateReport();
        });
        
        log.info("MDC context demonstration completed");
    }

    /**
     * Processes data asynchronously while preserving MDC context across threads.
     * <p>
     * Demonstrates how to maintain request traceability in asynchronous operations
     * by capturing the current MDC context and restoring it in the async thread.
     * This is crucial for maintaining logging context in multi-threaded environments.
     * </p>
     *
     * @param input the data to process asynchronously.
     * @return CompletableFuture containing the async processing result.
     */
    public CompletableFuture<String> processDataAsync(String input) {
        // Capture current MDC context for async processing
        Map<String, String> currentContext = MDCUtil.getCurrentContext();
        
        return CompletableFuture.supplyAsync(() -> {
            // Restore MDC context in async thread
            MDCUtil.withContext(currentContext, () -> {
                log.info("Starting async processing");
                
                try {
                    // Simulate async work
                    Thread.sleep(100);
                    log.debug("Async processing in progress");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Async processing interrupted", e);
                }
            });
            
            return "Async result for: " + input;
        }).whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Async processing failed", throwable);
            } else {
                log.info("Async processing completed successfully");
            }
        });
    }

    /**
     * Validates input data and logs validation details.
     * <p>
     * Performs basic input validation including length checks and logs
     * validation warnings when input exceeds recommended limits.
     * </p>
     *
     * @param input the input data to validate.
     */
    private void validateInput(String input) {
        log.debug("Validating input: length={}", input.length());
        if (input.length() > 100) {
            log.warn("Input length exceeds maximum allowed: {}", input.length());
        }
    }

    /**
     * Transforms input data to uppercase format.
     * <p>
     * Performs data transformation with detailed logging of the transformation
     * process including before and after values for debugging purposes.
     * </p>
     *
     * @param input the input data to transform.
     * @return the transformed data in uppercase format.
     */
    private String transformInput(String input) {
        log.debug("Transforming input to uppercase");
        String result = input.toUpperCase();
        log.debug("Transformation completed: original='{}', transformed='{}'", input, result);
        return result;
    }

    /**
     * Simulates core business logic execution with appropriate logging.
     * <p>
     * Represents a typical business operation with debug and info level
     * logging to demonstrate proper logging practices in business methods.
     * </p>
     */
    private void performBusinessLogic() {
        log.debug("Executing core business logic");
        // Simulate some business processing
        log.info("Business logic executed successfully");
    }

    /**
     * Simulates report generation process with logging.
     * <p>
     * Demonstrates logging in a report generation context, showing how
     * different operations within a business process should be logged.
     * </p>
     */
    private void generateReport() {
        log.debug("Starting report generation");
        // Simulate report generation
        log.info("Report generated successfully");
    }
}
