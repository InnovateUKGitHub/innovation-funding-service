*** Settings ***
Documentation     INFUND-3780: As an Assessor I want the system to autosave my work so that I can be sure that my assessment is always in its most current state.
...
...               INFUND-3303: As an Assessor I want the ability to reject the application after I have been given access to the full details so I can make Innovate UK aware.
...
...               INFUND-4203: Prevent navigation options appearing for questions that are not part of an assessment
...
...               INFUND-1483: As an Assessor I want to be asked to confirm whether the application is in the correct research category and scope so that Innovate UK know that the application aligns with the competition
...
...               INFUND-3394 Acceptance Test: Assessor should be able to view the full application and finance summaries for assessment
...
...
...               INFUND-550 As an assessor I want the ‘Assessment summary’ page to show me complete and incomplete sections, so that I can easily judge how much of the application is left to do
Suite Setup       guest user log-in    paul.plum@gmail.com    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Assessment summary shows questions as incomplete
    [Documentation]    INFUND-550
    The assessor navigates to the summary page
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

Navigation using next button
    [Documentation]    INFUND-4264
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    When the user clicks the button/link    link=Application details
    Then the user should see the text in the page    Application details
    And the user clicks next and goes to the page    Project summary
    And the user clicks next and goes to the page    Public description
    And the user clicks next and goes to the page    Scope
    And the user clicks next and goes to the page    How many
    And the user clicks next and goes to the page    Mediums
    And the user clicks next and goes to the page    Preferences
    And the user clicks next and goes to the page    Attire
    And the user should not see the element    css=.next

Navigation using previous button
    [Documentation]    INFUND-4264
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    When the user clicks the button/link    link=4. Attire
    Then the user should see the text in the page    Attire
    And the user clicks previous and goes to the page    Preferences
    And the user clicks previous and goes to the page    Mediums
    And the user clicks previous and goes to the page    How many
    And the user clicks previous and goes to the page    Scope
    And the user clicks previous and goes to the page    Public description
    And the user clicks previous and goes to the page    Project summary
    And the user clicks previous and goes to the page    Application details
    And the user should not see the element    css=.prev

Project details sections should not be scorable
    [Documentation]    INFUND-3400
    [Tags]
    When the user clicks the button/link    link=Back to assessment overview
    And the user clicks the button/link    link=Application details
    And the user should see the text in the page    Project title
    Then the user should not see the text in the page    Question score
    When the user clicks the button/link    jQuery=span:contains(Next)
    And the user should see the text in the page    This is the applicant response from Test One for Project Summary.
    Then the user should not see the text in the page    Question score
    When the user clicks the button/link    jQuery=span:contains(Next)
    And the user should see the text in the page    This is the applicant response from Test One for Public Description.
    Then the user should not see the text in the page    Question score
    And the user clicks the button/link    jQuery=span:contains(Next)
    And the user should see the text in the page    This is the applicant response from Test One for Scope.
    Then the user should not see the text in the page    Question score

Application questions should be scorable
    [Documentation]    INFUND-3400
    When the user clicks the button/link    jQuery=span:contains(Next)
    And The user should see the text in the page    How many balls can you juggle
    Then The user should see the element    jQuery=label:contains(Question score)
    When the user clicks the button/link    jQuery=span:contains(Next)
    And The user should see the text in the page    What mediums can you juggle with
    Then The user should see the element    jQuery=label:contains(Question score)
    When the user clicks the button/link    jQuery=span:contains(Next)
    And The user should see the text in the page    What is your preferred juggling pattern
    Then The user should see the element    jQuery=label:contains(Question score)
    When the user clicks the button/link    jQuery=span:contains(Next)
    And The user should see the text in the page    What do you wear when juggling
    Then The user should see the element    jQuery=label:contains(Question score)

Choosing 'not in scope' should update on the overview page
    [Documentation]    INFUND-1483
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    And the user clicks the button/link    link=Scope
    When the user selects the option from the drop-down menu    Technical feasibility studies    id=research-category
    And the user clicks the button/link    jQuery=label:contains(No)
    And the user clicks the button/link    link=Back to assessment overview
    And the user should see the text in the page    In scope? No
    Then The user should not see the element    css=.column-third > img    #green flag

Scope: Autosave
    [Documentation]    INFUND-1483
    ...
    ...    INFUND-3780
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    And the user clicks the button/link    link=Scope
    When the user selects the option from the drop-down menu    Technical feasibility studies    id=research-category
    And the user clicks the button/link    jQuery=label:contains(No)
    And The user enters text to a text field    css=#form-input-193 .editor    Testing feedback field when "No" is selected.
    And the user clicks the button/link    jQuery=a:contains(Back to assessment overview)
    Then the user should see the text in the page    In scope? No
    And the user clicks the button/link    link=Scope
    And the user should see the text in the page    Technical feasibility studies
    And the user should see the text in the page    Testing feedback field when "No" is selected.

