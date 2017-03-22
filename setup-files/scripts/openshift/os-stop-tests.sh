#!/bin/bash
set -e

PROJECT=$(oc project -q)

echo "Stopping tests on the current oc project ($PROJECT)"

function stopTests() {
    oc delete dc selenium-grid
    oc delete dc chrome
    oc delete dc robot-framework
    oc delete svc hub
}

stopTests
