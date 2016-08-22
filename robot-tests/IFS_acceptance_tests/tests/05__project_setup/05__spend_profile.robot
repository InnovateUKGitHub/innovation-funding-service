*** Settings ***
Documentation     INFUND-3970 As a partner I want a spend profile page in Project setup so that I can access and share Spend profile information within my partner organisation before submitting to the Project Manager
Suite Setup       the project finance user generates the spend profile table
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
    [Setup]    Log in as user    steve.smith@empire.com    Passw0rd
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



*** Keywords ***

the project finance user generates the spend profile table
    log in as user    project.finance1@innovateuk.test    Passw0rd
    the user navigates to the page    ${server}/project-setup-management/project/1/spend-profile/summary    # For now we need to go to the url directly, as the project finance dashboard doesn't exist yet.
    the user clicks the button/link    jQuery=.button:contains("Generate Spend Profile")
    the user clicks the button/link    name=submit-app-details    # this second click is confirming the decision on the modal
    logout as user