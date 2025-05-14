FROM maven:3.9.9-eclipse-temurin-17-aplpine as MAVEN_BUILD
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn package

FROM eclipse-temurin:17-alpine
EXPOSE 8080
COPY --from=MAVEN_BUILD /target/e-learning-app-*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]