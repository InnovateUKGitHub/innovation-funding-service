#!/bin/bash


function getBuildLocation() {
    echo "build/resources/main"
}

function isNamedEnvironment() {

    TARGET=$1

    if [[ ${TARGET} != "production" && ${TARGET} != "demo" && ${TARGET} != "uat" && ${TARGET} != "sysint" && ${TARGET} != "perf" ]]; then
        exit 1
    else
        exit 0
    fi
}

function isProductionEnvironment() {

    TARGET=$1

    if [[ ${TARGET} != "production" ]]; then
        exit 1
    else
        exit 0
    fi
}

function getProjectName() {

    PROJECT=$1
    TARGET=$2

    if $(isNamedEnvironment $TARGET); then
        echo "$TARGET"
    else
        echo "$PROJECT"
    fi
}

function getSvcAccountToken() {

    if [ -z "$bamboo_openshift_svc_account_token" ]; then
        echo "$(oc whoami -t)";
    else
        echo "${bamboo_openshift_svc_account_token}";
    fi
}

function getHost() {

    TARGET=$1

    if [[ (${TARGET} == "local") ]]; then
      echo "ifs-local"
    elif [[ ${TARGET} == "production" ]]; then
      echo "apply-for-innovation-funding.service.gov.uk"
    else
      echo "prod.ifs-test-clusters.com"
    fi
}

function getRouteDomain() {

    TARGET=$1
    HOST=$2

    if [[ ${TARGET} == "production" ]]; then
      echo "$HOST"
    else
      echo "apps.$HOST"
    fi
}

function getRegistry() {

    if [[ (${TARGET} == "local") ]]; then
        echo "$(getLocalRegistryUrl)"
    else
        echo "docker-registry-default.apps.prod.ifs-test-clusters.com"
    fi
}

function getInternalRegistry() {

    if [[ (${TARGET} == "local") ]]; then
        echo "$(getLocalRegistryUrl)"
    else
        echo "172.30.80.28:5000"
    fi
}

function getSvcAccountClause() {

    TARGET=$1
    PROJECT=$2
    SVC_ACCOUNT_TOKEN=$3

    if [[ (${TARGET} == "local") ]]; then
        SVC_ACCOUNT_CLAUSE_SERVER_PART='localhost:8443'
    else
        SVC_ACCOUNT_CLAUSE_SERVER_PART='console.prod.ifs-test-clusters.com:443'
    fi

    echo "--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://${SVC_ACCOUNT_CLAUSE_SERVER_PART} --insecure-skip-tls-verify=true"
}

function convertFileToBlock() {
    cat "$1" | tr -d '\r' | tr '\n' '^' | sed "s/\^/<<>>/g" | rev | cut -c 5- | rev
}

