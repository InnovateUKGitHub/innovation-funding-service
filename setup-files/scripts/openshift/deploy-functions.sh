#!/bin/bash


function getBuildLocation() {
    echo "build/resources/main"
}

function isNamedEnvironment() {

    TARGET=$1

    if [[ ${TARGET} != "ifs-prod" && ${TARGET} != "ifs-demo" && ${TARGET} != "ifs-uat" && ${TARGET} != "ifs-sysint" && ${TARGET} != "ifs-perf" ]]; then
        exit 1
    else
        exit 0
    fi
}

function isProductionEnvironment() {

    TARGET=$1

    if [[ ${TARGET} != "ifs-prod" ]]; then
        exit 1
    else
        exit 0
    fi
}

function isSysIntEnvironment() {

    TARGET=$1

    if [[ ${TARGET} != "ifs-sysint" ]]; then
        exit 1
    else
        exit 0
    fi
}

function isPerfEnvironment() {

    TARGET=$1

    if [[ ${TARGET} != "ifs-perf" ]]; then
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
    elif [[ ${TARGET} == "ifs-prod" ]]; then
      echo "apply-for-innovation-funding.service.gov.uk"
    else
      echo $(getClusterAddress)
    fi
}

function getRouteDomain() {

    TARGET=$1
    HOST=$2

    if [[ ${TARGET} == "ifs-prod" ]]; then
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

function setMinimumNumberOfReplicas() {

    echo "Setting application replicas"
    CURRENT_REPLICAS=$(oc describe dc/application-svc ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/4-application-service.yml

    echo "Setting front door replicas"
    CURRENT_REPLICAS=$(oc describe dc/front-door-svc ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/5-front-door-service.yml

    echo "Setting data replicas"
    CURRENT_REPLICAS=$(oc describe dc/data-service ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/31-data-service.yml

    echo "Setting finance data replicas"
    CURRENT_REPLICAS=$(oc describe dc/finance-data-service ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/32-finance-data-service.yml

    echo "Setting assessment replicas"
    CURRENT_REPLICAS=$(oc describe dc/assessment-svc ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/41-assessment-svc.yml

    echo "Setting competition replicas"
    CURRENT_REPLICAS=$(oc describe dc/competition-mgt-svc ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/42-competition-mgt-svc.yml

    echo "Setting idp replicas"
    CURRENT_REPLICAS=$(oc describe dc/idp ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/shib/56-idp.yml

    echo "Setting shib replicas"
    CURRENT_REPLICAS=$(oc describe dc/shib ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/shib/5-shib.yml

    echo "Setting project management replicas"
    CURRENT_REPLICAS=$(oc describe dc/project-setup-mgt-svc ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/43-project-setup-mgt-svc.yml

    echo "Setting project setup replicas"
    CURRENT_REPLICAS=$(oc describe dc/project-setup-svc ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/44-project-setup-svc.yml

    echo "Setting registration replicas"
    CURRENT_REPLICAS=$(oc describe dc/registration-svc ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/ifs-services/45-registration-svc.yml

    echo "Setting survey data replicas"
    CURRENT_REPLICAS=$(oc describe dc/survey-data-service ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/survey/survey-data-service.yml

    echo "Setting survey replicas"
    CURRENT_REPLICAS=$(oc describe dc/survey-svc ${SVC_ACCOUNT_CLAUSE} | grep -m1 Replicas: | awk '{ print $2}')
    sed -i.bak "s/replicas: 1/replicas: ${CURRENT_REPLICAS}/g" $(getBuildLocation)/survey/survey-service.yml
}

function tailorAppInstance() {
    if [[ ${TARGET} == "ifs-prod" || ${TARGET} == "ifs-uat" || ${TARGET} == "ifs-perf" ]]
    then
        setMinimumNumberOfReplicas
    fi
}

function useContainerRegistry() {
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/**/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/**/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/**/*.yml
}

function useNexusRegistry() {
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" $(getBuildLocation)/**/*.yml
    sed -i.bak "s# innovateuk/# ${NEXUS_REGISTRY}/release/#g" $(getBuildLocation)/**/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" $(getBuildLocation)/**/*.yml
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

function pushAnonymisedDatabaseDumpImages() {
    docker tag innovateuk/db-anonymised-data:latest \
        ${REGISTRY}/${PROJECT}/db-anonymised-data:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/db-anonymised-data:${VERSION}
}

function blockUntilServiceIsUp() {
    while oc get pods ${SVC_ACCOUNT_CLAUSE} 2>&1 | grep "No resources found."; do
        echo "No pods are deployed yet.."
        sleep 15
    done

    RETRIES=0
    UNREADY_PODS=1
    UNSATISFIED_DEPLOYMENTS=1
    while [ ${UNREADY_PODS} -ne "0" ] || [ ${UNSATISFIED_DEPLOYMENTS} -ne "0" ];
    do
        UNREADY_PODS=$(oc get pods ${SVC_ACCOUNT_CLAUSE} -o custom-columns='NAME:{.metadata.name},READY:{.status.conditions[?(@.type=="Ready")].status}' | grep -v True | sed 1d | wc -l)
        UNSATISFIED_DEPLOYMENTS=$(oc get dc ${SVC_ACCOUNT_CLAUSE} | sed 1d | awk '{ print $4 }' | grep 0 | wc -l)
        ERRORRED_PODS=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep Error | wc -l)
        DEPLOY_PODS=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep deploy | wc -l)
        ERRORRED_DEPLOY_PODS=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep deploy | grep Error | wc -l)
        ERRORRED_CRASHLOOPING_PODS=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep -E "CrashLoopBackOff|Error" | wc -l)
        OUTOFPODS=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep -E "OutOfpods" | wc -l)

        if [ ${DEPLOY_PODS} -eq "0" ]; then
            if [ ${ERRORRED_PODS} -ne "0" ]; then
                echo "$ERRORRED_PODS pods stuck in error state.."
                POD=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep Error | awk '{ print $1 }')
                if ! (( $(isNamedEnvironment ${TARGET}) )); then
                    oc logs ${SVC_ACCOUNT_CLAUSE} $POD
                fi
                exit 1
            fi
        else
            if [ ${ERRORRED_DEPLOY_PODS} -ne "0" ]; then
                if [ "$RETRIES" -lt 1 ]; then
                    echo "$ERRORRED_DEPLOY_PODS deploy pods in error state.. Retrying"
                    RETRIES=$((RETRIES + 1))
                    POD=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep deploy | grep Error | awk '{ print $1 }')
                    oc deploy ${SVC_ACCOUNT_CLAUSE} --retry dc/${POD%-1-deploy}
                else
                    echo "$ERRORRED_DEPLOY_PODS deploy pods stuck in error state after $RETRIES retries.. Exiting"
                    exit 1
                fi
            fi

            if [ ${ERRORRED_CRASHLOOPING_PODS} -ne "0" ]; then
                POD=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep -E "CrashLoopBackOff|Error" | awk '{ print $1 }')
                SINCE=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep -E "CrashLoopBackOff|Error" | awk '{ print $5 }')
                echo "$POD is crashlooping for ${SINCE%m} minutes. Pod logs are:"
                if [ ${SINCE%m} -gt "5" ]; then
                    if ! (( $(isNamedEnvironment ${TARGET}) )); then
                        oc logs ${SVC_ACCOUNT_CLAUSE} $POD
                    fi
                fi
            fi
        fi

        oc get pods ${SVC_ACCOUNT_CLAUSE} -o wide
        echo "$UNREADY_PODS pods still not ready.."
        echo "$UNSATISFIED_DEPLOYMENTS deployments still not ready.."
        
        if [ ${OUTOFPODS} -ne "0" ]; then
            echo "CLUSTER IS FULL - Delete some projects and retry"
            exit 1
        fi

        sleep 10s
    done
    oc get routes ${SVC_ACCOUNT_CLAUSE}
}

function createProject() {
    until oc new-project $PROJECT ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete project $PROJECT ${SVC_ACCOUNT_CLAUSE} || true
      sleep 10
    done
}

function getClusterAddress() {
  echo $(cat gradle.properties | grep openshiftDomain | cut -d'=' -f2)
}

function getRemoteRegistryUrl() {
  echo "docker-registry.default.svc:5000"
}

function getNexusRegistryUrl() {
  echo "docker-ifs.devops.innovateuk.org"
}

function getNexusCredentials() {
    echo "${bamboo_openshift_svc_account_token}"
}