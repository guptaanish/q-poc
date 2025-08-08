package com.example.qpoc.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter for automatically managing MDC context for HTTP requests.
 * <p>
 * This filter is executed at the beginning of each HTTP request to establish
 * MDC context that will be available throughout the entire request processing
 * lifecycle. It extracts relevant information from the HTTP request and
 * populates the MDC with contextual data for logging purposes.
 * </p>
 * <p>
 * The filter automatically sets the following MDC values:
 * <ul>
 *   <li>requestId - Unique identifier for request tracking</li>
 *   <li>userId - User identifier from headers or "anonymous" if not present</li>
 *   <li>sessionId - HTTP session identifier</li>
 *   <li>clientIp - Client IP address with proxy header support</li>
 *   <li>userAgent - Browser/client user agent string (truncated if too long)</li>
 * </ul>
 * </p>
 * <p>
 * The filter ensures proper cleanup by clearing all MDC context at the end
 * of request processing, preventing context leakage between requests.
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(1)
public class MDCFilter implements Filter {

    /**
     * MDC key for storing the unique request identifier.
     */
    private static final String REQUEST_ID = "requestId";

    /**
     * MDC key for storing the user identifier.
     */
    private static final String USER_ID = "userId";

    /**
     * MDC key for storing the HTTP session identifier.
     */
    private static final String SESSION_ID = "sessionId";

    /**
     * MDC key for storing the client IP address.
     */
    private static final String CLIENT_IP = "clientIp";

    /**
     * MDC key for storing the client user agent string.
     */
    private static final String USER_AGENT = "userAgent";

    /**
     * Processes each HTTP request to establish and manage MDC context.
     * <p>
     * This method is called for every HTTP request and performs the following operations:
     * <ol>
     *   <li>Generates a unique request ID for tracking</li>
     *   <li>Extracts user information from request headers</li>
     *   <li>Captures session ID, client IP, and user agent</li>
     *   <li>Sets all extracted information in MDC context</li>
     *   <li>Logs request start and completion</li>
     *   <li>Ensures MDC cleanup after request processing</li>
     * </ol>
     * </p>
     *
     * @param request the servlet request being processed.
     * @param response the servlet response being generated.
     * @param chain the filter chain for continuing request processing.
     * @throws IOException if an I/O error occurs during request processing.
     * @throws ServletException if a servlet error occurs during request processing.
     */
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
    
    /**
     * Extracts the real client IP address from the HTTP request.
     * <p>
     * This method handles various proxy scenarios by checking multiple headers
     * in order of preference to determine the actual client IP address.
     * It supports common proxy headers used in load balancer and reverse
     * proxy configurations.
     * </p>
     * <p>
     * The method checks headers in the following order:
     * <ol>
     *   <li>X-Forwarded-For (takes first IP if comma-separated list)</li>
     *   <li>X-Real-IP</li>
     *   <li>Remote address from servlet request</li>
     * </ol>
     * </p>
     *
     * @param request the HTTP servlet request to extract IP from.
     * @return the client IP address, never null.
     */
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
