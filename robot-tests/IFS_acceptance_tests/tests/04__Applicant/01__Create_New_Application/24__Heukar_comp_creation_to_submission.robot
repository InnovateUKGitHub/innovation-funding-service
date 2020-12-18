*** Settings ***
Documentation     IFS-8638: Create new competition type
...
...               IFS-8751: Increase project duration in months
...
...               IFS-8769: Email notification for application submission
...
...               IFS-8752: Application Submission confirmation page
...
...               IFS-8641: Email notification of unsuccessful application
...
...               IFS-8662: HEUKAR stage 1 privacy notice
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/keywords/05__Email_Keywords.robot

*** Variables ***
${heukarCompTypeSelector}                       dt:contains("Competition type") ~ dd:contains("${compType_HEUKAR}")
${heukarApplicationName}                        Heukar application
${newHeukarApplicationName}                     NEW Heukar application
${leadApplicantEmail}                           tim.timmy@heukar.com
${newLeadApplicantEmail}                        barry.barrington@heukar.com
${heukarApplicationSubmissionEmailSubject}      confirmation of your Horizon Europe UK Application Registration
${heukarApplicationUnsuccessfulEmailSubject}    update about your Horizon Europe UK Application Registration for government-backed funding
${huekarApplicationSubmissionEmail}             We have received your stage 1 pre-registration to the Horizon Europe UK Application Registration programme
${huekarApplicationUnsuccessfulEmail}           We have been advised you were unsuccessful in your grant application for Horizon Europe funding from The European Commission

*** Test Cases ***
Comp admin can select the competition type option Heukar in Initial details on competition setup
    [Documentation]  IFS-8638
    Given the user logs-in in new browser             &{Comp_admin1_credentials}
    When the user navigates to the page               ${CA_UpcomingComp}
    And the user clicks the button/link               jQuery = .govuk-button:contains("Create competition")
    Then the user fills in the CS Initial details     ${heukarCompetitionName}  ${month}  ${nextyear}  ${compType_HEUKAR}  NOT_AID  GRANT

Comp admin can view Heukar competition type in Initial details read only view
    [Documentation]  IFS-8638
    Given the user clicks the button/link    link = Initial details
    Then the user can view Heukar competition type in Initial details read only view

Comp admin can select Horizon Europe – UK Application Registration Privacy Policy to add to the competition
    [Documentation]  IFS-8662
    Given the user clicks the button/link               link = Back to competition details
    When the user selects the HEUKAR Privacy Notice
    Then the user views the HEUKAR privacy notice
    And the user should see the element                 jQuery = li:contains("Terms and conditions") .task-status-complete

Comp admin creates Heukar competition
    [Documentation]  IFS-8751
    Given the competition admin creates Heukar competition     ${BUSINESS_TYPE_ID}  ${heukarCompetitionName}  ${compType_HEUKAR}  ${compType_HEUKAR}  GRANT  RELEASE_FEEDBACK  no  1  false  single-or-collaborative
    Then get competition id and set open date to yesterday     ${heukarCompetitionName}

Lead applicant can submit application
    [Documentation]  IFS-8751
    Given the user logs out if they are logged in
    When the user successfully completes application     tim   timmy   ${leadApplicantEmail}   ${heukarApplicationName}
    Then the user can submit the application

Lead applicant is presented with the Application Summary page when an application is submitted and should get a confirmation email
    [Documentation]  IFS-8752
    Given the user should see the element       jQuery = h1:contains("Application status")
    When Requesting IDs of this application     ${heukarApplicationName}
    Then the user is presented with the Application Summary page
    And the user reads his email                ${leadApplicantEmail}  ${ApplicationID}: ${heukarApplicationSubmissionEmailSubject}  ${huekarApplicationSubmissionEmail}

The Application Summary page must not include the Reopen Application link when the internal team mark the application as successful / unsuccessful
    [Documentation]  IFS-8752
    Given Log in as a different user                                               &{Comp_admin1_credentials}
    And Requesting IDs of this competition                                         ${heukarCompetitionName}
    When the internal team mark the application as successful / unsuccessful       ${heukarApplicationName}   FUNDED
    And Log in as a different user                                                 email=${leadApplicantEmail}    password=${short_password}
    Then the application summary page must not include the reopen application link

Lead applicant receives email notifiction when internal user marks application unsuccessful
    [Documentation]  IFS-8641
    Given the user logs out if they are logged in
    And the user successfully completes application                              barry   barrington   ${newLeadApplicantEmail}   ${newHeukarApplicationName}
    And the user can submit the application
    And log in as a different user                                               &{Comp_admin1_credentials}
    When the internal team mark the application as successful / unsuccessful     ${newHeukarApplicationName}   UNFUNDED
    And the user clicks the button/link                                          link = Competition
    And Requesting IDs of this application                                       ${newHeukarApplicationName}
    And the internal team notifies all applicants
    Then the user reads his email                                                ${newLeadApplicantEmail}  ${ApplicationID}: ${heukarApplicationUnsuccessfulEmailSubject}  ${huekarApplicationUnsuccessfulEmail}

