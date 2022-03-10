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
...
...               IFS-6728 - Mop up ticket for ifs 6502
...
...               IFS-7723 Improvement to company search results
...
...               IFS-9575 MO documents: MO notification of submission
...
...               IFS-9579 MO documents: Change of internal approve/reject authority
...
...               IFS-9577 MO documents: approve or reject
...
...               IFS-9701 MO documents: Monitor project page status updates
...
...               IFS-9578 MO documents: design changes for other roles (not MO or Project manager)
...
...               IFS-9965 Enable document link when the MO rejects the document 
...
...               IFS-10239 Rejected document is visible in PM view throws upload error
...
...               IFS-10067 Email sent to MO to review the documents shouldnt display projectID
...
Suite Setup       Custom Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${PROJ_WITH_SOLE_APPLICANT}              ${project_ids["High-speed rail and its effects on soil compaction"]}
${USER_BECKY_ORG_PUBSECTOR}              becky.mason@gmail.com
${USER_PM}                               myrtle.barton@jabbertype.example.com
${MO_EMAIL}                              Orville.Gibbs@gmail.com
${newOrgRejectedDocumentMessagePM}       We have marked this document as incomplete because you have made a change to your project team.
${newOrgRejectedDocumentMessagePartner}  We have marked this document as incomplete because a change has been made to your project team.
${MO_DocApproval_application_Title}      Correlation of maintenance data of corroded knuckles (CorMaCK)
${MO_DocApproval_application_No}         ${application_ids["${MO_DocApproval_application_Title}"]}
${MO_DocApproval_ProjectID}              ${project_ids["${MO_DocApproval__application_Title}"]}

*** Test Cases ***
Non-lead partner cannot upload either document
    [Documentation]  INFUND-3011  INFUND-2621  INFUND-5258  INFUND-5806  INFUND-5490
    [Tags]  HappyPath
    When the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And The user should see the element        jQuery = p:contains("The Project Manager must upload supporting documents to be reviewed.")
    When the user goes to documents page       Documents  Collaboration agreement
    Then the user should see the element       jQuery = p:contains("Awaiting upload by the Project Manager")
    And the user should not see the element    jQuery = label:contains("Upload")
    When the user goes to documents page       Return to documents  Exploitation plan
    Then the user should see the element       jQuery = p:contains("Awaiting upload by the Project Manager")
    And the user should not see the element    jQuery = label:contains("Upload")

Lead partner cannot upload either document
    [Documentation]  INFUND-3011  INFUND-5490
    [Tags]  HappyPath
    [Setup]    log in as a different user   &{lead_applicant_credentials}
    Given the user navigates to the page    ${Project_In_Setup_Page}
    When The user should see the element    jQuery = p:contains("The Project Manager must upload supporting documents to be reviewed.")
    When the user goes to documents page    Documents  Collaboration agreement
    Then the user should see the element    jQuery = p:contains("Awaiting upload by the Project Manager")
    And the user goes to documents page     Return to documents  Exploitation plan
    Then the user should see the element    jQuery = p:contains("Awaiting upload by the Project Manager")

Large pdfs not allowed for either document
    [Documentation]  INFUND-3011
    [Tags]
    Given Log in as a different user                    &{lead_applicant_credentials_bd}
    Given the user navigates to the page                ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    And the user clicks the button/link                 link = Collaboration agreement
    When the user uploads to the collaboration agreement/exploitation plan   ${too_large_pdf}
    Then the user should see a field error              ${too_large_10MB_validation_error}
    When the user uploads to the collaboration agreement/exploitation plan    ${too_large_pdf}
    Then the user should see a field error              ${too_large_10MB_validation_error}
    And the user should not see the element             link = ${too_large_pdf}")
    [Teardown]  the user clicks the button/link         link = Back to document overview

Non pdf files not allowed for either document
    [Documentation]  INFUND-3011
    [Tags]
    Given the user clicks the button/link                link = Collaboration agreement
    When the user uploads to the collaboration agreement/exploitation plan      ${text_file}
    Then the user should see a field error               ${wrong_filetype_validation_error}
    And the user goes to documents page                  Back to document overview  Exploitation plan
    When the user uploads to the collaboration agreement/exploitation plan      ${text_file}
    Then the user should see a field error               ${wrong_filetype_validation_error}
    And the user should not see the element              jQuery = .govuk-error-message:contains("${text_file}")

