*** Settings ***
Documentation     IFS-10694 Hesta - Email notification content for application submission
...
...               IFS-10688 Hesta - Create competition type hesta
...
...               IFS-10695 Hesta - Email notification content for unsuccessfull application
...
...               IFS-10697 Hesta - Application Submission confirmation page
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${hestaCompTypeSelector}                        dt:contains("Competition type") ~ dd:contains("${compType_HESTA}")
${hestaApplicationName}                         Hesta application
${newHestaApplicationName}                      NEW Hesta application
${leadApplicantEmail}                           tim.timmy@heukar.com
${newLeadApplicantEmail}                        barry.barrington@heukar.com
${hestaApplicationSubmissionEmailSubject}       confirmation of your Horizon Europe UK Application Registration
${hestaApplicationUnsuccessfulEmailSubject}     update about your Horizon Europe UK Application Registration for government-backed funding
${hestaApplicationSubmissionEmail}              We have received your stage 1 pre-registration to the Horizon Europe UK Application Registration programme
${hestaApplicationUnsuccessfulEmail}            We have been advised you were unsuccessful in your grant application for Horizon Europe funding from The European Commission
${assessorEmail}                                another.person@gmail.com
${webTestAssessor}                              Angel Witt
${webTestAssessorEmailAddress}                  angel.witt@gmail.com

*** Test Cases ***
Comp admin can select the competition type option Hesta in Initial details on competition setup
    [Documentation]  IFS-10688
    Given the user logs-in in new browser             &{Comp_admin1_credentials}
    When the user navigates to the page               ${CA_UpcomingComp}
    And the user clicks the button/link               jQuery = .govuk-button:contains("Create competition")
    Then the user fills in the CS Initial details     ${hestaCompetitionName}  ${month}  ${nextyear}  ${compType_HESTA}  STATE_AID  GRANT

Comp admin can view Hesta competition type in Initial details read only view
    [Documentation]  IFS-10688
    Given the user clicks the button/link    link = Initial details
    Then the user can view Hesta competition type in Initial details read only view

Comp admin creates Hesta competition
    [Documentation]  IFS-8751
    Given the user clicks the button/link                            link = Back to competition details
    Then the competition admin creates Hesta competition             ${BUSINESS_TYPE_ID}  ${hestaCompetitionName}  ${compType_HESTA}  ${compType_HESTA}  STATE_AID  GRANT  RELEASE_FEEDBACK  no  1  false  single-or-collaborative
    [Teardown]  Get competition id and set open date to yesterday    ${hestaCompetitionName}

Lead applicant can submit application
    [Documentation]  IFS-8751
    Given the user logs out if they are logged in
    When the user successfully completes application          tim   timmy   ${leadApplicantEmail}   ${hestaApplicationName}
    And the user clicks the button/link                       link = Your project finances
    Then the user marks the finances as complete              ${hestaApplicationName}  labour costs  54,000  no
    Then the user can submit the application

Lead applicant should get a confirmation email after application submission
    [Documentation]    IFS-10694
    Given Requesting IDs of this application    ${hestaApplicationName}
    Then the user reads his email               ${leadApplicantEmail}  ${ApplicationID}: ${hestaApplicationSubmissionEmailSubject}  ${hestaApplicationSubmissionEmail}

The Application Summary page must not include the Reopen Application link when the internal team mark the application as successful / unsuccessful
    [Documentation]  IFS-10697
    Given Log in as a different user                                                &{Comp_admin1_credentials}
    And Requesting IDs of this competition                                          ${hestaCompetitionName}
    And Competition admin creates an assessment period                              ${competitionId}
    And comp admin sends invite to assesor
    And the assessor accepts an invite to an application
    And Log in as a different user                                                 &{Comp_admin1_credentials}
    And The user clicks the button/link                                             link = ${hestaCompetitionName}
    And assign the application to assessor                                          ${hestaApplicationName}
    When the internal team mark the application as successful / unsuccessful        ${hestaApplicationName}   FUNDED
    And Log in as a different user                                                  email=${leadApplicantEmail}   password=${short_password}
    Then the application summary page must not include the reopen application link
    And the user should see the element                                            jQuery = h1:contains("Application status")
    And the user is presented with the Application Summary page

