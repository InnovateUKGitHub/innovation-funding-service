*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess..
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition..
...
...               INFUND-304: As an assessor I want to be able to accept the invitation for a competition..
...
...               INFUND-3716: As an Assessor when I have accepted to assess within a competition and the assessment period is current, I can see the number of competitions and their titles on my dashboard...
...
...               INFUND-3720 As an Assessor I can see deadlines for the assessment of applications currently in assessment on my dashboard...
...
...               INFUND-5157 Add missing word count validation when rejecting an application for assessment
...
...               INFUND-3718 As an Assessor I can see all the upcoming competitions that I have accepted to assess...
...
...               INFUND-5165 As an assessor attempting to accept/reject an invalid invitation to assess in a competition, I will receive a notification that I cannot reject the competition..
...
...               INFUND-5001 As an assessor I want to see information about competitions that I have accepted to assess...
...
...               INFUND-5509 As an Assessor I can see details relating to work and payment...
...
...               INFUND-943 As an assessor I have to accept invitations to assess a competition within a timeframe...
...
...               INFUND-6500 Speedbump when not logged in and attempting to accept invite where a user already exists
...
...               INFUND-6455 As an assessor with an account, I can see invitations to assess competitions on my dashboard...
...
...               INFUND-6450 As a member of the competitions team, I can see the status of each assessor invite s0...
...
...               INFUND-5494 An assessor CAN follow a link to the competition brief from the competition dashboard
Suite Setup       log in as user    &{existing_assessor1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${Invitation_existing_assessor1}    ${server}/assessment/invite/competition/dcc0d48a-ceae-40e8-be2a-6fd1708bd9b7
${Invitation_for_upcoming_comp_assessor1}    ${server}/assessment/invite/competition/1ec7d388-3639-44a9-ae62-16ad991dc92c
${Invitation_nonexisting_assessor2}    ${server}/assessment/invite/competition/396d0782-01d9-48d0-97ce-ff729eb555b0 #invitation for assessor:${test_mailbox_one}+david.peters@gmail.com
${ASSESSOR_DASHBOARD}    ${server}/assessment/assessor/dashboard
${Correct_date}    12 January to 29 January
${Correct_date_start}    12 January
${Correct_date_end}    29 January

*** Test Cases ***
Assessor dashboard contains the correct competitions
    [Documentation]    INFUND-3716
    ...
    ...    INFUND-4950
    ...
    ...    INFUND-6899
    [Tags]    HappyPath
    [Setup]
    Given the user should see the text in the page    Assessor dashboard
    Then The user should not see the text in the page    Competitions for assessment
    And The user should see the text in the page    Upcoming competitions to assess
    And The user should see the text in the page    ${UPCOMING_COMPETITION_TO_ASSESS_NAME}
    And The user should see the text in the page    Invitations to assess

Competition brief link can be seen
    [Documentation]    INFUND-5494
    [Tags]
    When the user clicks the button/link    link=${UPCOMING_COMPETITION_TO_ASSESS_NAME}
    Then the user should see the element    link=See competition brief (opens in a new window)

User can view the competition brief
    [Documentation]    INFUND-5494
    [Tags]
    When the user clicks the button/link    link=See competition brief (opens in a new window)
    Then the user should get a competition brief window
    And the user should not see an error in the page
    And the user should see the text in the page    ${UPCOMING_COMPETITION_TO_ASSESS_NAME}
    And the user should see the text in the page    Competition opens
    And the user should see the text in the page    Competition closes
    And the user should see the text in the page    Or go to your dashboard to continue an existing application.
    And the user should see the element    jQuery=.button:contains("Start new application")
    And The user closes the competition brief
    And the user clicks the button/link    link=Assessor dashboard
    [Teardown]

Calculation of the Upcoming competitions and Invitations to assess should be correct
    [Documentation]    INFUND-7107
    ...
    ...    INFUND-6455
    [Tags]    HappyPath
    Then the total calculation in dashboard should be correct    Upcoming competitions to assess    //*[@class="upcoming-to-assess"]/div/ul/li
    And the total calculation in dashboard should be correct    Invitations to assess    //*[@class="invite-to-assess"]/div/ul/li

Existing assessor: Reject invitation from Dashboard
    [Documentation]    INFUND-4631
    ...
    ...    INFUND-5157
    ...
    ...    INFUND-6455
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Photonics for health
    And the user should see the text in the page    Invitation to assess '${READY_TO_OPEN_COMPETITION_NAME}'
    And the user should see the text in the page    You are invited to assess the competition '${READY_TO_OPEN_COMPETITION_NAME}'
    And the user clicks the button/link    css=form a
    And the user clicks the button/link    jQuery=button:contains(Cancel)
    And the user should not see the element    id=rejectComment
    And the user clicks the button/link    css=form a
    And The user enters text to a text field    id=rejectComment    a a a a a a a a \\ a a a a \\ a a a a a a \\ a a a a a \\ a a a a \\ a a a a \\ a a a a a a a a a a a \\ a a \\ a a a a a a a a a a \\ a a a a a a a a a a a a a a a a a a a \\ a a a a a a a \\ a a a \\ a a \\ aa \\ a a a a a a a a a a a a a a \\ a
    And the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see an error    The reason cannot be blank.
    And the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    And the assessor fills all fields with valid inputs
    And the user clicks the button/link    jQuery=button:contains("Reject")
    And the user should see the text in the page    Thank you for letting us know you are unable to assess applications within this competition.

Existing Assessor tries to accept closed competition
    [Documentation]    INFUND-943
    [Tags]
    [Setup]    Close the competition in assessment
    Given Log in as a different user    &{existing_assessor1_credentials}
    Then The user should not see the element    link=Sustainable living models for the future
    And the user navigates to the page    ${Invitation_for_upcoming_comp_assessor1}
    Then The user should see the text in the page    This invitation is now closed
    [Teardown]    Run Keywords    Connect to Database    @{database}
    ...    AND    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE type='ASSESSMENT_CLOSED' AND competition_id=4;

Existing assessor: Accept invitation from the invite link
    [Documentation]    INFUND-228
    ...
    ...    INFUND-304
    ...
    ...    INFUND-3716
    ...
    ...    INFUND-5509
    ...
    ...    INFUND-6500
    [Tags]    HappyPath
    [Setup]    Logout as user
    Given the user navigates to the page    ${Invitation_for_upcoming_comp_assessor1}
    And the user should see the text in the page    You are invited to assess the competition '${IN_ASSESSMENT_COMPETITION_NAME}'.
    And the user should see the text in the page    Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}'
    And the user should see the text in the page    12 January 2068 to 28 January 2068: Assessment period
    And the user should see the text in the page    taking place at 12:00am on 15 April.
    And the user should see the text in the page    100 per application.
    When the user clicks the button/link    jQuery=.button:contains("Yes, create account")
    Then the user should see the text in the page    Your email address is linked to an existing account.
    And the user clicks the button/link    jQuery=a:contains("Click here to sign in")
    And Invited guest user log in    &{existing_assessor1_credentials}
    And The user should see the text in the page    Assessor dashboard
    And the user should see the element    link=${IN_ASSESSMENT_COMPETITION_NAME}

