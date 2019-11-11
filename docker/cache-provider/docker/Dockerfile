FROM redis:5.0.5

RUN usermod -a -G root redis
RUN mkdir /redis-data
COPY redis.conf /usr/local/etc/redis/redis.conf
RUN chown -R :root /redis-data
RUN chmod -R 777 /redis-data

# Health checks
COPY redis-docker-healthcheck.sh /usr/local/bin/docker-healthcheck.sh
RUN chmod 755 /usr/local/bin/docker-healthcheck.sh
HEALTHCHECK --interval=15s --timeout=8s \
  CMD bash /usr/local/bin/docker-healthcheck.sh

CMD [ "redis-server", "/usr/local/etc/redis/redis.conf" ]
