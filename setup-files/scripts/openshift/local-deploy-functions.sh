#!/bin/bash

function getLocalRegistryUrl() {

    LOCAL_DOCKER_REGISTRY_IP=$(oc get svc docker-registry -n default -o jsonpath='{.spec.clusterIP}')
    LOCAL_DOCKER_REGISTRY_PORT=$(oc get svc docker-registry -n default -o jsonpath='{.spec.ports..port}')
    echo "$LOCAL_DOCKER_REGISTRY_IP:$LOCAL_DOCKER_REGISTRY_PORT"
}

function replacePersistentFileClaim() {

    sed -i.bak "s#persistentVolumeClaim:#emptyDir: {}#g" $(getBuildLocation)/31-data-service.yml
    sed -i.bak "s#claimName: file-upload-claim##g" $(getBuildLocation)/31-data-service.yml
    sed -i.bak "s/imagePullPolicy: Always/imagePullPolicy: IfNotPresent/g" $(getBuildLocation)/31-data-service.yml

}