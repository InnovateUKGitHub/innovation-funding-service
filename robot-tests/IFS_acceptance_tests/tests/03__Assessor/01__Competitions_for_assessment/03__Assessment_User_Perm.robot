*** Settings ***
Documentation     INFUND-4569:  Assessor permissions checks
Suite Setup       guest user log-in    paul.plum@gmail.com    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot


*** Test Cases ***
User can't see assessment from different assessors
    [Documentation]    INFUND-4569
    [Tags]
    #user paul should not able access felix's  application in assessment
    [Setup]    guest user log-in    paul.plum@gmail.com    Passw0rd
    When the user navigates to the assessor page    ${Assessment_overview_11}
    Then The user should see expected text on the page    You do not have the necessary permissions for your request

Felix can't access paul's application in assessment
    [Documentation]    INFUND-4569
    [Tags]
    [Setup]    guest user log-in    felix.wilson@gmail.com    Passw0rd
    When the user navigates to the assessor page    ${Assessment_overview_9}
    Then The user should see expected text on the page    You do not have the necessary permissions for your request

User can see assessment which accepted by them
    [Documentation]    INFUND-4569
    [Tags]
    #Paul should be able to access his own application in assessment
    [Setup]    guest user log-in    paul.plum@gmail.com    Passw0rd
    When The user navigates to the assessor page    ${Assessment_overview_9}
    Then the user should not see the text in the page    You do not have the necessary permissions for your request

Permission error when acess other user application question
    [Documentation]     INFUND-4569
    [Tags]
    #felix user should not be able to access paul  user application questions
    [Setup]    guest user log-in    felix.wilson@gmail.com    Passw0rd
    When the user navigates to the assessor page   ${Application_question_url}
    Then The user should see expected text on the page    You do not have the necessary permissions for your request






*** Keywords ***
Provided precondition
    Setup system under test