#!/bin/bash

oc login -u system:admin -n default > /dev/null

LOCAL_DOCKER_REGISTRY_IP=$(oc get svc docker-registry -n default -o jsonpath='{.spec.clusterIP}')
LOCAL_DOCKER_REGISTRY_PORT=$(oc get svc docker-registry -n default -o jsonpath='{.spec.ports..port}')

echo "$LOCAL_DOCKER_REGISTRY_IP:$LOCAL_DOCKER_REGISTRY_PORT"