*** Keywords ***
the user can view Heukar competition type in Initial details read only view
    the user should see the element     jQuery = ${heukarCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Edit")
    the user should see the element     jQuery = ${heukarCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Done")

the competition admin creates HEUKAR competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
# REMOVE/ADD NEGATIVE CASE FUNDING INFORMATION IN NEXT SPRINT
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}  ${researchParticipation}  ${collaborative}  # 1 means 30%
    the user fills in the CS Funding eligibility            ${researchCategory}
    the user selects the organisational eligibility         true    true
    the user fills in the CS Milestones                     ${completionStage}   ${month}   ${nextyear}
    the user marks the application as done                  ${projectGrowth}  ${compType}  ${competition}
# REMOVE/ADD NEGATIVE CASE ASSESSORS IN NEXT SPRINT
#    the user fills in the CS Assessors                      ${fundingType}
# REMOVE/ADD NEGATIVE CASE DOCUMENTS IN NEXT SPRINT
#    the user fills in the CS Documents in other projects
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview
    the user clicks the button/link                         jQuery = a:contains("Complete")
    the user clicks the button/link                         jQuery = button:contains('Done')
    the user navigates to the page                          ${CA_UpcomingComp}
    the user should see the element                         jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user selects the HEUKAR Privacy Notice
    the user clicks the button/link                     link = Terms and conditions
    the user sees that the radio button is selected     termsAndConditionsId  termsAndConditionsId5
    the user should see the element                     link = Horizon Europe UK Application Privacy Notice (opens in a new window)
    the user clicks the button/link                     jQuery = button:contains("Done")

the user views the HEUKAR privacy notice
    the user clicks the button/link     link = Horizon Europe UK Application Privacy Notice (opens in a new window)
    Select Window                       title = Terms and conditions of an Innovate UK grant award - Innovation Funding Service
    the user should see the element     jQuery = h1:contains("Horizon Europe UK Application Privacy Notice")
    the user should see the element     jQuery = p:contains("Any personal data that you provide to UK Research and Innovation")
    the user clicks the button/link     link = UK Research and Innovation Privacy Notice (opens in a new window)
    Select Window                       title = Privacy notice – UKRI
    Close Window
    Select Window                       title = Terms and conditions of an Innovate UK grant award - Innovation Funding Service
    the user clicks the button/link     link = Innovate UK Privacy Notice and Information Management Policy (opens in a new window)
    Select Window                       title = Privacy notice and information management policy: Innovate UK - GOV.UK
    Close Window
    Select Window                       title = Terms and conditions of an Innovate UK grant award - Innovation Funding Service
    Close Window
    Select Window                       title = Competition terms and conditions - Innovation Funding Service
    the user clicks the button/link     link = Back to competition details

Requesting IDs of this application
    [Arguments]  ${applicationName}
    ${ApplicationID} =  get application id by name    ${applicationName}
    Set suite variable    ${ApplicationID}

Requesting IDs of this competition
    [Arguments]  ${competitionName}
    ${competitionId} =  get comp id from comp title  ${competitionName}
    Set suite variable  ${competitionId}

user selects where is organisation based
    [Arguments]  ${org_type}
    the user selects the radio button     international  ${org_type}
    the user clicks the button/link       id = international-organisation-cta

the user completes Heukar Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}  ${projectDuration}
    the user should see the element             jQuery = h1:contains("Application details")
    the user enters text to a text field        id = name  ${appTitle}
    the user enters text to a text field        id = startDate  ${tomorrowday}
    the user enters text to a text field        css = #application_details-startdate_month  ${month}
    the user enters text to a text field        css = #application_details-startdate_year  ${nextyear}
    the user should see the element             jQuery = label:contains("Project duration in months")
    the user enters text to a text field        css = [id="durationInMonths"]  ${projectDuration}
    the user clicks the button twice            css = label[for="resubmission-no"]
    the user successfully marks Application details as complete

the user successfully marks Application details as complete
    the user clicks the button/link             id = application-question-complete
    the user clicks the button/link             link = Back to application overview
    the user should see the element             jQuery = li:contains("Application details") > .task-status-complete

the user successfully completes application
    [Arguments]   ${firstName}   ${lastName}   ${email}   ${applicationName}
    the user select the competition and starts application          ${heukarCompetitionName}
    the user clicks the button/link                                 link = Continue and create an account
    user selects where is organisation based                        isNotInternational
    the user selects the radio button                               organisationTypeId    radio-1
    the user clicks the button/link                                 jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House            innovate  INNOVATE LTD
    the user should be redirected to the correct page               ${SERVER}/registration/register
    the user enters the details and clicks the create account       ${firstName}  ${lastName}  ${email}  ${short_password}
    the user reads his email and clicks the link                    ${email}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page               ${REGISTRATION_VERIFIED}
    the user clicks the button/link                                 link = Sign in
    Logging in and Error Checking                                   ${email}  ${short_password}
    the user clicks the button/link                                 link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link                                 link = Application details
    the user completes Heukar Application details                   ${applicationName}  ${tomorrowday}  ${month}  ${nextyear}  84
    the applicant completes Application Team
    the applicant marks EDI question as complete
    the lead applicant fills all the questions and marks as complete(heukar)
    the user accept the competition terms and conditions            Back to application overview

the user is presented with the Application Summary page
    the user should see the element          jQuery = h2:contains("Application submitted")
    the user should see the element          jQuery = .govuk-panel:contains("Application number: ${ApplicationID}")
    the user should see the element          link = Reopen application
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
    the user clicks the button/link                      link = Manage funding notifications
    the user clicks the button/link                      id = app-row-${ApplicationID}
    the user clicks the button/link                      id = write-and-send-email
    the user clicks the button/link                      id = send-email-to-all-applicants
    the user clicks the button/link                      id = send-email-to-all-applicants-button
    the user refreshes until element appears on page     jQuery = td:contains("Sent")

the application summary page must not include the reopen application link
    the user navigates to the page          ${server}/application/${ApplicationID}/track
    the user should not see the element     link = Reopen application

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom Suite Teardown
    the user closes the browser
    Disconnect from database
