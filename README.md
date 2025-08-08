# Q-POC Spring Boot Application

A Spring Boot application built with Java 21 and Gradle Kotlin DSL, featuring comprehensive SLF4J logging.

## Features

- Java 21
- Spring Boot 3.3.2
- Gradle Kotlin DSL
- Spring Web
- Spring Data JPA
- H2 Database (for development)
- Lombok with SLF4J logging
- Spring Boot DevTools
- JUnit 5 for testing

## Logging Features

All Java classes in this project use SLF4J logging with Lombok's `@Slf4j` annotation:

- **Application startup logging** - Main application class logs startup events
- **Controller request logging** - All REST endpoints log incoming requests and responses
- **Service layer logging** - Business logic includes appropriate log levels
- **Test logging** - Unit tests include logging for better test traceability

### Log Levels Used

- `TRACE` - Very detailed information, typically only of interest when diagnosing problems
- `DEBUG` - Detailed information on the flow through the system
- `INFO` - Interesting runtime events (startup/shutdown)
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

- `GET /api/hello` - Returns a hello message
- `GET /api/health` - Returns application health status
- `GET /api/process?input=<text>` - Processes input text (demonstrates service logging)
- `GET /api/demo-logs` - Demonstrates different log levels
- `GET /h2-console` - H2 database console (development only)

### Running Tests

```bash
./gradlew test
```

Tests include comprehensive logging to track test execution and results.

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
│   │       ├── QPocApplication.java          # Main app with startup logging
│   │       ├── controller/
│   │       │   └── HelloController.java     # REST controller with request logging
│   │       └── service/
│   │           └── LoggingService.java      # Service with business logic logging
│   └── resources/
│       └── application.properties           # Enhanced logging configuration
└── test/
    └── java/
        └── com/example/qpoc/
            ├── QPocApplicationTests.java     # Context tests with logging
            ├── controller/
            │   └── HelloControllerTest.java # Controller tests with logging
            └── service/
                └── LoggingServiceTest.java  # Service tests with logging
```

## Logging Configuration

The application uses enhanced logging configuration in `application.properties`:

- **Application logs**: DEBUG level for `com.example.qpoc` package
- **Spring Web logs**: INFO level
- **Hibernate SQL logs**: DEBUG level with parameter binding
- **Custom log patterns**: Timestamp, thread, level, logger, and message
- **Console and file patterns**: Consistent formatting

### Example Log Output

```
2025-08-08 15:41:12.123 [main] INFO  com.example.qpoc.QPocApplication - Starting Q-POC Spring Boot Application...
2025-08-08 15:41:12.456 [http-nio-8080-exec-1] INFO  c.e.q.controller.HelloController - Received request for /api/hello endpoint
2025-08-08 15:41:12.457 [http-nio-8080-exec-1] DEBUG c.e.q.controller.HelloController - Returning response: Hello from Spring Boot with Java 21!
```

## Development

This project uses:
- **SLF4J with Lombok** for clean, annotation-based logging
- **Spring Boot DevTools** for hot reloading
- **H2 console** for database inspection during development
- **Comprehensive test logging** for better debugging and traceability

### Testing Logging

To see logging in action:

1. Start the application: `./gradlew bootRun`
2. Visit `http://localhost:8080/api/demo-logs` to see different log levels
3. Check the console output for various log messages
4. Run tests with `./gradlew test` to see test logging
