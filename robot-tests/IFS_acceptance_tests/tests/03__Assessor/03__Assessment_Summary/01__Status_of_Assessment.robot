*** Settings ***
Documentation     INFUND-550: As an assessor I want the ‘Assessment summary’ page to show me complete and incomplete sections, so that I can easily judge how much of the application is left to do
...
...               INFUND-1485: As an Assessor I want to be able to provide my final feedback for the application so that I can tell Innovate UK whether or not I recommend the application for funding.
Suite Setup       guest user log-in    felix.wilson@gmail.com    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
To verify all the sections are present
    [Documentation]    INFUND-4648
    [Tags]    HappyPath
    When The user navigates to the assessor page    ${Assessment_summary_incomplete_12}
    Then The user should see the element    jQuery=h2:contains("Overall scores")
    And The user should see the element    jQuery=h2:contains("Review assessment")
    And The user should see the element    jQuery=span:contains("Do you believe that this application is suitable for funding?")
    And The user should see the element    id=form-label-feedback
    And The user should see the element    id=form-label-comments

Assessment summary shows questions as incomplete
    [Documentation]    INFUND-550
    Given The user navigates to the assessor page    ${Assessment_summary_incomplete_12}
    Then the collapsible button should contain    jQuery=button:contains(1. How many)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(2. Mediums)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(3. Preference)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(4. Attire)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(Scope)    Incomplete

Assessment summary shows the questions without score
    [Documentation]    INFUND-550
    Then the collapsible button should contain    jQuery=button:contains(1. How many)    N/A
    And the collapsible button should contain    jQuery=button:contains(2. Mediums)    N/A
    And the collapsible button should contain    jQuery=button:contains(3. Preference)    N/A
    And the collapsible button should contain    jQuery=button:contains(4. Attire)    N/A
    [Teardown]    logout as user

Assessment summary shows questions as complete
    [Documentation]    INFUND-550
    [Tags]    HappyPath
    [Setup]    log in as user    &{assessor_credentials}
    Given the user adds score and feedback for every question
    When the user clicks the button/link    link=Review assessment
    Then The user should be redirected to the correct page    ${Assessment_summary_complete_9}
    Then the collapsible button should contain    jQuery=button:contains(1. How many)    Complete
    And the collapsible button should contain    jQuery=button:contains(2. Mediums)    Complete
    And the collapsible button should contain    jQuery=button:contains(3. Preference)    Complete
    And the collapsible button should contain    jQuery=button:contains(4. Attire)    Complete
    And the collapsible button should contain    jQuery=button:contains(Scope)    Complete

Assessment summary shows questions scores
    [Documentation]    INFUND-550
    [Tags]    HappyPath
    Then The user should see the text in the page    Total: 50/50
    And The user should see the text in the page    100%
    And the table should show the correct scores
    And the collapsible button should contain    jQuery=button:contains(1. How many)    Score: 20/20
    And the collapsible button should contain    jQuery=button:contains(2. Mediums)    Score: 10/10
    And the collapsible button should contain    jQuery=button:contains(3. Preference)    Score: 10/10
    And the collapsible button should contain    jQuery=button:contains(4. Attire)    Score: 10/10

Overall scores section
    [Documentation]    INFUND-4648
    Then each question will contain links to respective questions
    And the scores under each question should be correct
    And Element should contain    css=div:nth-child(5) p.no-margin strong    Total: 50/50
    And Element should contain    css=div:nth-child(5) p:nth-child(2) strong    100%

Assessment summary shows feedback in each section
    [Documentation]    INFUND-550
    When The user clicks the button/link    jQuery=button:contains(1. How many)
    Then The user should see the text in the page    Testing how many feedback text
    When The user clicks the button/link    jQuery=button:contains(2. Mediums)
    Then The user should see the text in the page    Testing Mediums feedback text
    When The user clicks the button/link    jQuery=button:contains(3. Preference)
    Then The user should see the text in the page    Testing Preferences feedback text
    When The user clicks the button/link    jQuery=button:contains(4. Attire)
    Then The user should see the text in the page    Testing Attire feedback text
    When The user clicks the button/link    jQuery=button:contains(Scope)
    Then The user should see the text in the page    Testing scope feedback text

Assessor should be able to re-edit before submit
    [Documentation]    INFUND-3400
    When The user clicks the button/link    jQuery=#collapsible-1 a:contains(Return to this question)
    and The user should see the text in the page    This is the applicant response from Test One for How Many
    When the user selects the option from the drop-down menu    8    id=assessor-question-score
    And the user enters text to a text field    css=#form-input-195 .editor    This is a new feedback entry.
    And the user clicks the button/link    jQuery=a:contains(Back to assessment overview)
    And The assessor navigates to the summary page
    When The user clicks the button/link    jQuery=button:contains(1. How many)
    Then the user should see the text in the page    This is a new feedback entry.
    And the user should see the text in the page    8

