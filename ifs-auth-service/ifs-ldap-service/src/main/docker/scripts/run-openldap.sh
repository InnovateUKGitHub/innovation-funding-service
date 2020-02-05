#!/bin/sh

# TODO comment
cat /var/certs/ldap-encryption.key > /etc/ldap/ldap-encryption.key
cat /var/certs/ldap-encryption.crt > /etc/ldap/ldap-encryption.crt

exec /usr/sbin/slapd -h "ldaps://0.0.0.0:$LDAP_PORT/ ldapi:///" -F /etc/ldap/slapd.d -d 256
