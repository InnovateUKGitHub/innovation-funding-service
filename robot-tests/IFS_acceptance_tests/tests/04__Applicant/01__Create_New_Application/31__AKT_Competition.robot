*** Settings ***
Documentation     IFS-12745 Adding new template to support KTP AKT
...
...               IFS-12746 KTA is optional for KTP AKT funding type
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${AKT2ICompName}                            Access Knowledge Transfer to Innovate Competition
${aktLeadEmail}                             akt.ktp@gmail.com
&{aktLeadCredentials}                       email=${aktLeadEmail}  password=${short_password}
${AKT2IAssessmentCompetitionID}             ${competition_i
${ktpAssessmentApplicationID}               ${application_ids['${ktpAssessmentApplicationName}']}
${ktpDetailsFinanceCompetitionName}         KTP assessment Detailed Finances
${ktpDetailsFinanceCompetitionID}           ${competition_ids['${ktpDetailsFinanceCompetitionName}']}
${ktpDetailsFinanceApplicationName}    	    KTP assessment detailed finances application
${ktpDetailsFinanceApplicationID}           ${application_ids['${ktpDetailsFinanceApplicationName}']}
${ktpOverviewFinanceCompetitionName}        KTP assessment Overview Finances
${ktpOverviewFinanceCompetitionID}          ${competition_ids['${ktpOverviewFinanceCompetitionName}']}
${ktpOverviewFinanceApplicationName}        KTP assessment overview finances application
${ktpOverviewFinanceApplicationID}          ${application_ids['${ktpOverviewFinanceApplicationName}']}
${nonKTPOverviewFinanceCompetitionName}     Non KTP competition all finance overview
${nonKTPOverviewFinanceCompetitionID}       ${competition_ids['${nonKTPOverviewFinanceCompetitionName}']}
${nonKTPOverviewFinanceApplicationName}     Non KTP Application
${nonKTPOverviewFinanceApplicationID}       ${application_ids['${nonKTPOverviewFinanceApplicationName}']}
${ktaEmail}                                 hermen.mermen@ktn-uk.test
${existingKTAEmail}                         john.fenton@ktn-uk.test
${monitoringOfficerEmail}                   hermen.mermen@ktn-uk.test
${KTPapplication}  	                        KTP in panel application
${ktpProjectID}                             ${project_ids["${KTPapplication}"]}
${KTPapplicationId}                         ${application_ids["${KTPapplication}"]}
${KTPcompetiton}                            KTP in panel
${ktpLead}                                  bob@knowledge.base
${ktp}                                      jessica.doe@ludlow.co.uk
${ktpApplicationTitle}                      KTP New Application


*** Test Cases ***
Comp admin can select AKT2I Competition funding type
    [Documentation]  IFS-12745
    Given the user navigates to the page            ${CA_UpcomingComp}
    When the user clicks the button/link            jQuery = .govuk-button:contains("Create competition")
    And the user fills in initial details           ${AKT2ICompName}  ${month}  ${nextyear}  ${compType_Programme}  STATE_AID  KTP_AKT
    Then the user should see the element            jQuery = dt:contains("Funding type")+dd:contains("Access Knowledge Transfer to Innovate (AKT2I)")
    And the user should see the element             jQuery = button:contains("Edit")
    [Teardown]  the user clicks the button/link     link = Back to competition details

Comp admin can not view ktp related assessment sections on selecting AKT2I funding type
    [Documentation]  IFS-12745
    When the user clicks the button/link          link = Application
    Then the user should not see the element      link = Impact
    And the user should not see the element       link = Innovation
    And the user should not see the element       link = Challenge
    And the user should not see the element       link = Cohesiveness

Comp admin create an AKT2I competition and opens to external users
    [Documentation]  IFS-12745
    Given the user clicks the button/link                            link = Back to competition details
    Then the competition admin creates AKT2I competition             ${KTP_TYPE_ID}  ${AKT2ICompName}  Access Knowledge Transfer to Innovate  ${compType_Programme}  STATE_AID  KTP_AKT  PROJECT_SETUP  no  50  true  single-or-collaborative  No
    [Teardown]  Get competition id and set open date to yesterday    ${AKT2ICompName}

Lead applicant can complete application team section without KTA
    [Documentation]  IFS-12746
    [Setup]  logout as user
    Given the user select the competition and starts application    ${AKT2ICompName}
    And The user clicks the button/link                             link = Continue and create an account
    And the user apply with knowledge base organisation             The University of Liverpool   The University of Liverpool
    And the user creates an account and verifies email              KTP  AKT  ${aktLeadEmail}  ${short_password}
    When Logging in and Error Checking                              &{aktLeadCredentials}
    And the user clicks the button/link                             jQuery = a:contains("${UNTITLED_APPLICATION_DASHBOARD_LINK}")
    And applicant completes edi profile                             COMPLETE  ${aktLeadEmail}
    And the user clicks the button/link                             link = Application team
    And the user should see the element                             jQuery = h2:contains("Knowledge transfer adviser (optional)")
    And the user clicks the button/link                             id = application-question-complete
    Then the user should see the element                            jQuery = p:contains("Application team is marked as complete")

Lead applicant can not view KTA details in application summary
    [Documentation]  IFS-12476
    Given the user clicks the button/link       link = Application overview
    When the user clicks the button/link        link = Review and submit
    And the user clicks the button/link         id = accordion-questions-heading-1-1
    Then the user should not see the element    jQuery = h2:contains("Knowledge transfer adviser")

Lead applicant completes the KTP application
    Given the user clicks the button/link                                                        link = Application overview
    the user completes the KTP application except application team and your project finances
    the user selects research category                                                           Feasibility studies
    the lead applicant marks the KTP project location as complete

Lead applicant is shown a validation error when marking a non-selected option as complete for the organisation's fEC model type
     [Documentation]  IFS-9239
     Given the user clicks the button/link                     link = Application overview
     When the user clicks the button/link                      link = Your project finances
     When the user clicks the button/link                      link = Your fEC model
     And the user selects the radio button                     fecModelEnabled  fecModelEnabled-yes
     And The user clicks the button/link                       jQuery = button:contains("Next")
     Then the user sees fEC model validation error message

Lead applicant makes a 'Yes' selection for the organisation's fEC model without uploading a document
     [Documentation]  IFS-9240
     Then the user should see a field and summary error     You must upload a file.

Lead applicant uploads a document for the organisation's fEC model and save the selection
     [Documentation]  IFS-9240  IFS-11143
     When the user uploads the file                      css = .inputfile   testing_5MB.pdf
     And the user enters empty data into date fields     01  12  2500
     Then the user clicks the button/link                jQuery = button:contains("Mark as complete")
     And The user should see the element                 jQuery = li:contains("Your fEC model") span:contains("Complete")

Lead applicant can declare any other government funding received
    [Documentation]  IFS-7956  IFS-7958
    Given the user fills in the funding information                         ${Application}   no

Lead applicant completes the project costs
    [Documentation]  IFS-7146  IFS-7147  IFS-7148  IFS-7812  IFS-7814  IFS-8154
    When the user fills in ktp project costs
    Then the user should see the element         jQuery = li:contains("Your project costs") span:contains("Complete")

Lead applicant submits the KTP application
    Given the user clicks the button/link       link = Application overview
    When the user clicks the button/link        link = Review and submit
    And the user clicks the button/link          id = submit-application-button

Invite the KTA to assess the KTP competition
    [Documentation]   IFS-8260
    Given log in as a different user                    &{ifs_admin_user_credentials}
    When the user navigates to the page                 ${server}/management/competition/${competitionId}/assessors/find
    And the user clicks the button/link                 jQuery = tr:contains("Hermen Mermen") label
    And the user clicks the button/link                 id = add-selected-assessors-to-invite-list-button
    When the user clicks the button/link                id = review-and-send-assessor-invites-button
    And the user clicks the button/link                 jQuery = button:contains("Send invitation")
    Then the user should see the element                link = Hermen Mermen

Assessor accept the inviation to assess the KTP competition
    [Documentation]   IFS-8260
    Given KTA accepts the invitation to assess the application      ${AKT2ICompName}   ${ktaEmail}   ${short_password}
    When log in as a different user                                 &{ifs_admin_user_credentials}
    And the user navigates to the page                              ${server}/management/competition/${competitionId}/assessors/accepted
    Then the user should see the element                            link = Hermen Mermen

Allocated KTA to assess the KTP application
    [Documentation]   IFS-8260
    Given the user navigates to the page     ${server}/management/assessment/competition/${CompetitionID}/applications
    When the user clicks the button/link     link = View progress
    And the user selects the checkbox        assessor-row-1
    And the user clicks the button/link      jQuery = button:contains("Add to application")
    Then the user should see the element     jQuery = tr td:contains("Hermen Mermen")

Assessor accept the inviation to assess the KTP application
    [Documentation]   IFS-8260
    Given the user navigates to the page               ${server}/management/competition/${CompetitionID}
    And the user clicks the button/link                id = notify-assessors-changes-since-last-notify-button
    When KTA accepts to assess the KTP application     ${CompetitionID}   ${ktaEmail}  ${short_password}
    And the user clicks the button/link                link = Access Knowledge Transfer to Innovate Competition

Assessor can see lead organisation project finances when all option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link     link = Finances overview
    When the user clicks the button/link      jQuery = div:contains("A base of knowledge") ~ a:contains("View finances")
    Then the user should see the element      link = Your project costs
    And the user should see the element       link = Your project location
    And the user should see the element       link = Your funding

Assessor can see project cost summary in finance overview when all option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link     link = Back to finances overview
    Then the user should see the element      jQuery = h2:contains("Project cost summary")
    And the user should see the element       jQuery = td:contains("Other costs") + td:contains("1,100")

Assessor should get a validation message if the score is not selected
    [Documentation]   IFS-7915
    Given The user clicks the button/link                  link = Back to your assessment overview
    And the user clicks the button/link                    link = Impact
    When the user clicks the button/link                   jQuery = button:contains("Save and return to assessment overview")
    Then the user should see a field and summary error     The assessor score must be a number.

Assessor can score impact category in the KTP application
    [Documentation]   IFS-7915
    Given The user clicks the button/link             link = Back to your assessment overview
    And the user clicks the button/link               link = Impact
    When Assessor completes the KTP category          Testing feedback text
    Then Assessor should see the category details     Impact   10   25%

Assessor can score innovation category in the KTP application
    [Documentation]   IFS-7915
    Given the user clicks the button/link             link = Innovation
    When Assessor completes the KTP category          Testing feedback text
    Then Assessor should see the category details     Innovation   20   50%

Assessor can score challenge category in the KTP application
    [Documentation]   IFS-7915
    Given the user clicks the button/link             link = Challenge
    When Assessor completes the KTP category          Testing feedback text
    Then Assessor should see the category details     Innovation   30   75%

Assessor can score cohesiveness category in the KTP application
    [Documentation]   IFS-7915
    Given the user clicks the button/link             link = Cohesiveness
    When Assessor completes the KTP category          Testing feedback text
    Then Assessor should see the category details     Innovation   40   100%

Assessor can see the Print button and the score Total
    [Documentation]   IFS-8617
    When the user should see the element      jQuery = a:contains("Print or download the application")
    And the user should see the element       jQuery = p:contains("Total score:")

Assessor is presented with an error message when saving an assessment without guidance for funding sutability decision
    [Documentation]   IFS-8295
    Given the user clicks the button/link                  link = Review and complete your assessment
    When the user clicks the button/link                   jQuery = button:contains("Save assessment")
    Then the user should see a field and summary error     You must select an option.
    And the user should see the element                    jQuery = h1:contains("Assessment summary")
    And the user should see the element                    jQuery = h2:contains("Review assessment")

Assessor should see the scope section as incomplete if try to review the assessment without completing scope section
    [Documentation]   IFS-8295
    When the user clicks the button/link                                    id = accordion-questions-heading-1
    Then Assessor should review the incomplete scope category details       Incomplete    accordion-questions-content-1   ${EMPTY}

Assessor can review feedback they added to the impact assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                           id = accordion-questions-heading-2
    Then Assessor should review the assessment category details     Complete    10/10   accordion-questions-content-2   Testing feedback text

Assessor can review feedback they added to the innovation assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-3
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-3   Testing feedback text

Assessor can review feedback they added to the challenge assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-4
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-4   Testing feedback text

Assessor can review feedback they added to the cohesiveness assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-5
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-5   Testing feedback text

Assessor can amend the feedback they added to the scope assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the scope section
    When Assessor completes the scope section of an application
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the scope category details           Complete    Yes   accordion-questions-content-1   Testing feedback text

Assessor can amend the feedback they added to the impact assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the impact section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-2   NEW testing feedback text

Assessor can amend the feedback they added to the innovation assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the innovation section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-3   NEW testing feedback text

Assessor can amend the feedback they added to the challenge assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the challenge section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-4   NEW testing feedback text

Assessor can amend the feedback they added to the cohesiveness assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the cohesiveness section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-5   NEW testing feedback text

Assessor can save the KTP application assessment
    [Documentation]   IFS-8295
    Given the user should see the element           jQuery = .govuk-body:contains("You must explain your decision")
    And the user selects the radio button           fundingConfirmation   true
    And the user enters text to a text field        id = feedback    Testing feedback text
    And the user clicks the button/link             jQuery = button:contains("Save assessment")
    Then the user should see the element            jQuery = li:contains("KTP assessment application") .msg-progress:contains("Assessed")

Assessor can submit the KTP application assessment
    [Documentation]   IFS-8295
    Given the user selects the checkbox             assessmentIds1
    When the user clicks the button/link            id = submit-assessment-button
    And the user clicks the button/link             jQuery = button:contains("Yes I want to submit the assessments")
    Then the user should see the element            jQuery = li:contains("KTP assessment application") .msg-progress:contains("Recommended")

Deafult value of assessor view finance config set to all for ktp competitions
    [Documentation]   IFS-8594  IFS-8779
    Given Log in as a different user                 &{Comp_admin1_credentials}
    When the user navigates to the page              ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     competition config  ${month}  ${nextyear}  ${compType_Programme}  SUBSIDY_CONTROL  KTP
    And the user clicks the button/link              link = Assessors
    Then radio button should be set to               assessorFinanceView   ALL

Assessor can see lead organisation detailed finances when detailed option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given Invite KTA to assess the competition     ${ktpDetailsFinanceCompetitionID}   ${ktpDetailsFinanceApplicationName}   ${ktpDetailsFinanceCompetitionName}   ${ktaEmail}  ${short_password}
    And the user clicks the button/link            link = Finances overview
    When the user clicks the button/link           jQuery = div:contains("A base of knowledge") ~ a:contains("View finances")
    Then the user should not see the element       link = Your project costs
    And the user should not see the element        link = Your project location
    And the user should not see the element        link = Your funding
    And the user should see the element            jQuery = h2:contains("Detailed finances")

Assessor can see project cost summary in detailed finance overview when detailed option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link     link = Back to funding
    Then the user should see the element      jQuery = h2:contains("Project cost summary")
    And the user should see the element       jQuery = td:contains("Other costs") + td:contains("1,100")


Assessor can see lead organisation finances for non ktp compettition when all option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given Invite KTA to assess the competition     ${nonKTPOverviewFinanceCompetitionID}    ${nonKTPOverviewFinanceApplicationName}    ${nonKTPOverviewFinanceCompetitionName}     addison.shannon@gmail.com   ${short_password}
    And the user clicks the button/link            link = Finances overview
    When the user clicks the button/link           jQuery = div:contains("Mo Juggling Mo Problems Ltd") ~ a:contains("View finances")
    Then the user should see the element           link = Your project costs
    And the user should see the element            link = Your project location
    And the user should see the element            link = Your organisation
    And the user should see the element            link = Your funding

KTA can see application successfull banner and feedback information with date on making the application successful, before the feedback is released
    [Documentation]  IFS-8548
    Given IFS Admin makes the application decision           ${ktpAssessmentCompetitionName}  Successful
    And IFS Admin notifies all applicants
    When MO navigates to application overview page           ${ktpAssessmentApplicationName}  This application was successful.
    Then the user should see the element                     jQuery = h2:contains("This application was successful.")
    And the user should see the element                      jQuery = p:contains("All application feedback will be available here from ${ktpAssessmentCompetitionReleaseFeedbackDayMonthYear}.")

KTA can see application successful banner and feedback information after the feedback is released
    [Documentation]  IFS-8548
    Given IFS admin releases feedback to the applicant    ${ktpAssessmentCompetitionName}
    When MO navigates to application overview page        ${ktpAssessmentApplicationName}  This application was successful.
    Then the user should see the element                  jQuery = h2:contains("This application was successful.")
    And the user should see the element                   jQuery = p:contains("You can view all scores and application feedback in the relevant sections.")

KTA can see application unsuccessful banner and feedback information with date on making the application unsuccessful, before the feedback is released
    [Documentation]  IFS-8548
    Given IFS Admin makes the application decision        ${ktpDetailsFinanceCompetitionName}  Unsuccessful
    And IFS Admin notifies all applicants
    When MO navigates to application overview page        ${ktpDetailsFinanceApplicationName}  This application was unsuccessful.
    Then the user should see the element                  jQuery = h2:contains("This application was unsuccessful.")
    And the user should see the element                   jQuery = p:contains("All application feedback will be available here from ${ktpDetailsFinanceCompetitionReleaseFeedbackDayMonthYear}.")

KTA can see application unsuccessful banner and feedback information after the feedback is released
    [Documentation]  IFS-8548
    Given IFS admin releases feedback to the applicant    ${ktpDetailsFinanceCompetitionName}
    When MO navigates to application overview page        ${ktpDetailsFinanceApplicationName}  This application was unsuccessful.
    Then the user should see the element                  jQuery = h2:contains("This application was unsuccessful.")
    And the user should see the element                   jQuery = p:contains("You can view all scores and application feedback in the relevant sections.")

KTA receives a notification email that assessor and supporter feedback is available on release feedback
    [Documentation]  IFS-8550
    Given log in as a different user                                     &{ifs_admin_user_credentials}
    And the user clicks the button/link                                  link = ${KTPcompetiton}
    When IFS admin releases feedback on making application sucessful
    Then the user reads his email and clicks the link                    ${monitoringOfficerEmail}   ${KTPcompetiton}: Feedback for application ${KTPapplicationId} is now available.   You can now view the feedback for this application  1

KTA can view written feedback from assessors and supporters on release feedback
    [Documentation]  IFS-8550
    Given log in as a different user                        ${monitoringOfficerEmail}   ${short_password}
    When the user navigates to the page                     ${server}/project-setup/project/${ktpProjectID}
    And the user clicks the button/link                     link = view application overview
    #When the user navigates to application overview         ${KTPapplication}
    Then KTA should see assessors and supporters feedback

Project lead should not see assessor or supporter feedback
    [Documentation]  IFS-8550
    Given log in as a different user                                               ${ktpLead}  ${short_password}
    When the user navigates to the page                                            ${server}/project-setup/project/${ktpProjectID}
    And the user clicks the button/link                                            link = view application overview
    Then the project team member should not see assessor or supporter feedback


*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the user fills in initial details
    [Arguments]  ${compTitle}  ${month}  ${nextyear}  ${compType}  ${fundingRule}  ${fundingType}
    the user navigates to the page                          ${CA_UpcomingComp}
    the user clicks the button/link                         jQuery = .govuk-button:contains("Create competition")
    the user clicks the button/link                         jQuery = a:contains("Initial details")
    the user enters text to a text field                    css = #title  ${compTitle}
    the user selects the radio button                       fundingType  ${fundingType}
    the user selects the option from the drop-down menu     ${compType}  id = competitionTypeId
    the user selects the radio button                       fundingRule  ${fundingRule}
    the user selects the option from the drop-down menu     Emerging and enabling  id = innovationSectorCategoryId
    the user selects the option from the drop-down menu     Robotics and autonomous systems  css = select[id^=innovationAreaCategory]
    the user enters text to a text field                    css = #openingDateDay  1
    the user enters text to a text field                    css = #openingDateMonth  ${month}
    the user enters text to a text field                    css = #openingDateYear  ${nextyear}
    the user selects option from type ahead                 innovationLeadUserId  Ian Cooper  Ian Cooper
    the user selects option from type ahead                 executiveUserId  Robert Johnson  Robert Johnson
    the user clicks the button/link                         jQuery = button:contains("Done")

the competition admin creates AKT2I competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}  ${isOpenComp}
    the user selects the Terms and Conditions                   ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility                ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user fills in the CS funding eligibility                true   ${compType}  ${fundingRule}
    the user selects the organisational eligibility to no       false
    the user fills in the CS Milestones                         ${completionStage}   ${month}   ${nextyear}  ${isOpenComp}
    the user marks the KTP_AKT Assessed questions as complete   ${compType}
    the user fills in the CS Documents in other projects
    the user clicks the button/link                             link = Public content
    the user fills in the Public content and publishes          ${extraKeyword}
    the user clicks the button/link                             link = Return to setup overview
    the user clicks the button/link                             jQuery = a:contains("Complete")
    the user clicks the button/link                             jQuery = button:contains('Done')
    the user navigates to the page                              ${CA_UpcomingComp}
    the user should see the element                             jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user marks the KTP_AKT Assessed questions as complete
    [Arguments]  ${comp_type}
    the user clicks the button/link                                                         link = Application
    the user marks the Application details section as complete                              ${comp_type}
    the assessment questions are marked complete for other programme type competitions
    the user fills in the Finances questions without growth table                           false  true
    the user clicks the button/link                                                         jQuery = button:contains("Done")
    the user clicks the button/link                                                         link = Back to competition details
    the user should see the element                                                         jQuery = div:contains("Application") ~ .task-status-complete