Assessor must Provide feedback when "No" is selected for funding suitability
    [Documentation]    INFUND-1485
    Given The user navigates to the assessor page    ${Assessment_summary_complete_9}
    When The user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then The user should see an error    Please indicate your decision
    When the assessor selects the radio button "No"
    And The user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then The user should see an error    Please enter your feedback
    And The user enters text to a text field    id=form-textarea-feedback    Testing the feedback word count
    And The user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then The user should not see the text in the page    Please enter your feedback
    And The user enters text to a text field    id=form-textarea-comments    Testing the feedback inputs for optional feedback textarea.

Word count check: Your feedback
    [Documentation]    INFUND-1485
    [Tags]    HappyPath
    When The user navigates to the assessor page    ${Assessment_summary_complete_9}
    When The user enters text to a text field    id=form-textarea-feedback    Testing the feedback word count. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco ullamcoullamco ullamco
    Then the word count should be correct    Words remaining: -4
    And The user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then The user should see an error    This field cannot contain more than 255 characters
    And The user enters text to a text field    id=form-textarea-feedback    Testing the feedback word count
    Then the word count should be correct    Words remaining: 95

Word count check: Comments for InnovateUK
    [Documentation]    INFUND-1485
    When The user navigates to the assessor page    ${Assessment_summary_complete_9}
    And The user enters text to a text field    id=form-textarea-comments    Testing the feedback inputs for optional feedback textarea. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco ullamcoullamco ullamco
    Then the word count should be correct    Words remaining: -7
    And The user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then The user should see an error    This field cannot contain more than 255 characters
    And The user enters text to a text field    id=form-textarea-comments    Testing the feedback inputs for optional feedback textarea.
    Then the word count should be correct    Words remaining: 92
    [Teardown]    Logout as user

*** Keywords ***
The assessor navigates to the summary page
    Given the user navigates to the page    ${Assessment_overview_9}
    When The user clicks the button/link    jQuery=.button:contains(Review assessment)
    And The user should see the text in the page    Assessment summary

the collapsible button should contain
    [Arguments]    ${BUTTON}    ${TEXT}
    Element Should Contain    ${BUTTON}    ${TEXT}

the user adds score and feedback for every question
    Given the user navigates to the page    ${Assessment_overview_9}
    And the user clicks the button/link    link=Scope
    When the user selects the option from the drop-down menu    Technical feasibility studies    id=research-category
    And the user clicks the button/link    jQuery=label:contains(Yes)
    And The user enters text to a text field    css=#form-input-193 .editor    Testing scope feedback text
    wait until page contains    Saving
    the user clicks the button/link    css=.next
    the user selects the option from the drop-down menu    20    id=assessor-question-score
    the user enters text to a text field    css=#form-input-195 .editor    Testing how many feedback text
    wait until page contains    Saving
    the user clicks the button/link    css=.next
    the user selects the option from the drop-down menu    10    id=assessor-question-score
    the user enters text to a text field    css=#form-input-219 .editor    Testing Mediums feedback text
    wait until page contains    Saving
    the user clicks the button/link    css=.next
    the user selects the option from the drop-down menu    10    id=assessor-question-score
    the user enters text to a text field    css=#form-input-222 .editor    Testing Preferences feedback text
    wait until page contains    Saving
    the user clicks the button/link    css=.next
    the user selects the option from the drop-down menu    10    id=assessor-question-score
    the user enters text to a text field    css=#form-input-225 .editor    Testing Attire feedback text
    the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")

the table should show the correct scores
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(1)    20
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(2)    10
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(3)    10
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(4)    10

each question will contain links to respective questions
    The user should see the element    link=Q1
    the user clicks the button/link    link=Q1
    The user should be redirected to the correct page    ${Application_question_url}
    the user should see the text in the page    How many
    The user navigates to the page    ${Assessment_summary_complete_9}
    The user should see the element    link=Q2
    the user clicks the button/link    link=Q2
    The user should be redirected to the correct page    ${Application_question_168}
    the user should see the text in the page    Mediums
    The user navigates to the page    ${Assessment_summary_complete_9}
    The user should see the element    link=Q3
    the user clicks the button/link    link=Q3
    The user should be redirected to the correct page    ${Application_question_169}
    the user should see the text in the page    Preferences
    The user navigates to the page    ${Assessment_summary_complete_9}
    The user should see the element    link=Q4
    the user clicks the button/link    link=Q4
    The user should be redirected to the correct page    ${Application_question_170}
    the user should see the text in the page    Attire
    The user navigates to the page    ${Assessment_summary_complete_9}

the scores under each question should be correct
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(1)    20
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(2)    10
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(3)    10
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(4)    10

When the assessor selects the radio button "Yes"
    Click Element    xpath=//input[@type='radio' and @name='fundingConfirmation' and (@value='Yes' or @id='fundingConfirmation1')]

the assessor selects the radio button "No"
    Click Element    xpath=//input[@type='radio' and @name='fundingConfirmation' and (@value='No' or @id='fundingConfirmation2')]

the word count should be correct
    [Arguments]    ${wordCount}
    the user should see the text in the page    ${wordCount}
