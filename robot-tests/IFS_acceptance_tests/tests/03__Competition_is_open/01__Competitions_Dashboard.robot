*** Settings ***
Documentation     INFUND-7358 Inflight competition dashboards: Ready to open dashboard
...
...               INFUND-7562 Inflight competition dashboards: Open dashboard
...
...               INFUND-7561 Inflight competition dashboards- View milestones
...
...               INFUND-7560 Inflight competition dashboards- Viewing key statistics for 'Ready to Open', 'Open', 'Closed' and 'In assessment' competition states
...
...               INF-2637 Manage interview panel link on competition dashboard - Internal
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Test Cases ***
Competition dashboard Open competition
    [Documentation]    INFUND-7562  INF-2637
    When The user clicks the button/link   link = ${openCompetitionRTO_name}
    Then the user should see the element   jQuery = span:contains("Predicting market trends programme")
    And the user should see the element    jQuery = h1:contains("Open")
    And the user should see the element    jQuery = dt:contains("Competition type") ~ dd:contains("Programme")
    And the user should see the element    jQuery = dt:contains("Innovation sector") ~ dd:contains("Materials and manufacturing")
    And the user should see the element    jQuery = dt:contains("Innovation area") ~ dd:contains("Digital manufacturing")
    And the user should see the element    link = View and update competition details
    And the user should see the element    jQuery = a:contains("Invite assessors to assess the competition")
    And the user should see the element    jQuery = a:contains("Applications: All, submitted, ineligible")
    #The following checks test if the correct buttons are disabled
    And the user should see the element    jQuery = .disabled[aria-disabled = "true"]:contains("Input and review funding decision")
    And the user should see the element    jQuery = a:contains("Manage assessments")[aria-disabled="true"]
    And the user should see the element    jQuery = a:contains("Manage assessment panel")[aria-disabled="true"]
    And the user should see the element    jQuery = a:contains("Manage interview panel")[aria-disabled="true"]
    And the user should see the element    jQuery = a:contains("Input and review funding decision")[aria-disabled="true"]

Milestones for the Open Competitions
    [Documentation]    INFUND-7561
    Then the user should see the element    css = li:nth-child(2).done    #this keyword verifies that the 2.Briefing event is done
    And the user should see the element     css = li:nth-child(3).not-done    #this keyword verifies that the 3.Submission date is not done

Key statistics for the open Competitions
    [Documentation]    INFUND-7560
    [Setup]    Get the expected values for the open counts
    Then the counts of the open competition should be correct

Competition dashboard ready to Open competition
    [Documentation]    INFUND-7358  INF-2637
    Given the user navigates to the page    ${CA_UpcomingComp}
    When The user clicks the button/link in the paginated list    link = ${READY_TO_OPEN_COMPETITION_NAME}
    Then the user should see the element    jQuery = span:contains("${READY_TO_OPEN_COMPETITION_NAME}")
    And the user should see the element     jQuery = h1:contains("Ready to open")
    And the user should see the element     jQuery = h1:contains("Ready to open")
    And the user should see the element     jQuery = dt:contains("Competition type") ~ dd:contains("Programme")
    And the user should see the element     jQuery = dt:contains("Innovation sector") ~ dd:contains("Materials and manufacturing")
    And the user should see the element     jQuery = dt:contains("Innovation area") ~ dd:contains("Digital manufacturing")
    And the user should see the element     link = View and update competition details
    And The user should not see the text in the element  css = #main-content p  Once you complete, this competition will be ready to open.
    And the user should see the element     jQuery = a:contains("Invite assessors to assess the competition")
    #The following checks test if the correct buttons are disabled
    And the user should see the element     jQuery = .disabled[aria-disabled="true"]:contains("Input and review funding decision")
    And the user should see the element     jQuery = a:contains("Manage assessments")[aria-disabled="true"]
    And the user should see the element     jQuery = a:contains("Manage assessment panel")[aria-disabled="true"]
    And the user should see the element     jQuery = a:contains("Manage interview panel")[aria-disabled="true"]
    And the user should see the element     jQuery = a:contains("Input and review funding decision")[aria-disabled="true"]
    And the user should see the element     jQuery = a:contains("Applications: All, submitted, ineligible")[aria-disabled="true"]

