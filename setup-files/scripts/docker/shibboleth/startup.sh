#!/bin/bash
set -e

if [ -f "/opt/chef/bin/chef-client" ]; then
  /opt/chef/bin/chef-client -c /g2g3/chef/zero.rb -z -j /g2g3/chef/runlist.json
  /bin/cp -va "/g2g3/deploy"/* "/opt/shibboleth-idp/"
else
  /etc/init.d/shibd start
  /etc/init.d/slapd start
  if ! ldapsearch -H ldapi:/// -b 'dc=nodomain' -x | /bin/grep 'dn: uid=6b50cb4f-7222-33a5-99c5-8c068cd0b03c,dc=nodomain'; then
    ldapadd -H ldapi:/// -f /tmp/testentries.ldif -D "cn=admin,dc=nodomain" -w test
  fi
  /etc/init.d/tomcat8 start

  ldapsearch -H ldap://localhost/ -b 'dc=nodomain' -s sub '(objectClass=person)' -x \
     | grep 'dn: ' \
     | cut -c4- \
     | xargs ldapdelete -H ldap://localhost/ -D 'cn=admin,dc=nodomain' -w test

  [ -e /var/run/apache2/apache2.pid ] && rm -f /var/run/apache2/apache2.pid
  /usr/sbin/apache2ctl -D FOREGROUND
fi