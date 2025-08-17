# Recipe Management System

A full-stack web application for searching and managing recipes with a Spring Boot backend and React TypeScript frontend.

## 🚀 Features

### Backend Features
- **RESTful API** with Spring Boot 3.x and Java 17+
- **Full-text search** using Hibernate Search with Lucene indexing
- **In-memory H2 database** for fast data access
- **External API integration** with resilience patterns (retry, circuit breaker)
- **Comprehensive validation** and exception handling
- **Swagger/OpenAPI documentation** for API endpoints
- **Unit testing** with high code coverage
- **Structured logging** with correlation IDs

### Frontend Features
- **React 18+ with TypeScript** for type-safe development
- **Responsive design** with mobile-first approach
- **Global search** with debounced input (3+ characters)
- **Client-side sorting** by cook time in ascending/descending order
- **Client-side filtering** by recipe tags
- **Lazy loading** for images and components
- **Single Page Application** (SPA) architecture
- **Atomic design** component structure

## 🏗️ Architecture

```
Recipe Management System
├── Backend (Spring Boot)
│   ├── REST API Endpoints
│   ├── Hibernate Search Integration
│   ├── H2 In-Memory Database
│   └── External API Client
└── Frontend (React TypeScript)
    ├── Global Search Interface
    ├── Recipe Grid Display
    ├── Client-side Sorting/Filtering
    └── Responsive Design
```

## 📋 API Endpoints

### Recipe Operations
- `POST /api/recipes/load` - Load recipes from external API
- `GET /api/recipes/search?q={query}` - Search recipes by name/cuisine
- `GET /api/recipes/{id}` - Get specific recipe by ID

### Documentation
- `GET /swagger-ui.html` - Interactive API documentation
- `GET /v3/api-docs` - OpenAPI specification

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: H2 In-Memory
- **Search**: Hibernate Search 6.x + Lucene
- **Documentation**: SpringDoc OpenAPI 3
- **Testing**: JUnit 5, Mockito
- **Build**: Maven

### Frontend
- **Framework**: React 18+
- **Language**: TypeScript
- **Build Tool**: Vite
- **Styling**: SCSS + CSS Modules
- **HTTP Client**: Axios
- **Testing**: Jest, React Testing Library

## 🚦 Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Maven 3.8+
- npm or yarn

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd publicis
   ```

2. **Start the backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   Backend will be available at: http://localhost:8080

3. **Start the frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   Frontend will be available at: http://localhost:3000

4. **Load recipe data**
   ```bash
   curl -X POST http://localhost:8080/api/recipes/load
   ```

### Development Setup

#### Backend Development
```bash
cd backend

# Run tests
mvn test

# Run with coverage
mvn test jacoco:report

# Build JAR
mvn clean package

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Frontend Development
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Build for production
npm run build

# Preview production build
npm run preview
```

## 📊 Project Structure

```
publicis/
├── backend/                    # Spring Boot Application
│   ├── src/main/java/
│   │   └── com/publicis/recipes/
│   │       ├── RecipeApplication.java
│   │       ├── config/         # Configuration classes
│   │       ├── controller/     # REST Controllers
│   │       ├── service/        # Business Logic
│   │       ├── repository/     # Data Access Layer
│   │       ├── entity/         # JPA Entities
│   │       ├── dto/            # Data Transfer Objects
│   │       ├── exception/      # Custom Exceptions
│   │       └── external/       # External API clients
│   ├── src/main/resources/
│   │   ├── application.yml     # Configuration
│   │   └── logback-spring.xml  # Logging config
│   ├── src/test/java/          # Unit Tests
│   └── pom.xml                 # Maven dependencies
├── frontend/                   # React TypeScript Application
│   ├── public/                 # Static assets
│   ├── src/
│   │   ├── components/         # React Components
│   │   │   ├── atoms/          # Basic UI elements
│   │   │   ├── molecules/      # Composite components
│   │   │   ├── organisms/      # Complex components
│   │   │   └── templates/      # Page layouts
│   │   ├── pages/              # Page components
│   │   ├── services/           # API Services
│   │   ├── hooks/              # Custom React Hooks
│   │   ├── types/              # TypeScript Definitions
│   │   ├── utils/              # Utility Functions
│   │   └── styles/             # Global styles
│   ├── package.json            # npm dependencies
│   └── tsconfig.json           # TypeScript config
├── docs/                       # Documentation
│   ├── ARCHITECTURE.md         # System architecture
│   ├── TECHNICAL_SPECIFICATIONS.md
│   └── IMPLEMENTATION_GUIDE.md
└── README.md                   # This file
```

## 🔍 Usage Examples

### Search Recipes
```bash
# Search for pizza recipes
curl "http://localhost:8080/api/recipes/search?q=pizza"

# Search with pagination
curl "http://localhost:8080/api/recipes/search?q=italian&page=0&size=10"
```

### Get Recipe by ID
```bash
curl "http://localhost:8080/api/recipes/1"
```

### Frontend Usage
1. Open http://localhost:3000
2. Enter search query (minimum 3 characters)
3. Press Enter or click Search button
4. Use sorting dropdown to sort by cook time
5. Use tag filters to filter results

## 🧪 Testing

### Backend Testing
- **Unit Tests**: Service layer, repository layer, controllers
- **Integration Tests**: Full API endpoint testing
- **Coverage**: Minimum 85% code coverage required

```bash
# Run all tests
mvn test

# Generate coverage report
mvn test jacoco:report
# Report available at: target/site/jacoco/index.html
```

### Frontend Testing
- **Component Tests**: All React components
- **Integration Tests**: User interaction flows
- **Coverage**: Minimum 80% code coverage required

```bash
# Run all tests
npm test

# Run with coverage
npm run test:coverage
# Report available at: coverage/lcov-report/index.html
```

## 📈 Performance

### Backend Performance
- API response time: < 200ms for search queries
- Database queries optimized with proper indexing
- Connection pooling for database connections
- Caching for external API responses

### Frontend Performance
- First Contentful Paint: < 1.5s
- Largest Contentful Paint: < 2.5s
- Time to Interactive: < 3.5s
- Code splitting for optimal loading
- Image lazy loading

## 🔒 Security

- Input validation and sanitization
- CORS configuration for cross-origin requests
- Rate limiting for API endpoints
- Secure headers configuration
- XSS prevention measures

## 📝 API Documentation

Interactive API documentation is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## 🐛 Troubleshooting

### Common Issues

1. **Backend fails to start**
   - Check Java version (requires Java 17+)
   - Verify port 8080 is available
   - Check application logs for errors

2. **Frontend fails to start**
   - Check Node.js version (requires 18+)
   - Run `npm install` to install dependencies
   - Verify port 3000 is available

3. **Search returns no results**
   - Ensure recipes are loaded: `POST /api/recipes/load`
   - Check H2 console: http://localhost:8080/h2-console
   - Verify search query is at least 3 characters

4. **CORS errors**
   - Check backend CORS configuration
   - Verify frontend is running on http://localhost:3000

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the documentation in the `docs/` folder
- Review the implementation guide for detailed technical information

---

**Built with ❤️ using Spring Boot and React**