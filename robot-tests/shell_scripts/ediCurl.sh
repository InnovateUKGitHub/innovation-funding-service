#!/bin/bash

####################################################################################
####################################################################################
## This is to run the curl command in data service
####################################################################################
####################################################################################
#2020-01-31T01:02:03Z
#Complete
#In Progress

IFS_AUTH_TOKEN=$1
ediStatus=$2
ediReviewDate=$3

auth_header= "IFS_AUTH_TOKEN:${IFS_AUTH_TOKEN}"
echo $auth_header

status= "{\"ediStatus\":\"${ediStatus}\",\"ediReviewDate\":\"${ediReviewDate}\"}"
echo $status

# Define some functions for later use
DATASERVICE_POD=$(kubectl get pod -l app=data-service -o jsonpath="{.items[0].metadata.name}")

kubectl exec -it  $DATASERVICE_POD -- curl -v -X PATCH --header 'Content-Type:application/json' --header "IFS_AUTH_TOKEN:${IFS_AUTH_TOKEN}" --url http://localhost:8080/user/v1/edi  --data "{\"ediStatus\":\"${ediStatus}\",\"ediReviewDate\":\"${ediReviewDate}\"}"