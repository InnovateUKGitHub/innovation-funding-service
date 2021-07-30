# shortcut for list all
alias ll="ls -la"

# Use sensible defaults to deploy 'external' resources
alias sext="skaffold dev -f skaffold-EXT.yml --rpc-http-port=50054 --rpc-port=50053 --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true"

# Use sensible defaults to deploy dev and custom builds (use one at a time)
alias sdev="skaffold dev --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true"
alias scust="skaffold dev -f skaffold-CUSTOM.yml --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true"

alias sdevx="skaffold dev --watch-image=[] --cache-artifacts=false --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true"
alias scustx="skaffold dev -f skaffold-CUSTOM.yml --cache-artifacts=false --watch-image=[] --auto-build=false --auto-sync=false --auto-deploy=false --status-check=false --wait-for-deletions=true"


# View state/events for dev/custom in firefox
alias state="open -a Firefox http://localhost:50052/v1/state"
alias events="open -a Firefox http://localhost:50052/v1/events"
# Redeploy in a currently deployed skaffold dev/custom instance via the rest api
alias sredev="curl -X POST http://localhost:50052/v1/execute -d '{'build': true, 'sync': true, 'deploy': true}'"

# shortcuts for k8s gets
alias po="kubectl get po"
alias dep="kubectl get deployments"
alias svc="kubectl get svc"
alias configmap="kubectl get configmap"
alias secrets="kubectl get secrets"

alias logshib="kubectl logs -f $(kubectl get pod -l app=shib -o name)"
alias logldap="kubectl logs -f $(kubectl get pod -l app=ldap -o name)"
alias logidp="kubectl logs -f $(kubectl get pod -l app=idp -o name)"
alias logapp="kubectl logs -f $(kubectl get pod -l app=application-svc -o name)"
alias logds="kubectl logs -f $(kubectl get pod -l app=data-service -o name)"

# Use dep alias then the name is the first arg here e.g. 'log application-svc'
log() {
    pod=$(kubectl get pod -l app="$1" -o name)
    kubectl logs -f $pod
}

exec() {
  pod=$(kubectl get pod -l app="$1" -o name)
  kubectl exec --stdin --tty $pod -- /bin/bash
}

delete() {
  pod=$(kubectl get pod -l app="$1" -o name)
  kubectl delete po $pod
}

sync_ldap() {
  if [[ -z "${TEST_USER_PASSWORD}" ]]; then
    echo 'TEST_USER_PASSWORD env var is not set so using default of Passw0rd1357'
    pass=$(slappasswd -s "Passw0rd1357" | base64)
  else
    echo 'TEST_USER_PASSWORD is set as env var'
    pass=$TEST_USER_PASSWORD
  fi
  POD=$(kubectl get pod -l app=ldap -o name)
  kubectl exec "$POD" -- bash -c "export TEST_USER_PASSWORD=$pass && /usr/local/bin/ldap-sync-from-ifs-db.sh"
}