the user filters the KTA user
    the user navigates to the page           ${server}/management/competition/${ktpAssessmentCompetitionID}/assessors/find
    the user enters text to a text field     id = assessorNameFilter   Amy
    the user clicks the button/link          id = assessor-filter-button

KTA accepts the invitation to assess the application
    [Arguments]    ${compettitionName}                    ${ktaEmail}   ${short_password}
    log in as a different user                            ${ktaEmail}   ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = ${compettitionName}
    the user selects the radio button                     acceptInvitation   true
    the user clicks the button/link                       jQuery = button:contains("Confirm")

KTA accepts to assess the KTP application
    [Arguments]   ${compettitionName}                    ${ktaEmail}  ${short_password}
    log in as a different user                           ${ktaEmail}  ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = ${compettitionName}
    the user clicks the button/link                       link = Accept or reject
    the user selects the radio button                     assessmentAccept  true
    the user clicks the button/link                       jQuery = button:contains("Confirm")

Assessor completes the KTP category
    [Arguments]   ${feedbackText}
    The user selects the option from the drop-down menu     10    css = .assessor-question-score
    The user enters text to a text field                    css = .editor    ${feedbackText}
    Wait for autosave
    mouse out  css = .editor
    the user should see the element                                    jQuery = span:contains("Saved!")
    The user clicks the button/link                                    jQuery = button:contains("Save and return to assessment overview")
    ${error} =   Run Keyword and return status without screenshots     page should contain     An unexpected error occurred.
    Run Keyword If    '${error}' == 'True'                             the user clicks the button/link   jQuery = button:contains("Save and return to assessment overview")