Accepted and Rejected invites are not visible
    [Documentation]    INFUND-6455
    [Tags]
    Then the user should not see the element    link=Photonics for health
    And The user should not see the text in the page    Invitations to assess

Upcoming competition should be visible
    [Documentation]    INFUND-3718
    ...
    ...    INFUND-5001
    [Tags]    HappyPath
    Given the user navigates to the page    ${ASSESSOR_DASHBOARD}
    And the user should see the text in the page    Competitions for assessment
    And the assessor should see the correct date
    When The user clicks the button/link    link=Home and industrial efficiency programme
    And the user should see the text in the page    You have agreed to be an assessor for the upcoming competition 'Home and industrial efficiency programme'
    And The user clicks the button/link    link=Assessor dashboard
    Then The user should see the text in the page    Upcoming competitions to assess

The assessment period starts the comp moves to the comp for assessment
    [Tags]    MySQL    HappyPath
    [Setup]    Connect to Database    @{database}
    Given the assessment start period changes in the db in the past
    Then The user should not see the text in the page    Upcoming competitions to assess
    [Teardown]    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='2018-02-24 00:00:00' WHERE `competition_id`='${UPCOMING_COMPETITION_TO_ASSESS_ID}' and type IN ('OPEN_DATE', 'SUBMISSION_DATE', 'ASSESSORS_NOTIFIED');

