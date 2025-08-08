package com.example.qpoc.controller;

import com.example.qpoc.service.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test class for HelloController REST endpoints.
 * <p>
 * This test class provides comprehensive testing of all HelloController endpoints
 * using Spring Boot's MockMvc framework. It tests both successful scenarios and
 * error conditions to ensure proper behavior, response formats, and error handling.
 * The tests use mocked dependencies to isolate controller logic and verify
 * correct integration with service layers.
 * </p>
 * <p>
 * The test suite covers:
 * <ul>
 *   <li>Basic endpoints (hello, health) for simple response verification</li>
 *   <li>Data processing endpoints with various input scenarios</li>
 *   <li>Validation testing with both valid and invalid request data</li>
 *   <li>Error simulation and handling verification</li>
 *   <li>MDC context information endpoints</li>
 *   <li>User-specific processing endpoints</li>
 * </ul>
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    /**
     * MockMvc instance for performing HTTP requests in tests.
     * <p>
     * Provides the ability to perform HTTP requests against the controller
     * without starting a full HTTP server. Automatically configured by
     * Spring Boot's test framework.
     * </p>
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper for JSON serialization and deserialization in tests.
     * <p>
     * Used to convert Java objects to JSON strings for request bodies
     * and to parse JSON responses. Automatically configured by Spring Boot.
     * </p>
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mocked LoggingService dependency for isolated controller testing.
     * <p>
     * This mock allows testing of controller logic without depending on
     * the actual service implementation. Test methods configure mock
     * behavior as needed for specific test scenarios.
     * </p>
     */
    @MockBean
    private LoggingService loggingService;

    /**
     * Tests the basic hello endpoint for correct response.
     * <p>
     * Verifies that the /api/hello endpoint returns the expected greeting
     * message with HTTP 200 status. This test ensures basic controller
     * functionality and proper Spring MVC configuration.
     * </p>
     */
    @Test
    void testHelloEndpoint() throws Exception {
        log.info("Testing /api/hello endpoint");
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from Spring Boot with Java 21!"));
        log.debug("Hello endpoint test completed successfully");
    }

    /**
     * Tests the health check endpoint for correct response.
     * <p>
     * Verifies that the /api/health endpoint returns the expected health
     * status message with HTTP 200 status. This test ensures the health
     * check functionality works correctly for monitoring purposes.
     * </p>
     */
    @Test
    void testHealthEndpoint() throws Exception {
        log.info("Testing /api/health endpoint");
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application is running successfully!"));
        log.debug("Health endpoint test completed successfully");
    }

    /**
     * Tests the data processing endpoint with parameter input.
     * <p>
     * Verifies that the /api/process endpoint correctly accepts input
     * parameters, calls the service layer, and returns the processed
     * result. Uses mocked service behavior to isolate controller testing.
     * </p>
     */
    @Test
    void testProcessEndpoint() throws Exception {
        log.info("Testing /api/process endpoint");
        when(loggingService.processData(anyString())).thenReturn("TEST");
        
        mockMvc.perform(get("/api/process").param("input", "test"))
                .andExpect(status().isOk())
                .andExpect(content().string("TEST"));
        log.debug("Process endpoint test completed successfully");
    }

    /**
     * Tests the logging demonstration endpoint.
     * <p>
     * Verifies that the /api/demo-logs endpoint returns the expected
     * message directing users to check logs. This test ensures the
     * logging demonstration functionality is accessible.
     * </p>
     */
    @Test
    void testDemoLogsEndpoint() throws Exception {
        log.info("Testing /api/demo-logs endpoint");
        mockMvc.perform(get("/api/demo-logs"))
                .andExpect(status().isOk())
                .andExpect(content().string("Check the logs to see different log levels in action!"));
        log.debug("Demo logs endpoint test completed successfully");
    }

    /**
     * Tests the validated processing endpoint with valid request data.
     * <p>
     * Verifies that the /api/process-validated endpoint correctly accepts
     * valid JSON request bodies, performs validation, and returns the
     * processed result. This test ensures proper request body handling
     * and validation integration.
     * </p>
     */
    @Test
    void testProcessValidatedEndpointWithValidData() throws Exception {
        log.info("Testing /api/process-validated endpoint with valid data");
        
        Map<String, Object> request = Map.of(
            "data", "test data",
            "options", "uppercase"
        );
        
        when(loggingService.processData("test data")).thenReturn("TEST DATA");
        
        mockMvc.perform(post("/api/process-validated")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("TEST DATA"));
        
        log.debug("Process validated endpoint test with valid data completed successfully");
    }

    /**
     * Tests the validated processing endpoint with invalid request data.
     * <p>
     * Verifies that the /api/process-validated endpoint correctly rejects
     * invalid request data and returns appropriate validation error responses.
     * This test ensures that Bean Validation is properly integrated and
     * error responses include detailed validation information.
     * </p>
     */
    @Test
    void testProcessValidatedEndpointWithInvalidData() throws Exception {
        log.info("Testing /api/process-validated endpoint with invalid data");
        
        Map<String, Object> request = Map.of(
            "data", "", // Empty data should fail validation
            "options", "uppercase"
        );
        
        mockMvc.perform(post("/api/process-validated")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.data").exists());
        
        log.debug("Process validated endpoint test with invalid data completed successfully");
    }

    /**
     * Tests the error simulation endpoint without triggering an error.
     * <p>
     * Verifies that the /api/simulate-error endpoint returns a success
     * message when the throwError parameter is false. This test ensures
     * the normal operation path of the error simulation functionality.
     * </p>
     */
    @Test
    void testSimulateErrorEndpointNoError() throws Exception {
        log.info("Testing /api/simulate-error endpoint without error");
        
        mockMvc.perform(post("/api/simulate-error")
                .param("throwError", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("No error occurred"));
        
        log.debug("Simulate error endpoint test (no error) completed successfully");
    }

    /**
     * Tests the error simulation endpoint with error triggering.
     * <p>
     * Verifies that the /api/simulate-error endpoint correctly handles
     * simulated errors and returns appropriate error responses with
     * HTTP 500 status. This test ensures error handling and response
     * formatting work correctly.
     * </p>
     */
    @Test
    void testSimulateErrorEndpointWithError() throws Exception {
        log.info("Testing /api/simulate-error endpoint with error");
        
        mockMvc.perform(post("/api/simulate-error")
                .param("throwError", "true"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error occurred - check logs for details")));
        
        log.debug("Simulate error endpoint test (with error) completed successfully");
    }

    /**
     * Tests the MDC context information endpoint.
     * <p>
     * Verifies that the /api/context-info endpoint returns a JSON response
     * containing all expected MDC context fields including request ID,
     * user ID, full context map, and timestamp. This test ensures proper
     * MDC context exposure for debugging purposes.
     * </p>
     */
    @Test
    void testContextInfoEndpoint() throws Exception {
        log.info("Testing /api/context-info endpoint");
        
        mockMvc.perform(get("/api/context-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.fullMDCContext").exists())
                .andExpect(jsonPath("$.timestamp").exists());
        
        log.debug("Context info endpoint test completed successfully");
    }

    /**
     * Tests the user-specific data processing endpoint.
     * <p>
     * Verifies that the /api/user/{userId}/process endpoint correctly
     * accepts path variables, headers, and request bodies while returning
     * the expected user-specific response. This test ensures proper
     * parameter binding and user context handling.
     * </p>
     */
    @Test
    void testUserProcessEndpoint() throws Exception {
        log.info("Testing /api/user/{userId}/process endpoint");
        
        when(loggingService.processData("user data")).thenReturn("USER DATA");
        
        mockMvc.perform(post("/api/user/testuser/process")
                .header("X-Operation", "testOperation")
                .content("user data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Processed data for user: testuser"));
        
        log.debug("User process endpoint test completed successfully");
    }
}
