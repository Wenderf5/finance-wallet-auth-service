FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY ./src ./src
COPY ./pom.xml ./pom.xml

RUN ["mvn", "package"]

FROM eclipse-temurin:17-jdk AS runtime

WORKDIR /app

COPY --from=build /app/target/auth-0.0.1-SNAPSHOT.jar ./auth-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "./auth-0.0.1-SNAPSHOT.jar"]
