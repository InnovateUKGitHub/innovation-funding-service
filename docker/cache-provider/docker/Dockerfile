FROM redis:5.0.6

ENV GID=993
ENV UID=996
ENV PW=default

RUN groupadd -g ${GID} gluster && \
  useradd -p ${PW} -u ${UID} -g ${GID} gluster

RUN chown -R gluster:gluster /data && \
  find /data \( -type d -o -type f \) -exec chmod 775 {} +

USER gluster

# Health checks
COPY redis-docker-healthcheck.sh /usr/local/bin/docker-healthcheck.sh
RUN chmod 755 /usr/local/bin/docker-healthcheck.sh
HEALTHCHECK --interval=15s --timeout=8s \
  CMD bash /usr/local/bin/docker-healthcheck.sh

