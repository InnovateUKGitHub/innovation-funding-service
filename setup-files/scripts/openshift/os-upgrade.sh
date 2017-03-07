#!/usr/bin/env bash

set -ex

PROJECT=$1
TARGET=$2
VERSION=$3

if [[ ${TARGET} == "production" ]]
then
    PROJECT="production"
fi

REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com
INTERNAL_REGISTRY=172.30.80.28:5000

SVC_ACCOUNT_TOKEN="L2uE9Zka9JJnzBzcM0ItQQ3is26bO45EVzRh9SQJ9rA"
REGISTRY_TOKEN=${SVC_ACCOUNT_TOKEN}

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
    oc export dc data-service | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/data-service:${VERSION}#g" | oc apply -f -
    oc rollout status dc/data-service

    oc export dc application-svc | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/application-service:${VERSION}#g" | oc apply -f -
    oc export dc assessment-svc | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/assessment-service:${VERSION}#g" | oc apply -f -
    oc export dc competition-mgt-svc | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/competition-management-service:${VERSION}#g" | oc apply -f -
    oc export dc project-setup-mgt-svc | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}#g" | oc apply -f -
    oc export dc project-setup-svc | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/project-setup-service:${VERSION}#g" | oc apply -f -

    oc rollout status dc/application-svc
    oc rollout status dc/assessment-svc
    oc rollout status dc/competition-mgt-svc
    oc rollout status dc/project-setup-mgt-svc
    oc rollout status dc/project-setup-svc
}



# Entry point
uploadToRegistry
upgradeServices