#!/usr/bin/env bash

ldapsearch -H ldap://localhost/ -b 'dc=nodomain' -s sub '(objectClass=person)' -x \
 | grep 'dn: ' \
 | cut -c4- \
 | xargs ldapdelete -H ldap://localhost/ -D 'cn=admin,dc=nodomain' -w test
