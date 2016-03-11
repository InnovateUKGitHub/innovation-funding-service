#!/usr/bin/env bash
function executeMySQLCommand {

    dbUrl=$(./_read-dev-build-properties.sh ext.ifsDatasourceUrl)
    dbName=$(echo ${dbUrl} | sed 's/.*\/\(.*\)$/\1/')
    dbUsername=$(./_read-dev-build-properties.sh ext.ifsDatasourceUsername)
    dbPassword=$(./_read-dev-build-properties.sh ext.ifsDatasourcePassword)

    mysql ${dbName} -u${dbUsername} -p${dbPassword} -N -s -e "$1"
}

export -f executeMySQLCommand

function addUserToShibboleth {
    emailAddress=$1
    response=$(curl -k -d "{\"email\": \"${emailAddress}\",\"password\": \"Passw0rd\"}" -H 'Content-type: application/json' -H "api-key: 1234567890" https://ifs-local-dev/regapi/identities/)
    uuid=$(echo ${response} | sed 's/.*"uuid":"\([^"]*\)".*/\1/g')
    curl -X PUT -H 'Content-type: application/json' -H "api-key: 1234567890" https://ifs-local-dev/regapi/identities/${uuid}/activateUser --insecure
    executeMySQLCommand "update user set uid='${uuid}' where email='${emailAddress}';"
}

export -f addUserToShibboleth

executeMySQLCommand "select email from user;" | xargs -I{} bash -c "addUserToShibboleth {}"
