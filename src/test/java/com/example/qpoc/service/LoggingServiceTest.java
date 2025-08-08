package com.example.qpoc.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class LoggingServiceTest {

    @Autowired
    private LoggingService loggingService;

    @BeforeEach
    void setUp() {
        log.info("Setting up LoggingService test");
        MDC.clear();
        // Set up basic MDC context for testing
        MDC.put("requestId", "test-request-123");
        MDC.put("userId", "test-user");
    }

    @AfterEach
    void tearDown() {
        log.info("Cleaning up LoggingService test");
        MDC.clear();
    }

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

    @Test
    void testDemonstrateMDCContext() {
        log.info("Testing LoggingService.demonstrateMDCContext method");
        
        // Verify initial MDC context exists
        assertNotNull(MDC.get("requestId"));
        assertNotNull(MDC.get("userId"));
        
        // This test ensures the method runs without exceptions and preserves context
        assertDoesNotThrow(() -> loggingService.demonstrateMDCContext());
        
        // Verify original context is preserved
        assertEquals("test-request-123", MDC.get("requestId"));
        assertEquals("test-user", MDC.get("userId"));
        
        log.debug("MDC context demonstration test completed");
    }

    @Test
    void testProcessDataAsync() throws ExecutionException, InterruptedException {
        log.info("Testing LoggingService.processDataAsync method");
        
        String input = "async test";
        CompletableFuture<String> future = loggingService.processDataAsync(input);
        
        assertNotNull(future);
        String result = future.get(); // Wait for completion
        
        assertEquals("Async result for: " + input, result);
        log.debug("Async processing test completed successfully");
    }

    @Test
    void testProcessDataWithMDCContext() {
        log.info("Testing processData with specific MDC context");
        
        // Add additional context
        MDC.put("testContext", "specific-test");
        
        String input = "mdc test";
        String result = loggingService.processData(input);
        
        assertEquals("MDC TEST", result);
        
        // Verify MDC context is preserved
        assertEquals("test-request-123", MDC.get("requestId"));
        assertEquals("test-user", MDC.get("userId"));
        assertEquals("specific-test", MDC.get("testContext"));
        
        log.debug("MDC context processing test completed");
    }

    @Test
    void testProcessDataClearsTransactionContext() {
        log.info("Testing that processData clears transaction-specific context");
        
        String input = "transaction test";
        loggingService.processData(input);
        
        // Transaction-specific context should be cleared
        assertNull(MDC.get("transactionId"));
        assertNull(MDC.get("operation"));
        
        // Request-level context should remain
        assertEquals("test-request-123", MDC.get("requestId"));
        assertEquals("test-user", MDC.get("userId"));
        
        log.debug("Transaction context clearing test completed");
    }

    @Test
    void testProcessDataWithLongInput() {
        log.info("Testing processData with long input to trigger warning");
        
        String longInput = "a".repeat(150); // Create a string longer than 100 characters
        String result = loggingService.processData(longInput);
        
        assertEquals(longInput.toUpperCase(), result);
        log.debug("Long input processing test completed");
    }

    @Test
    void testAsyncProcessingPreservesContext() throws ExecutionException, InterruptedException {
        log.info("Testing that async processing preserves MDC context");
        
        // Add specific context for this test
        MDC.put("asyncTest", "context-preservation");
        
        String input = "context test";
        CompletableFuture<String> future = loggingService.processDataAsync(input);
        
        String result = future.get();
        
        assertEquals("Async result for: " + input, result);
        
        // Original context should still be present
        assertEquals("test-request-123", MDC.get("requestId"));
        assertEquals("test-user", MDC.get("userId"));
        assertEquals("context-preservation", MDC.get("asyncTest"));
        
        log.debug("Async context preservation test completed");
    }
}
