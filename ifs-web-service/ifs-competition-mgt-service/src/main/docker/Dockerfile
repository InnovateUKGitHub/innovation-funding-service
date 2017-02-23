FROM davidcaste/alpine-java-unlimited-jce:jdk8
MAINTAINER David Soff <dsoff@worth.systems>
RUN apk --update add curl
EXPOSE 8009
EXPOSE 8000
EXPOSE 8080
ENV JAVA_OPTS -Xmx150m
VOLUME /tmp
ADD management-1.0-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
HEALTHCHECK --interval=10s --timeout=5s \
  CMD curl -f http://localhost:8080/management/health || exit 1