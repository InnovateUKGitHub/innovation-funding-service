#!/usr/bin/env bash
umask 0002
java -Dfile.encoding=UTF8 $JAVA_OPTS $JMX_OPTS -jar app.jar
