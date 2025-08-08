package com.example.qpoc.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MDCUtilTest {

    @BeforeEach
    void setUp() {
        log.info("Setting up MDC test");
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        log.info("Cleaning up MDC test");
        MDC.clear();
    }

    @Test
    void testSetAndGetTransactionId() {
        log.info("Testing transaction ID management");
        
        String transactionId = "test-tx-123";
        MDCUtil.setTransactionId(transactionId);
        
        assertEquals(transactionId, MDC.get("transactionId"));
        log.debug("Transaction ID test passed");
    }

    @Test
    void testGenerateAndSetTransactionId() {
        log.info("Testing transaction ID generation");
        
        String generatedId = MDCUtil.generateAndSetTransactionId();
        
        assertNotNull(generatedId);
        assertEquals(8, generatedId.length());
        assertEquals(generatedId, MDC.get("transactionId"));
        log.debug("Transaction ID generation test passed");
    }

    @Test
    void testSetOperation() {
        log.info("Testing operation context management");
        
        String operation = "testOperation";
        MDCUtil.setOperation(operation);
        
        assertEquals(operation, MDC.get("operation"));
        log.debug("Operation context test passed");
    }

    @Test
    void testClearTransactionContext() {
        log.info("Testing transaction context clearing");
        
        MDCUtil.setTransactionId("test-tx");
        MDCUtil.setOperation("test-op");
        MDC.put("requestId", "test-request");
        
        MDCUtil.clearTransactionContext();
        
        assertNull(MDC.get("transactionId"));
        assertNull(MDC.get("operation"));
        assertEquals("test-request", MDC.get("requestId")); // Should remain
        log.debug("Transaction context clearing test passed");
    }

    @Test
    void testWithContextSingleValue() {
        log.info("Testing single value context management");
        
        String originalValue = "original";
        String testValue = "test";
        
        MDC.put("testKey", originalValue);
        
        MDCUtil.withContext("testKey", testValue, () -> {
            assertEquals(testValue, MDC.get("testKey"));
            log.debug("Inside context block with test value");
        });
        
        assertEquals(originalValue, MDC.get("testKey"));
        log.debug("Single value context test passed");
    }

    @Test
    void testWithContextMultipleValues() {
        log.info("Testing multiple values context management");
        
        Map<String, String> contextMap = Map.of(
            "key1", "value1",
            "key2", "value2"
        );
        
        MDCUtil.withContext(contextMap, () -> {
            assertEquals("value1", MDC.get("key1"));
            assertEquals("value2", MDC.get("key2"));
            log.debug("Inside context block with multiple values");
        });
        
        assertNull(MDC.get("key1"));
        assertNull(MDC.get("key2"));
        log.debug("Multiple values context test passed");
    }

    @Test
    void testGetCurrentContext() {
        log.info("Testing current context retrieval");
        
        MDC.put("key1", "value1");
        MDC.put("key2", "value2");
        
        Map<String, String> context = MDCUtil.getCurrentContext();
        
        assertNotNull(context);
        assertEquals("value1", context.get("key1"));
        assertEquals("value2", context.get("key2"));
        log.debug("Current context retrieval test passed");
    }

    @Test
    void testLogCurrentContext() {
        log.info("Testing current context logging");
        
        MDC.put("testKey", "testValue");
        
        // This should not throw any exception
        assertDoesNotThrow(() -> MDCUtil.logCurrentContext());
        log.debug("Current context logging test passed");
    }

    @Test
    void testGetRequestIdWhenNotSet() {
        log.info("Testing request ID retrieval when not set");
        
        String requestId = MDCUtil.getRequestId();
        
        assertNull(requestId);
        log.debug("Request ID retrieval test passed");
    }

    @Test
    void testGetUserIdWhenNotSet() {
        log.info("Testing user ID retrieval when not set");
        
        String userId = MDCUtil.getUserId();
        
        assertNull(userId);
        log.debug("User ID retrieval test passed");
    }

    @Test
    void testGetRequestIdWhenSet() {
        log.info("Testing request ID retrieval when set");
        
        String expectedRequestId = "test-request-123";
        MDC.put("requestId", expectedRequestId);
        
        String actualRequestId = MDCUtil.getRequestId();
        
        assertEquals(expectedRequestId, actualRequestId);
        log.debug("Request ID retrieval with value test passed");
    }

    @Test
    void testGetUserIdWhenSet() {
        log.info("Testing user ID retrieval when set");
        
        String expectedUserId = "test-user-456";
        MDC.put("userId", expectedUserId);
        
        String actualUserId = MDCUtil.getUserId();
        
        assertEquals(expectedUserId, actualUserId);
        log.debug("User ID retrieval with value test passed");
    }
}
