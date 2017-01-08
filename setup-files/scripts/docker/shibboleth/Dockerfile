FROM g2g3/ifs-local-dev
MAINTAINER David Soff<dsoff@worth.systems>

RUN ln -sf /dev/stdout /var/log/apache2/proxy_access.log && \
    ln -sf /dev/stderr /var/log/apache2/proxy_error.log && \
    rm /etc/apache2/sites-available/ifs-local-dev.conf && \
    rm /etc/apache2/sites-enabled/ifs-local-dev.conf

COPY ifs-local-dev.conf /etc/apache2/sites-available/<<HOSTNAME>>.conf
RUN ln -sf /etc/apache2/sites-available/<<HOSTNAME>>.conf /etc/apache2/sites-enabled/<<HOSTNAME>>.conf

#grep -rl --exclude-dir=proc --exclude-dir=sys ifs-local-dev
RUN sed -i 's/ifs-local-dev/<<HOSTNAME>>/g' /etc/shibboleth/shibboleth2.xml && \
    sed -i 's/ifs-local-dev/<<HOSTNAME>>/g' /etc/shibboleth/metadata.xml && \
    sed -i 's/ifs-local-dev/<<HOSTNAME>>/g' var/www/html/Logout/index.html && \
    sed -i 's/ifs-local-dev/<<HOSTNAME>>/g' opt/shibboleth-idp/conf/idp.properties && \
    sed -i 's/ifs-local-dev/<<HOSTNAME>>/g' opt/shibboleth-idp/conf/attribute-filter.xml && \
    sed -i 's/ifs-local-dev/<<HOSTNAME>>/g' opt/shibboleth-idp/metadata/idp-metadata.xml && \
    sed -i 's/ifs-local-dev/<<HOSTNAME>>/g' opt/shibboleth-idp/bin/build.xml