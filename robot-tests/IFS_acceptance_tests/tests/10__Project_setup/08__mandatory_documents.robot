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
...               INFUND-7342 As a lead partner I want to be able to submit a document in the "Other Documents" section of Project Setup if an earlier document has been rejected so that I can provide an alternative document for review and approval
...
...               INFUND-7345 As an internal user I want to be able to view resubmitted documents in the "Other Documents" section of Project Setup so that they can be reviewed again for approval
...
...               INFUND-5490 document upload non-user
Suite Setup       Log in as user  &{collaborator1_credentials}
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot

*** Variables ***

*** Test Cases ***
Non-lead partner cannot upload either document
    [Documentation]    INFUND-3011, INFUND-2621, INFUND-5258, INFUND-5806, INFUND-5490
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=.progress-list ul > li.waiting:nth-of-type(7)
    And The user should see the text in the page    Your Project Manager will need to upload the following
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Upload
    And the user should see the text in the page   Only the Project Manager can upload and submit additional documents

Lead partner cannot upload either document
    [Documentation]    INFUND-3011, INFUND-5490
    [Tags]
    [Setup]    log in as a different user   &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=.progress-list ul > li.waiting:nth-of-type(7)
    And The user should see the text in the page    Your Project Manager will need to upload the following
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Upload
    And the user should see the text in the page   Only the Project Manager can upload and submit additional documents

PM cannot submit when both documents are not uploaded
    [Documentation]    INFUND-3012, INFUND-5490
    [Tags]
    Given log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    And the user should see the text in the page   Only the Project Manager can upload and submit additional documents
    #Then the user should see the 2 Upload buttons
    And the user should see the element    jQuery=label[for="collaborationAgreement"]
    And the user should see the element    jQuery=label[for="exploitationPlan"]
    Then the user should not see the element    jQuery=.button.enabled:contains("Submit documents")


Large pdfs not allowed for either document
    [Documentation]    INFUND-3011
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the collaboration agreement question    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    And the user goes back to the previous page
    When the user uploads to the exploitation plan question    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    And the user should not see the text in the page    ${too_large_pdf}
    And the user goes back to the previous page

Non pdf files not allowed for either document
    [Documentation]    INFUND-3011
    When the user uploads to the collaboration agreement question    ${text_file}
    Then the user should see an error    ${wrong_filetype_validation_error}
    When the user uploads to the exploitation plan question    ${text_file}
    Then the user should see an error    ${wrong_filetype_validation_error}
    And the user should not see the text in the page    ${text_file}


PM can upload both documents
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the collaboration agreement question    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    When the user uploads to the exploitation plan question    ${valid_pdf}
    Then the user should not see an error in the page

Lead partner can view both documents
    [Documentation]    INFUND-3011, INFUND-2621
    [Tags]
    Given log in as a different user       &{lead_applicant_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    link=status of my partners
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)
    [Teardown]    the user navigates to the page    ${project_in_setup_page}

Lead partner does not have the option to submit the mandatory documents
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup_page}/partner/documents
    When the user should not see an error in the page
    And the user should not see the element    jQuery=.button.enabled:contains("Submit documents")

Lead partner cannot remove either document
    [Documentation]    INFUND-3011
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Non-lead partner can view both documents
    [Documentation]    INFUND-2621, INFUND-3011, INFUND-3013, INFUND-5806 , INFUND-4428
    [Tags]
    Given log in as a different user       &{collaborator1_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user moves focus to the element  jQuery=ul li:nth-child(7)
    And the user should see the element   jQuery=#content ul > li:nth-child(7) .msg-progress
    And the user clicks the button/link    link=Other documents
    And the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)
    And the user goes back to the previous page

Non-lead partner cannot remove or submit right
    [Documentation]    INFUND-3013
    [Tags]
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked
    And the user should not see the element    jQuery=.button.enabled:contains("Submit documents")

PM can view both documents
    [Documentation]    INFUND-3011, INFUND-2621
    [Tags]
    Given log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    And the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)
    And the user goes back to the previous page

