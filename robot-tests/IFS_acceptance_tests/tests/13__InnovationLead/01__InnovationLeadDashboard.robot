*** Settings ***
Documentation   IFS-984 Innovation Leads user journey navigation
...
...             IFS-191 Innovation Lead Stakeholder view filtered dashboard
...
...             IFS-1308 Innovation Leads: Project Setup
Suite Setup     The user logs-in in new browser  &{innovation_lead_one}
Suite Teardown  the user closes the browser
Force Tags      InnovationLead  HappyPath
Resource        ../../resources/defaultResources.robot
Resource        ../02__Competition_Setup/CompAdmin_Commons.robot
Resource        ../10__Project_setup/PS_Common.robot

*** Test Cases ***
Innovation Lead should see Submitted and Ineligible Applications
    [Documentation]  IFS-984
    [Tags]
    Given the user navigates to submitted application page
    And the user navigates to ineligible application page
    When the user clicks the button/link       jQuery = a:contains("Back")
    Then the user should not see the element   jQuery = .govuk-button:contains("Inform applicant")

Innovation lead cannot access CompSetup, Invite Assessors, Manage assessments, Funding decision, All Applictions
    [Documentation]  IFS-984, IFS-1414
    [Tags]
    Given the user should see permission denied page

Innnovation lead can see competitions assigned to him only
    [Documentation]  IFS-191  IFS-1308
    [Tags]  CompAdmin
    Given The Competition Admin assigns the Innovation Lead to a competition
    Then Innovateion lead see the assigned competitions
    [Teardown]  The user clicks the button/link  link = Dashboard

Innovation lead can only search for applications assigned to them
    [Documentation]  IFS-4564
    Given the user enters text to a text field    searchQuery  ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    When the user clicks the button/link          id = searchsubmit
    And the user clicks the button/link           link = ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    Then the user should see the element          jQuery = span:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    [Teardown]  The user clicks the button/link   link = Dashboard

Innovation lead cannot search for unassigned applications
    [Documentation]  IFS-4564
    Given the user enters text to a text field    searchQuery  ${INFORM_COMPETITION_NAME_1_NUMBER}
    When the user clicks the button/link          id = searchsubmit
    Then the user should see the element          jQuery = p:contains("0") strong:contains("${INFORM_COMPETITION_NAME_1_NUMBER}")
    [Teardown]  The user clicks the button/link   link = Dashboard

*** Keywords ***
The user should see permission error on page
    [Arguments]  ${page}
    The user navigates to the page and gets a custom error message  ${page}  ${403_error_message}

The Competition Admin assigns the Innovation Lead to a competition
    log in as a different user             &{Comp_admin1_credentials}
    comp admin add Innovateion lead        ${COMP_MANAGEMENT_UPDATE_COMP}/manage-innovation-leads/find
    comp admin add Innovateion lead        ${server}/management/competition/setup/${PROJECT_SETUP_COMPETITION}/manage-innovation-leads/find

comp admin add Innovateion lead
    [Arguments]  ${path}
    the user navigates to the page          ${path}
    the user clicks the button/link        jQuery = td:contains("Peter Freeman") button:contains("Add")
    the user should not see the element    jQuery = td:contains("Peter Freeman")

the user navigates to submitted application page
    the user navigates to the page             ${CA_Live}
    the user should see all live competitions
    the user navigates to the page             ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}
    the user should not see the element        jQuery = a:contains("View and update competition setup")
    the user clicks the button/link            link = Applications: Submitted, ineligible
    the user clicks the button/link            link = Submitted applications
    the user should see the element            jQuery = td:contains("${IN_ASSESSMENT_APPLICATION_4_TITLE}") ~ td:contains("57,803")

the user navigates to ineligible application page
    the user navigates to the page        ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/ineligible
    the user should see the element       css = #application-list
    the user clicks the button/link       jQuery = a:contains(${application_ids["Ineligible Virtualisation"]})
    the user should not see the element   jQuery = .govuk-button:contains("Reinstate application")

the user should see permission denied page
    The user should see permission error on page  ${server}/management/competition/setup/${openCompetitionRTO}
    The user should see permission error on page  ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/assessors/find
    The user should see permission error on page  ${server}/management/assessment/competition/${CLOSED_COMPETITION}
    The user should see permission error on page  ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/funding
    The user should see permission error on page  ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/all

Innovateion lead see the assigned competitions
    Log in as a different user             &{innovation_lead_two}
    the user should see the element        link = ${openGenericCompetition}
    the user should see the element        link = ${openCompetitionRTO_name}
    the user should not see the element    jQuery = h3:contains("${openCompetitionBusinessRTO_name}")
    the user clicks the button/link        css = #section-4 a  #Project setup tab
    the user should see the element        link = ${PROJECT_SETUP_COMPETITION_NAME}