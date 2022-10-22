*** Settings ***
Documentation     IFS-10694 Horizon Europe - Email notification content for application submission
...
...               IFS-10688 Horizon Europe - Create competition type Horizon Europe
...
...               IFS-10695 Horizon Europe - Email notification content for unsuccessful application
...
...               IFS-10697 Horizon Europe - Application Submission confirmation page
...
...               IFS-11269 HECP Phase 2 - Changes to cost categories
...
...               IFS-11486 HECP Phase 2 - Always open functionality to have the the ability to bypass IFS assessment
...
...               IFS-11299 HECP Phase 1 - EIC - New GOL Template
...
...               IFS-11366 HECP Phase 2 - Custom Question - Work Programme
...
...               IFS-11618 HECP Phase 2 - Cost categories - Application view additional updates
...
...               IFS-11688 HECP Phase 2 - Template update
...
...               IFS-11551 HECP Phase 2 - Spend profile - Content change
...
...               IFS-11511 HECP Phase 2 - Notification banners
...
...               IFS-11510 HECP Phase 2 - Remove content from 'View application feedback' link
...
...               IFS-11686 HECP Phase 2 - Read only views - Custom Question - Work Programme
...
...               IFS-11407 HECP Phase 2 - Cost categories - Project Setup views
...
...               IFS-11695 HECP Phase 2 - Cost categories - Spend profile updates
...
...               IFS-11791 HECP Phase 2 - Bug bash changes - Content
...
...               IFS-11794 HECP Phase 2 - Bug bash changes - Clicking the work programme labels does not select the radio button
...
...               IFS-11758 HECP Phase 2- Spend profile cost categories validations
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${horizonCompTypeSelector}                dt:contains("Competition type") ~ dd:contains("${compType_HORIZON_EUROPE}")
${horizonApplicationName}                 Horizon Europe application
${newHorizonApplicationName}              NEW Horizon Europe application
${leadApplicantEmail}                           tim.timmy@heukar.com
${newLeadApplicantEmail}                        barry.barrington@heukar.com
${horizonApplicationSubmissionEmailSubject}     Successful submission of application
${horizonApplicationSubmissionEmail}            You have successfully submitted an application for funding to
${horizonApplicationSuccessfulEmail}            We are pleased to inform you that your application for the Horizon Europe collaborative competition has been successful and passed the technical assessment phase.
${horizonApplicationUnsuccessfulEmail}          Thank you for submitting your application to Innovate UK for the competition
${horizonApplicationUnsuccessfulEmailSubject}   update about your Horizon Europe Guarantee application
${assessorEmail}                                another.person@gmail.com
${webTestAssessor}                              Angel Witt
${webTestAssessorEmailAddress}                  angel.witt@gmail.com

*** Test Cases ***
Comp admin can select the competition type option Horizon Europe in Initial details on competition setup
    [Documentation]  IFS-10688
    Given the user logs-in in new browser             &{Comp_admin1_credentials}
    When the user navigates to the page               ${CA_UpcomingComp}
    And the user clicks the button/link               jQuery = .govuk-button:contains("Create competition")
    Then the user fills in the CS Initial details     ${horizonCompetitionName}  ${month}  ${nextyear}  ${compType_HORIZON_EUROPE}  STATE_AID  HECP

Comp admin can view Horizon Europe competition type in Initial details read only view
    [Documentation]  IFS-10688
    Given the user clicks the button/link    link = Initial details
    Then the user can view Horizon Europe competition type in Initial details read only view

Comp admin creates Horizon Europe competition
    [Documentation]  IFS-8751  IFS-11486
    Given the user clicks the button/link                            link = Back to competition details
    Then the competition admin creates Horizon Europe competition    ${BUSINESS_TYPE_ID}  ${horizonCompetitionName}  ${compType_HORIZON_EUROPE}  ${compType_HORIZON_EUROPE}  STATE_AID  HECP  PROJECT_SETUP  no  50  false  single-or-collaborative
    [Teardown]  Get competition id and set open date to yesterday    ${horizonCompetitionName}

the lead applicant can view answer yet to be provided when work programme question is incomplete in readonly view
    [Documentation]  IFS-11686
    Given the user logs out if they are logged in
    And the user applys to the competition          tim   timmy   ${leadApplicantEmail}   ${horizonApplicationName}
    When the user clicks the button/link            link = Review and submit
    And the user clicks the button/link             jQuery = button:contains("Work programme")
    Then the user should see the element            jQuery = p:contains("Answer yet to be provided")

