# Q-POC Spring Boot Application

A Spring Boot application built with Java 21 and Gradle Kotlin DSL, featuring comprehensive SLF4J logging with MDC (Mapped Diagnostic Context) implementation.

## Features

- Java 21
- Spring Boot 3.3.2
- Gradle Kotlin DSL
- Spring Web
- Spring Data JPA
- H2 Database (for development)
- Lombok with SLF4J logging
- **MDC (Mapped Diagnostic Context)** for request tracing
- Spring Boot DevTools
- JUnit 5 for testing
- Async processing with MDC context preservation

## MDC (Mapped Diagnostic Context) Implementation

This application implements comprehensive MDC support following best practices from the [Baeldung MDC guide](https://www.baeldung.com/mdc-in-log4j-2-logback).

### MDC Features

**Automatic Request Context:**
- `requestId` - Unique UUID for each HTTP request
- `userId` - User identifier (from X-User-Id header or "anonymous")
- `sessionId` - HTTP session identifier
- `clientIp` - Client IP address (supports X-Forwarded-For)
- `userAgent` - User agent string (truncated if > 50 chars)

**Business Context:**
- `transactionId` - Unique identifier for business transactions
- `operation` - Current business operation being performed

**Async Support:**
- MDC context is preserved across async method calls
- Custom TaskExecutor with MDC context propagation

### MDC Components

1. **MDCFilter** - Servlet filter that automatically sets request-level context
2. **MDCUtil** - Utility class for managing MDC context programmatically
3. **MDCConfig** - Configuration for async processing with MDC preservation
4. **Enhanced logging patterns** - Log format includes MDC values

## Logging Features

All Java classes in this project use SLF4J logging with Lombok's `@Slf4j` annotation:

- **Application startup logging** - Main application class logs startup events
- **Request/response logging** - All REST endpoints log with MDC context
- **Service layer logging** - Business logic includes transaction tracking
- **Test logging** - Unit tests include logging for better test traceability
- **MDC context logging** - All logs include contextual information

### Log Format with MDC

```
2025-08-08 15:57:35.123 [http-nio-8080-exec-1] INFO [abc123-def456] [user123] [tx789] [processData] c.e.q.controller.HelloController - Processing request
```

Where:
- `[abc123-def456]` = Request ID
- `[user123]` = User ID
- `[tx789]` = Transaction ID
- `[processData]` = Current operation

### Log Levels Used

- `TRACE` - Very detailed information, typically only of interest when diagnosing problems
- `DEBUG` - Detailed information on the flow through the system
- `INFO` - Interesting runtime events (startup/shutdown, requests)
- `WARN` - Use of deprecated APIs, poor use of API, 'almost' errors
- `ERROR` - Runtime errors or unexpected conditions

## Getting Started

### Prerequisites

- Java 21 or higher
- No need to install Gradle (uses Gradle Wrapper)

### Running the Application

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Available Endpoints

**Basic Endpoints:**
- `GET /api/hello` - Returns a hello message
- `GET /api/health` - Returns application health status
- `GET /api/process?input=<text>` - Processes input text (demonstrates service logging)
- `GET /api/demo-logs` - Demonstrates different log levels

**MDC Demonstration Endpoints:**
- `GET /api/demo-mdc` - Demonstrates MDC context management
- `GET /api/context-info` - Returns current MDC context information
- `GET /api/async-process?input=<text>` - Demonstrates async processing with MDC
- `POST /api/user/{userId}/process` - User-specific processing with custom context
- `POST /api/simulate-error?throwError=true` - Error simulation with MDC context

**Development:**
- `GET /h2-console` - H2 database console (development only)

### Testing MDC

To see MDC in action:

1. **Start the application**: `./gradlew bootRun`
2. **Make requests with custom headers**:
   ```bash
   curl -H "X-User-Id: john.doe" http://localhost:8080/api/context-info
   curl -H "X-User-Id: jane.smith" -H "X-Operation: dataProcessing" \
        -X POST -d "test data" http://localhost:8080/api/user/jane.smith/process
   ```
3. **Check logs** to see contextual information in every log entry
4. **Test async processing**: `curl http://localhost:8080/api/async-process?input=async-test`

### Running Tests

```bash
./gradlew test
```

Tests include comprehensive MDC testing:
- MDC utility functions
- Filter integration tests
- Service layer MDC context management
- Async processing context preservation

### Building for Production

```bash
./gradlew bootJar
```

The executable JAR will be created in `build/libs/`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/qpoc/
│   │       ├── QPocApplication.java              # Main app with startup logging
│   │       ├── config/
│   │       │   └── MDCConfig.java               # MDC configuration for async
│   │       ├── controller/
│   │       │   └── HelloController.java         # REST controller with MDC
│   │       ├── filter/
│   │       │   └── MDCFilter.java               # Servlet filter for request context
│   │       ├── service/
│   │       │   └── LoggingService.java          # Service with business logic logging
│   │       └── util/
│   │           └── MDCUtil.java                 # MDC utility functions
│   └── resources/
│       └── application.properties               # Enhanced logging with MDC patterns
└── test/
    └── java/
        └── com/example/qpoc/
            ├── QPocApplicationTests.java         # Context tests with logging
            ├── controller/
            │   └── HelloControllerTest.java     # Controller tests with logging
            ├── filter/
            │   └── MDCFilterTest.java           # MDC filter integration tests
            ├── service/
            │   └── LoggingServiceTest.java      # Service tests with MDC
            └── util/
                └── MDCUtilTest.java             # MDC utility tests
```

## MDC Configuration

The application uses enhanced logging configuration in `application.properties`:

```properties
# Console logging pattern with MDC
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{requestId:-NO_REQUEST}] [%X{userId:-NO_USER}] [%X{transactionId:-}] [%X{operation:-}] %logger{36} - %msg%n

