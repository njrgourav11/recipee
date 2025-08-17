# Technical Specifications - Recipe Management System

## API Specifications

### Backend REST API Endpoints

#### 1. Load Recipes from External API
```http
POST /api/recipes/load
Content-Type: application/json

Response:
{
  "message": "Recipes loaded successfully",
  "count": 50,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 2. Search Recipes
```http
GET /api/recipes/search?q={query}&page={page}&size={size}
Content-Type: application/json

Parameters:
- q (required): Search query for recipe name and cuisine
- page (optional): Page number (default: 0)
- size (optional): Page size (default: 20)

Response:
{
  "content": [
    {
      "id": 1,
      "name": "Classic Margherita Pizza",
      "cuisine": "Italian",
      "difficulty": "Easy",
      "prepTimeMinutes": 20,
      "cookTimeMinutes": 15,
      "servings": 4,
      "ingredients": ["Pizza dough", "Tomato sauce", "Mozzarella"],
      "instructions": ["Step 1", "Step 2"],
      "tags": ["Pizza", "Italian", "Vegetarian"],
      "image": "https://example.com/image.jpg",
      "rating": 4.5,
      "reviewCount": 120,
      "caloriesPerServing": 285
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": false,
      "unsorted": true
    }
  },
  "totalElements": 45,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

#### 3. Get Recipe by ID
```http
GET /api/recipes/{id}
Content-Type: application/json

Response:
{
  "id": 1,
  "name": "Classic Margherita Pizza",
  "cuisine": "Italian",
  "difficulty": "Easy",
  "prepTimeMinutes": 20,
  "cookTimeMinutes": 15,
  "servings": 4,
  "ingredients": ["Pizza dough", "Tomato sauce", "Mozzarella"],
  "instructions": ["Step 1", "Step 2"],
  "tags": ["Pizza", "Italian", "Vegetarian"],
  "image": "https://example.com/image.jpg",
  "rating": 4.5,
  "reviewCount": 120,
  "caloriesPerServing": 285
}

Error Response (404):
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Recipe not found with id: 999",
  "path": "/api/recipes/999"
}
```

## Data Models

### Backend Entity Model

```java
@Entity
@Table(name = "recipes")
@Indexed
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @FullTextField(analyzer = "standard")
    @Column(nullable = false)
    private String name;
    
    @FullTextField(analyzer = "standard")
    @Column(nullable = false)
    private String cuisine;
    
    @Column(nullable = false)
    private String difficulty;
    
    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;
    
    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;
    
    private Integer servings;
    
    @ElementCollection
    @CollectionTable(name = "recipe_ingredients")
    private List<String> ingredients;
    
    @ElementCollection
    @CollectionTable(name = "recipe_instructions")
    private List<String> instructions;
    
    @ElementCollection
    @CollectionTable(name = "recipe_tags")
    private List<String> tags;
    
    private String image;
    private Double rating;
    
    @Column(name = "review_count")
    private Integer reviewCount;
    
    @Column(name = "calories_per_serving")
    private Integer caloriesPerServing;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### Frontend TypeScript Models

```typescript
export interface Recipe {
  id: number;
  name: string;
  cuisine: string;
  difficulty: 'Easy' | 'Medium' | 'Hard';
  prepTimeMinutes: number;
  cookTimeMinutes: number;
  servings: number;
  ingredients: string[];
  instructions: string[];
  tags: string[];
  image: string;
  rating: number;
  reviewCount: number;
  caloriesPerServing: number;
}

export interface SearchResponse {
  content: Recipe[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
    };
  };
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface LoadingState {
  isLoading: boolean;
  error: string | null;
}

export interface SearchFilters {
  sortBy: 'cookTimeMinutes' | 'rating' | 'name';
  sortOrder: 'asc' | 'desc';
  tags: string[];
}
```

## Implementation Details

### Backend Configuration

#### Maven Dependencies (pom.xml)
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Hibernate Search -->
    <dependency>
        <groupId>org.hibernate.search</groupId>
        <artifactId>hibernate-search-mapper-orm</artifactId>
        <version>6.2.2.Final</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate.search</groupId>
        <artifactId>hibernate-search-backend-lucene</artifactId>
        <version>6.2.2.Final</version>
    </dependency>
    
    <!-- Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>
    
    <!-- Resilience -->
    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-aspects</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### Application Configuration (application.yml)
```yaml
spring:
  application:
    name: recipe-management-system
  
  datasource:
    url: jdbc:h2:mem:recipedb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        search:
          backend:
            directory:
              root: target/lucene-indexes
  
  jackson:
    property-naming-strategy: SNAKE_CASE

external:
  api:
    recipes:
      url: https://dummyjson.com/recipes
      timeout: 5000
      retry:
        max-attempts: 3
        delay: 1000

logging:
  level:
    com.publicis.recipes: DEBUG
    org.hibernate.search: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{correlationId}] %logger{36} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

### Frontend Configuration

#### Package.json Dependencies
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "typescript": "^5.0.0",
    "axios": "^1.6.0",
    "react-router-dom": "^6.8.0",
    "react-intersection-observer": "^9.5.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "@vitejs/plugin-react": "^4.0.0",
    "vite": "^4.4.0",
    "sass": "^1.69.0",
    "@testing-library/react": "^13.4.0",
    "@testing-library/jest-dom": "^5.16.0",
    "jest": "^29.7.0",
    "eslint": "^8.45.0",
    "@typescript-eslint/eslint-plugin": "^6.0.0"
  }
}
```

## Performance Requirements

### Backend Performance
- API response time: < 200ms for search queries
- Database query optimization with proper indexing
- Connection pooling for database connections
- Caching for external API responses (TTL: 1 hour)

### Frontend Performance
- First Contentful Paint: < 1.5s
- Largest Contentful Paint: < 2.5s
- Time to Interactive: < 3.5s
- Image lazy loading with intersection observer
- Code splitting for route-based chunks

## Security Requirements

### Backend Security
- Input validation using Bean Validation
- SQL injection prevention with JPA
- CORS configuration for frontend domain
- Rate limiting: 100 requests per minute per IP
- Secure headers (HSTS, X-Frame-Options, etc.)

### Frontend Security
- XSS prevention with proper escaping
- Content Security Policy headers
- Secure API communication over HTTPS
- Input sanitization for search queries

## Testing Requirements

### Backend Testing Coverage
- Unit Tests: 85% minimum coverage
- Integration Tests: All repository methods
- API Tests: All endpoints with various scenarios
- Performance Tests: Load testing with 100 concurrent users

### Frontend Testing Coverage
- Component Tests: 80% minimum coverage
- Integration Tests: User interaction flows
- E2E Tests: Critical user journeys
- Accessibility Tests: WCAG 2.1 AA compliance

## Monitoring and Observability

### Backend Monitoring
- Application metrics with Micrometer
- Custom metrics for search performance
- Health checks for external API dependency
- Structured logging with correlation IDs

### Frontend Monitoring
- Error tracking and reporting
- Performance monitoring (Core Web Vitals)
- User interaction analytics
- API call success/failure rates

This technical specification provides the detailed blueprint for implementing the recipe management system with all required features and quality standards.