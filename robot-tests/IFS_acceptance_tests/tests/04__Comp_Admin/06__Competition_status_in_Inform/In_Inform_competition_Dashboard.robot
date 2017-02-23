*** Settings ***
Documentation     INFUND-7365 Inflight competition dashboards: Inform dashboard
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Competition Dashboard
    [Documentation]    INFUND-7365
    When The user clicks the button/link    link=${INFORM_COMPETITION_NAME}
    Then The user should see the text in the page    00000007: Integrated delivery programme - low carbon vehicles
    And The user should see the text in the page    Inform
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Materials and manufacturing
    And The user should see the text in the page    Earth Observation
    And The user should see the element    jQuery=.button:contains("Invite assessors")
    And the user should not see the element    link=View and update competition setup

Milestones for the In inform competition
    Then the user should see that the element is disabled    jQuery=.button:contains("Manage funding notifications")
    And The user should see the element    jQuery=button:contains("Release feedback")
    And the user should see the element    css=li:nth-child(13).done    #Verify that 12. Notifications
    And the user should see the element    css=li:nth-child(14).not-done    #Verify that 13. Release feedback is not done
