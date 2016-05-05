*** Settings ***
Documentation     INFUND-1436 As a lead applicant I want to be able to view the ratio of research participation costs in my consortium so I know my application is within the required range
Force Tags        Finances
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Variables ***

*** Test Cases ***
Alert should show If the research participation of the academic partner is too high
    [Documentation]    INFUND-1436
    [Tags]    HappyPath
    [Setup]    Guest user log-in    &{collaborator2_credentials}
    When the user navigates to the page    ${your_finances_url_application_2}
    And the user enters text to a text field    id=incurred-staff    1000000000
    And Guest user log-in    &{lead_applicant_credentials}
    And the user navigates to the page    ${FINANCES_OVERVIEW_URL_APPLICATION_2}
    Then the user should see the text in the page    The participation levels of this project are not within the required range
    And the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    And the user clicks the button/link    jquery=button:contains("Finances Summary")
    Then the user should see the text in the page    The participation levels of this project are not within the required range
    [Teardown]    Academics partner enters a valid resaerch participation value

Alert should not show If research participation is below the maximum level
    [Documentation]    INFUND-1436
    [Tags]    HappyPath
    [Setup]    Given Guest user log-in    &{collaborator1_credentials}
    When the first collaborator edits financial details to bring down the research participation level
    And Guest user log-in    &{lead_applicant_credentials}
    And the user navigates to the page    ${FINANCES_OVERVIEW_URL_APPLICATION_2}
    Then the user should see the text in the page    The participation levels of this project are within the required range
    And the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    And the user clicks the button/link    jquery=button:contains("Finances Summary")
    Then the user should see the text in the page    The participation levels of this project are within the required range
    [Teardown]    User closes the browser

*** Keywords ***
The first collaborator edits financial details to bring down the research participation level
    the user navigates to the page    ${your_finances_url_application_2}
    Click Element    jQuery=button:contains("Labour")
    Wait Until Element Is Visible    name=add_cost
    Click Element    jQuery=button:contains('Add another role')
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(1) input    test
    Sleep    1s
    focus    css=.app-submit-btn

Academics partner enters a valid resaerch participation value
    And Guest user log-in    &{collaborator2_credentials}
    And the user navigates to the page    ${your_finances_url_application_2}
    And the user enters text to a text field    id=incurred-staff    1000
    User closes the browser