lead applicant views work programme answers provided in review and submit page
    [Documentation]  IFS-11686  IFS-11791  IFS-11794
    Given the user clicks the button/link                           link = Application overview
    When the user complete the work programme
    And the user clicks the button/link                             link = Review and submit
    Then the user can see the read only view of work programme

Lead applicant can view funding conversion tool in project costs
    [Documentation]  IFS-11508  IFS-11686  IFS-11791
    Given the user clicks the button/link                                       link = Application overview
    And the user completes the application research category                    Feasibility studies
    And the user is able to complete horizon grant agreement section
    And the user should see Participating Organisation project region
    And the lead applicant fills all the questions and marks as complete(Hecp)
    And the user accept the competition terms and conditions                    Back to application overview
    When the user clicks the button/link                                        link = Your project finances
    And the user clicks the button/link                                         link = Your project costs
    Then the user should see the element                                        jQuery = a:contains("Horizon Europe guarantee notice and guidance – UKRI (opens in a new window)")
    And the user should see the element                                         jQuery = a:contains("heguarantee@iuk.ukri.org")

Lead applicant completes project finances and submits an application
    [Documentation]  IFS-8751  IFS-11269  IFS-11618  IFS-11366
    When the user clicks the button/link                                        link = Your project finances
    And the user completes hecp project finances                                ${horizonApplicationName}  no
    Then the user see the print view of the application
    And the user can submit the application

Lead applicant should get a confirmation email after application submission
    [Documentation]    IFS-10694
    Given Requesting IDs of this application    ${horizonApplicationName}
    Then the user reads his email               ${leadApplicantEmail}  ${ApplicationID}: ${horizonApplicationSubmissionEmailSubject}  ${horizonApplicationSubmissionEmail}

Applicant receives successful message of an application
    [Documentation]  IFS-11554
    Given Log in as a different user                                                &{Comp_admin1_credentials}
    And The user clicks the button/link                                             link = ${horizonCompetitionName}
    When Internal user notifies the applicant on status of application
    Then the user reads his email                                                   ${leadApplicantEmail}  Important message about your application '${horizonApplicationName}' for the competition '${horizonCompetitionName}'  ${horizonApplicationSuccessfulEmail}

The Application Summary page must not include the Reopen Application link when the internal team mark the application as successful / unsuccessful
    [Documentation]  IFS-10697  IFS-11406  IFS-11486
    When Log in as a different user                                                email=${leadApplicantEmail}   password=${short_password}
    Then the application summary page must not include the reopen application link
    And the user should see the element                                             jQuery = h1:contains("Application status")
    And the user is presented with the Application Summary page

Lead applicant receives email notifiction when internal user marks application unsuccessful
    [Documentation]  IFS-10695  IFS-11341  IFS-11486
    Given the user logs out if they are logged in
    And Requesting IDs of this competition                                          ${horizonCompetitionName}
    And the user applys to the competition                                          barry   barrington   ${newLeadApplicantEmail}   ${newHorizonApplicationName}
    And the user successfully completes application
    And the user clicks the button/link                                             link = Your project finances
    And the user completes hecp project finances                                    ${horizonApplicationName}  no
    And the user can submit the application
    And Log in as a different user                                                  &{Comp_admin1_credentials}
    And The user clicks the button/link                                             link = ${horizonCompetitionName}
    When the internal team mark the application as successful / unsuccessful        ${newHorizonApplicationName}   UNFUNDED
    And the user clicks the button/link                                             link = Competition
    And Requesting IDs of this application                                          ${newHorizonApplicationName}
    And the internal team notifies all applicants                                   ${ApplicationID}
    Then the user reads his email                                                   ${newLeadApplicantEmail}  Important message about your application '${newHorizonApplicationName}' for the competition '${horizonCompetitionName}'  ${horizonApplicationUnsuccessfulEmail}

