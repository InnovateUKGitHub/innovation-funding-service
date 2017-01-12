#!/bin/bash
# A Munro: Startup openshift origin and load in some pods

oc cluster up && {
  oc new-project test-project
  oc adm policy add-scc-to-user anyuid -n test-project -z default --config=/var/lib/origin/openshift.local.config/master/admin.kubeconfig
  oc edit scc anyuid --config=/var/lib/origin/openshift.local.config/master/admin.kubeconfig
  oc create -f os-files

  echo Waiting for pods to startup...
  c=0
  while [ "$(oc get pods|awk '/Running/ {c++} END {print c}')" != "11" ]
  do
    sleep 2
    ((c++))
    [ $c -eq 15 ] && {
      echo Pod creation went wrong. Investigate.
      exit 1
    }
  done

  sleep 10 # Need a further sleep...
  ldap=$(docker ps |awk '/ldap:/ {print $1}')
  [ -z "$ldap" ] && {
    echo ldap container not running. Investigate and fix.
    exit 1
  }

  docker cp setup-files/scripts/docker/_delete-shib-users-remote.sh $ldap:/usr/local/bin/
  docker exec -it $ldap /usr/local/bin/_delete-shib-users-remote.sh

  echo "Refreshing ldap with users in ifs db"
#  ifsdb=$(oc get svc ifs-database|awk '/ifs-database/ {print $2}')
#  [ -z "$ifsdb" ] && {
#    echo cannot determine IP for ifs-database openshift service. Investigate.
#    exit 1
#  }

  ./gradlew syncShib
}
