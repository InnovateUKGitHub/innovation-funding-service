*** Settings ***
Documentation     INFUND-3011 As a lead partner I need to provide mandatory documents so that they can be reviewed by all partners before submitting to Innovate UK
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
Suite Setup       the project is completed if it is not already complete
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot

*** Variables ***

*** Test Cases ***

Lead partner cannot upload exploitation plan
    [Documentation]    INFUND-3011, INFUND-5490, IFS-1864
    [Tags]
    [Setup]    log in as a different user   &{lead_applicant2_credentials}
    Given the user navigates to the page    ${project_in_setup2_page}
    Then the user should see the element    css=.progress-list ul > li.waiting:nth-of-type(7)
    And The user should see the text in the page    Your Project Manager needs to upload the following
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Upload
    And the user should see the text in the page   Only the Project Manager can upload and submit the exploitation plan

PM cannot submit when exploitation plan is not uploaded
    [Documentation]    INFUND-3012, INFUND-5490, IFS-1864
    [Tags]
    Given log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    When the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    And the user should see the text in the page   Only the Project Manager can upload and submit the exploitation plan
    #Then the user should see the 1 Upload button
    And the user should see the element    css=label[for="exploitationPlan"]
    Then the user should not see the element    jQuery=.button.enabled:contains("Submit document")

Large pdf not allowed for exploitation plan
    [Documentation]    INFUND-3011, IFS-1864
    [Tags]
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the exploitation plan    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    And the user should not see the text in the page    ${too_large_pdf}
    And the user goes back to the previous page

Non pdf files not allowed for exploitation plan
    [Documentation]    INFUND-3011, IFS-1864
    When the user uploads to the exploitation plan    ${text_file}
    Then the user should see an error    ${wrong_filetype_validation_error}
    And the user should not see the text in the page    ${text_file}

PM can upload exploitation plan
    [Documentation]    INFUND-3011, IFS-1864
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the exploitation plan    ${valid_pdf}
    Then the user should not see an error in the page

Lead partner can view exploitation plan
    [Documentation]    INFUND-3011, INFUND-2621, IFS-1864
    [Tags]
    Given log in as a different user       &{lead_applicant2_credentials}
    When the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user opens the link in new window   ${valid_pdf}
    Then the user goes back to the previous tab
    And the user navigates to the page    ${project_in_setup2_page}
    And the user should see the element    link=status of my partners
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)
    [Teardown]    the user navigates to the page    ${project_in_setup2_page}

Lead partner does not have the option to submit the mandatory exploitation plan
    [Documentation]    INFUND-3011, IFS-1864
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup2_page}/partner/documents
    When the user should not see an error in the page
    And the user should not see the element    jQuery=.button.enabled:contains("Submit document")

Lead partner cannot remove exploitation plan
    [Documentation]    INFUND-3011, IFS-1864
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeExploitationPlanClicked

PM can view exploitation plan
    [Documentation]    INFUND-3011, INFUND-2621, IFS-1864
    [Tags]
    Given log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    And the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user goes back to the previous tab
    And the user navigates to the page    ${project_in_setup2_page}
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)
    And the user goes back to the previous page

PM can remove the exploitation plan
    [Documentation]    INFUND-3011, IFS-1864
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup2_page}/partner/documents
    When the user clicks the button/link    name=removeExploitationPlanClicked
    Then the user should not see an error in the page

PM can upload exploitation plan after it has been removed
    [Documentation]    INFUND-3011, IFS-1864
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the exploitation plan    ${valid_pdf}
    Then the user should not see an error in the page

Status in the dashboard remains action required after upload
    [Documentation]    INFUND-3011, IFS-1864
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup2_page}
    Then the user should not see the element    css=ul li.complete:nth-child(7)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)

