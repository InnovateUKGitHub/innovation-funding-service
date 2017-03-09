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

SVC_ACCOUNT_TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJwcm9kdWN0aW9uIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImJhbWJvLXByb2R1Y3Rpb24tdG9rZW4tN2RtNG0iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYmFtYm8tcHJvZHVjdGlvbiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6Ijk1MmJjMTIxLTA0ZjUtMTFlNy1hMzE1LTA2MWM1NDEyYTQxMSIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpwcm9kdWN0aW9uOmJhbWJvLXByb2R1Y3Rpb24ifQ.OExhi78AlhgMnk1YfiW4J_z6_F-P9xXZ3_Wq5p53r_KY1bIqzVemJqep643FN02kOxV-m1iel_Twq0sh37XiqRYp7RMo2mg4z2bWgKD82bNCu8F3K2bHr8KAIXnKvdp05rjxRWv0Rq5v4BKCClZ3Pemj4iPQK67EgDfsoZGHjKcfH-_6Fv205oShu0ERD71oTRiVCMQs1aWcGTbHjCd7Oz0XhmS-Kruup7G5bdR5kZL2HNn6xFaz8P8GN7kaZQMbRyamGpN70nchYU1mI8mdJMHuWEjOclbbPa_AHFSFre1R5J5YOloag9FlAejcgz-Ahcf_bYhp1-efjdGdVoe6Fw"
SVC_ACCOUNT_CLAUSE="--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://console.prod.ifs-test-clusters.com:443 --insecure-skip-tls-verify=true"

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
    oc export dc data-service ${SVC_ACCOUNT_CLAUSE} | sed "s#image:.*#image: ${INTERNAL_REGISTRY}/${PROJECT}/data-service:${VERSION}#g" | oc apply ${SVC_ACCOUNT_CLAUSE} -f -
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