Scope: Word count
    [Documentation]    INFUND-1483
    ...
    ...    INFUND-3400
    [Tags]
    When the user enters text to a text field    css=#form-input-193 .editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco ullamcoullamco ullamco ullamco
    Then the user should see the text in the page    Words remaining: 0

Scope: Status in the overview
    [Documentation]    INFUND-1483
    [Tags]
    When the user clicks the button/link    jQuery=label:contains(Yes)
    And the user clicks the button/link    jquery=button:contains("Save and return to assessment overview")
    And the user should see the text in the page    In scope? Yes
    And the user should see the element    css=.column-third > img    #green flag

Question 1: Autosave
    [Documentation]    INFUND-3780
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    And the user clicks the button/link    link=1. How many
    When the user selects the option from the drop-down menu    9    id=assessor-question-score
    And the user enters text to a text field    css=#form-input-195 .editor    This is to test the feedback entry.
    And the user clicks the button/link    jQuery=a:contains(Back to assessment overview)
    And the user clicks the button/link    link=1. How many
    Then the user should see the text in the page    This is to test the feedback entry.
    And the user should see the text in the page    9

Question 1: Word count
    [Documentation]    INFUND-3400
    When the user enters text to a text field    css=#form-input-195 .editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco ullamcoullamco ullamco ullamco one
    Then the user should see the text in the page    Words remaining: -1
    When the user enters text to a text field    css=#form-input-195 .editor    Test text
    Then the user should see the text in the page    Words remaining: 98

Finance overview
    [Documentation]    INFUND-3394
    [Tags]
    Given the user navigates to the page    ${Assessment_overview_9}
    When the user clicks the button/link    link=Finances overview
    Then the user should see the text in the page    Finances summary
    And the user should not see the element    css=input
    And the finance summary total should be correct
    And the project cost breakdown total should be correct
    And the user clicks the button/link    link=Back to assessment overview
    And the user should be redirected to the correct page    ${Assessment_overview_9}
    [Teardown]

Validation check in the Reject application modal
    [Documentation]    INFUND-3540
    [Tags]    Failing
    Given the user navigates to the page    ${Assessment_overview_11}
    And the user clicks the button/link    jQuery=.summary:contains("Unable to assess this application")
    And the user clicks the button/link    link=Reject this application
    When the user clicks the button/link    jquery=button:contains("Reject")
    Then the user should see an error    This field cannot be left blank
    And the user should see the element    id=rejectReason
    Then the user selects the option from the drop-down menu    ${empty}    id=rejectReason    # Note that using this empty option will actually select the 'Select a reason' option at the top of the dropdown menu
    And the user should see an error    This field cannot be left blank
    Then the user enters text to a text field    id=rejectComment    Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut

Assessment summary shows questions as complete
    [Documentation]    INFUND-550
    Given the user adds score and feedback for every question
    When The assessor navigates to the summary page
    Then the collapsible button should contain    jQuery=button:contains(1. How many)    Complete
    And the collapsible button should contain    jQuery=button:contains(2. Mediums)    Complete
    And the collapsible button should contain    jQuery=button:contains(3. Preference)    Complete
    And the collapsible button should contain    jQuery=button:contains(4. Attire)    Complete
    And the collapsible button should contain    jQuery=button:contains(Scope)    Complete

Assessment summary shows questions scores
    [Documentation]    INFUND-550
    Then The user should see the text in the page    Total: 50/50
    And The user should see the text in the page    100%
    And the table should show the correct scores
    And the collapsible button should contain    jQuery=button:contains(1. How many)    Score: 20/20
    And the collapsible button should contain    jQuery=button:contains(2. Mediums)    Score: 10/10
    And the collapsible button should contain    jQuery=button:contains(3. Preference)    Score: 10/10
    And the collapsible button should contain    jQuery=button:contains(4. Attire)    Score: 10/10

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

*** Keywords ***
the user clicks next and goes to the page
    [Arguments]    ${page_content}
    the user clicks the button/link    css=.next
    the user should see the text in the page    ${page_content}

the user clicks previous and goes to the page
    [Arguments]    ${page_content}
    the user clicks the button/link    css=.prev
    the user should see the text in the page    ${page_content}

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

The assessor navigates to the summary page
    Given the user navigates to the page    ${Assessment_overview_9}
    When The user clicks the button/link    jQuery=.button:contains(Review assessment)
    And The user should see the text in the page    Assessment summary

the table should show the correct scores
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(1)    20
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(2)    10
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(3)    10
    Element should contain    css=.table-overflow tr:nth-of-type(2) td:nth-of-type(4)    10
