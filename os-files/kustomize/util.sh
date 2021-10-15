#
# Source this file (source [path/helpers.sh] then use -:
# util_help
#

function util_reinstall_minikube {
  minikube delete
  minikube start --cpus=$1 --memory=$2 minikube start --extra-config=kubelet.streaming-connection-idle-timeout=4h
  echo 'docker username' $(_util_prop "nexus_username")
  kubectl create secret docker-registry regcred --docker-server=docker-ifs.devops.innovateuk.org --docker-username="$(_util_prop "nexus_username")" --docker-password="$(_util_prop "nexus_password")"
}

function util_reinstall_minikube_purge {
  minikube delete --all --purge
  docker system prune --all --volumes --force
  minikube start --cpus=$1 --memory=$2 minikube start --extra-config=kubelet.streaming-connection-idle-timeout=4h
  echo 'docker username' $(_util_prop "nexus_username")
  kubectl create secret docker-registry regcred --docker-server=docker-ifs.devops.innovateuk.org --docker-username="$(_util_prop "nexus_username")" --docker-password="$(_util_prop 'nexus_password')"
}

function util_versions {
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
  echo 'kubectl:' $(kubectl version --short)
  echo 'kustomize:' $(kustomize version --short)
  echo 'skaffold:' $(skaffold version)
  echo $(minikube version)
  echo $(java -version 2>&1 | head -n 1)
  echo 'gradle' $(gradle -version | sed -n '3p')
  echo $(sysctl hw.memsize)
  echo $(sysctl hw.ncpu)
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
  echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
}

function util_help {
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
    echo 'Having sourced this file all commands prefixed util_ should tab auto-complete'
    echo ''
    echo 'Util re-install scripts (requires nexus creds in ~/.gradle/gradle.properties -:'
    echo '    util_reinstall_minikube 10 12g - delete and start minikube'
    echo '    util_reinstall_minikube_purge 10 12g - as above but purge of docker and minikube (slow)'
    echo ''
    echo 'Debug'
    echo '    util_versions - debug the various installed versions'
    echo '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
}

function _util_prop {
    grep "${1}" ~/.gradle/gradle.properties|cut -d'=' -f2
}
