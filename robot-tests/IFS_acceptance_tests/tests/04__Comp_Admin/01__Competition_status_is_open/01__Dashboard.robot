*** Settings ***
Documentation     INFUND-7358 Inflight competition dashboards: Ready to open dashboard
...
...               INFUND-7562 Inflight competition dashboards: Open dashboard
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../CompAdmin_Commons.robot

*** Test Cases ***
Competition dashboard open competition
    [Documentation]    INFUND-7562
    [Tags]
    When The user clicks the button/link    link=${OPEN_COMPETITION_NAME}
    Then The user should see the text in the page    00000001: Connected digital additive manufacturing
    And The user should see the text in the page    Open
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Materials and manufacturing
    And The user should see the text in the page    Earth Observation
    And the user should see the element     link=View and update competition setup
    And the user should see the element     jQuery=a:contains("Invite assessors")
    And the user should see the element     jQuery=.button:contains("Applications")
    And the user should see that the element is disabled    jQuery=.button:contains("Manage applications")
    #And the user should see that the element is disabled    jQuery=.button:contains("View panel sheet")
    #And the user should see that the element is disabled    jQuery=.button:contains("Funding")
    #TODO IEnable the checks when NFUND-7934 is ready

Competition dashboard ready to open competition
    [Documentation]    INFUND-7358
    [Tags]
    [Setup]    the user navigates to the page    ${CA_UpcomingComp}
    When The user clicks the button/link    link=${READY_TO_OPEN_COMPETITION_NAME}
    Then The user should see the text in the page    00000006: Photonics for health
    And The user should see the text in the page    Ready to open
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Materials and manufacturing
    And The user should see the text in the page    Earth Observation
    And the user should see the element     link=View and update competition setup
    And the user should see the element     jQuery=a:contains("Invite assessors")
    And the user should see that the element is disabled    jQuery=.button:contains("Applications")
    And the user should see that the element is disabled    jQuery=.button:contains("Manage applications")
    And the user should see that the element is disabled    jQuery=.button:contains("Manage applications")
    #And the user should see that the element is disabled    jQuery=.button:contains("View panel sheet")
    #And the user should see that the element is disabled    jQuery=.button:contains("Funding")
    #TODO IEnable the checks when NFUND-7934 is ready