PM can upload both documents
    [Documentation]  INFUND-3011  IFS-2371-2258 IFS-9575
    [Tags]  HappyPath
    [Setup]    log in as a different user     &{lead_applicant_credentials_bd}
    Given PM uploads the project documents    ${Grade_Crossing_Project_Id}

Lead partner can view both documents, but not submit or remove them
    [Documentation]  INFUND-3011  INFUND-2621
    [Tags]  HappyPath
    Given Log in as a different user                lewis.poole@vitruvius.example.com  ${short_password}
    When the user navigates to the page             ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    And the user clicks the button/link             link = Collaboration agreement
    And the user should not see the element         id = submit-document-button
    And the user should not see the element         name = deleteDocument
    And open pdf link                               jQuery = a:contains("${valid_pdf} (opens in a new window)")
    And the user goes to documents page            Back to document overview  Exploitation plan
    Then the user should not see the element         id = submit-document-button
    And the user should not see the element         name = deleteDocument
    And open pdf link                               jQuery = a:contains("${valid_pdf} (opens in a new window)")
    [Teardown]    the user navigates to the page    ${server}/project-setup/project/${Grade_Crossing_Project_Id}

Non-lead partner can view both documents
    [Documentation]  INFUND-2621  INFUND-3011  INFUND-3013  INFUND-5806  INFUND-4428
    [Tags]
    Given log in as a different user              &{collaborator1_credentials_bd}
    When the user navigates to the page           ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user goes to documents page           Documents  Collaboration agreement
    And the user should not see the element       id = submit-document-button
    And open pdf link                             jQuery = a:contains("${valid_pdf} (opens in a new window)")
    When the user goes to documents page          Return to documents  Exploitation plan
    And the user should not see the element       id = submit-document-button
    Then open pdf link                            jQuery = a:contains("${valid_pdf} (opens in a new window)")

PM can view both documents
    [Documentation]  INFUND-3011  INFUND-2621
    [Tags]  HappyPath
    Given log in as a different user         &{lead_applicant_credentials_bd}
    And the user navigates to the page       ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link     link = Collaboration agreement
    Then the user should see the element     jQuery = a:contains("${valid_pdf} (opens in a new window)")
    When the user goes to documents page     Back to document overview  Exploitation plan
    Then the user should see the element     jQuery = a:contains("${valid_pdf} (opens in a new window)")

PM can remove the Exploitation plan
    [Documentation]  INFUND-3011
    [Tags]  HappyPath
    When the user clicks the button/link    name = deleteDocument
    Then the user should not see an error in the page

Non-lead partner can still view the Collaboration agreement
    [Documentation]    INFUND-4252
    [Tags]
    [Setup]    log in as a different user            &{collaborator1_credentials_bd}
    When the user navigates to the page              ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user goes to documents page              Documents  Collaboration agreement
    Then the user should see the element             jQuery = a:contains("${valid_pdf} (opens in a new window)")
    [Teardown]  the user closes the browser

PM can remove the first document
    [Documentation]    INFUND-3011
    [Tags]  HappyPath
    [Setup]  the user logs-in in new browser    &{lead_applicant_credentials_bd}
    Given the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user goes to documents page         Documents  Collaboration agreement
    When the user clicks the button/link        name = deleteDocument
    Then the user should not see the element    jQuery = a:contains("${valid_pdf} (opens in a new window)")

Non-lead partner cannot view either document once removed
    [Documentation]    INFUND-4252
    [Tags]
    [Setup]    log in as a different user         &{collaborator1_credentials_bd}
    When the user navigates to the page           ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    And the user clicks the button/link           link = Collaboration agreement
    Then the user should not see the element      jQuery = a:contains("${valid_pdf} (opens in a new window)")
    When the user goes to documents page          Back to document overview  Exploitation plan
    Then the user should not see the element      jQuery = a:contains("${valid_pdf} (opens in a new window)")

