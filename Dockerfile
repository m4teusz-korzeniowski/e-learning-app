FROM maven:3.9.9-eclipse-temurin-17-alpine as MAVEN_BUILD
COPY ./pom.xml ./pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B
COPY ./src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM eclipse-temurin:17-alpine
EXPOSE 8080
COPY --from=MAVEN_BUILD /target/e-learning-app-*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
