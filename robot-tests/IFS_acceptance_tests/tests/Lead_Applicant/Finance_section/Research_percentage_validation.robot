*** Settings ***
Documentation       INFUND-1436 As a lead applicant I want to be able to view the ratio of research participation costs in my consortium so I know my application is within the required range
Suite Teardown      User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot


*** Variables ***



*** Test Cases ***

If research participation is too high, an error is shown on the form and also the overview page
    [Documentation]     INFUND-1436
    [Tags]              Research percentage     Validation      Finances
    Given the applicant logs in
    When the research participation on the second application is too high
    Then there is an error message on the finances form
    And there is an error message on the summary page
    And the applicant can log out


If research participation is below the maximum level, no error is shown
    [Documentation]     INFUND-1436
    [Tags]              Research percentage     Validation      Finances
    Given the first collaborator logs in
    When the first collaborator edits financial details to bring down the research participation level
    And the applicant logs in
    Then there is no error message on the finances form
    And there is no error message on the summary page
    And the first collaborator can log out



*** Keywords ***

The applicant logs in
    Login as user      &{lead_applicant_credentials}

The research participation on the second application is too high
    Go To              ${FINANCES_OVERVIEW_URL_APPLICATION_2}


There is an error message on the finances form
    Page Should Contain     The participation levels of this project are not within the required range

There is an error message on the summary page
    Go To           ${APPLICATION_2_SUMMARY_URL}
    Click Button    css=[aria-controls="collapsible-15"]
    Page Should Contain      The participation levels of this project are not within the required range

The applicant can log out
    Logout as user

The first collaborator logs in
    Login as user       &{collaborator1_credentials}

The first collaborator edits financial details to bring down the research participation level
    Go To               ${your_finances_url_application_2}
    Click Element       css=[aria-controls="collapsible-1"]
    Click Element        link=Add another role
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    1200000
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    test
    Logout as user

There is no error message on the finances form
    Go To       ${finances_overview_url_application_2}
    Page Should Not Contain       The participation levels of this project are not within the required range

There is no error message on the summary page
    Go To       ${APPLICATION_2_SUMMARY_URL}
    Wait Until Element Is Visible       css=[aria-controls="collapsible-15"]
    Click Button    css=[aria-controls="collapsible-15"]
    Sleep   1s
    Page Should Not Contain      The participation levels of this project are not within the required range

The first collaborator can log out
    Logout as user
