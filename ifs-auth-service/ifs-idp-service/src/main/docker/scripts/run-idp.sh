#!/bin/sh

# update configuration at runtime for configuration files without native environment variable support

# apache/tomcat "front" certificates
cat /var/certs/idp_proxy_key.pem | tee /etc/apache2/certs/idp_proxy_key.pem > /etc/tomcat8/certs/server.key && \
cat /var/certs/idp_proxy_certificate.pem | tee /etc/apache2/certs/idp_proxy_certificate.pem > /etc/tomcat8/certs/server.crt && \
cat /var/certs/idp_proxy_cacertificate.pem > /etc/apache2/certs/idp_proxy_cacertificate.pem && \
cat /etc/tomcat8/certs/server.crt >> /etc/apache2/certs/proxy.pem && printf '\n' >> /etc/apache2/certs/proxy.pem && \
cat /etc/tomcat8/certs/server.key >> /etc/apache2/certs/proxy.pem

# idp certificates
cat /var/certs/idp-signing.crt > /etc/shibboleth/idp-signing.crt && \
cat /var/certs/idp-encryption.crt > /etc/shibboleth/idp-encryption.crt && \
cat /var/certs/sp_proxy_certificate.pem > /etc/shibboleth/sp_proxy_certificate.pem

cat /var/certs/idp-signing.key > /opt/shibboleth-idp/credentials/idp-signing.key && \
cat /var/certs/idp-signing.crt > /opt/shibboleth-idp/credentials/idp-signing.crt && \
cat /var/certs/idp-encryption.key > /opt/shibboleth-idp/credentials/idp-encryption.key && \
cat /var/certs/idp-encryption.crt > /opt/shibboleth-idp/credentials/idp-encryption.crt

cat /var/certs/ldap-encryption.crt > /opt/shibboleth-idp/credentials/ldap-encryption.crt && \
$JAVA_HOME/bin/keytool -import -noprompt -trustcacerts -file /opt/shibboleth-idp/credentials/ldap-encryption.crt -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass "$JAVA_KEYSTORE_PASSWORD"

. idp-extras.sh

# idp configuration
sed -i "s#\/\/ifs.local-dev#\/\/$SP_DOMAIN#g" /etc/shibboleth/*.xml
sed -i "s#\/\/idp#\/\/$IDP_DOMAIN#g" /etc/shibboleth/metadata.xml /opt/shibboleth-idp/conf/idp.properties
sed -i "s#\/\/ifs-local-dev#\/\/$SP_DOMAIN#g" /opt/shibboleth-idp/conf/attribute-filter.xml /opt/shibboleth-idp/views/error.vm

proxy_certificate=$(sed '/^-----/d' /etc/shibboleth/sp_proxy_certificate.pem | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${PROXY_CERTIFICATE}#$proxy_certificate#g" /etc/shibboleth/metadata.xml

idp_saml_signing_certificate=$(sed '/^-----/d' /etc/shibboleth/idp-signing.crt | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_SAML_SIGNING_CERTIFICATE}#$idp_saml_signing_certificate#g" /etc/shibboleth/metadata.xml

idp_saml_encryption_certificate=$(sed '/^-----/d' /etc/shibboleth/idp-encryption.crt | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_SAML_ENCRYPTION_CERTIFICATE}#$idp_saml_encryption_certificate#g" /etc/shibboleth/metadata.xml

# are we sharing sessions between containers?
[ ${MEMCACHE_ENDPOINT} ] && \
sed -i -e '/<!-- ${MEMCACHE_ENDPOINT}/d' -e '/${MEMCACHE_ENDPOINT} -->/d' \
       -e "s/\${MEMCACHE_ENDPOINT}/$MEMCACHE_ENDPOINT/g" /opt/shibboleth-idp/conf/global.xml && \
sed -i -e 's/#idp.replayCache.StorageService = shibboleth.StorageService/idp.replayCache.StorageService = shibboleth.MemcachedStorageService/g' \
       -e 's/#idp.artifact.StorageService = shibboleth.StorageService/idp.artifact.StorageService = shibboleth.MemcachedStorageService/g' /opt/shibboleth-idp/conf/idp.properties

sed -i "s/\${GOOGLEANALYTICS_TRACKINGID}/$GOOGLEANALYTICS_TRACKINGID/" /opt/shibboleth-idp/messages/messages.properties
sed -i "s/\${BUILD_TIMESTAMP}/$BUILD_TIMESTAMP/" /opt/shibboleth-idp/messages/messages.properties
sed -i "s#\${RESOURCE_DOMAIN}#$RESOURCE_DOMAIN#g" /opt/shibboleth-idp/messages/messages.properties

[ -e /var/run/apache2/apache2.pid ] && rm -f /var/run/apache2/apache2.pid

/usr/local/bin/start-idp.sh
exec /usr/sbin/apache2ctl -D FOREGROUND
