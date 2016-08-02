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
Word count functionality and validation
    [Documentation]    INFUND-3402
    [Tags]
    [Setup]    guest user log-in    paul.plum@gmail.com    Passw0rd
    Given the user navigates to the page    ${Application_question_url}
    When the Assessor fills the application question
    Then the word count should be calculated correctly
    When the Assessor enters more than 100 in feedback
    # TODO pending due to INFUND-4352
   # Then The user should see an error    Maximum word count exceeded

Scope application question
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

Autosave and edit the Application questions
    [Documentation]    INFUND-3400
    [Tags]    Pending
    # TODO pending as the autosave is not implemented. It will be done in sprint 13
    Given the user navigates to the page    ${Application_question_url}
    When the Assessor edits the application question
    And the user reloads the page
    Then the modified text should be visible

*** Keywords ***
the Assessor fills the application question
    Select From List By Index    id=assessor-question-score    3
   # focus    css=#form-input-195 .isModified
   # Clear Element Text    css=#form-input-195 .isModified
    Input Text    css=#form-input-195 .inPlaceholderMode    This is to test the feedback entry.
   # sleep    1s

the modified text should be visible
    wait until element contains    css=#form-input-195 .isModified    This is to test the feedback entry is editable.

the Assessor fills in Scope details with In Scope as NO
    The user should see the element    id=research-category
    Select From List By Index    id=research-category   2
    # TODO the functionality is not implemented INFUND-4350
   # the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")
   # The user should see an error    In scope must be selected
    Select Radio Button    name=formInput[192]    No
    # TODO the functionality is not implemented INFUND-4350
   # the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")
    # The user should see an error     "feedback cannot be empty"
    The user enters text to a text field    css=#form-input-193 .editor.isModified    Testing feedback field when "No" is selected.
  #  Wait Until Page Does Not Contain    "feedback cannot be empty"  #TODO INFUND-4350
    the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")

the Assessor edits the Scope details with In Scope as Yes
   # Select From List By Index    id=research-category   3
    the user selects the radio button    name=formInput[192]    Yes
    Clear Element Text    css=#form-input-193 .editor.isModified
    # TODO INFUND-4350
   # The user should not see the text in the page    "feedback cannot be empty"
    the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")

the word count should be calculated correctly
    Wait Until Element Contains    css=#form-input-195 .textarea-footer > span    93

the Assessor enters more than 100 in feedback
    Input Text    css=#form-input-195 .isModified    This is to test the feedback entry is editable. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.
    Wait Until Element Contains    css=#form-input-195 .textarea-footer > span    -30


