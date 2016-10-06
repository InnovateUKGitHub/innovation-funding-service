*** Settings ***
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***

*** Test Cases ***
Milestone date for application in progress is visible
    [Documentation]    INFUND-5485
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    Then the user should see the date for submission of application

Number of days remaining until application submission
    [Documentation]    INFUND-5485
    [Tags]
    Then the user should see the number of days remaining
    And the days remaining should be correct


*** Keywords ***
the user should see the date for submission of application
    the user should see the element    css=.in-progress li:nth-child(1) div:nth-child(2) .competition-deadline .day
    the user should see the element    css=.in-progress li:nth-child(1) div:nth-child(2) .competition-deadline .month

the user should see the number of days remaining
    the user should see the element    css=.in-progress li:nth-child(1) div:nth-child(2) .pie-container .pie-overlay

the days remaining should be correct
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    2066-09-09    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.in-progress li:nth-child(1) div:nth-child(2) .pie-container .pie-overlay .day
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}