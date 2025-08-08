package com.example.qpoc.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class MDCUtil {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String OPERATION = "operation";

    /**
     * Get the current request ID from MDC
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    /**
     * Get the current user ID from MDC
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }

    /**
     * Set a transaction ID for database operations or external service calls
     */
    public static void setTransactionId(String transactionId) {
        MDC.put(TRANSACTION_ID, transactionId);
        log.debug("Transaction ID set: {}", transactionId);
    }

    /**
     * Generate and set a new transaction ID
     */
    public static String generateAndSetTransactionId() {
        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        setTransactionId(transactionId);
        return transactionId;
    }

    /**
     * Set the current operation being performed
     */
    public static void setOperation(String operation) {
        MDC.put(OPERATION, operation);
        log.debug("Operation set: {}", operation);
    }

    /**
     * Clear transaction-specific MDC keys (but keep request-level keys)
     */
    public static void clearTransactionContext() {
        MDC.remove(TRANSACTION_ID);
        MDC.remove(OPERATION);
        log.debug("Transaction context cleared");
    }

    /**
     * Execute a block of code with additional MDC context
     */
    public static void withContext(String key, String value, Runnable runnable) {
        String originalValue = MDC.get(key);
        try {
            MDC.put(key, value);
            runnable.run();
        } finally {
            if (originalValue != null) {
                MDC.put(key, originalValue);
            } else {
                MDC.remove(key);
            }
        }
    }

    /**
     * Execute a block of code with multiple MDC context values
     */
    public static void withContext(Map<String, String> contextMap, Runnable runnable) {
        Map<String, String> originalContext = MDC.getCopyOfContextMap();
        try {
            contextMap.forEach(MDC::put);
            runnable.run();
        } finally {
            MDC.clear();
            if (originalContext != null) {
                MDC.setContextMap(originalContext);
            }
        }
    }

    /**
     * Get all current MDC context as a map
     */
    public static Map<String, String> getCurrentContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * Log current MDC context for debugging
     */
    public static void logCurrentContext() {
        Map<String, String> context = getCurrentContext();
        if (context != null && !context.isEmpty()) {
            log.debug("Current MDC context: {}", context);
        } else {
            log.debug("MDC context is empty");
        }
    }
}