Milestones for the ready to Open Competitions
    [Documentation]    INFUND-7561
    Then the user should see the element    css = li:nth-child(1).not-done    #This keyword verifies that the first Milestone is not done
    And the user should see the element     css = li:nth-child(13).not-done    #This keyword verifies that the last Milestone is not done

Key statistics for the Ready to Open Competitions
    [Documentation]    INFUND-7560
    [Setup]    Get the expected values for the Ready to open counts
    Then the counts of the Ready to open statistics should be correct

*** Keywords ***
Get the expected values for the open counts
    The user clicks the button/link    jQuery = a:contains(Invite assessors)
    ${Invited} =     Get text          css = div:nth-child(1) > div > span
    Set Test Variable    ${Invited}
    ${Accepted} =     Get text         css = div:nth-child(2) > div > span
    Set Test Variable    ${Accepted}
    The user clicks the button/link    link = Competition
    The user clicks the button/link    jQuery = a:contains(Applications)
    The user clicks the button/link    jQuery = a:contains(All applications)
    ${Applications started} =     Get text    css = li:nth-child(2) > div > span
    Set Test Variable    ${Applications started}
    ${Applications_Beyond_50} =     Get text    css = li:nth-child(3) > div > span
    Set Test Variable    ${Applications_Beyond_50}
    ${Applications submitted} =     Get text    css = li:nth-child(4) > div > span
    Set Test Variable    ${Applications submitted}
    The user clicks the button/link    link = Applications
    The user clicks the button/link    link = Competition

the counts of the open competition should be correct
    ${INVITED_COUNT} =     Get text    css = ul:nth-child(3) > li:nth-child(1) > div > span
    Should Be Equal As Integers    ${INVITED_COUNT}    ${Invited}
    ${ACCEPTED_COUNT} =     Get text    css = ul:nth-child(3) > li:nth-child(2) > div > span
    Should Be Equal As Integers    ${ACCEPTED_COUNT}    ${Accepted}
    ${STARTED_COUNT} =     Get text    jQuery = .govuk-list:contains("Applications started") .govuk-grid-column-one-third:nth-child(1) .govuk-heading-l
    Should Be Equal As Integers    ${Applications started}    ${STARTED_COUNT}
    ${BEYOND_50)_COUNT} =     Get text    css = .govuk-grid-column-one-third:nth-child(2) .govuk-heading-l
    Should Be Equal As Integers    ${Applications_Beyond_50}    ${BEYOND_50)_COUNT}
    ${SUBMITTED_COUNT} =     Get text    jQuery = .govuk-list:contains("Applications submitted") .govuk-grid-column-one-third:nth-child(3) .govuk-heading-l
    Should Be Equal As Integers    ${SUBMITTED_COUNT}    ${Applications submitted}
    ${APPLICATIONS_PER_ASSESSOR} =     Get text    css = ul:nth-child(3) > li:nth-child(3) > div > span
    Should Be Equal As Integers    ${APPLICATIONS_PER_ASSESSOR}    3

Get the expected values for the Ready to open counts
    The user clicks the button/link    jQuery = a:contains(Invite assessors)
    ${Invited} =     Get text          css = div:nth-child(1) > div > span
    Set Test Variable    ${Invited}
    ${Accepted} =     Get text         css = div:nth-child(2) > div > span
    Set Test Variable    ${Accepted}
    The user clicks the button/link    link = Competition

the counts of the Ready to open statistics should be correct
    ${INVITED_COUNT} =     Get text     css = ul:nth-child(3) > li:nth-child(1) > div > span
    Should Be Equal As Integers    ${INVITED_COUNT}    ${Invited}
    ${ACCEPTED_COUNT} =     Get text    css = ul:nth-child(3) > li:nth-child(2) > div > span
    Should Be Equal As Integers    ${ACCEPTED_COUNT}    ${Accepted}