Assign a MO to the project and they check the documents are incomplete
    [Documentation]  IFS-9577  IFS-9701
    [Tags]
    Given log in as a different user        &{monitoring_officer_one_credentials}
    When the MO navigates to page
    Then the user should see the element    jQuery = ul li:contains("Documents") span:contains("Incomplete")

PM can upload both documents after they have been removed
    [Documentation]    INFUND-3011
    [Tags]  HappyPath
    [Setup]    log in as a different user       &{lead_applicant_credentials_bd}
    Given PM uploads the project documents      ${Grade_Crossing_Project_Id}

Status in the dashboard remains action required after uploads
    [Documentation]    INFUND-3011
    [Tags]
    Given the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    Then the user should see the element        jQuery = ul li:contains("Document") span:contains("To be completed")
    When the user clicks the button/link        link = View the status of partners
    Then the user should see the element        css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(3)

Mandatory document submission
    [Documentation]    INFUND-3011, INFUND-6152, INFUND-6139
    [Tags]  HappyPath
    # This ticket assumes that Project_details suite has set as PM the 'test twenty'
    When the user navigates to the page    ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    And the user reloads the page
    Then PM submits both documents     ${Grade_Crossing_Project_Id}

MO can see the Documents are awaiting review
    [Documentation]    IFS-9701
    [Tags]
    Given log in as a different user     &{monitoring_officer_one_credentials}
    When the user navigates to the page       ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    Then the user should see the element      jQuery = ul li:contains("Documents") span:contains("Awaiting review")

PM can still view both documents after submitting
    [Documentation]    INFUND-3012
    [Tags]
    Given log in as a different user         &{lead_applicant_credentials_bd}
    And the user navigates to the page       ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link     link = Collaboration agreement
    And open pdf link                        jQuery = a:contains("${valid_pdf} (opens in a new window)")
    Then the user goes to documents page     Return to documents  Exploitation plan
    And open pdf link                        jQuery = a:contains("${valid_pdf} (opens in a new window)")

PM cannot remove the documents after submitting
    [Documentation]    INFUND-3012
    [Tags]
    Given partners can not remove the documents
    And the user should not see the element       jQuery = .govuk-button.enabled:contains("Submit")

Lead partner cannot remove the documents after submission by PM
    [Documentation]  INFUND-3012
    [Setup]  log in as a different user            &{lead_applicant_credentials_bd}
    Given the user navigates to the page           ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link           link = Collaboration agreement
    Then the user should not see the element       name = deleteDocument
    When the user goes to documents page           Return to documents  Exploitation plan
    Then the user should not see the element       name = deleteDocument

Lead partner can still view both documents after submitting
    [Documentation]    INFUND-3012
    [Tags]
    Given open pdf link                     jQuery = a:contains("${valid_pdf} (opens in a new window)")
    When the user goes to documents page    Return to documents  Collaboration agreement
    Then open pdf link                      jQuery = a:contains("${valid_pdf} (opens in a new window)")

Internal finance cannot approve Exploitation or Collaboration documents
    [Documentation]   IFS-9579
    Given log in as a different user              &{internal_finance_credentials}
    And the user navigates to the page            ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link          link = Collaboration agreement
    Then the user cannot approve the document     approved   true
    And the user clicks the button/link           link = Return to documents
    And the user clicks the button/link           link = Exploitation plan
    And the user cannot approve the document      approved   true

Comp admin cannot approve Exploitation or Collaboration documents
    [Documentation]   IFS-9579
    Given log in as a different user              &{Comp_admin1_credentials}
    And the user navigates to the page            ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link          link = Collaboration agreement
    Then the user cannot approve the document     approved   true
    And the user clicks the button/link           link = Return to documents
    And the user clicks the button/link           link = Exploitation plan
    And the user cannot approve the document      approved   true

