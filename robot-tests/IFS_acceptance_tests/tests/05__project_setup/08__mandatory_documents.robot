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
Suite Setup       Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot

*** Variables ***

*** Test Cases ***
Non-lead partner cannot upload either document
    [Documentation]    INFUND-3011, INFUND-2621, INFUND-5258, INFUND-5806
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=.ifs-progress-list > li.waiting:nth-of-type(7)
    And The user should see the text in the page    The lead partner of the consortium will need to upload the following documents
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Upload


PM cannot submit when both documents are not uploaded
    [Documentation]    INFUND-3012
    [Tags]
    Given log in as a different user    worth.email.test+projectlead@gmail.com    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    #Then the user should see the 2 Upload buttons
    And the user should see the element    jQuery=label[for="collaborationAgreement"]
    And the user should see the element    jQuery=label[for="exploitationPlan"]
    Then the user should not see the element    jQuery=.button.enabled:contains("Submit partner documents")


Large pdfs not allowed for either document
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    log in as a different user    steve.smith@empire.com    Passw0rd
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

Lead partner cannot remove either document
    [Documentation]    INFUND-3011
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Lead partner can upload both documents
    [Documentation]    INFUND-3011
    [Tags]
    When the user uploads to the collaboration agreement question    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    When the user uploads to the exploitation plan question    ${valid_pdf}
    Then the user should not see an error in the page

Lead partner can view both documents
    [Documentation]    INFUND-3011, INFUND-2621
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    link=What's the status of each of my partners?
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(6)
    [Teardown]    the user navigates to the page    ${project_in_setup_page}

Lead partner does not have the option to submit the mandatory documents
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup_page}/partner/documents
    When the user should not see an error in the page
    And the user should not see the element    jQuery=.button.enabled:contains("Submit partner documents")

Non-lead partner can view both documents
    [Documentation]    INFUND-2621, INFUND-3011, INFUND-3013, INFUND-5806
    [Tags]
    Given log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    Then the user moves focus to the element  jQuery=ul li:nth-child(7)
    And the user should see the element   jQuery=#content > ul > li:nth-child(7) > div.progress-status
    And the user clicks the button/link    link=Other documents
    And the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(6)
    And the user goes back to the previous page

Non-lead partner cannot remove or submit right
    [Documentation]    INFUND-3013
    [Tags]
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked
    And the user should not see the element    jQuery=.button.enabled:contains("Submit partner documents")

PM can view both documents
    [Documentation]    INFUND-3011, INFUND-2621
    [Tags]
    Given log in as a different user    worth.email.test+projectlead@gmail.com    Passw0rd
    And the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(6)
    And the user goes back to the previous page

PM can remove the second document
    [Documentation]    INFUND-3011
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}/partner/documents
    When the user clicks the button/link    name=removeExploitationPlanClicked
    Then the user should not see an error in the page

Non-lead partner can still view the first document
    [Documentation]    INFUND-4252
    [Setup]    log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    Then the user should see the text in the page    ${valid_pdf}


PM can remove the first document
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    log in as a different user    worth.email.test+projectlead@gmail.com    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    name=removeCollaborationAgreementClicked
    Then the user should not see the text in the page    ${valid_pdf}

Non-lead partner cannot view either document once removed
    [Documentation]    INFUND-4252
    [Setup]    log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    ${valid_pdf}


PM can upload both documents
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    [Setup]    log in as a different user    worth.email.test+projectlead@gmail.com    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the collaboration agreement question    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    When the user uploads to the exploitation plan question    ${valid_pdf}
    Then the user should not see an error in the page

Status in the dashboard remains pending after uploads
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    When the user clicks the button/link    link=Project setup status
    Then the user should not see the element    jQuery=ul li.complete:nth-child(7)
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(6)

Mandatory document submission
    [Documentation]    INFUND-3011
    [Tags]    HappyPath
    [Setup]    log in as a different user    worth.email.test+projectlead@gmail.com    Passw0rd
    # This ticket assumes that Project_details suite has set as PM the 'test twenty'
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    And the user reloads the page
    When the user clicks the button/link    jQuery=.button:contains("Submit partner documents")
    And the user clicks the button/link    jQuery=.button:contains("Cancel")
    Then the user should see the element    name=removeExploitationPlanClicked    # testing here that the section has not become read-only
    When the user clicks the button/link    jQuery=.button:contains("Submit partner documents")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    When the user clicks the button/link    link=Project setup status
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.waiting:nth-child(7)
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=What's the status of each of my partners?
    And the user goes back to the previous page

PM can still view both documents after submitting
    [Documentation]    INFUND-3012
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    Then the user clicks the button/link    link=${valid_pdf}
    And the user should not see an error in the page
    And the user goes back to the previous page

PM cannot remove the documents after submitting
    [Documentation]    INFUND-3012
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Lead partner cannot remove the documents after submission by PM
    [Documentation]    INFUND-3012
    [Setup]    log in as a different user    steve.smith@empire.com    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Lead partner can still view both documents after submitting
    [Documentation]    INFUND-3012
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    Then the user clicks the button/link    link=${valid_pdf}
    And the user should not see an error in the page
    And the user goes back to the previous page

Non-lead partner cannot remove the documents after submission by PM
    [Documentation]    INFUND-3012
    [Setup]    log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Non-lead partner can still view both documents after submitting
    [Documentation]    INFUND-3012
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    Then the user clicks the button/link    link=${valid_pdf}
    And the user should not see an error in the page
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=What's the status of each of my partners?



