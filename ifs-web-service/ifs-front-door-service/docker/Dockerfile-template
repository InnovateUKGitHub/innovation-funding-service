FROM innovateuk/app-base-image
EXPOSE 8009
EXPOSE 8000
EXPOSE 8080
ENV JAVA_OPTS -Xmx300m -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n
ENV JMX_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
VOLUME /tmp

ADD newrelic.jar /
ADD newrelic.yml /
ADD coscale-monitoring.sh /root/coscale-monitoring.sh
ADD @app_name@-@version@.jar app.jar

HEALTHCHECK --interval=15s --timeout=8s \
CMD curl -f http://localhost:8080@server_context@/monitoring/health || exit 1
ENTRYPOINT exec java -Dfile.encoding=UTF8 $JAVA_OPTS $JMX_OPTS -jar app.jar
