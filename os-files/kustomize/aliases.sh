#
# Source this file (source [path/aliases.sh] then use -:
# skaffold_help
# k8s_help
#

# Deployments
# Use sensible defaults to deploy 'external' resources
alias skaffold_e="skaffold dev -f skaffold-EXT.yml --rpc-http-port=50054 --rpc-port=50053 --auto-build=false --auto-sync=false --auto-deploy=false --status-check=true --wait-for-deletions=true --tail=false"
# Use sensible defaults to deploy dev and custom builds in a faster mode (use one at a time)
alias skaffold_dx="skaffold dev --watch-image='[]' --cache-artifacts=false --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true --tail=false"
# View state/events for dev/custom in firefox
alias skaffold_state="open -a Firefox http://localhost:50052/v1/state"
alias skaffold_events="open -a Firefox http://localhost:50052/v1/events"
alias dev_home="open -a Firefox https://host.docker.internal:8443/"

# shortcuts for k8s gets
alias k8s_po="kubectl get po"
alias k8s_dep="kubectl get deployments"
alias k8s_svc="kubectl get svc"
alias k8s_configmap="kubectl get configmap"
alias k8s_secrets="kubectl get secrets"

skaffold_dev() {
  skaffold dev -f skaffold-ADHOC.yml -p $1 --watch-image='[]' --cache-artifacts=false --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true --tail=true
}

skaffold_debug() {
  skaffold debug -f skaffold-ADHOC.yml -p $1 --watch-image='[]' --cache-artifacts=false --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true --tail=true
}

# Use k8s_dep alias then the name is the first arg here e.g. 'k8s_log application-svc'
k8s_log() {
    pod=$(kubectl get pod -l app="$1" -o name)
    kubectl logs -f $pod
}

k8s_wp() {
    watch kubectl get po
}

k8s_logs() {
    k8s_log "$1"
}

k8s_describe() {
    pod=$(kubectl get pod -l app="$1" -o name)
    kubectl describe $pod
}

# Use k8s_dep alias then the name is the first arg here e.g. 'k8s_exec application-svc'
k8s_exec() {
  pod=$(kubectl get pod -l app="$1" -o name)
  kubectl exec --stdin --tty $pod -- /bin/bash
}

# Use k8s_dep alias then the name is the first arg here e.g. 'k8s_delete application-svc'
k8s_delete() {
  pod=$(kubectl get pod -l app="$1" -o name)
  kubectl delete $pod
}

k8s_describe() {
  pod=$(kubectl get pod -l app="$1" -o name)
  kubectl describe $pod
}

k8s_sync_ldap_all_users() {
  if [[ -z "${TEST_USER_PASSWORD}" ]]; then
    echo 'IFS_TEST_USER_PASSWORD env var is not set so using default of Passw0rd1357'
    pass=$(slappasswd -s "Passw0rd1357" | base64)
  else
    echo 'IFS_TEST_USER_PASSWORD is set as env var'
    pass=$TEST_USER_PASSWORD
  fi
  POD=$(kubectl get pod -l app=ldap -o name)
  kubectl exec "$POD" -- bash -c "export IFS_TEST_USER_PASSWORD=$pass && /usr/local/bin/ldap-sync-from-ifs-db.sh"
}

k8s_sync_ldap_one_user() {
  if [[ -z "${TEST_USER_PASSWORD}" ]]; then
    echo 'IFS_TEST_USER_PASSWORD env var is not set so using default of Passw0rd1357'
    pass=$(slappasswd -s "Passw0rd1357" | base64)
  else
    echo 'IFS_TEST_USER_PASSWORD is set as env var'
    pass=$TEST_USER_PASSWORD
  fi
  POD=$(kubectl get pod -l app=ldap -o name)
  kubectl exec "$POD" -- bash -c "export IFS_TEST_USER_PASSWORD=$pass && /usr/local/bin/ldap-sync-one-user.sh $1"
}

