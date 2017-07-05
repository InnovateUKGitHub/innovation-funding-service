oc cluster down
oc cluster up
oc login -u system:admin
oc policy add-role-to-user admin developer -n default
oc login -u=developer -p=developer
