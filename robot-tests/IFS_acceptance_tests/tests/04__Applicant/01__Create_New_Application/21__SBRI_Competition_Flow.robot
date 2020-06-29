*** Settings ***
Documentation     IFS-7313  New completion stage for Procurement - Comp setup journey
...
...

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***


*** Test Cases ***
Comp admin can save the completition satge with competition close option
    [Documentation]  IFS-7313
    Given the user navigates to the page                   ${CA_UpcomingComp}
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Create competition")
    When the user fills in the CS Initial details          ${organisationEligibilityCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  2  PROCUREMENT
    Then the user should see the enabled element           link = ${organisationalEligibilityTitle}
    And the user should not see the element                jQuery = li:contains("${organisationalEligibilityTitle}") .task-status-complete







*** Keywords ***
Custom Suite Setup
    Connect to Database  @{database}
    The user logs-in in new browser            &{Comp_admin1_credentials}
    Set predefined date variables

Custom suite teardown
    the user closes the browser
    Disconnect from database