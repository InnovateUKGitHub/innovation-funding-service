#!/bin/bash

function loginSystemAdmin() {

    oc login -u system:admin -n default > /dev/null
}

function loginUserWithToken() {

    TOKEN=$1
    oc login https://192.168.1.10:8443 --token=$TOKEN > /dev/null
}

function getLocalRegistryUrl() {

    TOKEN=$(oc whoami -t)

    loginSystemAdmin

    LOCAL_DOCKER_REGISTRY_IP=$(oc get svc docker-registry -n default -o jsonpath='{.spec.clusterIP}')
    LOCAL_DOCKER_REGISTRY_PORT=$(oc get svc docker-registry -n default -o jsonpath='{.spec.ports..port}')

    loginUserWithToken $TOKEN

    echo "$LOCAL_DOCKER_REGISTRY_IP:$LOCAL_DOCKER_REGISTRY_PORT"
}

function replacePersistentFileClaim() {

    sed -i.bak "s#persistentVolumeClaim:#emptyDir: {}#g" os-files-tmp/31-data-service.yml
    sed -i.bak "s#claimName: file-upload-claim##g" os-files-tmp/31-data-service.yml
    sed -i.bak "s/imagePullPolicy: Always/imagePullPolicy: IfNotPresent/g" os-files-tmp/31-data-service.yml

}