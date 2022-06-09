#!/bin/bash

####################################################################################
####################################################################################
## This is to run the curl command in data service
####################################################################################
####################################################################################
#2020-01-31T01:02:03Z

IFS_AUTH_TOKEN=$1
ediStatus=$2
ediReviewDate=$3

# Define some functions for later use
DATASERVICE_POD=$(kubectl get pod -l app=data-service -o jsonpath="{.items[0].metadata.name}")

kubectl exec -it  $DATASERVICE_POD -- curl -v -X PATCH --header 'Content-Type:application/json' --header 'IFS_AUTH_TOKEN:3036e2d4-dbe1-4d0b-80a2-da841dd1f1aa' --url http://localhost:8080/user/v1/edi  --data '{"ediStatus":"In Progress","ediReviewDate":"2076-01-22T12:02:03Z"}'