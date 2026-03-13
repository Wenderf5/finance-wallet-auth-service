FROM maven:3.9.13-eclipse-temurin-17 AS build

WORKDIR /app

COPY ./src ./src
COPY ./pom.xml ./pom.xml

RUN ["mvn", "clean", "package"]

FROM quay.io/wildfly/wildfly:38.0.0.Final-jdk17

WORKDIR /opt/jboss/wildfly

COPY --from=build ./app/target/finance-wallet-back-end-1.0-SNAPSHOT.war ./standalone/deployments/finance-wallet-back-end-1.0-SNAPSHOT.war
COPY ./cli ./cli
COPY ./modules-lib ./modules-lib
COPY --chmod=755 ./startup.sh ./startup.sh

EXPOSE 8080 9990
ENTRYPOINT [ "./startup.sh" ]
