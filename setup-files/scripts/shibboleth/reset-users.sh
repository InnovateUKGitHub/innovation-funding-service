#!/usr/bin/env bash

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