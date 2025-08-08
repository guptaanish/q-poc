package com.example.qpoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response with contextual information")
public class ErrorResponse {
    
    @Schema(description = "Error message", example = "An error occurred during processing")
    private String message;
    
    @Schema(description = "Request ID for tracking", example = "abc123-def456-ghi789")
    private String requestId;
    
    @Schema(description = "Error timestamp", example = "1754632612322")
    private Long timestamp;
    
    @Schema(description = "HTTP status code", example = "500")
    private Integer status;
}
