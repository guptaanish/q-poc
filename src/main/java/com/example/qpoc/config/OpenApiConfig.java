package com.example.qpoc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI 3.0 documentation and Swagger UI integration.
 * <p>
 * This configuration class sets up comprehensive API documentation for the Q-POC
 * application using OpenAPI 3.0 specification. It provides detailed API information,
 * server configurations, and organized tag structure for better API navigation
 * and understanding.
 * </p>
 * <p>
 * The configuration includes:
 * <ul>
 *   <li>API metadata (title, description, version, contact information)</li>
 *   <li>Server definitions for different environments</li>
 *   <li>Organized tag structure for endpoint categorization</li>
 *   <li>License information and external documentation links</li>
 * </ul>
 * </p>
 * <p>
 * The generated documentation is accessible via Swagger UI at /swagger-ui.html
 * and the OpenAPI specification is available at /v3/api-docs.
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class OpenApiConfig {

    /**
     * Server port configuration property for dynamic server URL generation.
     * <p>
     * This property is injected from the application configuration and used
     * to construct the local development server URL. Defaults to 8080 if
     * not specified in the configuration.
     * </p>
     */
    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Creates and configures the OpenAPI specification bean.
     * <p>
     * This method constructs a comprehensive OpenAPI specification including
     * API metadata, server configurations, and tag definitions. The specification
     * is used by Swagger UI to generate interactive API documentation and by
     * other tools for API discovery and client generation.
     * </p>
     * <p>
     * The configuration includes:
     * <ul>
     *   <li>Detailed API information with contact and license details</li>
     *   <li>Multiple server environments (local development and production)</li>
     *   <li>Organized tag structure for logical endpoint grouping</li>
     *   <li>Comprehensive descriptions for each API category</li>
     * </ul>
     * </p>
     *
     * @return configured OpenAPI specification instance.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Configuring OpenAPI documentation");
        
        return new OpenAPI()
                .info(new Info()
                        .title("Q-POC Spring Boot API")
                        .description("A comprehensive Spring Boot application demonstrating SLF4J logging with MDC (Mapped Diagnostic Context) implementation. " +
                                   "This API provides endpoints for testing logging functionality, MDC context management, and async processing.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Q-POC Development Team")
                                .email("dev@example.com")
                                .url("https://github.com/example/q-poc"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production Server")))
                .tags(List.of(
                        new Tag()
                                .name("Basic Operations")
                                .description("Basic application endpoints for health checks and simple operations"),
                        new Tag()
                                .name("Logging Demonstration")
                                .description("Endpoints demonstrating different logging levels and functionality"),
                        new Tag()
                                .name("MDC Context Management")
                                .description("Endpoints for demonstrating Mapped Diagnostic Context (MDC) functionality"),
                        new Tag()
                                .name("Async Processing")
                                .description("Endpoints demonstrating asynchronous processing with MDC context preservation"),
                        new Tag()
                                .name("Error Handling")
                                .description("Endpoints for testing error scenarios and exception handling")));
    }
}
