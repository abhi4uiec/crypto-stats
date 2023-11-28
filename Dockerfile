# Use the OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim
ENV APP_HOME=/usr/app/

MAINTAINER agupt

# Set the working directory in the container
WORKDIR $APP_HOME

# Copy the Gradle wrapper files to the container
COPY gradlew $APP_HOME
COPY gradle $APP_HOME/gradle

# Copy the build configuration files to the container
COPY settings.gradle $APP_HOME
COPY build.gradle $APP_HOME

# Copy the source code to the container
COPY src $APP_HOME/src

# Run the Gradle wrapper to download and install Gradle (Optional)
#RUN ./gradlew --version

# Build the Spring Boot application
RUN ./gradlew build

# Expose the default Spring Boot port
EXPOSE 9090

# Set the default command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "build/libs/crypto-0.0.1-SNAPSHOT.jar"]