*** Settings ***
Documentation     INFUND-3013 As a partner I want to be able to download mandatory documents supplied during project setup so that I can review information submitted to Innovate UK by the project manager
...
...               INFUND-3011 As a lead partner I need to provide mandatory documents so that they can be reviewed by all partners before submitting to Innovate UK
...
...               INFUND-3012: As a project manager I want to be able to submit all mandatory documents on behalf of all partners so that Innovate UK can review additional information to support our project setup
...
...               INFUND-2621 As a contributor I want to be able to review the current Project Setup status of all partners in my project so I can get an indication of the overall status of the consortium
...
...               INFUND-4621: As a competitions team member I want to be able to accept partner documents uploaded to the Other Documents section so that they can be used to support the Project Setup stage
...
...               INFUND-4620: As a competitions team member I want to be able to reject partner documents uploaded to the Other Documents section so that they can be informed they are unsuitable
...
...               INFUND-2610 As an internal user I want to be able to view and access all projects that have been successful within a competition so that I can track the project setup process
...
...               INFUND-5806 As a partner (non-lead) I want the status indicator of the Other Documents section to show as pending before the lead has uploaded documents so that I am aware there is no action required by me
...
...               INFUND-6139 Other Docs Team Status table should update
...
...               INFUND-7342 As a lead partner I want to be able to submit a document in the "Other Documents" section of Project Setup if an earlier document has been rejected so that I can provide an alternative document for review and approval
...
...               INFUND-7345 As an internal user I want to be able to view resubmitted documents in the "Other Documents" section of Project Setup so that they can be reviewed again for approval
...
...               INFUND-5490 document upload non-user
...
...               IFS-218 After rejection of mandatory documents, lead partner has a submit button
...
...               IFS-1864 Sole applicants do not have to provide a collaboration agreement document
...
...               IFS-1881 Project Setup internal project dashboard navigation
...
...               IFS-2371-2258 Prevent submission without both doc
...
...               project-documents-main
Suite Setup       the project is completed if it is not already complete
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot

*** Variables ***

${PROJ_WITH_SOLE_APPLICANT}  ${project_ids["High-speed rail and its effects on soil compaction"]}
${USER_BECKY_ORG_PUBSECTOR}  becky.mason@gmail.com

*** Test Cases ***
Non-lead partner cannot upload either document
    [Documentation]  INFUND-3011  INFUND-2621  INFUND-5258  INFUND-5806  INFUND-5490
    [Tags]
    Given Log in as a different user           &{collaborator1_credentials}
    When the user navigates to the page        ${project_in_setup_page}
    And The user should see the element        jQuery = p:contains("The Project Manager must upload supporting documents to be reviewed.")
    When the user clicks the button/link       link = Documents
    And the user clicks the button/link        link = Collaboration agreement
    Then the user should see the element       jQuery = p:contains("Awaiting upload by the Project Manager")
    And the user should not see the element    jQuery = label:contains("Upload")
    And the user clicks the button/link        link = Return to documents
    When the user clicks the button/link       link = Exploitation plan
    Then the user should see the element       jQuery = p:contains("Awaiting upload by the Project Manager")
    And the user should not see the element    jQuery = label:contains("Upload")

Lead partner cannot upload either document
    [Documentation]  INFUND-3011  INFUND-5490
    [Tags]
    [Setup]    log in as a different user   &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    When The user should see the element    jQuery = p:contains("The Project Manager must upload supporting documents to be reviewed.")
    When the user clicks the button/link    link = Documents
    And the user clicks the button/link     link = Collaboration agreement
    Then the user should see the element    jQuery = p:contains("Awaiting upload by the Project Manager")
    And the user clicks the button/link     link = Return to documents
    When the user clicks the button/link    link = Exploitation plan
    Then the user should see the element    jQuery = p:contains("Awaiting upload by the Project Manager")

