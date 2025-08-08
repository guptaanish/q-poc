package com.example.qpoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for data processing operations")
public class ProcessRequest {
    
    @NotBlank(message = "Data cannot be blank")
    @Size(max = 1000, message = "Data cannot exceed 1000 characters")
    @Schema(description = "Data to be processed", example = "Hello World", required = true)
    private String data;
    
    @Schema(description = "Processing options", example = "uppercase")
    private String options;
}
