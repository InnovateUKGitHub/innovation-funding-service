#!/bin/sh

JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true -server -Djava.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib:/usr/lib/x86_64-linux-gnu/"
cd "/var/lib/tomcat8"

# tomcat proxyName and proxyPort connector settings (server.xml) expect the user-visible domain and port
idp_domain_without_port=$(echo "$IDP_DOMAIN" | cut -d: -f 1)
idp_route_port=$(echo "$IDP_DOMAIN" | grep : | cut -d: -f 2)
[ -z "$idp_route_port" ] && idp_route_port=443

# We want everything logged to stdout/stderr, so the easiest way to do this is just run java directly,
# and remove all the logging settings so everything just logs to stdout/stderr.

java $JAVA_OPTS -Didp.domain="$idp_domain_without_port" -Didp.port="$idp_route_port" \
    -Djava.endorsed.dirs=/usr/share/tomcat8/endorsed -classpath /usr/share/tomcat8/bin/bootstrap.jar:/usr/share/tomcat8/bin/tomcat-juli.jar \
    -Dcatalina.base=/var/lib/tomcat8 \
    -Dcatalina.home=/usr/share/tomcat8 \
    -Djava.io.tmpdir=/tmp/tomcat8-tomcat8-tmp \
    org.apache.catalina.startup.Bootstrap start &

cd -
