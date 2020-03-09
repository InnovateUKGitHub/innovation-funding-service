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

email=$1

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

addUserToShibboleth() {
  uid=$(executeMySQLCommand "select uid from user where email='$(escaped $1)';")

  echo "dn: uid=$uid,$domain"
  echo "uid: $uid"
  echo "mail: $1"
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

addUserToShibboleth $email | ldapadd -H ldapi:/// -D "cn=admin,$domain" -w "$ldappass"