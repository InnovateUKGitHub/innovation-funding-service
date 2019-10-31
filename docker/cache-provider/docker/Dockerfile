FROM redis:5.0.6

USER root
RUN chown redis:redis -R /data
RUN apt-get update && apt-get install -y procps
RUN chmod -R 777 /data

# Health checks
COPY redis-docker-healthcheck.sh /usr/local/bin/docker-healthcheck.sh
RUN chmod 755 /usr/local/bin/docker-healthcheck.sh
HEALTHCHECK --interval=15s --timeout=8s \
  CMD bash /usr/local/bin/docker-healthcheck.sh

