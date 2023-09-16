FROM openjdk:17-jdk-slim

# Install necessary dependencies for building and running the Spring Boot app
RUN apt-get update && apt-get install -y gcc g++ python3 nodejs maven

ENV GCC_PATH /usr/bin/gcc
ENV GPP_PATH /usr/bin/g++
ENV PYTHON3_PATH /usr/bin/python3
ENV NODEJS_PATH /usr/bin/nodejs
ENV MAVEN_HOME /usr/share/maven

# Set the working directory in the container
WORKDIR /spring-boot-app

COPY pom.xml ./pom.xml
COPY src ./src

# Build the Spring Boot application
RUN mvn clean install

# Copy the generated Spring Boot application JAR file into the container
COPY target/*.jar /spring-boot-app/app.jar

# Expose the port that your Spring Boot app will run on
EXPOSE 8181

# Define the command to run your Spring Boot application
CMD ["java", "-jar", "app.jar"]

# Install necessary dependencies for building and running the Spring Boot app