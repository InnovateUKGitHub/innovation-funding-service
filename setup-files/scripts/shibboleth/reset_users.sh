#!/usr/bin/env bash

ldapsearch -H ldap://ifs-local-dev/ -b 'dc=nodomain' -s sub '(objectClass=person)' -x \
 | grep 'dn: ' \
 | cut -c4- \
 | xargs ldapdelete -H ldap://ifs-local-dev/ -D 'cn=admin,dc=nodomain' -w foobar