PM cannot submit when both documents are not uploaded
    [Documentation]  INFUND-3012  INFUND-5490
    [Tags]
    Given log in as a different user           ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    And the user navigates to the page         ${project_in_setup_page}/document/all
    When the user clicks the button/link       link = Collaboration agreement
    Then the user should see the element       jQuery = label:contains("Upload")
    And the user clicks the button/link        link = Back to document overview
    When the user clicks the button/link       link = Exploitation plan
    Then the user should see the element       jQuery = label:contains("Upload")

Large pdfs not allowed for either document
    [Documentation]  INFUND-3011
    [Tags]
    Given the user navigates to the page                             ${project_in_setup_page}/document/all
    And the user clicks the button/link                              link = Collaboration agreement
    When the user uploads to the collaboration agreement/exploitation plan    ${too_large_pdf}
    Then the user should see the text in the page                    ${too_large_pdf_validation_error}
    And the user goes back to the previous page
    And the user clicks the button/link                              link = Back to document overview
    And the user clicks the button/link                              link = Exploitation plan
    When the user uploads to the collaboration agreement/exploitation plan      ${too_large_pdf}
    Then the user should see the text in the page                    ${too_large_pdf_validation_error}
    And the user should not see the text in the page                 ${too_large_pdf}
    And the user goes back to the previous page
    [Teardown]  the user clicks the button/link                      link = Back to document overview

Non pdf files not allowed for either document
    [Documentation]  INFUND-3011
    [Tags]
    Given the user clicks the button/link                            link = Collaboration agreement
    When the user uploads to the collaboration agreement/exploitation plan    ${text_file}
    Then the user should see a field error                           ${wrong_filetype_validation_error}
    And the user clicks the button/link                              link = Back to document overview
    And the user clicks the button/link                              link = Exploitation plan
    When the user uploads to the collaboration agreement/exploitation plan      ${text_file}
    Then the user should see a field error                           ${wrong_filetype_validation_error}
    And the user should not see the text in the page                 ${text_file}

PM can upload both documents
    [Documentation]  INFUND-3011  IFS-2371-2258
    [Tags]
    [Setup]    log in as a different user                      ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page                       ${project_in_setup_page}/document/all
    And the user clicks the button/link                        link = Exploitation plan
    When the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    And the user should see the element                        jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
    And the user clicks the button/link                        link = Back to document overview
    When the user clicks the button/link                       link = Collaboration agreement
    And the user uploads to the collaboration agreement/exploitation plan   ${valid_pdf}
    And the user should see the element                        jQuery = .upload-section:contains("Collaboration agreement") a:contains("${valid_pdf}")
    Then the user should not see an error in the page

Lead partner can view both documents
    [Documentation]  INFUND-3011  INFUND-2621
    [Tags]
    Given log in as a different user                &{lead_applicant_credentials}
    When the user navigates to the page             ${project_in_setup_page}/document/all
    And the user clicks the button/link             link = Collaboration agreement
    Then the user opens the link in new window      ${valid_pdf}
    And the user should not see an error in the page
    And the user closes the last opened tab
    And the user clicks the button/link             link = Back to document overview
    When the user clicks the button/link            link = Exploitation plan
    And the user opens the link in new window       ${valid_pdf}
    Then the user should not see an error in the page
    And the user closes the last opened tab
    [Teardown]    the user navigates to the page    ${project_in_setup_page}

Lead partner does not have the option to submit the documents
    [Documentation]  INFUND-3011
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup_page}/document/all
    When the user should not see an error in the page
    And the user clicks the button/link          link = Collaboration agreement
    Then the user should not see the element     id = submitDocumentButton
    And the user clicks the button/link          link = Back to document overview
    When the user clicks the button/link         link = Exploitation plan
    Then the user should not see the element     id = submitDocumentButton

Lead partner cannot remove either document
    [Documentation]  INFUND-3011
    [Tags]
    Given the user should not see the element     name = deleteDocument      #Exploitation plan remove CTA
    And the user clicks the button/link           link = Return to documents
    When the user clicks the button/link          link = Collaboration agreement
    And the user should not see the element       name = deleteDocument     #Collaboration agreement remove CTA

