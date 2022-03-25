*** Settings ***
Documentation     IFS-10694 Hesta - Email notification content for application submission
...
...               IFS-10688 Hesta - Create competition type hesta
...
...               IFS-10695 Hesta - Email notification content for unsuccessfull application
...
...               IFS-10697 Hesta - Application Submission confirmation page
...
...               IFS-11269 HECP Phase 2 - Changes to cost categories
...
...               IFS-11299 HECP Phase 1 - EIC - New GOL Template
...
...               IFS-11618 HECP Phase 2 - Cost categories - Application view additional updates
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
    [Documentation]  IFS-8751  IFS-11269  IFS-11618
    Given the user logs out if they are logged in
    When the user successfully completes application          tim   timmy   ${leadApplicantEmail}   ${hestaApplicationName}
    And the user clicks the button/link                       link = Your project finances
    Then the user completes hecp project finances             ${hestaApplicationName}  no
    Then the user can submit the application

Lead applicant should get a confirmation email after application submission
    [Documentation]    IFS-10694
    Given Requesting IDs of this application    ${hestaApplicationName}
    Then the user reads his email               ${leadApplicantEmail}  ${ApplicationID}: ${hestaApplicationSubmissionEmailSubject}  ${hestaApplicationSubmissionEmail}

The Application Summary page must not include the Reopen Application link when the internal team mark the application as successful / unsuccessful
    [Documentation]  IFS-10697  IFS-11406
    Given Log in as a different user                                                &{Comp_admin1_credentials}
    And Requesting IDs of this competition                                          ${hestaCompetitionName}
    And Competition admin creates an assessment period                              ${competitionId}
    And comp admin sends invite to assesor
    And the assessor accepts an invite to an application
    And Log in as a different user                                                  &{Comp_admin1_credentials}
    And The user clicks the button/link                                             link = ${hestaCompetitionName}
    And assign the application to assessor                                          ${hestaApplicationName}
    When the internal team mark the application as successful / unsuccessful        ${hestaApplicationName}   FUNDED
    And Log in as a different user                                                  email=${leadApplicantEmail}   password=${short_password}
    Then the application summary page must not include the reopen application link
    And the user should see the element                                            jQuery = h1:contains("Application status")
    And the user is presented with the Application Summary page

Lead applicant receives email notifiction when internal user marks application unsuccessful
    [Documentation]  IFS-10695  IFS-11341
    Given the user logs out if they are logged in
    And Requesting IDs of this competition                                          ${hestaCompetitionName}
    And the user successfully completes application                                 barry   barrington   ${newLeadApplicantEmail}   ${newHestaApplicationName}
    And the user clicks the button/link                                             link = Your project finances
    And the user completes hecp project finances                                    ${hestaApplicationName}  no
    And the user can submit the application
    And Log in as a different user                                                  &{Comp_admin1_credentials}
    And The user clicks the button/link                                             link = ${hestaCompetitionName}
    And assign the application to assessor                                          ${newHestaApplicationName}
    When the internal team mark the application as successful / unsuccessful        ${newHestaApplicationName}   UNFUNDED
    And the user clicks the button/link                                             link = Competition
    And Requesting IDs of this application                                          ${newHestaApplicationName}
    And the internal team notifies all applicants                                   ${ApplicationID}
    Then the user reads his email                                                   ${newLeadApplicantEmail}  ${ApplicationID}: ${hestaApplicationUnsuccessfulEmailSubject}  ${hestaApplicationUnsuccessfulEmail}

Internal user can view hecp GOL template
    [Documentation]  IFS-11299
    Given the user completes all project setup sections
    When the user clicks the button/link                            jQuery = td:contains("Review")
    And user clicks on View the grant offer letter page
    And Select Window                                               NEW
    Then the user should see the element                            xpath = //h2[text()='Accepting your award ']
    [Teardown]  the user closes the last opened tab


*** Keywords ***
user clicks on View the grant offer letter page
    the user clicks the button/link        link = View the grant offer letter page (opens in a new window)

