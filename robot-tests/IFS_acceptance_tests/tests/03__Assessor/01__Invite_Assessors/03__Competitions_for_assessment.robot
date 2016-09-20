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
${NO_OF_DAYS_LEFT}
${CURRENT_DATE}
${MILESTONE_DATE}    2016-12-31
${STARTING_DATE}
${SCREEN_NO_OF_DAYS_LEFT}

*** Test Cases ***
Milestone date for assessment submission
    [Documentation]    INFUND-4857
    [Setup]    log in as user    &{existing_assessor1_credentials}  #worth.email.test+assessor1@gmail.com
    [Tags]
    When the user navigates to the page    ${Assessor_competition_dashboard}
    Then the assessor should see the date for submission of assessment

Number of days remaining until assessment submission
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
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    2016-12-31    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.my-applications div:nth-child(2) .pie-container .pie-overlay .day
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}