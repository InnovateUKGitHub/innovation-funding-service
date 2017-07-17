## Originally sourced from https://github.com/primait/docker-proxysql/blob/master/1.3/Dockerfile.  Maintained locally to offer more security and additional functionality.
FROM debian:jessie

## Original maintainer Andrea Usuelli <andreausu@gmail.com>
MAINTAINER Duncan Watson <duncan@hiveit.co.uk>

RUN apt-get update && \
    apt-get install -y wget mysql-client rsync && \
    wget https://github.com/sysown/proxysql/releases/download/1.3.0f/proxysql_1.3.0f-debian8_amd64.deb -O /opt/proxysql_1.3.0f-debian8_amd64.deb && \
    dpkg -i /opt/proxysql_1.3.0f-debian8_amd64.deb && \
    rm -rf /opt/proxysql_1.3.0f-debian8_amd64.deb /var/lib/apt/lists/*

RUN mkdir -p /dump/rewrites

RUN mkdir -p //.gnupg

COPY proxysql/entrypoint.sh proxysql/make-mysqldump.sh proxysql/make-proxysql-cnf-file.sh proxysql/rewrite-rule-generator.sh /dump/

COPY proxysql/rewrites/* /dump/rewrites/

COPY proxysql/proxysql.cnf /etc/

RUN chgrp -R 0 /etc && chmod -R g+rwX /etc
RUN chgrp -R 0 /dump && chmod -R g+rwx /dump
RUN chgrp -R 0 /var/lib/proxysql && chmod -R g+rwX /var/lib/proxysql
RUN chgrp -R 0 //.gnupg && chmod -R g+rwX //.gnupg

ENTRYPOINT ["/dump/entrypoint.sh"]