project finance approves eligibility and generates the Spend Profile
    [Arguments]  ${lead}  ${project}
    project finance approves Viability for  ${lead}  ${project}
    the user navigates to the page          ${server}/project-setup-management/project/${project}/finance-check/organisation/${lead}/eligibility
    the user approves project costs
    the user navigates to the page          ${server}/project-setup-management/project/${project}/finance-check
    the user clicks the button/link         css = .generate-spend-profile-main-button
    the user clicks the button/link         css = #generate-spend-profile-modal-button

The user fills in bank details
    the user clicks the button/link                      link = Bank details
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    applicant user enters bank details

Internal user reviews and approves documents
    log in as a different user                          &{ifs_admin_user_credentials}
    the user navigates to the page                      ${server}/project-setup-management/project/${hestaProjectID}/document/all
    the user clicks the button/link                     link = Exploitation plan
    the user clicks the button/link                     id = radio-review-approve
    the user clicks the button/link                     id = submit-button
    the user clicks the button/link                     id = accept-document

Internal user approves bank details
    the user navigates to the page                      ${server}/project-setup-management/project/${hestaProjectID}/organisation/${asosId}/review-bank-details
    the user clicks the button/link                     jQuery = button:contains("Approve bank account details")
    the user clicks the button/link                     id = submit-approve-bank-details

The user is able to complete and submit the spend profile
    Log in as a different user                          ${leadApplicantEmail}    ${short_password}
    the user navigates to the page                      ${server}/project-setup/project/${hestaProjectID}/partner-organisation/${asosId}/spend-profile/review
    the user clicks the button/link                      id = spend-profile-mark-as-complete-button
    the user clicks the button/link                      link = Review and submit project spend profile
    the user clicks the button/link                      id = submit-project-spend-profile-button
    the user clicks the button/link                      id = submit-send-all-spend-profiles

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

Requesting Project ID of this Project
    ${hestaProjectID} =  get project id by name    ${hestaApplicationName}
    Set suite variable    ${hestaProjectID}

Requesting IDs of this Hesta application
    ${hestaApplicationID} =  get application id by name    ${hestaApplicationName}
    Set suite variable    ${hestaApplicationID}

Requesting IDs of this Asos Organisation
    ${asosId} =    get organisation id by name     ${asosName}
    Set suite variable      ${asosId}

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
    the applicant completes Application Team                        COMPLETE  ${email}
    the user completes the application research category            Feasibility studies
    The user is able to complete hecp public description section
    The user is able to complete horizon grant agreement section
    the lead applicant fills all the questions and marks as complete(Hesta)
    the user accept the competition terms and conditions            Back to application overview

the user is presented with the Application Summary page
    the user should see the element          jQuery = h2:contains("Application submitted")
    the user should see the element          jQuery = .govuk-panel:contains("Application number: ${ApplicationID}")
    the user should see the element          jQuery = h2:contains("What happens next?")
    the user should see the element          jQuery = h3:contains("Verification checks")
    the user should see the element          jQuery = h3:contains("Once your application is verified")
    the user should see the element          jQuery = h3:contains("Application feedback")

the internal team mark the application as successful / unsuccessful
    [Arguments]   ${applicationName}   ${decision}
    the user navigates to the page      ${server}/management/competition/${competitionId}
    the user clicks the button/link     link = Input and review funding decision
    the user clicks the button/link     jQuery = tr:contains("${applicationName}") label
    the user clicks the button/link     css = [type="submit"][value="${decision}"]

the internal team notifies all applicants
    [Arguments]  ${ApplicationID}
    the user clicks the button/link                      link = Send notification and release feedback
    the user clicks the button/link                      jQuery = tr:contains(${ApplicationID}) label
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
    the user marks each question as complete                            Public description
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

Custom Suite Teardown
    the user closes the browser
    Disconnect from database

