#!/bin/sh
# Copy certs from the mounted runtime directory
cat /var/certs/ldap-encryption.key > /etc/ldap/ldap-encryption.key
cat /var/certs/ldap-encryption.crt > /etc/ldap/ldap-encryption.crt

# Start the main process in a background thread - see https://docs.docker.com/config/containers/multi-service_container/
/usr/sbin/slapd -h "ldaps://0.0.0.0:$LDAP_PORT/ ldapi:///" -F /etc/ldap/slapd.d -d 256 &

# Enable SSL
post_start_actions.sh

# Now we bring the LDAP primary process back into the foreground and leave it there
fg %1