the user should not see any references to assessment and release feedback on close competition page
    [Documentation]  IFS-11486
    When the user navigates to the page   ${server}/management/competition/${competitionId}/always-open
    Then the user should see the element  jQuery = li:contains("A submission date as been set and is now in the past.")
    And the user should see the element   jQuery = li:contains("All funding decision notifications have been sent.")
    And the user should see the element   jQuery = p:contains("Once this competition is closed you will no longer be able to add funding decisions.")
    And the element should be disabled    jQuery = button:contains("Close competition")

Applicant can view application link when in project setup
    [Documentation]  IFS-11510
    When the applicant navigates to project set up
    Then The user should see the text in the element               link = view application  view application

Lead applicant views hecp related cost categoires in project setup finances
    [Documentation]  IFS-11407
    Given the user is able to complete project details section
    And the user completes the project team details
    And the user completes hecp documents section
    And the user fills in bank details
    When the user clicks the button/link                        link = Finance checks
    And the user clicks the button/link                         link = your project finances
    Then the user should see hecp project cost categories
    And the user should see readonly detailed hecp finances

Internal users can view workp programmes section in view application
    [Documentation]  IFS-11686
    Given log in as a different user            &{internal_finance_credentials}
    And Requesting Project ID of this Project
    When the user navigates to the page         ${server}/management/competition/${competitionId}/application/${horizonApplicationID}
    Then the user can see the read only view of work programme

Internal users can edit the project costs
    [Documentation]  IFS-11407
    When the user navigates to the page         ${server}/project-setup-management/project/${horizonProjectID}/finance-check/organisation/${asosId}/eligibility
    And the user clicks the button/link         name = edit-project-costs
    And the user enters text to a text field    id = hecpIndirectCosts  10000
    And the user enters text to a text field    id = travel  20000
    And the user clicks the button/link         id = save-eligibility
    Then the user should see the element        jQuery = label:contains("Travel and subsistence") ~ span:contains("20,000")
    And the user should see the element         jQuery = label:contains("Indirect costs") ~ span:contains("10,000")
    And the user should see the element         css = [id="total-cost"][value="£220,000"]

Lead applicant views hecp project cost categories in spendprofile
    [Documentation]  IFS-11695  IFS-11551
    Given project finance approves eligibility and generates the Spend Profile      ${asosId}  ${horizonProjectID}
    And internal user assigns MO to application                                     ${horizonApplicationID}    ${horizonApplicationName}    Orvill  Orville Gibbs
    And Internal user reviews and approves documents
    And Internal user approves bank details
    When Log in as a different user                                                 ${leadApplicantEmail}    ${short_password}
    And the user navigates to the page                                              ${server}/project-setup/project/${horizonProjectID}/partner-organisation/${asosId}/spend-profile/review
    Then the user should see hecp project cost categories
    And the user should see the element                                             jQuery = p:contains("We have reviewed and confirmed your project costs. You should now develop a spend profile together with your project partners ​which estimates how you think costs will be spread out over the duration of your project")
    And the user should see the element                                             jQuery = p:contains("If you require further assistance in filling out your spend profile, contact your monitoring officer.")
    And the user should see the element                                             jQuery = p:contains("You need to mark this section as complete. You can then send completed spend profiles to Innovate UK.")

Lead applicant views Horizon project cost categories on validation and edit spend profile
    [Documentation]  IFS-11695  IFS-11758
    When the user clicks the button/link                                    link = Edit spend profile
    And the user edit the spend profile mothly values                       616  616  247  370  247  493  124
    And the user clicks the button/link                                     jQuery = button:contains("Save and return to spend profile overview")
    Then the user should see hecp project cost categories in summary box
    And the user should see hecp project cost categories
    [Teardown]   the user reverted the edited values in spend profile

Lead applicant submits spend profile to internal user for review
    [Documentation]  IFS-11551
    Given the user clicks the button/link   jQuery = button:contains("Save and return to spend profile overview")
    And the user clicks the button/link     id = spend-profile-mark-as-complete-button
    And the user clicks the button/link     link = Review and submit project spend profile
    And the user clicks the button/link     id = submit-project-spend-profile-button
    When the user clicks the button/link    id = submit-send-all-spend-profiles
    And the user navigates to the page      ${server}/project-setup/project/${horizonProjectID}/partner-organisation/${asosId}/spend-profile/review
    Then the user should see the element    jQuery = p:contains("We have reviewed and confirmed your project costs. You should now develop a spend profile together with your project partners ​which estimates how you think costs will be spread out over the duration of your project")

