echo "This will run the performance tests against OpenShift Local.  In order to run this, you should ensure that a fresh set of webtest data is available on OpenShift Local (although these tests themselves are rerunnable on a fresh set)."
echo ""
read -r -p "Are you happy to run these tests at this time? [y/N]" response

if [[ $response =~ ^([yY][eE][sS]|[yY])$ ]]
then
    rm /tmp/openshift-local-run.log
    jmeter -n -l /tmp/openshift-local-run.log -Jinvite_user=0 -Japplicant_users=600 -Jregistration_users=0 -popenshift-local.properties -tload_test_representative.jmx &
    
    last_call_count=0
   
    while true; do 
        new_call_count=$(cat /tmp/openshift-local-run.log | wc -l)
        echo "$((new_call_count - last_call_count)) new calls - total $new_call_count so far..."
        last_call_count=$new_call_count
        sleep 5
    done
    
else
    echo "No problem.  See you next time!"
fi
