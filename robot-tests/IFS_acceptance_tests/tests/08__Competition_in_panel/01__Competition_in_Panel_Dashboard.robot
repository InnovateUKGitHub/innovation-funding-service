*** Settings ***
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot

*** Test Cases ***
In Panel Dashboard
    [Documentation]
    [Tags]  MySQL
    Given the user navigates to the page      ${CA_Live}
    Then the user should see the element      jQuery=section:contains("Panel") > ul:contains("${FUNDERS_PANEL_COMPETITION_NAME}")
    And the user should see the element       jQuery=div:Contains("Internet of Things") + div:contains("Programme")
    And the user should see the element       jQuery=div:Contains("Internet of Things") + div:contains("Digital manufacturing")
    And the user should see the element       jQuery=div:Contains("Internet of Things") + div:contains("applicants")
    When the user clicks the button/link      link=${FUNDERS_PANEL_COMPETITION_NAME}
    Then the user should not see the element  link=View and update competition setup
    And the user should see the element       jQuery=a:contains("Input and review funding decision")
    And the user should see the element       jQuery=.disabled:contains("Invite assessors to assess the competition")
    And the user should see the element       jQuery=a:contains("Manage assessments")

Milestones for In Panel Competitions
    Then the user should see the element  jQuery=.govuk-button--disabled[aria-disabled="true"]:contains("Manage funding notifications")
    And the user should see the element   css=li:nth-child(8).done    #Verify that 8. Line draw is done
    And the user should see the element   css=li:nth-child(9).not-done    #Verify that 9. Assessment panel is not done

Internal user can see grant terms and conditions
    [Documentation]  IFS-3036
    [Tags]
    Given The user clicks the button/link  link=Applications: All, submitted, ineligible
    And The user clicks the button/link    link=All applications
    When the user clicks the button/link   link=${application_ids["SISM - Smart Internet Security Monitor"]}
    And the user clicks the button/link    link=view the grant terms and conditions
    Then the user should see the element   jQuery=h1:contains("Terms and conditions of an Innovate UK grant award")