#!/bin/sh
set -e

ENV=lf-project1
HOST=dev-projects.ifs-test-clusters.com

# Set up remote registry and project name params
rm -rf os-files-tmp
cp -r os-files os-files-tmp
sed -i "s#worth/#721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/#g" os-files-tmp/*
sed -i "s/<<SHIB-ADDRESS>>/$ENV.$HOST/g" os-files-tmp/*
sed -i "s/<<ADMIN-ADDRESS>>/admin-$ENV.$HOST/g" os-files-tmp/*
sed -i "s/1.0-SNAPSHOT/1.0-$ENV/g" os-files-tmp/*

# Build & tag Shib
rm -rf shibboleth
cp -r setup-files/scripts/docker/shibboleth shibboleth
sed -i "s/<<HOSTNAME>>/$ENV.$HOST/g" shibboleth/*
docker build -t 721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/shibboleth:1.0-$ENV shibboleth/

# Re-tag other images
docker tag worth/data-service:1.0-SNAPSHOT \
    721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/data-service:1.0-$ENV
docker tag worth/project-setup-service:1.0-SNAPSHOT \
    721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/project-setup-service:1.0-$ENV
docker tag worth/project-setup-management-service:1.0-SNAPSHOT \
    721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/project-setup-management-service:1.0-$ENV
docker tag worth/competition-management-service:1.0-SNAPSHOT \
    721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/competition-management-service:1.0-$ENV
docker tag worth/assessment-service:1.0-SNAPSHOT \
    721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/assessment-service:1.0-$ENV
docker tag worth/application-service:1.0-SNAPSHOT \
    721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/application-service:1.0-$ENV

# Push to ECR
docker push 721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/data-service:1.0-$ENV
docker push 721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/project-setup-service:1.0-$ENV
docker push 721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/project-setup-management-service:1.0-$ENV
docker push 721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/competition-management-service:1.0-$ENV
docker push 721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/assessment-service:1.0-$ENV
docker push 721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/application-service:1.0-$ENV
docker push 721685138178.dkr.ecr.eu-west-1.amazonaws.com/worth/shibboleth:1.0-$ENV

# Deploy
oc new-project $ENV
oc create -f os-files-tmp/1-aws-registry-secret.yml
oc secrets add serviceaccount/default secrets/aws-secret-2 --for=pull
rm -rf os-files-tmp/1-aws-registry-secret.yml
rm -rf os-files-tmp/11-scc.yml
oc adm policy add-scc-to-user anyuid -n $ENV -z default --config=setup-files/scripts/openshift/admin.kubeconfig

oc create -f os-files-tmp/

# Cleanup
rm -rf os-files-tmp
rm -rf shibboleth

oc get pods
oc get routes

