#!/bin/bash

oc login -u system:admin -n default > /dev/null
oc adm policy add-scc-to-user anyuid -n $PROJECT -z default