kustomize build base > /dev/null || echo 'xxx error on base'

kustomize build components/auth > /dev/null || echo 'xxx error on base'
kustomize build components/cache-provider > /dev/null || echo 'xxx error on base cache'
kustomize build components/data-layer > /dev/null || echo 'xxx error on base data'
kustomize build components/db-reset > /dev/null || echo 'xxx error on base web'
kustomize build components/ifs-database > /dev/null || echo 'xxx error on base web'
kustomize build components/mail > /dev/null || echo 'xxx error on base mail'
kustomize build components/robot > /dev/null || echo 'xxx error on base robot'
kustomize build components/sil-stub > /dev/null || echo 'xxx error on sil'
kustomize build components/web-layer > /dev/null || echo 'xxx error on base web'

kustomize build patches/health-check-patch > /dev/null || echo 'xxx error on health-check-patch'
kustomize build patches/new-relic-env-vars-patch > /dev/null || echo 'xxx error on new-relic-env-vars-patc'
kustomize build patches/pull-secrets-patch > /dev/null || echo 'xxx error on pull-secrets-patch'
kustomize build patches/spring-profile-patch > /dev/null || echo 'xxx error on spring-profile-patch'
kustomize build patches/wait-for-db-patch > /dev/null || echo 'xxx error on wait-for-db-patch'

kustomize build env/env-base > /dev/null || echo 'xxx error on env base'
kustomize build env/aws/aws-base > /dev/null || echo 'xxx error on aws base'
kustomize build env/aws/demo > /dev/null || echo 'xxx error on aws demo'
kustomize build env/aws/perf > /dev/null || echo 'xxx error on aws perf'
kustomize build env/aws/prod > /dev/null || echo 'xxx error on aws prod'
kustomize build env/aws/sysint > /dev/null || echo 'xxx error on aws sysint'
kustomize build env/aws/uat > /dev/null || echo 'xxx error on aws uat'

kustomize build env/stub/stub-base > /dev/null || echo 'xxx error on stub base'
kustomize build env/stub/at > /dev/null || echo 'xxx error on stub at'
kustomize build env/stub/local/dev > /dev/null || echo 'xxx error on local dev'
kustomize build env/stub/local/ext > /dev/null || echo 'xxx error on local ext'
#/dev/null || echo 'xxx error on local ext'


