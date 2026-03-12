#!/bin/bash
set -e

echo "Starting WildFly..."
/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 >/dev/null 2>&1 &
WILDFLY_PID=$!

until /opt/jboss/wildfly/bin/jboss-cli.sh --connect --commands=":whoami" >/dev/null 2>&1; do
  sleep 1
done

echo "Adding the PostgreSQL module..."
/opt/jboss/wildfly/bin/jboss-cli.sh --connect --file=/opt/jboss/wildfly/cli/add-postgre-module.cli

echo "Adding the PostgreSQL driver..."
/opt/jboss/wildfly/bin/jboss-cli.sh --connect --file=/opt/jboss/wildfly/cli/add-postgre-driver.cli

echo "Adding the PostgreSQL data source..."
/opt/jboss/wildfly/bin/jboss-cli.sh --connect --file=/opt/jboss/wildfly/cli/add-postgre-datasource.cli

echo "Restarting server..."
/opt/jboss/wildfly/bin/jboss-cli.sh --connect --command=":shutdown"
wait $WILDFLY_PID
exec /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
