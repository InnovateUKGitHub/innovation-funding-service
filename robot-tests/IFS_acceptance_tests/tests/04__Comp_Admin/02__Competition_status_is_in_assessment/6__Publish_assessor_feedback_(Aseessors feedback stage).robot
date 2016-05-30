*** Settings ***
Documentation     INFUND-2672
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    User closes the browser
Force Tags        Comp admin    Upload
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${assessor_feedback_competition_url}    ${server}/management/competition/3
${successful_application_overview}    ${server}/management/competition/3/application/16
${unsuccessful_application_overview}    ${server}/management/competition/3/application/17
${dialogue_warning_message}    Are you sure you wish to inform applicants if they have been successful in gaining funding.    # note that this will change!
${feedback_success_email}    Pending
${feedback_failure_email}    Pending

*** Test Cases ***
Comp admin can visit a competition page at "Assessor feedback" stage and the option to publish feedback is disabled
    [Documentation]    INFUND-2672
    When the user navigates to the page    ${assessor_feedback_competition_url}
    Then the user should see the text in the page    Assessor Feedback
    And the user should see the element    css=h2.bold-small.blue-block
    And the option to publish feedback is disabled

If feedback is uploaded for each application then the option to publish feedback is enabled
    [Documentation]    INFUND-2672
    [Tags]
    Given the user can see the option to upload a file on the page    ${successful_application_overview}
    When the user uploads the file    ${valid_pdf}
    Given the user can see the option to upload a file on the page    ${unsuccessful_application_overview}
    When the user uploads the file    ${valid_pdf}
    Then the option to publish feedback is enabled

Comp admin can remove feedback and the option to publish feedback becomes disabled
    [Documentation]    INFUND-2672
    [Tags]
    Given the user navigates to the page    ${successful_application_overview}
    And the user should see the text in the page    Remove
    When the user clicks the button/link    name=removeAssessorFeedback
    Then the option to publish feedback is disabled

Pushing the publish feedback button brings up a warning dialogue
    [Documentation]    INFUND-2672
    [Tags]
    [Setup]    Run Keywords    the user navigates to the page    ${successful_application_overview}
    ...    AND    the user uploads the file    ${valid_pdf}
    Given the user navigates to the page    ${assessor_feedback_competition_url}
    When the user clicks the button/link    jQuery=.button:contains("Publish assessor feedback")
    Then the user should see the text in the page    ${dialogue_warning_message}
    And the user should see the element    jQuery=.button:contains("Cancel")
    And the user should see the element    jQuery=.button:contains("Publish assessor feedback")

Choosing cancel on the dialogue goes back to the Assessor feedback page
    [Documentation]    INFUND-2672
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Cancel")
    Then the user should be redirected to the correct page    ${assessor_feedback_competition_url}
    And the user should see the text in the page    Assessor Feedback
    [Teardown]    The user clicks the button/link    jQuery=.button:contains("Publish assessor feedback")

Choosing Publish assessor feedback on the dialogue redirects to the Project setup page
    [Documentation]    INFUND-2672
    [Tags]
    When the user clicks the button/link    name=publish
    Then the user should be redirected to the correct page    ${assessor_feedback_competition_url}
    # The test above is required to trigger the state changes, but the step below is commented out as it is
    # Pending due to INFUND-3156
    # And the user should see the text in the page    Project setup

Successful applicants are notified of the feedback
    [Documentation]    INFUND-2608
    [Tags]    Email    Pending
    # Pending completion of the INFUND-2608 story
    Then the user should get a confirmation email    ${test_mailbox_one}    ${feedback_success_email}

Unsuccessful applicants are notified of the feedback
    [Documentation]    INFUND-2608
    [Tags]    Email    Pending
    # Pending completion of the INFUND-2608 story
    Then the user should get a confirmation email    ${test_mailbox_two}    ${feedback_failure_email}

Once applicants are notified, the whole state of the competition changes to Project setup
    [Documentation]    INFUND-2646
    [Tags]    Pending
    # Pending due to INFUND-3156
    When the user should see the text in the page    Project setup

*** Keywords ***
The option to publish feedback is enabled
    the user navigates to the page    ${assessor_feedback_competition_url}
    the user should see the element    id=publish-assessor-feedback
    the user should not see the element    xpath=//button[@disabled = 'disabled']

The option to publish feedback is disabled
    the user navigates to the page    ${assessor_feedback_competition_url}
    the user should see the element    xpath=//button[@disabled = 'disabled']

the user uploads the file
    [Arguments]    ${upload_filename}
    Choose File    id=assessorFeedback    ${UPLOAD_FOLDER}/${upload_filename}
    Sleep    500ms
