# Recipe Management System - Running Instructions

## 🚀 Quick Start Guide

The Recipe Management System consists of two main components:
- **Backend**: Spring Boot REST API (Java 17+)
- **Frontend**: React TypeScript application

## ✅ Prerequisites

### Required Software
- **Java 17 or higher** - [Download here](https://adoptium.net/)
- **Maven 3.8+** - [Download here](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [Download here](https://nodejs.org/)
- **npm or yarn** - Comes with Node.js

### Verify Installation
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Node.js version
node -version

# Check npm version
npm -version
```

## ⚠️ Troubleshooting

### Java Not Found Error
If you encounter "JAVA_HOME not found" or "'java' is not recognized" errors:

1. **Install Java 17+** from [Adoptium](https://adoptium.net/temurin/releases/)
2. **Set JAVA_HOME environment variable**:
   - Windows: Add `JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot` to system environment variables
   - Add `%JAVA_HOME%\bin` to your PATH
3. **Restart your terminal/command prompt**
4. **Verify installation**: `java -version`

### Maven Wrapper Issues
If Maven wrapper fails, ensure:
- All wrapper files are present: `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `.mvn/wrapper/maven-wrapper.jar`
- Internet connection is available for dependency downloads
- JAVA_HOME is properly set

## 🎯 Running the Application

### Step 1: Start the Backend (Spring Boot)

1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Install dependencies and run**
   ```bash
   # Option 1: Using Maven wrapper (recommended)
   ./mvnw spring-boot:run

   # Option 2: Using installed Maven
   mvn spring-boot:run

   # Option 3: Build and run JAR
   mvn clean package
   java -jar target/recipe-management-backend-1.0.0.jar
   ```

3. **Verify backend is running**
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

### Step 2: Start the Frontend (React)

1. **Open a new terminal and navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm run dev
   ```

4. **Access the application**
   - Frontend: http://localhost:5173
   - The application will automatically open in your browser

## 📊 Loading Sample Data

Once both servers are running, load sample recipe data:

### Method 1: Using the Frontend
1. Open http://localhost:5173
2. The application will show an error initially (no data loaded)
3. Use the search functionality - it will guide you to load data

### Method 2: Using API directly
```bash
# Load recipes from external API
curl -X POST http://localhost:8080/api/recipes/load

# Check loading status
curl http://localhost:8080/api/recipes/load/status

# Verify data is loaded
curl "http://localhost:8080/api/recipes/search?q=pizza"
```

### Method 3: Using Swagger UI
1. Open http://localhost:8080/swagger-ui.html
2. Find the "POST /api/recipes/load" endpoint
3. Click "Try it out" and "Execute"

## 🔍 Testing the Application

### Frontend Testing
1. **Search Functionality**
   - Enter at least 3 characters in the search box
   - Try searches like "pizza", "italian", "chicken"
   - Results should appear with recipe cards

2. **Responsive Design**
   - Resize your browser window
   - Test on mobile devices
   - All components should adapt properly

### Backend Testing
1. **API Endpoints**
   ```bash
   # Search recipes
   curl "http://localhost:8080/api/recipes/search?q=pizza"
   
   # Get specific recipe
   curl "http://localhost:8080/api/recipes/1"
   
   # Get statistics
   curl "http://localhost:8080/api/recipes/statistics"
   ```

2. **Database Console**
   - Open http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:recipedb`
   - Username: `sa`
   - Password: `password`

## 🛠️ Development Commands

### Backend Commands
```bash
cd backend

# Run tests
mvn test

# Run with coverage
mvn test jacoco:report

# Build without tests
mvn clean package -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Commands
```bash
cd frontend

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run tests (when implemented)
npm test

# Type checking
npm run type-check
```

## 🐛 Troubleshooting

### Common Issues

#### Backend Issues

1. **Port 8080 already in use**
   ```bash
   # Find process using port 8080
   netstat -ano | findstr :8080
   
   # Kill the process (replace PID)
   taskkill /PID <PID> /F
   ```

2. **Java version issues**
   ```bash
   # Check Java version
   java -version
   
   # Set JAVA_HOME if needed
   set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.x
   ```

3. **Maven not found**
   - Install Maven or use the Maven wrapper (`./mvnw`)
   - Add Maven to your PATH environment variable

#### Frontend Issues

1. **Port 5173 already in use**
   - Vite will automatically try the next available port
   - Or specify a different port: `npm run dev -- --port 3000`

2. **Node.js version issues**
   ```bash
   # Check Node.js version
   node -version
   
   # Update Node.js if needed
   # Download from https://nodejs.org/
   ```

3. **API connection issues**
   - Ensure backend is running on http://localhost:8080
   - Check the `.env` file in frontend directory
   - Verify CORS settings in backend

#### Data Loading Issues

1. **External API not accessible**
   - Check internet connection
   - The app uses https://dummyjson.com/recipes
   - Try loading data manually via Swagger UI

2. **Search returns no results**
   - Ensure data is loaded first
   - Check H2 console to verify data exists
   - Try different search terms

## 📱 Application Features

### Current Features ✅
- **Global search bar** with debounced input
- **Recipe grid display** with responsive design
- **Full-text search** powered by Hibernate Search
- **Error handling** and loading states
- **Mobile-responsive** design
- **Real-time search** as you type (after 3 characters)

### Planned Features 🚧
- Client-side sorting by cook time, rating
- Tag-based filtering
- Recipe details modal
- Favorites functionality
- Advanced search filters

## 🔧 Configuration

### Backend Configuration
- **Database**: H2 in-memory (auto-configured)
- **Search**: Hibernate Search with Lucene
- **External API**: dummyjson.com/recipes
- **Profiles**: dev, test, prod

### Frontend Configuration
- **API URL**: Configured in `.env` file
- **Build Tool**: Vite
- **Styling**: SCSS with CSS Modules
- **TypeScript**: Strict mode enabled

## 📚 Additional Resources

- **Backend README**: `backend/README.md`
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Architecture Guide**: `docs/ARCHITECTURE.md`
- **Technical Specs**: `docs/TECHNICAL_SPECIFICATIONS.md`

## 🆘 Getting Help

If you encounter issues:

1. **Check the logs**
   - Backend: Console output or `logs/recipe-management.log`
   - Frontend: Browser developer console

2. **Verify prerequisites**
   - Java 17+, Maven 3.8+, Node.js 18+

3. **Check port availability**
   - Backend: 8080
   - Frontend: 5173

4. **Review configuration**
   - Backend: `application.yml`
   - Frontend: `.env`

---

**🎉 Enjoy exploring the Recipe Management System!**