Assessor should see the category details
    [Arguments]   ${category}   ${score}   ${percentage}
    the user should see the element     jQuery = li:contains("${category}") .task-status-complete:contains("Complete")
    the user should see the element     jQuery = li:contains("${category}") .notification:contains("Score 10 / 10")
    the user should see the element     jQuery = p:contains("${score}")
    the user should see the element     jQuery = p:contains("${percentage}")

Assessor completes the scope section of an application
    the user selects the radio button                       govuk-radios__item     in-scope-true
    The user selects the option from the drop-down menu     Industrial research    css = .research-category
    The user enters text to a text field                    css = .editor    Testing feedback text
    Wait for autosave
    mouse out  css = .editor
    the user should see the element                                    jQuery = span:contains("Saved!")
    the user clicks the button/link                                    jQuery = button:contains("Save and return to assessment overview")
    ${error} =   Run Keyword and return status without screenshots     page should contain     An unexpected error occurred.
    Run Keyword If    '${error}' == 'True'                             the user clicks the button/link   jQuery = button:contains("Save and return to assessment overview")

Assessor should review the assessment category details
    [Arguments]   ${sectionStatus}   ${score}   ${idSelector}   ${feedbackText}
    the user should see the element     jQuery = h2:contains("${sectionStatus}")
    the user should see the element     jQuery = .section-score:contains("Score")
    the user should see the element     jQuery = .section-score:contains("${score}")
    the user should see the element     jQuery = \#${idSelector}:contains("${feedbackText}")
    the user should see the element     jQuery = .govuk-body:contains(40/40)
    the user should see the element     jQuery = .govuk-body:contains(100%)

