*** Settings ***
Documentation     INFUND-3780: As an Assessor I want the system to autosave my work so that I can be sure that my assessment is always in its most current state.
...
...               INFUND-3303: As an Assessor I want the ability to reject the application after I have been given access to the full details so I can make Innovate UK aware.
...
...               INFUND-4203: Prevent navigation options appearing for questions that are not part of an assessment
...
...               INFUND-1483: As an Assessor I want to be asked to confirm whether the application is in the correct research category and scope so that Innovate UK know that the application aligns with the competition
Suite Setup       guest user log-in    paul.plum@gmail.com    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Pending    Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Autosave and edit the Application question - How many
    [Documentation]    INFUND-3552
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    When the user clicks the button/link    link=1. How many
    Then The user should see the text in the page    Please review the answer provided and score the answer out of 20 points.
    And the Assessor fills in application questions
    And the user reloads the page
    And the text should be visible
    Then the Assessor edits the application question
    And the user reloads the page
    And the modified text should be visible

Feedback should accept up to 100 words
    [Documentation]    INFUND-3402
    [Tags]
    Given the user navigates to the page    ${Application_question_url}
    Then the word count should be calculated correctly
    When the Assessor enters more than 100 in feedback
    And the user reloads the page
    # TODO remove the comment after INFUND-4427 is fixed
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
    [Tags]
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

Non-scorable question cannot be scored/edited
    [Documentation]    INFUND-3400
    [Tags]
    When the user clicks the button/link    link=Application details
    And The user should see the text in the page    Project title
    Then The user should not see the element    jQuery=label:contains(Question score)
    And The user should not see the text in the page    Question score
    And The user clicks the button/link    jQuery=span:contains(Next)
    And The user should see the text in the page    second answer
    Then The user should not see the element    jQuery=label:contains(Question score)
    And The user should not see the text in the page    Question score
    And The user clicks the button/link    jQuery=span:contains(Next)
    And The user should see the text in the page    third answer
    Then The user should not see the element    jQuery=label:contains(Question score)
    And The user should not see the text in the page    Question score
    And The user clicks the button/link    jQuery=span:contains(Next)
    And The user should see the text in the page    fourth answer
    Then The user should not see the element    jQuery=label:contains(Question score)
    And The user should not see the text in the page    Question score

Finance summary
    [Documentation]    INFUND-3394
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    When the user clicks the button/link    link=Finances overview
    Then The user should see the text in the page    Finances summary
    And the user should not see the element    css=input
    And the finance summary total should be correct
    And the project cost breakdown total should be correct
    And the user clicks the button/link    link=Back to assessment overview
    And The user should be redirected to the correct page    ${Assessment_overview_9}
    [Teardown]    Logout as user

Validation check in the Reject application modal
    [Documentation]    INFUND-3540
    [Tags]    Pending
    # TODO or pending due to INFUND-4375
    Given the user navigates to the page    ${Assessment_overview_11}
    And the user clicks the button/link    css=#content .extra-margin details summary
    And the user clicks the button/link    css=#details-content-0 button
    When the user clicks the button/link    jquery=button:contains("Reject")
    Then the user should see an error    This field cannot be left blank
    And the user should see the element    id=rejectReason
    Then Select From List By value    id=rejectReason    Please select one reason
    And the user should see an error    This field cannot be left blank
    Then the user enters text to a text field    id=rejectComment    Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut
    [Teardown]

*** Keywords ***
the Assessor fills in application questions
    The user should see the element    id=assessor-question-score
    Select From List By Index    id=assessor-question-score    9
    The user should see the element    css=#form-input-195 .inPlaceholderMode
    Input Text    css=#form-input-195 .inPlaceholderMode    This is to test the feedback entry.
    Sleep    500ms

the text should be visible
    Wait Until Element Contains    css=#form-input-195 .editor    This is to test the feedback entry.

the Assessor edits the application question
    Select From List By Index    id=assessor-question-score    3
    Input Text    css=#form-input-195 .editor    This is to test the feedback entry is modified.
    Sleep    500ms

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
    Select From List By Index    id=research-category    1
    Mouse Out    id=research-category
    Click Element    xpath=//input[@type='radio' and @name='formInput[192]' and (@value='false' or @id='formInput1922')]
    Input Text    css=#form-input-193 .editor.inPlaceholderMode    Testing feedback field when "No" is selected.
    the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")

the Assessor edits the Scope details with In Scope as Yes
    the user clicks the button/link    link=Scope
    Select From List By Index    id=research-category    2
    Mouse Out    id=research-category
    Click Element    xpath=//input[@type='radio' and @name='formInput[192]' and (@value='true' or @id='formInput1921')]
    # TODO the following locator is not recognised, can someone take a look at it please
    # Mouse Out    xpath=//input[@type='radio' and @name='formInput[192]' and (@value='true' or @id='formInput1921')]
    # Focus    css=#form-input-193 .editor.isModified
    # Input Text    css=#form-input-193 .editor.isModified    Testing feedback field when "No" is selected. Also, testing feedback field when "Yes" is selected.
    the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")

the finance summary total should be correct
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(2) td:nth-child(2)    £7,680
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(1) td:nth-child(3)    60%
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(2) td:nth-child(4)    £4,608
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(2) td:nth-child(5)    £0
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(2) td:nth-child(6)    £3,072

the project cost breakdown total should be correct
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(2)    £7,680
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(3)    £6,400
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(4)    £1,280
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(5)    £0
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(6)    £0
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(7)    £0
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(8)    £0
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(9)    £0
