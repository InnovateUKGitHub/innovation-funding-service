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
...               IFS-9008 Always open competitions – assessment period actions
...
...               IFS-8852 Always open competitions: assign assessors to applications
...
...               IFS-8853 Always open competitions: assign applications to assessors in assessment period
...
...               IFS-8849 Always open competitions: internal comp dashboard
...
...               IFS-8855 Always open competitions: manage notifications/release feedback
...
...               IFS-9758 Comps to assess should have batch numbers
...
...               IFS-9785 Supporter getting internal server error on login to dashboard without assessment periods
...
...               IFS-9757 Assessment period validations
...
...               IFS-9756 Typo in Comp milestones page
...
...               IFS-9750 Empty heading is displayed when there is no assessment period attached to the competition
...
...               IFS-9760 first assessment period data is not validated/saved so return to emptyform input
...
...               IFS-9759 No assessment period state
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
${webTestCompID}                   ${competition_ids["${webTestCompName}"]}
${applicationName}                 Always open application
${webTestAppName}                  Always open application decision pending
#Delete the above application when the user can assign an assessor to the application
${briefingErrormessage}            1. Assessor briefing: Please enter a valid date.
${deadlineErrormessage}            2. Assessor accepts: Please enter a valid date.
${assessmentErrorMessage}          3. Assessor deadline: Please enter a valid date.
${webTestAssessor}                 Angel Witt
${webTestAssessorEmailAddress}     angel.witt@gmail.com
${assessorEmail}                   another.person@gmail.com

*** Test Cases ***
the user fills in milestones without a submission date
    [Documentation]  IFS-8848  IFS-9756
    Given The user logs-in in new browser            &{Comp_admin1_credentials}
    And the user navigates to the page               ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     ${openEndedCompName}  ${month}  ${nextyear}  ${compType_Programme}  STATE_AID  GRANT
    When the user completes milestones section
    Then the user should see the element             jQuery = td:contains("2. Submission date") + td:contains("-")+td:contains("-")+td:contains("None")
    And the user should see the element              jQuery = .panel:contains("Assessment dates are set after the competition has opened.")
    And the user should see the element              jQuery = .panel:contains("Do not complete the 'Submission date' milestone when creating open-ended competitions.")

the user should not see submission deadline date in public content dates
    [Documentation]  IFS-8848
    Given the user clicks the button/link           jQuery = span:contains("Public content")
    When the user clicks the button/link            link = Dates
    Then the user should see the element            jQuery = h2:contains("No submission deadline")
    And the user should see the element             jQuery = .wysiwyg-styles:contains("This is open-ended competition and applications can be submitted at any time.")
    And the user should see valid event details

the user creates a new open ended competiton
    [Documentation]  IFS-8848
    Given the user clicks the button/link                             link = Back to public content
    And the user clicks the button/link                               link = Competition details
    And the competition admin creates open ended competition          ${business_type_id}  ${openEndedCompName}  Open ended  ${compType_Programme}  STATE_AID  GRANT  PROJECT_SETUP  yes  1  true  collaborative
    When the user navigates to the page                               ${frontDoor}
    And the user clicks the button/link in the paginated list         link = ${openEndedCompName}
    Then the user check for valid content on front end
    [Teardown]  get competition id and set open date to yesterday     ${openEndedCompName}

the user should see the disabled send notification and release feedback button
    [Documentation]  IFS-9750
    Given the user navigates to the page     ${CA_Live}
    When the user clicks the button/link     link = ${openEndedCompName}
    Then the user should see the element     css = [disabled='disabled']

Send the email invite to the assessor for the competition using new content
    [Documentation]  IFS-9009
    When comp admin sends invite to assesor
    Then the user reads his email               ${webTestAssessorEmailAddress}  Invitation to be an assessor for competition: '${openEndedCompName}'  We invite you to assess applications for the competition:

Lead applicant creates an application and checks the dashboard content when the application is incomplete
    [Documentation]  IFS-8850
    Given the user logs out if they are logged in
    And the lead user creates an always open application                                         Test   User   test.user1@gmail.com   ${applicationName}
    When the lead user completes project details, application questions and finances sections
    Then the user checks the status of the application before completion

Lead applicant completes the application and checks the dashboard content before the application is submitted
    [Documentation]  IFS-8850
    Given the user adds a partner organisation and application details
    And log in as a different user                                         test.user1@gmail.com     ${short_password}
    When the user clicks the button/link                                   link = ${applicationName}
    And the applicant completes Application Team
    And the user accept the competition terms and conditions               Back to application overview
    Then the user checks the status of the application after completion

Lead applicant submits the application and checks the dashboard content and the guidance after submission
    [Documentation]  IFS-8850  IFS-9504
    Given the user clicks the button/link                                  link = ${applicationName}
    When the user clicks the button/link                                   link = Review and submit
    And the user clicks the button/link                                    jQuery = button:contains("Submit application")
    Then the user checks the status of the application after submission

