*** Settings ***
Documentation     INFUND-6604 As a member of the competitions team I can view the Invite assessors dashboard so...
...
...               INFUND-6599 As a member of the competitions team I can navigate to the dashboard of a closed competition so...
...
...               INFUND-6458 As a member of the competitions team I can select 'Notify Assessors' in a closed assessment so...
...
...               INFUND-7362 Inflight competition dashboards: Closed dashboard
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Competition dashboard
    [Documentation]    INFUND-6599
    ...
    ...    INFUND-7362
    When The user clicks the button/link    link=${CLOSED_COMPETITION_NAME}
    Then The user should see the text in the page    00000012: Machine learning for transport infrastructure
    And The user should see the text in the page    Closed
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Infrastructure systems
    And The user should see the text in the page    Transport Systems
    And the user should not see the element     link=View and update competition setup
    #And the user should see that the element is disabled    jQuery=.button:contains("View panel sheet")
    #And the user should see that the element is disabled    jQuery=.button:contains("Funding")
    #TODO IEnable the checks when NFUND-7934 is ready

Invite Assessors
    [Documentation]    INFUND-6604
    ...
    ...    INFUND-7362
    [Tags]
    When the user clicks the button/Link    link=Invite assessors
    Then The user should see the element    link=Overview
    And the user should see the element    link=Find
    And the user should see the element    link=Invite
    [Teardown]    The user clicks the button/link    link=Competition

Manage Applications
    [Documentation]    INFUND-7042
    ...
    ...    INFUND-7362
    When the user clicks the button/Link    jQuery=.button:contains("Manage applications")
    Then The user should see the text in the page    Assign assessors to applications.
    [Teardown]    The user clicks the button/link    link=Manage assessments

Notify Assessors
    [Documentation]    INFUND-6458
    ...
    ...    INFUND-7362
    [Tags]
    When The user clicks the button/link    jQuery=.button:contains("Notify assessors")
    Then the user should see the text in the page    In assessment
    [Teardown]    Run Keywords    Connect to Database    @{database}
    ...    AND    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE type='ASSESSORS_NOTIFIED' AND competition_id=12;
