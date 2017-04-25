#!/bin/bash

# container run script

# substitutions at runtime, we can't use environment variables in XML files
sed -i "s#\/\/ifs.local-dev#\/\/$SP_DOMAIN#g" /etc/shibboleth/* /var/www/html/Logout/index.html && \
sed -i "s/idp:8443/$IDP_PORT/g" /etc/shibboleth/metadata.xml && \
sed -i "s#\/\/idp#\/\/$IDP_DOMAIN#g" /etc/shibboleth/shibboleth2.xml /etc/shibboleth/metadata.xml /var/www/html/Logout/index.html

# Remove any lingering pid files
for p in /var/run/apache2/apache2.pid /var/run/shibboleth/shibd.pid
do
  [ -e $p ] && rm -f $p
done

/usr/sbin/shibd -f -c /etc/shibboleth/shibboleth2.xml -p /var/run/shibboleth/shibd.pid -w 30
exec /usr/sbin/apache2ctl -D FOREGROUND