Comp admin can see default empty assessment periods
    [Documentation]  IFS-9759  IFS-9760
    Given Log in as a different user                                              &{Comp_admin1_credentials}
    When the user clicks the button/link                                          link = ${openEndedCompName}
    And the user clicks the button/link                                           link = Manage assessments
    And the user clicks the button/link                                           link = Manage assessment period
    Then the user should see empty assessment periods
    And validation messages displaying on saving empty assessment periods
    And empty assessment periods should not be created on clicking back links

Comp admin creates a new assessment period
    [Documentation]  IFS-9759  IFS-9760
    Given the user clicks the button/link           link = Manage assessment period
    When the user create a new assessment period
    Then the user should see assessment period 1

Lead applicant checks the dashboard content and the guidance after an assessor is assigned to the application
    [Documentation]  IFS-8850
    Given Log in as a different user                                    &{lead_applicant_credentials}
    And the user clicks the application tile if displayed
    When the user clicks the button/link                                link = ${webTestAppName}
    Then the user checks the status of the application in assessment

Assessment period validations as internal user
    [Documentation]  IFS-9757
    Given Log in as a different user                       &{Comp_admin1_credentials}
    And the user clicks the button/link                    link = ${webTestCompName}
    When the user clicks the button/link                   link = Manage assessments
    And the user clicks the button/link                    link = Manage assessors
    And the user clicks the button/link                    jQuery = button:contains("Save and continue")
    Then the user should see a field and summary error     Please select an assessment period

Comp admin updates the assessment period
    [Documentation]  IFS-8851
    Given the user clicks the button/link                      link = Back to manage assessments
    When the user clicks the button/link                       link = Manage assessment period
    Then the user checks the milestone validation messages
    And the user clicks the button/link                        link = Back to manage assessments
    And the user should see the element                        jQuery = .govuk-table__cell:contains('20/01/2021')

Internal user notify the assessors of their assigned applications
    [Documentation]  IFS-9008  IFS-8852  IFS-8853  IFS-9758
    Given assign the application to assessor
    When the user clicks the button/link                     jQuery = button:contains("Notify assessors")
    And the user logs out if they are logged in
    Then the user reads his email and clicks the link        ${assessorEmail}  Applications assigned to you for competition '${webTestCompName}'  We have assigned applications for you to assess for this competition:   1
    And the assessor accepts an invite to an application

Internal user closes assessment period one
    [Documentation]  IFS-9008
    Given log in as a different user             &{ifs_admin_user_credentials}
    And the user navigates to the page           ${server}/management/assessment/competition/${webTestCompID}
    When the user clicks the button/link         jQuery = button:contains("Close assessment")
    Then the user should not see the element     jQuery = button:contains("Close assessment")
    And the user should see the element          jQuery = button:contains("Notify assessors")

Internal user sees valid information on dashboard
    [Documentation]  IFS-8849
    When the user clicks the button/link                       link = Competition
    Then the user sees valid open ended competition details

internal user inputs the decision and send the notification with feedback
    [Documentation]  IFS-8855
    Given the user inputs the funding decision for applications
    When the user sends notification and releases feedback
    And the user navigates to the page                               ${server}/project-setup-management/competition//${webTestCompID}/status/all
    Then the user refreshes until element appears on page            jQuery = tr div:contains("${webTestAppName}")

Assessor has been assigned to the competition
    [Documentation]  IFS-8852
    Given log in as a different user             ${assessorEmail}   ${short_password}
    When the user clicks the button/link         jQuery = a:contains('${webTestCompName}')
    Then the user should see the element         jQuery = h2:contains('Assessing open-ended competitions')

Comp admin manages the assessors
    [Documentation]  IFS-8852
    Given log in as a different user           &{ifs_admin_user_credentials}
    And the user navigates to the page         ${server}/management/assessment/competition/${webTestCompID}
    And the user clicks the button/link        link = Manage assessors
    When the user selects the radio button     assessmentPeriodId  99
    And the user clicks the button/link        jQuery = button:contains("Save and continue")
    And the user clicks the button/link        jQuery = td:contains("Another Person") ~ td a:contains("View progress")
    Then the user should see the element       jQuery = h2:contains('Assigned') ~ div td:contains('Always open application decision pending')
    And the user clicks the button/link        link = Back to manage assessors
    And the user clicks the button/link        link = Back to choose an assessment period to manage assessors
    And the user clicks the button/link        link = Back to manage assessments

Supporter can review open ended ktp competition applications
    [Documentation]  IFS-9785
    Given log in as a different user          &{supporter_credentials}
    And the user clicks the button/link       id = dashboard-link-SUPPORTER
    When supporter reviews an application
    Then the user should see the element      jQuery = button:contains("Edit")
    And the user should see the element       jQuery = dt:contains("Your feedback")+dd:contains("This is the comment from the supporter")
    And the user should see the element       jQuery = dt:contains("Are you interested in supporting this application?")+dd:contains("Yes")


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
    the user select the competition and starts application          ${openEndedCompName}
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

the user adds a partner organisation and application details
    the user clicks the button/link                      link = ${applicationName}
    the lead invites already registered user             ${collaborator1_credentials["email"]}   ${openEndedCompName}
    partner applicant completes the project finances     ${applicationName}  no  ${collaborator1_credentials["email"]}  ${short_password}

