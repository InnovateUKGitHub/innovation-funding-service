#!/bin/bash

# Container run script

# Env vars have defaults in the Dockerfile so we can use them for health checks.

exec /usr/sbin/slapd -h "ldap://0.0.0.0:$LDAP_PORT/ ldaps://0.0.0.0:$LDAP_SSL_PORT/ ldapi:///" -F /etc/ldap/slapd.d -d 256
