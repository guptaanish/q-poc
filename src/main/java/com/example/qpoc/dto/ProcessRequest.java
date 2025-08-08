package com.example.qpoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for data processing operation requests.
 * <p>
 * This request DTO encapsulates the data and options required for processing
 * operations. It includes comprehensive validation annotations to ensure
 * data integrity and provides clear error messages when validation fails.
 * The DTO is used by endpoints that perform data transformation and
 * processing operations.
 * </p>
 * <p>
 * The request supports both required data fields and optional processing
 * parameters, allowing for flexible processing configurations while
 * maintaining data validation standards.
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for data processing operations")
public class ProcessRequest {
    
    /**
     * The data to be processed by the processing operation.
     * <p>
     * This field is required and must not be blank. It has a maximum
     * length constraint to prevent processing of excessively large
     * data sets that could impact system performance. The data will
     * be processed according to the specified options or default
     * processing rules.
     * </p>
     */
    @NotBlank(message = "Data cannot be blank")
    @Size(max = 1000, message = "Data cannot exceed 1000 characters")
    @Schema(description = "Data to be processed", example = "Hello World", required = true)
    private String data;
    
    /**
     * Optional processing configuration parameters.
     * <p>
     * This field allows clients to specify how the data should be processed.
     * Common options include transformation types (e.g., "uppercase", "lowercase"),
     * formatting options, or other processing directives. If not specified,
     * default processing behavior will be applied.
     * </p>
     */
    @Schema(description = "Processing options", example = "uppercase")
    private String options;
}