Non-lead partner can view documents, but not remove after submission by PM
    [Documentation]  INFUND-3012  INFUND-4428  INFUND-6139
    [Tags]
    [Setup]  log in as a different user         &{collaborator1_credentials_bd}
    Given the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link        link = Collaboration agreement
    Then the user should not see the element    name = deleteDocument
    When the user goes to documents page        Return to documents  Exploitation plan
    Then the user should not see the element    name = deleteDocument
    Given open pdf link                         jQuery = a:contains("${valid_pdf} (opens in a new window)")
    When the user goes to documents page        Return to documents  Collaboration agreement
    Then open pdf link                          jQuery = a:contains("${valid_pdf} (opens in a new window)")
    And the user navigates to the page          ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user clicks the button/link         link = View the status of partners
    And the user should see the element         css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(3)

CompAdmin can see uploaded files
    [Documentation]    INFUND-4621, IFS-1881
    [Tags]  HappyPath
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    When the user navigates to the page     ${SERVER}/project-setup-management/project/${Grade_Crossing_Project_Id}/document/all
    And the user clicks the button/link     link = Collaboration agreement
    And open pdf link                       jQuery = a:contains("${valid_pdf} (opens in a new window)")
    When the user goes to documents page    Back to documents  Exploitation plan
    Then open pdf link                      jQuery = a:contains("${valid_pdf} (opens in a new window)")

IfsAdmin adds a partner organisation and all partners can see rejected documents
    [Documentation]  IFS-6728  IFS-7723
    [Setup]  Log in as a different user  &{ifs_admin_user_credentials}
    Given ifsadmin approves all documents
    And the user clicks the button/link                      jQuery = a:contains("Add a partner organisation")
    When the user adds a new partner organisation            Testing Errors Organisation  FName Surname  testErrMsg@gmail.com
    And a new organisation is able to accept project invite  FName  Surname  testErrMsg@gmail.com  FIRSTGROUP  FIRSTGROUP PLC  ${Grade_Crossing_Applicaiton_No}  ${Grade_Crossing_Application_Title}
    Then partners can see rejected documents due to new organisation
    [Teardown]  the user removes and reuploads project files

IfsAdmin rejects both documents
    [Documentation]    INFUND-4620  IFS-9579
    [Tags]  HappyPath
    [Setup]  Log in as a different user         &{ifs_admin_user_credentials}
    Given the user navigates to the page        ${SERVER}/project-setup-management/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link        link = Collaboration agreement
    Then ifs admin reject uploaded documents
    When the user goes to documents page        Return to documents  Exploitation plan
    Then ifs admin reject uploaded documents

MO can view Incomplete status on rejected document
    [Documentation]  IFS-9578  IFS-9701
    [Setup]  Log in as a different user      &{monitoring_officer_one_credentials}
    Given the user navigates to the page     ${SERVER}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    Then the user should see the element     jQuery = div:contains("Collaboration agreement") ~ div span:contains("Incomplete")
    And the user should see the element      jQuery = div:contains("Exploitation plan") ~ div span:contains("Incomplete")

Partners can see the documents rejected
    [Documentation]    INFUND-5559, INFUND-5424, INFUND-7342, IFS-218  IFS-10239
    [Tags]  HappyPath
    When log in as a different user                &{lead_applicant_credentials_bd}
    Then Partners can see both documents rejected  This document is rejected. We will contact you to discuss this document.
    When log in as a different user                &{collaborator1_credentials_bd}
    Then Partners can see both documents rejected  This document is rejected. We will contact you to discuss this document.
    When log in as a different user                &{collaborator2_credentials_bd}
    Then Partners can see both documents rejected  This document is rejected. We will contact you to discuss this document.

After rejection, status in the dashboard remains action required after uploads
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]
    When the user navigates to the page     ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    When the user clicks the button/link    link = View the status of partners
    Then the user should see the element    css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(3)

Project Manager can remove the offending documents
    [Documentation]    INFUND-7342
    [Tags]  HappyPath
    [Setup]  log in as a different user         &{lead_applicant_credentials_bd}
    Given the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link        link = Collaboration agreement
    And the user clicks the button/link         name = deleteDocument
    Then the user should not see the element    jQuery = a:contains("${valid_pdf} (opens in a new window)")
    When the user goes to documents page        Back to document overview  Exploitation plan
    And the user clicks the button/link         name = deleteDocument
    Then the user should not see the element    jQuery = a:contains("${valid_pdf} (opens in a new window)")

