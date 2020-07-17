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
Force Tags        Assessor  ATS2020
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Assessor_Commons.robot

*** Variables ***
${Invitation_for_upcoming_comp_assessor1}     ${server}/assessment/invite/competition/1ec7d388-3639-44a9-ae62-16ad991dc92c
${assessmentPeriod}                           ${IN_ASSESSMENT_COMPETITION_ASSESSOR_ACCEPTS_PRETTY_DATE} to ${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_PRETTY_DATE}: Assessment period

*** Test Cases ***
Existing assessor: Reject invitation from Dashboard
    [Documentation]    INFUND-4631  INFUND-5157  INFUND-6455
    Given the user clicks the button/link                                         link = ${READY_TO_OPEN_COMPETITION_NAME}
    And the user checks for field validations
    When the assessor fills all fields with valid inputs
    And The user clicks the button/link                                           jQuery = button:contains("Confirm")
    Then the user should see the element                                          jQuery = p:contains("Thank you for letting us know you are unable to assess applications within this competition.")
    And the assessor shouldn't see Accepted and Rejected invites on dashboard     ${READY_TO_OPEN_COMPETITION_NAME}

Existing Assessor tries to accept expired invitation in closed assessment
    [Documentation]    INFUND-943
    [Setup]    Close the competition in assessment
    Given Log in as a different user                   &{existing_assessor1_credentials}
    And the user should not see the element            link = ${IN_ASSESSMENT_COMPETITION_NAME}
    When the user navigates to the page                ${Invitation_for_upcoming_comp_assessor1}
    Then the user should see the element               jQuery = h1:contains("This invitation is now closed")
    [Teardown]  Reset competition's milestone

Existing assessor: Accept invitation from the invite link
    [Documentation]    INFUND-228  INFUND-304  INFUND-3716  INFUND-5509  INFUND-6500  INFUND-6455
    [Setup]    Logout as user
    Given the assessor accepts the invite
    When the user clicks the button/link                                          jQuery = a:contains("Click here to sign in")
    And Invited guest user log in                                                 &{existing_assessor1_credentials}
    Then the user should see the element                                          link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And the assessor shouldn't see Accepted and Rejected invites on dashboard     ${IN_ASSESSMENT_COMPETITION_NAME}

*** Keywords ***
the assessor fills all fields with valid inputs
    Select From List By Index                              id = rejectReasonValid    2
    The user enters text to a text field                   id = rejectComment    Unable to assess this application.
    the user cannot see a validation error in the page

Close the competition in assessment
    Log in as a different user          &{Comp_admin1_credentials}
    The user clicks the button/link     link = ${IN_ASSESSMENT_COMPETITION_NAME}
    The user clicks the button/link     jQuery = .govuk-button:contains("Close assessment")

Reset competition's milestone
    # That is to reset competition's milestone back to its original value, that was NUll before pressing the button "Close assessment"
    Execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE `type`='ASSESSMENT_CLOSED' AND `competition_id`='${IN_ASSESSMENT_COMPETITION}';

Custom suite setup
    The user logs-in in new browser     &{existing_assessor1_credentials}
    Connect to Database  @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user checks for field validations
    the user should see the element                        jQuery = h1:contains("Invitation to assess '${READY_TO_OPEN_COMPETITION_NAME}'")
    the user should not see the element                    id = rejectComment
    the user selects the radio button                      acceptInvitation  false
    The user enters multiple strings into a text field     id = rejectComment  a${SPACE}  102
    The user clicks the button/link                        jQuery = button:contains("Confirm")
    the user should see a field and summary error          The reason cannot be blank.
    the user should see a field and summary error          Maximum word count exceeded. Please reduce your word count to 100.

the assessor accepts the invite
    the user navigates to the page        ${Invitation_for_upcoming_comp_assessor1}
    the user should see the element       jQuery = h1:contains("Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}'")
    the user should see the element       jQuery = h2:contains("${assessmentPeriod}")
    the user selects the radio button     acceptInvitation  true
    the user clicks the button/link       jQuery = button:contains("Confirm")
    the user should see the element       jQuery = p:contains("Your email address is linked to an existing account.")

the assessor shouldn't see Accepted and Rejected invites on dashboard
    [Arguments]  ${competition_name}
    the user should not see the element     jQuery = h2:contains("Invitations to assess") ~ ul li a:contains("${competition_name}")
