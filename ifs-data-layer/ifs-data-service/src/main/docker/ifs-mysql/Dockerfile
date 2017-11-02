FROM mysql:5.6.29
MAINTAINER Richard Popple <richard@hiveit.co.uk>
# Copy over replacement entry point that avoids OpenShift permissions issues
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod 755 /usr/local/bin/docker-entrypoint.sh
# Allow the default user that OpenShift runs with to access the directories it needs
# The user will be a member of the root group so give access to that group
RUN chgrp -R 0 /var/run/mysqld/ && chmod -R g=u /var/run/mysqld/
RUN chgrp -R 0 /var/lib/mysql && chmod -R g=u /var/lib/mysql