Non-lead partner can view both documents
    [Documentation]  INFUND-2621  INFUND-3011  INFUND-3013  INFUND-5806  INFUND-4428
    [Tags]
    Given log in as a different user        &{collaborator1_credentials}
    When the user navigates to the page     ${project_in_setup_page}
    And the user clicks the button/link     link = Documents
    And the user clicks the button/link     link = Collaboration agreement
    And the user clicks the button/link     link = ${valid_pdf}
    Then the user should not see an error in the page
    And the user closes the last opened tab
    And the user clicks the button/link     link = Return to documents
    When the user clicks the button/link    link = Exploitation plan
    And the user clicks the button/link     link = ${valid_pdf}
    Then the user should not see an error in the page
    And the user closes the last opened tab
    And the user goes back to the previous page

Non-lead partner cannot remove or submit right
    [Documentation]  INFUND-3013
    [Tags]
    [Setup]  the user clicks the button/link      link = Exploitation plan
    Given the user should not see the element     name = deleteDocument      #Exploitation plan remove CTA
    And the user clicks the button/link           link = Return to documents
    When the user clicks the button/link          link = Collaboration agreement
    And the user should not see the element       name = deleteDocument     #Collaboration agreement remove CTA
    And the user should not see the element       id = submitDocumentButton

PM can view both documents
    [Documentation]  INFUND-3011  INFUND-2621
    [Tags]
    Given log in as a different user         ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    And the user navigates to the page       ${project_in_setup_page}/document/all
    When the user clicks the button/link     link = Collaboration agreement
    Then the user should see the element     link = ${valid_pdf}
    When the user clicks the button/link     link = Back to document overview
    And the user clicks the button/link      link = Exploitation plan
    Then the user should see the element     link = ${valid_pdf}

PM can remove the Exploitation plan
    [Documentation]  INFUND-3011
    [Tags]
    When the user clicks the button/link    name = deleteDocument
    Then the user should not see an error in the page

Non-lead partner can still view the Collaboration agreement
    [Documentation]    INFUND-4252
    [Tags]
    [Setup]    log in as a different user            &{collaborator1_credentials}
    When the user navigates to the page              ${project_in_setup_page}
    And the user clicks the button/link              link = Documents
    When the user clicks the button/link             link = Collaboration agreement
    Then the user should see the text in the page    ${valid_pdf}

PM can remove the first document
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page     ${project_in_setup_page}
    And the user clicks the button/link      link = Documents
    And the user clicks the button/link      link = Collaboration agreement
    When the user clicks the button/link     name = deleteDocument
    Then the user should not see the text in the page    ${valid_pdf}

Non-lead partner cannot view either document once removed
    [Documentation]    INFUND-4252
    [Tags]
    [Setup]    log in as a different user    &{collaborator1_credentials}
    When the user navigates to the page      ${project_in_setup_page}/document/all
    And the user clicks the button/link      link = Collaboration agreement
    Then the user should not see the text in the page    ${valid_pdf}
    When the user clicks the button/link     link = Back to document overview
    And the user clicks the button/link      link = Exploitation plan
    Then the user should not see the text in the page    ${valid_pdf}

PM can upload both documents after they have been removed
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    log in as a different user                    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page                     ${project_in_setup_page}/document/all
    And the user clicks the button/link                      link = Collaboration agreement
    When the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    Then the user should see the text in the page            ${valid_pdf}
    And the user clicks the button/link                      link = Back to document overview
    When the user clicks the button/link                     link = Exploitation plan
    And the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    Then the user should not see an error in the page

Status in the dashboard remains action required after uploads
    [Documentation]    INFUND-3011
    [Tags]
    Given the user navigates to the page        ${project_in_setup_page}
    Then the user should see the element        jQuery = ul li:contains("Document") span:contains("To be completed")
    When the user clicks the button/link        link = View the status of partners
    Then the user should see the element        css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(2)

