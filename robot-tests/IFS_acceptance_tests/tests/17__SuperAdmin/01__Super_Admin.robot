*** Settings ***
Documentation     IFS-9604 IFS Expert user can return assessment to assessor
...
...               IFS-9692 IFS Expert User can change status of approved documents to rejected
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Administrator  CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Variables ***
${assessmentResetCompetitionName}     Sustainable living models for the future
${assessmentResetCompetitionID}       ${competition_ids["${assessmentResetCompetitionName}"]}
${assessmentResetApplicationName}     Living with Augmented Reality
${assessmentResetApplicationName}     ${application_ids["${assessmentResetApplicationName}"]}
${projectName}                        Energy efficient home device
${projectID}                          ${project_ids["${projectName}"]}
&{leadApplicantCredentials}           email=ron.spencer@gmail.com     password=${short_password}

*** Test Cases ***
Super admin can unsubmit assessment of an application already submitted
    [Documentation]  IFS-9604
    Given Logging in and Error Checking         &{superAdminCredentials}
    And the user navigates to the page          ${server}/management/assessment/competition/${assessmentResetCompetitionID}
    And the user clicks the button/link         link = Manage assessors
    When internal user filters the assessor
    And the user clicks the button/link         link = View progress
    And the user clicks the button/link         jQuery = td:contains("${assessmentResetApplicationName}") ~ td:contains("Unsubmit")
    And the user clicks the button/link         jQuery = button:contains("Unsubmit assessment")
    Then the user should see the element        jQuery = td:contains("${assessmentResetApplicationName}") ~ td:nth-child(8):contains("-") ~ td:contains("Remove")

Assessor can resubmit the assessment
    [Documentation]  IFS-9604
    Given log in as a different user             &{assessor2_credentials}
    And the user navigates to the page           ${server}/assessment/assessor/dashboard/competition/${assessmentResetCompetitionID}
    When the user clicks the button/link         link = ${assessmentResetApplicationName}
    And the user clicks the button/link          link = 1. Business opportunity
    And assessor edit the assessment question
    Then assessor re submits the assessment      ${assessmentResetApplicationName}

Super admin can not unsubmit the assessment once assessment is closed
    [Documentation]  IFS-9604
    Given log in as a different user           &{superAdminCredentials}
    And the user navigates to the page         ${server}/management/competition/${assessmentResetCompetitionID}
    When the user clicks the button/link       id = close-assessment-button
    And the user navigates to the page         ${server}/management/assessment/competition/${assessmentResetCompetitionID}
    And the user clicks the button/link        link = Allocate assessors
    And internal user filters the assessor
    And the user clicks the button/link        link = Assign
    Then the user should see the element       jQuery = td:contains("${assessmentResetApplicationName}") ~ td:nth-child(8):contains("Yes") ~ td:contains("${EMPTY}")

Super admin can reopen the assessment
    [Documentation]  IFS-9604
    Given the user navigates to the page     ${server}/management/competition/${assessmentResetCompetitionID}
    When the user clicks the button/link     id = reopen-assessment-period-button
    Then the user should see the element     id = close-assessment-button
    And the user should see the element      jQuery = h1:contains("In assessment") > span:contains("${assessmentResetCompetitionName}")

Super admin can reject an approved document
    [Documentation]  IFS-9692
    Given the user navigates to the page      ${server}/project-setup-management/project/${projectID}/document/all
    And the user should see the element       jQuery = li:nth-child(1) span:contains("Approved")
    When the user rejects the document
    Then the user should see the element      jQuery = p:contains("You have rejected this document. Please contact the Project Manager to explain your decision.")
    And the user clicks the button/link       jQuery = a:contains("Return to documents")
    And the user should see the element       jQuery = div span:contains("Rejected")

Super admin user cannot reject a document once the project is completed
    [Documentation]  IFS-9692
    Given the applicant submits the document
    And compAdmin user approves uploaded documents
    And compAdmin user approves the GOL
    When log in as a different user                   &{superAdminCredentials}
    And the user navigates to the page                ${SERVER}/project-setup-management/project/${projectID}/document/all
    And the user clicks the button/link               link = Exploitation plan
    Then the user should not see the element          jQuery = .govuk-heading-m:contains("Reject document") + div:contains("Reject")

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

assessor edit the assessment question
    The user selects the option from the drop-down menu     10    css = .assessor-question-score
    The user enters text to a text field                    css = .editor    Edited This is the business opportunity feedback
    Wait for autosave
    mouse out                                               css = .editor
    Wait Until Page Contains Without Screenshots            Saved!
    The user clicks the button with resubmission            jquery = button:contains("Save and return to assessment overview")

assessor re submits the assessment
    [Arguments]  ${applicationName}
    the user clicks the button/link          link = Review and complete your assessment
    the user enters text to a text field     id = feedback    Edited Innovative solution with great promise, recommended
    the user clicks the button/link          jQuery = .govuk-button:contains("Save assessment")
    the user clicks the button/link          jQuery = li:contains("${applicationName}") label[for^="assessmentIds"]
    the user clicks the button/link          jQuery = .govuk-button:contains("Submit assessments")
    the user clicks the button/link          jQuery = button:contains("Yes I want to submit the assessments")
    the user should see the element          jQuery = li:contains("${applicationName}") strong:contains("Recommended")

internal user filters the assessor
    the user enters text to a text field     id = assessorNameFilter   Wilson
    the user clicks the button/link          jQuery = .govuk-button:contains("Filter")

the user rejects the document
    the user navigates to the page          ${server}/project-setup-management/project/${projectID}/document/all
    the user clicks the button/link         jQuery = a:contains("Exploitation plan")
    the user should see the element         jQuery = .govuk-heading-m:contains("Reject document") + div:contains("Reject")
    the user selects the radio button       approved   false
    the user enters text to a text field    id = document-reject-reason   Test string
    the user clicks the button/link         id = submit-button
    the user clicks the button/link         id = reject-document

the applicant submits the document
    log in as a different user                                           &{leadApplicantCredentials}
    the user navigates to the page                                       ${server}/project-setup/project/${projectID}/document/all
    the user clicks the button/link                                      jQuery = a:contains("Exploitation plan")
    the user clicks the button/link                                      jQuery = button:contains("Remove")
    Wait Until Page Does Not Contain Without Screenshots                 Removing
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user should see the element                                      jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
    the user clicks the button/link                                      jQuery = button:contains("Submit"):nth(1)
    the user clicks the button/link                                      jQuery = .modal-configured-partner-document button:contains("Submit")
    the user should not see an error in the page

compAdmin user approves uploaded documents
    log in as a different user           &{Comp_admin1_credentials}
    the user navigates to the page       ${SERVER}/project-setup-management/project/${projectID}/document/all
    the user clicks the button/link      link = Exploitation plan
    the user selects the radio button    approved   true
    the user clicks the button/link      id = submit-button
    the user clicks the button/link      id = accept-document

compAdmin user approves the GOL
    the user navigates to the page       ${SERVER}/project-setup-management/project/${projectID}/grant-offer-letter/send
    the user selects the radio button    approvalType  acceptGOL
    the user clicks the button/link      jQuery = button:contains("Submit")
    the user clicks the button/link      jQuery = button[type = "submit"]:contains("Accept signed grant offer letter")
    the user should see the element      jQuery = .success-alert h2:contains("These documents have been approved.")