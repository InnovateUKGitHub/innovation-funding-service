*** Settings ***
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    Assessor
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

Milestones for In Panel Competitions
    Then the user should see that the element is disabled    jQuery=.button:contains("Manage funding notifications")
    And the user should see the element    css=li:nth-child(8).done    #Verify that 8. Line draw is done
    And the user should see the element    css=li:nth-child(9).not-done    #Verify that 9. Assessment panel is not done
