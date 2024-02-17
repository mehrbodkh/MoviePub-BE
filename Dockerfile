# Use a base image with the JVM and Gradle
FROM gradle:8.4.0-jdk21 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle build files
COPY build.gradle.kts settings.gradle.kts gradle.properties /app/

# Copy the source code
COPY src /app/src

# Build the application
RUN gradle assemble

# Use a base image with the JVM
FROM openjdk:21

# Set environmental variables
ENV TMDB_API_KEY=write-api-key-here

# Set the working directory in the container
WORKDIR /app

# Copy the assembled JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Expose the port that your app runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]