package com.example.qpoc.controller;

import com.example.qpoc.service.LoggingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}
