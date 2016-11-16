FROM davidcaste/alpine-java-unlimited-jce:jdk8
MAINTAINER David Soff <dsoff@worth.systems>
EXPOSE 8080
EXPOSE 8081
EXPOSE 12345
RUN apk --update add curl
COPY certificate /certificate
WORKDIR $JAVA_HOME
RUN bin/keytool -import -trustcacerts -keystore jre/lib/security/cacerts -storepass changeit -noprompt -alias iuk-auth-localdev -file /certificate
WORKDIR /
VOLUME /tmp
ADD ifs-data-service-0.0.1-SNAPSHOT.war app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java", "-Xmx100m", "-jar", "/app.jar"]

HEALTHCHECK --interval=10s --timeout=3s \
  CMD curl -f http://localhost:8080/health || exit 1