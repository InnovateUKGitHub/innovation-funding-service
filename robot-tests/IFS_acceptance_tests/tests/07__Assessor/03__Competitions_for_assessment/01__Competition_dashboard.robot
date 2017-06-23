*** Settings ***
Library    String
Documentation     INFUND-1188 As an assessor I want to be able to review my assessments from one place so that I can work in my favoured style when reviewing
...
...               INFUND-3723 As an Assessor looking at my competition assessment dashboard I can see details for the competition, so that I am able to reference key information as I want.
...
...               INFUND-1180 As an Assessor I want to accept or decline an assignment of an application to assess so that the competitions team can manage the assessment process.
...
...               INFUND-4128 As an assessor I want the status of pending assignments to assess to update when I accept them so that I can see what I have committed to
...
...               INFUND-3726 As an Assessor I can select one or more assessments to submit so that I can work in my preferred way
...
...               INFUND-6040 As an assessor I want to see applications sorted by status in my competition dashboard so that I can clearly see applications that are pending, open and assessed
...
...               INFUND-3724 As an Assessor and I am looking at my competition assessment dashboard, I can review the status of applications that I am allocated so that I can track my work
...
...               INFUND-3725 As an Assessor I want to see the scores that I have given for applications I have completed assessing so that I can compare all the applications I am assessing.
...
...               INFUND-4797 Handle scenario where invitation to assess an application has been removed from this user before they have responded
...
...               INFUND-5494 An assessor CAN follow a link to the competition brief from the competition dashboard
Suite Setup       Guest user log-in in new browser  &{assessor2_credentials}
Suite Teardown    The user closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot
*** Variables ***

#${commentText} = Generate Random String  100  [LETTERS]

*** Test Cases ***
User cannot accept/reject an invite to an application that has been withdrawn
    [Documentation]    INFUND-4797
    [Tags]
    When the user navigates to the page    ${server}/assessment/${WITHDRAWN_ASSESSMENT}/assignment
    Then the user should see the text in the page    Invitation withdrawn
    [Teardown]    the user clicks the button/link    jQuery=#proposition-links a:contains(My dashboard)

Competition link should navigate to the applications
    [Documentation]    INFUND-3716
    [Tags]    HappyPath
    [Setup]
    When The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    Then The user should see the text in the page    Applications for assessment

Calculation of the applications for assessment should be correct
    Then the total calculation in dashboard should be correct    Applications for assessment    //div/form/div/ul/li

Details of the competition are visible
    [Documentation]    INFUND-3723
    [Tags]    HappyPath
    Then the user should see the text in the page    Competition
    And the user should see the text in the page    Innovation Lead
    And the user should see the text in the page    Ian Cooper
    And the user should see the text in the page    Accept applications deadline
    And the user should see the text in the page    Submit applications deadline
    And the user should see the text in the page    12:00pm Thursday 12 January 2068
    And the user should see the text in the page    12:00pm Saturday 28 January 2068

Competition brief link can be seen
    [Documentation]    INFUND-5494
    [Tags]
    Then The user opens the link in new window   View competition brief

User can view the competition brief
    [Documentation]    INFUND-5494
    [Tags]
    When The user opens the link in new window   View competition brief
    Then the user should get a competition brief window
    And the user should not see an error in the page
    And the user should see the text in the page    ${IN_ASSESSMENT_COMPETITION_NAME}
    And the user should see the text in the page    Competition opens
    And the user should see the text in the page    Competition closes
    And the user should see the element    jQuery=.button:contains("Start new application")
    [Teardown]    the user closes the competition brief

Applications should have correct status and order
    [Documentation]    INFUND-6040
    ...
    ...    INFUND-3724
    ...
    ...    INFUND-3725
    ...
    ...    INFUND-6358
    Then the order of the applications should be correct according to the status
    And The user should not see the text in the page    Overall score

Accept an application for assessment
    [Documentation]    INFUND-1180
    ...
    ...    INFUND-4128
    [Tags]    HappyPath
    Then the user should see the element    jQuery=.in-progress li:nth-child(1):contains("Intelligent water system"):contains("Pending")
    When The user clicks the button/link    jQuery=.in-progress li:nth-child(1) a:contains("Accept or reject")
    And the user should see the text in the page    Accept application
    And the user selects the radio button  assessmentAccept  true
    And The user clicks the button/link    jQuery=button:contains("Confirm")
    Then the user should be redirected to the correct page    ${Assessor_application_dashboard}
    And the user should see the element    jQuery=.in-progress li:nth-child(3):contains("Intelligent water system"):contains("Accepted")

Reject an application for assessment
    [Documentation]    INFUND-1180
    ...
    ...    INFUND-4128
    ...
    ...    INFUND-6358
    [Tags]
    [Setup]    Log in as a different user    &{assessor_credentials}
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user should see the element    jQuery=.in-progress li:nth-child(1):contains("Park living"):contains("Pending")
    When The user clicks the button/link    jQuery=.in-progress li:nth-child(1) a:contains("Accept or reject")
    And the user should see the text in the page    Accept application
    And the user should not see the element    id=rejectComment
    And the user selects the radio button  assessmentAccept  false
    And The user clicks the button/link    jQuery=button:contains("Confirm")
    Then the user should see an error    Please enter a reason.
    And the assessor fills all fields with valid inputs
    And the user clicks the button/link    jQuery=.button:contains("Confirm")
    And the application for assessment should be removed

Applications should not have a check-box when the status is Open
    [Documentation]    INFUND-3726
    Then The user should not see the element    css=.assessment-submit-checkbox

Check the comp admin see the assessor has rejected the application
    [Tags]
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    Given the user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link    jQuery=a:contains("Manage assessments")
    And the user clicks the button/link    jQuery=a:contains("Manage applications")
    And the user should see the element    jQuery=tr:nth-child(1) td:nth-child(2):contains("Park living")
    And the user clicks the button/link    jQuery=tr:nth-child(1) a:contains(View progress)
    And the user should see the text in the page    Rejected (1)
    And the user should see the element    jQuery=.assessors-rejected td:nth-child(6):contains("Not my area of expertise")
    And the user should see the element    jQuery=.assessors-rejected td:nth-child(6):contains("Unable to assess the application as i'm on holiday.")

*** Keywords ***
random stuff
    ${commentText} = Generate Random String  100  [LETTERS]
    Set suite variable  ${commentText}

the assessor fills all fields with valid inputs
    Select From List By Index    id=rejectReason    2
    The user should not see the text in the page    Please enter a reason
#    The user enters text to a text field    id=rejectComment    Hello all, Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco ullamcoullamco ullamco ullamco
    random stuff
    the user enters text to a text field     id=rejectComment   ${commentText}
    the user moves focus to the element    jQuery=.button:contains("Confirm")
    The user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    The user enters text to a text field    id=rejectComment    Unable to assess the application as i'm on holiday.

the application for assessment should be removed
    The user should not see the element    link=Park living

The order of the applications should be correct according to the status
    element should contain    css=li:nth-child(1) .msg-deadline-waiting    Pending
    element should contain    css=li:nth-child(2) .msg-deadline-waiting    Pending
    element should contain    css=li:nth-child(3) .msg-deadline-waiting    Pending
    element should contain    css=.progress-list li:nth-child(4) .msg-progress    Accepted
    element should contain    css=.progress-list li:nth-child(5) .msg-progress    Accepted
    element should contain    css=.progress-list li:nth-child(6) .msg-progress    Accepted

The user should get a competition brief window
    Select Window   title=Competition Overview - Innovation Funding Service

The user closes the competition brief
    Close Window
    Select Window