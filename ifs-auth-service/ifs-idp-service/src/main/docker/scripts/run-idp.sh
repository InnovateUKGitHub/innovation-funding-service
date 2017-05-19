#!/bin/bash

# Container run script

# Put these in scripts as it makes this script less cluttered:
/usr/local/bin/url-rewrites.sh

proxy_certificate=$(sed '/^-----/d' "/etc/shibboleth/$SP_PROXY_CERTIFICATE" | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${PROXY_CERTIFICATE}#$proxy_certificate#g" /etc/shibboleth/metadata.xml

idp_signing_certificate=$(sed '/^-----/d' "/etc/shibboleth/$IDP_SIGNING_CERTIFICATE" | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_SIGNING_CERTIFICATE}#$idp_signing_certificate#g" /etc/shibboleth/metadata.xml

idp_encryption_certificate=$(sed '/^-----/d' "/etc/shibboleth/$IDP_ENCRYPTION_CERTIFICATE" | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_ENCRYPTION_CERTIFICATE}#$idp_encryption_certificate#g" /etc/shibboleth/metadata.xml


# Env vars have defaults in the Dockerfile so we can use them for health checks.

# Some horrible port editing
sed -i -e "s/VirtualHost \*:443/VirtualHost \*:$HTTPSPORT/" -e "s/VirtualHost \*:80/VirtualHost \*:$HTTPPORT/" /etc/apache2/sites-available/*.conf
sed -i -e "s/Listen 80/Listen $HTTPPORT/" -e "s/Listen 443/Listen $HTTPSPORT/" /etc/apache2/ports.conf

# Do the ldap edits with this script
/usr/local/bin/ldap-settings.sh

sed -i "s/\${GOOGLEANALYTICS_TRACKINGID}/$GOOGLEANALYTICS_TRACKINGID/" /opt/shibboleth-idp/messages/messages.properties


[ -e /var/run/apache2/apache2.pid ] && rm -f /var/run/apache2/apache2.pid

# Generate new certs to match hostname on container startup
/usr/bin/openssl req -subj "/CN=$HOSTNAME" -new -newkey rsa:2048 -days 7300 -nodes -x509 -sha256 -keyout /etc/tomcat8/certs/server.key -out /etc/tomcat8/certs/server.crt

/usr/local/bin/start-idp.sh
exec /usr/sbin/apache2ctl -D FOREGROUND
