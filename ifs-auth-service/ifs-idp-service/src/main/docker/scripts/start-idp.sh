#!/bin/sh

JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true -server -Dspring.config.location=/etc/tomcat8/regapi.properties -Djava.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib:/usr/lib/x86_64-linux-gnu/"
cd "/var/lib/tomcat8"

# We want everything logged to stdout/stderr for docker, so the easiest way to do this is just run java directly in the normal docker way:
# And we remove all the logging settings so everything just logs to stdout/stderr.

java $JAVA_OPTS -Didp.domain="$IDP_DOMAIN" -Didp.port="$IDP_PORT" \
    -Djava.endorsed.dirs=/usr/share/tomcat8/endorsed -classpath /usr/share/tomcat8/bin/bootstrap.jar:/usr/share/tomcat8/bin/tomcat-juli.jar \
    -Dcatalina.base=/var/lib/tomcat8 \
    -Dcatalina.home=/usr/share/tomcat8 \
    -Djava.io.tmpdir=/tmp/tomcat8-tomcat8-tmp \
    org.apache.catalina.startup.Bootstrap start &

cd -
