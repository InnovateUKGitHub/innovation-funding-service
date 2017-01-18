#!/bin/bash

  ldap=$(docker ps |awk '/k8s_ldap\./ {print $1}')
  [ -z "$ldap" ] && {
    echo ldap container not running. Investigate and fix.
    exit 1
  }
  docker exec -it $ldap /usr/local/bin/ldap-delete-all-users.sh

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

  echo "Refreshing ldap with users in ifs db"
  setup-files/scripts/docker/k8s-ldap-sync-from-ifs-db.sh
  sudo sed -i "s/.*\sidp$/$IDPHOST idp/" /etc/hosts
  cat /etc/hosts
