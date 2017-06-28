#!/bin/bash

function isNamedEnvironment() {

    TARGET=$1

    if [[ ${TARGET} != "production" && ${TARGET} != "demo" && ${TARGET} != "uat" && ${TARGET} != "sysint" && ${TARGET} != "perf" ]]; then
        exit 1
    else
        exit 0
    fi
}

function getProjectName() {

    PROJECT=$1
    TARGET=$2

    if $(isNamedEnvironment $TARGET); then
        echo "$TARGET"
    else
        echo "$PROJECT"
    fi
}

function getSvcAccountToken() {

    if [ -z "$bamboo_openshift_svc_account_token" ]; then
        echo "$(oc whoami -t)";
    else
        echo "${bamboo_openshift_svc_account_token}";
    fi
}

function getHost() {

    TARGET=$1

    if [[ (${TARGET} == "local") ]]; then
      echo "ifs-local"
    elif [[ ${TARGET} == "production" ]]; then
      echo "apply-for-innovation-funding.service.gov.uk"
    else
      echo "prod.ifs-test-clusters.com"
    fi
}

function getRouteDomain() {

    TARGET=$1
    HOST=$2

    if [[ ${TARGET} == "production" ]]; then
      echo "$HOST"
    else
      echo "apps.$HOST"
    fi
}

function getRegistry() {

    if [[ (${TARGET} == "local") ]]; then
        echo "$(getLocalRegistryUrl)"
    else
        echo "docker-registry-default.apps.prod.ifs-test-clusters.com"
    fi
}

function getInternalRegistry() {

    if [[ (${TARGET} == "local") ]]; then
        echo "$(getLocalRegistryUrl)"
    else
        echo "172.30.80.28:5000"
    fi
}

function getSvcAccountClause() {

    TARGET=$1
    PROJECT=$2
    SVC_ACCOUNT_TOKEN=$3

    if [[ (${TARGET} == "local") ]]; then
        SVC_ACCOUNT_CLAUSE_SERVER_PART='localhost:8443'
    else
        SVC_ACCOUNT_CLAUSE_SERVER_PART='console.prod.ifs-test-clusters.com:443'
    fi

    echo "--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://${SVC_ACCOUNT_CLAUSE_SERVER_PART} --insecure-skip-tls-verify=true"
}