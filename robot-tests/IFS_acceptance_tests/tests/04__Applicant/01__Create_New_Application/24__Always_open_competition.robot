*** Settings ***
Documentation     IFS-9009  Always open competitions: invite assessors to competitions
...
...               IFS-8850  Always open competitions: applicant changes
...
...               IFS-8847  Always open competitions: new comp setup configuration
...
...               IFS-8848  Always open competitions: comp setup milestones
...
...               IFS-8851  Always open competitions: create assessment periods
...
...               IFS-9504  Always open assessments: applicants cannot reopen a submitted application
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
${openEndedCompName}               Open ended competition
# REPLACE WEB TEST DATA VARIABLES WITH ACTUAL E2E FLOW DATA AND CHANGE NAME OF VARIABLES
${webTestCompName}                 Always open competition
${applicationName}                 Always open application
${webTestAppName}                  Always open application decision pending
#Delete the above application when the user can assign an assessor to the application
${webTestAssessor}                 Paul Plum
${webTestAssessorEmailAddress}     paul.plum@gmail.com
${briefingErrormessage}            1. Assessor Briefing: Please enter a valid date.
${deadlineErrormessage}            2. Acceptance deadline: Please enter a valid date.
${assessmentErrorMessage}          3. Assessment deadline: Please enter a valid date.

*** Test Cases ***
the user fills in milestones without a submission date
    [Documentation]  IFS-8848
    Given The user logs-in in new browser            &{Comp_admin1_credentials}
    And the user navigates to the page               ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     ${openEndedCompName}  ${month}  ${nextyear}  ${compType_Programme}  STATE_AID  GRANT
    When the user completes milestones section
    Then the user should see the element             jQuery = td:contains("2. Submission date") + td:contains("-")+td:contains("-")+td:contains("None")
    And the user should see the element              jQuery = .panel:contains("Assessment dates are set after the competition has opened.")
    And the user should see the element              jQuery = .panel:contains("Do not complete the 'Submission date' milestone when created open-ended competitions.")

the user should not see submission deadline date in public content dates
    [Documentation]  IFS-8848
    Given the user clicks the button/link           jQuery = span:contains("Public content")
    When the user clicks the button/link            link = Dates
    Then the user should see the element            jQuery = h2:contains("No submission deadline")
    And the user should see the element             jQuery = .wysiwyg-styles:contains("This is open-ended competition and applications can be submitted at any time.")
    And the user should see valid event details

the user creates a new open ended competiton
    [Documentation]  IFS-8848
    Given the user clicks the button/link                             link = Public content
    And the user clicks the button/link                               link = Competition details
    And the competition admin creates open ended competition          ${business_type_id}  ${openEndedCompName}  Open ended  ${compType_Programme}  STATE_AID  GRANT  PROJECT_SETUP  yes  1  true  collaborative
    When the user navigates to the page                               ${frontDoor}
    And the user clicks the button/link in the paginated list         link = ${openEndedCompName}
    Then the user check for valid content on front end
    [Teardown]  get competition id and set open date to yesterday     ${openEndedCompName}

Send the email invite to the assessor for the competition using new content
    [Documentation]  IFS-9009
    Given the user navigates to the page        ${CA_Live}
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
    [Documentation]  IFS-8850  IFS-9504
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
    Given Log in as a different user                           &{Comp_admin1_credentials}
    When the user clicks the button/link                       link = ${webTestCompName}
    And the user clicks the button/link                        link = Manage assessments
    And the user clicks the button/link                        link = Manage assessment period
    Then the user checks the milestone validation messages
    And the user clicks the button/link                        link = Back to manage assessments
    And the user should see the element                        jQuery = .govuk-table__cell:contains('20/01/2021')

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The guest user opens the browser

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the user completes milestones section
    the user clicks the button/link                    link = Milestones
    the user clicks the button twice                   jQuery = label:contains("Project setup")
    the user clicks the button/link                    jQuery = button:contains("Done")
    the user completes application submission page     Yes
    the user clicks the button/link                    jQuery = button:contains("Done")

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
    the user should see the element         jQuery = h2:contains("Application submitted")
    the user should not see the element     jQuery = a:contains("Reopen application")
    the user should not see the element     jQuery = p:contains("If this application is reopened, it must be resubmitted before we can assess it.")
    the user should see the element         jQuery = p:contains("You will be asked to set up your project.")
    the user clicks the button/link         link = Back to applications
    the user should see the element         jQuery = li:contains("${applicationName}") .msg-deadline-waiting:contains("Decision pending") + .msg-progress:contains("Submitted")

the user checks the status of the application in assessment
    the user should see the element         jQuery = h2:contains("Application submitted")
    the user should not see the element     jQuery = a:contains("Reopen application")
    the user should not see the element     jQuery = p:contains("If this application is reopened, it must be resubmitted before we can assess it.")
    the user should see the element         jQuery = p:contains("You will be asked to set up your project.")
    the user clicks the button/link         link = Back to applications
    the user should see the element         jQuery = li:contains("${webTestAppName}") .msg-deadline-waiting:contains("Decision pending") + .msg-progress:contains("Submitted")
    the user should not see the element     jQuery = li:contains("${webTestAppName}") a:contains("Reopen")

the user should see valid event details
    the user clicks the button/link     jQuery = button:contains("+ add new event")
    the user should see the element     jQuery = span:contains("New event")
    the user should see the element     jQuery = p:contains("Everything entered here will appear in the competition's 'Dates' tab.")
    the user should see the element     jQuery = label:contains("Event description")

the competition admin creates open ended competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects the Terms and Conditions               ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}  # 1 means 30%
    the user fills in the CS funding eligibility            ${researchCategory}  ${compType}   ${fundingRule}
    the user selects the organisational eligibility to no   false
    the user marks the application as done                  ${projectGrowth}  ${compType}  ${competition}
    the user fills in the CS Assessors                      ${fundingType}
    the user fills in the CS Documents in other projects
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview
    the user clicks the button/link                         jQuery = a:contains("Complete")
    the user clicks the button/link                         jQuery = button:contains('Done')
    the user navigates to the page                          ${CA_UpcomingComp}
    the user should see the element                         jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user check for valid content on front end
    the user should see the element     jQuery = li:contains("There is no submission deadline") strong:contains("Competition closes:")
    the user clicks the button/link     id = tab_dates
    the user should see the element     jQuery = dt:contains("No submission deadline") + dd:contains("This is open-ended competition and applications can be submitted at any time.")

the user checks the milestone validation messages
    the user enters text to a text field     assessmentPeriods2.milestoneEntriesASSESSOR_BRIEFING.day  55
    the user enters text to a text field     assessmentPeriods2.milestoneEntriesASSESSOR_ACCEPTS.month  13
    the user enters text to a text field     assessmentPeriods2.milestoneEntriesASSESSOR_DEADLINE.year  1999
    the user enters text to a text field     assessmentPeriods2.milestoneEntriesASSESSOR_DEADLINE.month  15
    the user clicks the button/link          jQuery = button:contains('Save and return to manage assessments')
    the user should see a summary error      ${briefingErrormessage}
    the user should see a summary error      ${deadlineErrormessage}
    the user should see a summary error      ${assessmentErrorMessage}
