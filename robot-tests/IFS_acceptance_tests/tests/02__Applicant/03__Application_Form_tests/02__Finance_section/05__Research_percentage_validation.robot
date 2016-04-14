*** Settings ***
Documentation     INFUND-1436 As a lead applicant I want to be able to view the ratio of research participation costs in my consortium so I know my application is within the required range
Suite Teardown    User closes the browser
Force Tags        Finances    Validation
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Variables ***

*** Test Cases ***
If research participation is too high, an error is shown on the form and also the overview page
    [Documentation]    INFUND-1436
    [Tags]    HappyPath    Research percentage    Validation
    Given the applicant logs in
    When the research participation on the second application is too high
    Then there is an error message on the finances form
    And there is an error message on the summary page
    And the academic collaborator can change their level of participation back
    And the user can log out

If research participation is below the maximum level, no error is shown
    [Documentation]    INFUND-1436
    [Tags]    HappyPath
    Given the first collaborator logs in
    When the first collaborator edits financial details to bring down the research participation level
    And the applicant logs in
    Then there is no error message on the finances form
    And there is no error message on the summary page
    And the user can log out

*** Keywords ***
The applicant logs in
    Guest user log-in    &{lead_applicant_credentials}

The research participation on the second application is too high
    Guest user log-in    &{collaborator2_credentials}
    the user navigates to the page      ${your_finances_url_application_2}
    Input Text    id=incurred-staff    1000000000

the academic collaborator can change their level of participation back
    Guest user log-in    &{collaborator2_credentials}
    the user navigates to the page      ${your_finances_url_application_2}
    Input Text    id=incurred-staff    1000

There is an error message on the finances form
    the applicant logs in
    the user navigates to the page   ${FINANCES_OVERVIEW_URL_APPLICATION_2}
    Page Should Contain    The participation levels of this project are not within the required range

There is an error message on the summary page
    Go To    ${APPLICATION_2_SUMMARY_URL}
    Click Button    css=[aria-controls="collapsible-14"]
    Page Should Contain    The participation levels of this project are not within the required range


The first collaborator logs in
    Guest user log-in    &{collaborator1_credentials}


The first collaborator edits financial details to bring down the research participation level
    the user navigates to the page       ${your_finances_url_application_2}
    Click Element    css=[aria-controls="collapsible-0"]
    Wait Until Element Is Visible    name=add_cost
    Click Element    name=add_cost
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(1) input    test
    Sleep    1s
    focus    css=.app-submit-btn

There is no error message on the finances form
    Go To    ${finances_overview_url_application_2}
    Page Should Not Contain    The participation levels of this project are not within the required range

There is no error message on the summary page
    Go To    ${APPLICATION_2_SUMMARY_URL}
    Wait Until Element Is Visible    css=[aria-controls="collapsible-14"]
    Click Button    css=[aria-controls="collapsible-14"]
    Sleep    3s
    Page Should Not Contain    The participation levels of this project are not within the required range


