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

/**
 * REST controller providing endpoints for demonstrating logging and MDC functionality.
 * <p>
 * This controller serves as the main API interface for the Q-POC application, providing
 * comprehensive endpoints to demonstrate various logging scenarios, MDC context management,
 * asynchronous processing, and error handling. All endpoints include proper logging with
 * contextual information and request tracing.
 * </p>
 * <p>
 * The controller integrates with:
 * <ul>
 *   <li>LoggingService for business logic and logging demonstrations</li>
 *   <li>MDCUtil for context management and request tracing</li>
 *   <li>Swagger UI for comprehensive API documentation</li>
 *   <li>Global exception handling for consistent error responses</li>
 * </ul>
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Q-POC API", description = "Main API endpoints for the Q-POC Spring Boot application")
public class HelloController {

    /**
     * Service for handling logging operations and business logic demonstrations.
     * <p>
     * Injected via Spring's dependency injection mechanism to provide
     * logging functionality and MDC context management operations.
     * </p>
     */
    @Autowired
    private LoggingService loggingService;

    /**
     * Returns a simple hello message from the Spring Boot application.
     * <p>
     * This endpoint serves as a basic health check and demonstrates simple
     * request logging with MDC context. Useful for verifying application
     * startup and basic functionality.
     * </p>
     *
     * @return a greeting message indicating successful application startup.
     */
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
    
    /**
     * Returns the health status of the application.
     * <p>
     * Provides a simple health check endpoint that can be used by monitoring
     * systems to verify application availability and responsiveness.
     * </p>
     *
     * @return a status message indicating application health.
     */
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

    /**
     * Processes input data and returns the transformed result.
     * <p>
     * Demonstrates service layer integration with comprehensive logging
     * and transaction tracking. The processing includes input validation,
     * transformation, and proper error handling with MDC context.
     * </p>
     *
     * @param input the data to process; defaults to "test" if not provided.
     * @return the processed data result.
     */
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

    /**
     * Demonstrates all logging levels for testing and verification purposes.
     * <p>
     * Triggers TRACE, DEBUG, INFO, WARN, and ERROR level log messages
     * to demonstrate the logging configuration and help verify that
     * all log levels are properly configured and visible.
     * </p>
     *
     * @return a message directing users to check the application logs.
     */
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

    /**
     * Demonstrates MDC context functionality and returns current context information.
     * <p>
     * Shows how MDC context is managed throughout request processing,
     * including context propagation, custom context values, and
     * context cleanup. Returns the current MDC context as JSON.
     * </p>
     *
     * @return ResponseEntity containing the current MDC context map.
     */
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

    /**
     * Processes data with user-specific context for demonstration purposes.
     * <p>
     * Demonstrates custom MDC context management by adding user-specific
     * context information during processing. Shows how context can be
     * dynamically modified and restored during request processing.
     * </p>
     *
     * @param userId the user identifier for context-specific processing.
     * @param operation the operation type header for context tracking.
     * @param data the request body data to process.
     * @return a message indicating successful user-specific processing.
     */
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

    /**
     * Demonstrates asynchronous processing with MDC context preservation.
     * <p>
     * Shows how MDC context is preserved across thread boundaries during
     * asynchronous processing. This is crucial for maintaining request
     * traceability in async operations.
     * </p>
     *
     * @param input the data to process asynchronously.
     * @return CompletableFuture containing the async processing result.
     */
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

    /**
     * Returns detailed information about the current MDC context.
     * <p>
     * Provides comprehensive context information including request ID,
     * user ID, and the complete MDC context map. Useful for debugging
     * and verifying context propagation.
     * </p>
     *
     * @return ResponseEntity containing detailed context information.
     */
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

    /**
     * Simulates error conditions for testing error handling and MDC context.
     * <p>
     * Allows testing of error scenarios to verify that MDC context is
     * properly maintained during exception handling and that error
     * responses include appropriate contextual information.
     * </p>
     *
     * @param throwError whether to simulate an error condition.
     * @return ResponseEntity with success message or error details.
     */
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

    /**
     * Processes data using validated request body with comprehensive validation.
     * <p>
     * Demonstrates request validation using Bean Validation annotations
     * and shows how validation errors are handled with proper MDC context
     * information included in error responses.
     * </p>
     *
     * @param request the validated process request containing data and options.
     * @return ResponseEntity containing the processed data result.
     */
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