k8s_wait() {
  while [[ $(kubectl get pods -l app=$1 -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]];
    do echo "waiting for pod $1" && sleep 5;
  done;
}

k8s_rebuild_db() {
  k8s_delete ldap
  k8s_wait ldap
  k8s_delete ifs-database
  k8s_wait ifs-database
  k8s_delete data-service
  k8s_wait data-service
  k8s_sync_ldap_all_users
}

k8s_clean_svc() {
  kubectl delete deployment application-svc
  kubectl delete deployment assessment-svc
  kubectl delete deployment competition-mgt-svc
  kubectl delete deployment data-service
  kubectl delete deployment data-service-alerts
  kubectl delete deployment finance-data-service
  kubectl delete deployment front-door-svc
  kubectl delete deployment project-setup-mgt-svc
  kubectl delete deployment project-setup-svc
  kubectl delete deployment survey-data-svc
  kubectl delete deployment survey-svc
}

k8s_clean_all() {
  kubectl delete deployment --all
  kubectl delete svc application-svc
  kubectl delete svc assessment-svc
  kubectl delete svc cache-provider
  kubectl delete svc competition-mgt-svc
  kubectl delete svc data-service
  kubectl delete svc data-service-alerts
  kubectl delete svc finance-data-service
  kubectl delete svc front-door-svc
  kubectl delete svc idp
  kubectl delete svc ifs-database
  kubectl delete svc ldap
  kubectl delete svc registration-svc
  kubectl delete svc project-setup-mgt-svc
  kubectl delete svc project-setup-svc
  kubectl delete svc shib
  kubectl delete svc sil-stub
  kubectl delete svc survey-data-svc
  kubectl delete svc survey-svc
  kubectl delete configmap cache-config
  kubectl delete configmap cache-provider-config
  kubectl delete configmap data-service-config
  kubectl delete configmap feature-toggle-config
  kubectl delete configmap flyway-config
  kubectl delete configmap idp-config
  kubectl delete configmap ldap-config
  kubectl delete configmap  mysql-initdb-config
  kubectl delete configmap  new-relic-config
  kubectl delete configmap  performance-config
  kubectl delete configmap  shibboleth-config
  kubectl delete configmap  spring-config
  kubectl delete configmap  web-config
  kubectl delete secrets db-secrets
  kubectl delete secrets data-service-secrets
  kubectl delete secrets docusign-secrets
  kubectl delete secrets ldap-secrets
  kubectl delete secrets new-relic-secrets
  kubectl delete secrets shibboleth-secrets
  kubectl delete secrets survey-data-service-secrets
  kubectl delete secrets finance-data-service-secrets
  kubectl delete secrets cache-secrets
  kubectl delete secrets flyway-secrets
  kubectl delete secrets web-secrets
  kubectl delete secrets idp-keys-secrets
  kubectl delete secrets ldap-keys-secrets
  kubectl delete secrets sp-secrets
  kubectl delete secrets sp-keys-secrets
}

skaffold_help () {
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
    echo 'Having sourced this file all commands prefixed skaffold_ should tab auto-complete'
    echo 'Skaffold_e, _dx, _ports is the full dev env but there are many ways to run this'
    echo ''
    echo '    skaffold_e - auth, cache, mail, sil and ifs-database '
    echo '    skaffold_dx - runs data and web tier '
    echo '    skaffold_dev [file] runs fast dev mode on specified skaffold file'
    echo '    skaffold_debug [file] runs fast debug mode on specified skaffold file'
    echo ''
    echo '    It is quite easy to create ad-hoc configurations for any dev/ops purpose'
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
}

k8s_help() {
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
    echo 'Having sourced this file all commands prefixed k8s_ should tab auto-complete'
    echo ''
    echo 'Developer utility scripts -:'
    echo '    k8s_clean_all - clean the k8s namespace'
    echo '    k8s_clean_svc - clean non ext services'
    echo '    k8s_rebuild_db - rebuilds the database and ldap entries'
    echo '    k8s_sync_ldap_all_users - syncs all db users with ldap'
    echo '    k8s_sync_ldap_one_user - syncs given db user with ldap'
    echo ''
    echo 'Shortcuts (save typing) -:'
    echo '    k8s_po - get the list of pods'
    echo '    k8s_dep - get the list of deployments'
    echo '    k8s_svc - get the list of services'
    echo '    k8s_configmap - get the list of configmaps'
    echo '    k8s_secrets - get the list of secrets'
    echo ''
    echo 'Helpers (where there is one pod per deployment) and the name matches k8s_dep output -:'
    echo '    k8s_log [name] - tails the log of the pod matching the deployment'
    echo '    k8s_exec [name] - opens into the pods terminal matching the deployment'
    echo '    k8s_delete [name] - deletes the pod matching the deployment'
    echo '    k8s_describe [name] - deletes the pod matching the deployment'
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
}