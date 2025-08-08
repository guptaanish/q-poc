package com.example.qpoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Data Transfer Object representing current MDC context information.
 * <p>
 * This response DTO encapsulates comprehensive information about the current
 * Mapped Diagnostic Context (MDC) state, including request tracking information,
 * user context, and the complete MDC context map. It is used by endpoints
 * that provide context inspection and debugging capabilities.
 * </p>
 * <p>
 * The response includes both specific context fields (request ID, user ID)
 * and the complete MDC context map for comprehensive context visibility.
 * This is particularly useful for debugging context propagation issues
 * and verifying that expected context values are present.
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing current MDC context information")
public class ContextInfoResponse {
    
    /**
     * Unique identifier for the current HTTP request.
     * <p>
     * This ID is generated at the beginning of each request and remains
     * consistent throughout the request processing lifecycle. It enables
     * correlation of all log entries related to a specific request.
     * </p>
     */
    @Schema(description = "Unique request identifier", example = "abc123-def456-ghi789")
    private String requestId;
    
    /**
     * Identifier of the user associated with the current request.
     * <p>
     * This may be extracted from authentication headers, JWT tokens,
     * or other authentication mechanisms. Defaults to "anonymous"
     * for unauthenticated requests.
     * </p>
     */
    @Schema(description = "User identifier", example = "john.doe")
    private String userId;
    
    /**
     * Complete map of all current MDC context key-value pairs.
     * <p>
     * This provides a comprehensive view of all context information
     * currently available in the MDC, including both standard fields
     * (request ID, user ID, session ID) and any custom context values
     * that may have been added during request processing.
     * </p>
     */
    @Schema(description = "Complete MDC context map")
    private Map<String, String> fullMDCContext;
    
    /**
     * Timestamp when this context information response was generated.
     * <p>
     * Represents the time in milliseconds since epoch when this response
     * object was created. Useful for understanding the temporal context
     * of the MDC state snapshot.
     * </p>
     */
    @Schema(description = "Response timestamp", example = "1754632612322")
    private Long timestamp;
}