# File logging pattern with full MDC context
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{requestId:-NO_REQUEST}] [%X{userId:-NO_USER}] [%X{transactionId:-}] [%X{operation:-}] [%X{clientIp:-}] [%X{sessionId:-}] %logger{36} - %msg%n
```

### MDC Usage Examples

**Automatic Request Context (via MDCFilter):**
```java
// Every HTTP request automatically gets:
// - requestId: unique UUID
// - userId: from X-User-Id header or "anonymous"
// - sessionId: HTTP session ID
// - clientIp: client IP address
// - userAgent: browser/client information
```

**Manual Business Context:**
```java
// Set transaction context
MDCUtil.setTransactionId("tx-12345");
MDCUtil.setOperation("userDataProcessing");

// Use context in a block
MDCUtil.withContext("businessProcess", "validation", () -> {
    log.info("Executing validation"); // Includes all context
});

// Clear transaction context (keeps request context)
MDCUtil.clearTransactionContext();
```

**Async Processing:**
```java
@Async("mdcTaskExecutor")
public CompletableFuture<String> processAsync(String data) {
    // MDC context is automatically preserved
    log.info("Processing in async thread"); // Still has request context
    return CompletableFuture.completedFuture(result);
}
```

## Development

This project demonstrates:
- **Production-ready MDC implementation** with request tracing
- **Async processing** with context preservation
- **Comprehensive testing** of MDC functionality
- **Best practices** for structured logging
- **Spring Boot integration** with filters and configuration
- **Error handling** with contextual information

### Benefits of MDC Implementation

1. **Request Tracing**: Every log entry can be traced back to a specific request
2. **User Context**: Know which user performed which actions
3. **Transaction Tracking**: Follow business transactions across multiple components
4. **Debugging**: Easily filter logs by request ID, user, or transaction
5. **Monitoring**: Better observability in production environments
6. **Async Support**: Context preserved across thread boundaries

This implementation follows enterprise-grade logging practices and is ready for production use.