Lead applicant receives email notifiction when internal user marks application unsuccessful
    [Documentation]  IFS-10695
    Given the user logs out if they are logged in
    And Requesting IDs of this competition                                          ${hestaCompetitionName}
    And the user successfully completes application                                 barry   barrington   ${newLeadApplicantEmail}   ${newHestaApplicationName}
    And the user clicks the button/link                                             link = Your project finances
    And the user marks the finances as complete                                     ${newHestaApplicationName}  labour costs  54,000  no
    And the user can submit the application
    And Log in as a different user                                                  &{Comp_admin1_credentials}
    And The user clicks the button/link                                             link = ${hestaCompetitionName}
    And assign the application to assessor                                          ${newHestaApplicationName}
    And log in as a different user                                                  &{Comp_admin1_credentials}
    When the internal team mark the application as successful / unsuccessful        ${newHestaApplicationName}   UNFUNDED
    And the user clicks the button/link                                             link = Competition
    And Requesting IDs of this application                                          ${newHestaApplicationName}
    And the internal team notifies all applicants                                   ${ApplicationID}
    Then the user reads his email                                                   ${newLeadApplicantEmail}  ${ApplicationID}: ${hestaApplicationUnsuccessfulEmailSubject}  ${hestaApplicationUnsuccessfulEmail}


*** Keywords ***
the user can view Hesta competition type in Initial details read only view
    the user should see the element     jQuery = ${hestaCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Edit")
    the user should see the element     jQuery = ${hestaCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Done")

the competition admin creates Hesta competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects the Terms and Conditions               ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}  # 1 means 30%
    the user fills in the CS funding eligibility            true   ${compType_HESTA}  ${fundingRule}
    the user selects the organisational eligibility         true    true
    the user completes milestones section
    the user marks the Hesta application question as done   ${projectGrowth}  ${compType}  ${competition}
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview
    the user clicks the button/link                         jQuery = a:contains("Complete")
    the user clicks the button/link                         jQuery = button:contains('Done')
    the user navigates to the page                          ${CA_UpcomingComp}
    the user should see the element                         jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

Requesting IDs of this application
    [Arguments]  ${applicationName}
    ${ApplicationID} =  get application id by name    ${applicationName}
    Set suite variable    ${ApplicationID}

Requesting IDs of this competition
    [Arguments]  ${competitionName}
    ${competitionId} =  get comp id from comp title  ${hestaCompetitionName}
    Set suite variable  ${competitionId}

user selects where is organisation based
    [Arguments]  ${org_type}
    the user selects the radio button     international  ${org_type}
    the user clicks the button/link       id = international-organisation-cta

the user successfully completes application
    [Arguments]   ${firstName}   ${lastName}   ${email}   ${applicationName}
    the user select the competition and starts application          ${hestaCompetitionName}
    the user clicks the button/link                                 link = Continue and create an account
    user selects where is organisation based                        isNotInternational
    the user selects the radio button                               organisationTypeId    radio-1
    the user clicks the button/link                                 jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House            ASOS  ASOS PLC
    the user should be redirected to the correct page               ${SERVER}/registration/register
    the user enters the details and clicks the create account       ${firstName}  ${lastName}  ${email}  ${short_password}
    the user reads his email and clicks the link                    ${email}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page               ${REGISTRATION_VERIFIED}
    the user clicks the button/link                                 link = Sign in
    Logging in and Error Checking                                   ${email}  ${short_password}
    the user clicks the button/link                                 link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user completes the application details section              ${applicationName}  ${tomorrowday}  ${month}  ${nextyear}  84
    the applicant completes Application Team
    the user completes the application research category            Feasibility studies
    the lead applicant fills all the questions and marks as complete(Hesta)
    the user accept the competition terms and conditions            Back to application overview

the user is presented with the Application Summary page
    the user should see the element          jQuery = h2:contains("Application submitted")
    the user should see the element          jQuery = .govuk-panel:contains("Application number: ${ApplicationID}")
    the user should see the element          jQuery = h2:contains("What happens next?")
    the user should see the element          jQuery = p:contains("You have already applied directly to the European Commission for an EU grant.")
    the user should see the element          jQuery = h3:contains("Verification checks")
    the user should see the element          jQuery = h3:contains("Stage 2")
    the user should see the element          jQuery = h3:contains("If your application is successful")
    the user should see the element          jQuery = h3:contains("If your application is successful")
    the user should see the element          jQuery = p:contains("You will proceed to stage 2 of our process.")
    the user should see the element          jQuery = h3:contains("If your application is unsuccessful")
    the user should see the element          jQuery = p:contains("After registering your Horizon Europe UK Application, you may still be unsuccessful.")
    the user should see the element          jQuery = h3:contains("Application feedback")
    the user should see the element          jQuery = p:contains("Since we do not assess your application for EU grants we do not provide individual feedback.")
    the user should not see the element      jQuery = h3:contains("Assessment process")
    the user should not see the element      jQuery = h3:contains("Decision notification")
    the user should not see the element      jQuery = p:contains("Application feedback will be provided by")

the internal team mark the application as successful / unsuccessful
    [Arguments]   ${applicationName}   ${decision}
    the user navigates to the page      ${server}/management/competition/${competitionId}
    the user clicks the button/link     link = Input and review funding decision
    the user clicks the button/link     jQuery = tr:contains("${applicationName}") label
    the user clicks the button/link     css = [type="submit"][value="${decision}"]

the internal team notifies all applicants
    [Arguments]  ${ApplicationID}
    the user clicks the button/link                      link = Send notification and release feedback
    the user clicks the button/link                      id = app-row-${ApplicationID}
    the user clicks the button/link                      id = write-and-send-email
    the user clicks the button/link                      id = send-email-to-all-applicants
    the user clicks the button/link                      id = send-email-to-all-applicants-button
    the user refreshes until element appears on page     jQuery = td:contains("Sent")

the application summary page must not include the reopen application link
    the user navigates to the page          ${server}/application/${ApplicationID}/track
    the user should not see the element     link = Reopen application

the user marks the Hesta application question as done
    [Arguments]  ${growthTable}  ${comp_type}  ${competition}
    the user clicks the button/link                                     link = Application
    the user marks each question as complete                            Application details
    the user fills in the CS Application section with custom questions  ${growthTable}  ${comp_type}

the user completes milestones section
    the user clicks the button/link                 link = Milestones
    the user clicks the button twice                jQuery = label:contains("Project setup")
    the user clicks the button/link                 jQuery = button:contains("Done")
    the user completes application submission page  Yes
    the user clicks the button/link                 jQuery = button:contains("Done")
    the user clicks the button/link                 link = Back to competition details
    the user should see the element                 jQuery = div:contains("Milestones") ~ .task-status-complete

Competition admin creates an assessment period
    [Arguments]  ${competitionId}
    Log in as a different user              &{Comp_admin1_credentials}
    the user clicks the button/link         link = ${hestaCompetitionName}
    the user clicks the button/link         link = Manage assessments
    the user clicks the button/link         link = Manage assessment period
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_BRIEFING.day  12
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_BRIEFING.month  12
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_BRIEFING.year  2100
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_ACCEPTS.day  14
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_ACCEPTS.month  12
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_ACCEPTS.year  2100
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_DEADLINE.day  16
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_DEADLINE.month  12
    the user enters text to a text field    assessmentPeriods0.milestoneEntriesASSESSOR_DEADLINE.year  2100
    the user clicks the button/link         jQuery = button:contains('Save and return to manage assessments')
    the user clicks the button/link         jQuery = button:contains("Notify assessors")
    update assessment batch 1 milestone to yesterday   ${competitionId}  ASSESSOR_DEADLINE
    the user clicks the button/link         jQuery = button:contains("Close assessment")
    the user clicks the button/link         link = Competition

update assessment batch 1 milestone to yesterday
    [Arguments]  ${competition_id}  ${milestone}
    ${yesterday} =    get yesterday
    execute sql string  UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${competition_id}' and type IN ('${milestone}');
    reload page

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}
Custom Suite Teardown
    the user closes the browser
    Disconnect from database

