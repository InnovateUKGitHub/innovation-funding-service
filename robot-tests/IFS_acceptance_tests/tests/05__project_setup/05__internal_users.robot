*** Settings ***
Documentation     INFUND-4821: As a project finance team member I want to have a summary overview of project details for this competition so I can refer to this in a consistent way throughout the finance checks section
Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***

*** Test Cases ***
Project Finance can see Summary Overview
    [Documentation]    INFUND-4821
    [Tags]
    [Setup]    Log in as user    project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page    ${server}/project-setup-management/project/1/spend-profile/summary
    Then the user should see the element    jQuery=h2:contains("Finance Checks")
    And the user should see the text in the page    Overview
    And the table row has expected values
    [Teardown]    Logout as user

*** Keywords ***
the table row has expected values
    #TODO update selectors and values after INFUND-5476 & INFUND-5431
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[2]    36 months
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[3]    £ 356202.36026
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[4]    £ 71240.472052
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[5]    0
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[6]    20.0
