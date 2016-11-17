*** Settings ***
Documentation     INFUND-2672 As a competition administrator I want to be able to publish the assessor feedback when ready for distribution so that all applicants can review further information to support the funding decision
...
...               INFUND-2608 As a lead applicant I want to receive an email to inform me when the application feedback is accessible so that I can review the assessment
Suite Setup       Run Keywords    Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
...               AND    Run Keyword And Ignore Error    Delete the emails from both test mailboxes
Suite Teardown    the user closes the browser
Force Tags        Upload    CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${assessor_feedback_competition_url}    ${server}/management/competition/3
${successful_application_overview}    ${server}/management/competition/3/application/16
${unsuccessful_application_overview}    ${server}/management/competition/3/application/17
${project_setup_status_view}    ${server}/project-setup-management/competition/3/status
${dialogue_warning_message}    This will inform applicants that assessor feedback is available. 

*** Test Cases ***
The publish feedback should be disabled
    [Documentation]    INFUND-2672
    [Tags]    HappyPath
    When the user navigates to the page    ${assessor_feedback_competition_url}
    Then the user should see the text in the page    Assessor Feedback
    And the user should see the element    css=h2.bold-small.blue-block
    And the option to publish feedback is disabled

The publish feedback should be enabled
    [Documentation]    INFUND-2672
    [Tags]    HappyPath
    Given the user can see the option to upload a file on the page    ${successful_application_overview}
    When the user uploads the file    ${valid_pdf}
    Given the user can see the option to upload a file on the page    ${unsuccessful_application_overview}
    When the user uploads the file    ${valid_pdf}
    Then the option to publish feedback is enabled

Remove the upload then feedback button becomes disabled
    [Documentation]    INFUND-2672
    [Tags]    HappyPath
    Given the user navigates to the page    ${successful_application_overview}
    And the user should see the text in the page    Remove
    When the user clicks the button/link    name=removeAssessorFeedback
    Then the option to publish feedback is disabled

Pushing the publish feedback brings up a warning
    [Documentation]    INFUND-2672
    [Tags]    HappyPath
    [Setup]    Run Keywords    the user navigates to the page    ${successful_application_overview}
    ...    AND    the user uploads the file    ${valid_pdf}
    Given the user navigates to the page    ${assessor_feedback_competition_url}
    When the user clicks the button/link    jQuery=.button:contains("Publish assessor feedback")
    Then the user should see the text in the page    ${dialogue_warning_message}
    And the user should see the element    jQuery=button:contains("Cancel")
    And the user should see the element    jQuery=.button:contains("Publish assessor feedback")

Choosing cancel on the dialogue
    [Documentation]    INFUND-2672
    [Tags]
    When the user clicks the button/link    jQuery=button:contains("Cancel")
    Then the user should be redirected to the correct page    ${assessor_feedback_competition_url}
    And the user should see the text in the page    Assessor Feedback
    [Teardown]    The user clicks the button/link    jQuery=.button:contains("Publish assessor feedback")

Choosing to Notify the applicants in the dialogue
    [Documentation]    INFUND-2672
    [Tags]    HappyPath
    When the user clicks the button/link    name=publish
    Then the user should be redirected to the correct page    ${project_setup_status_view}
    And the user should see the text in the page    Projects in setup

Successful applicant gets feedback email
    [Documentation]    INFUND-2608, INFUND-3476
    [Tags]    Email
    Then the user reads his email from the default mailbox    worth.email.test+fundsuccess@gmail.com    Feedback for your application into the competition La Fromage is now available.    Dear test ten

Unsuccessful applicant gets feedback email
    [Documentation]    INFUND-2608, INFUND-3476
    [Tags]    Email
    Then the user reads his email from the second default mailbox    worth.email.test.two+fundfailure@gmail.com    Feedback for your application into the competition La Fromage is now available.    Dear test eleven
    [Teardown]    Delete the emails from both default test mailboxes

The whole state of the competition should change to Project setup
    [Documentation]    INFUND-2646
    [Tags]
    When the user should see the text in the page    Projects in setup

*** Keywords ***
The option to publish feedback is enabled
    the user navigates to the page    ${assessor_feedback_competition_url}
    the user should see the element    jQuery=.button:contains("Publish assessor feedback")
    the user should not see the element    xpath=//button[@disabled = 'disabled']

The option to publish feedback is disabled
    the user navigates to the page    ${assessor_feedback_competition_url}
    the user should see the element    xpath=//button[@disabled = 'disabled']

the user uploads the file
    [Arguments]    ${upload_filename}
    Choose File    id=assessorFeedback    ${UPLOAD_FOLDER}/${upload_filename}
    Sleep    500ms
