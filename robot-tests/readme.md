# General Readme about these tests:

General docs: 
<http://robotframework.org/>

##Installation
Before you are able to run these tests, first install the Robot Framework (the ride editor is optional)
<https://sites.google.com/a/worth.systems/worth-projects/home/tsb/nomensa-alpha/setup-dev-environment/setup-ride-on-osx>


## Running the tests
### Running it in RIDE  
inside the ride editor, you are able to run tests. You can also do it without RIDE.

###Run it with Pybot
    
    pybot --outputdir target IFS_acceptance_tests/3.Application_form.robot

####Run it with an other browser than Firefox

For chrome you also need the chromedriver.
    
    --variable BROWSER:GoogleChrome
    
####Run it on some other server.

    --variable SERVER:http://localhost:8080 
    
    
### Running it on saucelabs: 

<http://datakurre.pandala.org/2014/03/cross-browser-selenium-testing-with.html>

Full command: 

    pybot --outputdir target  -v REMOTE_URL:http://<saucelabs_user>:<saucelabs_access_key>@ondemand.saucelabs.com:80/wd/hub -v SERVER_AUTH:<basic_auth_user>:<basic_auth_password> -v SERVER_BASE:ifs.test.worth.systems -v DESIRED_CAPABILITIES:"platform:Windows 10,browserName:chrome,version:45.0" IFS_acceptance_tests/3.Application_form.robot

	pybot --outputdir target  

Set the connection details for saucelabs

	-v REMOTE_URL:http://<saucelabs_user>:<saucelabs_access_key>@ondemand.saucelabs.com:80/wd/hub 

Set the basic auth if needed

	-v SERVER_AUTH:<basic_auth_user>:<basic_auth_password>

Set the URL the test should use for the test

	-v SERVER_BASE:ifs.test.worth.systems 

Set the capabilities of the browser that is needed. <https://docs.saucelabs.com/reference/platforms-configurator/#/>

	-v DESIRED_CAPABILITIES:"platform:Windows 10,browserName:chrome,version:45.0" 







