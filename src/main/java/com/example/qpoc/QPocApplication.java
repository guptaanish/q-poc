package com.example.qpoc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class QPocApplication {

    public static void main(String[] args) {
        log.info("Starting Q-POC Spring Boot Application...");
        SpringApplication.run(QPocApplication.class, args);
        log.info("Q-POC Spring Boot Application started successfully!");
    }

}
