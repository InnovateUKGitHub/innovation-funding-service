*** Settings ***
Documentation     INFUND-187: As an applicant in the application form, I should be able to format my text in a basic way (bold, underline and bullets), so I can style my text properly
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Project summary section accepts Bold text format
    [Documentation]    INFUND-187
    [Tags]    Applicant    Form    WYSIWYG
    Given Applicant goes to the 'project summary' question
    When the Applicant clicks on Bold button in "Project summary"
    Then text entered should be Bold and keep the same after page refresh

Project summary section accepts Italic text format
    [Documentation]    INFUND-187
    [Tags]    Applicant    Form    WYSIWYG
    Given Applicant goes to the 'project summary' question
    When the Applicant clicks on Italic button in "Project summary"
    Then text entered should be Italic and keep the same after page refresh

Project summary section accepts Numbering bullet format
    [Documentation]    INFUND-187
    [Tags]    Applicant    Form    WYSIWYG
    Given Applicant goes to the 'project summary' question
    When the Applicant clicks on Numbering bullet button in "Project summary"
    Then text entered should be in Numbering bullets and keep the same after page refresh

Project summary section accepts Bullet format
    [Documentation]    INFUND-187
    [Tags]    Applicant    Form    WYSIWYG
    Given Applicant goes to the 'project summary' question
    When the Applicant clicks on Bullet format button in "Project summary"
    Then text entered should be in Bullet format and keep the same after page refresh

*** Keywords ***
the Applicant clicks on Bold button in "Project summary"
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    click element    css=.bold_button

the Applicant clicks on Italic button in "Project summary"
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Click Element    css=.italic_button

text entered should be Bold and keep the same after page refresh
    Input Text    css=#form-input-11 .editor    Entering text to verify BOLD.
    Element Should Be Visible    css=#form-input-11 .editor b
    Focus    css=.app-submit-btn
    Sleep    1s
    Reload Page
    Wait Until Page Contains Element    css=#form-input-11 .editor strong

the Applicant clicks on Numbering bullet button in "Project summary"
    Input Text    css=#form-input-11 .editor    This is testing for numbering bullets.
    Click Element    css=.insertOrderedList_button
    Focus    css=.app-submit-btn
    Sleep    1s

the Applicant clicks on Bullet format button in "Project summary"
    Input Text    css=#form-input-11 .editor    testing
    Click Element    css=.insertUnorderedList_button
    Focus    css=.app-submit-btn
    Sleep    1s

text entered should be Italic and keep the same after page refresh
    Input Text    css=#form-input-11 .editor    Entering text to verify ITALIC.
    Element Should Be Visible    css=#form-input-11 .editor i
    Focus    css=.app-submit-btn
    Sleep    1s
    Reload Page
    Wait Until Page Contains Element    css=#form-input-11 .editor em

text entered should be in Numbering bullets and keep the same after page refresh
    Element Should Be Visible    css=#form-input-11 .editor ol
    Reload Page
    Focus    css=.app-submit-btn
    Sleep    1s
    Wait Until Page Contains Element    css=#form-input-11 .editor ol

text entered should be in Bullet format and keep the same after page refresh
    Element Should Be Visible    css=#form-input-11 .editor li
    Reload Page
    Focus    css=.app-submit-btn
    Sleep    1s
    Wait Until Page Contains Element    css=#form-input-11 .editor li

the applicant is in the Application details section
    go to    ${APPLICATION_URL}
