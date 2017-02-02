*** Settings ***
Documentation     INFUND-7358 Inflight competition dashboards: Ready to open dashboard
...
...               INFUND-7562 Inflight competition dashboards: Open dashboard
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot
Resource          ../CompAdmin_Commons.robot

*** Test Cases ***
Competition dashboard open competition
    [Documentation]    INFUND-7562
    [Tags]
    Given the user navigates to the page  ${CA_Live}
    When The user clicks the button/link  link=${OPEN_COMPETITION_NAME}
    Then the user should see the element  jQuery=span:contains("00000001: Connected digital additive manufacturing")
    And the user should see the element   jQuery=h1:contains("Open")
    And the user should see the element   jQuery=dt:contains("Competition type") ~ dd:contains("Programme")
    And the user should see the element   jQuery=dt:contains("Innovation sector") ~ dd:contains("Materials and manufacturing")
    And the user should see the element   jQuery=dt:contains("Innovation area") ~ dd:contains("Earth Observation")
    And the user should see the element   jQuery=a:contains("Manage applications")[aria-disabled="true"]

Competition dashboard ready to open competition
    [Documentation]    INFUND-7358
    [Tags]
    Given the user navigates to the page    ${CA_UpcomingComp}
    When The user clicks the button/link    link=${READY_TO_OPEN_COMPETITION_NAME}
    Then the user should see the element    jQuery=span:contains("00000006: Photonics for health")
    And the user should see the element     jQuery=h1:contains(" Ready to open")
    And the user should see the element     jQuery=h1:contains(" Ready to open")
    And the user should see the element     jQuery=dt:contains("Competition type") ~ dd:contains("Programme")
    And the user should see the element     jQuery=dt:contains("Innovation sector") ~ dd:contains("Materials and manufacturing")
    And the user should see the element     jQuery=dt:contains("Innovation area") ~ dd:contains("Earth Observation")
    And the user should see the element     jQuery=a:contains("Manage applications")[aria-disabled="true"]
    And the user should see the element     jQuery=a:contains("Applications")[aria-disabled="true"]