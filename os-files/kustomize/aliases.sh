# Deployments
# Use sensible defaults to deploy 'external' resources
alias skaffold_e="skaffold dev -f skaffold-EXT.yml --rpc-http-port=50054 --rpc-port=50053 --auto-build=false --auto-sync=false --auto-deploy=false --status-check=true --wait-for-deletions=true --tail=false"
# Use sensible defaults to deploy dev and custom builds (use one at a time)
alias skaffold_d="skaffold dev --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true --tail=false"
alias skaffold_c="skaffold dev -f skaffold-CUSTOM.yml --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true --tail=false"
# Use sensible defaults to deploy dev and custom builds in a faster mode (use one at a time)
alias skaffold_dx="skaffold dev --watch-image='[]' --cache-artifacts=false --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true --tail=false"
alias skaffold_cx="skaffold dev -f skaffold-CUSTOM.yml --cache-artifacts=false --watch-image='[]' --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true --tail=false"
# View state/events for dev/custom in firefox
alias skaffold_state="open -a Firefox http://localhost:50052/v1/state"
alias skaffold_events="open -a Firefox http://localhost:50052/v1/events"
# Redeploy in a currently deployed skaffold dev/custom instance via the rest api
alias skaffold_refresh="curl -X POST http://localhost:50052/v1/execute -d '{'build': true, 'sync': true, 'deploy': true}'"
# port forwarding to access application and db
alias skaffold_ports="skaffold dev -f skaffold-PORTS.yml"

# shortcuts for k8s gets
alias k8s_po="kubectl get po"
alias k8s_dep="kubectl get deployments"
alias k8s_svc="kubectl get svc"
alias k8s_configmap="kubectl get configmap"
alias k8s_secrets="kubectl get secrets"

# Use k8s_dep alias then the name is the first arg here e.g. 'k8s_log application-svc'
k8s_log() {
    pod=$(kubectl get pod -l app="$1" -o name)
    kubectl logs -f $pod
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

k8s_sync_ldap() {
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

k8s_wait() {
  while [[ $(kubectl get pods -l app=$1 -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]];
    do echo "waiting for pod $1" && sleep 5;
  done;
}

k8s_port_shib() {
  k8s_wait shib
  kubectl port-forward svc/shib 8443:443
}

k8s_port_idp() {
  k8s_wait idp
  kubectl port-forward svc/idp 9443:443
}

k8s_port_db() {
  k8s_wait ifs-database
  kubectl port-forward svc/ifs-database 3306:3306
}

k8s_rebuild_db() {
  k8s_delete ldap
  k8s_wait ldap
  k8s_delete ifs-database
  k8s_wait ifs-database
  k8s_delete data-service
  k8s_wait data-service
  k8s_sync_ldap
}

k8s_clean() {
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
  kubectl delete configmap data-service-config
  kubectl delete configmap db-config
  kubectl delete configmap docusign-config
  kubectl delete configmap feature-toggle-config
  kubectl delete configmap finance-data-service-config
  kubectl delete configmap flyway-config
  kubectl delete configmap idp-config
  kubectl delete configmap ldap-config
  kubectl delete configmap  mysql-initdb-config
  kubectl delete configmap  new-relic-config
  kubectl delete configmap  performance-config
  kubectl delete configmap  shibboleth-config
  kubectl delete configmap  spring-profile-env
  kubectl delete configmap  survey-data-service-config
  kubectl delete configmap  web-config
  kubectl delete secrets idp-keys-secrets
  kubectl delete secrets ldap-keys-secrets
  kubectl delete secrets sp-secrets
  kubectl delete secrets sp-keys-secrets
}