Assessor should review the scope category details
    [Arguments]   ${sectionStatus}   ${scopeAnswer}   ${idSelector}   ${feedbackText}
    the user should see the element     jQuery = h2:contains("${sectionStatus}")
    the user should see the element     jQuery = .score:contains("In scope: ${scopeAnswer}")
    the user should see the element     jQuery = \#${idSelector}:contains("${feedbackText}")

Assessor should review the incomplete scope category details
    [Arguments]   ${sectionStatus}   ${idSelector}   ${feedbackText}
    the user should see the element         jQuery = h2:contains("${sectionStatus}")
    the user should not see the element     jQuery = .score:contains("In scope:")
    the user should see the element         jQuery = \#${idSelector}:contains("${feedbackText}")

Invite KTA to assess the competition
    [Arguments]   ${competitionID}   ${applicationTitle}   ${competitionName}   ${email}  ${short_password}
    Log in as a different user                               &{ifs_admin_user_credentials}
    the user navigates to the page                           ${server}/management/competition/${competitionID}/assessors/find
    ${status}   ${value} =  Run Keyword And Ignore Error Without Screenshots    the user should see the element    jQuery = span:contains("Non KTP competition all finance overview")
    Run Keyword If   '${status}' == 'PASS'    the user selects the checkbox     jQuery = tr:contains("Addison Shannon") :checkbox
    ...                              ELSE     the user selects the checkbox     jQuery = tr:contains("Amy Colin") :checkbox
    the user clicks the button/link                          id = add-selected-assessors-to-invite-list-button
    the user clicks the button/link                          id = review-and-send-assessor-invites-button
    the user clicks the button/link                          jQuery = button:contains("Send invitation")
    KTA accepts the invitation to assess the application     ${competitionName}  ${email}   ${short_password}
    Log in as a different user                               &{ifs_admin_user_credentials}
    the user navigates to the page                           ${server}/management/assessment/competition/${competitionID}/applications
    the user clicks the button/link                          link = View progress
    the user selects the checkbox                            assessor-row-1
    the user clicks the button/link                          jQuery = button:contains("Add to application")
    the user navigates to the page                           ${server}/management/competition/${competitionID}
    the user clicks the button/link                          id = notify-assessors-changes-since-last-notify-button
    KTA accepts to assess the KTP application                ${competitionName}    ${email}  ${short_password}
    the user clicks the button/link                          link = ${applicationTitle}

