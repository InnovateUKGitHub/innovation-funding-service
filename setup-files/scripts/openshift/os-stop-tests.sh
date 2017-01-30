#!/bin/bash
set -e

PROJECT=$1

echo "Stopping tests on the $PROJECT Openshift environment"

function stopTests() {
    oc delete dc selenium-grid
    oc delete dc chrome
    oc delete dc robot-framework
    oc delete svc hub
}

stopTests
