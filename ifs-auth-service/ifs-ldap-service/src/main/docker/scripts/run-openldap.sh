#!/bin/sh
# enable & enforce SSL, be lax about self-signed certificates \
  echo /var/certs/ldap-encryption.key > /etc/ldap/ldap-encryption.key
  echo /var/certs/ldap-encryption.crt > /etc/ldap/ldap-encryption.crt

  ldapmodify -Y EXTERNAL -H ldapi:/// -f /usr/local/bin/mod_ssl.ldif

exec /usr/sbin/slapd -h "ldaps://0.0.0.0:$LDAP_PORT/ ldapi:///" -F /etc/ldap/slapd.d -d 256