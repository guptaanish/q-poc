package com.example.qpoc.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class LoggingServiceTest {

    @Autowired
    private LoggingService loggingService;

    @Test
    void testProcessDataWithValidInput() {
        log.info("Testing LoggingService.processData with valid input");
        String input = "hello world";
        String result = loggingService.processData(input);
        
        assertEquals("HELLO WORLD", result);
        log.debug("Test completed successfully for valid input");
    }

    @Test
    void testProcessDataWithNullInput() {
        log.info("Testing LoggingService.processData with null input");
        String result = loggingService.processData(null);
        
        assertEquals("Invalid input", result);
        log.debug("Test completed successfully for null input");
    }

    @Test
    void testProcessDataWithEmptyInput() {
        log.info("Testing LoggingService.processData with empty input");
        String result = loggingService.processData("   ");
        
        assertEquals("Invalid input", result);
        log.debug("Test completed successfully for empty input");
    }

    @Test
    void testDemonstrateLogging() {
        log.info("Testing LoggingService.demonstrateLogging method");
        // This test just ensures the method runs without exceptions
        assertDoesNotThrow(() -> loggingService.demonstrateLogging());
        log.debug("Logging demonstration test completed");
    }
}
