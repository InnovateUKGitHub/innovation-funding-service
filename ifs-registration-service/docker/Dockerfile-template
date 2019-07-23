FROM innovateuk/openjdk
EXPOSE 8009
EXPOSE 8000
EXPOSE 8080

ENV JAVA_OPTS -Xmx300m
ENV JMX_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

# LDAP-specific environment variables
ENV SHIBBOLETH_LDAP_BASE_DN="dc=nodomain" \
    SHIBBOLETH_LDAP_PPOLICY_ATTRIB="cn=PPolicy,ou=Policies" \
    SHIBBOLETH_LDAP_USER="cn=admin,dc=nodomain" \
    LDAP_PASSWORD="default" \
    LDAP_URL="ldaps://ldap:8389" \
    SHIBBOLETH_LDAP_PORT="8389" \
    SHIBBOLETH_LDAP_REQUIRE_VALID_P_POLICY="true"

VOLUME /tmp

ADD newrelic.jar /
ADD newrelic.yml /
ADD coscale-monitoring.sh /root/coscale-monitoring.sh
ADD @app_name@-@version@.jar app.jar

COPY ldap-encryption.crt /ldap-encryption.crt
RUN $JAVA_HOME/bin/keytool -import -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -noprompt -alias iuk-auth-localdev -file /ldap-encryption.crt

HEALTHCHECK --interval=15s --timeout=8s \
  CMD curl -f http://localhost:8080/monitoring/health || exit 1
ENTRYPOINT exec java $JAVA_OPTS $JMX_OPTS -jar app.jar

