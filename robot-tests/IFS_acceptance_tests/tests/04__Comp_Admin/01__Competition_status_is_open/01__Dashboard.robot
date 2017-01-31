*** Settings ***

Documentation     INFUND-7358     Inflight competition dashboards: Ready to open dashboard
...
...               INFUND-7562     Inflight competition dashboards: Open dashboard
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
    And the user should see that the element is disabled    jQuery=.button:contains("Manage applications")

Invite Assessors open competition
    [Documentation]    INFUND-7562
    [Tags]
    When the user clicks the button/Link    link=Invite assessors
    Then The user should see the element    link=Overview
    And the user should see the element    link=Find
    And the user should see the element    link=Invite
    [Teardown]    The user clicks the button/link    link=Competition

View and update open competition setup
    [Documentation]    INFUND-7562
    [Tags]
    When the user clicks the button/link    link=View and update competition setup
    Then the user should be redirected to the correct page    ${COMP_MANAGEMENT_UPDATE_COMP}
    [Teardown]    the user navigates to the page    ${CA_UpcomingComp}

Competition dashboard ready to open competition
    [Documentation]    INFUND-7358
    [Tags]
    When The user clicks the button/link    link=${READY_TO_OPEN_COMPETITION_NAME}
    Then The user should see the text in the page    00000006: Photonics for health
    And The user should see the text in the page    Ready to open
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Materials and manufacturing
    And The user should see the text in the page    Earth Observation
    And the user should see that the element is disabled    jQuery=.button:contains("Manage applications")
    And the user should see that the element is disabled    jQuery=.button:contains("Applications")

Invite Assessors ready to open competition
    [Documentation]    INFUND-7358
    [Tags]
    When the user clicks the button/Link    link=Invite assessors
    Then The user should see the element    link=Overview
    And the user should see the element    link=Find
    And the user should see the element    link=Invite
    [Teardown]    The user clicks the button/link    link=Competition


View and update ready to open competition setup
    [Documentation]    INFUND-7358
    [Tags]
    When the user clicks the button/link    link=View and update competition setup
    Then the user should be redirected to the correct page    ${COMP_MANAGEMENT_READY_TO_OPEN}