Mandatory document submission
    [Documentation]    INFUND-3011, INFUND-6152, INFUND-6139, IFS-1864
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    # This ticket assumes that Project_details suite has set as PM the 'test twenty'
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    And the user reloads the page
    When the user clicks the button/link    jQuery=.button:contains("Submit document")
    And the user clicks the button/link    jQuery=button:contains("Cancel")
    Then the user should see the element    name=removeExploitationPlanClicked    # testing here that the section has not become read-only
    When the user clicks the button/link    jQuery=.button:contains("Submit document")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    When the user clicks the button/link    link=Project setup status
    Then the user should be redirected to the correct page    ${project_in_setup2_page}
    And the user should see the element    css=ul li.waiting:nth-child(7)
    When the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=status of my partners
    And the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(6)
    And the user goes back to the previous page

PM can still view exploitation plan after submitting
    [Documentation]    INFUND-3012, IFS-1864
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user goes back to the previous tab

PM cannot remove the exploitation plan after submitting
    [Documentation]    INFUND-3012, IFS-1864
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeExploitationPlanClicked

Lead partner cannot remove the exploitation plan after submission by PM
    [Documentation]    INFUND-3012, IFS-1864
    [Setup]    log in as a different user  &{lead_applicant2_credentials}
    Given the user navigates to the page    ${project_in_setup2_page}
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeExploitationPlanClicked

Lead partner can still view exploitation plan after submitting
    [Documentation]    INFUND-3012, IFS-1864
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user goes back to the previous tab

Non-lead partner can still view exploitation plan after submitting
    [Documentation]    INFUND-3012 , INFUND-4428, INFUND-6139, IFS-1864
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user goes back to the previous tab
    When the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=status of my partners
    And the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(6)

CompAdmin can see uploaded file
    [Documentation]    INFUND-4621, IFS-1864
    [Tags]    HappyPath
    [Setup]    Log in as a different user  &{Comp_admin1_credentials}
    When the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link    link=${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see the element    jQuery=h2:contains("Projects in setup")
    When the user clicks the button/link    css=#table-project-status tr:nth-child(3) td:nth-child(7) a
    Then the user should see the text in the page    Exploitation plan
    When the user clicks the button/link    css=.uploaded-file:nth-of-type(1)
    Then the user should see the file without error

CompAdmin rejects exploitation plan
    [Documentation]    INFUND-4620, IFS-1864
    [Tags]    HappyPath
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_2_PROJECT}/partner/documents
    And the user should see the text in the page    Other documents
    When the user clicks the button/link    jQuery=button:contains("Reject document")
    And the user clicks the button/link    jQuery=.modal-reject-doc button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Reject document")
    And the user clicks the button/link    jQuery=.modal-reject-doc .button:contains("Reject document")
    Then the user should see the text in the page    This document has been reviewed and rejected. We have returned it to the Project Manager.

Partner can see the exploitation plan rejected
    [Documentation]    INFUND-5559, INFUND-5424, INFUND-7342, IFS-218, IFS-1864
    [Tags]
    Given log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}  #Project Manager
    And the user navigates to the page    ${project_in_setup2_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve your document and have returned it to you. A member of Innovate UK will be in touch to discuss our requirements.")
    And the user should see the element  jQuery=.button:contains("Submit document")
    Given log in as a different user    &{lead_applicant2_credentials}
    And the user navigates to the page    ${project_in_setup2_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve your document and have returned it to you. A member of Innovate UK will be in touch to discuss our requirements.")
    And the user should not see the element  jQuery=.button:contains("Submit document")

After rejection, lead partner cannot remove exploitation plan
    [Documentation]    INFUND-3011, INFUND-7342, IFS-1864
    [Tags]
    [Setup]    log in as a different user   &{lead_applicant2_credentials}
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeExploitationPlanClicked

After rejection, lead partner cannot upload exploitation plan
    [Documentation]    INFUND-3011, INFUND-7342, IFS-1864
    [Tags]    HappyPath
    [Setup]    log in as a different user   &{lead_applicant2_credentials}
    Given the user navigates to the page    ${project_in_setup2_page}
    Then the user should see the element    css=.progress-list ul > li.waiting:nth-of-type(7)
    And The user should see the text in the page    Your Project Manager needs to upload the following
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Upload

After rejection, lead partner can view exploitation plan
    [Documentation]    INFUND-3011, INFUND-2621, INFUND-7342, IFS-1864
    [Tags]
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf} (opens in a new window)
    Then the user goes back to the previous tab
    And the user navigates to the page    ${project_in_setup2_page}
    And the user should see the element    link=status of my partners
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)
    [Teardown]    the user navigates to the page    ${project_in_setup2_page}

