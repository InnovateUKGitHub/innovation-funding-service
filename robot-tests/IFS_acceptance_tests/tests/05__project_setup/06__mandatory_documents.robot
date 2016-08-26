*** Settings ***
Documentation     INFUND-3013 As a partner I want to be able to download mandatory documents supplied during project setup so that I can review information submitted to Innovate UK by the project manager
...
...               INFUND-3011 As a lead partner I need to provide mandatory documents so that they can be reviewed by all partners before submitting to Innovate UK
...
...               INFUND-3012: As a project manager I want to be able to submit all mandatory documents on behalf of all partners so that Innovate UK can review additional information to support our project setup
Suite Setup       Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***

*** Test Cases ***
Non-lead partner cannot upload either document
    [Documentation]    INFUND-3011
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Upload
    [Teardown]    Logout as user

PM cannot submit when both documents are not uploaded
    [Documentation]    INFUND-3012
    [Tags]
    Given Guest user log-in    worth.email.test+projectlead@gmail.com    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    And the user should see the text in the page    Upload
    Then the user should not see the element    jQuery=.button.enabled:contains("Submit partner documents")
    [Teardown]    Logout as user

Large pdfs not allowed for either document
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    Guest user log-in    steve.smith@empire.com    Passw0rd
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

Lead partner can upload both documents
    [Documentation]    INFUND-3011
    [Tags]
    When the user uploads to the collaboration agreement question    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    When the user uploads to the exploitation plan question    ${valid_pdf}
    Then the user should not see an error in the page

Lead partner can view both documents
    [Documentation]    INFUND-3011
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page

Lead partner cannot remove either document
    [Documentation]    INFUND-3011
    [Tags]    Pending
    # Pending due to INFUND-4253
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Lead partner does not have the option to submit the mandatory documents
    [Documentation]    INFUND-3011
    [Tags]
    When the user should not see an error in the page
    And the user should not see the element    jQuery=.button.enabled:contains("Submit partner documents")

Non-lead partner can view both documents
    [Documentation]    INFUND-3011
    ...
    ...
    ...    INFUND-3013
    [Tags]
    [Setup]    Logout as user
    Given guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    And the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page

Non-lead partner cannot remove either document
    [Documentation]    INFUND-3013
    [Tags]
    When the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

Non-lead partner does not have the option to submit the mandatory documents
    [Documentation]    INFUND-3013
    [Tags]
    When the user should not see the element    jQuery=.button.enabled:contains("Submit partner documents")


PM can view both documents
    [Documentation]    INFUND-3011
    [Setup]    Logout as user
    [Tags]
    Given Guest user log-in    worth.email.test+projectlead@gmail.com    Passw0rd
    And the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page

PM can remove the second document
    [Documentation]    INFUND-3011
    [Tags]
    When the user clicks the button/link    name=removeExploitationPlanClicked
    Then the user should not see an error in the page
    [Teardown]    logout as user

Non-lead partner can still view the first document
    [Documentation]    INFUND-4252
    [Setup]    guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    Then the user should see the text in the page    ${valid_pdf}
    [Teardown]    logout as user

PM can remove the first document
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    guest user log-in    worth.email.test+projectlead@gmail.com    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    name=removeCollaborationAgreementClicked
    Then the user should not see the text in the page    ${valid_pdf}
    [Teardown]    logout as user

Non-lead partner cannot view either document once removed
    [Documentation]    INFUND-4252
    [Setup]    guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    ${valid_pdf}
    [Teardown]    logout as user

PM can upload both documents
    [Documentation]    INFUND-3011
    [Tags]
    [Setup]    guest user log-in    worth.email.test+projectlead@gmail.com    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user uploads to the collaboration agreement question    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    When the user uploads to the exploitation plan question    ${valid_pdf}
    Then the user should not see an error in the page

Status in the dashboard remains pending after uploads
    [Documentation]    INFUND-3011
    [Tags]
    When the user clicks the button/link    link=Project setup status
    Then the user should not see the element    jQuery=ul li.complete:nth-child(7)
    [Teardown]    logout as user

Mandatory document submission
    [Documentation]    INFUND-3011
    [Setup]    guest user log-in    worth.email.test+projectlead@gmail.com    Passw0rd
    [Tags]
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Other documents
    When the user clicks the button/link    jQuery=.button:contains("Submit partner documents")
    And the user clicks the button/link    jQuery=.button:contains("Cancel")
    Then the user should see the element    name=removeExploitationPlanClicked    # testing here that the section has not become read-only
    When the user clicks the button/link    jQuery=.button:contains("Submit partner documents")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    And the user should see the text in the page    These documents have been approved by Innovate UK.
    And the user clicks the button/link    link=Project setup status
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(7)

PM cannot remove the documents after submitting
    [Documentation]    INFUND-3012
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked

PM can still view both documents after submitting
    [Documentation]    INFUND-3012
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    Then the user clicks the button/link    link=${valid_pdf}
    And the user should not see an error in the page
    And the user goes back to the previous page
    [Teardown]    logout as user

Lead partner cannot remove the documents after submission by PM
    [Documentation]    INFUND-3012
    [Setup]    Guest user log-in    steve.smith@empire.com    Passw0rd
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
    [Teardown]    logout as user

Non-lead partner cannot remove the documents after submission by PM
    [Documentation]    INFUND-3012
    [Setup]    Guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
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
    And the user goes back to the previous page
    [Teardown]    logout as user

*** Keywords ***
the user uploads to the collaboration agreement question
    [Arguments]    ${file_name}
    choose file    name=collaborationAgreement    ${upload_folder}/${file_name}

the user uploads to the exploitation plan question
    [Arguments]    ${file_name}
    choose file    name=exploitationPlan    ${upload_folder}/${file_name}
