#!/bin/bash 
#
# A Munro: For sp: rewrite shib and idp domain names, if required
# By default SPHOST is ifs-local-dev and IDPHOST is idp.
# 
# UPDATES:
# A Munro 2 Feb 2017: update tomcat server.xml with proxyHost and proxyPort

SPDEF="ifs-local-dev"
IDPDEF="idp"

  # Change shib url domain name; only for ifs-local-dev
[ ! -z "$SPHOST" ] && [ "$SPHOST" != "$SPDEF" ] && {
  echo "Changing web hostname from $SPDEF to $SPHOST."
  sed -i "s#\/\/$SPDEF#\/\/$SPHOST#g" \
    /etc/shibboleth/metadata.xml \
    /opt/shibboleth-idp/conf/attribute-filter.xml \
    /opt/shibboleth-idp/views/error.vm \
    /etc/apache2/sites-available/idpproxy.conf
}

[ ! -z "$IDPHOST" ] && [ "$IDPHOST" != "$IDPDEF" ] && {
  echo "Changing idp host from $IDPDEF to $IDPHOST shibboleth idp config."
  sed -i "s#\/\/$IDPDEF#\/\/$IDPHOST#g" /etc/shibboleth/metadata.xml /opt/shibboleth-idp/conf/idp.properties /opt/shibboleth-idp/metadata/idp-metadata.xml

  # This could change... So lets create a loop.
  for f in /etc/apache2/sites-available/idpproxy.conf
  do
    echo "Changing web hostname from $IDPDEF to $IDPHOST in file $f."
    sed -i "s/ServerName $IDPDEF/ServerName $IDPHOST/" $f
  done

  IDPH=$(echo $IDPHOST|cut -d: -f1)
  sed -i "s#proxyName=.*#proxyName=\"$IDPH\"#g" /etc/tomcat8/server.xml

  # Fix for where the idp listener apache port has been changed:
  [[ $IDPHOST =~ : ]] && {
    IDPPORT=$(echo $IDPHOST|cut -d: -f2)
    sed -i "s/$IDPPORT:8443/8443/g" /etc/shibboleth/metadata.xml /opt/shibboleth-idp/metadata/idp-metadata.xml
    sed -i "s/proxyPort=.*/proxyPort=\"$IDPPORT\"/" /etc/tomcat8/server.xml
    sed -i "s/$IDPHOST/$IDPH/g" /etc/apache2/sites-available/idpproxy.conf
  }
}
