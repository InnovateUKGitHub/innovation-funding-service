#!/bin/bash
# Turn on bash's job control. We need this so we can start back ground process.
# see https://docs.docker.com/config/containers/multi-service_container/ for more information.
set -m

# Start the main process in a background thread
echo "Starting LDAP in a back ground process"
/usr/sbin/slapd -h "ldaps://0.0.0.0:$LDAP_PORT/ ldapi:///" -F /etc/ldap/slapd.d -d 256 &

# Do any actions we need after we've started LDAP.
echo "Post start up configuration"

# Enable SSL
echo "Enabling SSL"
until ldapmodify -Y EXTERNAL -H ldapi:/// -f /usr/local/bin/mod_ssl.ldif
do
    echo "Waiting for LDAP to start"
    sleep 3
done

# Now we bring the LDAP primary process back into the foreground and leave it there.
echo "Bringing LDAP to the foreground"
fg %1

