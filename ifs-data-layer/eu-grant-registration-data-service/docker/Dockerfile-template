FROM innovateuk/app-base-image
EXPOSE 8080
EXPOSE 8090
EXPOSE 8081
EXPOSE 8000
EXPOSE 12345
ENV JAVA_OPTS -Xmx300m -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n
ENV JMX_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

VOLUME /mnt/ifs_storage

RUN groupadd -g @glusterGID@ gluster && \
  useradd -p @glusterUserPassword@ -u @glusterUID@ -g @glusterGID@ gluster

RUN mkdir /tmp/ifs && \ 
  mkdir /tmp/virus-scan-holding && \ 
  mkdir /tmp/virus-scan-scanned && \ 
  chown -R gluster:gluster /tmp/ifs && \
  chown -R gluster:gluster /tmp/virus-scan* && \
  find /tmp/ifs  \( -type d -o -type f \) -exec chmod 775 {} + && \
  find /tmp/virus-scan*  \( -type d -o -type f \) -exec chmod 775 {} +

ADD newrelic.jar /
ADD newrelic.yml /
ADD coscale-monitoring.sh /root/coscale-monitoring.sh
ADD set-umask0002.sh set-umask0002.sh 

ADD @app_name@-@version@.jar app.jar

HEALTHCHECK --interval=15s --timeout=8s \
  CMD curl -f http://localhost:8080/monitoring/health || exit 1
USER gluster
ENTRYPOINT ["./set-umask0002.sh"] 
CMD ["sh", "-c", "java -Dfile.encoding=UTF8 $JAVA_OPTS $JMX_OPTS -jar app.jar"]

