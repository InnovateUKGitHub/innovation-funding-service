FROM davidcaste/alpine-java-unlimited-jce:jdk8
MAINTAINER David Soff <dsoff@worth.systems>
RUN apk --update add curl
EXPOSE 8080
EXPOSE 8081
EXPOSE 9090
EXPOSE 12345
VOLUME /tmp
ADD application-service-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java", "-Xmx150m", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=8081,suspend=n", "-jar", "/app.jar"]
HEALTHCHECK --interval=10s --timeout=5s \
  CMD curl -f http://localhost:8080/health || exit 1