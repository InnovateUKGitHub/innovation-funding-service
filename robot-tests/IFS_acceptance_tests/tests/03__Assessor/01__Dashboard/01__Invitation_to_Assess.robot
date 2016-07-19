*** Settings ***
Documentation     -INFUND-225 As an assessor and I am signed in, I have an overview of the competitions for which I assess the applications, so that I can see my workload.
...
...               -INFUND-246 As an assessor I want to see my assessment progress at competition level (how many assessments completed vs. total), so I can manage workload.
...
...               -INFUND-284- As an assessor I can log into the system to be redirected to my dashboard, so I can view my assessments
...
...               INFUND-337
Suite Setup       Guest user log-in    &{assessor_credentials}
Suite Teardown    TestTeardown User closes the browser
Test Setup
Test Teardown
Force Tags        Pending
Resource          ../../resources/GLOBAL_LIBRARIES.robot    # TODO Pending due to upcoming refactoring work for the assessor story
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot