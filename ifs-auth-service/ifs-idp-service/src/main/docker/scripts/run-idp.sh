#!/bin/sh

# update configuration at runtime for configuration files without native environment variable support

# Put these in scripts as it makes this script less cluttered:
/usr/local/bin/url-rewrites.sh

# apache/tomcat "front" certificates
echo "$IDP_PROXY_KEY" | tee /etc/apache2/certs/idp_proxy_key.pem > /etc/tomcat8/certs/server.key && \
echo "$IDP_PROXY_CERTIFICATE" | tee /etc/apache2/certs/idp_proxy_certificate.pem > /etc/tomcat8/certs/server.crt && \
echo "$IDP_PROXY_CACERTIFICATE" > /etc/apache2/certs/idp_proxy_cacertificate.pem && \
cat /etc/tomcat8/certs/server.crt >> /etc/apache2/certs/proxy.pem && printf '\n' >> /etc/apache2/certs/proxy.pem && \
cat /etc/tomcat8/certs/server.key >> /etc/apache2/certs/proxy.pem

# idp certificates
echo "$IDP_SIGNING_CERTIFICATE" > /etc/shibboleth/idp-signing.crt && \
echo "$IDP_ENCRYPTION_CERTIFICATE" > /etc/shibboleth/idp-encryption.crt && \
echo "$SP_PROXY_CERTIFICATE" > /etc/shibboleth/sp_proxy_certificate.pem

echo "$IDP_SIGNING_KEY" > /opt/shibboleth-idp/credentials/idp-signing.key && \
echo "$IDP_SIGNING_CERTIFICATE" > /opt/shibboleth-idp/credentials/idp-signing.crt && \
echo "$IDP_ENCRYPTION_KEY" > /opt/shibboleth-idp/credentials/idp-encryption.key && \
echo "$IDP_ENCRYPTION_CERTIFICATE" > /opt/shibboleth-idp/credentials/idp-encryption.crt

# idp configuration
sed -i "s#\/\/ifs.local-dev#\/\/$SPHOST#g" /etc/shibboleth/*

proxy_certificate=$(sed '/^-----/d' /etc/shibboleth/sp_proxy_certificate.pem | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${PROXY_CERTIFICATE}#$proxy_certificate#g" /etc/shibboleth/metadata.xml

idp_signing_certificate=$(sed '/^-----/d' /etc/shibboleth/idp-signing.crt | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_SIGNING_CERTIFICATE}#$idp_signing_certificate#g" /etc/shibboleth/metadata.xml

idp_encryption_certificate=$(sed '/^-----/d' /etc/shibboleth/idp-encryption.crt | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_ENCRYPTION_CERTIFICATE}#$idp_encryption_certificate#g" /etc/shibboleth/metadata.xml

# are we sharing sessions between containers?
[ ${MEMCACHE_ENDPOINT} ] && \
sed -i -e '/<!-- ${MEMCACHE_ENDPOINT}/d' -e '/${MEMCACHE_ENDPOINT} -->/d' \
       -e "s/\${MEMCACHE_ENDPOINT}/$MEMCACHE_ENDPOINT/g" /opt/shibboleth-idp/conf/global.xml && \
sed -i -e 's/#idp.replayCache.StorageService = shibboleth.StorageService/idp.replayCache.StorageService = shibboleth.MemcachedStorageService/g' \
       -e 's/#idp.artifact.StorageService = shibboleth.StorageService/idp.artifact.StorageService = shibboleth.MemcachedStorageService/g' /opt/shibboleth-idp/conf/idp.properties

# Env vars have defaults in the Dockerfile so we can use them for health checks.

# Some horrible port editing
sed -i -e "s/VirtualHost \*:443/VirtualHost \*:$HTTPSPORT/" -e "s/VirtualHost \*:80/VirtualHost \*:$HTTPPORT/" /etc/apache2/sites-available/*.conf
sed -i -e "s/Listen 80/Listen $HTTPPORT/" -e "s/Listen 443/Listen $HTTPSPORT/" /etc/apache2/ports.conf

# Do the ldap edits with this script
/usr/local/bin/ldap-settings.sh

sed -i "s/\${GOOGLEANALYTICS_TRACKINGID}/$GOOGLEANALYTICS_TRACKINGID/" /opt/shibboleth-idp/messages/messages.properties


[ -e /var/run/apache2/apache2.pid ] && rm -f /var/run/apache2/apache2.pid

# Generate new certs to match hostname on container startup
#/usr/bin/openssl req -subj "/CN=$HOSTNAME" -new -newkey rsa:2048 -days 7300 -nodes -x509 -sha256 -keyout /etc/tomcat8/certs/server.key -out /etc/tomcat8/certs/server.crt

/usr/local/bin/start-idp.sh
exec /usr/sbin/apache2ctl -D FOREGROUND
