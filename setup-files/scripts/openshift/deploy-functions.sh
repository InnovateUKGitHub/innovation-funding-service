function convertFileToBlock() {
   cat $1 | tail -n +2 | sed '$ d' | tr -d '\r' | tr '\n' ' ' |  rev | cut -c 2- | rev
}

function injectDBVariables() {
    if [ -z "$DB_USER" ]; then echo "Set DB_USER environment variable"; exit -1; fi
    if [ -z "$DB_PASS" ]; then echo "Set DB_PASS environment variable"; exit -1; fi
    if [ -z "$DB_NAME" ]; then echo "Set DB_NAME environment variable"; exit -1; fi
    if [ -z "$DB_HOST" ]; then echo "Set DB_HOST environment variable"; exit -1; fi
    DB_PORT=${DB_PORT:-3306}
    sed -i.bak "s/<<DB-USER>>/$DB_USER/g" os-files-tmp/*.yml
    sed -i.bak "s/<<DB-PASS>>/$DB_PASS/g" os-files-tmp/*.yml
    sed -i.bak "s/<<DB-NAME>>/$DB_NAME/g" os-files-tmp/*.yml
    sed -i.bak "s/<<DB-HOST>>/$DB_HOST/g" os-files-tmp/*.yml
    sed -i.bak "s/<<DB-PORT>>/$DB_PORT/g" os-files-tmp/*.yml
}

function injectFlywayVariables() {
    [ -z "$FLYWAY_LOCATIONS" ] && { echo "Set FLYWAY_LOCATIONS environment variable"; exit -1; }
    sed -i.bak "s_<<FLYWAY-LOCATIONS>>_${FLYWAY_LOCATIONS}_g" os-files-tmp/*.yml
}

function injectLDAPVariables() {
    if [ -z "$LDAP_HOST" ]; then echo "Set LDAP_HOST environment variable"; exit -1; fi
    LDAP_PORT=${LDAP_PORT:-389}
    sed -i.bak "s/<<LDAP-HOST>>/$LDAP_HOST/g" os-files-tmp/*.yml
    sed -i.bak "s/<<LDAP-PORT>>/$LDAP_PORT/g" os-files-tmp/*.yml
    sed -i.bak "s/<<LDAP-PASS>>/$LDAP_PASS/g" os-files-tmp/*.yml
    sed -i.bak "s/<<LDAP-DOMAIN>>/$LDAP_DOMAIN/g" os-files-tmp/*.yml
}

function tailorAppInstance() {
    if [ -z "$SSLCERTFILE" ]; then echo "Set SSLCERTFILE, SSLCACERTFILE, and SSLKEYFILE environment variables"; exit -1; fi
    sed -i.bak $"s#<<SSLCERT>>#$(convertFileToBlock $SSLCERTFILE)#g" os-files-tmp/shib/*.yml
    sed -i.bak $"s#<<SSLCERT>>#$(convertFileToBlock $SSLCERTFILE)#g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak $"s#<<SSLCACERT>>#$(convertFileToBlock $SSLCACERTFILE)#g" os-files-tmp/shib/*.yml
    sed -i.bak $"s#<<SSLCACERT>>#$(convertFileToBlock $SSLCACERTFILE)#g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak $"s#<<SSLKEY>>#$(convertFileToBlock $SSLKEYFILE)#g" os-files-tmp/shib/*.yml
    sed -i.bak $"s#<<SSLKEY>>#$(convertFileToBlock $SSLKEYFILE)#g" os-files-tmp/shib/named-envs/*.yml

    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/named-envs/*.yml

    sed -i.bak "s/<<MAIL-ADDRESS>>/mail-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/mail/*.yml
    sed -i.bak "s/<<ADMIN-ADDRESS>>/admin-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/spring-admin/*.yml

    if [[ ${TARGET} == "production" || ${TARGET} == "demo" || ${TARGET} == "uat" || ${TARGET} == "sysint" ]]
    then
        sed -i.bak "s/claimName: file-upload-claim/claimName: ${TARGET}-file-upload-claim/g" os-files-tmp/*.yml

        if [[ ${TARGET} == "demo" ]]
        then
            if [ -z "${bamboo_demo_ldap_password}" ]; then echo "Set bamboo_${TARGET}_ldap_password environment variable"; exit -1; fi
            sed -i.bak "s/<<LDAP-PASSWORD>>/${bamboo_demo_ldap_password}/g" os-files-tmp/shib/named-envs/*.yml
        fi
        if [[ ${TARGET} == "sysint" ]]
        then
            if [ -z "${bamboo_sysint_ldap_password}" ]; then echo "Set bamboo_${TARGET}_ldap_password environment variable"; exit -1; fi
            sed -i.bak "s/<<LDAP-PASSWORD>>/${bamboo_sysint_ldap_password}/g" os-files-tmp/shib/named-envs/*.yml
        fi
        if [[ ${TARGET} == "uat" ]]
        then
            if [ -z "${bamboo_uat_ldap_password}" ]; then echo "Set bamboo_${TARGET}_ldap_password environment variable"; exit -1; fi
            sed -i.bak "s/<<LDAP-PASSWORD>>/${bamboo_uat_ldap_password}/g" os-files-tmp/shib/named-envs/*.yml
        fi
        if [[ ${TARGET} == "production" ]]
        then
            if [ -z "${bamboo_production_ldap_password}" ]; then echo "Set bamboo_${TARGET}_ldap_password environment variable"; exit -1; fi
            sed -i.bak "s/<<LDAP-PASSWORD>>/${bamboo_production_ldap_password}/g" os-files-tmp/shib/named-envs/*.yml
        fi
    fi

    if [[ ${TARGET} == "production" || ${TARGET} == "uat" ]]
    then
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/4*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/shib/*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/shib/named-envs/*.yml
    fi
}

function useContainerRegistry() {
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" os-files-tmp/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/innovateuk/#g" os-files-tmp/shib/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/innovateuk/#g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" os-files-tmp/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" os-files-tmp/shib/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" os-files-tmp/robot-tests/*.yml
}

function pushApplicationImages() {
    docker tag innovateuk/data-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/data-service:${VERSION}
    docker tag innovateuk/project-setup-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/project-setup-service:${VERSION}
    docker tag innovateuk/project-setup-management-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}
    docker tag innovateuk/competition-management-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/competition-management-service:${VERSION}
    docker tag innovateuk/assessment-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/assessment-service:${VERSION}
    docker tag innovateuk/application-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/application-service:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -e unused -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/data-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/project-setup-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/competition-management-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/assessment-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/application-service:${VERSION}
}

function pushDBResetImages() {
    docker tag innovateuk/dbreset:${VERSION} \
        ${REGISTRY}/${PROJECT}/dbreset:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -e unused -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/dbreset:${VERSION}
}

function cloneConfig() {
    cp -r os-files os-files-tmp
}

function cleanUp() {
    rm -rf os-files-tmp
    rm -rf shibboleth
}

function scaleDataService() {
    oc scale dc data-service --replicas=2 ${SVC_ACCOUNT_CLAUSE}
}
