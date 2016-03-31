*** Settings ***
Documentation     INFUND-921 : As an applicant I want to be able to select a link from the competition web page to visit a competition further description page containing relevant links so that I can apply into the competition.
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Non logged in users see the Apply now button
    [Documentation]    INFUND-921
    [Tags]    Applicant
    Given the user navigates to the page    ${LOG_OUT}
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    Then the user should see the element    jQuery=.column-third .button:contains('Apply now')

Logged in users should see a warning
    [Documentation]    INFUND-921
    [Tags]    Applicant
    Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    Then the user should see the element    css=.warning-alert
    And the user should see the text in the page    You are already logged in. You can only create one application per account. If you want to apply to another competition please logout and create a new account.
