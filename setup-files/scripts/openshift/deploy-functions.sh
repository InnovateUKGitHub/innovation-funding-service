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

function isSysIntEnvironment() {

    TARGET=$1

    if [[ ${TARGET} != "sysint" ]]; then
        exit 1
    else
        exit 0
    fi
}

function isServiceEnabled() {

    SERVICE=$1
    GRADLE_PROPERTIES_FILE=~/.gradle/gradle.properties

    ENABLED=`cat "$GRADLE_PROPERTIES_FILE" | grep -v '^#' | grep "$SERVICE" | cut -d'=' -f2`

    if [[ -n "$ENABLED" && "$ENABLED" == true ]]; then
        exit 0
    else
        exit 1
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
      echo $(getClusterAddress)
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
        echo "docker-registry-default.apps."$(getClusterAddress)
    fi
}

function getInternalRegistry() {

    if [[ (${TARGET} == "local") ]]; then
        echo "$(getLocalRegistryUrl)"
    else
        echo "$(getRemoteRegistryUrl)"
    fi
}

function getSvcAccountClause() {

    TARGET=$1
    PROJECT=$2
    SVC_ACCOUNT_TOKEN=$3

    if [[ (${TARGET} == "local") ]]; then
        SVC_ACCOUNT_CLAUSE_SERVER_PART='localhost:8443'
    else
        SVC_ACCOUNT_CLAUSE_SERVER_PART="console."$(getClusterAddress)":443"
    fi

    echo "--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://${SVC_ACCOUNT_CLAUSE_SERVER_PART} --insecure-skip-tls-verify=true"
}

function convertFileToBlock() {
    cat "$1" | tr -d '\r' | tr '\n' '^' | sed "s/\^/<<>>/g" | rev | cut -c 5- | rev
}


function injectFlywayVariables() {
    [ -z "$FLYWAY_LOCATIONS" ] && { echo "Set FLYWAY_LOCATIONS environment variable"; exit -1; }
    sed -i.bak "s#<<FLYWAY-LOCATIONS>>#${FLYWAY_LOCATIONS}#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#<<FLYWAY-LOCATIONS>>#${FLYWAY_LOCATIONS}#g" $(getBuildLocation)/db-baseline/*.yml
    sed -i.bak "s#<<FLYWAY-BASELINE-VERSION>>#${FLYWAY_BASELINE_VERSION}#g" $(getBuildLocation)/db-baseline/*.yml
    sed -i.bak "s#<<FLYWAY-BASELINE-DESCRIPTION>>#${FLYWAY_BASELINE_DESCRIPTION}#g" $(getBuildLocation)/db-baseline/*.yml

    [ -z "$SYSTEM_USER_UUID" ] && { echo "Set SYSTEM_USER_UUID environment variable"; exit -1; }
    sed -i.bak "s#<<SYSTEM-USER-UUID>>#${SYSTEM_USER_UUID}#g" $(getBuildLocation)/db-reset/*.yml
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


    if [ -z "$SSLCERTFILE" ]; then echo "Set SSLCERTFILE, SSLCACERTFILE, and SSLKEYFILE environment variables"; exit -1; fi
    sed -i.bak -e $"s#<<SSLCERT>>#$(convertFileToBlock $SSLCERTFILE)#g" -e 's/<<>>/\\n/g' $(getBuildLocation)/shib/*.yml
    sed -i.bak -e $"s#<<SSLCACERT>>#$(convertFileToBlock $SSLCACERTFILE)#g" -e 's/<<>>/\\n/g' $(getBuildLocation)/shib/*.yml
    sed -i.bak -e $"s#<<SSLKEY>>#$(convertFileToBlock $SSLKEYFILE)#g" -e 's/<<>>/\\n/g' $(getBuildLocation)/shib/*.yml


    if [[ ${TARGET} == "production" || ${TARGET} == "uat" || ${TARGET} == "perf"  ]]
    then
        sed -i.bak "s/replicas: 1/replicas: 2/g" $(getBuildLocation)/4*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" $(getBuildLocation)/5-front-door-service.yml
    fi
}

function useContainerRegistry() {
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/sil-stub/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/db-baseline/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/fractal/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/robot-tests/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/mysql/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/finance-data-service-sync/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/prototypes/*.yml


    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/sil-stub/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/db-baseline/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/fractal/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/mysql-client/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/shib/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/robot-tests/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/mysql/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/mail/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/finance-data-service-sync/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/prototypes/*.yml

    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/db-reset/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/db-baseline/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/fractal/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/db-anonymised-data/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/robot-tests/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/finance-data-service-sync/*.yml
}


function pushDBResetImages() {
    docker tag innovateuk/dbreset:latest \
        ${REGISTRY}/${PROJECT}/dbreset:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/dbreset:${VERSION}
}

function pushDBBaselineImages() {
    docker tag innovateuk/dbbaseline:latest \
        ${REGISTRY}/${PROJECT}/dbbaseline:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/dbbaseline:${VERSION}
}

function pushFinanceDataServiceSyncImages() {
    docker tag innovateuk/finance-data-service-sync:latest \
        ${REGISTRY}/${PROJECT}/finance-data-service-sync:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/finance-data-service-sync:${VERSION}
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

function scaleFinanceDataService() {
    oc scale dc finance-data-service --replicas=2 ${SVC_ACCOUNT_CLAUSE}
}

function createProject() {
    until oc new-project $PROJECT ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete project $PROJECT ${SVC_ACCOUNT_CLAUSE} || true
      sleep 10
    done
}

function getClusterAddress() {
    echo "prod.ifs-test-clusters.com"
#     echo "dev-nige-1.dev.ifs-test-clusters.com"
}

function getRemoteRegistryUrl() {
        echo "172.30.80.28:5000"
#        echo "172.30.114.178:5000"
}