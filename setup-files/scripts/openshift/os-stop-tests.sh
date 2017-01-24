#!/bin/bash
set -e

ENV=$1
HOST=dev.ifs-test-clusters.com

echo "Stopping tests on the $ENV Openshift environment"

function stopTests() {
    oc delete dc selenium-grid
    oc delete dc chrome
    oc delete dc robot-framework
    oc delete svc hub
}

stopTests