After rejection, non-lead partner cannot upload either document
    [Documentation]    INFUND-3011, INFUND-2621, INFUND-5258, INFUND-5806, INFUND-7342
    [Tags]
    [Setup]    log in as a different user       &{collaborator1_credentials_bd}
    Given the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And The user should see the element         jQuery = p:contains("The Project Manager must upload supporting documents to be reviewed.")
    When the user goes to documents page        Documents  Collaboration agreement
    Then the user should not see the element    jQuery = label:contains("Upload")
    And the user clicks the button/link         link = Return to documents
    When the user clicks the button/link        link = Exploitation plan
    Then the user should not see the element    jQuery = label:contains("Upload")

After rejection PM can upload both documents when both documents are removed
    [Documentation]    INFUND-3011
    [Tags]  HappyPath
    [Setup]    log in as a different user    &{lead_applicant_credentials_bd}
    Given PM uploads the project documents   ${Grade_Crossing_Project_Id}

After rejection, mandatory document submission
    [Documentation]    INFUND-3011, INFUND-6152, INFUND-7342
    [Tags]  HappyPath
    [Setup]    log in as a different user   &{lead_applicant_credentials_bd}
    # This ticket assumes that Project_details suite has set as PM the 'test twenty'
    When the user navigates to the page     ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user clicks the button/link     link = Documents
    And the user reloads the page
    Then PM submits both documents          ${Grade_Crossing_Project_Id}

Stakeholder is unable to view the documents before approval
    [Documentation]  IFS-6806
    Given log in as a different user   &{stakeholder_user}
    Then the user navigates to the page and gets a custom error message    ${server}/project-setup/project/${Grade_Crossing_Project_Id}/document/all   ${403_error_message}

ifsAdmin approves both documents
    [Documentation]    INFUND-4621, INFUND-5507, INFUND-7345  IFS-9579
    [Tags]  HappyPath
    [Setup]    Log in as a different user             &{ifs_admin_user_credentials}
    Given the user navigates to the page              ${SERVER}/project-setup-management/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link              link = Collaboration agreement
    Then internal user approve uploaded documents
    When the user goes to documents page              Return to documents  Exploitation plan
    Then internal user approve uploaded documents

MO can view ifsAdmin approved the document banners
    [Documentation]  IFS-9578  IFS-9701
    Given Log in as a different user                           &{monitoring_officer_one_credentials}
    When the user navigates to the page                        ${SERVER}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    Then the user sees Innovate Uk approved document banner

Partners can see the documents approved
    [Documentation]    INFUND-5559, INFUND-5424, INFUND-7345
    [Tags]  HappyPath
     When log in as a different user         &{lead_applicant_credentials_bd}  #Project Manager and Lead
     Then Partners can see both documents approved
     When log in as a different user         &{collaborator1_credentials_bd}
     Then Partners can see both documents approved
     When log in as a different user         &{collaborator2_credentials_bd}
     Then Partners can see both documents approved

Stakeholders can see approved documents
    [Documentation]  IFS-6806
    [Setup]  log in as a different user   &{stakeholder_user}
    Given the user navigates to the page  ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/document/all
    When the user clicks the button/link  link = Collaboration agreement
    And open pdf link                     link = ${valid_pdf} (opens in a new window)

CompAdmin can see Project status updated
    [Documentation]    INFUND-2610
    [Tags]  HappyPath
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the competition
    Then the user should see the element    jQuery = tr:nth-child(4):contains("${Grade_Crossing_Application_Title}")
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(3) > a

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049 , INFUND-5543
    [Tags]
    Given the user should see the element    css = #table-project-status tr:nth-of-type(4) td:nth-of-type(1)
    And the user should see the element      css = #table-project-status tr:nth-of-type(4) td:nth-of-type(2)
    And the user should see the element      css = #table-project-status tr:nth-of-type(4) td:nth-of-type(3).status.ok
    And the user should see the element      css = #table-project-status tr:nth-of-type(4) td:nth-of-type(4)
    And the user should see the element      css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(3) > a

