# Modern Web Application

A modern Spring Boot application built with Kotlin, featuring a responsive web interface and REST API endpoints.

## 🚀 Features

- **Modern Tech Stack**: Spring Boot 3.5.3, Kotlin 2.2.10, Java 21 LTS
- **Responsive UI**: Bootstrap 5.3.3 with modern design
- **REST API**: JSON endpoints with timestamp support
- **Health Monitoring**: Spring Boot Actuator for application health
- **Live Development**: Spring Boot DevTools for automatic reload
- **Interactive HTTP Debugging**: Client-side HTTP request/response visualization
- **Containerization**: Docker support with multi-stage builds
- **Comprehensive Testing**: Unit, integration, and MVC tests
- **Modern Kotlin**: Constructor injection, data classes, and modern syntax

## 🛠️ Technology Stack

---

### Backend

#### **Spring Boot 3.5.3**
- A powerful framework for building Java-based web applications and microservices.
- Provides production-ready features like embedded servers, metrics, and security.
- Offers auto-configuration to minimize boilerplate code.
- Integrates seamlessly with databases, messaging queues, and cloud services.

---

### Language

#### **Kotlin 2.2.10**
- A modern, concise, and expressive programming language for the JVM.
- Fully interoperable with Java, allowing gradual adoption in Java projects.
- Offers features like null safety, coroutines, and extension functions.
- Reduces boilerplate compared to Java, increasing developer productivity.

---

### Java Platform

#### **Java 21 LTS (Long-Term Support)**
- The latest long-term support release of Java.
- Brings performance improvements and new language features (e.g., pattern matching, record patterns, virtual threads).
- Ensures long-term stability and security updates for enterprise applications.

---

### Frontend

#### **Bootstrap 5.3.3**
- A popular CSS framework for responsive, mobile-first frontends.
- Provides ready-to-use components like buttons, modals, and grids.
- Eliminates the need to write extensive custom CSS for common UI patterns.

#### **Thymeleaf**
- A server-side Java template engine for rendering HTML.
- Integrates seamlessly with Spring Boot for dynamic page generation.
- Allows embedding logic directly in HTML templates using natural syntax.
- Great for building MVC applications with server-rendered views.

---

### Build Tool

#### **Gradle 9.0.0**
- A modern build automation tool for Java, Kotlin, and other languages.
- Uses a **Groovy** or **Kotlin DSL** for build configuration.
- Offers high performance with incremental builds and build caching.
- Widely supported in the Spring Boot ecosystem.

---

### Testing

#### **JUnit 5**
- The standard testing framework for Java and Kotlin.
- Provides annotations like `@Test`, `@BeforeEach`, and `@AfterEach` for structured testing.
- Supports parameterized tests and extensions for advanced use cases.

#### **AssertJ**
- A fluent assertion library for Java/Kotlin tests.
- Offers human-readable assertions like  
  ```java
  assertThat(actual).isEqualTo(expected);
- Improves test readability and debugging compared to plain JUnit assertions.

#### **MockMvc**
- A Spring testing utility for simulating HTTP requests and responses.
- Enables testing of Spring MVC controllers without starting a full server.
- Useful for verifying API endpoints and ensuring correct request/response behavior.

---

### Added technologies
#### **Spring Security**
- A powerful and customizable security framework for Spring applications.  
- Provides authentication, authorization, CSRF protection, and session management.  
- Integrates seamlessly with Spring Boot, reducing boilerplate when implementing security features.

#### BCrypt 
- A password-hashing function designed for secure storage of credentials.  
- Resistant to brute-force attacks due to its adaptive complexity factor ("work factor").  
- Widely used as a best practice for storing user passwords.

#### Bucket4j 
- A Java library for **rate limiting** based on the token-bucket algorithm.  
- Prevents abuse of APIs by controlling the number of requests per user or client over time.  
- Flexible, with support for distributed environments (e.g., via Redis, Hazelcast).

#### Swagger / OpenAPI 
- A specification and toolset for designing, building, and documenting REST APIs.  
- Generates interactive API documentation (Swagger UI), allowing developers to test endpoints directly.  
- Widely adopted, making APIs easier to understand and consume.

#### Mockito 
- A mocking framework for unit testing in Java.  
- Allows developers to create mock objects to simulate dependencies.  
- Makes it easier to test components in isolation without relying on real implementations.

---

## 📋 Prerequisites

- Java 21 or higher
- Gradle 9.0.0 or higher
- Docker (optional)

## 🏃‍♂️ Quick Start

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd template-lab1-git-race
   ```

