#!/bin/bash

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

echo "Database parameters"
echo "host:"$host
echo "database:"$db
echo "user:"$user
echo "port"$port
echo "LDAP parameters"
echo "domain":$domain

executeMySQLCommand() {
    mysql $db -P $port -u $user --password=$pass -h "$host" -N -s -e "$1"
}

findAllUsersInDatabase() {
    $(executeMySQLCommand "select email from user where system_user = 0;")
}

addUserToShibboleth() {
  email=$1
  addUser $email | ldapadd -H ldapi:/// -D "cn=admin,$domain" -w "$ldappass"
}

addUser() {
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

wipeLdapUsers() {
  ldapsearch -H ldapi:/// -b "$domain" -s sub '(objectClass=person)' -x \
   | grep 'dn: ' \
   | cut -c4- \
   | xargs ldapdelete -H ldapi:/// -D "cn=admin,$domain" -w "$ldappass"
}