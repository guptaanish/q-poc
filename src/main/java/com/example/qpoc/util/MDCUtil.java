package com.example.qpoc.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Utility class for managing SLF4J Mapped Diagnostic Context (MDC) operations.
 * <p>
 * This utility provides comprehensive MDC management functionality including
 * request tracking, user context management, transaction tracking, and
 * context scoping operations. It serves as the central point for all MDC
 * operations throughout the application, ensuring consistent context
 * management and proper cleanup.
 * </p>
 * <p>
 * Key features include:
 * <ul>
 *   <li>Request ID and user ID management</li>
 *   <li>Transaction tracking with unique identifiers</li>
 *   <li>Operation context management</li>
 *   <li>Scoped context execution with automatic cleanup</li>
 *   <li>Context debugging and inspection utilities</li>
 * </ul>
 * </p>
 * <p>
 * This class is thread-safe as it operates on the thread-local MDC context
 * provided by SLF4J. All methods are static and the class cannot be instantiated.
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class MDCUtil {

    /**
     * MDC key for storing the unique request identifier.
     * <p>
     * Used to track individual HTTP requests throughout their lifecycle
     * for debugging and monitoring purposes.
     * </p>
     */
    private static final String REQUEST_ID = "requestId";

    /**
     * MDC key for storing the user identifier.
     * <p>
     * Used to associate log entries with specific users for
     * security auditing and user-specific debugging.
     * </p>
     */
    private static final String USER_ID = "userId";

    /**
     * MDC key for storing the transaction identifier.
     * <p>
     * Used to track business transactions and database operations
     * that may span multiple method calls or service interactions.
     * </p>
     */
    private static final String TRANSACTION_ID = "transactionId";

    /**
     * MDC key for storing the current operation name.
     * <p>
     * Used to identify the specific business operation being performed
     * for better log categorization and debugging.
     * </p>
     */
    private static final String OPERATION = "operation";

    /**
     * Retrieves the current request ID from the MDC context.
     * <p>
     * The request ID is typically set by the MDCFilter at the beginning
     * of each HTTP request and remains available throughout the request
     * processing lifecycle.
     * </p>
     *
     * @return the current request ID, or null if not set.
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    /**
     * Retrieves the current user ID from the MDC context.
     * <p>
     * The user ID is typically set during authentication or extracted
     * from request headers and remains available throughout the request
     * processing lifecycle.
     * </p>
     *
     * @return the current user ID, or null if not set.
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }

    /**
     * Sets a transaction ID in the MDC context for tracking business operations.
     * <p>
     * Transaction IDs are used to correlate log entries related to specific
     * business transactions, database operations, or external service calls.
     * This method logs the transaction ID setting for debugging purposes.
     * </p>
     *
     * @param transactionId the transaction identifier to set; must not be null.
     */
    public static void setTransactionId(String transactionId) {
        MDC.put(TRANSACTION_ID, transactionId);
        log.debug("Transaction ID set: {}", transactionId);
    }

    /**
     * Generates a new unique transaction ID and sets it in the MDC context.
     * <p>
     * Creates a short UUID-based transaction ID (8 characters) for easier
     * readability in logs while maintaining uniqueness. The generated ID
     * is automatically set in the MDC context.
     * </p>
     *
     * @return the generated transaction ID.
     */
    public static String generateAndSetTransactionId() {
        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        setTransactionId(transactionId);
        return transactionId;
    }

    /**
     * Sets the current operation name in the MDC context.
     * <p>
     * Operation names help identify the specific business operation being
     * performed, making it easier to categorize and filter log entries
     * based on functionality.
     * </p>
     *
     * @param operation the operation name to set; must not be null.
     */
    public static void setOperation(String operation) {
        MDC.put(OPERATION, operation);
        log.debug("Operation set: {}", operation);
    }

    /**
     * Clears transaction-specific MDC context while preserving request-level context.
     * <p>
     * Removes transaction ID and operation from the MDC context but keeps
     * request-level information like request ID and user ID. This is typically
     * called at the end of business operations to clean up transaction-specific
     * context.
     * </p>
     */
    public static void clearTransactionContext() {
        MDC.remove(TRANSACTION_ID);
        MDC.remove(OPERATION);
        log.debug("Transaction context cleared");
    }

    /**
     * Executes a block of code with additional MDC context for a single key-value pair.
     * <p>
     * Temporarily adds the specified key-value pair to the MDC context,
     * executes the provided runnable, and then restores the original context.
     * This ensures proper context cleanup even if exceptions occur during execution.
     * </p>
     *
     * @param key the MDC key to set; must not be null.
     * @param value the MDC value to set; must not be null.
     * @param runnable the code block to execute with the additional context; must not be null.
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
     * Executes a block of code with additional MDC context from a map of key-value pairs.
     * <p>
     * Temporarily adds all key-value pairs from the provided map to the MDC context,
     * executes the provided runnable, and then restores the original context.
     * This is useful for adding multiple context values at once.
     * </p>
     *
     * @param contextMap the map of MDC key-value pairs to add; must not be null.
     * @param runnable the code block to execute with the additional context; must not be null.
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
     * Retrieves a copy of the current MDC context as a map.
     * <p>
     * Returns a snapshot of all current MDC key-value pairs. This is useful
     * for debugging, context inspection, or preserving context for async
     * operations. The returned map is a copy and modifications to it will
     * not affect the actual MDC context.
     * </p>
     *
     * @return a map containing all current MDC key-value pairs, or null if context is empty.
     */
    public static Map<String, String> getCurrentContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * Logs the current MDC context for debugging purposes.
     * <p>
     * Outputs all current MDC key-value pairs to the debug log level.
     * This is useful for troubleshooting context propagation issues
     * and verifying that expected context values are present.
     * </p>
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
