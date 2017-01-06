FROM g2g3/ifs-local-dev
MAINTAINER David Soff<dsoff@worth.systems>

RUN ln -sf /dev/stdout /var/log/apache2/proxy_access.log
RUN ln -sf /dev/stderr /var/log/apache2/proxy_error.log

RUN rm /etc/apache2/sites-available/ifs-local-dev.conf
RUN rm /etc/apache2/sites-enabled/ifs-local-dev.conf

COPY ifs-local-dev.conf /etc/apache2/sites-available/a-random-host.conf
RUN ln -sf /etc/apache2/sites-available/a-random-host.conf /etc/apache2/sites-enabled/a-random-host.conf


#grep -rl --exclude-dir=proc --exclude-dir=sys ifs-local-dev
RUN sed -i 's/ifs-local-dev/a-random-host/g' /etc/shibboleth/shibboleth2.xml
RUN sed -i 's/ifs-local-dev/a-random-host/g' /etc/shibboleth/metadata.xml
RUN sed -i 's/ifs-local-dev/a-random-host/g' var/www/html/Logout/index.html
RUN sed -i 's/ifs-local-dev/a-random-host/g' opt/shibboleth-idp/conf/idp.properties
RUN sed -i 's/ifs-local-dev/a-random-host/g' opt/shibboleth-idp/conf/attribute-filter.xml
RUN sed -i 's/ifs-local-dev/a-random-host/g' opt/shibboleth-idp/metadata/idp-metadata.xml
RUN sed -i 's/ifs-local-dev/a-random-host/g' opt/shibboleth-idp/bin/build.xml