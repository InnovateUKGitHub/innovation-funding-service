*** Settings ***
Documentation      INFUND-1423 Going back from the 'create your account' page gives an error
Suite Setup        The guest user opens the browser
Suite Teardown    User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***

The user presses the back button while on the create account page
    [Documentation]     INFUND-1423
    [Tags]  Create account   Back button
    Given the user is on the login page
    When the user goes to the create account page
    And the user presses the back button
    Then the user is redirected to the login page



The user creates an account and then presses the back button
    [Documentation]     INFUND-1423
    [Tags]  Create account  Back button     Pending
    # tagged as failing due to bug INFUND-1496. This test should not be fixed until that issue is fixed.
    Given the user goes to the create account page
    And the user enters valid details and creates an account
    When the user is redirected to the overview page
    And the user presses the back button
    Then the user is redirected to the dashboard



The user logs in and visits the create account page
    [Documentation]     INFUND-1423
    [Tags]  Create account  Back button
    Given the user is logged in
    When the user goes to the create account page
    Then the user is redirected to the dashboard




*** Keywords ***
the user is on the login page
    go to   ${LOGIN_URL}

the user goes to the create account page
    go to    ${ACCOUNT_CREATION_FORM_URL}
    Sleep   2s

the user presses the back button
    Go Back

the user is redirected to the login page
    Page Should Contain     Sign in


the user enters valid details and creates an account
    Input Text      id=firstName            Joe
    Input Text      id=lastName             Bloggs
    Input Text      id=phoneNumber          01234567890
    Input Text      id=email                joe@joebloggs.com
    Input Text      id=password             password123
    Input Text      id=retypedPassword      password123
    Select Checkbox         termsAndConditions
    Submit Form
    Sleep       3s

the user is logged in
    login as user   &{lead_applicant_credentials}

the user is redirected to the overview page
    Page Should Contain     Application Overview


the user is redirected to the dashboard
    Page Should Contain     Your Profile

