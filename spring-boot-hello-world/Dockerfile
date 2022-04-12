FROM openjdk:8-jdk-alpine

# Set working directory
RUN mkdir spring-app
WORKDIR /spring-app

# Set args
ARG JAR_FILE=spring-boot-hello-world/target/*.jar

# Copy jar into workdir
COPY ${JAR_FILE} app.jar

# Start the app
ENTRYPOINT ["java","-jar","app.jar"]