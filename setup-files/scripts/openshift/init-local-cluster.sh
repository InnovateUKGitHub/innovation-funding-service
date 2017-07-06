#!/bin/bash

# (Re)start the local OpenShift cluster
oc cluster down
oc cluster up

# give additional rights to the "developer" user
oc login -u system:admin
oc policy add-role-to-user admin developer -n default

# and login as the developer user
oc login -u=developer -p=developer