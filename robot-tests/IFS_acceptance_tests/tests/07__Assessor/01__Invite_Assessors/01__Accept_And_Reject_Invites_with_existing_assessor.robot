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
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${Invitation_existing_assessor1}           ${server}/assessment/invite/competition/dcc0d48a-ceae-40e8-be2a-6fd1708bd9b7
${Invitation_for_upcoming_comp_assessor1}  ${server}/assessment/invite/competition/1ec7d388-3639-44a9-ae62-16ad991dc92c
${Invitation_nonexisting_assessor2}        ${server}/assessment/invite/competition/396d0782-01d9-48d0-97ce-ff729eb555b0
${Correct_date_start}                      ${createApplicationOpenCompetitionAssessorAcceptsDayMonth}
${Correct_date_end}                        ${createApplicationOpenCompetitionAssessorDeadlineDayMonth}
${assessmentPeriod}                        ${IN_ASSESSMENT_COMPETITION_ASSESSOR_ACCEPTS_PRETTY_DATE} to ${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_PRETTY_DATE}: Assessment period

#invitation for assessor:${test_mailbox_one}+david.peters@gmail.com
# ${IN_ASSESSMENT_COMPETITION_NAME} is the Sustainable living models for the future
# ${UPCOMING_COMPETITION_TO_ASSESS_NAME} is the Home and industrial efficiency programme

*** Test Cases ***
Assessor dashboard contains the correct competitions
    [Documentation]    INFUND-3716  INFUND-4950  INFUND-6899
    Given the user should see the element     jQuery = h1:contains(${ASSESSOR_DASHBOARD_TITLE})
    Then The user should not see the element  jQuery = h2:contains("Competitions for assessment")
    And The user should see the element       jQuery = h2:contains("Upcoming competitions to assess") ~ ul a:contains("${UPCOMING_COMPETITION_TO_ASSESS_NAME}")
    And The user should see the element       jQuery = h2:contains("Invitations to assess")

User can view the competition brief
    [Documentation]    INFUND-5494
    Given the user clicks the button/link        link = ${UPCOMING_COMPETITION_TO_ASSESS_NAME}
    When the user clicks the button/link         link = View competition brief (opens in a new window)
    Then The user should get a competition brief window
    And the user should see competition details
    And The user closes the competition brief

Calculation of the Upcoming competitions and Invitations to assess should be correct
    [Documentation]    INFUND-7107  INFUND-6455
    Given the user clicks the button/link                      link = ${ASSESSOR_DASHBOARD_TITLE}
    Then the total calculation in dashboard should be correct  Upcoming competitions to assess    //*[@class = "upcoming-to-assess"]/div/ul/li
    And the total calculation in dashboard should be correct   Invitations to assess    //*[@class = "invite-to-assess"]/div/ul/li

Existing assessor: Reject invitation from Dashboard
    [Documentation]    INFUND-4631  INFUND-5157  INFUND-6455
    Given the user clicks the button/link                   link = ${READY_TO_OPEN_COMPETITION_NAME}
    And the user checks for field validations
    When the assessor fills all fields with valid inputs
    And The user clicks the button/link                     jQuery = button:contains("Confirm")
    Then the user should see the element                    jQuery = p:contains("Thank you for letting us know you are unable to assess applications within this competition.")
    And the assessor shouldn't see Accepted and Rejected invites on dashboard    ${READY_TO_OPEN_COMPETITION_NAME}

Existing Assessor tries to accept expired invitation in closed assessment
    [Documentation]    INFUND-943
    [Setup]    Close the competition in assessment
    Given Log in as a different user               &{existing_assessor1_credentials}
    And the user should not see the element        link = ${IN_ASSESSMENT_COMPETITION_NAME}
    When the user navigates to the page            ${Invitation_for_upcoming_comp_assessor1}
    Then the user should see the element           jQuery = h1:contains("This invitation is now closed")
    [Teardown]  Reset competition's milestone

Existing assessor: Accept invitation from the invite link
    [Documentation]    INFUND-228  INFUND-304  INFUND-3716  INFUND-5509  INFUND-6500  INFUND-6455
    [Setup]    Logout as user
    Given the assessor accepts the invite
    When the user clicks the button/link      jQuery = a:contains("Click here to sign in")
    And Invited guest user log in             &{existing_assessor1_credentials}
    Then the user should see the element      link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And the assessor shouldn't see Accepted and Rejected invites on dashboard   ${IN_ASSESSMENT_COMPETITION_NAME}

