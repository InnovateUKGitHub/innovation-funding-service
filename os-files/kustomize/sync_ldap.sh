POD=$(kubectl get pod -l app=ldap -o jsonpath="{.items[0].metadata.name}")
kubectl exec $POD  /usr/local/bin/ldap-sync-from-ifs-db.sh
