# Q-POC Spring Boot Application

A comprehensive Spring Boot application built with Java 21 and Gradle Kotlin DSL, featuring SLF4J logging with MDC (Mapped Diagnostic Context) implementation and complete Swagger/OpenAPI 3 documentation.

## Features

- Java 21
- Spring Boot 3.3.2
- Gradle Kotlin DSL
- Spring Web
- Spring Data JPA
- H2 Database (for development)
- Lombok with SLF4J logging
- **MDC (Mapped Diagnostic Context)** for request tracing
- **Swagger UI / OpenAPI 3** for API documentation and testing
- Spring Boot DevTools
- JUnit 5 for testing
- Async processing with MDC context preservation
- Global exception handling with contextual error responses

## API Documentation & Testing

This application includes comprehensive API documentation using **Swagger UI / OpenAPI 3**.

### Swagger UI Access

Once the application is running, you can access:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

### Swagger Features

- **Interactive API Testing** - Test all endpoints directly from the browser
- **Comprehensive Documentation** - Detailed descriptions, examples, and schemas
- **Request/Response Examples** - Real examples for all endpoints
- **Schema Validation** - Input validation with error examples
- **MDC Context Documentation** - Explains request tracing and context management
- **Organized by Tags** - Endpoints grouped by functionality:
  - Basic Operations
  - Logging Demonstration
  - MDC Context Management
  - Async Processing
  - Error Handling

### API Documentation Highlights

- **Contact Information** - Development team contact details
- **Server Configuration** - Local and production server URLs
- **Request/Response DTOs** - Structured data models with validation
- **Error Handling** - Standardized error responses with request tracking
- **Authentication Headers** - X-User-Id header for user context
- **Custom Headers** - X-Operation header for operation tracking

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

**Data Processing:**
- `GET /api/process?input=<text>` - Processes input text (demonstrates service logging)
- `POST /api/process-validated` - Processes data with validation (JSON body)

**Logging Demonstration:**
- `GET /api/demo-logs` - Demonstrates different log levels

**MDC Demonstration Endpoints:**
- `GET /api/demo-mdc` - Demonstrates MDC context management
- `GET /api/context-info` - Returns current MDC context information
- `POST /api/user/{userId}/process` - User-specific processing with custom context

**Async Processing:**
- `GET /api/async-process?input=<text>` - Demonstrates async processing with MDC

**Error Handling:**
- `POST /api/simulate-error?throwError=true` - Error simulation with MDC context

**Development Tools:**
- `GET /swagger-ui.html` - Interactive API documentation and testing
- `GET /api-docs` - OpenAPI 3 JSON specification
- `GET /h2-console` - H2 database console (development only)

### Testing the API

#### Using Swagger UI (Recommended)

1. **Start the application**: `./gradlew bootRun`
2. **Open Swagger UI**: `http://localhost:8080/swagger-ui.html`
3. **Test endpoints interactively** with the built-in testing interface

#### Using curl

```bash
# Basic hello endpoint
curl http://localhost:8080/api/hello

# With user context
curl -H "X-User-Id: john.doe" http://localhost:8080/api/context-info

# User-specific processing
curl -H "X-User-Id: jane.smith" -H "X-Operation: dataProcessing" \
     -X POST -d "test data" http://localhost:8080/api/user/jane.smith/process

# Validated data processing
curl -H "Content-Type: application/json" \
     -X POST -d '{"data":"hello world","options":"uppercase"}' \
     http://localhost:8080/api/process-validated

# Async processing
curl http://localhost:8080/api/async-process?input=async-test

# Error simulation
curl -X POST http://localhost:8080/api/simulate-error?throwError=true
```

### Running Tests

```bash
./gradlew test
```

Tests include comprehensive coverage:
- MDC utility functions
- Filter integration tests
- Service layer MDC context management
- Async processing context preservation
- Swagger integration tests
- Controller endpoint tests with validation

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
│   │       │   ├── MDCConfig.java               # MDC configuration for async
│   │       │   └── OpenApiConfig.java           # Swagger/OpenAPI configuration
│   │       ├── controller/
│   │       │   └── HelloController.java         # REST controller with Swagger annotations
│   │       ├── dto/
│   │       │   ├── ContextInfoResponse.java     # Response DTOs for API
│   │       │   ├── ErrorResponse.java           # Error response DTO
│   │       │   └── ProcessRequest.java          # Request DTO with validation
│   │       ├── exception/
│   │       │   └── GlobalExceptionHandler.java  # Global error handling
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
            │   └── HelloControllerTest.java     # Controller tests with validation
            ├── filter/
            │   └── MDCFilterTest.java           # MDC filter integration tests
            ├── service/
            │   └── LoggingServiceTest.java      # Service tests with MDC
            ├── swagger/
            │   └── SwaggerIntegrationTest.java  # Swagger integration tests
            └── util/
                └── MDCUtilTest.java             # MDC utility tests
```

## Configuration

### Swagger Configuration

The application uses enhanced Swagger configuration in `application.properties`:

```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
springdoc.swagger-ui.displayRequestDuration=true
springdoc.swagger-ui.persistAuthorization=true
springdoc.packages-to-scan=com.example.qpoc.controller
```

### MDC Logging Configuration

```properties
# Console logging pattern with MDC
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{requestId:-NO_REQUEST}] [%X{userId:-NO_USER}] [%X{transactionId:-}] [%X{operation:-}] %logger{36} - %msg%n

# File logging pattern with full MDC context
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{requestId:-NO_REQUEST}] [%X{userId:-NO_USER}] [%X{transactionId:-}] [%X{operation:-}] [%X{clientIp:-}] [%X{sessionId:-}] %logger{36} - %msg%n
```

## Development

This project demonstrates:
- **Production-ready API documentation** with Swagger UI
- **Interactive API testing** without external tools
- **Comprehensive request/response examples** for all endpoints
- **Input validation** with detailed error responses
- **MDC implementation** with request tracing
- **Async processing** with context preservation
- **Global exception handling** with contextual information
- **Enterprise-grade logging** practices

### Benefits of This Implementation

1. **Self-Documenting API** - Swagger UI provides live, interactive documentation
2. **Developer Experience** - Easy API testing and exploration
3. **Request Tracing** - Every log entry can be traced back to a specific request
4. **User Context** - Know which user performed which actions
5. **Transaction Tracking** - Follow business transactions across multiple components
6. **Error Handling** - Standardized error responses with request context
7. **Validation** - Input validation with clear error messages
8. **Monitoring Ready** - Better observability in production environments

This implementation follows enterprise-grade practices and is ready for production use with comprehensive API documentation, testing capabilities, and observability features.