Mandatory document submission
    [Documentation]    INFUND-3011, INFUND-6152, INFUND-6139
    [Tags]
    # This ticket assumes that Project_details suite has set as PM the 'test twenty'
    Given the user navigates to the page    ${project_in_setup_page}/document/all
    And the user reloads the page
    And the user clicks the button/link     link = Collaboration agreement
    When the user clicks the button/link    id = submitDocumentButton
    And the user clicks the button/link     jQuery = button:contains("Cancel")
    Then the user should see the element    name = deleteDocument
    When the user clicks the button/link    id = submitDocumentButton
    And the user clicks the button/link     id = submitDocumentButtonConfirm
    When the user clicks the button/link    link = Return to documents
    And the user clicks the button/link     link = Exploitation plan
    Then the user clicks the button/link    id = submitDocumentButton
    And the user clicks the button/link     id = submitDocumentButtonConfirm
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    When the user navigates to the page     ${project_in_setup_page}

PM can still view both documents after submitting
    [Documentation]    INFUND-3012
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}/document/all
    When the user clicks the button/link    link = Collaboration agreement
    And the user clicks the button/link     link = ${valid_pdf}
    Then the user should not see an error in the page
    And the user closes the last opened tab
    When the user clicks the button/link    link = Return to documents
    And the user clicks the button/link     link = Exploitation plan
    Then the user clicks the button/link    link = ${valid_pdf}
    And the user should not see an error in the page
    And the user closes the last opened tab

PM cannot remove the documents after submitting
    [Documentation]    INFUND-3012
    [Tags]
    Given the user should not see the element     name = deleteDocument      #Exploitation plan remove CTA
    And the user clicks the button/link           link = Return to documents
    When the user clicks the button/link          link = Collaboration agreement
    And the user should not see the element       name = deleteDocument     #Collaboration agreement remove CTA
    And the user should not see the element       jQuery = .govuk-button.enabled:contains("Submit")

Lead partner cannot remove the documents after submission by PM
    [Documentation]  INFUND-3012
    [Tags]
    [Setup]  The user logs-in in new browser       &{lead_applicant_credentials}
    Given the user navigates to the page           ${project_in_setup_page}/document/all
    When the user clicks the button/link           link = Collaboration agreement
    Then the user should not see the element       name = deleteDocument
    And the user clicks the button/link            link = Return to documents
    When the user clicks the button/link           link = Exploitation plan
    Then the user should not see the element       name = deleteDocument

Lead partner can still view both documents after submitting
    [Documentation]    INFUND-3012
    [Tags]
    When the user clicks the button/link    link = ${valid_pdf}
    Then the user should not see an error in the page
    And the user closes the last opened tab
    And the user clicks the button/link     link = Return to documents
    When the user clicks the button/link    link = Collaboration agreement
    Then the user clicks the button/link    link = ${valid_pdf}
    And the user should not see an error in the page
    And the user closes the last opened tab

Non-lead partner cannot remove the documents after submission by PM
    [Documentation]  INFUND-3012
    [Tags]
    [Setup]  log in as a different user         &{collaborator1_credentials}
    Given the user navigates to the page        ${project_in_setup_page}/document/all
    When the user clicks the button/link        link = Collaboration agreement
    Then the user should not see the element    name = deleteDocument
    And the user clicks the button/link         link = Return to documents
    When the user clicks the button/link        link = Exploitation plan
    Then the user should not see the element    name = deleteDocument

Non-lead partner can still view both documents after submitting
    [Documentation]    INFUND-3012 , INFUND-4428, INFUND-6139
    [Tags]
    When the user clicks the button/link        link = ${valid_pdf}
    Then the user should not see an error in the page
    And the user closes the last opened tab
    And the user clicks the button/link         link = Return to documents
    When the user clicks the button/link        link = Collaboration agreement
    Then the user clicks the button/link        link = ${valid_pdf}
    Then the user should not see an error in the page
    And the user closes the last opened tab
    When the user navigates to the page         ${project_in_setup_page}
    And the user clicks the button/link         link = View the status of partners
    And the user should see the element         css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(2)

