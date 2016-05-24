*** Settings ***
Documentation     INFUND-187: As an applicant in the application form, I should be able to format my text in a basic way (bold, underline and bullets), so I can style my text properly
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Test Cases ***
Bold text format
    [Documentation]    INFUND-187
    [Tags]    Applicant    Form    HappyPath
    Given the user navigates to the page    ${BUSINESS_OPPORTUNITY_URL}
    When the Applicant clicks on the Bold button in the "business opportunity" field
    Then all text entered should be Bold and stay the same after page refresh

Italic text format
    [Documentation]    INFUND-187
    [Tags]    Applicant    Form    HappyPath
    Given the user navigates to the page    ${BUSINESS_OPPORTUNITY_URL}
    When the Applicant clicks on the Italic button in the "business opportunity" field
    Then all text entered should be Italic and stay the same after page refresh

Numbering bullet format
    [Documentation]    INFUND-187
    [Tags]    Applicant    Form    HappyPath
    Given the user navigates to the page    ${BUSINESS_OPPORTUNITY_URL}
    When the Applicant clicks on the Numbering bullet button in the "business opportunity" field
    Then all text entered should be in Numbering bullets and stay the same after page refresh

Bullet format
    [Documentation]    INFUND-187
    [Tags]    Applicant    Form    HappyPath
    Given the user navigates to the page    ${BUSINESS_OPPORTUNITY_URL}
    When the Applicant clicks on the Bullet format button in the "business opportunity" field
    Then all text entered should be in Bullet format and stay the same after page refresh

*** Keywords ***
the Applicant clicks on the Bold button in the "business opportunity" field
    Clear Element Text    css=#form-input-1 .editor
    Press Key    css=#form-input-1 .editor    \\8
    click element    css=.bold_button

the Applicant clicks on the Italic button in the "business opportunity" field
    Clear Element Text    css=#form-input-1 .editor
    Press Key    css=#form-input-1 .editor    \\8
    Click Element    css=.italic_button

all text entered should be Bold and stay the same after page refresh
    Input Text    css=#form-input-1 .editor    Entering text to verify BOLD.
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Element Should Be Visible    css=#form-input-1 .editor b
    Run Keyword If    '${status}' == 'FAIL'    Element Should Be Visible    css=#form-input-1 .editor strong
    Focus    css=.app-submit-btn
    Sleep    1s
    Reload Page
    Wait Until Page Contains Element    css=#form-input-1 .editor strong

the Applicant clicks on the Numbering bullet button in the "business opportunity" field
    Input Text    css=#form-input-1 .editor    This is testing for numbering bullets.
    Click Element    css=.insertOrderedList_button
    Focus    css=.app-submit-btn
    Sleep    500ms

the Applicant clicks on the Bullet format button in the "business opportunity" field
    Input Text    css=#form-input-1 .editor    testing
    Click Element    css=.insertUnorderedList_button
    Focus    css=.app-submit-btn
    Sleep    1s

all text entered should be Italic and stay the same after page refresh
    Input Text    css=#form-input-1 .editor    Entering text to verify ITALIC.
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Element Should Be Visible    css=#form-input-1 .editor i
    Run Keyword If    '${status}' == 'FAIL'    Element Should Be Visible    css=#form-input-1 .editor em
    Focus    css=.app-submit-btn
    Sleep    1s
    Reload Page
    Wait Until Page Contains Element    css=#form-input-1 .editor em

all text entered should be in Numbering bullets and stay the same after page refresh
    Element Should Be Visible    css=#form-input-1 .editor ol
    Focus    css=.app-submit-btn
    Reload Page
    Run Keyword And Ignore Error    Confirm Action
    Focus    css=.app-submit-btn
    Sleep    1s
    Wait Until Page Contains Element    css=#form-input-1 .editor ol

all text entered should be in Bullet format and stay the same after page refresh
    Element Should Be Visible    css=#form-input-1 .editor li
    Focus    css=.app-submit-btn
    Reload Page
    Focus    css=.app-submit-btn
    Sleep    1s
    Wait Until Page Contains Element    css=#form-input-1 .editor li