# Project used for sole applicant tests - High-speed rail and its effects on soil compaction -
# has lead org type of Public sector, charity or non Je-S registered research organisation
Sole applicant uploads only exploitation plan and submits
    [Documentation]  IFS-1864
    [Tags]  HappyPath
    [Setup]  log in as a different user         ${USER_BECKY_ORG_PUBSECTOR}  ${short_password}
    Given the user navigates to the page        ${server}/project-setup/project/${PROJ_WITH_SOLE_APPLICANT}
    And the user clicks the button/link         link = Documents
    Then the user should not see the element    link = Collaboration agreement
    When the user clicks the button/link        link = Exploitation plan
    Then the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    And the user clicks the button/link         id = submit-document-button
    And the user clicks the button/link         id = submitDocumentButtonConfirm
    When the user goes to documents page        Return to documents  Set up your project
    Then the user should see the element        jQuery = li:contains("Documents") span:contains("Awaiting review")

IfsAdmin sees uploaded file and approves it
    [Documentation]    IFS-1864  IFS-9579
    [Tags]
    [Setup]    Log in as a different user       &{ifs_admin_user_credentials}
    Given the user navigates to the page        ${server}/project-setup-management/project/${PROJ_WITH_SOLE_APPLICANT}/document/all
    Then the user should not see the element    link = Collaboration agreement
    And the user clicks the button/link         link = Exploitation plan
    When open pdf link                          jQuery = a:contains("${valid_pdf} (opens in a new window)")
    And internal user approve uploaded documents

Sole applicant can see documents approval
    [Documentation]    IFS-1864
    [Tags]
    [Setup]  log in as a different user    ${USER_BECKY_ORG_PUBSECTOR}  ${short_password}
    When the user navigates to the page    ${server}/project-setup/project/${PROJ_WITH_SOLE_APPLICANT}
    Then the user should see the element   jQuery = li:contains("Documents") span:contains("Completed")
    When the user goes to documents page   Documents  Exploitation plan
    Then the user should see the element   jQuery = .success-alert p:contains("Innovate UK approved this document on ${today}.")

Assign a MO to the project
    [Documentation]  IFS-9577
    [Setup]  log in as a different user            &{Comp_admin1_credentials}
    Given the user navigates to the page           ${server}/project-setup-management/monitoring-officer/view-all?ktp=false
    When search for MO                             Orvill  Orville Gibbs
    Then the user should see the element           jQuery = span:contains("Assign projects to Monitoring Officer")
    And the internal user assign project to MO     ${MO_DocApproval_application_No}   ${MO_DocApproval_application_Title}

PM uploads documents and the MO receives an email
    [Documentation]    IFS-9575  IFS-10067
    [Setup]    log in as a different user                         ${USER_PM}     ${short_password}
    Given PM uploads and notifies the project documents to MO     ${MO_DocApproval_ProjectID}
    And the user logs out if they are logged in
    And the user reads his email                                  ${MO_EMAIL}     You have a new document to review for project ${MO_DocApproval_application_Title}  ${MO_DocApproval_application_No}

MO rejects the document
    [Documentation]  IFS-9577
    [Setup]  The user logs-in in new browser      &{monitoring_officer_one_credentials}
    Given the user navigates to the page          ${server}/project-setup/project/${MO_DocApproval_ProjectID}/document/all
    When the user clicks the button/link          link = Collaboration agreement
    Then MO reject uploaded documents
    And the user should see the element           jQuery = div:contains("Collaboration agreement") ~ div span:contains("Incomplete")

MO can view the document feedback and access the uploaded files
    [Documentation]   IFS-9965
    When the user clicks the button/link     link = Collaboration agreement
    Then the user should see the element     jQuery = a:contains('testing.pdf')
    And the user should see the element      jQuery = p:contains('Rejected')
    And the user clicks the button/link      jQuery = a:contains("Return to documents")

MO approves the document
    [Documentation]  IFS-9577
    Given the user clicks the button/link     link = Exploitation plan
    Then MO approves uploaded documents
    And the user should see the element       jQuery = span:contains("Approved")

