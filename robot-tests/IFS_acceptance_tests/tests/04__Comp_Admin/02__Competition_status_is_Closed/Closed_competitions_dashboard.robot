*** Settings ***
Documentation     INFUND-6604 As a member of the competitions team I can view the Invite assessors dashboard so that I can find and invite assessors to the competition
...
...               INFUND-6599 As a member of the competitions team I can navigate to the dashboard of a closed competition so that I can see information and further actions for the competition
...
...               INFUND-6458 As a member of the competitions team I can select 'Notify Assessors' in a closed assessment so that the competition is moved to state 'In assessment'
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
The user should be able to navigate to the Closed dashboard
    [Documentation]    INFUND-6599
    When The user clicks the button/link    link=${CLOSED_COMPETITION_NAME}
    Then the user should see the element    link=Applications
    and the user should see the element    jQuery=.button:contains("Notify assessors")
    and the user should see the element    link=Invite assessors

The user can Invite Assessors
    [Documentation]    INFUND-6604
    [Tags]    Pending
    #TODO Pending Due to Infund 6985
    When the user clicks the button/Link    link=Invite assessors
    Then The user should see the element    link=Overview
    And the user should see the element    link=Find
    And the user should see the element    link=Invite
    [Teardown]    The user clicks the button/link    link=All Competitions

The user can Notify Assessors
    [Documentation]    INFUND-6458
    [Tags]    Pending
    When The user clicks the button/link    jQuery=.button:contains("Notify assessors")
    Then the user should be redirected to the correct page    ${COMP_ADMINISTRATOR_DASHBOARD}
    [Teardown]    Suite Teardown    Run Keywords    Connect to Database    @{database}
    ...    AND    execute sql string    UPDATE `ifs`.`milestone` SET `DATE`=NULL WHERE type='ASSESSORS_NOTIFIED' AND competition_id=12;
