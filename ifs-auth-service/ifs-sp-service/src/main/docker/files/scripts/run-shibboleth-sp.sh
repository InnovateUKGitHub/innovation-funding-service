#!/bin/sh

# update configuration at runtime for configuration files without native environment variable support

# apache certificates
echo "$SP_PROXY_KEY" > /etc/apache2/certs/sp_proxy_key.pem && \
echo "$SP_PROXY_CERTIFICATE" > /etc/apache2/certs/sp_proxy_certificate.pem && \
echo "$SP_PROXY_CACERTIFICATE" > /etc/apache2/certs/sp_proxy_cacertificate.pem

# sp certificates
cp /etc/apache2/certs/* /etc/shibboleth/ && \
echo "$IDP_SIGNING_CERTIFICATE" > /etc/shibboleth/idp-signing.crt && \
echo "$IDP_ENCRYPTION_CERTIFICATE" > /etc/shibboleth/idp-encryption.crt

# sp configuration
sed -i "s#\/\/ifs.local-dev#\/\/$SP_DOMAIN#g" /etc/shibboleth/* /var/www/html/Logout/index.html && \
sed -i "s#\/\/idp#\/\/$IDP_DOMAIN#g" /etc/shibboleth/shibboleth2.xml /etc/shibboleth/metadata.xml /var/www/html/Logout/index.html && \
sed -i "s/idp:8443/$IDP_PORT/g" /etc/shibboleth/metadata.xml

sp_proxy_certificate=$(sed '/^-----/d' /etc/shibboleth/sp_proxy_certificate.pem | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${SP_PROXY_CERTIFICATE}#$sp_proxy_certificate#g" /etc/shibboleth/metadata.xml

idp_signing_certificate=$(sed '/^-----/d' /etc/shibboleth/idp-signing.crt | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_SIGNING_CERTIFICATE}#$idp_signing_certificate#g" /etc/shibboleth/metadata.xml

idp_encryption_certificate=$(sed '/^-----/d' /etc/shibboleth/idp-encryption.crt | sed '{:q;N;s/\n/\\n/g;t q}')
sed -i "s#\${IDP_ENCRYPTION_CERTIFICATE}#$idp_encryption_certificate#g" /etc/shibboleth/metadata.xml

# are we sharing sessions between containers?
[ ${MEMCACHE_ENDPOINT} ] && sed -i -e '/<!-- ${MEMCACHE_ENDPOINT}/d' -e '/${MEMCACHE_ENDPOINT} -->/d' \
                                   -e "s/\${MEMCACHE_ENDPOINT}/$MEMCACHE_ENDPOINT/g" \
                                   -e 's/ss:mem/ss:mc/g' /etc/shibboleth/shibboleth2.xml

# Remove any lingering pid files
for p in /var/run/apache2/apache2.pid /var/run/shibboleth/shibd.pid
do
  [ -e $p ] && rm -f $p
done

/usr/sbin/shibd -f -c /etc/shibboleth/shibboleth2.xml -p /var/run/shibboleth/shibd.pid -w 30
exec /usr/sbin/apache2ctl -D FOREGROUND