IFS Admin makes the application decision
    [Arguments]   ${competitionName}  ${decision}
    log in as a different user                            &{ifs_admin_user_credentials}
    the user clicks the button/link                       link = ${competitionName}
    the user clicks the button/link                       id = close-assessment-button
    IFS admin inputs the funding decision                 ${decision}

IFS admin inputs the funding decision
    [Arguments]   ${decision}
    the user clicks the button/link     link = Input and review funding decision
    the user clicks the button/link     id = select-all-1
    the user clicks the button/link     jQuery = button:contains("${decision}")
    the user clicks the button/link     link = Competition

IFS Admin notifies all applicants
    the user clicks the button/link                      link = Manage funding notifications
    the user clicks the button/link                      id = select-all-1
    the user clicks the button/link                      id = write-and-send-email
    the user clicks the button/link                      id = send-email-to-all-applicants
    the user clicks the button/link                      id = send-email-to-all-applicants-button
    the user refreshes until element appears on page     jQuery = td:contains("Sent")

IFS admin releases feedback to the applicant
    [Arguments]  ${competitionName}
    log in as a different user          &{ifs_admin_user_credentials}
    the user clicks the button/link     link = ${competitionName}
    the user clicks the button/link     id = release-feedback-button

MO navigates to application overview page
    [Arguments]  ${applicationName}  ${message}
    log in as a different user                            ${monitoringOfficerEmail}  ${short_password}
    the user navigates to application overview            ${applicationName}
    the user refreshes until element appears on page      jQuery = h2:contains("${message}")

