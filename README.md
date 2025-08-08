# Q-POC Spring Boot Application

A Spring Boot application built with Java 21 and Gradle Kotlin DSL.

## Features

- Java 21
- Spring Boot 3.3.2
- Gradle Kotlin DSL
- Spring Web
- Spring Data JPA
- H2 Database (for development)
- Lombok
- Spring Boot DevTools
- JUnit 5 for testing

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
- `GET /h2-console` - H2 database console (development only)

### Running Tests

```bash
./gradlew test
```

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
│   │       ├── QPocApplication.java
│   │       └── controller/
│   │           └── HelloController.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/example/qpoc/
            ├── QPocApplicationTests.java
            └── controller/
                └── HelloControllerTest.java
```

## Configuration

The application uses H2 in-memory database for development. Configuration can be found in `src/main/resources/application.properties`.

## Development

This project uses:
- Spring Boot DevTools for hot reloading
- Lombok for reducing boilerplate code
- H2 console for database inspection during development
