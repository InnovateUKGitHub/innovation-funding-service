#!/usr/bin/env bash
export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64
cd /opt/shibboleth-idp/bin/
./build.sh
cp ../war/idp.war /var/lib/tomcat7/webapps/

