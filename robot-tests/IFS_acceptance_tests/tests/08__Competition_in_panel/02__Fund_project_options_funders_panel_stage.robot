*** Settings ***
Documentation     INFUND-2601 As a competition administrator I want a view of all applications at the 'Funders Panel' stage
...
...               INFUND-8065 Filter on 'Funding decision' dashboard
Suite Setup       guest user log-in  &{internal_finance_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Variables ***
${funders_panel_competition_url}    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/funding

*** Test Cases ***
Funding decision buttons should be disabled
    [Documentation]    INFUND-2601
    [Tags]
    When the user navigates to the page    ${funders_panel_competition_url}
    Then the user should see the text in the page    Mark application as
    And the user should see the options to make funding decisions disabled

An application is selected and the buttons become enabled
    [Documentation]    INFUND-7377
    [Tags]
    When the user selects the checkbox    app-row-2
    Then the user should see the options to make funding decisions enabled
    When the user unselects the checkbox    app-row-2
    Then the user should see the options to make funding decisions disabled

User should be able to make a funding decision
    [Documentation]  INFUND-7377
    [Tags]  HappyPath
    Given the user navigates to the page  ${funders_panel_competition_url}
    When the user sets the funding decision of application    app-row-1    On hold
    Then the user should see the element  jQuery=tr:first-of-type():contains("On hold")

User should be able to change a funding decision after one has been chosen
    [Documentation]    INFUND-7377
    [Tags]
    When the user sets the funding decision of application    app-row-1    Unsuccessful
    Then the user should see the element    jQuery=tr:first-of-type():contains("Unsuccessful")

Filter on application number
    [Documentation]    INFUND-8065
    [Tags]
    Given the user enters text to a text field     id=stringFilter    ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    And the user selects the option from the drop-down menu    Unsuccessful    id=fundingFilter
    When the user clicks the button/link    jQuery=button:contains("Filter")
    Then the user should see the element    jQuery=td:nth-child(3):contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    And the user should not see the element    jQuery=td:nth-child(3):contains("${FUNDERS_PANEL_APPLICATION_2_TITLE}")
    And the user clicks the button/link    jQuery=.button:contains("Clear all filters")
    And the user should see the element    jQuery=td:nth-child(3):contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")

*** Keywords ***
the user should see the options to make funding decisions disabled
    the user should see the element  jQuery=button:contains("Successful")[disabled]
    the user should see the element  jQuery=button:contains("Unsuccessful")[disabled]
    the user should see the element  jQuery=button:contains("On hold")[disabled]

the user should see the options to make funding decisions enabled
    the user should not see the element  jQuery=button:contains("Successful")[disabled]
    the user should not see the element  jQuery=button:contains("Unsuccessful")[disabled]
    the user should not see the element  jQuery=button:contains("On hold")[disabled]

the user sets the funding decision of application
    [Arguments]    ${checkbox}    ${decision_button}
    the user selects the checkbox    ${checkbox}
    the user clicks the button/link    jQuery=button:contains("${decision_button}")