the user navigates to application overview
    [Arguments]  ${applicationName}
    the user navigates to the page                       ${server}/project-setup/monitoring-officer/dashboard
    the user selects the checkbox                        previousProject
    the user clicks the button/link                      id = update-documents-results-button
    the user refreshes until element appears on page     link = ${applicationName}
    the user clicks the button/link                      link = ${applicationName}
    the user clicks the button/link                      link = view application overview

KTA should see assessors and supporters feedback
    the user clicks the button/link     jQuery = .govuk-heading-m:contains("Score assessment") + div button:contains("Open all")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Impact") + div h3:contains("Assessor 1") + p:contains("This is the impact feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Impact") + div h3:contains("Assessor 2") + p:contains("This is the impact feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Impact") span:contains("Average score 7.0 / 10")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Innovation") + div h3:contains("Assessor 1") + p:contains("This is the innovation feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Innovation") + div h3:contains("Assessor 2") + p:contains("This is the innovation feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Innovation") span:contains("Average score 7.0 / 10")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Challenge") + div h3:contains("Assessor 1") + p:contains("This is the challenge feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Challenge") + div h3:contains("Assessor 2") + p:contains("This is the challenge feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Challenge") span:contains("Average score 7.0 / 10")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Cohesiveness") + div h3:contains("Assessor 1") + p:contains("This is the cohesiveness feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Cohesiveness") + div h3:contains("Assessor 2") + p:contains("This is the cohesiveness feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Cohesiveness") span:contains("Average score 7.0 / 10")
    the user should see the element     jQuery = h3:contains("Application score: 70.0%")
    the user clicks the button/link     jQuery = .govuk-heading-m:contains("Application feedback") + div button:contains("Open all")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Assessor feedback") + div ul li:contains("Assessor 1") p:contains("Perfect application")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Assessor feedback") + div ul li:contains("Assessor 2") p:contains("Perfect application")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Anarchy inc.") p:contains("This application is extraordinary I'd love to fund it")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Money inc.") p:contains("This application is extraordinary I'd love to fund it")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Evil inc.") p:contains("This application is extraordinary I'd hate to fund it")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Guest inc.") p:contains("This application is extraordinary I'd hate to fund it")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Connolly inc.")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Alston inc.")

