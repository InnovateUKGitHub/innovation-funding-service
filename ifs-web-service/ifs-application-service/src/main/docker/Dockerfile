FROM davidcaste/debian-oracle-java:jdk8
MAINTAINER David Soff <dsoff@worth.systems>
## RUN apk --update add curl
RUN apt-get update
RUN apt-get install -y curl
EXPOSE 8009
EXPOSE 8000
EXPOSE 8080
EXPOSE 8849
VOLUME /tmp
COPY jprofiler_agent_linux-x64 /opt/jprofiler_agent_linux-x64
ADD ifs-application-service-1.0-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java", "-Xmx150m", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n", "-agentpath:/opt/jprofiler_agent_linux-x64/bin/linux-x64/libjprofilerti.so=port=8849,nowait", "-jar", "/app.jar", "--logging.file=/tmp/log.log"]
HEALTHCHECK --interval=10s --timeout=5s \
  CMD curl -f http://localhost:8080/health || exit 1