package com.example.qpoc.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
public class MDCFilter implements Filter {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String SESSION_ID = "sessionId";
    private static final String CLIENT_IP = "clientIp";
    private static final String USER_AGENT = "userAgent";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        try {
            // Generate unique request ID
            String requestId = UUID.randomUUID().toString();
            MDC.put(REQUEST_ID, requestId);
            
            // Extract user information (in real app, this would come from authentication)
            String userId = httpRequest.getHeader("X-User-Id");
            if (userId == null) {
                userId = "anonymous";
            }
            MDC.put(USER_ID, userId);
            
            // Extract session ID
            String sessionId = httpRequest.getSession().getId();
            MDC.put(SESSION_ID, sessionId);
            
            // Extract client IP
            String clientIp = getClientIpAddress(httpRequest);
            MDC.put(CLIENT_IP, clientIp);
            
            // Extract User-Agent
            String userAgent = httpRequest.getHeader("User-Agent");
            if (userAgent != null && userAgent.length() > 50) {
                userAgent = userAgent.substring(0, 50) + "...";
            }
            MDC.put(USER_AGENT, userAgent != null ? userAgent : "unknown");
            
            log.info("Request started: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            
            // Continue with the request
            chain.doFilter(request, response);
            
            log.info("Request completed: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            
        } finally {
            // Always clear MDC after request processing
            MDC.clear();
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