the user completes hecp project finances
    [Arguments]  ${Application}   ${Project_growth_table}
    The user is able to complete hecp project costs
    the user enters the project location
    Run Keyword if  '${Project_growth_table}' == 'no'    the user fills in the organisation information  ${Application}  ${SMALL_ORGANISATION_SIZE}
    Run Keyword if  '${Project_growth_table}' == 'yes'  the user fills the organisation details with Project growth table  ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section        ${Application}
    the user should see all finance subsections complete
    the user clicks the button/link  link = Back to application overview
    the user should see the element  jQuery = li:contains("Your project finances") > .task-status-complete

The user is able to complete hecp project costs
    the user clicks the button/link           link = Your project costs
    the user should see the element           jQuery = h1:contains("Your project costs")
    the user should see the element           jQuery = span:contains("Personnel costs")
    the user should see the element           jQuery = span:contains("Subcontracting costs")
    the user should see the element           jQuery = span:contains("Travel and subsistence")
    the user should see the element           jQuery = span:contains("Equipment")
    the user should see the element           jQuery = span:contains("Other goods, works and services")
    the user should see the element           jQuery = span:contains("Other costs")
    the user should see the element           jQuery = span:contains("Indirect costs")
    the user enters text to a text field      id = labour  50000
    the user enters text to a text field      id = subcontracting  50000
    the user enters text to a text field      id = travel  10000
    the user enters text to a text field      id = material  30000
    the user enters text to a text field      id = capital  20000
    the user enters text to a text field      id = other  40000
    the user enters text to a text field      id = overhead  0
    the user clicks the button/link           jQuery = button:contains("Mark")
    the user should see the element           jQuery = li:contains("Your project costs") > .task-status-complete

The user is able to complete hecp public description section
    the user clicks the button/link           jQuery = a:contains("Public description")
    the user should see the element           jQuery = h1:contains("Public description")
    the user enters text to a text field      css=.textarea-wrapped .editor    This is some random text
    the user clicks the button/link           id = application-question-complete
    the user clicks the button/link           jQuery = a:contains("Return to application overview")
    the user should see the element           jQuery = li:contains("Public description") > .task-status-complete

The user is able to complete horizon grant agreement section
    the user clicks the button/link           jQuery = a:contains("Horizon Europe Guarantee grant agreement")
    the user should see the element           jQuery = h1:contains("grant agreement")
    the user uploads the file                 id = grantAgreement  ${valid_pdf}
    the user clicks the button/link           id = mark-as-complete
    the user clicks the button/link           link = Return to application overview
    the user should see the element           jQuery = li:contains("Horizon Europe Guarantee grant agreement") > .task-status-complete


IFS admin approves the spend profiles for hestaApplication
    [Arguments]  ${project}
    log in as a different user       &{ifs_admin_user_credentials}
    the user navigates to the page   ${server}/project-setup-management/project/${project}/spend-profile/approval
    #the user selects the checkbox    approvedByLeadTechnologist
    the user clicks the button/link  id = radio-spendprofile-approve
    the user clicks the button/link  id = submit-button

the user completes all project setup sections
    Requesting IDs of this Hesta application
    Requesting IDs of this Asos Organisation
    the internal team mark the application as successful / unsuccessful         ${hestaApplicationID}  FUNDED
    the user clicks the button/link                                             link = Competition
    the internal team notifies all applicants                                   ${hestaApplicationID}
    the user refreshes until element appears on page                            jQuery = td:contains("${hestaApplicationID}") ~ td:contains("Sent")
    log in as a different user                                                  ${leadApplicantEmail}    ${short_password}
    the user clicks the button/link                                             link = ${hestaApplicationName}
    the user is able to complete project details section
    the user completes the project team details
    the user is able to complete the Documents section
    the user fills in bank details
    log in as a different user                                                  &{internal_finance_credentials}
    internal user assigns MO to application                                     ${hestaApplicationID}    ${hestaApplicationName}    Orvill  Orville Gibbs
    Requesting Project ID of this Project
    project finance approves eligibility and generates the Spend Profile        ${asosId}  ${hestaProjectID}
    Internal user reviews and approves documents
    Internal user approves bank details
    The user is able to complete and submit the spend profile
    IFS admin approves the spend profiles for hestaApplication                  ${hestaProjectID}