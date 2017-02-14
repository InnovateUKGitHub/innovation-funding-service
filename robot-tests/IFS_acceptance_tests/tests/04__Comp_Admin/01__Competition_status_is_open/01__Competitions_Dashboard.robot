*** Settings ***
Documentation     INFUND-7358 Inflight competition dashboards: Ready to open dashboard
...
...               INFUND-7562 Inflight competition dashboards: Open dashboard
...
...               INFUND-7561 Inflight competition dashboards- View milestones
...
...               INFUND-7560 Inflight competition dashboards- Viewing key statistics for 'Ready to Open', 'Open', 'Closed' and 'In assessment' competition states
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot
Resource          ../CompAdmin_Commons.robot

*** Test Cases ***
Competition dashboard Open competition
    [Documentation]    INFUND-7562
    [Tags]
    When The user clicks the button/link    link=${OPEN_COMPETITION_NAME}
    Then the user should see the element    jQuery=span:contains("00000001: Connected digital additive manufacturing")
    And the user should see the element    jQuery=h1:contains("Open")
    And the user should see the element    jQuery=dt:contains("Competition type") ~ dd:contains("Programme")
    And the user should see the element    jQuery=dt:contains("Innovation sector") ~ dd:contains("Materials and manufacturing")
    And the user should see the element    jQuery=dt:contains("Innovation area") ~ dd:contains("Earth Observation")
    And the user should see the element    link=View and update competition setup
    And the user should see the element    jQuery=a:contains("Invite assessors")
    And the user should see the element    jQuery=.button:contains("Applications")
    #The following checks test if the correct buttons are disabled
    And the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("View panel sheet")
    And the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("Funding decision")
    And the user should see the element    jQuery=a:contains("Manage applications")[aria-disabled="true"]

Milestones for the Open Competitions
    [Documentation]    INFUND-7561
    Then the user should see the element    css=li:nth-child(2).done    #this keyword verifies that the 2.Briefing event is done
    And the user should see the element    css=li:nth-child(3).not-done    #this keyword verifies that the 3.Submission date is not done

Key statistics for the open Competitions
    [Documentation]    INFUND-7560
    [Setup]    Get the expected values for the counts
    Then the coults should be correct

Competition dashboard ready to Open competition
    [Documentation]    INFUND-7358
    [Tags]
    Given the user navigates to the page    ${CA_UpcomingComp}
    When The user clicks the button/link    link=${READY_TO_OPEN_COMPETITION_NAME}
    Then the user should see the element    jQuery=span:contains("00000006: Photonics for health")
    And the user should see the element    jQuery=h1:contains(" Ready to open")
    And the user should see the element    jQuery=h1:contains(" Ready to open")
    And the user should see the element    jQuery=dt:contains("Competition type") ~ dd:contains("Programme")
    And the user should see the element    jQuery=dt:contains("Innovation sector") ~ dd:contains("Materials and manufacturing")
    And the user should see the element    jQuery=dt:contains("Innovation area") ~ dd:contains("Earth Observation")
    And the user should see the element    link=View and update competition setup
    And the user should see the element    jQuery=a:contains("Invite assessors")
    #The following checks test if the correct buttons are disabled
    And the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("View panel sheet")
    And the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("Funding decision")
    And the user should see the element    jQuery=a:contains("Manage applications")[aria-disabled="true"]
    And the user should see the element    jQuery=a:contains("Applications")[aria-disabled="true"]

Milestones for the ready to Open Competitons
    [Documentation]    INFUND-7561
    Then the user should see the element    css=li:nth-child(1).not-done    #This keyword verifies that the first Milestone is not done
    And the user should see the element    css=li:nth-child(13).not-done    #This keyword verifies that the last Milestone is not done

*** Keywords ***
Get the expected values for the counts
    The user clicks the button/link    jQuery=.button:contains(Invite assessors)
    ${IInvited}=    Get text    css=div:nth-child(1) > div > span
    Set Test Variable    ${IInvited}
    ${Accepted}=    Get text    css=div:nth-child(2) > div > span
    Set Test Variable    ${Accepted}
    The user clicks the button/link    link=Competition
    The user clicks the button/link    jQuery=.button:contains(Applications)
    The user clicks the button/link    jQuery=.button:contains(All applications)
    ${Applications started}=    Get text    css=li:nth-child(2) > div > span
    Set Test Variable    ${Applications started}
    ${Applications_Beyond_50}=    Get text    css=li:nth-child(3) > div > span
    Set Test Variable    ${Applications_Beyond_50}
    ${Applications submitted}=    Get text    css=li:nth-child(4) > div > span
    Set Test Variable    ${Applications submitted}
    The user clicks the button/link    link=Applications
    The user clicks the button/link    link=Competition

the coults should be correct
    ${INVITED_COUNT}=    Get text    css=ul:nth-child(3) > li:nth-child(1) > div > span
    Should Be Equal As Integers    ${INVITED_COUNT}    ${IInvited}
    ${ACCEPTED_COUNT}=    Get text    css=ul:nth-child(3) > li:nth-child(2) > div > span
    Should Be Equal As Integers    ${ACCEPTED_COUNT}    ${Accepted}