Internal user can view Horizon GOL template
    [Documentation]  IFS-11299
    Given ifs admin approves the spend profiles for horizonApplication    ${horizonProjectID}
    When the user clicks the button/link                                jQuery = td:contains("Review")
    And user clicks on View the grant offer letter page
    And Select Window                                                   NEW
    Then the user should see the element                                xpath = //h2[text()='Annex 1: acceptance of award']
    [Teardown]  the user closes the last opened tab

Lead Applicant can view banner message for a successful application
    [Documentation]  IFS-11511
    Given log in as a different user         ${leadApplicantEmail}  ${short_password}
    When the user clicks the button/link     link = ${horizonApplicationName}
    And the user clicks the button/link      link = view application
    Then the user should see the element     jQuery = h2:contains("Congratulations, your application has been successful")
    And the user should see the element      jQuery = p:contains("You have been successful in this round of funding.")

Lead Applicant can view banner message for a unsuccessful application
    [Documentation]  IFS-11511
    Given log in as a different user         ${newLeadApplicantEmail}  ${short_password}
    When the user clicks the button/link     link = ${newHorizonApplicationName}
    Then the user should see the element     jQuery = h2:contains("Your application has not been successful in this competition.")

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
    the user clicks the button/link                      link = Set up your project

Internal user reviews and approves documents
    log in as a different user        &{ifs_admin_user_credentials}
    the user navigates to the page    ${server}/project-setup-management/project/${horizonProjectID}/document/all
    the user clicks the button/link   link = Test document type
    the user clicks the button/link   id = radio-review-approve
    the user clicks the button/link   id = submit-button
    the user clicks the button/link   id = accept-document

Internal user approves bank details
    the user navigates to the page      ${server}/project-setup-management/project/${horizonProjectID}/organisation/${asosId}/review-bank-details
    the user clicks the button/link     jQuery = button:contains("Approve bank account details")
    the user clicks the button/link     id = submit-approve-bank-details

