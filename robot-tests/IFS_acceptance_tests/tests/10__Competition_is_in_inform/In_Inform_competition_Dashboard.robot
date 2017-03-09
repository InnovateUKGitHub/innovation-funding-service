*** Settings ***
Documentation     INFUND-7365 Inflight competition dashboards: Inform dashboard
...
...               INFUND-7561 Inflight competition dashboards- View milestones
...
...               INFUND-8050 Release feedback and send notification email
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Competition Dashboard
    [Documentation]    INFUND-7365
    When The user clicks the button/link    link=${INFORM_COMPETITION_NAME}
    Then The user should see the text in the page    7: Integrated delivery programme - low carbon vehicles
    And The user should see the text in the page    Inform
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Materials and manufacturing
    And The user should see the text in the page    Satellite Applications
    And The user should see the element    jQuery=a:contains("Invite assessors to assess the competition")
    And the user should not see the element    link=View and update competition setup

Milestones for the In inform competition
    [Documentation]    INFUND-7561
    [Tags]
    Then the user should see the element    jQuery=.button:contains("Manage funding notifications")
    And The user should see the element    jQuery=button:contains("Release feedback")
    And the user should see the element    css=li:nth-child(13).done    #Verify that 12. Notifications
    And the user should see the element    css=li:nth-child(14).not-done    #Verify that 13. Release feedback is not done

Release feedback
    [Documentation]    INFUND-8050
    When The user clicks the button/link    jQuery=button:contains("Release feedback")
    Then The user should not see the text in the page    Inform
    When The user clicks the button/link    jQuery=a:contains(Live)
    Then The user should not see the text in the page    ${INFORM_COMPETITION_NAME}
