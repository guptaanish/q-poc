package com.example.qpoc.controller;

import com.example.qpoc.service.LoggingService;
import com.example.qpoc.util.MDCUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    private LoggingService loggingService;

    @GetMapping("/hello")
    public String hello() {
        log.info("Received request for /api/hello endpoint");
        String response = "Hello from Spring Boot with Java 21!";
        log.debug("Returning response: {}", response);
        return response;
    }
    
    @GetMapping("/health")
    public String health() {
        log.info("Received request for /api/health endpoint");
        String response = "Application is running successfully!";
        log.debug("Health check response: {}", response);
        return response;
    }

    @GetMapping("/process")
    public String processData(@RequestParam(defaultValue = "test") String input) {
        log.info("Received request for /api/process endpoint with input: {}", input);
        String result = loggingService.processData(input);
        log.debug("Processing completed, returning: {}", result);
        return result;
    }

    @GetMapping("/demo-logs")
    public String demonstrateLogs() {
        log.info("Received request for /api/demo-logs endpoint");
        loggingService.demonstrateLogging();
        return "Check the logs to see different log levels in action!";
    }

    @GetMapping("/demo-mdc")
    public ResponseEntity<Map<String, String>> demonstrateMDC() {
        log.info("Received request for /api/demo-mdc endpoint");
        
        // Demonstrate MDC context management
        loggingService.demonstrateMDCContext();
        
        // Return current MDC context
        Map<String, String> currentContext = MDCUtil.getCurrentContext();
        log.debug("Returning current MDC context: {}", currentContext);
        
        return ResponseEntity.ok(currentContext);
    }

    @PostMapping("/user/{userId}/process")
    public String processWithUserContext(
            @PathVariable String userId,
            @RequestBody String data,
            @RequestHeader(value = "X-Operation", defaultValue = "userDataProcessing") String operation) {
        
        log.info("Received request for user-specific processing: userId={}, operation={}", userId, operation);
        
        // Add user-specific context to MDC
        MDCUtil.withContext(Map.of(
            "targetUserId", userId,
            "dataSize", String.valueOf(data.length())
        ), () -> {
            MDCUtil.setOperation(operation);
            log.info("Processing data for specific user");
            
            // Process the data
            String result = loggingService.processData(data);
            log.info("User-specific processing completed");
        });
        
        return "Processed data for user: " + userId;
    }

    @GetMapping("/async-process")
    public CompletableFuture<String> processAsync(@RequestParam(defaultValue = "async-test") String input) {
        log.info("Received request for async processing with input: {}", input);
        
        return loggingService.processDataAsync(input)
                .thenApply(result -> {
                    log.info("Async processing completed in controller");
                    return result;
                });
    }

    @GetMapping("/context-info")
    public ResponseEntity<Map<String, Object>> getContextInfo() {
        log.info("Received request for context information");
        
        Map<String, String> mdcContext = MDCUtil.getCurrentContext();
        String requestId = MDCUtil.getRequestId();
        String userId = MDCUtil.getUserId();
        
        Map<String, Object> contextInfo = Map.of(
            "requestId", requestId != null ? requestId : "N/A",
            "userId", userId != null ? userId : "N/A",
            "fullMDCContext", mdcContext != null ? mdcContext : Map.of(),
            "timestamp", System.currentTimeMillis()
        );
        
        log.debug("Context info prepared: {}", contextInfo);
        return ResponseEntity.ok(contextInfo);
    }

    @PostMapping("/simulate-error")
    public ResponseEntity<String> simulateError(@RequestParam(defaultValue = "false") boolean throwError) {
        log.info("Received request to simulate error: throwError={}", throwError);
        
        try {
            if (throwError) {
                log.warn("About to throw simulated error");
                throw new RuntimeException("Simulated error for testing MDC context");
            }
            
            log.info("No error simulation requested");
            return ResponseEntity.ok("No error occurred");
            
        } catch (Exception e) {
            log.error("Simulated error occurred", e);
            return ResponseEntity.internalServerError()
                    .body("Error occurred - check logs for details with request ID: " + MDCUtil.getRequestId());
        }
    }
}