Upcoming competition should be visible
    [Documentation]    INFUND-3718  INFUND-5001
    Given the user navigates to the page           ${ASSESSOR_DASHBOARD_URL}
    And the assessor should see the correct date
    When The user clicks the button/link           link = ${UPCOMING_COMPETITION_TO_ASSESS_NAME}
    And the user should see the element            jQuery = p:contains("You have agreed to be an assessor for the upcoming competition '${UPCOMING_COMPETITION_TO_ASSESS_NAME}'")
    And The user clicks the button/link            link = ${ASSESSOR_DASHBOARD_TITLE}
    Then the user should see the element           jQuery = h2:contains("Upcoming competitions to assess")

The assessment period starts the comp moves to the comp for assessment
    [Documentation]  INFUND-3718  INFUND-3720
    Given the assessment start period changes in the db in the past       ${UPCOMING_COMPETITION_TO_ASSESS_ID}
    Then the assessor should see the date for submission of assessment    ${UPCOMING_COMPETITION_TO_ASSESS_ID}
    And the user should not see the element                               jQuery = h2:contains("Upcoming competitions to assess")
    [Teardown]  Reset milestones back to the original values

Number of days remaining until assessment submission
    [Documentation]    INFUND-3720  INFUND-3716
    [Tags]  Failing
    # TODO IFS-3176
    Given the assessor should see the number of days remaining     ${UPCOMING_COMPETITION_TO_ASSESS_ID}
    Then the calculation of the remaining days should be correct   ${UPCOMING_COMPETITION_TO_ASSESS_ASSESSOR_DEADLINE_DATE_SIMPLE}    ${UPCOMING_COMPETITION_TO_ASSESS_ID}
    And the total calculation in dashboard should be correct       Competitions for assessment   //*[@class = "my-applications"]/div/ul/li

Registered user should not allowed to accept other assessor invite
    [Documentation]    INFUND-4895
    Given the user navigates to the page   ${Invitation_nonexisting_assessor2}
    And the user selects the radio button  acceptInvitation  true
    And The user clicks the button/link    jQuery = button:contains("Confirm")
    Then Page Should Contain               ${403_error_message}

The user should not be able to accept or reject the same applications
    [Documentation]    INFUND-5165
    Given the assessor shouldn't be able to accept the rejected competition
    And the assessor shouldn't be able to reject the rejected competition
    Then the assessor shouldn't be able to accept the accepted competition
    And the assessor shouldn't be able to reject the accepted competition

The Admin's invites overview should be updated for accepted invites
    [Documentation]    INFUND-6450
    [Setup]    log in as a different user   &{Comp_admin1_credentials}
    Given The user clicks the button/link   link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And The user clicks the button/link     jQuery = a:contains("Invite assessors to assess the competition")
    When The user clicks the button/link    link = Accepted
    Then the user should see the element    jQuery = tr:contains("Alexis Colon")

*** Keywords ***
the assessor fills all fields with valid inputs
    Select From List By Index                     id = rejectReasonValid    2
    The user enters text to a text field          id = rejectComment    Unable to assess this application.
    the user cannot see a validation error in the page

the assessor should see the date for submission of assessment
    [Arguments]    ${competitionId}
    the user should see the element  css = .my-applications .msg-deadline[data-competition-id = '${competitionId}'] .day
    the user should see the element  css = .my-applications .msg-deadline[data-competition-id = '${competitionId}'] .month

the assessor should see the number of days remaining
    [Arguments]    ${competitionId}
    the user should see the element  css = .my-applications .msg-deadline[data-competition-id = '${competitionId}'] .days-remaining

the assessor shouldn't be able to accept the rejected competition
    the user navigates to the page    ${Invitation_existing_assessor1}
    the assessor is unable to see the invitation

the assessor shouldn't be able to reject the rejected competition
    the user navigates to the page    ${Invitation_existing_assessor1}
    the assessor is unable to see the invitation

the assessor shouldn't be able to accept the accepted competition
    the user navigates to the page    ${Invitation_for_upcoming_comp_assessor1}
    the assessor is unable to see the invitation

