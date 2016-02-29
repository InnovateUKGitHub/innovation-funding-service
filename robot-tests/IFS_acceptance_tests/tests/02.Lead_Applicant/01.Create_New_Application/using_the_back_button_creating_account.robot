*** Settings ***
Documentation     INFUND-1423 Going back from the 'create your account' page gives an error
Suite Setup       The guest user opens the browser
Suite Teardown
Test Teardown     User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
The user presses the back button while on the create account page
    [Documentation]    INFUND-1423
    [Tags]    Create account    Back button
    Given user navigates to the page    ${LOGIN_URL}
    When user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    And the user presses the back button
    Then user should be redirected to the correct page    ${LOGIN_URL}

The user logs in and visits the create account page
    [Documentation]    INFUND-1423
    [Tags]    Create account    Back button
    Given Guest user log-in    steve.smith@empire.com    test
    When user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    Then user should see the text in the page    Your Profile

*** Keywords ***
the user presses the back button
    Go Back