CompAdmin can see uploaded files
    [Documentation]    INFUND-4621
    [Tags]    HappyPath
    [Setup]    Log in as a different user    john.doe@innovateuk.test    Passw0rd
    When the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link    link=Killer Riffs
    Then the user should see the element    jQuery=h2:contains("Projects in setup")
    # Comp Admin should see the element as action needed instead of done TODO-INFUND-5601
    When the user clicks the button/link    jQuery=#table-project-status tr:nth-child(1) td:nth-child(7) a
    Then the user should see the text in the page    Collaboration Agreement
    When the user clicks the button/link    jQuery=.uploaded-file:nth-of-type(1)
    Then the user should see the file without error
    When the user clicks the button/link    jQuery=.uploaded-file:nth-of-type(2)
    Then the user should see the file without error


CompAdmin rejects other documents
    [Documentation]    INFUND-4620
    [Tags]    HappyPath
    [Setup]    Log in as a different user    john.doe@innovateuk.test    Passw0rd
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/1/partner/documents
    And the user should see the text in the page    Other documents
    When the user clicks the button/link    jQuery=button:contains("Reject documents")
    And the user clicks the button/link    jQuery=.modal-reject-docs button:contains("Cancel")
    Then the user should not see an error in the page
#    When the user clicks the button/link    jQuery=button:contains("Reject documents")
#    And the user clicks the button/link    jQuery=.modal-reject-docs .button:contains("Reject Documents")
#    Then the user should see the text in the page    These documents have been reviewed and rejected. We have returned them to the project team.
### Commenting out those lines so that the Other Documents can be Approved instead. Have been tested and the functionality works.


Partners can see the documents rejected
    [Documentation]    INFUND-5559, INFUND-5424
    ...       This test Case has been deactivated for project id=1. Because the Other Documents are Approved instead.
    [Tags]    Failing
    Given log in as a different user    worth.email.test+projectlead@gmail.com    Passw0rd    #Project Manager
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve these documents. Please contact Customer Support.")
    Given log in as a different user    steve.smith@empire.com    Passw0rd    #Lead Partner
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve these documents. Please contact Customer Support.")
    Given log in as a different user    pete.tom@egg.com    Passw0rd    #Academic Partner
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve these documents. Please contact Customer Support.")
    Given log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd    #Other Partner
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.warning-alert h2:contains("We are unable to approve these documents. Please contact Customer Support.")


Project Finance is able to Approve and Reject
    [Documentation]    INFUND-4621, INFUND-5440
    [Tags]
    [Setup]    Log in as a different user    project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/1/partner/documents
    Then the user should see the text in the page    Other documents
    And the user should see the element    jQuery=button:contains("Accept documents")
    And the user should see the element    jQuery=button:contains("Reject documents")
    When the user clicks the button/link    jQuery=button:contains("Accept documents")
    And the user clicks the button/link    jQuery=.modal-accept-docs button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Reject documents")
    And the user clicks the button/link    jQuery=.modal-reject-docs button:contains("Cancel")
    Then the user should not see an error in the page


Project Finance user can clik the link and go back to the Project setup status page
    [Documentation]    INFUND-5516
    [Tags]
    When the user clicks the button/link           link=Project setup status
    Then the user should not see an error in the page
    And the user should see the text in the page   Projects in setup
    [Teardown]    the user goes back to the previous page


CompAdmin approves other documents
    [Documentation]    INFUND-4621
    [Tags]    HappyPath
    [Setup]    Log in as a different user    john.doe@innovateuk.test    Passw0rd
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/1/partner/documents
    And the user should see the text in the page    Other documents
    And the user should see the text in the page    Vitruvius Stonework Limited
    And the user should see the text in the page    Ludlow
    And the user should see the text in the page    EGGS
    And the user should see the text in the page    worth.email.test+projectlead@gmail.com
    Then the user should see the element    jQuery=button:contains("Accept documents")
    And the user should see the element    jQuery=button:contains("Reject documents")
    When the user clicks the button/link    jQuery=button:contains("Accept documents")
    And the user clicks the button/link    jQuery=.modal-accept-docs button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Accept documents")
    And the user clicks the button/link    jQuery=.modal-accept-docs .button:contains("Accept Documents")
    Then the user should see the text in the page    The documents provided have been approved.


Partners can see the documents approved
    [Documentation]    INFUND-5559, INFUND-5424
    [Tags]    HappyPath    Pending
    #TO DO:INFUND-5887
    Given log in as user    worth.email.test+projectlead@gmail.com    Passw0rd    #Project Manager
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("These documents have been approved by Innovate UK")
    Given log in as a different user    steve.smith@empire.com    Passw0rd    #Lead Partner
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("These documents have been approved by Innovate UK")
    Given log in as a different user    pete.tom@egg.com    Passw0rd    #Academic Partner
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("These documents have been approved by Innovate UK")
    Given log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd    #Other Partner
    And the user navigates to the page    ${project_in_setup_page}/partner/documents
    Then the user should see the element    jQuery=.success-alert h2:contains("These documents have been approved by Innovate UK")

CompAdmin can see Project status updated
    [Documentation]    INFUND-2610
    [Tags]    HappyPath
    [Setup]    Log in as a different user    john.doe@innovateuk.test    Passw0rd
    Given the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link    link=Killer Riffs
    Then the user should see the element    jQuery=tr:nth-child(1):contains("best riffs")
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(6)


Status updates correctly for internal user's table
    [Documentation]    INFUND-4049
    [Tags]    Experian
    [Setup]    log in as a different user    john.doe@innovateuk.test    Passw0rd
    When the user navigates to the page    ${internal_project_summary}
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(2).status.ok
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).status.action
    # bank details are ok only when all 3 bank details are approved TODO
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.action
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.ok

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
