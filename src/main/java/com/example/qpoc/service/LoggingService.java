package com.example.qpoc.service;

import com.example.qpoc.util.MDCUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class LoggingService {

    public void demonstrateLogging() {
        log.trace("This is a TRACE level log message");
        log.debug("This is a DEBUG level log message");
        log.info("This is an INFO level log message");
        log.warn("This is a WARN level log message");
        log.error("This is an ERROR level log message");
    }

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

    private void validateInput(String input) {
        log.debug("Validating input: length={}", input.length());
        if (input.length() > 100) {
            log.warn("Input length exceeds maximum allowed: {}", input.length());
        }
    }

    private String transformInput(String input) {
        log.debug("Transforming input to uppercase");
        String result = input.toUpperCase();
        log.debug("Transformation completed: original='{}', transformed='{}'", input, result);
        return result;
    }

    private void performBusinessLogic() {
        log.debug("Executing core business logic");
        // Simulate some business processing
        log.info("Business logic executed successfully");
    }

    private void generateReport() {
        log.debug("Starting report generation");
        // Simulate report generation
        log.info("Report generated successfully");
    }
}
