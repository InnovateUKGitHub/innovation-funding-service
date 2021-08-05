POD=$(kubectl get pod -l app=ldap -o jsonpath="{.items[0].metadata.name}")
kubectl exec $POD  -- env IFS_TEST_USER_PASSWORD="enter encrypted password here" /usr/local/bin/ldap-sync-from-ifs-db.sh

# run below command in terminal to get encrypted password, replace password keyword with actual password which you intend to use (Must be as per IFS format)
# slappasswd -s "password" | base64