CompAdmin can see uploaded files
    [Documentation]    INFUND-4621, IFS-1881
    [Tags]
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    When the user navigates to the page     ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link     link = ${PROJECT_SETUP_COMPETITION_NAME}
    When the user navigates to the page     ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/document/all
    And the user clicks the button/link     link = Collaboration agreement
    And the user clicks the button/link     link = ${valid_pdf}
    Then the user should see the file without error
    And the user clicks the button/link     link = Documents
    When the user clicks the button/link    link = Exploitation plan
    And the user clicks the button/link     link = ${valid_pdf}
    Then the user should see the file without error

CompAdmin rejects both documents
    [Documentation]    INFUND-4620
    [Tags]
    Given the user navigates to the page        ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/document/all
    When the user clicks the button/link        link = Collaboration agreement
    Then compAdmin reject uploaded documents
    And the user clicks the button/link         link = Return to documents
    When the user clicks the button/link        link = Exploitation plan
    Then compAdmin reject uploaded documents

Partners can see the documents rejected
    [Documentation]    INFUND-5559, INFUND-5424, INFUND-7342, IFS-218
    [Tags]
    When log in as a different user              ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}  #Project Manager
    Then Partners can see both documents rejected
    When log in as a different user              &{lead_applicant_credentials}
    Then Partners can see both documents rejected
    When log in as a different user              &{collaborator2_credentials}
    Then Partners can see both documents rejected
    When log in as a different user              &{collaborator1_credentials}
    Then Partners can see both documents rejected

After rejection, status in the dashboard remains action required after uploads
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]
    When the user navigates to the page     ${project_in_setup_page}
    When the user clicks the button/link    link = View the status of partners
    Then the user should see the element    css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(2)

Project Manager can remove the offending documents
    [Documentation]    INFUND-7342
    [Tags]
    [Setup]    log in as a different user     ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page      ${project_in_setup_page}/document/all
    When the user clicks the button/link      link = Collaboration agreement
    And the user clicks the button/link       name = deleteDocument
    Then the user should not see the element  link = ${valid_pdf}
    And the user clicks the button/link       link = Back to document overview
    When the user clicks the button/link      link = Exploitation plan
    And the user clicks the button/link       name = deleteDocument
    Then the user should not see the element  link = ${valid_pdf}

After rejection, non-lead partner cannot upload either document
    [Documentation]    INFUND-3011, INFUND-2621, INFUND-5258, INFUND-5806, INFUND-7342
    [Tags]
    [Setup]    log in as a different user       &{collaborator1_credentials}
    Given the user navigates to the page        ${project_in_setup_page}
    And The user should see the element         jQuery = p:contains("The Project Manager must upload supporting documents to be reviewed.")
    When the user clicks the button/link        link = Documents
    And the user clicks the button/link         link = Collaboration agreement
    Then the user should not see the element    jQuery = label:contains("Upload")
    And the user clicks the button/link         link = Return to documents
    When the user clicks the button/link        link = Exploitation plan
    Then the user should not see the element    jQuery = label:contains("Upload")

After rejection PM can upload both documents when both documents are removed
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page     ${project_in_setup_page}
    And the user clicks the button/link      link = Documents
    And the user clicks the button/link      link = Exploitation plan
    When the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    And the user should see the element      jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
    And the user clicks the button/link      link = Back to document overview
    When the user clicks the button/link     link = Collaboration agreement
    And the user uploads to the collaboration agreement/exploitation plan   ${valid_pdf}
    And the user should see the element      jQuery = .upload-section:contains("Collaboration agreement") a:contains("${valid_pdf}")
    Then the user should not see an error in the page

After rejection, mandatory document submission
    [Documentation]    INFUND-3011, INFUND-6152, INFUND-7342
    [Tags]
    [Setup]    log in as a different user   ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    # This ticket assumes that Project_details suite has set as PM the 'test twenty'
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link     link = Documents
    And the user reloads the page
    And the user clicks the button/link     link = Collaboration agreement
    When the user clicks the button/link    id = submitDocumentButton
    And the user clicks the button/link     jQuery = button:contains("Cancel")
    Then the user should see the element    name = deleteDocument
    When the user clicks the button/link    id = submitDocumentButton
    And the user clicks the button/link     id = submitDocumentButtonConfirm
    When the user clicks the button/link    link = Return to documents
    And the user clicks the button/link     link = Exploitation plan
    Then the user clicks the button/link    id = submitDocumentButton
    And the user clicks the button/link     id = submitDocumentButtonConfirm
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    When the user navigates to the page     ${project_in_setup_page}

