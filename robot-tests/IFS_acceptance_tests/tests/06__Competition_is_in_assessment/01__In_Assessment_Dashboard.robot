*** Settings ***
Documentation     INFUND-7363 Inflight competitions dashboards: In assessment dashboard
...
...               INFUND-7560 Inflight competition dashboards- Viewing key statistics for 'Ready to Open', 'Open', 'Closed' and 'In assessment' competition states
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot

*** Test Cases ***
In Assessment dashboard page
    [Documentation]    INFUND-7363
    Given The user clicks the button/link             link = ${IN_ASSESSMENT_COMPETITION_NAME}
    Then the user should see the competition details  ${IN_ASSESSMENT_COMPETITION_NAME}  In assessment  Materials and manufacturing  Digital manufacturing  Input and review funding decision  Invite assessors to assess the competition
    And the user should see the element               link = View and update competition setup

Milestones for In Assessment competitions
    [Documentation]    INFUND-7561
    Then the user should see the element   jQuery = button:contains("Close assessment")
    And the user should see the element    css = li:nth-child(9).not-done    #this keyword verifies that the 8. Line Draw is not done
    And the user should see the element    css = li:nth-child(5).done    #this keyword verifies that the 5.Assessor briefing is done

Key statistics of the In Assessment competitions
    [Documentation]    INFUND-7560
    Then The key statistics counts should be correct

*** Keywords ***
The key statistics counts should be correct
    ${TOTAL_ASSIGNMENT} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Total assignments") .govuk-heading-l
    Should Be Equal As Integers    ${TOTAL_ASSIGNMENT}    16  # Total assignments
    ${AWAITING} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Assignments awaiting response") .govuk-heading-l
    Should Be Equal As Integers    ${AWAITING}    7  # Assignments awaiting response
    ${ACCEPTED} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Assignments accepted") .govuk-heading-l
    Should Be Equal As Integers    ${ACCEPTED}    6  # Assignments accepted
    ${STARTED} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Assessments started") .govuk-heading-l
    Should Be Equal As Integers    ${STARTED}    3  # Assessments started
    ${SUBMITTED} =    Get text    jQuery = .govuk-grid-column-one-third:contains("Assessments submitted") .govuk-heading-l
    Should Be Equal As Integers    ${SUBMITTED}    0  # Assessments submitted