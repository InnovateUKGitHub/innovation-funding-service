#!/bin/sh
set -e

/bin/echo -e "\e[90m"
oc cluster up #--routing-suffix=ifs-local-dev
/bin/echo -e "\e[0m"

DASHBOARD_URL=$(oc status | head -n 1 | awk '{print $8}')

/bin/echo -e "\e[32m"
echo "--------------------------------------------------------"
echo ""
echo "\tOpenshift is up on $DASHBOARD_URL"
echo ""
echo "\tPress a key then locate the authentication token and return to this screen"
echo ""
echo "--------------------------------------------------------"
/bin/echo -e "\e[0m"
read DUMMYVAR

x-www-browser $DASHBOARD_URL#/console/command-line >/dev/null 2>/dev/null
echo "Please provide the auth token: "
read AUTH_TOKEN

echo "oc login $DASHBOARD_URL --token=$AUTH_TOKEN"
oc login $DASHBOARD_URL --token=$AUTH_TOKEN

PROJECT_NAME=test-project
echo oc new-project $PROJECT_NAME
oc new-project $PROJECT_NAME

oc adm policy add-scc-to-user anyuid -n $PROJECT_NAME -z default --config=/var/lib/origin/openshift.local.config/master/admin.kubeconfig

echo "Please modify the SCC anyuid with SYS_PTRACE"
read DUMMYVAR

oc create -f os-files/


# cat ~/.docker/config.json | base64
oc secrets add serviceaccount/default secrets/aws-secret-2 --for=pull



# wait until the app is up

# change ifs-local-dev to the dahsboard ip
# change ifs-database ip to the mysql pod's custer ip
# ./gradlew syncShib