assign the application to assessor
    the user clicks the button/link     link = Manage applications
    the user clicks the button twice    jQuery = label:contains("Assessment period 1")
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user clicks the button/link     jQuery = td:contains("Always open application decision pending") ~ td a:contains("View progress")
    the user selects the checkbox       assessor-row-1
    the user clicks the button/link     jQuery = button:contains("Add to application")
    the user clicks the button/link     link = Back to manage applications
    the user clicks the button/link     link = Back to choose an assessment period to manage applications
    the user clicks the button/link     link = Back to manage assessments

the assessor accepts an invite to an application
    logging in and error checking         ${assessorEmail}   ${short_password}
    the user clicks the button/link       link = ${webTestAppName}
    the user selects the radio button     assessmentAccept  true
    the user clicks the button/link       jQuery = button:contains("Confirm")
    the user clicks the button/link       link = Assessments
    the user should see the element       jQuery = strong:contains("Batch assessment 1") ~ h3:contains("${webTestCompName}")

the user sees valid open ended competition details
    the user should see the element      jQuery = a:contains("Send notification and release feedback")
    the user should see the element      jQuery = a:contains("Input and review funding decision")
    the user should see the element      jQuery = h3:contains("Submission date")+ p:contains("Open-ended")
    the user should see the element      jQuery = h3:contains("Briefing event") + p a:contains("See public dates")
    the user should see the element      jQuery = h3:contains("Assessment") + p:contains("Batch assessments")
    the user should see the element      jQuery = h3:contains("Funding decision and assessment feedback")

the user inputs the funding decision for applications
    the user clicks the button/link     link = Input and review funding decision
    the user clicks the button/link     id = app-row-1
    the user clicks the button/link     jQuery = button:contains("Successful")
    the user clicks the button/link     link = Competition

the user sends notification and releases feedback
    the user clicks the button/link                                           jQuery = a:contains("Send notification and release feedback")
    the user should see the element                                           jQuery = h1:contains("Manage funding decisions and notifications")
    the user clicks the button/link                                           id = select-all-1
    the user clicks the button/link                                           id = write-and-send-email
    the user should see the element                                           jQuery = h1:contains("Send decision notification and release feedback")
    the internal sends the descision notification email to all applicants     Open ended competition body text

supporter reviews an application
    the user clicks the button/link         link = Always open ktp competition
    the user clicks the button/link         jQuery = li:nth-child(1) div a:contains("Review")
    the user selects the radio button       decision  decision-yes
    the user enters text to a text field    css = .editor  This is the comment from the supporter
    the user clicks the button/link         jQuery = button:contains("Save and return to applications")

the user should see empty assessment periods
    the user should see the element     name = assessmentPeriods[0].milestoneEntries[ASSESSOR_BRIEFING].day
    the user should see the element     name = assessmentPeriods[0].milestoneEntries[ASSESSOR_ACCEPTS].day
    the user should see the element     name = assessmentPeriods[0].milestoneEntries[ASSESSOR_DEADLINE].day
    the element should be disabled      jQuery = button:contains("+ Add new assessment period")

validation messages displaying on saving empty assessment periods
    the user clicks the button/link         jQuery = button:contains("Save and return to manage assessments")
    the user should see a summary error     1. Assessor briefing: Please enter a valid date.
    the user should see a summary error     2. Assessor accepts: Please enter a valid date.
    the user should see a summary error     3. Assessor deadline: Please enter a valid date.

empty assessment periods should not be created on clicking back links
    the user clicks the button/link     link = Back to manage assessments
    the user should see the element     jQuery = p:contains("No assessment periods have been created.")
    the user should see the element     jQuery = p:contains("You can create and manage assessment periods via the 'Manage assessment period'")
    the user should see the element     jQuery = .disabled:contains("Manage assessors")
    the user should see the element     jQuery = .disabled:contains("Manage applications")

the user create a new assessment period
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_BRIEFING.day  12
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_BRIEFING.month  12
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_BRIEFING.year  2100
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_ACCEPTS.day  14
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_ACCEPTS.month  12
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_ACCEPTS.year  2100
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_DEADLINE.day  16
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_DEADLINE.month  12
    the user enters text to a text field     assessmentPeriods0.milestoneEntriesASSESSOR_DEADLINE.year  2100
    the user clicks the button/link          jQuery = button:contains('Save and return to manage assessments')

the user should see assessment period 1
    the user should see the element     jQuery = td:contains("1. Assessor briefing") ~ td:contains("12/12/2100")
    the user should see the element     jQuery = td:contains("2. Assessor accepts") ~ td:contains("14/12/2100")
    the user should see the element     jQuery = td:contains("3. Assessor deadline") ~ td:contains("16/12/2100")
    the user should see the element     jQuery = button:contains("Notify assessors")
    the user clicks the button/link     link = Manage assessment period
    the user should see the element     jQuery = button:contains("+ Add new assessment period")
