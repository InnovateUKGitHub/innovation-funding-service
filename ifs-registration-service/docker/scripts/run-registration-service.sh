#!/bin/sh

$JAVA_HOME/bin/keytool -import -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -noprompt -alias iuk-auth-localdev -file /var/certs/ldap-encryption.crt

exec java $JAVA_OPTS $JMX_OPTS -jar app.jar