# This is bank details and finance test but has been placed here as the required project is used here
Project finance can see zero funding for partner in bank details
    [Documentation]    INFUND-9269
    [Tags]
    When partners submit bank details
    And log in as a different user                      &{collaborator1_credentials}
    And the user navigates to the page                  ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-checks/eligibility
    Then the user should see the text in the element    css = .table-overview tr:nth-child(1) td:nth-child(2)    £200,903    # Total costs
    And the user should see the text in the element     css = .table-overview tr:nth-child(1) td:nth-child(3)     0%          # Funding level (%)
    And the user should see the text in the element     css = .table-overview tr:nth-child(1) td:nth-child(4)     0         # Funding sought
    When log in as a different user                     &{internal_finance_credentials}
    And the user navigates to the page                  ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/review-all-bank-details
    Then the user should see the element                jQuery = .read-only span:contains("No action required")

CompAdmin approves both documents
    [Documentation]    INFUND-4621, INFUND-5507, INFUND-7345
    [Tags]
    [Setup]    Log in as a different user       &{Comp_admin1_credentials}
    Given the user navigates to the page        ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/document/all
    When the user clicks the button/link        link = Collaboration agreement
    Then internal user approve uploaded documents
    And the user clicks the button/link         link = Return to documents
    When the user clicks the button/link        link = Exploitation plan
    Then internal user approve uploaded documents

Partners can see the documents approved
    [Documentation]    INFUND-5559, INFUND-5424, INFUND-7345
    [Tags]
     When log in as a different user         ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}  #Project Manager
     Then Partners can see both documents approved
     When log in as a different user         &{lead_applicant_credentials}
     Then Partners can see both documents approved
     When log in as a different user         &{collaborator2_credentials}
     Then Partners can see both documents approved
     When log in as a different user         &{collaborator1_credentials}
     Then Partners can see both documents approved

