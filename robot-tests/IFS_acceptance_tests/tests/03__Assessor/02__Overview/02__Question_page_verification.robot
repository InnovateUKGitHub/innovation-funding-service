*** Settings ***
Documentation     INFUND-3780: As an Assessor I want the system to autosave my work so that I can be sure that my assessment is always in its most current state.
...
...               INFUND-3303: As an Assessor I want the ability to reject the application after I have been given access to the full details so I can make Innovate UK aware.
...
...               INFUND-4203: Prevent navigation options appearing for questions that are not part of an assessment
Suite Setup       guest user log-in    paul.plum@gmail.com    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Autosave and edit the Application question - How many
    [Documentation]    INFUND-3552
    [Tags]    Pending
    Given the user navigates to the page    ${Assessment_overview_9}
    When the user clicks the button/link    link=1. How many
    Then The user should see the text in the page    Please review the answer provided and score the answer out of 20 points.
    And the Assessor fills in application questions
    And the user reloads the page
    And the text should be visible
    Then the Assessor edits the application question
    And the user reloads the page
    And the modified text should be visible

Word count functionality
    [Documentation]    INFUND-3402
    [Tags]    Pending
    Given the user navigates to the page    ${Application_question_url}
    Then the word count should be calculated correctly
    When the Assessor enters more than 100 in feedback
    And the user reloads the page
   # Then the word count should remain the same

Scope - Project details
    [Documentation]    INFUND-3402
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    When the user clicks the button/link    link=Scope
    Then The user should see the element    jquery=button:contains("Save and return to assessment overview")
    And the Assessor fills in Scope details with In Scope as NO
    Then The user should be redirected to the correct page    ${Assessment_overview_9}
    And The user should see the element    css=#form-input-46 .column-third img
    And The user should see the text in the page    In scope? No
    Then the Assessor edits the Scope details with In Scope as Yes
    And The user should see the text in the page    In scope? Yes
    And The user should see the element    css=#form-input-46 div.column-third div

Navigation link should not appear for questions that are not part of an assessment
    [Documentation]    INFUND-4264
    [Tags]    Pending
    Given the user navigates to the page    ${Assessment_overview_9}
    When the user clicks the button/link    link=Application details
    Then The user should see the element    css=#content .next .pagination-part-title
    And the user clicks the button/link    css=#content .next .pagination-part-title
    And The user should see the text in the page    Project summary
    Then the user clicks the button/link    css=#content .next .pagination-part-title
    And The user should see the text in the page    Public description
    Then the user clicks the button/link    css=#content .next .pagination-part-title
    And The user should see the text in the page    Scope
    Then the user clicks the button/link    css=#content .next .pagination-part-title
    And The user should see the text in the page    How many
    And the user should not see the element    css=#content .next .pagination-part-title

*** Keywords ***
the Assessor fills in application questions
    The user should see the element    id=assessor-question-score
    Select From List By Index    id=assessor-question-score    9
    The user should see the element    css=#form-input-195 .inPlaceholderMode
    Input Text    css=#form-input-195 .inPlaceholderMode    This is to test the feedback entry.

the text should be visible
    Wait Until Element Contains    css=#form-input-195 .editor    This is to test the feedback entry.

the Assessor edits the application question
    Select From List By Index    id=assessor-question-score    3
    Input Text    css=#form-input-195 .editor    This is to test the feedback entry is modified.

the modified text should be visible
    wait until element contains    css=#form-input-195 .editor    This is to test the feedback entry is modified.

the word count should be calculated correctly
    Wait Until Element Contains    css=#form-input-195 .textarea-footer > span    91

the Assessor enters more than 100 in feedback
    Input Text    css=#form-input-195 .editor    This is to test the feedback entry is modified. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.
    Wait Until Element Contains    css=#form-input-195 .textarea-footer > span    -30

the word count should remain the same
    Wait Until Element Contains    css=#form-input-195 .textarea-footer > span    -30

the Assessor fills in Scope details with In Scope as NO
    The user should see the element    id=research-category
    Select From List By Index    id=research-category   1
    Mouse Out     id=research-category
    Click Element    xpath=//input[@type='radio' and @name='formInput[192]' and (@value='false' or @id='formInput1922')]
    Input Text    css=#form-input-193 .editor.inPlaceholderMode    Testing feedback field when "No" is selected.
    the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")

the Assessor edits the Scope details with In Scope as Yes
    the user clicks the button/link    link=Scope
    Select From List By Index    id=research-category   2
    Mouse Out     id=research-category
    Click Element    xpath=//input[@type='radio' and @name='formInput[192]' and (@value='true' or @id='formInput1921')]
    Input Text    css=#form-input-193 .editor.isModified    Testing feedback field when "No" is selected. Also, testing feedback field when "Yes" is selected.
    the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")





