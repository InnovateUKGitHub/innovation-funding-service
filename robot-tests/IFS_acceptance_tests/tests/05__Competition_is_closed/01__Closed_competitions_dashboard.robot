*** Settings ***
Documentation     INFUND-6604 As a member of the competitions team I can view the Invite assessors dashboard so...
...
...               INFUND-6599 As a member of the competitions team I can navigate to the dashboard of a closed competition so...
...
...               INFUND-6458 As a member of the competitions team I can select 'Notify Assessors' in a closed assessment so...
...
...               INFUND-7362 Inflight competition dashboards: Closed dashboard
...
...               INFUND-7561 Inflight competition dashboards- View milestones
...
...               INFUND-7560 Inflight competition dashboards- Viewing key statistics for 'Ready to Open', 'Open', 'Closed' and 'In assessment' competition states
Suite Setup       Custom suite setup
Suite Teardown    Custom Suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot

*** Test Cases ***
Competition dashboard
    [Documentation]    INFUND-6599  INFUND-7362  INFUND-7561
    Given The user clicks the button/link              link = ${CLOSED_COMPETITION_NAME}
    Then the user should see the competition details   ${CLOSED_COMPETITION_NAME}   Closed  Infrastructure systems  Smart infrastructure  Input and review funding decision  Invite assessors to assess the competition
    And the user should see the element                link = View and update competition setup
    And the user should see the milestones for the closed competitions

Key Statistics for Closed competitions
    [Documentation]    INFUND-7560
    Given get the expected values from the invite page
    Then the counts of the key statistics of the closed competition should be correct

Invite Assessors
    [Documentation]    INFUND-6604  INFUND-7362
    [Tags]
    Given the user clicks the button/Link   link = Invite assessors to assess the competition
    Then The user should see the element    link = Pending and declined
    And the user should see the element     link = Find
    And the user should see the element     link = Invite
    [Teardown]    The user clicks the button/link    link = Competition

Notify Assessors
    [Documentation]  INFUND-6458 INFUND-7362
    [Tags]
    Given The user clicks the button/link            jQuery = .govuk-button:contains("Notify assessors")
    Then the user should see the element             jQuery = h1:contains("In assessment")
    [Teardown]  Reset competition's milestone

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Connect to database  @{database}

get the expected values from the invite page
    The user clicks the button/link    jQuery=a:contains(Invite assessors)
    ${Invited}=    Get text    css = div:nth-child(1) > div > span
    Set Test Variable    ${Invited}
    ${Accepted}=    Get text    css = div:nth-child(2) > div > span
    Set Test Variable    ${Accepted}
    The user clicks the button/link    link = Competition
    The user clicks the button/link    link = Manage assessments
    The user clicks the button/link    jQuery = a:contains("Allocate applications")
    Get the total number of submitted applications
    The user clicks the button/link    link = Manage assessments
    The user clicks the button/link    link = Competition

the counts of the key statistics of the closed competition should be correct
    ${INVITED_COUNT} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Assessors invited") .govuk-heading-l
    Should Be Equal As Integers    ${INVITED_COUNT}    ${Invited}
    ${ACCEPTED_COUNT} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Invitations accepted") .govuk-heading-l
    Should Be Equal As Integers    ${ACCEPTED_COUNT}    ${Accepted}
    ${APPLICATIONS_PER_ASSESSOR} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Applications per assessor") .govuk-heading-l
    Should Be Equal As Integers    ${APPLICATIONS_PER_ASSESSOR}    3
    ${APPLICATIONS_REQ} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Applications requiring additional assessors") .govuk-heading-l
    Should Be Equal As Integers    ${NUMBER_OF_APPLICATIONS}    ${APPLICATIONS_REQ}
    ${Assessor_without_app} =    Get text     jQuery = .govuk-grid-column-one-third:contains("Assessors without applications") .govuk-heading-l
    Should Be Equal As Integers    ${Assessor_without_app}   10

Reset competition's milestone
    Execute sql string  UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE `competition_id`='${competition_ids['${CLOSED_COMPETITION_NAME}']}' and `type`='ASSESSORS_NOTIFIED';

Custom suite teardown
    Disconnect from database
    The user closes the browser

the user should see the milestones for the closed competitions
    the user should see the element    jQuery = button:contains("Notify assessors")
    the user should see the element    jQuery = li:contains("Assessor briefing").done
    the user should see the element    jQuery = li:contains("Assessor accepts").not-done
