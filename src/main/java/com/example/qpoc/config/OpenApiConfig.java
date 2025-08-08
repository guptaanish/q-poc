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

@Slf4j
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

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
