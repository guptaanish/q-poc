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

@Slf4j
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoggingService loggingService;

    @Test
    void testHelloEndpoint() throws Exception {
        log.info("Testing /api/hello endpoint");
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from Spring Boot with Java 21!"));
        log.debug("Hello endpoint test completed successfully");
    }

    @Test
    void testHealthEndpoint() throws Exception {
        log.info("Testing /api/health endpoint");
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application is running successfully!"));
        log.debug("Health endpoint test completed successfully");
    }

    @Test
    void testProcessEndpoint() throws Exception {
        log.info("Testing /api/process endpoint");
        when(loggingService.processData(anyString())).thenReturn("TEST");
        
        mockMvc.perform(get("/api/process").param("input", "test"))
                .andExpect(status().isOk())
                .andExpect(content().string("TEST"));
        log.debug("Process endpoint test completed successfully");
    }

    @Test
    void testDemoLogsEndpoint() throws Exception {
        log.info("Testing /api/demo-logs endpoint");
        mockMvc.perform(get("/api/demo-logs"))
                .andExpect(status().isOk())
                .andExpect(content().string("Check the logs to see different log levels in action!"));
        log.debug("Demo logs endpoint test completed successfully");
    }

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

    @Test
    void testSimulateErrorEndpointNoError() throws Exception {
        log.info("Testing /api/simulate-error endpoint without error");
        
        mockMvc.perform(post("/api/simulate-error")
                .param("throwError", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("No error occurred"));
        
        log.debug("Simulate error endpoint test (no error) completed successfully");
    }

    @Test
    void testSimulateErrorEndpointWithError() throws Exception {
        log.info("Testing /api/simulate-error endpoint with error");
        
        mockMvc.perform(post("/api/simulate-error")
                .param("throwError", "true"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error occurred - check logs for details")));
        
        log.debug("Simulate error endpoint test (with error) completed successfully");
    }

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
