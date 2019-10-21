#!/bin/bash

# A Munro: Put users in ifs db into the local ldap.
# Ensure uids are correct.
# Wipes all users on the ldap.
# Uses the same password for all users.
#
# Update history: 
# A Munro 10 Mar 2017 Big gotya, slapadd is intermittent with replication; sometimes it works
# and sometimes it doesn't (even with the -w arg). So need to use ldapadd.

# Args:
# $1: db host (default ifs-database)
# $2: db (database name; default ifs)
# $3: db user (default root)
# $4: db user password (default 'password;)
# $5: db port (default 3306)

# The infamous user password: Passw0rd
password="e1NTSEF9b2lRZUF1OHNrR0VqQmhweUpmV01hOFF3M0dNK2xRd2Q="

host=$DB_HOST
#host=mysql
db=$DB_NAME
user=$DB_USER
pass=$DB_PASS
port=$DB_PORT

[ ! -z "$1" ] && host=$1
[ ! -z "$2" ] && db=$2
[ ! -z "$3" ] && user=$3
[ ! -z "$4" ] && pass=$4
[ ! -z "$5" ] && port=$5

echo host:$host
echo database:$db
echo user:$user
echo port:$port

echo ldap host:$LDAP_HOST
echo ldap port:$LDAP_PORT
echo ldap domain:$LDAP_DOMAIN
echo ldap scheme:$LDAP_SCHEME

wipeLdapUsers() {
  [ -z "$LDAP_PORT" ] && LDAP_PORT=8389

  ldapsearch -H $LDAP_SCHEME://$LDAP_HOST:$LDAP_PORT/ -b $LDAP_DOMAIN -s sub '(objectClass=person)' -D "cn=admin,$LDAP_DOMAIN" -w $LDAP_PASS \
   | grep 'dn: ' \
   | cut -c4- \
   | xargs ldapdelete -H $LDAP_SCHEME://$LDAP_HOST:$LDAP_PORT/ -D "cn=admin,$LDAP_DOMAIN" -w $LDAP_PASS
}

executeMySQLCommand() {
    mysql $db -P $port -u $user --password=$pass -h $host -N -s -e "$1"
}

addUserToShibboleth() {
  IFS=${2} read -r -a array <<< "$1"
  uid="${array[0]}"
  email="${array[1]}"

  echo "dn: uid=$uid,$LDAP_DOMAIN"
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

downloadAccUserCsv() {
#    Download users from repository
    curl -0 -u ${ACC_USERNAME}:${ACC_PASSWORD} ${ACC_BITBUCKET_URL} -o users.csv
#    Remove first line of column names
    tail -n +2 users.csv > tempusers.csv && mv tempusers.csv users.csv
#    Create new Csv with emails and new generated UUID
    cat users.csv | awk -v SUFFIX="${ACC_SUFFIX}" -F "\"*,\"*" '("uuidgen" | getline uuid) > 0 {print uuid, $3 SUFFIX} {close("uuidgen")}' | sed 's/\ /,/g' > emailsAndUUids.csv
}
# Main

wipeLdapUsers
downloadAccUserCsv

IFS=$'\n'
for u in $(executeMySQLCommand "select uid,email from user where system_user = 0;")
do
  addUserToShibboleth $u $'\t'
done | ldapadd -H $LDAP_SCHEME://$LDAP_HOST:$LDAP_PORT/ -D "cn=admin,$LDAP_DOMAIN" -w $LDAP_PASS

while read -a csv_line;
do
  addUserToShibboleth $csv_line ','
done < emailsAndUUids.csv | ldapadd -H $LDAP_SCHEME://$LDAP_HOST:$LDAP_PORT/ -D "cn=admin,$LDAP_DOMAIN" -w $LDAP_PASS

