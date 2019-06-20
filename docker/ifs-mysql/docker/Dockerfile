FROM mysql:5.6.39
EXPOSE 3306
# Extending the 5.6.39 mysql image but adding our own healthcheck
COPY mysql-docker-healthcheck.sh /usr/local/bin/docker-healthcheck.sh
RUN chmod 755 /usr/local/bin/docker-healthcheck.sh

HEALTHCHECK --interval=15s --timeout=8s \
  CMD bash /usr/local/bin/docker-healthcheck.sh

