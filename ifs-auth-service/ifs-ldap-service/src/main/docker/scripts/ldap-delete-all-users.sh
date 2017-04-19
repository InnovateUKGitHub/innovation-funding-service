#!/usr/bin/env bash

[ -z "$LDAP_PORT" ] && LDAP_PORT=8389

domain=$(ldapsearch -H ldapi:// -LLL -Q -Y EXTERNAL -b "cn=config" "(olcRootDN=*)" olcSuffix|awk '/olcSuffix/ {print $2}')

ldapsearch -H ldap://localhost:$LDAP_PORT/ -b "$domain" -s sub '(objectClass=person)' -x \
 | grep 'dn: ' \
 | cut -c4- \
 | xargs ldapdelete -H ldap://localhost:$LDAP_PORT/ -D "cn=admin,$domain" -w $LDAP_PASSWORD
