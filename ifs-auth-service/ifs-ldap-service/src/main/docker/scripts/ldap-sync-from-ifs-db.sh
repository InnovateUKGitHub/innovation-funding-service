#!/bin/bash

# A Munro: Put users in ifs db into the local ldap.
# Ensure uids are correct.
# Wipes all users on the ldap.
# Uses the same password for all users.
#
# Update history: 
# A Munro 10 Mar 2017 Big gotya, slapadd is intermittent with replication; sometimes it works
# and sometimes it doesn't (even with the -w arg). To be safe, need to use ldapadd.

# Args:
# $1: db host (default ifs-database)
# $2: db (database name; default ifs)
# $3: db user (default root)
# $4: db user password (default 'password;)
# $5: db port (default 3306)
# $6: ldap password


# The infamous user password: Passw0rd
password="e1NTSEF9b2lRZUF1OHNrR0VqQmhweUpmV01hOFF3M0dNK2xRd2Q="
[ -z "$LDAP_PORT" ] && LDAP_PORT=8389

host=ifs-database
#host=mysql
db=ifs
user=root
pass=password
port=3306
ldappass=$LDAP_PASSWORD

# Get the ldap domain from the local system
domain=$(ldapsearch -H ldapi:// -LLL -Q -Y EXTERNAL -b "cn=config" "(olcRootDN=*)" olcSuffix|awk '/olcSuffix/ {print $2}')

[ ! -z "$1" ] && host=$1
[ ! -z "$2" ] && db=$2
[ ! -z "$3" ] && user=$3
[ ! -z "$4" ] && pass=$4
[ ! -z "$5" ] && port=$5
[ ! -z "$6" ] && ldappass=$5

echo host:$host
echo database:$db
echo user:$user
echo port:$port

wipeLdapUsers() {
  ldapsearch -H ldap://localhost:$LDAP_PORT/ -b "$domain" -s sub '(objectClass=person)' -x \
   | grep 'dn: ' \
   | cut -c4- \
   | xargs ldapdelete -H ldap://localhost:$LDAP_PORT/ -D "cn=admin,$domain" -w "$ldappass"
}

executeMySQLCommand() {
    mysql $db -P $port -u $user --password=$pass -h "$host" -N -s -e "$1"
}

addUserToShibboleth() {
  email=$1
  uid=$(executeMySQLCommand "select uid from user where email='$email';")

  echo "dn: uid=$uid,$domain"
  echo "uid: $uid"
  echo "mail: $email"
  echo "sn:: IA=="
  echo "cn:: IA=="
  echo "objectClass: inetOrgPerson"
  echo "objectClass: person"
  echo "objectClass: top"
  echo "employeeType: active"
  echo "userPassword:: $password"
  echo ""
}

# Main

wipeLdapUsers

for u in $(mysql $db -P $port -u $user --password=$pass -h $host -N -s -e "select email from user where status = 'ACTIVE' and system_user = 0;")
do
  addUserToShibboleth "$u"
done | ldapadd -H ldap://localhost:$LDAP_PORT/ -D "cn=admin,$domain" -w "$ldappass"
