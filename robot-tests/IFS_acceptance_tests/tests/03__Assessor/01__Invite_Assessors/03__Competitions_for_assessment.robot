*** Settings ***
Documentation     INFUND-3720: As an Assessor I can see deadlines for the assessment of applications currently in assessment on my dashboard, so that I am reminded to deliver my work on time.
...
...               INFUND-3716: As an Assessor when I have accepted to assess within a competition and the assessment period is current, I can see the number of competitions and their titles on my dashboard, so that I can plan my work.
Suite Setup
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***
${DAYS_LEFT}      ${EMPTY}

*** Test Cases ***
Milestone date for assessment submission
    [Documentation]    INFUND-4857
    [Setup]    log in as user    &{existing_assessor1_credentials}  #worth.email.test+assessor1@gmail.com
    [Tags]
    When the user navigates to the page    ${Assessor_competition_dashboard}
    Then the assessor should see the date for submission of assessment

Verify number of days remaining until the deadline
    [Documentation]    INFUND-4857
    [Tags]
    Then the assessor should see the number of days remaining
    And the days remaining should be correct

*** Keywords ***
the assessor should see the date for submission of assessment
    the user should see the element    css=.my-applications div:nth-child(2) .competition-deadline .day
    the user should see the element    css=.my-applications div:nth-child(2) .competition-deadline .month

the assessor should see the number of days remaining
    the user should see the element    css=.my-applications div:nth-child(2) .pie-container .pie-overlay .day

the days remaining should be correct
    ${DAYS_LEFT}=    Get Text    css=.my-applications div:nth-child(2) .pie-container .pie-overlay .day
    Should Be True    ${DAYS_LEFT}>=0
