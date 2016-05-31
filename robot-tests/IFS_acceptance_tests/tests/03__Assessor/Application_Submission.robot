*** Settings ***
Documentation     INFUND-248: As an assessor I want to submit my assessments one at a time or as a batch, so I can work in the way I feel most comfortable.
Suite Setup       Guest user log-in    &{assessor_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Pending
Resource          ../../resources/GLOBAL_LIBRARIES.robot    # Pending due to upcoming refactoring work for the assessor story
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot

*** Variables ***
${application_name}    Mobile Phone Data for Logistics Analytics
${competition_name}    ${OPEN_COMPETITION_LINK}

*** Test Cases ***
Submitting an application shows a confirmation popup
    [Documentation]    INFUND-342
    [Tags]    Assessor    HappyPath
    Given The user clicks the button/link    link=${competition_name}
    When Select Application Checkbox    ${application_name}
    And Submit Selected Applications
    Then Cancel the confirmation popup

Cancelling the confirmation leaves the Application in unchanged state
    [Documentation]    INFUND-342
    [Tags]    Assessor
    Given Select Application Checkbox    ${application_name}
    And Submit Selected Applications
    When Cancel the confirmation popup
    Then Application is not submitted

Confirming the popup changes the Application state to submitted
    [Documentation]    INFUND-342
    [Tags]    Assessor    HappyPath
    Given Select Application Checkbox    ${application_name}
    And Submit Selected Applications
    When Confirm the confirmation popup
    Then Application is submitted

*** Keywords ***
Select Application Checkbox
    [Arguments]    ${application_name}
    Click Element    xpath=//li[.//a[contains(text(),'${application_name}')]]//input[@class="assessment-submit-checkbox"]

Submit Selected Applications
    #Choose Cancel On Next Confirmation
    Click Element    xpath=//*[@id="formSubmitAssessmentButton"]

Cancel the confirmation popup
    Click Element    css=#bulk_assessments_submission > div.modal-confirm-submit > div > button.js-close.button.buttonlink.large

Confirm the confirmation popup
    Click Element    css=#bulk_assessments_submission > div.modal-confirm-submit > div > button:nth-child(1)

Application is not submitted
    Reload Page
    Page Should Contain Element    xpath=//*[@class="in-progress"]//a[contains(text(),'${application_name}')]

Application is submitted
    Reload Page
    Page Should Contain Element    xpath=//*[@class="submitted"]//*[contains(text(),'${application_name}')]
