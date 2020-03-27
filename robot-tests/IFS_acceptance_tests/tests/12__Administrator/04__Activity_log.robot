*** Settings ***
Documentation     IFS-6062 Activity Log front-end
Suite Teardown    the user closes the browser
Force Tags        Administrator  HappyPath
Resource          ../../resources/defaultResources.robot
Resource          ../10__Project_setup/PS_Common.robot

*** Variables ***
${PsActivityLogCompetitionName}     Integrated delivery programme - low carbon vehicles
${PsActivityLogCompId}              ${competition_ids['${PsActivityLogCompetitionName}']}
${AdminPsActivityLogCompUrl}        ${server}/project-setup-management/competition/${PsActivityLogCompId}/status/all

${PsActivityLogApplicationName}     Climate control solution
${PsActivityLogProjectId}           ${project_ids["${PsActivityLogApplicationName}"]}
${AppPsActivityLogCompUrl}          ${server}/project-setup/project/${PsActivityLogProjectId}

${PsCompleteAppApplicationName}     Super-EFFY - Super Efficient Forecasting of Freight Yields
${PsCompleteAppApplicationId}       ${project_ids["${PsCompleteAppApplicationName}"]}
${PsCompleteAppUrl}                 ${server}/project-setup-management/competition/${PS_Competition_Id}/project/${PsCompleteAppApplicationId}/activity-log

*** Test Cases ***
Ifs Admin is not able to see logs entries before they have happened
    [Documentation]  IFS-6062
    [Setup]  the user logs-in in new browser     &{ifs_admin_user_credentials}
    Given the admin navigates to project activity log
    Then the user is not able to see logs entries before they have happened

Project details log entry
    [Documentation]  IFS-6062
    [Setup]  log in as a different user                   &{lead_applicant2_credentials}
    Given the user navigates to the page                  ${AppPsActivityLogCompUrl}
    When the user completes project details
    Then the admin is able to see log entry               Project details completed
    And the admin is able to navigate to log entry link   View project details   Project details

Project team Project manager nominated log entry
    [Documentation]  IFS-6062
    [Setup]  log in as a different user                   &{lead_applicant2_credentials}
    Given the user navigates to the page                  ${AppPsActivityLogCompUrl}
    When the user completes project team
    Then the admin is able to see log entry               Project manager nominated
    And the admin is able to navigate to log entry link   View project team   Project team

Project team Finance contact nominated log entry
    [Documentation]  IFS-6062
    Given the admin is able to see log entry               Finance contact nominated
    Then the admin is able to navigate to log entry link   View project team   Project team

Documents added log entry
    [Documentation]  IFS-6062
    [Setup]  log in as a different user                   &{lead_applicant2_credentials}
    Given the user navigates to the page                  ${AppPsActivityLogCompUrl}
    When the user completes documents
    Then the admin is able to see log entry               Document added
    And the admin is able to navigate to log entry link   View exploitation plan   Exploitation plan

Documents approved log entry
    [Documentation]  IFS-6062
    [Setup]  log in as a different user                   &{Comp_admin1_credentials}
    Given the user navigates to the page                  ${AdminPsActivityLogCompUrl}
    When the user approves documents
    Then the admin is able to see log entry               Document approved
    And the admin is able to navigate to log entry link   View exploitation plan   Exploitation plan

The admin is able to see existing log entries
    [Documentation]  IFS-6062
    Given the user navigates to the page          ${PsCompleteAppUrl}
    Then The admin is able to see all existing log entries

*** Keywords ***
The user approves documents
    the user navigates to the page         ${server}/project-setup-management/project/${PsActivityLogProjectId}/document/all
    the user clicks the button/link        link = Exploitation plan
    internal user approve uploaded documents

The user completes documents
    the user clicks the button/link     link = Documents
    the user clicks the button/link     link = Exploitation plan
    the user uploads the file           name=document   ${valid_pdf}
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm

The user completes project team
    the user clicks the button/link     link = Project team
    the user clicks the button/link     jQuery = a:contains("Project"):contains("manager")
    the user selects the radio button   projectManager   53
    the user clicks the button/link     jQuery = button:contains("Save project manager")
    the user clicks the button/link     jQuery = a:contains("Your finance contact")
    the user selects the radio button   financeContact   53
    the user clicks the button/link     jQuery = button:contains("Save finance contact")

The admin navigates to project activity log
    the user navigates to the page     ${AdminPsActivityLogCompUrl}
    the user clicks the button/link    jQuery = small:contains("Radiowaves Ltd") a:contains("View activity log")

The admin is able to see log entry
    [Arguments]  ${logActivityName}
    log in as a different user                   &{ifs_admin_user_credentials}
    the admin navigates to project activity log
    the user should see the element              jQuery = strong:contains("${logActivityName}")

The admin is able to see all existing log entries
    the user should see the element              jQuery = strong:contains("Application submitted")
    the user should see the element              jQuery = strong:contains("Application moved into setup")
    the user should see the element              jQuery = strong:contains("Bank details added")
    the user should see the element              jQuery = strong:contains("Finance contact nominated")
    the user should see the element              jQuery = strong:contains("Project details completed")
    the user should see the element              jQuery = strong:contains("Project manager nominated")
    the user should see the element              jQuery = strong:contains("Finance viability approved")
    the user should see the element              jQuery = strong:contains("Finance eligibility approved")
    the user should see the element              jQuery = strong:contains("Spend profile approved")
    the user should see the element              jQuery = strong:contains("Spend profile sent to Innovate UK")
    the user should see the element              jQuery = strong:contains("Spend profiles generated")


The admin is able to navigate to log entry link
    [Arguments]  ${linkName}  ${pageHeadingName}
    the user clicks the button/link     jQuery = a:contains("${linkName}")
    the user should see the element     jQuery = h1:contains("${pageHeadingName}")

The user is not able to see logs entries before they have happened
    the user should see the element       jQuery = h1:contains("Activity log")
    the user should see the element       jQuery = strong:contains("Application moved into setup")
    the user should see the element       jQuery = strong:contains("Application submitted")
    the user should not see the element   jQuery = strong:contains("Project details completed")

The user completes project details
    the user clicks the button/link                     link = Project details
    the user clicks the button/link                     jQuery = a:contains("Correspondence address")
    the user enters text to a text field                id = addressForm.postcodeInput  BS1 4NT
    the user clicks the button/link                     id = postcode-lookup
    the user selects the index from the drop-down menu  1  id=addressForm.selectedPostcodeIndex
    the user clicks the button/link                     jQuery = .govuk-button:contains("Save address")

