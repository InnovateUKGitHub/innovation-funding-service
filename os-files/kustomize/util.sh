#
# Source this file (source [path/helpers.sh] then use -:
# util_help
#
function util_stuck_pods {
  echo 'Run this but be aware this may leave pods consuming resources hidden inside minikube...'
  echo 'kubectl delete --all pods --grace-period=0 --force'
  echo 'After deletion you need to check for zombie pods in docker so open a terminal -:'
  echo 'docker ps'
  echo 'Then kill anything that should not be running'
}

function util_versions {
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
  echo 'docker client:' "$(docker version --format {{.Client.Version}})"
  echo 'docker server:' "$(docker version --format {{.Server.Version}})"
  echo 'kubectl:' "$(kubectl version --short)"
  echo 'kustomize:' "$(kustomize version --short)"
  echo 'skaffold:' "$(skaffold version)"
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
    echo 'Debug'
    echo '    util_ver - pulls docker credentials from gradle.properties and adds to docker k8s as a secret'
    echo '    util_debug_env - check hosts file'
    echo '    util_versions - debug the various installed versions'
    echo '    util_stuck_pods - run this for instructions on clearing stuck pods'
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
}

function util_kube_secret {
  echo 'docker username' "$(_util_prop "nexus_username")"
  kubectl create secret docker-registry regcred --docker-server=docker-ifs.devops.innovateuk.org --docker-username="$(_util_prop "nexus_username")" --docker-password="$(_util_prop "nexus_password")"
}

function _util_prop {
    grep "${1}" ~/.gradle/gradle.properties|cut -d'=' -f2
}

function util_debug_env {
  _util_check_host auth.local-dev
  _util_check_host ifs.local-dev
  _util_check_host host.docker.internal
  _util_check_host kubernetes.docker.internal
  _util_check_host ifs-database
}

function _util_check_host {
  if grep -Fq "$1" /etc/hosts > /dev/null
    then
      _util_coloredEcho "Found host $1 /etc/hosts" green
    else
      _util_coloredEcho "$1 not in /etc/hosts" red
  fi
}

function _util_coloredEcho() {
    local exp=$1;
    local color=$2;
    if ! [[ ${color} =~ '^[0-9]$' ]] ; then
       case $(echo ${color} | tr '[:upper:]' '[:lower:]') in
        black) color=0 ;;
        red) color=1 ;;
        green) color=2 ;;
        yellow) color=3 ;;
        blue) color=4 ;;
        magenta) color=5 ;;
        cyan) color=6 ;;
        white|*) color=7 ;; # white or invalid color
       esac
    fi
    tput setaf ${color};
    echo ${exp};
    tput sgr0;
}