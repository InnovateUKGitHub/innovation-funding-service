*** Settings ***
Documentation     INFUND-4851 As a project manager I want to be able to submit an uploaded Grant Offer Letter so that Innovate UK can review my signed copy
Suite Teardown    the user closes the browser
Force Tags        Project Setup    Pending
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Test Cases ***
Partners should not be able to access the Submit button
    [Documentation]    INFUND-4851
    [Setup]    log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    And the user clicks the button/link    link=Grant offer letter
    Then the user should not see the element    jQuery=label:contains(+ Upload)
    And the user should not see the element    jQuery=.button:contains("Submit signed offer letter")
    [Teardown]    logout as user

Project manager should be able to access the Submit button
    [Documentation]    INFUND-4851
    [Setup]    log in as user    &{lead_applicant_credentials}
    Given the user clicks the button/link    link=00000001: best riffs
    And the user clicks the button/link    link=Grant offer letter
    When the lead uploads a grant offer letter    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    And the Grant offer submit button should be enabled

When the Grant Offer Letter is submitted the Dashboard should be updated
    [Documentation]    INFUND-4851
    When the user clicks the button/link    jQuery=.button:contains("Submit signed offer letter")
    and the user clicks the button/link    jQuery=button:contains("Confirm Submission")
    Then the user should see the element

*** Keywords ***
the lead uploads a grant offer letter
    [Arguments]    ${file_name}
    choose file    name=signedGrantOfferLetter    ${upload_folder}/${file_name}

the Grant offer submit button should be enabled
    Element Should Be Enabled    jQuery=.button:contains("Submit signed offer letter")
