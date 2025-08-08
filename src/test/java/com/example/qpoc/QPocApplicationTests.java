package com.example.qpoc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class QPocApplicationTests {

    @Test
    void contextLoads() {
        log.info("Running context loads test");
        log.debug("Spring Boot context loaded successfully");
    }

}