PM can remove the second document
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_page}/partner/documents
    When the user clicks the button/link    name=removeExploitationPlanClicked
    Then the user should not see an error in the page

Non-lead partner can still view the first document
    [Documentation]    INFUND-4252
    [Setup]    log in as a different user  &{collaborator1_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    Then the user should see the text in the page    ${valid_pdf}


PM can remove the first document
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    name=removeCollaborationAgreementClicked
    Then the user should not see the text in the page    ${valid_pdf}

Non-lead partner cannot view either document once removed
    [Documentation]    INFUND-4252
    [Setup]    log in as a different user  &{collaborator1_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    ${valid_pdf}

PM can upload both documents after they have been removed
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the collaboration agreement question    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    When the user uploads to the exploitation plan question    ${valid_pdf}
    Then the user should not see an error in the page

Status in the dashboard remains action required after uploads
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_page}
    Then the user should not see the element    jQuery=ul li.complete:nth-child(7)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)

Mandatory document submission
    [Documentation]    INFUND-3011, INFUND-6152, INFUND-6139
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    # This ticket assumes that Project_details suite has set as PM the 'test twenty'
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    And the user reloads the page
    When the user clicks the button/link    jQuery=.button:contains("Submit documents")
    And the user clicks the button/link    jQuery=button:contains("Cancel")
    Then the user should see the element    name=removeExploitationPlanClicked    # testing here that the section has not become read-only
    When the user clicks the button/link    jQuery=.button:contains("Submit documents")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    When the user clicks the button/link    link=Project setup status
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.waiting:nth-child(7)
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=status of my partners
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(6)
    And the user goes back to the previous page

PM can still view both documents after submitting
    [Documentation]    INFUND-3012
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user goes back to the previous page
    Then the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    And the user should not see an error in the page
    And the user goes back to the previous page

PM cannot remove the documents after submitting
    [Documentation]    INFUND-3012
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Lead partner cannot remove the documents after submission by PM
    [Documentation]    INFUND-3012
    [Setup]    log in as a different user  &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Lead partner can still view both documents after submitting
    [Documentation]    INFUND-3012
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user goes back to the previous page
    Then the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    And the user should not see an error in the page
    And the user goes back to the previous page

Non-lead partner cannot remove the documents after submission by PM
    [Documentation]    INFUND-3012
    [Setup]    log in as a different user   &{collaborator1_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Non-lead partner can still view both documents after submitting
    [Documentation]    INFUND-3012 , INFUND-4428, INFUND-6139
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user goes back to the previous page
    Then the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    And the user should not see an error in the page
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=status of my partners
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(6)

CompAdmin can see uploaded files
    [Documentation]    INFUND-4621
    [Tags]    HappyPath
    [Setup]    Log in as a different user  &{Comp_admin1_credentials}
    When the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link    link=${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see the element    jQuery=h2:contains("Projects in setup")
    When the user clicks the button/link    jQuery=#table-project-status tr:nth-child(2) td:nth-child(7) a
    Then the user should see the text in the page    Collaboration agreement
    When the user clicks the button/link    jQuery=.uploaded-file:nth-of-type(1)
    Then the user should see the file without error
    When the user clicks the button/link    jQuery=.uploaded-file:nth-of-type(2)
    Then the user should see the file without error

CompAdmin rejects other documents
    [Documentation]    INFUND-4620
    [Tags]    HappyPath
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/partner/documents
    And the user should see the text in the page    Other documents
    When the user clicks the button/link    jQuery=button:contains("Reject documents")
    And the user clicks the button/link    jQuery=.modal-reject-docs button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Reject documents")
    And the user clicks the button/link    jQuery=.modal-reject-docs .button:contains("Reject Documents")
    Then the user should see the text in the page    These documents have been reviewed and rejected. We have returned them to the Project Manager.


Partners can see the documents rejected
    [Documentation]    INFUND-5559, INFUND-5424, INFUND-7342
    [Tags]
    Given log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}  #Project Manager
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve your documents and have returned them to you. A member of Innovate UK will be in touch to discuss our requirements.")
    Given log in as a different user    &{lead_applicant_credentials}
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve your documents and have returned them to you. A member of Innovate UK will be in touch to discuss our requirements.")
    Given log in as a different user   &{collaborator2_credentials}
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve your documents and have returned them to you. A member of Innovate UK will be in touch to discuss our requirements.")
    Given log in as a different user    &{collaborator1_credentials}
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve your documents and have returned them to you. A member of Innovate UK will be in touch to discuss our requirements.")

After rejection, lead partner cannot remove either document
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]
    [Setup]    log in as a different user   &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

