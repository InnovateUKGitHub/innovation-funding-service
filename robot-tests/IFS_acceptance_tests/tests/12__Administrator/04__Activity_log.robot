*** Settings ***
Documentation     IFS-6062 Activity Log front-end
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Administrator  HappyPath
Resource          ../../resources/defaultResources.robot
Resource          ../10__Project_setup/PS_Common.robot

*** Variables ***
${PsActivityLogCompetitionName}     Integrated delivery programme - low carbon vehicles
${PsActivityLogCompId}              ${competition_ids['${PsActivityLogCompetitionName}']}
${AdminPsActivityLogCompUrl}        ${server}/project-setup-management/competition/${PsActivityLogCompId}/status/all
${PsActivityLogApplicationName}     Climate control solution
${PsActivityLogProjectId}           ${project_ids["${PsActivityLogProjectId}"]}
${AppPsActivityLogCompUrl}          ${server}/project-setup/project/${PsActivityLogProjectId}

*** Test Cases ***
Ifs Admin is not able to see logs entries before they have happened
    Given the user navigates to the page    ${AdminPsActivityLogCompUrl}
    When the user clicks the button/link    jQuery = small:contains("Radiowaves Ltd") a:contains("View activity log")
    Then the user is not able to see logs entries before they have happened

Applicant completes Project details and IFS Admin is able to see Project Details log entry
    [Setup]  log in as a different user    &{lead_applicant2_credentials}
    Given the user navigates to the page   ${AppPsActivityLogCompUrl}

*** Keywords ***
Custom suite setup
    the user logs-in in new browser              &{ifs_admin_user_credentials}

The user is not able to see logs entries before they have happened
    the user should see the element       jQuery = h1:contains("Activity log")
    the user should see the element       jQuery = strong:contains("Application moved into setup")
    the user should see the element       jQuery = strong:contains("Application submitted")
    the user should not see the element   jQuery = strong:contains("Project details completed")


The user completes project details
    the user clicks the button/link                     jQuery = .govuk-button:contains("Save")
    the user should see a field and summary error       Search using a valid postcode or enter the address manually.
    the user enters text to a text field                id = addressForm.postcodeInput  BS1 4NT
    the user clicks the button/link                     id = postcode-lookup
    the user selects the index from the drop-down menu  1  id=addressForm.selectedPostcodeIndex
    the user clicks the button/link                     jQuery = .govuk-button:contains("Save address")

