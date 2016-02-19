*** Settings ***
Documentation     INFUND-1683 As a user of IFS application, if I attempt to perform an action that I am not authorised perform, I am redirected to authorisation failure page with appropriate message
Test Teardown     Close Browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${APPLICATION_7_OVERVIEW_PAGE}    ${SERVER}/application/7
${APPLICATION_7_FORM}    ${SERVER}/application/7/form/question/9

*** Test Cases ***
Guest user can't access overview page
    [Documentation]    INFUND-1683
    Given the guest user opens the browser
    When user enters the url of the application 7 overview
    Then guest user should get a log-in page

Guest user can't be able to access application form
    [Documentation]    INFUND-1683
    Given the guest user opens the browser
    When user enters the url of the application 7 form
    Then guest user should get a log-in page

Applicant who is not team member can't access overview page
    [Documentation]    INFUND-1683
    Given the user is logged in as Pete Tom
    When user enters the url of the application 7 overview
    Then User should get an error page

Applicant who is not team member can't access application form page
    [Documentation]    INFUND-1683
    Given the user is logged in as Pete Tom
    When user enters the url of the application 7 form
    Then User should get an error page

Assessor can't access the overview page
    [Documentation]    INFUND-1683
    [Setup]    Login as user    &{assessor_credentials}
    When user enters the url of the application 7 overview
    Then User should get an error page

Assessor can't access the application form
    [Documentation]    INFUND-1683
    [Setup]    Login as user    &{assessor_credentials}
    When user enters the url of the application 7 form
    Then User should get an error page

*** Keywords ***
the user is logged in as Pete Tom
    Login as user    &{collaborator2_credentials}

user enters the url of the application 7 overview
    GO TO    ${APPLICATION_7_OVERVIEW_PAGE}

User should get an error page
    Page Should Contain    Oops, something went wrong

user enters the url of the application 7 form
    go to    ${APPLICATION_7_FORM}

Close Browser
    Close All Browsers

guest user should get a log-in page
    Page Should Contain    Sign in
