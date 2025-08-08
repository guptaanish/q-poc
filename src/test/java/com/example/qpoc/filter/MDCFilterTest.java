package com.example.qpoc.filter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
class MDCFilterTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Build MockMvc with all filters including MDCFilter
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new MDCFilter())
                .build();
    }

    @Test
    void testMDCFilterWithGetRequest() throws Exception {
        log.info("Testing MDC filter with GET request");
        
        mockMvc.perform(get("/api/hello")
                .header("X-User-Id", "test-user-123")
                .header("User-Agent", "Test-Agent/1.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from Spring Boot with Java 21!"));
        
        log.debug("MDC filter GET request test completed");
    }

    @Test
    void testMDCFilterWithPostRequest() throws Exception {
        log.info("Testing MDC filter with POST request");
        
        mockMvc.perform(post("/api/user/test-user/process")
                .header("X-User-Id", "test-user-456")
                .header("X-Operation", "testOperation")
                .header("User-Agent", "Test-Agent/2.0")
                .content("test data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Processed data for user: test-user"));
        
        log.debug("MDC filter POST request test completed");
    }

    @Test
    void testMDCFilterWithContextInfo() throws Exception {
        log.info("Testing MDC filter with context info endpoint");
        
        mockMvc.perform(get("/api/context-info")
                .header("X-User-Id", "context-test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.userId").value("context-test-user"))
                .andExpect(jsonPath("$.fullMDCContext").exists())
                .andExpect(jsonPath("$.timestamp").exists());
        
        log.debug("MDC filter context info test completed");
    }

    @Test
    void testMDCFilterWithAnonymousUser() throws Exception {
        log.info("Testing MDC filter with anonymous user");
        
        mockMvc.perform(get("/api/context-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.userId").value("anonymous"))
                .andExpect(jsonPath("$.fullMDCContext").exists());
        
        log.debug("MDC filter anonymous user test completed");
    }

    @Test
    void testMDCFilterWithDemoMDCEndpoint() throws Exception {
        log.info("Testing MDC filter with demo MDC endpoint");
        
        mockMvc.perform(get("/api/demo-mdc")
                .header("X-User-Id", "demo-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.userId").value("demo-user"))
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.clientIp").exists());
        
        log.debug("MDC filter demo endpoint test completed");
    }

    @Test
    void testMDCFilterBasicFunctionality() throws Exception {
        log.info("Testing basic MDC filter functionality");
        
        mockMvc.perform(get("/api/health")
                .header("X-User-Id", "health-check-user")
                .header("User-Agent", "Health-Check-Agent/1.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application is running successfully!"));
        
        log.debug("Basic MDC filter functionality test completed");
    }

    @Test
    void testMDCFilterWithLongUserAgent() throws Exception {
        log.info("Testing MDC filter with long User-Agent header");
        
        String longUserAgent = "Very-Long-User-Agent-String-That-Exceeds-Fifty-Characters-And-Should-Be-Truncated";
        
        mockMvc.perform(get("/api/hello")
                .header("X-User-Id", "long-agent-user")
                .header("User-Agent", longUserAgent))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from Spring Boot with Java 21!"));
        
        log.debug("Long User-Agent MDC filter test completed");
    }
}
