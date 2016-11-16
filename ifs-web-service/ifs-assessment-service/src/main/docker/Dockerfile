FROM davidcaste/alpine-java-unlimited-jce:jdk8
MAINTAINER David Soff <dsoff@worth.systems>
RUN apk --update add curl
EXPOSE 8080
EXPOSE 8081
EXPOSE 12345
VOLUME /tmp
ADD assessment-0.0.1-SNAPSHOT.war app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java", "-Xmx150m", "-jar", "/app.jar"]
HEALTHCHECK --interval=10s --timeout=5s \
  CMD curl -f http://localhost:8080/health || exit 1