#!/usr/bin/env bash

#
# This script is used to synchronise the users in the Shibboleth LDAP directory with the current users in the IFS MySQL database.
#
# It does this in 4 steps:
#
# 1) Clear down the current users in the LDAP directory
# 2) Select the current set of email addresses from the MySQL database
# 3) For each email address, POST to the Shib Rest API to create the user, and get back the uuid of the user
# 4) Feed the uuid from the Shib Rest API back into MySQL
#
function executeMySQLCommand {

    dbUrl=$(./read-dev-build-property.sh ext.ifsDatasourceUrl)
    dbName=$(echo ${dbUrl} | sed 's/.*\/\(.*\)$/\1/')
    dbUsername=$(./read-dev-build-property.sh ext.ifsDatasourceUsername)
    dbPassword=$(./read-dev-build-property.sh ext.ifsDatasourcePassword)

    mysql ${dbName} -u${dbUsername} -p${dbPassword} -N -s -e "$1"
}

export -f executeMySQLCommand

function addUserToShibboleth {
    emailAddress=$1
    response=$(curl -k -d "{\"email\": \"${emailAddress}\",\"password\": \"test\"}" -H 'Content-type: application/json' -H "api-key: 1234567890" https://ifs-local-dev/regapi/identities/)
    uuid=$(echo ${response} | sed 's/.*"uuid":"\([^"]*\)".*/\1/g')
    executeMySQLCommand "update user set uid='${uuid}' where email='${emailAddress}';"
}

export -f addUserToShibboleth

ldapsearch -H ldap://ifs-local-dev/ -b 'dc=nodomain' -s sub '(objectClass=person)' -x \
 | grep 'dn: ' \
 | cut -c4- \
 | xargs ldapdelete -H ldap://ifs-local-dev/ -D 'cn=admin,dc=nodomain' -w foobar

executeMySQLCommand "select email from user;" | xargs -I{} bash -c "addUserToShibboleth {}"