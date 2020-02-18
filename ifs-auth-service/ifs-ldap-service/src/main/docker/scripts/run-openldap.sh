#!/bin/bash
# Turn on bash's job control. We need this so we can start back ground process.
# see https://docs.docker.com/config/containers/multi-service_container/ for more information.
set -m

# Copy certs from the mounted runtime directory
cat /var/certs/ldap-encryption.key > /etc/ldap/ldap-encryption.key
cat /var/certs/ldap-encryption.crt > /etc/ldap/ldap-encryption.crt

# Start the main process in a background thread
echo "Starting LDAP in a back ground process"
/usr/sbin/slapd -h "ldaps://0.0.0.0:$LDAP_PORT/ ldapi:///" -F /etc/ldap/slapd.d -d 256 &

# Do any actions we need after we've started LDAP.
echo "Post start up configuration"

# Enable SSL
echo "Enabling SSL"
until ldapmodify -H ldapi:/// -D cn=admin,cn=config -w root_password_123 -f mod_ssl.ldif
do
    echo "Waiting for LDAP to start"
    sleep 3
done

# Remove the root password now configuration is complete
echo "Removing the root password now configuration is complete"
ldapmodify -H ldapi:/// -D cn=admin,cn=config -w root_password_123 -f rootpw_cnconfig_delete.ldif

# Now we bring the LDAP primary process back into the foreground and leave it there.
echo "Bringing LDAP to the foreground"
fg %1

