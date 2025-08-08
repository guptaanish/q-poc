package com.example.qpoc.controller;

import com.example.qpoc.service.LoggingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    private LoggingService loggingService;

    @GetMapping("/hello")
    public String hello() {
        log.info("Received request for /api/hello endpoint");
        String response = "Hello from Spring Boot with Java 21!";
        log.debug("Returning response: {}", response);
        return response;
    }
    
    @GetMapping("/health")
    public String health() {
        log.info("Received request for /api/health endpoint");
        String response = "Application is running successfully!";
        log.debug("Health check response: {}", response);
        return response;
    }

    @GetMapping("/process")
    public String processData(@RequestParam(defaultValue = "test") String input) {
        log.info("Received request for /api/process endpoint with input: {}", input);
        String result = loggingService.processData(input);
        log.debug("Processing completed, returning: {}", result);
        return result;
    }

    @GetMapping("/demo-logs")
    public String demonstrateLogs() {
        log.info("Received request for /api/demo-logs endpoint");
        loggingService.demonstrateLogging();
        return "Check the logs to see different log levels in action!";
    }
}