CompAdmin can see Project status updated
    [Documentation]    INFUND-2610
    [Tags]
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link     link = ${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see the element    jQuery = tr:nth-child(2):contains("${PROJECT_SETUP_APPLICATION_1_TITLE}")
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(2) > td:nth-child(3) > a

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049 , INFUND-5543
    [Tags]  Experian
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    When the user navigates to the page      ${internal_competition_status}
    Then the user should see the element     css = #table-project-status tr:nth-of-type(2) td:nth-of-type(1).status.ok
    And the user should see the element      css = #table-project-status tr:nth-of-type(2) td:nth-of-type(2).status.ok
    And the user should see the element      css = #table-project-status tr:nth-of-type(2) td:nth-of-type(3).status
    And the user should see the element      css = #table-project-status tr:nth-of-type(2) td:nth-of-type(4).status.action
    And the user should see the element      css = #table-project-status > tbody > tr:nth-child(2) > td:nth-child(3) > a

# Project used for sole applicant tests - High-speed rail and its effects on soil compaction -
# has lead org type of Public sector, charity or non Je-S registered research organisation
Sole applicant uploads only exploitation plan and submits
    [Documentation]  IFS-1864
    [Tags]
    [Setup]  log in as a different user         ${USER_BECKY_ORG_PUBSECTOR}  ${short_password}
    Given the user navigates to the page        ${server}/project-setup/project/${PROJ_WITH_SOLE_APPLICANT}
    And the user clicks the button/link         link = Documents
    Then the user should not see the element    link = Collaboration agreement
    When the user clicks the button/link        link = Exploitation plan
    Then the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    And the user clicks the button/link         id = submitDocumentButton
    And the user clicks the button/link         id = submitDocumentButtonConfirm
    And the user clicks the button/link         link = Return to documents
    When the user clicks the button/link        link = Set up your project
    Then the user should see the element        jQuery = li:contains("Documents") span:contains("Awaiting review")

CompAdmin sees uploaded file and approves it
    [Documentation]    IFS-1864
    [Tags]
    [Setup]    Log in as a different user       &{Comp_admin1_credentials}
    Given the user navigates to the page        ${server}/project-setup-management/project/${PROJ_WITH_SOLE_APPLICANT}/document/all
    Then the user should not see the element    link = Collaboration agreement
    And the user clicks the button/link         link = Exploitation plan
    When the user clicks the button/link        link = ${valid_pdf}
    Then the user should see the file without error
    And internal user approve uploaded documents

Sole applicant can see documents approval
    [Documentation]    IFS-1864
    [Tags]
    [Setup]  log in as a different user    ${USER_BECKY_ORG_PUBSECTOR}  ${short_password}
    When the user navigates to the page    ${server}/project-setup/project/${PROJ_WITH_SOLE_APPLICANT}
    Then the user should see the element   jQuery = li:contains("Documents") span:contains("Completed")
    When the user clicks the button/link   link = Documents
    And the user clicks the button/link    link = Exploitation plan
    Then the user should see the element   jQuery = .success-alert h2:contains("This document has been approved by us.")

*** Keywords ***
the project is completed if it is not already complete
    The user logs-in in new browser  &{lead_applicant_credentials}
    the user navigates to the page   ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details
    ${project_manager_not_set}  ${value} =  run keyword and ignore error without screenshots  The user should not see the element  jQuery = #project-manager-status.yes
    run keyword if  '${project_manager_not_set}' == 'PASS'  all previous sections of the project are completed

all previous sections of the project are completed
    project lead submits project details        ${PROJECT_SETUP_APPLICATION_1_PROJECT}
    all partners submit their finance contacts
    project finance submits monitoring officer  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  Grace  Harper  ${test_mailbox_two}+monitoringofficer@gmail.com  08549731414

all partners submit their finance contacts
    the partner submits their finance contact  ${EMPIRE_LTD_ID}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  &{lead_applicant_credentials}
    the partner submits their finance contact  ${organisationLudlowId}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  &{collaborator1_credentials}
    the partner submits their finance contact  ${organisationEggsId}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  &{collaborator2_credentials}

the user uploads to the collaboration agreement/exploitation plan
    [Arguments]  ${file_name}
    choose file  name = document  ${upload_folder}/${file_name}

the user should see the file without error
    the user should not see an error in the page
    the user closes the last opened tab

partners submit bank details
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_LEAD_PARTNER_EMAIL}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  ${account_one}  ${sortCode_one}
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  ${account_one}  ${sortCode_one}

compAdmin reject uploaded documents
    the user selects the radio button           approved   false
    the user enters text to a text field        id = document-reject-reason   Rejected
    the user clicks the button/link             id = submit-button
    the user clicks the button/link             jQuery = .modal-reject-configured-doc button:contains("Cancel")
    the user should not see an error in the page
    the user clicks the button/link             id = submit-button
    the user clicks the button/link             id = reject-document
    the user should see the element             jQuery = p:contains("You have rejected this document. Please contact the Project Manager to explain your decision.")

Partners can see both documents rejected
    the user navigates to the page       ${project_in_setup_page}/document/all
    the user clicks the button/link      link = Collaboration agreement
    the user should see the element      jQuery = .warning-alert h2:contains("We will contact you to discuss this document.")
    the user should not see the element  jQuery = label:contains("Upload")
    the user clicks the button/link      link = Return to documents
    the user clicks the button/link      link = Exploitation plan
    the user should see the element      jQuery = .warning-alert h2:contains("We will contact you to discuss this document.")
    the user should not see the element  jQuery = label:contains("Upload")

Partners can see both documents approved
    the user navigates to the page      ${project_in_setup_page}/document/all
    the user clicks the button/link     link = Collaboration agreement
    the user should see the element     jQuery = .success-alert h2:contains("This document has been approved by us.")
    the user clicks the button/link     link = Return to documents
    the user clicks the button/link     link = Exploitation plan
    the user should see the element     jQuery = .success-alert h2:contains("This document has been approved by us.")