*** Settings ***
Documentation     INFUND-550: As an assessor I want the ‘Assessment summary’ page to show me complete and incomplete sections, so that I can easily judge how much of the application is left to do
Suite Setup
Suite Teardown    TestTeardown User closes the browser
Force Tags        Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot    # TODO
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Completed overall scores present at the top of the page
    [Documentation]    INFUND-550
    ...
    ...    INFUND-3416
    [Tags]    HappyPath
    Given the user navigates to the summary page of the Robot test application
    Then the completed overall scores shown at the top
    And the total and percentage is shown under the scores

All questions present with collapsible sections
    [Documentation]    INFUND-550
    ...
    ...    INFUND-3416
    [Tags]
    Then all the questions should be visible

*** Keywords ***
