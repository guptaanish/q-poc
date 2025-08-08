package com.example.qpoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing current MDC context information")
public class ContextInfoResponse {
    
    @Schema(description = "Unique request identifier", example = "abc123-def456-ghi789")
    private String requestId;
    
    @Schema(description = "User identifier", example = "john.doe")
    private String userId;
    
    @Schema(description = "Complete MDC context map")
    private Map<String, String> fullMDCContext;
    
    @Schema(description = "Response timestamp", example = "1754632612322")
    private Long timestamp;
}
