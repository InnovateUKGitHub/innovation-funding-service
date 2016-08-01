*** Settings ***
Documentation     INFUND-3303: As an Assessor I want the ability to reject the application after I have been given access to the full details so I can make Innovate UK aware.
Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Autosave and edit the Application questions
    [Documentation]    INFUND-3400
    [Tags]    Pending
    # TODO pending as the autosave is not implemented. It will done in sprint 13
    Given the user navigates to the page    ${Application_question_url}
    When the Assessor edits the application question
    And the user reloads the page
    Then the modified text should be visible

*** Keywords ***
the Assessor edits the application question
    Select From List By Index    id=assessor-question-score    3
   # focus    css=#form-input-195 .isModified
   # Clear Element Text    css=#form-input-195 .isModified
    Input Text    css=#form-input-195 .isModified    This is to test the feedback entry is editable.
    sleep    1s

the modified text should be visible
    wait until element contains    css=#form-input-195 .isModified    This is to test the feedback entry is editable.