2. **Build the application**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**
   - Web Interface: http://localhost:8080
   - API Endpoint: http://localhost:8080/api/hello
   - Health Check: http://localhost:8080/actuator/health

### Using Docker for Development

1. **Using Docker Compose** (Recommended):
   ```bash
   docker-compose -f docker-compose.dev.yml up --build
   ```

2. **Build and run development container**:
   ```bash
   docker build -f Dockerfile.dev -t modern-web-app-dev .
   docker run -p 8080:8080 -p 35729:35729 -v $(pwd):/app modern-web-app-dev
   ```

The development Docker setup includes:
- **LiveReload Support**: Automatic browser refresh on code changes
- **Volume Mounting**: Source code changes are immediately reflected
- **Spring Boot DevTools**: Automatic application restart on file changes
- **Health Monitoring**: Built-in health checks via Spring Boot Actuator

## 🧪 Testing

Run all tests:
```bash
./gradlew test
```

Run specific test classes:
```bash
./gradlew test --tests "HelloControllerUnitTests"
./gradlew test --tests "IntegrationTest"
```

## 📡 API Endpoints

### Web Endpoints & REST API Endpoints
- Documented with Swagger/OpenAPI in the endpoint /swagger-ui/index.html

### Monitoring Endpoints
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Interactive HTTP Debugging
- **Web Page Testing**: Test the main page with personalized greetings
- **API Testing**: Test REST endpoints with real-time request/response display
- **Health Check Testing**: Monitor application health status
- **Live Reload**: Spring Boot DevTools automatically reloads on file changes

## 🏗️ Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   ├── configuration/              # OpenApi and Security configurations
│   │   │   └── ...      
│   │   ├── controller/                 # Web and API controllers
│   │   │   └── ...  
│   │   ├── entity/                     # Domain entities
│   │   │   └── ...  
│   │   ├── enum/                       # Enum data classes
│   │   │   └── ...  
│   │   ├── filter/                     # Rate limit filter
│   │   │   └── ...  
│   │   ├── repository/                 # Repositories for data persistence
│   │   │   └── ...  
│   │   ├── response/                   # API responses data classes
│   │   │   └── ...  
│   │   ├── service/                    # Domain services for using repositories
│   │   │   └── ...  
│   │   └── HelloWorld.kt               # Main application class
│   └── resources/
│       ├── application.properties      # Application configuration
│       ├── templates/                  # Thymeleaf templates
│       │   ├── login.html
│       │   ├── signup.html
│       │   └── welcome.html           
│       └── public/
│           └── assets/
│               └── logo.svg            # Application logo
└── test/
    └── kotlin/
        ├── integration/                # Integration tests
        │   └── ...
        ├── slice/                      # Slice tests
        │   ├── controller/             # MVC tests
        │   │   └── ...
        │   └── repository/             # JPA tests
        │       └── ...
        └── unit/                       # Unit tests
            ├── controller/                    
            │   └── ...
            ├── entity/                    
            │   └── ...
            └── service/                    
                └── ...
```

## ⚙️ Configuration

Key configuration options in `application.properties`:

```properties
# Application settings
spring.application.name=modern-web-app
server.port=8080

# Custom message
app.message=Welcome to the Modern Web App!

# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics
```

## 🐳 Docker Details

The application includes a development-focused Docker setup:

- **Development Dockerfile**: Uses JDK 21 Alpine for development with live reload
- **Docker Compose**: Orchestrates the development environment with volume mounting
- **LiveReload**: Spring Boot DevTools automatically reloads on file changes
- **Volume Mounting**: Source code changes are immediately reflected in the container
- **Health Checks**: Built-in health monitoring via Spring Boot Actuator
- **Development Tools**: Includes wget for health checks and debugging utilities

## 📊 Monitoring

The application includes Spring Boot Actuator for monitoring:

- **Health**: Application and dependency health status
- **Info**: Application metadata and build information
- **Metrics**: JVM and application metrics

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆕 What's New in This Modern Version

- ✅ Upgraded to Java 21 LTS for better performance
- ✅ Modern Kotlin syntax with constructor injection
- ✅ Separated web and API controllers for better organization
- ✅ Added comprehensive test coverage
- ✅ Implemented Spring Boot Actuator for monitoring
- ✅ Created responsive Bootstrap 5.3.3 UI
- ✅ Added Docker support with multi-stage builds
- ✅ Fixed Bootstrap version inconsistencies
- ✅ Enhanced error handling and validation
- ✅ Added interactive features and API endpoints
