package com.example.qpoc.swagger;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureWebMvc
class SwaggerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    void testSwaggerApiDocsEndpoint() throws Exception {
        log.info("Testing Swagger API docs endpoint");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Q-POC Spring Boot API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.paths").exists());
        
        log.debug("Swagger API docs endpoint test completed successfully");
    }

    @Test
    void testSwaggerUIRedirect() throws Exception {
        log.info("Testing Swagger UI redirect");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // The /swagger-ui.html endpoint typically redirects to /swagger-ui/index.html
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
        
        log.debug("Swagger UI redirect test completed successfully");
    }

    @Test
    void testSwaggerUIIndexEndpoint() throws Exception {
        log.info("Testing Swagger UI index endpoint");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
        
        log.debug("Swagger UI index endpoint test completed successfully");
    }

    @Test
    void testApiDocsContainsExpectedEndpoints() throws Exception {
        log.info("Testing that API docs contain expected endpoints");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/hello']").exists())
                .andExpect(jsonPath("$.paths['/api/health']").exists())
                .andExpect(jsonPath("$.paths['/api/process']").exists())
                .andExpect(jsonPath("$.paths['/api/demo-logs']").exists())
                .andExpect(jsonPath("$.paths['/api/demo-mdc']").exists())
                .andExpect(jsonPath("$.paths['/api/context-info']").exists())
                .andExpect(jsonPath("$.paths['/api/async-process']").exists())
                .andExpect(jsonPath("$.paths['/api/user/{userId}/process']").exists())
                .andExpect(jsonPath("$.paths['/api/simulate-error']").exists())
                .andExpect(jsonPath("$.paths['/api/process-validated']").exists());
        
        log.debug("API docs endpoints verification completed successfully");
    }

    @Test
    void testApiDocsContainsTags() throws Exception {
        log.info("Testing that API docs contain expected tags");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[?(@.name == 'Basic Operations')]").exists())
                .andExpect(jsonPath("$.tags[?(@.name == 'Logging Demonstration')]").exists())
                .andExpect(jsonPath("$.tags[?(@.name == 'MDC Context Management')]").exists())
                .andExpect(jsonPath("$.tags[?(@.name == 'Async Processing')]").exists())
                .andExpect(jsonPath("$.tags[?(@.name == 'Error Handling')]").exists());
        
        log.debug("API docs tags verification completed successfully");
    }

    @Test
    void testApiDocsContainsSchemas() throws Exception {
        log.info("Testing that API docs contain expected schemas");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas").exists())
                .andExpect(jsonPath("$.components.schemas.ContextInfoResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ErrorResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ProcessRequest").exists());
        
        log.debug("API docs schemas verification completed successfully");
    }

    @Test
    void testApiDocsContainsServerInfo() throws Exception {
        log.info("Testing that API docs contain server information");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servers").isArray())
                .andExpect(jsonPath("$.servers[0].url").value("http://localhost:8080"))
                .andExpect(jsonPath("$.servers[0].description").value("Local Development Server"));
        
        log.debug("API docs server info verification completed successfully");
    }

    @Test
    void testApiDocsContainsContactInfo() throws Exception {
        log.info("Testing that API docs contain contact information");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.contact.name").value("Q-POC Development Team"))
                .andExpect(jsonPath("$.info.contact.email").value("dev@example.com"))
                .andExpect(jsonPath("$.info.license.name").value("MIT License"));
        
        log.debug("API docs contact info verification completed successfully");
    }
}
