

Deploy overlays - go to os-files/kustomize folder and run below command

    kubectl kustomize overlays/<overlays_name e.g local> | kubectl apply -f - 

Get all pods details

    kubectl get pods

Get logs from pod
    
    kubectl logs -f  <pod_name>

Delete specific service

    kubectl delete deployment <service_name e.g. data-service>

Delete all config maps and deployments

    kubectl delete all --all

Run one of the following commands to view the Deployment object
    
    kubectl get -k ./
    kubectl describe -k ./