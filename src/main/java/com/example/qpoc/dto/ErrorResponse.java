package com.example.qpoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for standardized error responses with contextual information.
 * <p>
 * This response DTO provides a consistent structure for all error responses
 * throughout the application. It includes essential error information along
 * with contextual data from the MDC to enable effective error tracking and
 * debugging. The standardized format ensures that clients can reliably
 * parse and handle error responses.
 * </p>
 * <p>
 * The error response includes both human-readable error messages and
 * technical information such as request IDs for correlation with server
 * logs. This dual approach supports both end-user error handling and
 * technical debugging scenarios.
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response with contextual information")
public class ErrorResponse {
    
    /**
     * Human-readable error message describing what went wrong.
     * <p>
     * This message is intended for display to end users or for logging
     * purposes. It should be descriptive enough to understand the nature
     * of the error without exposing sensitive system information or
     * implementation details.
     * </p>
     */
    @Schema(description = "Error message", example = "An error occurred during processing")
    private String message;
    
    /**
     * Unique request identifier for correlating this error with server logs.
     * <p>
     * This ID matches the request ID in the MDC context and server logs,
     * enabling developers and support teams to quickly locate relevant
     * log entries for debugging purposes. The request ID is automatically
     * populated from the current MDC context.
     * </p>
     */
    @Schema(description = "Request ID for tracking", example = "abc123-def456-ghi789")
    private String requestId;
    
    /**
     * Timestamp when the error occurred.
     * <p>
     * Represents the time in milliseconds since epoch when the error
     * response was generated. This timestamp helps with temporal
     * correlation of errors and can be useful for debugging
     * time-sensitive issues.
     * </p>
     */
    @Schema(description = "Error timestamp", example = "1754632612322")
    private Long timestamp;
    
    /**
     * HTTP status code associated with this error.
     * <p>
     * Provides the numeric HTTP status code that corresponds to this
     * error condition. This allows clients to programmatically handle
     * different types of errors based on standard HTTP status codes
     * (e.g., 400 for client errors, 500 for server errors).
     * </p>
     */
    @Schema(description = "HTTP status code", example = "500")
    private Integer status;
}
