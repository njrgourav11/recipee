# Use a base image with Java and Maven
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Download dependencies (to leverage Docker layer caching)
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the source code
COPY src src

# Run the package command
RUN ./mvnw clean package -DskipTests

# ... (rest of your Dockerfile)



# Stage 2: Create the final image
FROM openjdk:17-slim-buster
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