function injectDBVariables() {
    if [ -z "$DB_USER" ]; then echo "Set DB_USER environment variable"; exit -1; fi
    if [ -z "$DB_PASS" ]; then echo "Set DB_PASS environment variable"; exit -1; fi
    if [ -z "$DB_NAME" ]; then echo "Set DB_NAME environment variable"; exit -1; fi
    if [ -z "$DB_HOST" ]; then echo "Set DB_HOST environment variable"; exit -1; fi

    DB_PORT=${DB_PORT:-3306}

    sed -i.bak "s#<<DB-USER>>#$DB_USER#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<DB-PASS>>#$DB_PASS#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<DB-NAME>>#$DB_NAME#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<DB-HOST>>#$DB_HOST#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<DB-PORT>>#$DB_PORT#g" $(getBuildLocation)/db-reset/*.yml

    sed -i.bak "s#<<DB-USER>>#$DB_USER#g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s#<<DB-PASS>>#$DB_PASS#g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s#<<DB-NAME>>#$DB_NAME#g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s#<<DB-HOST>>#$DB_HOST#g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s#<<DB-PORT>>#$DB_PORT#g" $(getBuildLocation)/db-anonymised-data/*.yml
}


function injectFlywayVariables() {
    [ -z "$FLYWAY_LOCATIONS" ] && { echo "Set FLYWAY_LOCATIONS environment variable"; exit -1; }
    sed -i.bak "s#<<FLYWAY-LOCATIONS>>#${FLYWAY_LOCATIONS}#g" $(getBuildLocation)/db-reset/*.yml
}

function injectLDAPVariables() {
    if [ -z "$LDAP_HOST" ]; then echo "Set LDAP_HOST environment variable"; exit -1; fi
    LDAP_PORT=${LDAP_PORT:-8389}
    sed -i.bak "s#<<LDAP-HOST>>#$LDAP_HOST#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<LDAP-PORT>>#$LDAP_PORT#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<LDAP-PASS>>#$LDAP_PASS#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<LDAP-DOMAIN>>#$LDAP_DOMAIN#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<LDAP-SCHEME>>#$LDAP_SCHEME#g" $(getBuildLocation)/db-reset/*.yml
}

function getEnvVariableValue() {
    variableName=$1
    eval echo "\$$variableName"
}

function substituteOptionalEnvVariable() {
    variableValue=$(getEnvVariableValue $1)
    replacementToken=$2
    find $(getBuildLocation) -name '*.yml' | xargs sed -i.bak "s#${replacementToken}#${variableValue}#g"
}

function substituteMandatoryEnvVariable() {

    variableName=$1
    variableValue=$(getEnvVariableValue $variableName)

    if [ -z "${variableValue}" ]; then
        echo "Environment variable: ${variableName} not set"; exit -1
    fi

    replacementToken=$2

    find $(getBuildLocation) -name '*.yml' | xargs sed -i.bak "s#${replacementToken}#${variableValue}#g"
}

function tailorAppInstance() {

    # We will set up the default environment variables here for local and remote projects as we do not expect these to
    # be available other than in Bamboo-triggered jobs
    if ! $(isNamedEnvironment $TARGET); then

        # for the IDP and Registration Service
        export LDAP_URL="ldaps://ldap:389"
        export LDAP_PASSWORD="default"

        # for the SP
        export SHIBBOLETH_SP_MPM_STARTSERVERS="2"
        export SHIBBOLETH_SP_MPM_MINSPARETHREADS="25"
        export SHIBBOLETH_SP_MPM_MAXSPARETHREADS="75"
        export SHIBBOLETH_SP_MPM_THREADLIMIT="64"
        export SHIBBOLETH_SP_MPM_THREADSPERCHILD="25"
        export SHIBBOLETH_SP_MPM_MAXREQUESTWORKERS="150"
        export SHIBBOLETH_SP_MPM_MAXCONNECTIONSPERCHILD="0"
        export SHIBBOLETH_SP_MEMORY_REQUEST="300Mi"
        export SHIBBOLETH_SP_MEMORY_LIMIT="600Mi"

        # for the IDP
        export SHIBBOLETH_IDP_MEMORY_REQUEST="500M"
        export SHIBBOLETH_IDP_MEMORY_LIMIT="2048M"
        export GA_TRACKING_ID=
        export LDAP_USESSL="true"
        export LDAP_BASEDN="dc=nodomain"
        export LDAP_BINDDN="cn=admin,dc=nodomain"
        export LDAP_AUTHENTICATOR="anonSearchAuthenticator"

        # for the SP and IDP
        export SHIBBOLETH_MEMCACHE_ENDPOINT=
    fi

    if [ -z "$SSLCERTFILE" ]; then echo "Set SSLCERTFILE, SSLCACERTFILE, and SSLKEYFILE environment variables"; exit -1; fi
    sed -i.bak -e $"s#<<SSLCERT>>#$(convertFileToBlock $SSLCERTFILE)#g" -e 's/<<>>/\\n/g' $(getBuildLocation)/shib/*.yml
    sed -i.bak -e $"s#<<SSLCACERT>>#$(convertFileToBlock $SSLCACERTFILE)#g" -e 's/<<>>/\\n/g' $(getBuildLocation)/shib/*.yml
    sed -i.bak -e $"s#<<SSLKEY>>#$(convertFileToBlock $SSLKEYFILE)#g" -e 's/<<>>/\\n/g' $(getBuildLocation)/shib/*.yml

    sed -i.bak -e "s/<<NEWRELIC-LICENCE-KEY>>/$NEWRELIC_LICENCE_KEY/g" -e "s/<<NEWRELIC-ENVIRONMENT>>/$TARGET/g" $(getBuildLocation)/*.yml
    sed -i.bak -e "s/<<NEWRELIC-LICENCE-KEY>>/$NEWRELIC_LICENCE_KEY/g" -e "s/<<NEWRELIC-ENVIRONMENT>>/$TARGET/g" $(getBuildLocation)/sil-stub/*.yml
    sed -i.bak -e "s/<<NEWRELIC-LICENCE-KEY>>/$NEWRELIC_LICENCE_KEY/g" -e "s/<<NEWRELIC-ENVIRONMENT>>/$TARGET/g" $(getBuildLocation)/shib/56-*.yml

    if [[ ${TARGET} == "production" ]]
    then
      sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth.$ROUTE_DOMAIN/g" $(getBuildLocation)/*.yml
      sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth.$ROUTE_DOMAIN/g" $(getBuildLocation)/shib/*.yml

      sed -i.bak "s/<<SHIB-ADDRESS>>/$ROUTE_DOMAIN/g" $(getBuildLocation)/*.yml
      sed -i.bak "s/<<SHIB-ADDRESS>>/$ROUTE_DOMAIN/g" $(getBuildLocation)/shib/*.yml

    else
      sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" $(getBuildLocation)/*.yml
      sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" $(getBuildLocation)/shib/*.yml

      sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" $(getBuildLocation)/*.yml
      sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" $(getBuildLocation)/shib/*.yml
    fi


    sed -i.bak "s/<<MAIL-ADDRESS>>/mail-$PROJECT.$ROUTE_DOMAIN/g" $(getBuildLocation)/mail/*.yml
    sed -i.bak "s/<<ADMIN-ADDRESS>>/admin-$PROJECT.$ROUTE_DOMAIN/g" $(getBuildLocation)/spring-admin/*.yml


    if $(isNamedEnvironment ${TARGET}); then

        sed -i.bak "s/claimName: file-upload-claim/claimName: ${TARGET}-file-upload-claim/g" $(getBuildLocation)/*.yml

    fi

    # for the IDP and Registration Service
    substituteMandatoryEnvVariable LDAP_URL "<<LDAP-URL>>"
    substituteMandatoryEnvVariable LDAP_PASSWORD "<<LDAP-PASSWORD>>"

    # for the SP
    substituteMandatoryEnvVariable SHIBBOLETH_SP_MPM_STARTSERVERS "<<SHIBBOLETH_SP_MPM_STARTSERVERS>>"
    substituteMandatoryEnvVariable SHIBBOLETH_SP_MPM_MINSPARETHREADS "<<SHIBBOLETH_SP_MPM_MINSPARETHREADS>>"
    substituteMandatoryEnvVariable SHIBBOLETH_SP_MPM_MAXSPARETHREADS "<<SHIBBOLETH_SP_MPM_MAXSPARETHREADS>>"
    substituteMandatoryEnvVariable SHIBBOLETH_SP_MPM_THREADLIMIT "<<SHIBBOLETH_SP_MPM_THREADLIMIT>>"
    substituteMandatoryEnvVariable SHIBBOLETH_SP_MPM_THREADSPERCHILD "<<SHIBBOLETH_SP_MPM_THREADSPERCHILD>>"
    substituteMandatoryEnvVariable SHIBBOLETH_SP_MPM_MAXREQUESTWORKERS "<<SHIBBOLETH_SP_MPM_MAXREQUESTWORKERS>>"
    substituteMandatoryEnvVariable SHIBBOLETH_SP_MPM_MAXCONNECTIONSPERCHILD "<<SHIBBOLETH_SP_MPM_MAXCONNECTIONSPERCHILD>>"
    substituteOptionalEnvVariable SHIBBOLETH_SP_MEMORY_REQUEST "<<SHIBBOLETH_SP_MEMORY_REQUEST>>"
    substituteMandatoryEnvVariable SHIBBOLETH_SP_MEMORY_LIMIT "<<SHIBBOLETH_SP_MEMORY_LIMIT>>"

    # for the IDP
    substituteOptionalEnvVariable SHIBBOLETH_IDP_MEMORY_REQUEST "<<SHIBBOLETH_IDP_MEMORY_REQUEST>>"
    substituteMandatoryEnvVariable SHIBBOLETH_IDP_MEMORY_LIMIT "<<SHIBBOLETH_IDP_MEMORY_LIMIT>>"
    substituteOptionalEnvVariable GA_TRACKING_ID "<<GA-TRACKING-ID>>"
    substituteMandatoryEnvVariable LDAP_USESSL "<<LDAP_USESSL>>"
    substituteMandatoryEnvVariable LDAP_BASEDN "<<LDAP_BASEDN>>"
    substituteMandatoryEnvVariable LDAP_BINDDN "<<LDAP_BINDDN>>"
    substituteMandatoryEnvVariable LDAP_AUTHENTICATOR "<<LDAP_AUTHENTICATOR>>"

    # for the SP and IDP
    substituteOptionalEnvVariable SHIBBOLETH_MEMCACHE_ENDPOINT "<<SHIBBOLETH-MEMCACHE-ENDPOINT>>"

    ## TODO DW - when we remove the tech debt of having multiple files for the shib yml files per named environment,
    ## we can do away with this more complex configuration block and that of the one above that this one mirrors
    if $(isNamedEnvironment ${TARGET}); then

        sed -i.bak "s#<<SHIBBOLETH_LDAP_PORT>>#389#g" $(getBuildLocation)/45-registration-svc.yml
        sed -i.bak "s#<<SHIBBOLETH_LDAP_BASE_DN>>#dc=int,dc=g2g3digital,dc=net#g" $(getBuildLocation)/45-registration-svc.yml
        sed -i.bak "s#<<SHIBBOLETH_LDAP_USER>>#cn=admin,dc=int,dc=g2g3digital,dc=net#g" $(getBuildLocation)/45-registration-svc.yml

    else
        sed -i.bak "s#<<SHIBBOLETH_LDAP_PORT>>#389#g" $(getBuildLocation)/45-registration-svc.yml
        sed -i.bak "s#<<SHIBBOLETH_LDAP_BASE_DN>>#dc=nodomain#g" $(getBuildLocation)/45-registration-svc.yml
        sed -i.bak "s#<<SHIBBOLETH_LDAP_USER>>#cn=admin,dc=nodomain#g" $(getBuildLocation)/45-registration-svc.yml
    fi

    if [[ ${TARGET} == "production" || ${TARGET} == "uat" || ${TARGET} == "perf"  ]]
    then
        sed -i.bak "s/replicas: 1/replicas: 2/g" $(getBuildLocation)/4*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" $(getBuildLocation)/5-front-door-service.yml
    fi

    if [[ ${TARGET} == "local" ]]
    then
        replacePersistentFileClaim
    fi
}

function tailorFractalInstance() {
    sed -i.bak "s/<<FRACTAL-ADDRESS>>/fractal-$PROJECT.$ROUTE_DOMAIN/g" $(getBuildLocation)/fractal/*.yml
}


function useContainerRegistry() {

    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/sil-stub/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/fractal/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/robot-tests/*.yml

    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/sil-stub/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/fractal/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/shib/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/robot-tests/*.yml

#    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/sil-stub/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/fractal/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/shib/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/robot-tests/*.yml

}

function pushApplicationImages() {
    docker tag innovateuk/data-service:latest \
        ${REGISTRY}/${PROJECT}/data-service:${VERSION}
    docker tag innovateuk/project-setup-service:latest \
        ${REGISTRY}/${PROJECT}/project-setup-service:${VERSION}
    docker tag innovateuk/project-setup-management-service:latest \
        ${REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}
    docker tag innovateuk/competition-management-service:latest \
        ${REGISTRY}/${PROJECT}/competition-management-service:${VERSION}
    docker tag innovateuk/assessment-service:latest \
        ${REGISTRY}/${PROJECT}/assessment-service:${VERSION}
    docker tag innovateuk/application-service:latest \
        ${REGISTRY}/${PROJECT}/application-service:${VERSION}
    docker tag innovateuk/front-door-service:latest \
        ${REGISTRY}/${PROJECT}/front-door-service:${VERSION}
    docker tag innovateuk/sp-service:latest \
        ${REGISTRY}/${PROJECT}/sp-service:${VERSION}
    docker tag innovateuk/idp-service:latest \
        ${REGISTRY}/${PROJECT}/idp-service:${VERSION}
    docker tag innovateuk/ldap-service:latest \
        ${REGISTRY}/${PROJECT}/ldap-service:${VERSION}
    docker tag innovateuk/registration-service:latest \
        ${REGISTRY}/${PROJECT}/registration-service:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/data-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/project-setup-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/competition-management-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/assessment-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/application-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/front-door-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/sp-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/idp-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/ldap-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/registration-service:${VERSION}
}

function pushSilStubImages(){
    docker tag innovateuk/sil-stub:latest \
        ${REGISTRY}/${PROJECT}/sil-stub:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/sil-stub:${VERSION}
}

function pushDBResetImages() {
    docker tag innovateuk/dbreset:latest \
        ${REGISTRY}/${PROJECT}/dbreset:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/dbreset:${VERSION}
}

function pushFractalImages() {
    docker tag innovateuk/fractal:latest \
        ${REGISTRY}/${PROJECT}/fractal:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/fractal:${VERSION}
}


function pushAnonymisedDatabaseDumpImages() {
    docker tag innovateuk/db-anonymised-data:latest \
        ${REGISTRY}/${PROJECT}/db-anonymised-data:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/db-anonymised-data:${VERSION}
}

function blockUntilServiceIsUp() {
    UNREADY_PODS=1
    while [ ${UNREADY_PODS} -ne "0" ]
    do
        UNREADY_PODS=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} -o custom-columns='NAME:{.metadata.name},READY:{.status.conditions[?(@.type=="Ready")].status}' | grep -v True | sed 1d | wc -l)
        oc get pods ${SVC_ACCOUNT_CLAUSE}
        echo "$UNREADY_PODS pods still not ready"
        sleep 5s
    done
    oc get routes ${SVC_ACCOUNT_CLAUSE}
}


function scaleDataService() {
    oc scale dc data-service --replicas=2 ${SVC_ACCOUNT_CLAUSE}
}

function createProject() {
    until oc new-project $PROJECT ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete project $PROJECT ${SVC_ACCOUNT_CLAUSE} || true
      sleep 10
    done
}

function createProjectIfNecessaryForNonNamedEnvs() {
    if ! $(isNamedEnvironment $TARGET); then
        createProject
    fi
}
