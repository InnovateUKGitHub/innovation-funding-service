echo "This will run the performance tests against OpenShift Local.  In order to run this, you should ensure that a fresh set of webtest data is available on OpenShift Local (although these tests themselves are rerunnable on a fresh set)."
echo ""
read -r -p "Are you happy to run these tests at this time? [y/N]" response

if [[ $response =~ ^([yY][eE][sS]|[yY])$ ]]
then
    jmeter -n -l /tmp/openshift-local-run.log -Jinvite_user=0 -Japplicant_users=600 -Jregistration_users=0 -popenshift-local.properties -tload_test_representative.jmx
else
    echo "No problem.  See you next time!"
fi
