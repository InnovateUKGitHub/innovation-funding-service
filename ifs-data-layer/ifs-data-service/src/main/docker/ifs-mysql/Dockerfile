FROM mysql:5.6.29
MAINTAINER Richard Popple <richard@hiveit.co.uk>
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod 755 /usr/local/bin/docker-entrypoint.sh
RUN chgrp -R 0 /var/run/mysqld/ && chmod -R g=u /var/run/mysqld/