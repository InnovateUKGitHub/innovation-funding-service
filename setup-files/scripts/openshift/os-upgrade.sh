#!/usr/bin/env bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
HOST=$(getHost $TARGET)
ROUTE_DOMAIN=$(getRouteDomain $TARGET $HOST)
REGISTRY=$(getRegistry)
INTERNAL_REGISTRY=$(getInternalRegistry)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)
REGISTRY_TOKEN=$SVC_ACCOUNT_TOKEN

function upgradeServices {
    # Deploying finance-data-service before data-service as latter submits updates to former.
    # rolloutStatus checks ensure that service has been deployed successfully before proceeding further.
    oc apply -f $(getBuildLocation)/ifs-services/32-finance-data-service.yml ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "finance-data-service"

    # data-service
    oc apply -f $(getBuildLocation)/ifs-services/31-data-service.yml ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus "data-service"

    # services
    oc apply -f $(getBuildLocation)/ifs-services/4-application-service.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/ifs-services/42-competition-mgt-svc.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/ifs-services/43-project-setup-mgt-svc.yml ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus application-svc
    rolloutStatus competition-mgt-svc
    rolloutStatus project-setup-mgt-svc

    oc apply -f $(getBuildLocation)/ifs-services/5-front-door-service.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/ifs-services/41-assessment-svc.yml ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus front-door-svc
    rolloutStatus assessment-svc

    oc apply -f $(getBuildLocation)/ifs-services/44-project-setup-svc.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/ifs-services/45-registration-svc.yml ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus project-setup-svc
    rolloutStatus registration-svc

    oc apply -f $(getBuildLocation)/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/shib/56-idp.yml ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus shib
    rolloutStatus idp

    # The SIL stub is required in all environments, in one form or another, except for production
    if ! $(isProductionEnvironment ${TARGET}); then
        oc apply -f $(getBuildLocation)/sil-stub/80-sil-stub.yml ${SVC_ACCOUNT_CLAUSE}
    fi

    # conditionally deploy prototypes service
    if $(isSysIntEnvironment ${TARGET}); then
        oc apply -f $(getBuildLocation)/prototypes/46-prototypes-service.yml ${SVC_ACCOUNT_CLAUSE}
    fi

    # conditionally deploy zipkin
    if $(isPerfEnvironment ${TARGET}); then
        oc apply -f $(getBuildLocation)/zipkin/70-zipkin.yml ${SVC_ACCOUNT_CLAUSE}
        oc apply -f $(getBuildLocation)/mysql/3-zipkin-mysql.yml ${SVC_ACCOUNT_CLAUSE}
    fi

    watchSilStubAndPrototypesStatus

    upgradeSurvey

    upgradeEuGrantRegistration

}

function upgradeSurvey {
    # Survey service
    oc apply -f $(getBuildLocation)/survey/survey-data-service.yml ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "survey-data-service"
    oc apply -f $(getBuildLocation)/survey/survey-service.yml ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "survey-svc"
}

function upgradeEuGrantRegistration {
    oc apply -f $(getBuildLocation)/eu-grant-registration/eu-grant-registration-data-service.yml ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "eu-grant-registration-data-service"
    oc apply -f $(getBuildLocation)/eu-grant-registration/eu-grant-registration-service.yml ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "eu-grant-registration-service"
}

function forceReloadSurvey {
    oc rollout latest dc/survey-data-service ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "survey-data-service"
    oc rollout latest dc/survey-svc ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "survey-svc"
}

function forceReloadEuGrantRegistration {
    oc rollout latest dc/eu-grant-registration-data-service ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "eu-grant-registration-data-service"
    oc rollout latest dc/eu-grant-registration-svc ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus "eu-grant-registration-svc"
}

function forceReload {
    oc rollout latest dc/finance-data-service ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus finance-data-service

    oc rollout latest dc/data-service ${SVC_ACCOUNT_CLAUSE}
    rolloutStatus data-service

    oc rollout latest dc/application-svc ${SVC_ACCOUNT_CLAUSE}
    oc rollout latest dc/competition-mgt-svc ${SVC_ACCOUNT_CLAUSE}
    oc rollout latest dc/project-setup-mgt-svc ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus application-svc
    rolloutStatus competition-mgt-svc
    rolloutStatus project-setup-mgt-svc

    oc rollout latest dc/front-door-svc ${SVC_ACCOUNT_CLAUSE}
    oc rollout latest dc/assessment-svc ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus front-door-svc
    rolloutStatus assessment-svc

    oc rollout latest dc/project-setup-svc ${SVC_ACCOUNT_CLAUSE}
    oc rollout latest dc/registration-svc ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus project-setup-svc
    rolloutStatus registration-svc

    oc rollout latest dc/idp ${SVC_ACCOUNT_CLAUSE}
    oc rollout latest dc/shib ${SVC_ACCOUNT_CLAUSE}

    rolloutStatus idp
    rolloutStatus shib

    # The SIL stub is required in all environments, in one form or another, except for production
    if ! $(isProductionEnvironment ${TARGET}); then
        oc rollout latest dc/sil-stub ${SVC_ACCOUNT_CLAUSE}
    fi

    # conditionally deploy prototypes service
    if $(isSysIntEnvironment ${TARGET}); then
        oc rollout latest dc/prototypes-svc ${SVC_ACCOUNT_CLAUSE}
    fi

    watchSilStubAndPrototypesStatus

    forceReloadSurvey

    forceReloadEuGrantRegistration
}

function watchSilStubAndPrototypesStatus {

    # The SIL stub is required in all environments, in one form or another, except for production
    if ! $(isProductionEnvironment ${TARGET}); then
        rolloutStatus sil-stub
    fi

    # conditionally check prototypes service
    if $(isSysIntEnvironment ${TARGET}); then
        rolloutStatus prototypes-svc
    fi
}

function rolloutStatus {
    FINISHED=0
    while [  $FINISHED -ne 1 ]; do
        RESULT=$(oc rollout status  dc/$1 ${SVC_ACCOUNT_CLAUSE} 2>&1) || true # the || true is to eat up all non-zero exit codes
        echo "$RESULT"

        if [[ ${RESULT} != *"timed out"* ]]; then
          FINISHED=1
        fi
        if [[ ${RESULT} == *"failed progressing"* ]]; then
          echo "FAILED TO DEPLOY $1"
          echo "Try triggering the deploy again with oc rollout latest dc/application-svc in project $PROJECT and check oc logs and oc describe heavily"
          exit 1
        fi
    done
}

# Entry point
tailorAppInstance

if [[ ${TARGET} == "local" ]]
then
    replacePersistentFileClaim
fi

useContainerRegistry
upgradeServices

if [[ ${bamboo_openshift_force_reload} == "true" ]]
then
    forceReload
fi

if [[ ${TARGET} == "production" || ${TARGET} == "uat" || ${TARGET} == "perf" ]]
then
    # We only scale up data-serviced once started up and performed the Flyway migrations on one thread
    scaleDataService
    scaleFinanceDataService
    scaleSurveyDataService
fi