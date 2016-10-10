*** Settings ***
Documentation     INFUND-2602 As a competition administrator I want a view of the Application Overview page that allows me to upload the assessor feedback document so that this can be shared with the applicants
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Upload    CompAdmin
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${successful_application_overview}    ${server}/management/competition/3/application/16
${unsuccessful_application_overview}    ${server}/management/competition/3/application/17

*** Test Cases ***
Invalid Large pdf
    [Documentation]    INFUND-2602
    [Tags]  Upload
    Given the user can see the option to upload a file on the page    ${successful_application_overview}
    When the user uploads the file    ${too_large_pdf}
    Then the user should get an error page    ${too_large_pdf_validation_error}

Invalid Non pdf
    [Documentation]    INFUND-2602
    [Tags]
    Given the user can see the option to upload a file on the page    ${successful_application_overview}
    When the user uploads the file    ${text_file}
    Then the user should get an error page    ${wrong_filetype_validation_error}

Valid upload to a successful application
    [Documentation]    INFUND-2602
    [Tags]    HappyPath
    Given the user can see the option to upload a file on the page    ${successful_application_overview}
    And the user uploads the file    ${valid_pdf}

Open and view the file
    [Documentation]    INFUND-2602
    [Tags]    HappyPath
    Given the user should see the text in the page    ${valid_pdf}
    And the file has been scanned for viruses
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    [Teardown]    The user navigates to the page    ${successful_application_overview}

Upload more than one file is impossible
    [Documentation]    INFUND-2602
    [Tags]
    When the user should see the text in the page    ${valid_pdf}
    Then the user should not see the element    jQuery=.button:contains("Upload")

Remove the file
    [Documentation]    INFUND-2602
    [Tags]    HappyPath
    Given the user should see the text in the page    ${valid_pdf}
    And the user should see the text in the page    Remove
    When the user clicks the button/link    name=removeAssessorFeedback
    Then the user should not see the text in the page    ${valid_pdf}
    And the user should see the text in the page    Upload

Re-upload after removing
    [Documentation]    INFUND-2602
    [Tags]    HappyPath
    Given the user can see the option to upload a file on the page    ${successful_application_overview}
    And the user uploads the file    ${valid_pdf}
    [Teardown]    the user clicks the button/link    name=removeAssessorFeedback

Upload a file to an unsuccessful application
    [Documentation]    INFUND-2602
    [Tags]
    Given the user can see the option to upload a file on the page    ${unsuccessful_application_overview}
    And the user uploads the file    ${valid_pdf}
    [Teardown]    the user clicks the button/link    name=removeAssessorFeedback

*** Keywords ***
the user uploads the file
    [Arguments]    ${upload_filename}
    Choose File    id=assessorFeedback    ${UPLOAD_FOLDER}/${upload_filename}
    Sleep    500ms
