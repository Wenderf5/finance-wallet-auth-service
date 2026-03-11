FROM quay.io/wildfly/wildfly:38.0.0.Final-jdk17

WORKDIR /opt/jboss/wildfly

COPY ./cli ./cli
COPY ./modules-lib ./modules-lib
COPY ./target/finance-wallet-back-end-1.0-SNAPSHOT.war ./standalone/deployments/finance-wallet-back-end-1.0-SNAPSHOT.war
COPY --chmod=755 ./startup.sh ./startup.sh

EXPOSE 8080 9990
ENTRYPOINT [ "./startup.sh" ]
