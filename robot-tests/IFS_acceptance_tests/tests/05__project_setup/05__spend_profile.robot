*** Settings ***
Documentation     INFUND-3970 As a partner I want a spend profile page in Project setup so that I can access and share Spend profile information within my partner organisation before submitting to the Project Manager
Suite Setup       Log in as user    steve.smith@empire.com    Passw0rd
Suite Teardown    the user closes the browser
Force Tags
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot



*** Variables ***


*** Test Cases ***

Lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link     link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK


Lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     January 2017
    And the user should see the text in the page     3 Months
    [Teardown]    Logout as user


Non-lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]
    [Setup]    Log in as user    jessica.doe@ludlow.co.uk     Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link     link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK


Non-lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     January 2017
    And the user should see the text in the page     3 Months
    [Teardown]    Logout as user