After rejection, lead partner does not have the option to submit the exploitation plan
    [Documentation]    INFUND-3011, INFUND-7342, IFS-1864
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup2_page}/partner/documents
    When the user should not see an error in the page
    And the user should not see the element    jQuery=.button.enabled:contains("Submit document")

After rejection, status in the dashboard remains action required after upload
    [Documentation]    INFUND-3011, INFUND-7342, IFS-1864
    [Tags]    HappyPath
    When the user clicks the button/link    link=Project setup status
    Then the user should not see the element    css=ul li.complete:nth-child(7)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)

Project Manager can remove the offending exploitation plan
    [Documentation]    INFUND-7342, IFS-1864
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup2_page}/partner/documents
    When the user clicks the button/link    name=removeExploitationPlanClicked
    Then the user should not see the text in the page    ${valid_pdf}
    And the user should not see the element    jQuery=.button.enabled:contains("Submit document")

After rejection, non pdf files not allowed for the exploitation plan
    [Documentation]    INFUND-3011, INFUND-7342
    [Tags]
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the exploitation plan    ${text_file}
    Then the user should see an error    ${wrong_filetype_validation_error}
    And the user should not see the text in the page    ${text_file}

After rejection, large pdfs not allowed for the exploitation plan
    [Documentation]    INFUND-3011, INFUND-7342, IFS-1864
    [Tags]
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the exploitation plan    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    And the user should not see the text in the page    ${too_large_pdf}
    And the user goes back to the previous page

After rejection, PM cannot submit when the exploitation plan is removed
    [Documentation]    INFUND-3012, INFUND-7342, IFS-1864
    [Tags]
    Given log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    When the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    #Then the user should see the 1 Upload button
    And the user should see the element    css=label[for="exploitationPlan"]
    Then the user should not see the element    jQuery=.button.enabled:contains("Submit document")

After rejection PM can upload exploitation plan when exploitation plan is removed
    [Documentation]    INFUND-3011, IFS-1864
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the exploitation plan    ${valid_pdf}
    Then the user should not see an error in the page

After rejection, exploitation submission
    [Documentation]    INFUND-3011, INFUND-6152, INFUND-7342, IFS-1864
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}
    Given the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=Other documents
    And the user reloads the page
    When the user clicks the button/link    jQuery=.button:contains("Submit document")
    And the user clicks the button/link    jQuery=button:contains("Cancel")
    Then the user should see the element    name=removeExploitationPlanClicked    # testing here that the section has not become read-only
    When the user clicks the button/link    jQuery=.button:contains("Submit document")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    When the user clicks the button/link    link=Project setup status
    Then the user should be redirected to the correct page    ${project_in_setup2_page}
    And the user should see the element    css=ul li.waiting:nth-child(7)
    When the user navigates to the page    ${project_in_setup2_page}
    And the user clicks the button/link    link=status of my partners

Project Finance is able to Approve and Reject
    [Documentation]    INFUND-4621, INFUND-5440, INFUND-7345, IFS-1864
    [Tags]
    [Setup]    Log in as a different user   &{internal_finance_credentials}
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_2_PROJECT}/partner/documents
    Then the user should see the text in the page    Other documents
    And the user should see the element    jQuery=button:contains("Accept document")
    And the user should see the element    jQuery=button:contains("Reject document")
    When the user clicks the button/link    jQuery=button:contains("Accept document")
    And the user clicks the button/link    jQuery=.modal-accept-doc button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Reject document")
    And the user clicks the button/link    jQuery=.modal-reject-doc button:contains("Cancel")
    Then the user should not see an error in the page

