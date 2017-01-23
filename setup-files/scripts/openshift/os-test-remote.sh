#!/bin/sh
set -e

ENV=lf-project4
HOST=dev.ifs-test-clusters.com

# Set up remote registry and project name params
rm -rf os-files-tmp
cp -r os-files os-files-tmp
sed -i.bak "s#worth/#721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/#g" os-files-tmp/robot-tests/*.yml
sed -i.bak "s/1.0-SNAPSHOT/1.0-$ENV/g" os-files-tmp/robot-tests/*.yml

# Robot tests
docker build -t 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/robot-framework:1.0-$ENV robot-tests/
docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/robot-framework:1.0-$ENV

oc create -f os-files-tmp/robot-tests/7-selenium-grid.yml
sleep 5
oc create -f os-files-tmp/robot-tests/8-robot.yml

# Cleanup
rm -rf os-files-tmp
