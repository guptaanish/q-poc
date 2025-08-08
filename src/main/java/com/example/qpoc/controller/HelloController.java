package com.example.qpoc.controller;

import com.example.qpoc.dto.ContextInfoResponse;
import com.example.qpoc.dto.ErrorResponse;
import com.example.qpoc.dto.ProcessRequest;
import com.example.qpoc.service.LoggingService;
import com.example.qpoc.util.MDCUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Q-POC API", description = "Main API endpoints for the Q-POC Spring Boot application")
public class HelloController {

    @Autowired
    private LoggingService loggingService;

    @Operation(
            summary = "Get hello message",
            description = "Returns a simple hello message from the Spring Boot application",
            tags = {"Basic Operations"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved hello message",
                    content = @Content(mediaType = "text/plain", 
                    examples = @ExampleObject(value = "Hello from Spring Boot with Java 21!")))
    })
    @GetMapping("/hello")
    public String hello() {
        log.info("Received request for /api/hello endpoint");
        String response = "Hello from Spring Boot with Java 21!";
        log.debug("Returning response: {}", response);
        return response;
    }
    
    @Operation(
            summary = "Health check",
            description = "Returns the health status of the application",
            tags = {"Basic Operations"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is healthy",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Application is running successfully!")))
    })
    @GetMapping("/health")
    public String health() {
        log.info("Received request for /api/health endpoint");
        String response = "Application is running successfully!";
        log.debug("Health check response: {}", response);
        return response;
    }

    @Operation(
            summary = "Process data",
            description = "Processes input data and returns the result. Demonstrates service layer logging with transaction tracking.",
            tags = {"Logging Demonstration"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data processed successfully",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "HELLO WORLD"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/process")
    public String processData(
            @Parameter(description = "Input data to process", example = "hello world")
            @RequestParam(defaultValue = "test") String input) {
        log.info("Received request for /api/process endpoint with input: {}", input);
        String result = loggingService.processData(input);
        log.debug("Processing completed, returning: {}", result);
        return result;
    }

    @Operation(
            summary = "Demonstrate logging levels",
            description = "Triggers all logging levels (TRACE, DEBUG, INFO, WARN, ERROR) for demonstration purposes",
            tags = {"Logging Demonstration"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logging demonstration completed",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Check the logs to see different log levels in action!")))
    })
    @GetMapping("/demo-logs")
    public String demonstrateLogs() {
        log.info("Received request for /api/demo-logs endpoint");
        loggingService.demonstrateLogging();
        return "Check the logs to see different log levels in action!";
    }

    @Operation(
            summary = "Demonstrate MDC context",
            description = "Demonstrates Mapped Diagnostic Context (MDC) functionality and returns current context",
            tags = {"MDC Context Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MDC context demonstration completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
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

    @Operation(
            summary = "Process data for specific user",
            description = "Processes data with user-specific context. Demonstrates custom MDC context management.",
            tags = {"MDC Context Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data processed successfully",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Processed data for user: john.doe"))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/user/{userId}/process")
    public String processWithUserContext(
            @Parameter(description = "User identifier", example = "john.doe")
            @PathVariable String userId,
            @Parameter(description = "Processing operation type", example = "dataTransformation")
            @RequestHeader(value = "X-Operation", defaultValue = "userDataProcessing") String operation,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to process",
                    content = @Content(examples = @ExampleObject(value = "Sample user data to process"))
            )
            @RequestBody String data) {
        
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

    @Operation(
            summary = "Process data asynchronously",
            description = "Demonstrates asynchronous processing with MDC context preservation across threads",
            tags = {"Async Processing"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Async processing completed",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Async result for: test-data")))
    })
    @GetMapping("/async-process")
    public CompletableFuture<String> processAsync(
            @Parameter(description = "Input data for async processing", example = "async-test")
            @RequestParam(defaultValue = "async-test") String input) {
        log.info("Received request for async processing with input: {}", input);
        
        return loggingService.processDataAsync(input)
                .thenApply(result -> {
                    log.info("Async processing completed in controller");
                    return result;
                });
    }

    @Operation(
            summary = "Get current context information",
            description = "Returns detailed information about the current MDC context including request ID, user ID, and full context map",
            tags = {"MDC Context Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Context information retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContextInfoResponse.class)))
    })
    @GetMapping("/context-info")
    public ResponseEntity<ContextInfoResponse> getContextInfo() {
        log.info("Received request for context information");
        
        Map<String, String> mdcContext = MDCUtil.getCurrentContext();
        String requestId = MDCUtil.getRequestId();
        String userId = MDCUtil.getUserId();
        
        ContextInfoResponse contextInfo = new ContextInfoResponse(
            requestId != null ? requestId : "N/A",
            userId != null ? userId : "N/A",
            mdcContext != null ? mdcContext : Map.of(),
            System.currentTimeMillis()
        );
        
        log.debug("Context info prepared: {}", contextInfo);
        return ResponseEntity.ok(contextInfo);
    }

    @Operation(
            summary = "Simulate error scenarios",
            description = "Simulates error conditions for testing error handling and MDC context in error scenarios",
            tags = {"Error Handling"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "No error simulation requested",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "No error occurred"))),
            @ApiResponse(responseCode = "500", description = "Simulated error occurred",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Error occurred - check logs for details with request ID: abc123-def456")))
    })
    @PostMapping("/simulate-error")
    public ResponseEntity<String> simulateError(
            @Parameter(description = "Whether to throw an error", example = "true")
            @RequestParam(defaultValue = "false") boolean throwError) {
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

    @Operation(
            summary = "Process data with validation",
            description = "Processes data using request body with validation. Demonstrates request validation and error handling.",
            tags = {"Logging Demonstration"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data processed successfully",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "PROCESSED DATA"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/process-validated")
    public ResponseEntity<String> processValidatedData(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data processing request with validation",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProcessRequest.class))
            )
            @Valid @RequestBody ProcessRequest request) {
        
        log.info("Received validated process request: {}", request);
        
        String result = loggingService.processData(request.getData());
        log.debug("Validated processing completed: {}", result);
        
        return ResponseEntity.ok(result);
    }
}
