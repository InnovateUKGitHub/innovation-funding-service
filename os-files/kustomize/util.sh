#
# Source this file (source [path/helpers.sh] then use -:
# util_help
#
function util_stuck_pods {
  echo 'Run this but be aware this may leave pods consuming resources hidden inside minikube...'
  echo 'kubectl delete --all pods --grace-period=0 --force'
  echo 'After deletion you need to check for zombie pods in minikube so open a terminal inside minikube (minikube ssh)'
  echo 'docker ps'
  echo 'Then kill anything that should not be running'
}

function util_start_minikube {
  if [ $# -ne 2 ]
      then
        echo "Specify cpus and ram e.g. 10 20g Aborting..."
        return
    fi
    _util_mini_install "$1" "$2"
}

function util_reinstall_minikube {
  if [ $# -ne 2 ]
    then
      echo "Specify cpus and ram e.g. 10 20g Aborting..."
      return
  fi
  minikube delete
  _util_mini_install "$1" "$2"
}

function util_reinstall_minikube_purge {
  if [ $# -ne 2 ]
    then
      echo "Specify cpus and ram e.g. 10 20g Aborting..."
      return
  fi
  minikube delete --all --purge
  docker system prune --all --volumes --force
  _util_mini_install "$1" "$2"
}

function util_versions {
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
  echo 'kubectl:' "$(kubectl version --short)"
  echo 'kustomize:' "$(kustomize version --short)"
  echo 'skaffold:' "$(skaffold version)"
  minikube version
  java -version 2>&1 | head -n 1
  echo 'gradle' "$(gradle -version | sed -n '3p')"
  sysctl hw.memsize
  sysctl hw.ncpu
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
}

function util_help {
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
    echo 'Having sourced this file all commands prefixed util_ should tab auto-complete'
    echo ''
    echo 'Util re-install scripts (requires nexus credentials in ~/.gradle/gradle.properties -:'
    echo '    util_reinstall_minikube 10 12g - delete and start minikube'
    echo '    util_reinstall_minikube_purge 10 12g - as above but purge of docker and minikube (slow)'
    echo ''
    echo 'Debug'
    echo '    util_versions - debug the various installed versions'
    echo '    util_stuck_pods - run this for instructions on clearing stuck pods'
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
}

function _util_mini_install {
  minikube start --cpus="$1" --memory="$2" --driver=hyperkit --apiserver-port=6443  --extra-config=kubelet.streaming-connection-idle-timeout=4h --extra-config=apiserver.service-node-port-range=1-32767
  echo 'docker username' "$(_util_prop "nexus_username")"
  kubectl create secret docker-registry regcred --docker-server=docker-ifs.devops.innovateuk.org --docker-username="$(_util_prop "nexus_username")" --docker-password="$(_util_prop "nexus_password")"
  _util_update_hosts_minikube_ip auth.local-dev
  _util_update_hosts_minikube_ip ifs.local-dev
  kubectl cluster-info
}

function _util_prop {
    grep "${1}" ~/.gradle/gradle.properties|cut -d'=' -f2
}

function _util_update_hosts_minikube_ip {
  MINIKUBE_IP=$(minikube ip)
  echo "Minikube Ip: $MINIKUBE_IP"
  LOCAL_HOSTNAME=$1
  HOSTS_ENTRY="$MINIKUBE_IP $LOCAL_HOSTNAME"
  if grep -Fq "$LOCAL_HOSTNAME" /etc/hosts > /dev/null
    then
      sudo sed -i '' "/$LOCAL_HOSTNAME/d" /etc/hosts
      echo "Removed existing host $LOCAL_HOSTNAME"
  fi
  echo "$HOSTS_ENTRY" | sudo tee -a /etc/hosts
  echo "Added hosts $LOCAL_HOSTNAME to point to $MINIKUBE_IP"
}