After rejection, lead partner cannot upload either document
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]    HappyPath
    [Setup]    log in as a different user   &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=.progress-list ul > li.waiting:nth-of-type(7)
    And The user should see the text in the page    Your Project Manager will need to upload the following
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Upload

After rejection, lead partner can view both documents
    [Documentation]    INFUND-3011, INFUND-2621, INFUND-7342
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    link=status of my partners
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)
    [Teardown]    the user navigates to the page    ${project_in_setup_page}

After rejection, lead partner does not have the option to submit the mandatory documents
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup_page}/partner/documents
    When the user should not see an error in the page
    And the user should not see the element    jQuery=.button.enabled:contains("Submit documents")

After rejection, non-lead partner cannot view both documents
    [Documentation]    INFUND-2621, INFUND-3011, INFUND-3013, INFUND-5806 , INFUND-4428, INFUND-7342
    [Tags]    HappyPath
    Given log in as a different user       &{collaborator1_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user moves focus to the element  jQuery=ul li:nth-child(7)
    And the user should see the element   jQuery=#content ul > li:nth-child(7) .msg-progress
    And the user clicks the button/link    link=Other documents
    And the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user should not see an error in the page
    And the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)


After rejection, status in the dashboard remains action required after uploads
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]    HappyPath
    When the user clicks the button/link    link=Project setup status
    Then the user should not see the element    jQuery=ul li.complete:nth-child(7)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)

Project Manager can remove the offending documents
    [Documentation]    INFUND-7342
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup_page}/partner/documents
    When the user clicks the button/link    name=removeCollaborationAgreementClicked
    And the user clicks the button/link    name=removeExploitationPlanClicked
    Then the user should not see the text in the page    ${valid_pdf}
    And the user should not see the element    jQuery=.button.enabled:contains("Submit documents")

After rejection, non-lead partner cannot upload either document
    [Documentation]    INFUND-3011, INFUND-2621, INFUND-5258, INFUND-5806, INFUND-7342
    [Tags]
    [Setup]    log in as a different user   &{collaborator1_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=.progress-list ul > li.waiting:nth-of-type(7)
    And The user should see the text in the page    Your Project Manager will need to upload the following
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Upload

After rejection, non pdf files not allowed for either document
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the collaboration agreement question    ${text_file}
    Then the user should see an error    ${wrong_filetype_validation_error}
    When the user uploads to the exploitation plan question    ${text_file}
    Then the user should see an error    ${wrong_filetype_validation_error}
    And the user should not see the text in the page    ${text_file}

After rejection, large pdfs not allowed for either document
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the collaboration agreement question    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    And the user goes back to the previous page
    When the user uploads to the exploitation plan question    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    And the user should not see the text in the page    ${too_large_pdf}
    And the user goes back to the previous page

After rejection, PM cannot submit when both documents are removed
    [Documentation]    INFUND-3012, INFUND-7342
    [Tags]
    Given log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    #Then the user should see the 2 Upload buttons
    And the user should see the element    jQuery=label[for="collaborationAgreement"]
    And the user should see the element    jQuery=label[for="exploitationPlan"]
    Then the user should not see the element    jQuery=.button.enabled:contains("Submit documents")


After rejection PM can upload both documents when both documents are removed
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the collaboration agreement question    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    When the user uploads to the exploitation plan question    ${valid_pdf}
    Then the user should not see an error in the page

After rejection, mandatory document submission
    [Documentation]    INFUND-3011, INFUND-6152, INFUND-7342
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}
    # This ticket assumes that Project_details suite has set as PM the 'test twenty'
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    And the user reloads the page
    When the user clicks the button/link    jQuery=.button:contains("Submit documents")
    And the user clicks the button/link    jQuery=button:contains("Cancel")
    Then the user should see the element    name=removeExploitationPlanClicked    # testing here that the section has not become read-only
    When the user clicks the button/link    jQuery=.button:contains("Submit documents")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    When the user clicks the button/link    link=Project setup status
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.waiting:nth-child(7)
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=status of my partners


