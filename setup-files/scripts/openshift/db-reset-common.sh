#!/bin/bash

function failIfProduction() {

    if [[ "$TARGET" == "production" ]]; then
        echo "Cannot reset the database on production"
        exit 1
    fi
}

function exportDBENV() {

if [[ "$TARGET" == "local" || "$TARGET" == "remote" ]]; then

    export DB_NAME=ifs
    export DB_USER=root
    export DB_PASS=password
    export DB_HOST=ifs-database
    export DB_PORT=3306

    export LDAP_HOST="ldap"
    export LDAP_PORT=389
    export LDAP_PASS="default"
    export LDAP_DOMAIN="dc=nodomain"
    export LDAP_SCHEME="ldaps"

    export FLYWAY_LOCATIONS="filesystem:/flyway/sql/db/migration,filesystem:/flyway/sql/db/setup,filesystem:/flyway/sql/db/webtest"
    export SYSTEM_USER_UUID="c0d02979-e66e-11e7-ac43-0242ac120002"
fi
}



injectDBVariables
injectLDAPVariables
injectFlywayVariables

useContainerRegistry
pushDBResetImages

dbReset

echo Waiting for container to start
until [ "$(oc get po dbreset ${SVC_ACCOUNT_CLAUSE} &> /dev/null; echo $?)" == 0 ] && [ "$(oc get po dbreset -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" == 'Running' ]
do
  echo -n .
  sleep 5
done

oc logs -f dbreset ${SVC_ACCOUNT_CLAUSE}

echo Waiting for container to terminate before checking its status
sleep 5

if [[ "$(oc get po dbreset -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" != "Succeeded" ]]; then exit -1; fi

# tidy up the pod afterwards
oc delete pod dbreset ${SVC_ACCOUNT_CLAUSE}
exit 0
