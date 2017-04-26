#!/bin/bash

# container run script

# substitutions at runtime, we can't use environment variables in XML files
sed -i "s#\/\/ifs.local-dev#\/\/$SP_DOMAIN#g" /etc/shibboleth/* /var/www/html/Logout/index.html && \
sed -i "s/idp:8443/$IDP_PORT/g" /etc/shibboleth/metadata.xml
proxy_certificate=$(sed '/^-----/d' "/etc/shibboleth/$PROXY_CERTIFICATE" | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${PROXY_CERTIFICATE}#$proxy_certificate#g" /etc/shibboleth/metadata.xml && \

idp_signing_certificate=$(sed '/^-----/d' "/etc/shibboleth/$IDP_SIGNING_CERTIFICATE" | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_SIGNING_CERTIFICATE}#$idp_signing_certificate#g" /etc/shibboleth/metadata.xml && \

idp_encryption_certificate=$(sed '/^-----/d' "/etc/shibboleth/$IDP_ENCRYPTION_CERTIFICATE" | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_ENCRYPTION_CERTIFICATE}#$idp_encryption_certificate#g" /etc/shibboleth/metadata.xml && \

sed -i "s#\/\/idp#\/\/$IDP_DOMAIN#g" /etc/shibboleth/shibboleth2.xml /etc/shibboleth/metadata.xml /var/www/html/Logout/index.html

# Remove any lingering pid files
for p in /var/run/apache2/apache2.pid /var/run/shibboleth/shibd.pid
do
  [ -e $p ] && rm -f $p
done

/usr/sbin/shibd -f -c /etc/shibboleth/shibboleth2.xml -p /var/run/shibboleth/shibd.pid -w 30
exec /usr/sbin/apache2ctl -D FOREGROUND