Project Finance user can click the link and go back to the Competition Dashboard page
    [Documentation]    INFUND-5516, INFUND-7345, IFS-1864
    [Tags]
    When the user clicks the button/link           link=Projects in setup
    Then the user should not see an error in the page
    And the user should see the text in the page   Projects in setup
    [Teardown]    the user goes back to the previous page

CompAdmin approves other documents
    [Documentation]    INFUND-4621, INFUND-5507, INFUND-7345, IFS-1864
    [Tags]    HappyPath
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_2_PROJECT}/partner/documents
    And the user should see the text in the page    Other documents
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_2_LEAD_ORGANISATION_NAME}
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}
    Then the user should see the element    jQuery=button:contains("Accept document")
    And the user should see the element    jQuery=button:contains("Reject document")
    When the user clicks the button/link    jQuery=button:contains("Accept document")
    And the user clicks the button/link    jQuery=.modal-accept-doc button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Accept document")
    And the user clicks the button/link    jQuery=.modal-accept-doc .button:contains("Accept document")
    Then the user should see the text in the page    The document provided has been approved.

Partner can see the exploitation plan approved
    [Documentation]    INFUND-5559, INFUND-5424, INFUND-7345, IFS-1864
    [Tags]    HappyPath
    Given log in as a different user      ${PROJECT_SETUP_APPLICATION_2_PM_EMAIL}  ${short_password}  #Project Manager
    And the user navigates to the page    ${project_in_setup2_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("This document has been approved by Innovate UK.")
    Given log in as a different user      &{lead_applicant2_credentials}
    And the user navigates to the page    ${project_in_setup2_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("This document has been approved by Innovate UK.")

CompAdmin can see Project status updated
    [Documentation]    INFUND-2610, IFS-1864
    [Tags]    HappyPath
    [Setup]    Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link    link=${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see the element    jQuery=tr:nth-child(3):contains("${PROJECT_SETUP_APPLICATION_2_TITLE}")
    And the user should see the element    css=#table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(6)

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049, INFUND-5543, IFS-1864
    [Tags]    Experian    HappyPath
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    When the user navigates to the page    ${internal_competition_status}
    Then the user should see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(1).status.ok
    And the user should see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(2).status.ok
    And the user should see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(3).status
    And the user should see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(4).status.action
    And the user should see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(6).status.ok

*** Keywords ***

the project is completed if it is not already complete
    The user logs-in in new browser  &{lead_applicant2_credentials}
    the user navigates to the page   ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_2_PROJECT}/details
    ${project_manager_not_set}  ${value} =  run keyword and ignore error without screenshots  The user should not see the element  jQuery=#project-manager-status.yes
    run keyword if  '${project_manager_not_set}' == 'PASS'  all previous sections of the project are completed

all previous sections of the project are completed
    project lead submits project details        ${PROJECT_SETUP_APPLICATION_2_PROJECT}
    all partners submit their finance contacts
    project finance submits monitoring officer  ${PROJECT_SETUP_APPLICATION_2_PROJECT}  Grace  Harper  ${test_mailbox_two}+monitoringofficer@gmail.com  08549731414

all partners submit their finance contacts
    the partner submits their finance contact  ${PROJECT_SETUP_APPLICATION_2_LEAD_ORGANISATION_ID}  ${PROJECT_SETUP_APPLICATION_2_PROJECT}  &{lead_applicant2_credentials}

the user uploads to the exploitation plan
    [Arguments]  ${file_name}
    choose file  name=exploitationPlan  ${upload_folder}/${file_name}

the user should see the file without error
    the user should not see an error in the page
    the user goes back to the previous page

partners submit bank details
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_2_LEAD_PARTNER_EMAIL}  ${PROJECT_SETUP_APPLICATION_2_PROJECT}  ${account_one}  ${sortCode_one}
