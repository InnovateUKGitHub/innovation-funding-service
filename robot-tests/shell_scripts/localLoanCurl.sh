#!/bin/bash

####################################################################################
####################################################################################
## This is to run the curl command in data service
####################################################################################
####################################################################################
#2022-04-11T12:15:45.000Z
IFS_AUTH_TOKEN=$1
application_id=$2
questionStatus=$3
questionDate=$4

# Define some functions for later use
DATASERVICE_POD=$(kubectl get pod -l app=data-service -o jsonpath="{.items[0].metadata.name}")

kubectl exec -it  $DATASERVICE_POD -- curl -v -X PATCH --header 'Content-Type:application/json' --header "IFS_AUTH_TOKEN:${IFS_AUTH_TOKEN}" --url http://localhost:8080/application-update/${application_id} --data "{\"questionSetupType\":\"LOAN_BUSINESS_AND_FINANCIAL_INFORMATION\",\"completionStatus\":\"${questionStatus}\",\"completionDate\":\"${questionDate}\"}"