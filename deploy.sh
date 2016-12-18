#!/usr/bin/env bash

oc cluster up --routing-suffix=ifs-local-dev

oc login https://10.10.67.180:8443 --token=OeAK8IZlponYagtpILCjJRi0q2Tm52hwP07IYvSOf5Y

oc new-project test-project

oc adm policy add-scc-to-user anyuid -n test-project -z default --config=/var/lib/origin/openshift.local.config/master/admin.kubeconfig

oc create -f os-files/
