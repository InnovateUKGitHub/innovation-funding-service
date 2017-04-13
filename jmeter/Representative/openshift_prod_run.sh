echo "This will run the performance tests against OpenShift Production.  In order to run this, you should ensure that a fresh set of webtest data is available on Prod (although these tests themselves are rerunnable on a fresh set)."
echo ""
echo "You should also ensure that the Prod environment is free to use for this purpose.  Please check the #environment-updates channel to see if it reserved at this time."
echo ""
read -r -p "Are you happy to run these tests at this time? [y/N]" response

if [[ $response =~ ^([yY][eE][sS]|[yY])$ ]]
then
    jmeter -Jinvite_user=1 -Japplicant_users=10 -Jregistration_users=1 -popenshift-prod.properties -tload_test_representative.jmx
else
    echo "No problem.  See you next time!"
fi
