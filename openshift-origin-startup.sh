#!/bin/bash
# A Munro: Startup openshift origin and load in some pods

s=0
for i in \
orangebus/ifs-ldap \
orangebus/ifs-shib-sp \
orangebus/ifs-shib-idp \
worth/project-setup-service \
worth/project-setup-management-service \
worth/competition-management-service \
worth/assessment-service \
worth/application-service \
worth/data-service
do
   [ -z "$(docker images -q $i)" ] && {
      echo Image $i not loaded in docker. Make sure its loaded.
      s=1
   }
done

[ $s -eq 1 ] && exit 1

oc cluster up && {
  oc new-project test-project
  oc adm policy add-scc-to-user anyuid -n test-project -z default --config=/var/lib/origin/openshift.local.config/master/admin.kubeconfig
  oc edit scc anyuid --config=/var/lib/origin/openshift.local.config/master/admin.kubeconfig
  oc create -f os-files

  echo Waiting for pods to startup...
  c=0
  while [ "$(oc get pods|awk '/Running/ {c++} END {print c}')" != "12" ]
  do
    sleep 2
    ((c++))
    [ $c -eq 15 ] && {
      echo Pod creation went wrong. Investigate.
      exit 1
    }
  done

  sleep 10 # Need a further sleep...

  ldap=$(docker ps |awk '/k8s_ldap\./ {print $1}')
  [ -z "$ldap" ] && {
    echo ldap container not running. Investigate and fix.
    exit 1
  }

  export IDPHOST=$(oc get svc idp|awk '/^idp/ {print $2}')
  [ -z "$IDPHOST" ] && {
    echo cannot determine IP for idp openshift service. Investigate.
    exit 1
  }

  export IFSDB=$(oc get svc ifs-database |awk '/^ifs-database/ {print $2}')
  [ -z "$IFSDB" ] && {
    echo cannot determine IP for ifs-database openshift service. Investigate.
    exit 1
  }

  echo Waiting some time for the IFS app to initialise...
  sleep 60

  echo "Refreshing ldap with users in ifs db"
  docker exec -it $ldap /usr/local/bin/ldap-delete-all-users.sh
  setup-files/scripts/docker/k8s-ldap-sync-from-ifs-db.sh
  sudo sed -i "s/.*\sidp$/$IDPHOST idp/" /etc/hosts
}
