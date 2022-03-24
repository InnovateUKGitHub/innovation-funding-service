
kustomize build env/aws/demo | kubeval --strict --openshift
kustomize build env/aws/perf | kubeval --strict --openshift
kustomize build env/aws/prod | kubeval --strict --openshift
kustomize build env/aws/sysint | kubeval --strict --openshift
kustomize build env/aws/uat | kubeval --strict --openshift
kustomize build env/stub/at | kubeval --strict --openshift
kustomize build env/stub/custom | kubeval --strict --openshift

kustomize build env/stub/local/ext  | kubeval --strict
kustomize build env/stub/local/dev | kubeval --strict
kustomize build env/stub/local/custom | kubeval --strict
kustomize build env/stub/local/debug | kubeval --strict
