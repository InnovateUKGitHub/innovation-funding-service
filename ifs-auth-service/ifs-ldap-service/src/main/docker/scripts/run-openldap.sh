#!/bin/bash
# Turn on bash's job control. We need this so we can start back ground process.
# see https://docs.docker.com/config/containers/multi-service_container/ for more information.
set -m

# Copy certs from the mounted runtime directory
cat /var/certs/ldap-encryption.key > /etc/ldap/ldap-encryption.key
cat /var/certs/ldap-encryption.crt > /etc/ldap/ldap-encryption.crt

# Start the main process in a background thread
/usr/sbin/slapd -h "ldaps://0.0.0.0:$LDAP_PORT/ ldapi:///" -F /etc/ldap/slapd.d -d 256 &

# Do any actions we need after we'ce started LDAP.
# TODO better solution than wait
sleep 30
ldapmodify -H ldapi:/// -D cn=admin,cn=config -w foobar123 -f mod_ssl.ldif
# TODO remove root user.

# Now we bring the LDAP primary process back into the foreground and leave it there.
fg %1

