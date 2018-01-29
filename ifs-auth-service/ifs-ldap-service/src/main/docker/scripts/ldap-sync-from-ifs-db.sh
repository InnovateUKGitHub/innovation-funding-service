#!/bin/bash

# Functionality to reset the users in ldap using the current values in the database.
# Note the following:
#
# This script is put on the ldap server as part of the docker build. The ldap server
# is only present in non named environments and so this script is only present there. 
# For named environments a similar mechanism is present with an indentically named,
# but slightly different, shell script being put on a stand alone built docker image. 
# That process could be used instead of this, but it would lengthen build times. 
#
# This script does the following:
# Wipes all users on the ldap
# Put users from the ifs db into the local ldap
# Uids are added as they are found in the database
# All users get the same password
#
# Update history:
# A Munro 10 Mar 2017 Big gotya, slapadd is intermittent with replication; sometimes it works
# and sometimes it doesn't (even with the -w arg). To be safe, need to use ldapadd.

# Args:
# $1: db host (default ifs-database)
# $2: db (database name; default ifs)
# $3: db user (default root)
# $4: db user password (default password)
# $5: db port (default 3306)
# $6: ldap password (default default)
# $7: ldap domain (default dc=nodomain)

# The infamous user password: Passw0rd
password="e1NTSEF9b2lRZUF1OHNrR0VqQmhweUpmV01hOFF3M0dNK2xRd2Q="
# Defaults. As this script should only ever be run on development environments they should suffice.
# Database
host="ifs-database"
db="ifs"
user="root"
pass="password"
port="3306"
# LDAP
ldappass="default"
domain="dc=nodomain"

# However if required allow overriding of the default parameters.
[ ! -z "$1" ] && host=$1
[ ! -z "$2" ] && db=$2
[ ! -z "$3" ] && user=$3
[ ! -z "$4" ] && pass=$4
[ ! -z "$5" ] && port=$5
[ ! -z "$6" ] && ldappass=$6

echo "Database parameters"
echo "host:"$host
echo "database:"$db
echo "user:"$user
echo "port"$port
echo "LDAP parameters"
echo "domain":$domain

wipeLdapUsers() {
  ldapsearch -H ldapi:/// -b "$domain" -s sub '(objectClass=person)' -x \
   | grep 'dn: ' \
   | cut -c4- \
   | xargs ldapdelete -H ldapi:/// -D "cn=admin,$domain" -w "$ldappass"
}

executeMySQLCommand() {
    mysql $db -P $port -u $user --password=$pass -h "$host" -N -s -e "$1"
}

addUserToShibboleth() {
  email=$1
  uid=$(executeMySQLCommand "select uid from user where email='$(escaped $email)';")

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

# Escape single quote for use in sql where clauses.
escaped() {
  echo $1 | sed "s/'/\\\\'/g"
}

# Main
wipeLdapUsers

for u in $(mysql $db -P $port -u $user --password=$pass -h $host -N -s -e "select email from user where system_user = 0;")
do
  addUserToShibboleth "$u"
done | ldapadd -H ldapi:/// -D "cn=admin,$domain" -w "$ldappass"