assign the application to assessor
    [Arguments]   ${applicationName}
    the user clicks the button/link     link = Manage assessments
    the user clicks the button/link     link = Manage applications
    the user clicks the button/link     jQuery = td:contains("${applicationName}") ~ td a:contains("View progress")
    the user selects the checkbox       assessor-row-1
    the user clicks the button/link     jQuery = button:contains("Add to application")
    the user clicks the button/link     link = Allocate applications
    the user clicks the button/link     link = Back to manage assessments
    the user clicks the button/link     link = Competition
    the user clicks the button/link     link = Input and review funding decision
    the user should see the element     jQuery =td:contains("${hestaApplicationName}")

comp admin sends invite to assesor
    the user clicks the button/link          link = Invite assessors to assess the competition
    the user enters text to a text field     id = assessorNameFilter  ${webTestAssessor}
    the user clicks the button/link          jQuery = .govuk-button:contains("Filter")
    the user clicks the button/link          jQuery = tr:contains("${webTestAssessor}") label[for^="assessor-row"]
    the user clicks the button/link          jQuery = .govuk-button:contains("Add selected to invite list")
    the user clicks the button/link          link = Invite
    the user clicks the button/link          link = Review and send invites
    the user clicks the button/link          jQuery = .govuk-button:contains("Send invitation")
    the user logs out if they are logged in

the assessor accepts an invite to an application
    logging in and error checking         ${webTestAssessorEmailAddress}   ${short_password}
    the user clicks the button/link       link = ${hestaCompetitionName}
    the user selects the radio button     acceptInvitation  true
    the user clicks the button/link       jQuery = button:contains("Confirm")
