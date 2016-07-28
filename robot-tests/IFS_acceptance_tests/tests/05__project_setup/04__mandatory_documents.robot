*** Settings ***
Documentation     INFUND-3013 As a partner I want to be able to download mandatory documents supplied during project setup so that I can review information submitted to Innovate UK by the project manager
...
...               INFUND-3011 As a lead partner I need to provide mandatory documents so that they can be reviewed by all partners before submitting to Innovate UK

Suite Setup       Log in as user    jessica.doe@ludlow.co.uk       Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Pending    # Pending due to ongoing work by Ewan Cormack
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
    [Documentation]    INFUND-3430
    [Tags]
    Given the user navigates to the page     ${project_in_setup_page}
    When the user clicks the button/link     link=Other documents
    Then the user should not see the text in the page      Upload
    [Teardown]    Logout as user

Large pdfs not allowed for either document
    [Documentation]   INFUND-3430
    [Tags]
    [Setup]   Guest user log-in  steve.smtih@empire.com   Passw0rd
    Given the user navigates to the page     ${project_in_setup_page}
    When the user uploads to the foo question      ${too_large_pdf}
    Then the user should see an error       ${too_large_pdf_validation_error}
    When the user uploads to the bar question     ${too_large_pdf}
    Then the user should see an error      ${too_large_pdf_validation_error}
    And the user should not see the text in the page      ${too_large_pdf}

Non pdf files not allowed for either document
    [Documentation]    INFUND-3430
    When the user uploads to the foo question      ${text_file}
    Then the user should see an error       ${wrong_filetype_validation_error}
    When the user uploads to the bar question     ${text_file}
    Then the user should see an error      ${wrong_filetype_validation_error}
    And the user should not see the text in the page      ${text_file}

Lead partner can upload both documents
    [Documentation]    INFUND-3430
    [Tags]
    When the user uploads to the foo question     ${valid_pdf}
    Then the user should see the text in the page     ${valid_pdf}
    When the user uploads to the bar question     ${valid_pdf}
    Then the user should not see the text in the page     Remove
    And the user should not see an error in the page


Lead partner can view both documents
    [Documentation]    INFUND-3430
    [Tags]
    When the user clicks the button/link     link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link     link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page


Lead partner cannot remove either document
    [Documentation]    INFUND-3430
    [Tags]
    When the user should not see the text in the page    Remove
    And the user should not see the element      link=Remove


Lead partner does not have the option to submit the mandatory documents
    [Documentation]  INFUND-3430
    [Tags]
    When the user should not see an error in the page
    And the user should see the element    jQuery=.button.enabled:contains("Submit other documents")
    [Teardown]   Logout as user


Non-lead partner can view both documents
    [Documentation]    INFUND-3422
    [Tags]
    [Setup]   Guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link     link=Other documents
    And the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page

Non-lead partner cannot remove either document
    [Documentation]    INFUND-3422
    [Tags]
    When the user should not see the text in the page     Remove
    And the user should not see the element     link=Remove

Non-lead partner does not have the option to submit the mandatory documents
    [Documentation]    INFUND-3422
    [Tags]
    When the user should not see the element     jQuery=.button.enabled:contains("Submit other documents")
    [Teardown]    logout as user

PM can view both documents
    [Documentation]    INFUND-3430
    [Tags]
    [Setup]   Guest user log-in  test20@test.test   Passw0rd
    Given the user navigates to the page     ${project_in_setup_page}
    And the user clicks the button/link     link=Other documents
    When the user clicks the button/link     link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page


PM can remove both documents
    [Documentation]   INFUND-3430
    [Tags]
    When the user clicks the button/link     link=Remove
    Then the user should not see the element     link=${valid_pdf}(2)
    When the user clicks the button/link     link=Remove
    Then the user should not see an error in the page
    And the user should not see the element  link=${valid_pdf}

PM can upload both documents
    [Documentation]   INFUND-3430
    [Tags]
    When the user uploads to the foo question    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    When the user uploads to the bar question    ${valid_pdf}
    Then the user should not see an error in the page


Status in the dashboard remains pending after uploads
    [Documentation]   INFUND-3430
    [Tags]
    When the user clicks the button/link   link=Back to project details
    Then the user should see the element     blah.pending

Mandatory document submission
    [Documentation]   INFUND-3430
    [Tags]
    Given the user clicks the button/link    link=Other documents
    When the user clicks the button/link    jQuery=.button:contains("Submit other documents")
    And the user clicks the button/link    jQuery=.button:contains("Cancel")
    Then the user should see the element    link=Remove     # testing here that the section has not become read-only
    When the user clicks the button/link    jQuery=.button:contains("Submit other documents")
    And the user clicks the button/link     jQuery=.button:contains("Submit")
    Then the user should be redirected to the correct page  ${project_in_setup_page}


*** Keywords ***

the user uploads to the foo question
    [Arguments]    ${pdf_name}

the user uploads to the bar question
    [Arguments]    ${pdf_name}