IFS admin can view MO approved and rejected the document banners
    [Documentation]  IFS-9578
    Given Log in as a different user                             &{ifs_admin_user_credentials}
    And the user navigates to the page                           ${server}/project-setup-management/project/${MO_DocApproval_ProjectID}/document/all/
    When the user clicks the button/link                         link = Collaboration agreement
    Then the user sees MO rejected document banner and reason
    And the user clicks the button/link                          link = Exploitation plan
    And the user should see the element                          jQuery = p:contains("Orville Gibbs (monitoring officer) approved this document on ${today}.")

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser  &{collaborator1_credentials_bd}
    ${today}  get today
    set suite variable  ${today}

the user removes and reuploads project files
    log in as a different user             &{lead_applicant_credentials_bd}
    the user navigates to the page         ${SERVER}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    the user clicks the button/link        link = Exploitation plan
    the user clicks the button/link        jQuery = button:contains("Remove")
    Wait Until Page Does Not Contain Without Screenshots    Removing
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user should see the element        jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
    the user clicks the button/link        jQuery = button:contains("Submit"):nth(1)
    the user clicks the button/link        jQuery = .modal-configured-partner-document button:contains("Submit")
    the user goes to documents page        Back to document overview  Collaboration agreement
    the user clicks the button/link        jQuery = button:contains("Remove")
    Wait Until Page Does Not Contain Without Screenshots    Removing
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user should see the element        jQuery = .upload-section:contains("Collaboration agreement") a:contains("${valid_pdf}")
    the user clicks the button/link        jQuery = button:contains("Submit"):nth(1)
    the user clicks the button/link        jQuery = .modal-configured-partner-document button:contains("Submit")
    the user should not see an error in the page

partners can see rejected documents due to new organisation
    log in as a different user                &{lead_applicant_credentials_bd}
    Partners can see both documents rejected  ${newOrgRejectedDocumentMessagePM}
    log in as a different user                &{collaborator1_credentials_bd}
    Partners can see both documents rejected  ${newOrgRejectedDocumentMessagePartner}
    log in as a different user                &{collaborator2_credentials_bd}
    Partners can see both documents rejected  ${newOrgRejectedDocumentMessagePartner}

ifsadmin approves all documents
    the user navigates to the page        ${SERVER}/project-setup-management/project/${Grade_Crossing_Project_Id}/document/all
    the user clicks the button/link        link = Collaboration agreement
    ifs admin approves uploaded documents
    the user goes to documents page        Return to documents  Exploitation plan
    ifs admin approves uploaded documents
    the user navigates to the page        ${SERVER}/project-setup-management/competition/${PS_Competition_Id}/project/${Grade_Crossing_Project_Id}/team

the user navigates to the competition
    the user navigates to the page      ${server}/project-setup-management/competition/${PS_Competition_Id}/status/all

ifs admin reject uploaded documents
    the user selects the radio button           approved   false
    the user enters text to a text field        id = document-reject-reason   Rejected
    the user clicks the button/link             id = submit-button
    the user clicks the button/link             jQuery = .modal-reject-configured-doc button:contains("Cancel")
    the user should not see an error in the page
    the user clicks the button/link             id = submit-button
    the user clicks the button/link             id = reject-document
    the user should see the element             jQuery = p:contains("You have rejected this document.")

ifs admin approves uploaded documents
    the user selects the radio button           approved   true
    the user clicks the button/link             id = submit-button
    the user clicks the button/link             id = accept-document

Partners can see both documents rejected
    [Arguments]  ${warningMessage}
    the user navigates to the page         ${SERVER}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    the user clicks the button/link        link = Collaboration agreement
    the user should see the element        jQuery = h2:contains(${warningMessage})
    the user should not see the element    jQuery = label:contains("Upload")
    the user clicks the button/link        link = Back to document overview
    the user clicks the button/link        link = Exploitation plan
    the user should see the element        jQuery = h2:contains(${warningMessage})
    the user should not see the element    jQuery = label:contains("Upload")

Partners can see both documents approved
    the user navigates to the page      ${SERVER}/project-setup/project/${Grade_Crossing_Project_Id}/document/all
    the user clicks the button/link     link = Collaboration agreement
    the user should see the element     jQuery = .success-alert p:contains("Innovate UK approved this document on ${today}.")
    the user clicks the button/link     link = Return to documents
    the user clicks the button/link     link = Exploitation plan
    the user should see the element     jQuery = .success-alert p:contains("Innovate UK approved this document on ${today}.")

