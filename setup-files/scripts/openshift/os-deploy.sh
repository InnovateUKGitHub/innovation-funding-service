#!/bin/bash
set -ex

PROJECT=$1
TARGET=$2

if [[ ${TARGET} == "remote" ]]
then
    HOST=prod.ifs-test-clusters.com
else
    HOST=ifs-local
fi

ROUTE_DOMAIN=apps.$HOST
REGISTRY=721685138178.dkr.ecr.eu-west-2.amazonaws.com

echo "Deploying the $PROJECT Openshift PROJECTironment"

function tailorAppInstance() {
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<IMAP-ADDRESS>>/imap-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<ADMIN-ADDRESS>>/admin-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" shib-init/*.sh
}

function useContainerRegistry() {
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/init/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s/1.0-SNAPSHOT/1.0-$PROJECT/g" os-files-tmp/*.yml
    sed -i.bak "s/1.0-SNAPSHOT/1.0-$PROJECT/g" os-files-tmp/init/*.yml
    sed -i.bak "s/1.0-SNAPSHOT/1.0-$PROJECT/g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s# innovateuk/# ${REGISTRY}/innovateuk/#g" os-files-tmp/*.yml
    sed -i.bak "s# innovateuk/# ${REGISTRY}/innovateuk/#g" os-files-tmp/init/*.yml
    sed -i.bak "s# innovateuk/# ${REGISTRY}/innovateuk/#g" os-files-tmp/robot-tests/*.yml

    docker tag innovateuk/data-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/data-service:1.0-$PROJECT
    docker tag innovateuk/project-setup-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/project-setup-service:1.0-$PROJECT
    docker tag innovateuk/project-setup-management-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/project-setup-management-service:1.0-$PROJECT
    docker tag innovateuk/competition-management-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/competition-management-service:1.0-$PROJECT
    docker tag innovateuk/assessment-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/assessment-service:1.0-$PROJECT
    docker tag innovateuk/application-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/application-service:1.0-$PROJECT
    docker tag innovateuk/shib-init:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/shib-init:1.0-$PROJECT

    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAyEwggMdBgkqhkiG9w0BBwagggMOMIIDCgIBADCCAwMGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMIGgO965/W8vj5hXcAgEQgIIC1OEsFyGL2KFUORcIRnj96utwEPxfUZXqm00BgTX5l4G0fQCb2FL0Ql7TVkQNiK44qBw38bgfiCaZVHt4sriyD5KaY5V3XKQc2X7HuglXxrmdTEgko7gYbfCrPvF3D2gNSLjAsg/tDQvJR9UsBQCZQOlSShVB5iB+pH9NEpTEFQjbq9Qd93sc9/zd4NyFgxPskEyC6ZBVl1fpTKHfCG7ugP4Uw0omkCRxxRBlH4bajD3YmtjqIN3AOoLk/8mrVbt6MROPGthadCLiBxoUJobM/1cVL5O00HZreB6fQwYdKnJMjmSCgTh2/MaRGMjdwO7iNxWBA03HrSxm/KMO4GWNkulKIkIJBC1yEdaQEt9dgP1uzEIRVFU637p3zCmJEx4vwfd0bmTBd6q0dluFdaenfPQI/Y1zovkyixX6yW39/nGpFQsErlIVWqrdeLepqJzXktAJfYbHd5GERPdt6HhH7PPA7ANrvK0/EzyH387lG66k3E2htvm238U+C5ocfE2wLdK+IQhIjrX3bKef4xonxaiFTd+m+ru265CxDkmxPXccwEwtqVbjoeguxhouBBqel1yxB6ZrFsJJbDtQB74jdJXn0dUMAVsFmX4KcdNTsTWypqp3BDd3jDHVRkkcyFYvs0adWHS0G9wZxre4Fx8zepsyhy16IhCUwUgs1h4zHNFqlRdcXSOlrXsmu7QwuNwV5p40/vmU1H5dOpane0EdTljwZT3r7h938UxcE5ojVOajg/NYDaNN70bhh/W3KU0AMEXsJspAadR/p92DLvfHyaOEkciAaULU14B4PRWpN83FNWWzAnn1z9TI6eZwdEe/S8aBQrmrliPEbzqjReQZO/2lprLYttT1CYlEqrQJ65Ilaa1P4zyRVxaAtrjIl6aC4IxfFEyvvmqppEoMt0cQ8P/NveiFHrp7jRM+yISU1o4oK0IbPDHPGFFDahtCWUO15+VUCQs= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com

    docker push ${REGISTRY}/innovateuk/data-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/project-setup-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/project-setup-management-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/competition-management-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/assessment-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/application-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/shib-init:1.0-$PROJECT
}

function buildShibInit() {
    docker build -t innovateuk/shib-init:1.0-SNAPSHOT shib-init
}

function deploy() {
    oc new-project $PROJECT

    oc create -f os-files-tmp/1-aws-registry-secret.yml
    oc secrets add serviceaccount/default secrets/aws-secret-2 --for=pull
    rm -rf os-files-tmp/1-aws-registry-secret.yml

    if [[ ${TARGET} == "local" ]]
    then
        oc adm policy add-scc-to-user anyuid -n $PROJECT -z default
    else
        chmod 600 setup-files/scripts/openshift/ifs
        ssh-add setup-files/scripts/openshift/ifs
        ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ec2-user@52.56.119.142 "oc adm policy add-scc-to-user anyuid -n $PROJECT -z default"
    fi

    oc create -f os-files-tmp/
}

function blockUntilServiceIsUp() {
    UNREADY_PODS=1
    while [ ${UNREADY_PODS} -ne "0" ]
    do
        UNREADY_PODS=$(oc get pods -o custom-columns='NAME:{.metadata.name},READY:{.status.conditions[?(@.type=="Ready")].status}' | grep -v True | sed 1d | wc -l)
        oc get pods
        echo "$UNREADY_PODS pods still not ready"
        sleep 5s
    done
    oc get routes
}

function shibInit() {
    oc rsh $(oc get pods | grep ldap | awk '{ print $1 }') /usr/local/bin/ldap-delete-all-users.sh
    oc create -f os-files-tmp/init/6-shib-init.yml
}

function cleanUp() {
    rm -rf os-files-tmp
    rm -rf shibboleth
    rm -rf shib-init
}

function cloneConfig() {
    cp -r os-files os-files-tmp
    cp -r setup-files/scripts/openshift/shib-init shib-init
}

cleanUp
cloneConfig
tailorAppInstance
buildShibInit
if [[ ${TARGET} == "remote" ]]
then
    useContainerRegistry
fi
deploy
blockUntilServiceIsUp
shibInit
cleanUp
