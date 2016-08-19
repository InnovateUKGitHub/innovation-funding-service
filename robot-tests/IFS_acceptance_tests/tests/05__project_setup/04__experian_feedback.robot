*** Settings ***
Documentation     INFUND-3763 As a project finance team member I want to receive feedback from Experian regarding a partners' bank account details
Suite Setup       Log in as user    project.finance1@innovateuk.test    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Experian    Pending
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***

*** Test Cases ***

The user can see the company name with score
    [Documentation]    INFUND-3763
    [Tags]
    When the user navigates to the page    ${server}/project-setup-management/project/1/organisation/31/review-bank-details    # note that this user does not have a dashboard yet, so we need to browse to this page directly for now
    Then the user should see the text in the page    Vitruvius Stonework Limited
    And the user should see the element

The user can see the company number with status
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the element
    And the user should see the element

The user can see the account number with status
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the element
    And the user should see the element

The user can see the address with score
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the element
    And the user should see the element

The user has the options to edit the details and to approve the bank details
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the element
    And the user should see the element
    [Teardown]    logout as user

Other internal users cannot access this page
    [Documentation]    INFUND-3763
    [Tags]
    [Setup]    guest user log-in  john.doe@innovateuk.test    Passw0rd
    the user navigates to the page and gets a custom error message  You do not have the permissions for your request
    [Teardown]    logout as user


Project partners cannot access this page
    [Documentation]    INFUND-3763
    [Tags]
    [Setup]    guest user log-in    steve.smith@empire.com    Passw0rd
    the user navigates to the page and gets a custom error message    You do not have the permissions for your your request
    [Teardown]    logout as user