the project team member should not see assessor or supporter feedback
    the user should not see the element    jQuery = .govuk-accordion__section-header:contains("Assessor feedback")
    the user should not see the element    jQuery = .govuk-accordion__section-header:contains("Supporter feedback")
    the user should not see the element    jQuery = h2:contains("Score assessment")

IFS admin releases feedback on making application sucessful
    IFS admin inputs the funding decision                Successful
    IFS Admin notifies all applicants
    the user clicks the button/link                      link = Competition
    the user refreshes until element appears on page     id = release-feedback-button
    the user clicks the button/link                      id = release-feedback-button

the user should see read only view for FEC declaration
    the user should not see the element                     jQuery = button:contains("Edit your fEC Model")
    the user checks the read-only page


the user checks the read-only page
    # Due to us testing webtest data here, the file does not exist so we check for only no internal server errors. Page not found is OK in this case.
    the user should see the element     jQuery = h3:contains("Will you be using the full economic costing (fEC) funding model?") ~ div p:contains("Yes")
    the user clicks the button/link     jQuery = h3:contains("Your fEC certificate") ~ div a:contains("fec-file")
    Select Window                       NEW
    the user should not see internal server and forbidden errors
    the user closes the last opened tab

the user completes the KTP application except application team and your project finances
    the user clicks the button/link                                                 link = Application details
    the user fills in the KTP Application details                                   ${KTPapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                                        ${ktpApplicationTitle}
    the lead applicant marks the KTP project location as complete
    the user accept the competition terms and conditions                            Return to application overview

the user fills in the KTP Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user should see the element                jQuery = h1:contains("Application details")
    the user should not see the element            id = startDate
    the user enters text to a text field           id = name  ${appTitle}
    the user enters text to a text field           id = durationInMonths  24
    the user clicks the button twice               css = label[for="resubmission-no"]
    the user can mark the question as complete
    the user should see the element                jQuery = li:contains("Application details") > .task-status-complete

the lead applicant marks the KTP project location as complete
    the user enters the project location
    the user should see the element          jQuery = li:contains("Your project location") span:contains("Complete")
    the user clicks the button/link          link = Back to application overview


the applicant submits the application
    the user clicks the button/link                    link = Review and submit
    the user should not see the element                jQuery = .task-status-incomplete
    the user clicks the button/link                    jQuery = .govuk-button:contains("Submit application")
    the user should be redirected to the correct page  track

the applicant goes to the project summary, and performs actions
    click link    Project summary
    time until page contains    Please provide a short summary of your project    Loading the project summary section
    Input Text    css = #form-input-1039 .editor    This is some random text
    mark section as complete    Marking summary section as complete
    the applicant saves and returns to the overview    Saving the project summary section

the applicant goes to the public description, and performs actions
    click link    Public description
    time until page contains    Please provide a brief description of your project    Loading the public description section
    Input Text    css = #form-input-1040 .editor    This is some random text
    mark section as complete    Marking public description section as complete
    the applicant saves and returns to the overview    Saving the public description section

the applicant goes to the scope section, and performs actions
    click link    Scope
    time until page contains    If your application doesn't align with the scope    Loading the scope section
    Input Text    css = #form-input-1041 .editor    This is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random text
    mark section as complete    Marking scope section as complete
    the applicant saves and returns to the overview    Saving the scope section

the user selects research category
    [Arguments]  ${res_category}
    the user clicks the button/link   link = Research category
    the user clicks the button twice  jQuery = label:contains("${res_category}")
    the user can mark the question as complete
    the user should see the element   jQuery = li:contains("Research category") > .task-status-complete

the user sees fEC model validation error message
     the user clicks the button/link                   jQuery = button:contains("Mark as complete")
     the user should see the element                   jQuery = span:contains("You must upload a file.")
     the user should see the element                   jQuery = span:contains("You must enter an expiry date.")

the user fills in ktp project costs
    the user fills in Associate employment
    the user fills in Associate development
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   css = textarea[id$="associateSalary.description"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Additional company cost estimates")
    the user clicks the button/link             exceed-limit-yes
    And Input Text                              css = .textarea-wrapped .editor  This is some random text
    the user fills additional company costs     description  100
    the user clicks the button/link             css = label[for="stateAidAgreed"]
    the user clicks the button/link             jQuery = button:contains("Mark as complete")

the user enters empty data into date fields
    [Arguments]  ${date}  ${month}  ${year}
    the user enters text to a text field   id = fecCertExpiryDay  ${date}
    the user enters text to a text field   id = fecCertExpiryMonth   ${month}
    the user enters text to a text field   id = fecCertExpiryYear  ${year}
