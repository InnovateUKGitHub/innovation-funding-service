*** Settings ***
Documentation     INFUND-2601 As a competition administrator I want a view of all applications at the 'Funders Panel' stage
Suite Setup       Log in as user    email=lee.bowman@innovateuk.test    password=Passw0rd
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Variables ***
${funders_panel_competition_url}    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/funding

*** Test Cases ***
Funding decision buttons should be disabled
    [Documentation]    INFUND-7376
    [Tags]    HappyPath
    When the user navigates to the page    ${funders_panel_competition_url}
    Then the user should see the text in the page    Mark application as
    And the options to mark funding decisions should be disabled

An application is selected and the buttons become enabled
    [Documentation]    INFUND-7376
    [Tags]    HappyPath
    When the user selects the checkbox    app-row-1
    Then the options to mark funding decisions should be enabled

An application is unselected and the buttons become disabled
    [Documentation]    INFUND-7376
    [Tags]
    When the user unselects the checkbox    app-row-1
    Then the options to mark funding decisions should be disabled

User should be able to mark funding decisions when an application has been chosen
    [Documentation]    INFUND-7376
    [Tags]    HappyPath
    When the user marks the funding decision of application    app-row-1    Successful
    Then the user should see the element    jQuery=td:contains("Successful")
    And checkbox should not be selected    app-row-1
    And the options to mark funding decisions should be disabled

User should be able to mark and application as unsuccessful
    [Documentation]    INFUND-7376
    [Tags]    HappyPath
    When the user marks the funding decision of application    app-row-2    Unsuccessful
    Then the user should see the element    jQuery=td:contains("Unsuccessful")

User should be able to change a funding decision after one has been chosen
    [Documentation]    INFUND-7376
    [Tags]
    When the user marks the funding decision of application    app-row-2    On hold
    Then the user should see the element    jQuery=td:contains("On hold")
    When the user marks the funding decision of application    app-row-2    Unsuccessful
    Then the user should see the element    jQuery=td:contains("Unsuccessful")

*** Keywords ***
the options to mark funding decisions should be disabled
    the option to mark funding decision is disabled for button    Successful
    the option to mark funding decision is disabled for button    Unsuccessful
    the option to mark funding decision is disabled for button    On hold

the options to mark funding decisions should be enabled
    the option to mark funding decision is enabled for button    Successful
    the option to mark funding decision is enabled for button    Unsuccessful
    the option to mark funding decision is enabled for button    On hold

the option to mark funding decision is disabled for button
    [Arguments]    ${decision_button}
    the element should be disabled    jQuery=.button:contains("${decision_button}")

the option to mark funding decision is enabled for button
    [Arguments]    ${decision_button}
    The user should see the element    jQuery=.button:contains("${decision_button}")
    And Element Should Be Enabled      jQuery=.button:contains("${decision_button}")

the user marks the funding decision of application
    [Arguments]    ${checkbox}    ${decision_button}
    When the user moves focus to the element    jQuery=label[for="${checkbox}"]
    And the user selects the checkbox    ${checkbox}
    And the user clicks the button/link    jQuery=button:contains("${decision_button}")