the user can view Horizon competition type in Initial details read only view
    the user should see the element     jQuery = ${horizonCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Edit")
    the user should see the element     jQuery = ${horizonCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Done")

the competition admin creates Horizon competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects the Terms and Conditions                   ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user completes project impact section                   No
    the user fills in the CS Project eligibility                ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user fills in the CS funding eligibility                true   ${compType_HORIZON_EUROPE}  ${fundingRule}
    the user selects the organisational eligibility to no       false
    the user completes milestones section
    the user marks the Horizon application question as done
    the user fills in the CS Documents in other projects
    the user clicks the button/link                             link = Public content
    the user fills in the Public content and publishes          ${extraKeyword}
    the user clicks the button/link                             link = Return to setup overview
    the user clicks the button/link                             jQuery = a:contains("Complete")
    the user clicks the button/link                             jQuery = button:contains('Done')
    the user navigates to the page                              ${CA_UpcomingComp}
    the user should see the element                             jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

Requesting IDs of this application
    [Arguments]  ${applicationName}
    ${ApplicationID} =  get application id by name    ${applicationName}
    Set suite variable    ${ApplicationID}

Requesting Project ID of this Project
    ${horizonProjectID} =  get project id by name    ${horizonApplicationName}
    Set suite variable    ${horizonProjectID}

Requesting IDs of this Horizon application
    ${horizonApplicationID} =  get application id by name    ${horizonApplicationName}
    Set suite variable    ${horizonApplicationID}

Requesting IDs of this Asos Organisation
    ${asosId} =    get organisation id by name     ${asosName}
    Set suite variable      ${asosId}

Requesting IDs of this competition
    [Arguments]  ${competitionName}
    ${competitionId} =  get comp id from comp title  ${horizonCompetitionName}
    Set suite variable  ${competitionId}

user selects where is organisation based
    [Arguments]  ${org_type}
    the user selects the radio button     international  ${org_type}
    the user clicks the button/link       id = international-organisation-cta

the user applies to the competition
    [Arguments]   ${firstName}   ${lastName}   ${email}   ${applicationName}
    the user select the competition and starts application          ${horizonCompetitionName}
    the user clicks the button/link                                 link = Continue and create an account
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

the user can see the read only view of work programme
    the user should see the element    jQuery = dt:contains("Select the Horizon Europe Work programme Part you applied to, e.g. CL2.")
    the user should see the element    jQuery = dd:contains("Culture, Creativity and Inclusive Society (CL2)")
    the user should see the element    jQuery = dt:contains("Select the call you applied to.")
    the user should see the element    jQuery = dd:contains("HORIZON-CL2-2021-DEMOCRACY-01")

the user successfully completes application
    the user completes the application research category            Feasibility studies
    the user complete the work programmes
    the user is able to complete horizon grant agreement section
    the lead applicant fills all the questions and marks as complete(Hecp)
    the user accept the competition terms and conditions            Back to application overview

the user successfully completes applications
    [Arguments]   ${firstName}   ${lastName}   ${email}   ${applicationName}
    the user select the competition and starts application          ${horizonCompetitionName}
    the user clicks the button/link                                 link = Continue and create an account
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
    the user complete the work programmes
    The user is able to complete horizon grant agreement section
    the lead applicant fills all the questions and marks as complete(Hecp)
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
    the user clicks the button/link                      link = Send notification
    the user clicks the button/link                      jQuery = tr:contains(${ApplicationID}) label
    the user clicks the button/link                      id = write-and-send-email
    the user clicks the button/link                      id = send-email-to-all-applicants
    the user clicks the button/link                      id = send-email-to-all-applicants-button
    the user refreshes until element appears on page     jQuery = td:contains("${ApplicationID}") ~ td:contains("Sent")

the application summary page must not include the reopen application link
    the user navigates to the page          ${server}/application/${ApplicationID}/track
    the user should not see the element     link = Reopen application

the user marks the Horizon application question as done
    the user clicks the button/link                                 link = Application
    the user marks each question as complete                        Application details
    the assessed questions are marked complete(HECP type)
    the user clicks the button/link                                 jQuery = .govuk-heading-s a:contains("Finances")
    the user clicks the button/link                                 jQuery = button:contains("Done")
    the user clicks the button/link                                 jQuery = button:contains("Done")
    the user clicks the button/link                                 link = Back to competition details
    the user should see the element                                 jQuery = div:contains("Application") ~ .task-status-complete

the user completes milestones section
    the user clicks the button/link                     link = Milestones
    the user clicks the button twice                    jQuery = label:contains("Project setup")
    the user clicks the button/link                     jQuery = button:contains("Done")
    the user completes application submission page      Yes
    the user inputs application expression of interest  No
    the user inputs application assessment decision     No
    the user clicks the button/link                     jQuery = button:contains("Done")
    the user clicks the button/link                     link = Back to competition details
    the user should see the element                     jQuery = div:contains("Milestones") ~ .task-status-complete

update assessment batch 1 milestone to yesterday
    [Arguments]  ${competition_id}  ${milestone}
    ${yesterday} =    get yesterday
    execute sql string  UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${competition_id}' and type IN ('${milestone}');
    reload page

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

the assessor accepts an invite to an application
    logging in and error checking         ${webTestAssessorEmailAddress}   ${short_password}
    the user clicks the button/link       link = ${horizonCompetitionName}
    the user selects the radio button     acceptInvitation  true
    the user clicks the button/link       jQuery = button:contains("Confirm")

Custom Suite Teardown
    the user closes the browser
    Disconnect from database

the user completes hecp project finances
    [Arguments]  ${Application}   ${Project_growth_table}
    The user is able to complete Horizon project costs
    the user enters the project location
    Run Keyword if  '${Project_growth_table}' == 'no'    the user fills in the organisation information  ${Application}  ${SMALL_ORGANISATION_SIZE}
    Run Keyword if  '${Project_growth_table}' == 'yes'  the user fills the organisation details with Project growth table  ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user completes your funding section        ${Application}
    the user should see all finance subsections complete
    the user clicks the button/link  link = Back to application overview
    the user should see the element  jQuery = li:contains("Your project finances") > .task-status-complete
    the user clicks the button/link  link = Finances overview
    the user should see the element  jQuery = th:contains("Personnel costs (£)")
    the user should see the element  jQuery = th:contains("Subcontracting (£)")
    the user should see the element  jQuery = th:contains("Travel and subsistence (£)")
    the user should see the element  jQuery = th:contains("Equipment (£)")
    the user should see the element  jQuery = th:contains("Other goods, works and services (£)")
    the user should see the element  jQuery = th:contains("Other costs (£)")
    the user should see the element  jQuery = th:contains("Indirect costs (£)")
    the user clicks the button/link  link = Application overview

The user is able to complete Horizon project costs
    the user clicks the button/link           link = Your project costs
    the user should see the element           jQuery = h1:contains("Your project costs")
    the user should see hecp project cost categories
    the user enters text to a text field      id = personnel  50000
    the user enters text to a text field      id = subcontracting  50000
    the user enters text to a text field      id = travel  10000
    the user enters text to a text field      id = equipment  30000
    the user enters text to a text field      id = otherGoods  20000
    the user enters text to a text field      id = other  40000
    the user enters text to a text field      id = hecpIndirectCosts  0
    the user clicks the button/link           jQuery = button:contains("Mark")
    the user should see the element           jQuery = li:contains("Your project costs") > .task-status-complete

The user is able to complete Horizon public description section
    the user clicks the button/link           jQuery = a:contains("Public description")
    the user should see the element           jQuery = h1:contains("Public description")
    the user enters text to a text field      css=.textarea-wrapped .editor    This is some random text
    the user clicks the button/link           id = application-question-complete
    the user clicks the button/link           jQuery = a:contains("Return to application overview")
    the user should see the element           jQuery = li:contains("Public description") > .task-status-complete

the user fills in the CS Application section Horizon question
    [Arguments]  ${question_link}
    the user clicks the button/link         jQuery = h4 a:contains("${question_link}")
    the user enters text to a text field    id = question.guidanceTitle  Innovation is crucial to the continuing success of any organization.
    the user enters text to a text field    css = [aria-labelledby="question.guidance-label"]  Please use Microsoft Word where possible. If you complete your application using Google Docs or any other open source software, this can be incompatible with the application form.
    the user clicks the button/link         jQuery = button:contains("Done")

the lead applicant marks the application question as complete
    [Arguments]  ${questionName}   ${questionAnswer}
    the user clicks the button/link     link = ${questionName}
    the user clicks the button twice    jQuery = label:contains("${questionAnswer}")
    the user clicks the button/link     id = application-question-complete
    the user clicks the button/link     link = Back to application overview

ifs admin approves the spend profiles for horizonApplication
    [Arguments]  ${project}
    log in as a different user       &{ifs_admin_user_credentials}
    the user navigates to the page   ${server}/project-setup-management/project/${project}/spend-profile/approval
    the user clicks the button/link  id = radio-spendprofile-approve
    the user clicks the button/link  id = submit-button

Internal user notifies the applicant on status of application
    Requesting IDs of this Asos Organisation
    the internal team mark the application as successful / unsuccessful         ${horizonApplicationID}  FUNDED
    the user clicks the button/link                                             link = Competition
    the internal team notifies all applicants                                   ${horizonApplicationID}
    the user refreshes until element appears on page                            jQuery = td:contains("${horizonApplicationID}") ~ td:contains("Sent")

the applicant navigates to project set up
    log in as a different user                                                  ${leadApplicantEmail}    ${short_password}
    the user clicks the button/link                                             link = ${horizonApplicationName}

the user completes all project setup sections except spend profile
    the user is able to complete project details section
    the user completes the project team details
    the user is able to complete the Documents section
    the user fills in bank details
    log in as a different user                                                  &{internal_finance_credentials}
    Requesting Project ID of this Project
    internal user assigns MO to application                                     ${horizonApplicationID}    ${horizonApplicationName}    Orvill  Orville Gibbs
    project finance approves eligibility and generates the Spend Profile        ${asosId}  ${horizonProjectID}
    Internal user reviews and approves documents
    Internal user approves bank details

the user completes your funding section
    [Arguments]  ${Application}
    the user clicks the button/link             link = Your funding
    the user fills in the funding information   ${Application}   no

the user complete the work programme
    the user clicks the button/link                jQuery = a:contains("Work programme")
    the user should see read only view of work program part
    the user clicks the button/link                jQuery = button:contains("Save and continue")
    the user should see a field and summary error  You must select an option.
    the user clicks the button twice               jQuery = label:contains("Culture, Creativity and Inclusive Society (CL2)")
    the user clicks the button/link                jQuery = button:contains("Save and continue")
    the user should see read only view of call ID
    the user clicks the button/link                jQuery = button:contains("Save and continue")
    the user should see a field and summary error  You must select an option.
    the user clicks the button twice               jQuery = label:contains("HORIZON-CL2-2021-DEMOCRACY-01")
    the user clicks the button/link                jQuery = button:contains("Save and continue")
    the user can mark the question as complete for work programme
    the user should see the element                jQuery = li:contains("Work programme") > .task-status-complete

the user complete the work programmes
    the user clicks the button/link                jQuery = a:contains("Work programme")
    the user clicks the button twice               jQuery = label:contains("Culture, Creativity and Inclusive Society (CL2)")
    the user clicks the button/link                jQuery = button:contains("Save and continue")
    the user clicks the button twice               jQuery = label:contains("HORIZON-CL2-2021-DEMOCRACY-01")
    the user clicks the button/link                jQuery = button:contains("Save and continue")
    the user can mark the question as complete for work programme
    the user should see the element                jQuery = li:contains("Work programme") > .task-status-complete

the user can mark the question as complete for work programme
    the user clicks the button/link     id = application-question-complete
    the user should see the element     jQuery = p:contains("This question is marked as complete.")
    the user clicks the button/link     link = Back to application overview

the user see the print view of the application
    Requesting IDs of this Horizon application
    the user navigates to the page without the usual headers      ${SERVER}/application/${horizonApplicationID}/print?noprint
    the user should see the element                               xpath = //*[contains(text(),'Personnel costs (£)')]
    the user should see the element                               xpath = //*[contains(text(),'Subcontracting (£)')]
    the user should see the element                               xpath = //*[contains(text(),'Travel and subsistence (£)')]
    the user should see the element                               xpath = //*[contains(text(),'Equipment (£)')]
    the user should see the element                               xpath = //*[contains(text(),'Other goods, works and services (£)')]
    the user should see the element                               xpath = //*[contains(text(),'Other costs (£)')]
    the user should see the element                               xpath = //*[contains(text(),'Indirect costs (£)')]
    the user should see the element                               xpath = //*[contains(text(),'Select the Horizon Europe Work programme Part you applied to, e.g. CL2.')]
    the user should see the element                               xpath = //*[contains(text(),'Culture, Creativity and Inclusive Society (CL2)')]
    the user should see the element                               xpath = //*[contains(text(),'Select the call you applied to.')]
    the user should see the element                               xpath = //*[contains(text(),'HORIZON-CL2-2021-DEMOCRACY-01')]
    the user navigates to the page                                ${SERVER}/application/${horizonApplicationID}

the user should see read only view of work program part
    the user should see the element    jQuery = h1:contains("Select the Horizon Europe Work programme Part")
    the user should see the element    jQuery = label:contains("Culture, Creativity and Inclusive Society (CL2)")
    the user should see the element    jQuery = label:contains("Civil Security for Society (CL3)")
    the user should see the element    jQuery = label:contains("Digital, Industry and Space (CL4 & EUSPA)")
    the user should see the element    jQuery = label:contains("Climate, Energy and Mobility (CL5)")
    the user should see the element    jQuery = label:contains("Food, Bioeconomy, Natural Resources, Agriculture and Environment (CL6)")
    the user should see the element    jQuery = label:contains("EIC (EIC)")
    the user should see the element    jQuery = label:contains("European Innovation Ecosystems (EIE)")
    the user should see the element    jQuery = label:contains("Health (HLTH)")
    the user should see the element    jQuery = label:contains("Research Infrastructures (INFRA)")
    the user should see the element    jQuery = label:contains("Missions (MISS)")
    the user should see the element    jQuery = label:contains("Widening Participation and Strengthening the European Research Area (WIDERA)")

the user should see read only view of call ID
    the user should see the element    jQuery = label:contains("HORIZON-CL2-2021-DEMOCRACY-01")
    the user should see the element    jQuery = label:contains("HORIZON-CL2-2021-HERITAGE-01")
    the user should see the element    jQuery = label:contains("HORIZON-CL2-2021-HERITAGE-02")
    the user should see the element    jQuery = label:contains("HORIZON-CL2-2021-TRANSFORMATIONS-01")

the user should see hecp project cost categories
    the user should see the element     jQuery = span:contains("Personnel costs")
    the user should see the element     jQuery = span:contains("Subcontracting")
    the user should see the element     jQuery = span:contains("Travel and subsistence")
    the user should see the element     jQuery = span:contains("Equipment")
    the user should see the element     jQuery = span:contains("Other goods, works and services")
    the user should see the element     jQuery = span:contains("Other costs")
    the user should see the element     jQuery = span:contains("Indirect costs")

the user should see hecp project cost categories in summary box
    the user should see the element     jQuery = li:contains("Equipment")
    the user should see the element     jQuery = li:contains("Indirect costs")
    the user should see the element     jQuery = li:contains("Other costs")
    the user should see the element     jQuery = li:contains("Other goods, works and services")
    the user should see the element     jQuery = li:contains("Personnel costs")
    the user should see the element     jQuery = li:contains("Subcontracting")
    the user should see the element     jQuery = li:contains("Travel and subsistence")

the user should see readonly detailed hecp finances
    the user should see the element    jQuery = label:contains("Personnel costs") ~ span:contains("50,000")
    the user should see the element    jQuery = label:contains("Subcontracting") ~ span:contains("50,000")
    the user should see the element    jQuery = label:contains("Travel and subsistence") ~ span:contains("10,000")
    the user should see the element    jQuery = label:contains("Equipment") ~ span:contains("30,000")
    the user should see the element    jQuery = label:contains("Other goods, works and services") ~ span:contains("20,000")
    the user should see the element    jQuery = label:contains("Other costs") ~ span:contains("40,000")
    the user should see the element    jQuery = label:contains("Indirect costs") ~ span:contains("0")
    the user should see the element    css = [id="total-cost"][value="£200,000"]

the user edit the spend profile mothly values
    [Arguments]  ${personnelCosts}  ${subcontractingCosts}  ${travelAndSubsistenceCosts}  ${equipmentCosts}  ${otherGoodsWorksAndServicesCosts}  ${otherCosts}  ${indirectCosts}
    the user enters text to a text field     jQuery = tr:nth-of-type(1) td:nth-of-type(1) input[id^="table.monthlyCostsPerCategoryMap"]    ${personnelCosts}
    the user enters text to a text field     jQuery = tr:nth-of-type(2) td:nth-of-type(1) input[id^="table.monthlyCostsPerCategoryMap"]    ${subcontractingCosts}
    the user enters text to a text field     jQuery = tr:nth-of-type(3) td:nth-of-type(1) input[id^="table.monthlyCostsPerCategoryMap"]    ${travelAndSubsistenceCosts}
    the user enters text to a text field     jQuery = tr:nth-of-type(4) td:nth-of-type(1) input[id^="table.monthlyCostsPerCategoryMap"]    ${equipmentCosts}
    the user enters text to a text field     jQuery = tr:nth-of-type(5) td:nth-of-type(1) input[id^="table.monthlyCostsPerCategoryMap"]    ${otherGoodsWorksAndServicesCosts}
    the user enters text to a text field     jQuery = tr:nth-of-type(6) td:nth-of-type(1) input[id^="table.monthlyCostsPerCategoryMap"]    ${otherCosts}
    the user enters text to a text field     jQuery = tr:nth-of-type(7) td:nth-of-type(1) input[id^="table.monthlyCostsPerCategoryMap"]    ${indirectCosts}

the user reverted the edited values in spend profile
    the user clicks the button/link                 link = Edit spend profile
    the user edit the spend profile monthly values   615  615  246  369  246  492  123

the user should see Participating Organisation project region
    the user clicks the button/link  jQuery = a:contains("Participating Organisation project region")
    the user should see the element  jQuery = div:contains("Please type the region your project is being carried out in.")
    the user clicks the button/link  jQuery = a:contains("Back to application overview")

The user completes hecp documents section
    [Documentation]  IFS-5700
    the user clicks the button/link         link = Documents
    the user clicks the button/link         link = Test document type
    the user uploads the file               css = .inputfile  ${valid_pdf}
    the user clicks the button/link         id = submit-document-button
    the user clicks the button/link         id = submitDocumentButtonConfirm
    the user clicks the button/link         link = Return to documents
    the user clicks the button/link         link = Set up your project
    the user should see the element         jQuery = .progress-list li:nth-child(3):contains("Awaiting review")