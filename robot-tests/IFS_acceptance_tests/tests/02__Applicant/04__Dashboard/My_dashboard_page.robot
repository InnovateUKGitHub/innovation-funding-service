*** Settings ***
Documentation     INFUND-37 As an applicant and I am on the application overview, I can view the status of this application, so I know what actions I need to take
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot

*** Variables ***

*** Test Cases ***
Milestone date for application in progress is visible
    [Documentation]    INFUND-37
    ...
    ...    INFUND-5485
    [Tags]
    when The user navigates to the page    ${DASHBOARD_URL}
    Then the user should see the date for submission of application

Number of days remaining until submission should be correct
    [Documentation]    INFUND-37
    ...
    ...    INFUND-5485
    [Tags]
    Then the user should see the number of days remaining
    And the days remaining should be correct (Applicant's dashboard)    2066-09-09

*** Keywords ***
the user should see the date for submission of application
    the user should see the element    css=.in-progress li:nth-child(1) div:nth-child(2) .competition-deadline .day
    the user should see the element    css=.in-progress li:nth-child(1) div:nth-child(2) .competition-deadline .month

the user should see the number of days remaining
    the user should see the element    css=.in-progress li:nth-child(1) div:nth-child(2) .pie-container .pie-overlay
