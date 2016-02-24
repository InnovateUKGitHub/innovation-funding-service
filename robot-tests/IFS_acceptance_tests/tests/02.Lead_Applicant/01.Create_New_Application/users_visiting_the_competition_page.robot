*** Settings ***
Documentation     INFUND-921 : As an applicant I want to be able to select a link from the competition web page to visit a competition further description page containing relevant links so that I can apply into the competition.
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
The user is logged in
    [Documentation]    INFUND-921
    [Tags]    Applicant    Details page    HappyPath
    Given user navigates to the page    ${COMPETITION_DETAILS_URL}
    When user clicks the button/link    jQuery=.column-third .button:contains('Create application')
    Then user should be redirected to the correct page    ${CHECK_ELIGIBILITY}

The user is not logged in and later enters correct login
    [Documentation]    INFUND-921
    [Tags]    Applicant    Details page
    Given user navigates to the page    ${LOG_OUT}
    When user navigates to the page    ${COMPETITION_DETAILS_URL}
    Then user should see the element    jQuery=.column-third .button:contains('Sign in')
    And user clicks the button/link    jQuery=.column-third .button:contains('Sign in')
    And the guest user enters the login credentials    steve.smith@empire.com    test
    And user clicks the button/link    css=input.button
    Then user should be redirected to the correct page    ${YOUR_DETAILS}

The user is not logged in and later enters incorrect login
    [Documentation]    INFUND-921
    [Tags]    Applicant    Details page
    Given user navigates to the page    ${LOG_OUT}
    When user navigates to the page    ${COMPETITION_DETAILS_URL}
    And user should see the element    jQuery=.column-third .button:contains('Sign in')
    And user clicks the button/link    jQuery=.column-third .button:contains('Sign in')
    And the guest user enters the login credentials    steve.smith@empire.com    testpsw123
    And user clicks the button/link    css=input.button
    Then user should see an error    Your username/password combination doesn't seem to work

*** Keywords ***
