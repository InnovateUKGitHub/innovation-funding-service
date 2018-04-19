FROM innovateuk/app-base-image
EXPOSE 8080
ENV JAVA_OPTS -Xmx300m

WORKDIR /
VOLUME /tmp

ADD ifs-sil-stub-@version@.jar app.jar

HEALTHCHECK --interval=15s --timeout=8s \
  CMD curl -f http://localhost:8080/monitoring/health || exit 1
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
