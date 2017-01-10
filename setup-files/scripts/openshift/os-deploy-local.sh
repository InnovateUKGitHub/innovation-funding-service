#!/bin/sh
set -e

# oc cluster up --routing-suffix=ifs.local.dev

ENV=lf-project1

# Set up remote registry and project name params
rm -rf os-files-tmp
cp -r os-files os-files-tmp
sed -i "s/<<HOSTNAME>>/ifs.local.dev/g" os-files-tmp/*
sed -i "s/1.0-SNAPSHOT/1.0-$ENV/g" os-files-tmp/*

# Build & tag Shib
rm -rf shibboleth
cp -r setup-files/scripts/docker/shibboleth shibboleth
sed -i "s/<<HOSTNAME>>/shib-$ENV.ifs.local.dev/g" shibboleth/*
docker build -t worth/shibboleth:1.0-$ENV shibboleth/

# Re-tag other images
docker tag worth/data-service:1.0-SNAPSHOT \
    worth/data-service:1.0-$ENV
docker tag worth/project-setup-service:1.0-SNAPSHOT \
    worth/project-setup-service:1.0-$ENV
docker tag worth/project-setup-management-service:1.0-SNAPSHOT \
    worth/project-setup-management-service:1.0-$ENV
docker tag worth/competition-management-service:1.0-SNAPSHOT \
    worth/competition-management-service:1.0-$ENV
docker tag worth/assessment-service:1.0-SNAPSHOT \
    worth/assessment-service:1.0-$ENV
docker tag worth/application-service:1.0-SNAPSHOT \
    worth/application-service:1.0-$ENV

# Deploy
oc new-project $ENV
rm -rf os-files-tmp/1-aws-registry-secret.yml
rm -rf os-files-tmp/11-scc.yml
oc adm policy add-scc-to-user anyuid -n $ENV -z default --config=/var/lib/origin/openshift.local.config/master/admin.kubeconfig

oc create -f os-files-tmp/

# Cleanup
rm -rf os-files-tmp
rm -rf shibboleth

oc get pods
oc get routes