Milestone date for assessment submission is visible
    [Documentation]    INFUND-3720
    [Tags]    MySQL
    Then the assessor should see the date for submission of assessment

Number of days remaining until assessment submission
    [Documentation]    INFUND-3720
    [Tags]    MySQL    Pending
    #TO DO Pending due to infund-8925
    Then the assessor should see the number of days remaining
    And the calculation of the remaining days should be correct    2068-01-28

Calculation of the Competitions for assessment should be correct
    [Documentation]    INFUND-3716
    [Tags]    MySQL    HappyPath
    Then the total calculation in dashboard should be correct    Competitions for assessment    //div[3]/div/ul/li

Registered user should not allowed to accept other assessor invite
    [Documentation]    INFUND-4895
    [Tags]
    Given the user navigates to the page    ${Invitation_nonexisting_assessor2}
    When the user clicks the button/link    jQuery=.button:contains("Yes")
    Then The user should see permissions error message

The user should not be able to accept or reject the same applications
    [Documentation]    INFUND-5165
    [Tags]
    Then the assessor shouldn't be able to accept the rejected competition
    And the assessor shouldn't be able to reject the rejected competition
    Then the assessor shouldn't be able to accept the accepted competition
    And the assessor shouldn't be able to reject the accepted competition

The Admin's invites overview should be updated for accepted invites
    [Documentation]    INFUND-6450
    [Tags]
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And The user clicks the button/link    jQuery=a:contains("Invite assessors to assess the competition")
    And The user clicks the button/link    link=Overview
    And the user should see the element    jQuery=tr:nth-child(1) td:contains(Invite accepted)

*** Keywords ***
the assessor fills all fields with valid inputs
    Select From List By Index    id=rejectReason    2
    The user should not see the text in the page    This field cannot be left blank
    The user enters text to a text field    id=rejectComment    Unable to assess this application.

the assessor should see the date for submission of assessment
    the user should see the element    css=.my-applications .msg-deadline .day
    the user should see the element    css=.my-applications .msg-deadline .month

the assessor should see the number of days remaining
    the user should see the element    css=.my-applications .msg-deadline .days-remaining

the assessor shouldn't be able to accept the rejected competition
    When the user navigates to the page    ${Invitation_existing_assessor1}
    the assessor is unable to see the invitation

the assessor shouldn't be able to reject the rejected competition
    When the user navigates to the page    ${Invitation_existing_assessor1}
    the assessor is unable to see the invitation

the assessor shouldn't be able to accept the accepted competition
    When the user navigates to the page    ${Invitation_for_upcoming_comp_assessor1}
    the assessor is unable to see the invitation

the assessor shouldn't be able to reject the accepted competition
    When the user navigates to the page    ${Invitation_for_upcoming_comp_assessor1}
    the assessor is unable to see the invitation

The assessor is unable to see the invitation
    The user should see the text in the page    This invitation is now closed
    The user should see the text in the page    You have already accepted or rejected this invitation.

the assessor should see the correct date
    ${Assessment_period_start}=    Get Text    css=.upcoming-to-assess .standard-definition-list dd:nth-child(2)
    ${Assessment_period_end}=    Get Text    css=.upcoming-to-assess .standard-definition-list dd:nth-child(4)
    Should Be Equal    ${Assessment_period_start}    ${Correct_date_start}
    Should Be Equal    ${Assessment_period_end}    ${Correct_date_end}

Close the competition in assessment
    Log in as a different user    &{Comp_admin1_credentials}
    The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    The user clicks the button/link    jQuery=.button:contains("Close assessment")

The user should get a competition brief window
    Select Window    title=Competition Overview - Innovation Funding Service

The user closes the competition brief
    Close Window
    Select Window
