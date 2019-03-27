FROM mysql:5.6.39
EXPOSE 3306
# Extending the 5.6.39 mysql image but adding our own healthcheck
COPY mysql-docker-healthcheck.sh /usr/local/bin/docker-healthcheck.sh
RUN chmod 755 /usr/local/bin/docker-healthcheck.sh

ARG GID=993
ARG UID=996
ARG PW=default

ENV GID=993
ENV UID=996
ENV PW=default

RUN groupadd -g ${GID} gluster
RUN useradd -p ${PW} -u ${UID} -g ${GID} gluster

RUN chown -R gluster:gluster /var/lib/mysql/

USER gluster

HEALTHCHECK --interval=15s --timeout=8s \
  CMD bash /usr/local/bin/docker-healthcheck.sh
