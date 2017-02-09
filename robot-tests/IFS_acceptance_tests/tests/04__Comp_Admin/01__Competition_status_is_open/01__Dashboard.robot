*** Settings ***
Documentation     INFUND-7358 Inflight competition dashboards: Ready to open dashboard
...
...               INFUND-7562 Inflight competition dashboards: Open dashboard
...
...               INFUND-7561 Inflight competition dashboards- View milestones
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
    And the user should see the element    jQuery=a:contains("Invite assessors to assess the competition")
    And the user should see the element    jQuery=a:contains("Applications - All, submitted, non-submitted, ineligable")
    #The following checks test if the correct buttons are disabled
    And the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("Download panel sheet")
    And the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("Input and review funding decision")
    And the user should see the element    jQuery=a:contains("Assessor management - assignment to applications")[aria-disabled="true"]

Milestones for the Open Competitions
    [Documentation]    INFUND-7561
    Then the user should see the element    css=li:nth-child(2).done    #this keyword verifies that the 2.Briefing event is done
    And the user should see the element    css=li:nth-child(3).not-done    #this keyword verifies that the 3.Submission date is not done

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
    And the user should see the element    jQuery=a:contains("Invite assessors to assess the competition")
    #The following checks test if the correct buttons are disabled
    And the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("Download panel sheet")
    And the user should see the element    jQuery=.disabled[aria-disabled="true"]:contains("Input and review funding decision")
    And the user should see the element    jQuery=a:contains("Assessor management - assignment to applications")[aria-disabled="true"]
    And the user should see the element    jQuery=a:contains("Applications - All, submitted, non-submitted, ineligable")[aria-disabled="true"]

Milestones for the ready to Open Competitons
    [Documentation]    INFUND-7561
    Then the user should see the element    css=li:nth-child(1).not-done    #This keyword verifies that the first Milestone is not done
    And the user should see the element    css=li:nth-child(13).not-done    #This keyword verifies that the last Milestone is not done