Project Finance is able to Approve and Reject
    [Documentation]    INFUND-4621, INFUND-5440, INFUND-7345
    [Tags]
    [Setup]    Log in as a different user   &{internal_finance_credentials}
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/partner/documents
    Then the user should see the text in the page    Other documents
    And the user should see the element    jQuery=button:contains("Accept documents")
    And the user should see the element    jQuery=button:contains("Reject documents")
    When the user clicks the button/link    jQuery=button:contains("Accept documents")
    And the user clicks the button/link    jQuery=.modal-accept-docs button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Reject documents")
    And the user clicks the button/link    jQuery=.modal-reject-docs button:contains("Cancel")
    Then the user should not see an error in the page


Project Finance user can click the link and go back to the Competition Dashboard page
    [Documentation]    INFUND-5516, INFUND-7345
    [Tags]
    When the user clicks the button/link           link=Projects in setup
    Then the user should not see an error in the page
    And the user should see the text in the page   Projects in setup
    [Teardown]    the user goes back to the previous page


CompAdmin approves other documents
    [Documentation]    INFUND-4621, INFUND-5507, INFUND-7345
    [Tags]    HappyPath
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/partner/documents
    And the user should see the text in the page    Other documents
    And the user should see the text in the page    Empire Ltd
    And the user should see the text in the page    Ludlow
    And the user should see the text in the page    EGGS
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}
    Then the user should see the element    jQuery=button:contains("Accept documents")
    And the user should see the element    jQuery=button:contains("Reject documents")
    When the user clicks the button/link    jQuery=button:contains("Accept documents")
    And the user clicks the button/link    jQuery=.modal-accept-docs button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Accept documents")
    And the user clicks the button/link    jQuery=.modal-accept-docs .button:contains("Accept Documents")
    Then the user should see the text in the page    The documents provided have been approved.
    [Teardown]  the user clicks the button/link      link=Projects in setup


Partners can see the documents approved
    [Documentation]    INFUND-5559, INFUND-5424, INFUND-7345
    [Tags]    HappyPath
    Given log in as user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}  ${short_password}  #Project Manager
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("These documents have been approved by Innovate UK")
    Given log in as a different user      &{lead_applicant_credentials}
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("These documents have been approved by Innovate UK")
    Given log in as a different user   &{collaborator2_credentials}
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("These documents have been approved by Innovate UK")
    Given log in as a different user    &{collaborator1_credentials}
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("These documents have been approved by Innovate UK")

CompAdmin can see Project status updated
    [Documentation]    INFUND-2610
    [Tags]    HappyPath
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link    link=${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see the element    jQuery=tr:nth-child(2):contains("${PROJECT_SETUP_APPLICATION_1_TITLE}")
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(6)


Status updates correctly for internal user's table
    [Documentation]    INFUND-4049 , INFUND-5543
    [Tags]    Experian    HappyPath
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    When the user navigates to the page    ${internal_project_summary}
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(1).status.ok
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(2).status.ok
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(3).status
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(4).status.action
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(6).status.ok

*** Keywords ***
the user uploads to the collaboration agreement question
    [Arguments]    ${file_name}
    choose file    name=collaborationAgreement    ${upload_folder}/${file_name}

the user uploads to the exploitation plan question
    [Arguments]    ${file_name}
    choose file    name=exploitationPlan    ${upload_folder}/${file_name}

the user should see the file without error
    the user should not see an error in the page
    the user goes back to the previous page
