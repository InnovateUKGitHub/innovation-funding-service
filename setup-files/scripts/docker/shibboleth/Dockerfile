FROM g2g3/ifs-local-dev
MAINTAINER David Soff<dsoff@worth.systems>

RUN ln -sf /dev/stdout /var/log/apache2/proxy_access.log
RUN ln -sf /dev/stderr /var/log/apache2/proxy_error.log

COPY ifs-local-dev.conf /etc/apache2/sites-available/ifs-local-dev.conf