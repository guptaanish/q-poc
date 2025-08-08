package com.example.qpoc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Q-POC demonstration project.
 * <p>
 * This application demonstrates comprehensive SLF4J logging with MDC (Mapped Diagnostic Context)
 * implementation, including request tracing, contextual logging, and async processing with
 * context preservation. The application provides REST endpoints for testing various logging
 * scenarios and MDC functionality.
 * </p>
 * <p>
 * Key features include:
 * <ul>
 *   <li>SLF4J logging with Lombok @Slf4j annotation</li>
 *   <li>MDC context management for request tracing</li>
 *   <li>Swagger UI integration for API documentation</li>
 *   <li>Global exception handling with contextual error responses</li>
 *   <li>Async processing with MDC context preservation</li>
 * </ul>
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@SpringBootApplication
public class QPocApplication {

    /**
     * Main entry point for the Spring Boot application.
     * <p>
     * Initializes the Spring application context and starts the embedded web server.
     * Logs application startup and completion events for monitoring purposes.
     * </p>
     *
     * @param args command line arguments passed to the application.
     */
    public static void main(String[] args) {
        log.info("Starting Q-POC Spring Boot Application...");
        SpringApplication.run(QPocApplication.class, args);
        log.info("Q-POC Spring Boot Application started successfully!");
    }
}
