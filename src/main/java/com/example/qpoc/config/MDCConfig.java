package com.example.qpoc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Configuration class for MDC-aware asynchronous processing setup.
 * <p>
 * This configuration class enables asynchronous method execution with proper
 * MDC (Mapped Diagnostic Context) context preservation across thread boundaries.
 * It provides custom task executors and decorators that ensure logging context
 * is maintained when processing moves to background threads.
 * </p>
 * <p>
 * The configuration addresses the challenge of maintaining request traceability
 * in asynchronous operations by automatically capturing and restoring MDC
 * context in async threads. This is crucial for maintaining consistent
 * logging context throughout the entire request processing lifecycle.
 * </p>
 * <p>
 * Key features include:
 * <ul>
 *   <li>Custom TaskDecorator for MDC context preservation</li>
 *   <li>Configured ThreadPoolTaskExecutor with MDC support</li>
 *   <li>Automatic context cleanup after async execution</li>
 *   <li>Comprehensive logging of executor configuration</li>
 * </ul>
 * </p>
 *
 * @author Q-POC Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableAsync
public class MDCConfig {

    /**
     * Custom TaskDecorator implementation that preserves MDC context across async method calls.
     * <p>
     * This decorator captures the current thread's MDC context before async execution
     * and restores it in the async thread. It ensures that all logging within async
     * methods maintains the same contextual information as the originating request.
     * </p>
     * <p>
     * The decorator follows a capture-restore-cleanup pattern:
     * <ol>
     *   <li>Captures current MDC context from the calling thread</li>
     *   <li>Restores the context in the async execution thread</li>
     *   <li>Executes the async task with proper context</li>
     *   <li>Cleans up MDC context after execution</li>
     * </ol>
     * </p>
     *
     * @author Q-POC Development Team
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class MDCTaskDecorator implements TaskDecorator {
        
        /**
         * Decorates a Runnable to preserve MDC context during async execution.
         * <p>
         * This method wraps the provided Runnable with MDC context management
         * logic. The returned Runnable will execute with the same MDC context
         * that was present in the calling thread, ensuring consistent logging
         * context across thread boundaries.
         * </p>
         *
         * @param runnable the original Runnable to be executed asynchronously.
         * @return a decorated Runnable that preserves MDC context.
         */
        @Override
        public Runnable decorate(Runnable runnable) {
            // Capture the current MDC context
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            
            return () -> {
                try {
                    // Set the MDC context in the async thread
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    // Clear MDC context after execution
                    MDC.clear();
                }
            };
        }
    }

    /**
     * Configures and provides an MDC-aware async task executor.
     * <p>
     * This method creates a ThreadPoolTaskExecutor configured with appropriate
     * pool sizes and the custom MDCTaskDecorator for context preservation.
     * The executor is optimized for typical web application async processing
     * needs while ensuring proper MDC context management.
     * </p>
     * <p>
     * Executor configuration:
     * <ul>
     *   <li>Core pool size: 5 threads for baseline capacity</li>
     *   <li>Max pool size: 10 threads for peak load handling</li>
     *   <li>Queue capacity: 100 tasks for request buffering</li>
     *   <li>Thread naming: "MDC-Async-" prefix for easy identification</li>
     *   <li>Task decoration: MDCTaskDecorator for context preservation</li>
     * </ul>
     * </p>
     *
     * @return configured Executor instance with MDC context preservation.
     */
    @Bean(name = "mdcTaskExecutor")
    public Executor mdcTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MDC-Async-");
        executor.setTaskDecorator(new MDCTaskDecorator());
        executor.initialize();
        
        log.info("MDC-aware task executor configured with core pool size: {}, max pool size: {}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        
        return executor;
    }
}
