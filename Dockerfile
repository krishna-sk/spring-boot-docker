# Use the OpenJDK 17 base image
FROM openjdk:17-jdk-slim

RUN apt-get update && \
    apt-get install -y curl iputils-ping && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/spring-boot-docker*.jar /app/spring-boot-docker.jar

# Expose the application port
EXPOSE 8080

# Command to run the JAR
ENTRYPOINT ["java", "-jar", "spring-boot-docker.jar"]