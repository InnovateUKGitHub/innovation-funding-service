#!/bin/bash
set -a
# Defaults below for Oracle JDK, unless overridden (Dockerfile, etc).
[ -z "$JAVA_HOME" ] && JAVA_HOME="/usr/lib/jvm/default-java" 
source "/etc/default/tomcat8" 
CATALINA_HOME="/usr/share/tomcat8" 
CATALINA_BASE="/var/lib/tomcat8" 
JAVA_OPTS="-Djava.awt.headless=true -Xms384M -Xmx768M -server -XX:+UseConcMarkSweepGC -Dspring.config.location=/etc/tomcat8/regapi.properties -Djava.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib:/usr/lib/x86_64-linux-gnu/" 
CATALINA_PID="/var/run/tomcat8/tomcat8.pid" 
CATALINA_TMPDIR="/tmp/tomcat8-tomcat8-tmp" 
LANG="" 
[ -z "$JSSE_HOME" ] && JSSE_HOME="/usr/lib/jvm/default-java/jre/" 
cd "/var/lib/tomcat8" 

# We want everything logged to stdout/stderr for docker, so the easiest way to do this is just run java directly in the normal docker way:
# And we remove all the logging settings so everything just logs to stdout/stderr.
#/usr/share/tomcat8/bin/catalina.sh start # Old method
#/usr/lib/jvm/default-java/bin/java 

java -Djava.awt.headless=true -Xms384M -Xmx768M -server -XX:+UseConcMarkSweepGC -Dspring.config.location=/etc/tomcat8/regapi.properties -Djava.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib:/usr/lib/x86_64-linux-gnu/ -Djava.endorsed.dirs=/usr/share/tomcat8/endorsed -classpath /usr/share/tomcat8/bin/bootstrap.jar:/usr/share/tomcat8/bin/tomcat-juli.jar -Dcatalina.base=/var/lib/tomcat8 -Dcatalina.home=/usr/share/tomcat8 -Djava.io.tmpdir=/tmp/tomcat8-tomcat8-tmp org.apache.catalina.startup.Bootstrap start &
cd -
