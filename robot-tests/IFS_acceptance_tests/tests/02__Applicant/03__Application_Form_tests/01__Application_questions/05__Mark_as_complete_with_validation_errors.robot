*** Settings ***
Documentation     INFUND-406: As an applicant, and on the application form I have validation error, I cannot mark questions or sections as complete in order to submit my application
Suite Setup       log in and create new application if there is not one already
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot
Resource          ../../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***

*** Test Cases ***
Mark as complete is impossible for empty questions
    [Documentation]    -INFUND-406
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Project summary
    When the "Project Summary" question is empty
    And the applicant marks the public description question as complete
    Then the applicant should get a warning to enter data in the "Project Summary" question
    And the applicant should get an alert with the description of the error

Error should not be visible when the text area is not empty
    [Documentation]    -INFUND-406
    [Tags]
    When the "Project Summary" question is empty
    And the applicant inserts some text again in the "Project Summary" question
    Then applicant should be able to mark the question as complete
    And the applicant can click edit to make the section editable again

*** Keywords ***
the "Project Summary" question is empty
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8

the applicant marks the public description question as complete
    Click Element    css=#form-input-11 .buttonlink[name="mark_as_complete"]

the applicant should get an alert with the description of the error
    Wait Until Element Is Visible    css=.error-summary li

the applicant should get a warning to enter data in the "Project Summary" question
    Wait Until Element Is Visible    css=#form-input-11 .error-message

the applicant inserts some text again in the "Project Summary" question
    Input Text    css=#form-input-11 .editor    test if the applicant can mark the question as complete
    mouse out    css=#form-input-11 .editor
    Sleep    300ms

applicant should be able to mark the question as complete
    Wait Until Element Is Visible    jQuery=button:contains("Mark as complete")
    focus    jQuery=button:contains("Mark as complete")
    Click Element    jQuery=button:contains("Mark as complete")
    Wait Until Element Is Not Visible    css=#form-input-11 .error-message
    Wait Until Element Is Not Visible    css=.error-summary li

the applicant can click edit to make the section editable again
    Sleep    500ms
    Wait Until Element is Visible    jQuery=button:contains("Edit")
    focus    jQuery=button:contains("Edit")
    click element    jQuery=button:contains("Edit")
    Wait Until Element is Visible    jQuery=button:contains("Mark as complete")
