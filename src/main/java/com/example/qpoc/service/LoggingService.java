package com.example.qpoc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggingService {

    public void demonstrateLogging() {
        log.trace("This is a TRACE level log message");
        log.debug("This is a DEBUG level log message");
        log.info("This is an INFO level log message");
        log.warn("This is a WARN level log message");
        log.error("This is an ERROR level log message");
    }

    public String processData(String input) {
        log.info("Processing data with input: {}", input);
        
        if (input == null || input.trim().isEmpty()) {
            log.warn("Received null or empty input");
            return "Invalid input";
        }
        
        String result = input.toUpperCase();
        log.debug("Processed result: {}", result);
        return result;
    }
}
