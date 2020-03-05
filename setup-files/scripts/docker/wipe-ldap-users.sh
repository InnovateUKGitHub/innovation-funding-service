#!/bin/bash

# Wipes all users on the ldap.

echo ldap host:$LDAP_HOST
echo ldap port:$LDAP_PORT
echo ldap domain:$LDAP_DOMAIN
echo ldap scheme:$LDAP_SCHEME
echo only delete ifs users:$ONLY_DELETE_IFS_USERS

wipeLdapUsersByLdap() {
  [ -z "$LDAP_PORT" ] && LDAP_PORT=8389

  ldapsearch -H $LDAP_SCHEME://$LDAP_HOST:$LDAP_PORT/ -b $LDAP_DOMAIN -s sub '(objectClass=person)' -D "cn=admin,$LDAP_DOMAIN" -w $LDAP_PASS \
   | grep 'dn: ' \
   | cut -c4- \
   | xargs ldapdelete -H $LDAP_SCHEME://$LDAP_HOST:$LDAP_PORT/ -D "cn=admin,$LDAP_DOMAIN" -w $LDAP_PASS
}

wipeLdapUsersByDatabase() {
  [ -z "$LDAP_PORT" ] && LDAP_PORT=8389

  for u in $(mysql $db -P $port -u $user --password=$pass -h $host -N -s -e "select email from user where system_user = 0;")
  do
    uid=$(executeMySQLCommand "select uid from user where email='$(escaped $u)';")
    echo "uid=$uid,$domain"
  done | ldapdelete -H ldapi:/// -D "cn=admin,$domain" -w "$ldappass"
}

executeMySQLCommand() {
    mysql $db -P $port -u $user --password=$pass -h $host -N -s -e "$1"
}

if [ $ONLY_DELETE_IFS_USERS = "true" ] ; then
    wipeLdapUsersByDatabase
else
    wipeLdapUsersByLdap
fi