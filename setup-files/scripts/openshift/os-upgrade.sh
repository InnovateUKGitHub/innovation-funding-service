#!/usr/bin/env bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

if [[ ${TARGET} == "production" ]]
then
    PROJECT="production"
fi

REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com
INTERNAL_REGISTRY=172.30.80.28:5000

SVC_ACCOUNT_TOKEN=${bamboo_openshift_svc_account_token}
SVC_ACCOUNT_CLAUSE="--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://console.prod.ifs-test-clusters.com:443 --insecure-skip-tls-verify=true"

if [ -z "$SVC_ACCOUNT_TOKEN" ]
then
    REGISTRY_TOKEN=$(oc whoami -t)
else
    REGISTRY_TOKEN=${SVC_ACCOUNT_TOKEN}
fi

function uploadToRegistry() {
    docker tag innovateuk/data-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/data-service:${VERSION}
    docker tag innovateuk/project-setup-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/project-setup-service:${VERSION}
    docker tag innovateuk/project-setup-management-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}
    docker tag innovateuk/competition-management-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/competition-management-service:${VERSION}
    docker tag innovateuk/assessment-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/assessment-service:${VERSION}
    docker tag innovateuk/application-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/application-service:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -e unused -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/data-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/project-setup-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/competition-management-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/assessment-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/application-service:${VERSION}
}

function upgradeServices {
    oc export dc data-service ${SVC_ACCOUNT_CLAUSE} | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/data-service:${VERSION}#g" | oc apply ${SVC_ACCOUNT_CLAUSE} -f -
    sleep 90
    oc rollout status dc/data-service --request-timeout='5m' ${SVC_ACCOUNT_CLAUSE}

    oc export dc application-svc ${SVC_ACCOUNT_CLAUSE} | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/application-service:${VERSION}#g" | oc apply ${SVC_ACCOUNT_CLAUSE} -f -
    oc export dc assessment-svc ${SVC_ACCOUNT_CLAUSE} | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/assessment-service:${VERSION}#g" | oc apply ${SVC_ACCOUNT_CLAUSE} -f -
    oc export dc competition-mgt-svc ${SVC_ACCOUNT_CLAUSE} | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/competition-management-service:${VERSION}#g" | oc apply ${SVC_ACCOUNT_CLAUSE} -f -
    oc export dc project-setup-mgt-svc ${SVC_ACCOUNT_CLAUSE} | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}#g" | oc apply ${SVC_ACCOUNT_CLAUSE} -f -
    oc export dc project-setup-svc ${SVC_ACCOUNT_CLAUSE} | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/project-setup-service:${VERSION}#g" | oc apply ${SVC_ACCOUNT_CLAUSE} -f -

    sleep 90
    oc rollout status dc/application-svc --request-timeout='5m' ${SVC_ACCOUNT_CLAUSE}
    oc rollout status dc/assessment-svc --request-timeout='5m' ${SVC_ACCOUNT_CLAUSE}
    oc rollout status dc/competition-mgt-svc --request-timeout='5m' ${SVC_ACCOUNT_CLAUSE}
    oc rollout status dc/project-setup-mgt-svc --request-timeout='5m' ${SVC_ACCOUNT_CLAUSE}
    oc rollout status dc/project-setup-svc --request-timeout='5m' ${SVC_ACCOUNT_CLAUSE}
}



# Entry point
uploadToRegistry
upgradeServices