*** Settings ***
Documentation     INFUND-187: As an applicant in the application form, I should be able to format my text in a basic way (bold, underline and bullets), so I can style my text properly
Suite Setup       log in and create new application if there is not one already  Robot test application
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot

*** Test Cases ***
Bold text format
    [Documentation]    INFUND-187
    [Tags]  HappyPath
    Given the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link    link = Robot test application
    And the user clicks the button/link    link = 1. Business opportunity
    When the Applicant clicks on the Bold button in the "business opportunity" field
    Then all text entered should be Bold and stay the same after page refresh

Italic text format
    [Documentation]    INFUND-187
    [Tags]  HappyPath
    When the Applicant clicks on the Italic button in the "business opportunity" field
    Then all text entered should be Italic and stay the same after page refresh

Numbering bullet format
    [Documentation]    INFUND-187
    [Tags]  HappyPath
    When the Applicant clicks on the Numbering bullet button in the "business opportunity" field
    Then all text entered should be in Numbering bullets and stay the same after page refresh

Bullet format
    [Documentation]    INFUND-187
    [Tags]  HappyPath
    When the Applicant clicks on the Bullet format button in the "business opportunity" field
    Then all text entered should be in Bullet format and stay the same after page refresh

*** Keywords ***
the Applicant clicks on the Bold button in the "business opportunity" field
    Clear Element Text    css = .textarea-wrapped .editor
    Press Key             css = .textarea-wrapped .editor    \\8
    wait for autosave
    click element         css = .bold_button

the Applicant clicks on the Italic button in the "business opportunity" field
    Clear Element Text    css = .textarea-wrapped .editor
    Press Key             css = .textarea-wrapped .editor    \\8
    click element         css = .italic_button

all text entered should be Bold and stay the same after page refresh
    The user enters text to a text field    css = .textarea-wrapped .editor    Entering text to verify BOLD.
    ${STATUS}    ${VALUE} =     Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible    css = .textarea-wrapped .editor b
    Run Keyword If    '${status}' == 'FAIL'    Element Should Be Visible    css = .textarea-wrapped .editor strong
    Set Focus To Element      id = application-question-complete
    wait for autosave
    the user reloads the page
    the user should see the element    css = .textarea-wrapped .editor strong

the Applicant clicks on the Numbering bullet button in the "business opportunity" field
    The user enters text to a text field    css = .textarea-wrapped .editor    This is testing for numbering bullets.
    Press Key        css = .textarea-wrapped .editor    \\8
    click element    css = .insertOrderedList_button
    Set Focus To Element              id = application-question-complete
    wait for autosave

the Applicant clicks on the Bullet format button in the "business opportunity" field
    The user enters text to a text field    css = .textarea-wrapped .editor    testing
    Press Key        css = .textarea-wrapped .editor    \\8
    click element    css = .insertUnorderedList_button
    Set Focus To Element              id = application-question-complete
    wait for autosave

all text entered should be Italic and stay the same after page refresh
    The user enters text to a text field    css = .textarea-wrapped .editor    Entering text to verify ITALIC.
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible    css = .textarea-wrapped .editor i
    Run Keyword If    '${status}' == 'FAIL'    the user should see the element    css = .textarea-wrapped .editor em
    Set Focus To Element      id = application-question-complete
    wait for autosave
    the user reloads the page
    the user should see the element    css = .textarea-wrapped .editor em

all text entered should be in Numbering bullets and stay the same after page refresh
    the user should see the element    css = .textarea-wrapped .editor ol
    Set Focus To Element      id = application-question-complete
    wait for autosave
    the user reloads the page
    the user should see the element    css = .textarea-wrapped .editor ol

all text entered should be in Bullet format and stay the same after page refresh
    the user should see the element    css = .textarea-wrapped .editor li
    Set Focus To Element      id = application-question-complete
    wait for autosave
    the user reloads the page
    Set Focus To Element      id = application-question-complete
    the user should see the element    css = .textarea-wrapped .editor li
