*** Settings ***
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***
${DAYS_LEFT}      ${EMPTY}

*** Test Cases ***
The Days left to submit are visible in dashboard page
    [Documentation]    INFUND-37 As an applicant and I am on the application overview, I can view the status of this application, so I know what actions I need to take
    [Tags]    Applicant    HappyPath
    # Pending due to INFUND-4909
    Given the user navigates to the page    ${DASHBOARD_URL}
    Then the user should see the days left to submit
    And the days left to submit should be correct

*** Keywords ***
the user should see the days left to submit
    the user should see the element   css=#content > div > section.in-progress > ul > li:nth-child(1) > div > div:nth-child(2) > div.pie-container > div.pie-overlay

the days left to submit should be correct
    ${DAYS_LEFT}=    Get Text    css=.pie-overlay .day
    Should Be True    ${DAYS_LEFT}>=0
