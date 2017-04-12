*** Settings ***
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
In Panel Dashboard
    Given The user clicks the button/link    link=${FUNDERS_PANEL_COMPETITION_NAME}
    Then The user should see the text in the page    5: Internet of Things
    And The user should see the text in the page    Panel
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Materials and manufacturing
    And The user should see the text in the page    Satellite applications
    And the user should not see the element    link=View and update competition setup
    And the user should see the element    jQuery=a:contains("Input and review funding decision")
    And the user should see the element    jQuery=a:contains("Invite assessors to assess the competition")
    And the user should see the element    jQuery=a:contains("Assessor management: Assignments")

Milestones for In Panel Competitions
    Then the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("Manage funding notifications")
    And the user should see the element    css=li:nth-child(8).done    #Verify that 8. Line draw is done
    And the user should see the element    css=li:nth-child(9).not-done    #Verify that 9. Assessment panel is not done
