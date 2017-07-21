*** Settings ***
Documentation   IFS-984 Innovation Leads user journey navigation
Suite Setup     The user logs-in in new browser  &{innovation_lead_one}
Suite Teardown  the user closes the browser
Force Tags      InnovationLead
Resource        ../../resources/defaultResources.robot
Resource        ../02__Competition_Setup/CompAdmin_Commons.robot

*** Test Cases ***
Innovation Lead should see Submitted and Ineligible Applications
    [Documentation]  IFS-984
    [Tags]  HappyPath
    Given the user navigates to the page      ${CA_Live}
    Then the user should see all live competitions
    When the user navigates to the page       ${server}/management/competition/${competition_ids['${CLOSED_COMPETITION_NAME}']}
    Then the user should not see the element  jQuery=a:contains("View and update competition setup")
    When the user clicks the button/link      link=Applications: Submitted, ineligible
    And the user clicks the button/link       link=Submitted applications
    Then the user should see the element      jQuery=td:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") ~ td:contains("28,902")
    When the user navigates to the page       ${server}/management/competition/${competition_ids['${CLOSED_COMPETITION_NAME}']}/applications/ineligible
    Then the user should see the element      css=#application-list

Innovation lead cannot access CompSetup, Invite Assessors, Manage assessments, Funding decision
    [Documentation]  IFS-984
    [Tags]
    The user should see permission error on page  ${server}/management/competition/setup/${OPEN_COMPETITION}
    The user should see permission error on page  ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/assessors/find
    The user should see permission error on page  ${server}/management/assessment/competition/${competition_ids['${CLOSED_COMPETITION_NAME}']}
    The user should see permission error on page  ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/funding


*** Keywords ***
The user should see permission error on page
    [Arguments]  ${page}
    The user navigates to the page and gets a custom error message  ${page}  ${403_error_message}