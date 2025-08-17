# Recipe Management Backend

A Spring Boot REST API for recipe management with full-text search capabilities using Hibernate Search and H2 in-memory database.

## 🚀 Features

- **Full-text search** with Hibernate Search and Lucene indexing
- **External API integration** with resilience patterns (retry, circuit breaker)
- **H2 in-memory database** for fast data access and testing
- **Comprehensive validation** and exception handling
- **Swagger/OpenAPI documentation** for interactive API testing
- **Asynchronous data loading** from external sources
- **Structured logging** with correlation IDs
- **High test coverage** with unit and integration tests

## 🛠️ Technology Stack

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Hibernate Search 6.2.2** with Lucene backend
- **H2 Database** (in-memory)
- **SpringDoc OpenAPI 3** for API documentation
- **Maven** for build management
- **JUnit 5** and **Mockito** for testing

## 📋 API Endpoints

### Recipe Operations
- `GET /api/recipes/search?q={query}` - Search recipes by name/cuisine
- `GET /api/recipes/{id}` - Get specific recipe by ID
- `GET /api/recipes/cuisine/{cuisine}` - Get recipes by cuisine
- `GET /api/recipes/top-rated` - Get top-rated recipes
- `GET /api/recipes/suggestions?q={query}` - Get search suggestions
- `GET /api/recipes/statistics` - Get recipe statistics

### Data Management
- `POST /api/recipes/load` - Load recipes from external API
- `GET /api/recipes/load/status` - Get data loading status

### Documentation
- `GET /swagger-ui.html` - Interactive API documentation
- `GET /v3/api-docs` - OpenAPI specification JSON

### Monitoring
- `GET /actuator/health` - Health check endpoint
- `GET /actuator/metrics` - Application metrics
- `GET /h2-console` - H2 database console (dev only)

## 🚦 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Running the Application

1. **Clone and navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Run with Maven**
   ```bash
   mvn spring-boot:run
   ```

3. **Or build and run JAR**
   ```bash
   mvn clean package
   java -jar target/recipe-management-backend-1.0.0.jar
   ```

4. **Access the application**
   - API Base URL: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

### Loading Sample Data

Load recipes from external API:
```bash
curl -X POST http://localhost:8080/api/recipes/load
```

Check loading status:
```bash
curl http://localhost:8080/api/recipes/load/status
```

## 🔍 API Usage Examples

### Search Recipes
```bash
# Basic search
curl "http://localhost:8080/api/recipes/search?q=pizza"

# Search with pagination
curl "http://localhost:8080/api/recipes/search?q=italian&page=0&size=10"

# Search with sorting
curl "http://localhost:8080/api/recipes/search?q=pasta&sort=rating&direction=desc"
```

### Get Recipe by ID
```bash
curl "http://localhost:8080/api/recipes/1"
```

### Get Recipes by Cuisine
```bash
curl "http://localhost:8080/api/recipes/cuisine/Italian"
```

### Get Search Suggestions
```bash
curl "http://localhost:8080/api/recipes/suggestions?q=piz&limit=5"
```

