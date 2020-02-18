#!/bin/sh
# TODO wait on LDAP to be up[ instead of sleeping
sleep 60
ldapmodify -H ldapi:/// -D cn=admin,cn=config -w foobar123 -f mod_ssl.ldif
# TODO remove the root user.


