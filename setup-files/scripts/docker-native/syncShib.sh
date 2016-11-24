#!/bin/bash
function executeMySQLCommand {
    mysql ifs -uroot -ppassword -hifs-database -N -s -e "$1"
}

export -f executeMySQLCommand

function addUserToShibboleth {

    emailAddress=$1

    system_user=$(executeMySQLCommand "select system_user from user where email='${emailAddress}';")

    if [ "${system_user}" == "1" ]; then

      echo "Skipping adding user ${emailAddress} to Shibboleth as they are a System User and as such have no login"

    else

      echo "Adding User ${emailAddress} from MySQL in Shibboleth"

      response=$(curl -s -k -d "{\"email\": \"${emailAddress}\",\"password\": \"Passw0rd\"}" -H 'Content-type: application/json' -H "api-key: 1234567890" https://ifs-local-dev/regapi/identities/)
      uuid=$(echo ${response} | sed 's/.*"uuid":"\([^"]*\)".*/\1/g')
      executeMySQLCommand "update user set uid='${uuid}' where email='${emailAddress}';"

      userStatus=$(executeMySQLCommand "select status from user where email='${emailAddress}';")

      if [ "${userStatus}" == "ACTIVE" ]; then
        echo "User ${emailAddress} is active in MySQL, so activating them in Shibboleth"
        curl -s -X PUT -H 'Content-type: application/json' -H "api-key: 1234567890" https://ifs-local-dev/regapi/identities/${uuid}/activateUser --insecure
      fi

    fi
}

export -f addUserToShibboleth

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd ${BASEDIR}

for item in $( docker-compose -p ifs ps -q shib ); do
    docker cp scripts/_delete-shib-users-remote.sh ${item}:/tmp/_delete-shib-users-remote.sh
done

docker-compose -p ifs exec shib /tmp/_delete-shib-users-remote.sh

mysql ifs -uroot -ppassword -hifs-database -N -s -e "select email from user;" | xargs -I{} bash -c "addUserToShibboleth {}"