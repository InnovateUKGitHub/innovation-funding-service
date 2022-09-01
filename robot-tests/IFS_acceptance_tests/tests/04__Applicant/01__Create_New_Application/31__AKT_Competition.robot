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


Lead applicant completes the KTP application
    Given the user clicks the button/link                                                        link = Application overview
    the user completes the KTP application except application team and your project finances
    the user selects research category from funding
    the applicant goes to the project summary, and performs actions
    the lead applicant marks the KTP project location as complete
    the applicant goes to the scope section, and performs actions
    the applicant goes to the public description, and performs actions
    the applicant submits the application

Lead applicant can not view KTA details in application summary
    [Documentation]  IFS-12476
    Given the user clicks the button/link       link = Application overview
    When the user clicks the button/link        link = Review and submit
    And the user clicks the button/link         id = accordion-questions-heading-1-1
    Then the user should not see the element    jQuery = h2:contains("Knowledge transfer adviser")



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