### Get Statistics
```bash
curl "http://localhost:8080/api/recipes/statistics"
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

### View Coverage Report
Open `target/site/jacoco/index.html` in your browser.

### Run Integration Tests
```bash
mvn verify
```

## 📊 Project Structure

```
backend/
├── src/main/java/com/publicis/recipes/
│   ├── RecipeApplication.java          # Main application class
│   ├── config/                         # Configuration classes
│   │   ├── HibernateSearchConfig.java  # Search configuration
│   │   ├── OpenApiConfig.java          # Swagger configuration
│   │   └── WebConfig.java              # Web and CORS configuration
│   ├── controller/                     # REST Controllers
│   │   └── RecipeController.java       # Recipe API endpoints
│   ├── dto/                            # Data Transfer Objects
│   │   ├── RecipeDto.java              # Recipe DTO
│   │   └── RecipeMapper.java           # Entity-DTO mapper
│   ├── entity/                         # JPA Entities
│   │   └── Recipe.java                 # Recipe entity with search annotations
│   ├── exception/                      # Custom Exceptions
│   │   ├── GlobalExceptionHandler.java # Global exception handler
│   │   ├── RecipeNotFoundException.java
│   │   ├── DataLoadingException.java
│   │   └── SearchServiceException.java
│   ├── external/                       # External API clients
│   │   ├── ExternalRecipeService.java  # External API service
│   │   └── ExternalApiException.java   # External API exception
│   ├── repository/                     # Data Access Layer
│   │   ├── RecipeRepository.java       # JPA repository
│   │   └── RecipeSearchRepository.java # Hibernate Search repository
│   └── service/                        # Business Logic
│       ├── RecipeService.java          # Recipe business logic
│       └── DataLoadingService.java     # Data loading service
├── src/main/resources/
│   ├── application.yml                 # Application configuration
│   └── logback-spring.xml             # Logging configuration
├── src/test/java/                      # Test classes
└── pom.xml                            # Maven dependencies
```

## ⚙️ Configuration

### Application Profiles

- **default**: Basic configuration
- **dev**: Development with debug logging
- **test**: Test configuration with test database
- **prod**: Production configuration

### Key Configuration Properties

```yaml
# Database
spring.datasource.url: jdbc:h2:mem:recipedb
spring.h2.console.enabled: true

# Hibernate Search
spring.jpa.properties.hibernate.search.backend.directory.root: target/lucene-indexes

# External API
external.api.recipes.url: https://dummyjson.com/recipes
external.api.recipes.timeout: 5000
external.api.recipes.retry.max-attempts: 3

# Server
server.port: 8080

# Logging
logging.level.com.publicis.recipes: DEBUG
```

## 🔧 Development

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comprehensive JavaDoc comments
- Maintain consistent indentation (4 spaces)

### Adding New Features

1. **Create entity** (if needed) in `entity/` package
2. **Add repository** methods in `repository/` package
3. **Implement service** logic in `service/` package
4. **Create controller** endpoints in `controller/` package
5. **Add DTOs** for API contracts in `dto/` package
6. **Write tests** for all layers
7. **Update documentation**

### Database Schema

The application uses H2 in-memory database with the following main table:

```sql
CREATE TABLE recipes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    cuisine VARCHAR(100) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    prep_time_minutes INTEGER,
    cook_time_minutes INTEGER,
    servings INTEGER,
    image VARCHAR(500),
    rating DECIMAL(3,2),
    review_count INTEGER,
    calories_per_serving INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Additional tables for collections
CREATE TABLE recipe_ingredients (
    recipe_id BIGINT,
    ingredient VARCHAR(500)
);

CREATE TABLE recipe_instructions (
    recipe_id BIGINT,
    instruction VARCHAR(1000),
    step_order INTEGER
);

CREATE TABLE recipe_tags (
    recipe_id BIGINT,
    tag VARCHAR(50)
);
```

## 🐛 Troubleshooting

### Common Issues

1. **Application fails to start**
   - Check Java version (requires 17+)
   - Verify port 8080 is available
   - Check application logs for errors

2. **Search not working**
   - Ensure Hibernate Search index is built
   - Check if recipes are loaded in database
   - Verify Lucene index directory permissions

3. **External API errors**
   - Check internet connectivity
   - Verify external API URL is accessible
   - Review retry configuration

4. **H2 Console not accessible**
   - Ensure `spring.h2.console.enabled=true`
   - Check if running in development profile
   - Verify URL: http://localhost:8080/h2-console

### Logging

Application logs are available at:
- Console output (development)
- `logs/recipe-management.log` (production)

Log levels can be adjusted in `application.yml`:
```yaml
logging:
  level:
    com.publicis.recipes: DEBUG
    org.hibernate.search: INFO
```

## 📈 Performance

### Optimization Features
- Connection pooling for database
- Hibernate Search indexing for fast queries
- Async processing for data loading
- Caching for external API responses
- Batch processing for large datasets

### Monitoring
- Actuator endpoints for health checks
- JVM metrics and application metrics
- Custom metrics for search performance
- Structured logging with correlation IDs

## 🔒 Security

- Input validation with Bean Validation
- SQL injection prevention with JPA
- CORS configuration for frontend
- Secure headers configuration
- Error message sanitization

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

---

**Built with ❤️ using Spring Boot and Java 17**