partners can not remove the documents
    the user should not see the element       name = deleteDocument      #Exploitation plan remove CTA
    the user goes to documents page           Return to documents  Collaboration agreement
    the user should not see the element       name = deleteDocument     #Collaboration agreement remove CTA

PM uploads and notifies the project documents to MO
    [Arguments]  ${compName}
    the user navigates to the page                                       ${SERVER}/project-setup/project/${compName}/document/all
    the user clicks the button/link                                      link = Exploitation plan
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user should see the element                                      jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
    the user clicks the button/link                                      id = submit-document-button
    the user clicks the button/link                                      id = submitDocumentButtonConfirm
    the user goes to documents page                                      Back to document overview  Collaboration agreement
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user should see the element                                      jQuery = .upload-section:contains("Collaboration agreement") a:contains("${valid_pdf}")
    the user clicks the button/link                                      id = submit-document-button
    the user clicks the button/link                                      id = submitDocumentButtonConfirm

MO reject uploaded documents
    the user selects the radio button       approved   false
    the user enters text to a text field    id = document-reject-reason   Rejected
    the user clicks the button/link         id = submit-button
    the user clicks the button/link         jQuery = .modal-reject-configured-doc button:contains("Cancel")
    the user clicks the button/link         id = submit-button
    the user clicks the button/link         id = reject-document
    the user should see the element         jQuery = p:contains("You have rejected this document.")
    the user clicks the button/link         jQuery = a:contains("Return to documents")

MO approves uploaded documents
    the user selects the radio button     approved   true
    the user clicks the button/link       id = submit-button
    the user clicks the button/link       id = accept-document
    the user should see the element       jQuery = p:contains("You approved this document on ${today}.")
    the user clicks the button/link       jQuery = a:contains("Return to documents")

the user cannot approve the document
    [Arguments]    ${RADIO_BUTTON}    ${RADIO_BUTTON_OPTION}
    the user should not see the element     css=[name^="${RADIO_BUTTON}"][value="${RADIO_BUTTON_OPTION}"] ~ label, [id="${RADIO_BUTTON_OPTION}"] ~ label

the user sees MO rejected document banner and reason
    the user should see the element    jQuery = p:contains("Orville Gibbs (monitoring officer) rejected this document.")
    the user should see the element    jQuery = h3:contains("Reason for rejection") ~ p:contains("Rejected")
    the user clicks the button/link    jQuery = a:contains("Return to documents")

the user sees Innovate Uk approved document banner
    the user clicks the button/link    link = Collaboration agreement
    the user should see the element    jQuery = p:contains("Innovate UK approved this document on ${today}.")
    the user clicks the button/link    jQuery = a:contains("Return to documents")
    the user clicks the button/link    link = Exploitation plan
    the user should see the element    jQuery = p:contains("Innovate UK approved this document on ${today}.")

the MO navigates to page
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    the user navigates to the page    ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    Run Keyword if  '${status}' == 'FAIL'   Assign a MO to the project and login as MO
    the user navigates to the page          ${server}/project-setup/project/${Grade_Crossing_Project_Id}

Assign a MO to the project and login as MO
    log in as a different user                        &{Comp_admin1_credentials}
    the user navigates to the page                    ${server}/project-setup-management/monitoring-officer/view-all?ktp=false
    search for MO                                     Orvill  Orville Gibbs
    the user should see the element                   jQuery = span:contains("Assign projects to Monitoring Officer")
    the element should be disabled                    jQuery = button:contains("Assign")
    wait until keyword succeeds without screenshots   10s    200ms   input text         id = projectId   ${Grade_Crossing_Applicaiton_No} - ${Grade_Crossing_Application_Title}
    Execute Javascript                                document.evaluate("//li[text()='${Grade_Crossing_Applicaiton_No} - ${Grade_Crossing_Application_Title}']",document.body,null,9,null).singleNodeValue.click();
    the user clicks the button/link                   jQuery = button:contains("Assign")
    log in as a different user                        &{monitoring_officer_one_credentials}