the assessor shouldn't be able to reject the accepted competition
    the user navigates to the page    ${Invitation_for_upcoming_comp_assessor1}
    the assessor is unable to see the invitation

The assessor is unable to see the invitation
    The user should see the element           jQuery = h1:contains("This invitation is now closed")
    The user should see the element           jQuery = p:contains("You have already accepted or rejected this invitation.")

the assessor should see the correct date
    ${Assessment_period_start} =    Get Text    css = .upcoming-to-assess .standard-definition-list dd:nth-child(2)
    ${Assessment_period_end} =    Get Text      css = .upcoming-to-assess .standard-definition-list dd:nth-child(4)
    Should Be Equal    ${Assessment_period_start}    ${Correct_date_start}
    Should Be Equal    ${Assessment_period_end}    ${Correct_date_end}

Close the competition in assessment
    Log in as a different user       &{Comp_admin1_credentials}
    The user clicks the button/link  link = ${IN_ASSESSMENT_COMPETITION_NAME}
    The user clicks the button/link  jQuery = .govuk-button:contains("Close assessment")

The user should get a competition brief window
    Select Window       title = Competition overview - Home and industrial efficiency programme - Innovation Funding Service

The user closes the competition brief
    Close Window
    Select Window

Reset competition's milestone
    # That is to reset competition's milestone back to its original value, that was NUll before pressing the button "Close assessment"
    Execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE `type`='ASSESSMENT_CLOSED' AND `competition_id`='${IN_ASSESSMENT_COMPETITION}';

Reset milestones back to the original values
    execute sql string   UPDATE `${database_name}`.`milestone` SET `date`='${UPCOMING_COMPETITION_TO_ASSESS_OPEN_DB}' WHERE `type`='OPEN_DATE' AND `competition_id`='${UPCOMING_COMPETITION_TO_ASSESS_ID}';
    execute sql string   UPDATE `${database_name}`.`milestone` SET `date`='${UPCOMING_COMPETITION_TO_ASSESS_CLOSE_DB}' WHERE `type`='SUBMISSION_DATE' AND `competition_id`='${UPCOMING_COMPETITION_TO_ASSESS_ID}';
    execute sql string   UPDATE `${database_name}`.`milestone` SET `date`='${UPCOMING_COMPETITION_TO_ASSESS_CLOSE_DB}' WHERE `type`='ASSESSORS_NOTIFIED' AND `competition_id`='${UPCOMING_COMPETITION_TO_ASSESS_ID}';

Custom suite setup
    The user logs-in in new browser  &{existing_assessor1_credentials}
    Connect to Database  @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user should see competition details
    And the user should not see an error in the page
    And the user should see the element         jQuery = h1:contains("${UPCOMING_COMPETITION_TO_ASSESS_NAME}")
    And the user should see the element         jQuery = li:contains("Competition opens")
    And the user should see the element         jQuery = li:contains("Competition closes")
    And the user should see the element         jQuery = .govuk-button:contains("Start new application")

the user checks for field validations
    the user should see the element                     jQuery = h1:contains("Invitation to assess '${READY_TO_OPEN_COMPETITION_NAME}'")
    the user should not see the element                 id = rejectComment
    the user selects the radio button                   acceptInvitation  false
    The user enters multiple strings into a text field  id = rejectComment  a${SPACE}  102
    The user clicks the button/link                     jQuery = button:contains("Confirm")
    the user should see a field and summary error       The reason cannot be blank.
    the user should see a field and summary error       Maximum word count exceeded. Please reduce your word count to 100.

the assessor accepts the invite
    the user navigates to the page      ${Invitation_for_upcoming_comp_assessor1}
    the user should see the element     jQuery = h1:contains("Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}'")
    the user should see the element     jQuery = h2:contains("${assessmentPeriod}")
    the user selects the radio button   acceptInvitation  true
    the user clicks the button/link     jQuery = button:contains("Confirm")
    the user should see the element     jQuery = p:contains("Your email address is linked to an existing account.")

the assessor shouldn't see Accepted and Rejected invites on dashboard
    [Arguments]  ${competition_name}
    the user should not see the element    jQuery = h2:contains("Invitations to assess") ~ ul li a:contains("${competition_name}")
