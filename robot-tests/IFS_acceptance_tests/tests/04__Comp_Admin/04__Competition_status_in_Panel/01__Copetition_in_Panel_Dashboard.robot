*** Settings ***
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
In Panel Dashboard
    Given The user clicks the button/link    link=${FUNDERS_PANEL_COMPETITION_NAME}
    Then The user should see the text in the page    00000005: Internet of Things
    And The user should see the text in the page    Panel
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Materials and manufacturing
    And The user should see the text in the page    Earth Observation
    And the user should not see the element    link=View and update competition setup
    And the user should see the element    jQuery=.button:contains("View panel sheet")
    And the user should see the element    jQuery=.button:contains("Funding decision")
    And the user should see the element    jQuery=.button:contains("Invite assessors")
    And the user should see the element    jQuery=.button:contains("Manage applications")
