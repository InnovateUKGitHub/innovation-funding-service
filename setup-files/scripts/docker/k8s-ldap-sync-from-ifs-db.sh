#!/bin/bash

db=ifs-database
[ ! -z "$IFSDB" ] && db=$IFSDB

idp=idp
[ ! -z "$IDPHOST" ] && idp=$IDPHOST

echo database:$db
echo idp:$idp

executeMySQLCommand() {
    mysql ifs -uroot -ppassword -h${db} -N -s -e "$1"
}

addUserToShibboleth() {

    emailAddress=$1

    system_user=$(executeMySQLCommand "select system_user from user where email='${emailAddress}';")

    if [ "${system_user}" == "1" ]; then

      echo "Skipping adding user ${emailAddress} to Shibboleth as they are a System User and as such have no login"

    else

      echo "Adding User ${emailAddress} from MySQL in Shibboleth"

      response=$(curl -s -k -d "{\"email\": \"${emailAddress}\",\"password\": \"Passw0rd\"}" -H 'Content-type: application/json' -H "api-key: 1234567890" https://$idp/regapi/identities/)
      uuid=$(echo ${response} | sed 's/.*"uuid":"\([^"]*\)".*/\1/g')
      echo "uuid: $uuid"
      executeMySQLCommand "update user set uid='${uuid}' where email='${emailAddress}';"

      userStatus=$(executeMySQLCommand "select status from user where email='${emailAddress}';" )

      if [ "${userStatus}" == "ACTIVE" ]; then
        echo "User ${emailAddress} is active in MySQL, so activating them in Shibboleth"
        curl -s -X PUT -k -H 'Content-type: application/json' -H "api-key: 1234567890" https://$idp/regapi/identities/${uuid}/activateUser
      fi

    fi
}

for u in $(mysql ifs -uroot -ppassword -h$db -N -s -e "select email from user;")
do
  addUserToShibboleth $u
done
