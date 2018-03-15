FROM dhoer/flyway:4.1.2
MAINTAINER Ravish Bhagdev <ravish.bhagdev@gmail.com>
VOLUME /tmp
RUN apt-get update -y && apt-get install -y mysql-client
COPY db/ /flyway/sql/db
COPY *.sh /

RUN touch /flyway/flyway.conf && chgrp -R 0 /flyway/flyway.conf && chmod -R g+rwX /flyway/flyway.conf

RUN chgrp -R 0 /*.sh && chmod -R g+rwX /*.sh && \
    chgrp -R 0 /usr/local/bin/flyway && chmod -R g+rwX /usr/local/bin/flyway && \
    chgrp -R 0 /flyway && chmod -R g+rwX /flyway