*** Settings ***
Documentation     IFS-9009  Always open competitions: invite assessors to competitions
...
...               IFS-8850  Always open competitions: applicant changes
...
...               IFS-8851  Always open competitions: create assessment periods
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown

Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/keywords/05__Email_Keywords.robot

*** Variables ***
# REPLACE WEB TEST DATA VARIABLES WITH ACTUAL E2E FLOW DATA AND CHANGE NAME OF VARIABLES
${webTestCompName}                 Always open competition
${applicationName}                 Always open application
${webTestAppName}                  Always open application decision pending
#Delete the above application when the user can assign an assessor to the application
${webTestAssessor}                 Paul Plum
${webTestAssessorEmailAddress}     paul.plum@gmail.com
${briefingErrormessage}            Assessor briefing:you must enter a valid date.
${deadlineErrormessage}            Assessment deadline: you must enter a date later than the previous milestone.

*** Test Cases ***
Send the email invite to the assessor for the competition using new content
    [Documentation]  IFS-9009
    Given Logging in and Error Checking         &{Comp_admin1_credentials}
    When comp admin sends invite to assesor
    Then the user reads his email               ${webTestAssessorEmailAddress}  Invitation to be an assessor for competition: '${webTestCompName}'  We invite you to assess applications for the competition:

Lead applicant creates an application and checks the dashboard content when the application is incomplete
    [Documentation]  IFS-8850
    Given the user logs out if they are logged in
    And the lead user creates an always open application                                         Test   User   test.user1@gmail.com   ${applicationName}
    When the lead user completes project details, application questions and finances sections
    Then the user checks the status of the application before completion

Lead applicant completes the application and checks the dashboard content before the application is submitted
    [Documentation]  IFS-8850
    Given the user clicks the button/link                                   link = ${applicationName}
    When the user accept the competition terms and conditions               Back to application overview
    Then the user checks the status of the application after completion

Lead applicant submits the application and checks the dashboard content and the guidance after submission
    [Documentation]  IFS-8850
    Given the user clicks the button/link                                  link = ${applicationName}
    When the user clicks the button/link                                   link = Review and submit
    And the user clicks the button/link                                    jQuery = button:contains("Submit application")
    Then the user checks the status of the application after submission

Lead applicant checks the dashboard content and the guidance after an assessor is assigned to the application
    [Documentation]  IFS-8850
    Given Log in as a different user                                    &{lead_applicant_credentials}
    And the user clicks the application tile if displayed
    When the user clicks the button/link                                link = ${webTestAppName}
    Then the user checks the status of the application in assessment

Comp admin updates the assessment period
    [Documentation]  IFS-8851
    Given Log in as a different user                       &{Comp_admin1_credentials}
    And the user clicks the button/link                    link = ${webTestCompName}
    And the user clicks the button/link                    link = Manage assessments
    And the user clicks the button/link                    link = Manage assessment period
    When the user enters text to a text field              assessmentPeriods1.milestoneEntries6. Assessor accepts.day  55
    And the user clicks the button/link                    jQuery = button:contains('Save and return to manage assessments')
    Then the user should see a field and summary error     ${briefingErrormessage}
    And the user enters text to a text field               assessmentPeriods3.milestoneEntries7. Assessor deadline.year  1999
    And the user clicks the button/link                    jQuery = button:contains('Save and return to manage assessments')
    And the user should see a field and summary error      ${deadlineErrormessage}
    And the user should see the element                    jQuery = td:contains(20/01/2021)

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The guest user opens the browser

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

comp admin sends invite to assesor
    the user clicks the button/link          link = ${webTestCompName}
    the user clicks the button/link          link = Invite assessors to assess the competition
    the user enters text to a text field     id = assessorNameFilter  ${webTestAssessor}
    the user clicks the button/link          jQuery = .govuk-button:contains("Filter")
    the user clicks the button/link          jQuery = tr:contains("${webTestAssessor}") label[for^="assessor-row"]
    the user clicks the button/link          jQuery = .govuk-button:contains("Add selected to invite list")
    the user clicks the button/link          link = Invite
    the user clicks the button/link          link = Review and send invites
    the user clicks the button/link          jQuery = .govuk-button:contains("Send invitation")

the lead user creates an always open application
    [Arguments]   ${firstName}   ${lastName}   ${email}   ${applicationName}
    the user select the competition and starts application          ${webTestCompName}
    the user clicks the button/link                                 link = Continue and create an account
    the user selects the radio button                               organisationTypeId    radio-${BUSINESS_TYPE_ID}
    the user clicks the button/link                                 jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House            ASOS  ASOS PLC
    the user should be redirected to the correct page               ${SERVER}/registration/register
    the user enters the details and clicks the create account       ${firstName}  ${lastName}  ${email}  ${short_password}
    the user reads his email and clicks the link                    ${email}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page               ${REGISTRATION_VERIFIED}
    the user clicks the button/link                                 link = Sign in
    Logging in and Error Checking                                   ${email}  ${short_password}
    the user clicks the button/link                                 link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}


the user completes the research category
    the user clicks the button/link      link = Research category
    the user selects the checkbox        id = researchCategory
    the user clicks the button/link      id = application-question-complete
    the user clicks the button/link      link = Back to application overview
    the user should see the element      jQuery=li:contains("Research category") > .task-status-complete


the user checks the status of the application after completion
    the user should see the element         jQuery = dt:contains("Application deadline:") ~ dd:contains("Decision pending")
    the user clicks the button/link         link = Back to applications
    the user should see the element         jQuery = li:contains("${applicationName}") .status:contains("Ready to review and submit")
    the user should not see the element     jQuery = li:contains("${applicationName}") .status:contains("days left")

the user checks the status of the application after submission
    the user should see the element     jQuery = h2:contains("Application submitted")
    the user should see the element     jQuery = a:contains("Reopen application")
    the user should see the element     jQuery = p:contains("If this application is reopened, it must be resubmitted before we can assess it.")
    the user should see the element     jQuery = p:contains("You will be asked to set up your project.")
    the user clicks the button/link     link = Back to applications
    the user should see the element     jQuery = li:contains("${applicationName}") .msg-deadline-waiting:contains("Awaiting assessment") + .msg-progress:contains("Submitted") a:contains("Reopen")

the user checks the status of the application in assessment
    the user should see the element         jQuery = h2:contains("Application submitted")
    the user should not see the element     jQuery = a:contains("Reopen application")
    the user should not see the element     jQuery = p:contains("If this application is reopened, it must be resubmitted before we can assess it.")
    the user should see the element         jQuery = p:contains("You will be asked to set up your project.")
    the user clicks the button/link         link = Back to applications
    the user should see the element         jQuery = li:contains("${webTestAppName}") .msg-deadline-waiting:contains("Decision pending") + .msg-progress:contains("Submitted")
    the user should not see the element     jQuery = li:contains("${webTestAppName}") a:contains("Reopen")
