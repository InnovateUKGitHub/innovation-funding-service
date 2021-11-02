*** Settings ***
Documentation     IFS-10694 Hesta - Email notification content for application submission
...
...               IFS-10688 Hesta - Create competition type hesta
...
...               IFS-10695 Hesta - Email notification content for unsuccessfull application
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${hestaApplicationSubmissionEmailSubject}    confirmation of your Horizon Europe UK Application Registration
${hestaApplicationSubmissionEmail}           We have received your stage 1 pre-registration to the Horizon Europe UK Application Registration programme
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
    Given the user clicks the button/link                             link = Back to competition details
    Then the competition admin creates Hesta competition             ${BUSINESS_TYPE_ID}  ${hestaCompetitionName}  ${compType_HESTA}  ${compType_HESTA}  STATE_AID  GRANT  RELEASE_FEEDBACK  no  1  false  single-or-collaborative
    [Teardown]  Get competition id and set open date to yesterday    ${hestaCompetitionName}

Lead applicant can submit application
    [Documentation]  IFS-8751
    Given the user logs out if they are logged in
    When the user successfully completes application
    And the user clicks the button/link                       link = Your project finances
    Then the user marks the finances as complete              ${hestaApplicationName}  labour costs  54,000  no
    Then the user can submit the application

Lead applicant should get a confirmation email after application submission
    [Documentation]    IFS-10694
    Given Requesting IDs of this application
    Then the user reads his email     ${leadApplicantEmail}  ${ApplicationID}: ${hestaApplicationSubmissionEmailSubject}  ${hestaApplicationSubmissionEmail}

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
    the user fills in the CS Milestones                     ${completionStage}   ${month}   ${nextyear}  No
    the user marks the Hesta application question as done   ${projectGrowth}  ${compType}  ${competition}
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview
    the user clicks the button/link                         jQuery = a:contains("Complete")
    the user clicks the button/link                         jQuery = button:contains('Done')
    the user navigates to the page                          ${CA_UpcomingComp}
    the user should see the element                         jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

Requesting IDs of this application
    ${ApplicationID} =  get application id by name    ${hestaApplicationName}
    Set suite variable    ${ApplicationID}

user selects where is organisation based
    [Arguments]  ${org_type}
    the user selects the radio button     international  ${org_type}
    the user clicks the button/link       id = international-organisation-cta

the user successfully completes application
    the user select the competition and starts application          ${hestaCompetitionName}
    the user clicks the button/link                                 link = Continue and create an account
    user selects where is organisation based                        isNotInternational
    the user selects the radio button                               organisationTypeId    radio-1
    the user clicks the button/link                                 jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House            ASOS  ASOS PLC
    the user should be redirected to the correct page               ${SERVER}/registration/register
    the user enters the details and clicks the create account       tim  timmy  ${newLeadApplicantEmail}  ${short_password}
    the user reads his email and clicks the link                    ${newLeadApplicantEmail}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page               ${REGISTRATION_VERIFIED}
    the user clicks the button/link                                 link = Sign in
    Logging in and Error Checking                                   ${newLeadApplicantEmail}  ${short_password}
    the user clicks the button/link                                 link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user completes the application details section              ${hestaApplicationName}  ${tomorrowday}  ${month}  ${nextyear}  84
    the applicant completes Application Team
    the user completes the application research category            Feasibility studies
    the lead applicant fills all the questions and marks as complete(Hesta)
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

the internal team mark the application as successful
    the user navigates to the page      ${server}/management/competition/${competitionId}
    the user clicks the button/link     link = Input and review funding decision
    the user clicks the button/link     jQuery = tr:contains("${hestaApplicationName}") label
    the user clicks the button/link     css = [type="submit"][value="FUNDED"]

the application summary page must not include the reopen application link
    the user navigates to the page          ${server}/application/${ApplicationID}/track
    the user should not see the element     link = Reopen application

the user marks the Hesta application question as done
    [Arguments]  ${growthTable}  ${comp_type}  ${competition}
    the user clicks the button/link                                     link = Application
    the user marks each question as complete                            Application details
    the user fills in the CS Application section with custom questions  ${growthTable}  ${comp_type}

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom Suite Teardown
    the user closes the browser
    Disconnect from database