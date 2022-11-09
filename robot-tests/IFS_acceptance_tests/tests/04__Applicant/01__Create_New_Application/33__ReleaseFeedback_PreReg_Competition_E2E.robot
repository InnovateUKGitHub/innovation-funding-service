*** Settings ***
Documentation     IFS-12958 2 stage competitions - Full application is not getting created on marking application as successful for a 'Release feedback' competition
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${hecpPreregReleaseFeedbackCompName}                    Hecp Pre Registration Competition with Release feedback
${hecpPreregReleaseFeedbackAppName}                     release feedback EOI Application
${unSuccessPreregReleaseFeedbackAppName}                unSuccessfulPreRegApplication with Release feedback
${unSubmittedPreregReleaseFeedbackAppName}              unSubmittedPreRegApplication with Release feedback
${preRegApplicationUnsuccessfulEmailReleaseFeedback}    You have been unsuccessful in the expression of interest stage for funding to Innovate UK's ${hecpPreregReleaseFeedbackCompName} competition.
${preRegApplicationSuccessfulEmail}                     We are pleased to inform you that your expression of interest application has been successful.
${preregApplicationSubmissionEmail}                     You have successfully submitted an application for funding to
${fullApplicationSuccessfulEmail}                       We are pleased to inform you that your application for the Horizon Europe collaborative competition has been successful and passed the technical assessment phase.
${evidenceSubmittedEmailSubject}                        Evidence file submitted
${evidenceSubmittedEmailDescriptionReleaseFeedback}     You have successfully submitted your evidence file to ${hecpPreregReleaseFeedbackCompName} competition.

*** Test Cases ***
Comp Admin creates a prereg competition
    [Documentation]  IFS-12958
    Given The user logs-in in new browser                    &{Comp_admin1_credentials}
    Then the competition admin creates prereg competition    ${BUSINESS_TYPE_ID}  ${hecpPreregReleaseFeedbackCompName}  Pre Registration  ${compType_HORIZON_EUROPE}  NOT_AID  HECP  Release_Feedback  no  50  false  single-or-collaborative

Comp admin can see readonly view of expression of intrest question selection
    [Documentation]  IFS-12958
    [Setup]  Get competitions id and set it as suite variable     ${hecpPreregReleaseFeedbackCompName}
    Given the user navigates to the page                          ${server}/management/competition/setup/${preregReleaseFeedbackCompetitionId}/section/application/landing-page
    When the user clicks the button/link                          link = 2. Participating Organisation project region
    Then the user should see the element                          jQuery = dt:contains("Is this also an expression of interest question?") + dd:contains("Yes")

Comp admin can edit the expression of interest question selection
    [Documentation]  IFS-12958
    Given the user clicks the button/link   jQuery = a:contains("Edit this question")
    When the user clicks the button twice   jQuery = label[for=expression-of-interest-no]
    And the user clicks the button/link     jQuery = button:contains("Done")
    And the user clicks the button/link     link = 2. Participating Organisation project region
    Then the user should see the element    jQuery = dt:contains("Is this also an expression of interest question?") + dd:contains("No")
    [Teardown]  the user marks the competition application section as complete

Applicants should view prereg related content when competition is opened
    [Documentation]  IFS-12958
    [Setup]  Comp admin set the competion as prereg comp and hide the question, section and subsection
    Given Update competition to have evidence required
    When the user navigates to the page                 ${frontDoor}
    And the user enters text to a text field            id = keywords   Pre Registration
    And the user clicks the button/link                 id = update-competition-results-button
    Then the user should see the element                jQuery = li:contains("${hecpPreregReleaseFeedbackCompName}") div:contains("Refer to competition date for competition submission deadlines.")

Internal users should see EOI specifc content on funding decision page
    [Documentation]    IFS-12958
    Given the user navigates to the page            ${server}/management/competition/${preregReleaseFeedbackCompetitionId}/applications
    When the user clicks the button/link            link = Expressions of interest
    Then User should see EOI Related content
    And the user should see the element             jQuery = td:contains("No applications found.")

Applicant can not view hidden question, section and subsection
    [Documentation]  IFS-12958
    Given log in as a different user                &{lead_applicant_credentials}
    When logged in user applies to competition      ${hecpPreregReleaseFeedbackCompName}  1
    Then the user should not see the element        link = Participating Organisation project region
    And the user should not see the element         link = Award terms and conditions
    And the user should not see the element         jQuery = h2:contains("Terms and conditions")
    And the user should not see subsection          Your project location

Applicants views expression of interest labels in application overview page for pre reg applications
    [Documentation]  IFS-12958
    Given the user clicks the button/link                         link = Back to expression of interest overview
    When the user completes the application details section       ${hecpPreregReleaseFeedbackAppName}  ${tomorrowday}  ${month}  ${nextyear}   23
    And Requesting application ID of prereg application           ${hecpPreregReleaseFeedbackAppName}
    Then the user should see EOI labels for prereg application
    And the user should see the element                           jQuery = dt:contains("Application number:")+dd:contains("${preregReleaseFeedbackApplicationID}")

Applicant should view EOI label on dashboard for expression of interest applications
    [Documentation]  IFS-12958
    When the user clicks the button/link    link = Back to applications
    Then the user should see the element    jQuery = li:contains("${hecpPreregReleaseFeedbackAppName}") .status-msg:contains("Expression of interest")

Lead applicant invites a team member to the application
    [Documentation]  IFS-12958
    Given the user clicks the button/link                                           link = ${hecpPreregReleaseFeedbackAppName}
    And the user clicks the button/link                                             link = Application team
    When the user clicks the button/link                                            jQuery = button:contains("Add person to Empire Ltd")
    And the user invites a person to the same organisation                          Troy Ward  troy.ward@gmail.com
    Then the user accepts invitation to join application under same organisation    troy.ward@gmail.com   ${short_password}   Invitation to contribute in Hecp Pre Registration Competition   You are invited by Steve Smith to participate in an application for funding through the Innovation Funding Service.

Lead applicant invites a partner organisation and completes project finances
    [Documentation]  IFS-12958
    Given log in as a different user                            &{lead_applicant_credentials}
    And the user clicks the application tile if displayed
    And the user clicks the button/link                         link = ${hecpPreregReleaseFeedbackAppName}
    When the lead invites already registered user               ${collaborator1_credentials["email"]}  ${hecpPreregReleaseFeedbackCompName}
    Then Partner applicant completes prereg project finances    ${hecpPreregReleaseFeedbackAppName}  ${collaborator1_credentials["email"]}  ${short_password}

Lead applicant completes the application sections
    [Arguments]  IFS-12958
    Given log in as a different user                                                &{lead_applicant_credentials}
    And the user clicks the application tile if displayed
    And the user clicks the button/link                                             link = ${hecpPreregReleaseFeedbackAppName}
    When the applicant completes Application Team                                   COMPLETE  steve.smith@empire.com
    And the user complete the work programme
    And the lead applicant fills all the questions and marks as complete(prereg)
    And the user completes prereg project finances                                  ${hecpPreregReleaseFeedbackAppName}   no
    Then the user should see the element                                            jQuery = .progress:contains("100%")
    And the user should see the element                                             link = Print your expression of interest

Applicant can not view hidden question, section and subsection in application summary
    [Documentation]  IFS-12958
    When the user clicks the button/link        id = application-overview-submit-cta
    Then the user should not see the element    jQuery = button:contains("Participating Organisation project region")
    And the user should not see the element     jQuery = h2:contains("Terms and conditions")
    And the user should not see the element     jQuery = button:contains("Award terms and conditions")
    And the user should see the element         jQuery = h1:contains("Expression of interest summary")
    And the user should see the element         jQuery = h2:contains("Expression of interest questions")
    And the user should see the element         link = Expression of interest overview

Applicant submits the expression of interest application
    [Documentation]  IFS-12958
    When the user clicks the button/link        id = submit-application-button
    Then the user should see the element        jQuery = h2:contains("Expression of interest submitted")
    And the user should see the element         jQuery = h1:contains("Expression of interest status")
    And the user should see the element         link = View expression of interest
    And the user should see the element         link = Print expression of interest
    And the user reads his email                steve.smith@empire.com  ${preregReleaseFeedbackApplicationID}: Successful submission of expression of interest   You have successfully submitted an expression of interest for funding to Innovate UK’s ${hecpPreregReleaseFeedbackCompName}.

Applicant can not view hidden question, section and subsection in print application
    [Documentation]  IFS-12958
    When the user navigates to the page without the usual headers      ${SERVER}/application/${preregReleaseFeedbackApplicationID}/print?noprint
    Then the user should see the element                               xpath = //*[contains(text(),'Expression of interest questions')]
    And the user should not see the element                            xpath = //h2[contains(text(),'Terms and conditions')]
    And the user should not see the element                            xpath = //span[contains(text(),'Award terms and conditions')]

Lead applicant views application status as evidence required on submitting an application
    [Documentation]  IFS-12958
    When the user navigates to the page    ${SERVER}/applicant/dashboard
    Then the user should see the element   jQuery = li:contains("${hecpPreregReleaseFeedbackAppName}") .status-msg:contains("Expression of interest") + .status-msg:contains("Evidence required")

Member of the same lead organisation views the application status as evidence required on submitting an application
    [Documentation]  IFS-12958
    Given log in as a different user       troy.ward@gmail.com   ${short_password}
    When the user navigates to the page    ${SERVER}/applicant/dashboard
    Then the user should see the element   jQuery = li:contains("${hecpPreregReleaseFeedbackAppName}") .status-msg:contains("Expression of interest") + .status-msg:contains("Evidence required")

Partner applicant should not see evidence required status instead should still view submited status
    [Documentation]  IFS-12958
    Given log in as a different user                           ${collaborator1_credentials["email"]}  ${short_password}
    When the user clicks the application tile if displayed
    Then the user should see the element                   jQuery = li:contains("${hecpPreregReleaseFeedbackAppName}") .status-msg:contains("Expression of interest") + .status-msg:contains("Submitted")

Parter applicant can not view evidence upload section
    [Documentation]  IFS-12958
    Given the user clicks the button/link       link = ${hecpPreregReleaseFeedbackAppName}
    Then the user should not see the element    name = eoiEvidenceFile

Internal users can see submitted expression of interest applications without checkbox when the eveidence is not uploaded
    [Documentation]  IFS-12958
    Given log in as a different user            &{ifs_admin_user_credentials}
    And the user navigates to the page          ${server}/management/competition/${preregReleaseFeedbackCompetitionId}
    And the user clicks the button/link         link = Applications: All, submitted, expression of interest, ineligible
    When the user clicks the button/link        link = Expressions of interest
    Then the user should see the element        jQuery = td:contains("${preregReleaseFeedbackApplicationID}") + td:contains("${hecpPreregReleaseFeedbackAppName}")
    And the user should see the element         jQuery = .highlight-panel:contains("Expressions of interest") span:contains("1")
    And the user should not see the element     jQuery = label[for = "app-row-1"]

Expression of interest evidence upload validation : wrong file upload
    [Documentation]  IFS-12958
    Given log in as a different user                        &{lead_applicant_credentials}
    And the user navigates to the page                      ${server}/application/${preregReleaseFeedbackApplicationID}/track
    When the user uploads the file                          eoiEvidenceFile    ${excel_file}
    Then the user should not see a field error              Your upload must be a PDF.
    And The user should see valid evidence upload content

Applicant can remove the file uploaded
    [Documentation]  IFS-12958
    Given the user uploads the file             eoiEvidenceFile    ${contract_pdf}
    When the user can remove the uploaded file  remove-eoi-evidence  ${contract_pdf}
    Then the user should not see the element    jQuery = a:contains("${contract_pdf} (opens in a new window)")

Comp admin can not view mark as ineligible application link
    [Documentation]  IFS-12958
    Given log in as a different user                &{ifs_admin_user_credentials}
    When the user navigates to the page             ${server}/management/competition/${preregReleaseFeedbackCompetitionId}/application/${preregReleaseFeedbackApplicationID}
    Then the user should not see the element        jQuery = span:contains("Mark application as ineligible")

Internal users can see expression of interest statistics
    [Documentation]  IFS-12958
    Given the user navigates to the page        ${server}/management/competition/${preregReleaseFeedbackCompetitionId}
    When the user clicks the button/link        link = Applications: All, submitted, expression of interest, ineligible
    Then the user should see the element        jQuery = .highlight-panel:contains("Expressions of interest") span:contains("1")

Lead organisation should get notified on submitting the EOI evidence
    [Documentation]  IFS-12958
    When Lead applicant submits evidence for review    ${preregReleaseFeedbackApplicationID}   ${contract_pdf}
    Then the user should see the element               link = Contract.pdf (opens in a new window)
    And the user reads his email                       ${lead_applicant_credentials["email"]}  ${evidenceSubmittedEmailSubject}  ${evidenceSubmittedEmailDescriptionReleaseFeedback}

Lead applicant views read only evidence file submitted for review
    [Documentation]  IFS-12958
    When the user navigates to the page         ${server}/application/${preregReleaseFeedbackApplicationID}/summary
    Then the user checks file is downloaded     ${contract_pdf}
    And the user should see the element         jQuery = h3:contains("Eoi Evidence")

Partner applicant can not view read only evidence uploaded by lead applicant
    [Documentation]  IFS-12958
    Given log in as a different user             ${collaborator1_credentials["email"]}  ${short_password}
    When the user navigates to the page          ${server}/application/${preregReleaseFeedbackApplicationID}/summary
    Then the user should not see the element     jQuery = h3:contains("Eoi Evidence")
    And the user should not see the element      jQuery = a:contains("${contract_pdf}")

Internal user submit the EOI applications funding decision after evidence is uploaded
    [Documentation]  IFS-12958
    Given Existing user creates and submits new application for unsuccessful EOI journey
    And Requesting application ID of unsuccessful prereg application
    When Lead applicant submits evidence for review                                         ${unSuccessfulReleaseFeedbackPreRegApplicationID}   ${contract_pdf}
    And Log in as a different user                                                          &{Comp_admin1_credentials}
    And Internal user marks the EOI as successful/unsuccessful                              ${unSuccessPreregReleaseFeedbackAppName}   EOI_REJECTED
    And Internal user marks the EOI as successful/unsuccessful                              ${hecpPreregReleaseFeedbackAppName}   EOI_APPROVED
    Then the user should see the element                                                    jQuery = td:contains("${preregReleaseFeedbackApplicationID}")+td:contains("${hecpPreregReleaseFeedbackAppName}")+td:contains("Empire Ltd")+td:contains("Successful")
    And the user should see the element                                                     jQuery = td:contains("${unSuccessfulReleaseFeedbackPreRegApplicationID}")+td:contains("${unSuccessPreregReleaseFeedbackAppName}")+td:contains("Empire Ltd")+td:contains("Unsuccessful")

Write and send email button enabled for internal users
    [Documentation]    IFS-12958
    When the user clicks the button/link        Link = Manage notifications
    Then the user should see the element        jQuery = h1:contains("Expression of interest notifications")
    And User should see EOI Related content
    And the user selects the checkbox           app-row-${preregReleaseFeedbackApplicationID}
    And The user should not see the element     css = .govuk-button[disabled]

Internal user should view EOI related content in the notification template
    [Documentation]  IFS-12958
    When the user clicks the button/link    jQuery = button:contains("Write and send email")
    Then the user should see the element    jQuery = h1:contains("Send an expression of interest notification")
    And the user should see the element     css = [value="Notification regarding your expression of interest application '[application name]' for the competition '[competition name]'"]
    And the user should see the element     jQuery = th:contains("Expression of interest decision")

Internal user sends a successful notification of an EOI application
    [Documentation]    IFS-12958
    When the user clicks the button/link                    jQuery = button:contains("Send notification")[data-js-modal = "send-to-all-applicants-modal"]
    And the user clicks the button/link                     jQuery = .send-to-all-applicants-modal button:contains("Send email to all applicants")
    And the user refreshes until element appears on page    jQuery = td:contains("${hecpPreregReleaseFeedbackAppName}") ~ td:contains("Sent")
    Then the user reads his email                           steve.smith@empire.com  Notification regarding your expression of interest application '${hecpPreregReleaseFeedbackAppName}' for the competition '${hecpPreregReleaseFeedbackCompName}'  ${preRegApplicationSuccessfulEmail}
    And the user navigates to the page                      ${server}/management/competition/${preregReleaseFeedbackCompetitionId}/applications/all
    And the user should see the element                     jQuery = td:contains("${preregReleaseFeedbackApplicationID}")+td:contains("${hecpPreregReleaseFeedbackAppName}")

Internal user sends a unsuccessful notification of an EOI application
    [Documentation]    IFS-12958
    Given the user navigates to the page                    ${server}/management/competition/${preregReleaseFeedbackCompetitionId}/eoi/notification
    And the user selects the checkbox                       app-row-${unSuccessfulReleaseFeedbackPreRegApplicationID}
    When the user clicks the button/link                    jQuery = button:contains("Write and send email")
    And the user clicks the button/link                     jQuery = button:contains("Send notification")[data-js-modal = "send-to-all-applicants-modal"]
    And the user clicks the button/link                     jQuery = .send-to-all-applicants-modal button:contains("Send email to all applicants")
    And the user refreshes until element appears on page    jQuery = td:contains("${unSuccessPreregReleaseFeedbackAppName}") ~ td:contains("Sent")
    Then the user reads his email                           steve.smith@empire.com  Notification regarding your expression of interest application '${unSuccessPreregReleaseFeedbackAppName}' for the competition '${hecpPreregReleaseFeedbackCompName}'  ${preRegApplicationUnsuccessfulEmailReleaseFeedback}

Lead applicant views unsuccessful applications in previous dashboard
    [Documentation]  IFS-12958
    Given log in as a different user                            &{lead_applicant_credentials}
    When the user clicks the application tile if displayed
    Then the user should see the element                        jQuery = li:contains("${unSuccessPreregReleaseFeedbackAppName}") .status-msg:contains("Unsuccessful")
    And the user should see the element                         jQuery = li:contains("${unSuccessPreregReleaseFeedbackAppName}") .status-msg:contains("Expression of interest")

Lead applicant can view full application details in dashboard
    [Documentation]  IFS-12958
    When the user navigates to the page         ${APPLICANT_DASHBOARD_URL}
    Then the user should see the element        jQuery = .in-progress li:contains("${hecpPreregReleaseFeedbackAppName}") .status:contains("% complete")
    And the user should not see the element     jQuery = li:contains("${preregReleaseFeedbackApplicationID}") .status-msg:contains("Expression of interest")

Lead applicant can view the answers provided in EOI applications in full application along with new application questions
    [Documentation]  IFS-12958
    Given the user clicks the button/link                                   link = ${hecpPreregReleaseFeedbackAppName}
    When the user clicks the button/link                                    link = 1. Tell us where your organisation is based
    Then the user should see the element                                    jQuery = p:contains("My organisation is based in the UK or a British Overseas Territory")
    And Lead applicant should see new questions added in full application

Lead applicant can navigate to orginal EOI application from full application
    [Documentation]  IFS-12958
    Given the user clicks the button/link                    link = Back to application overview
    When the user clicks the button/link                     link = Expression of interest
    Then the user clicks the button/link                     jQuery = h1:contains("Expression of interest overview")
    And the user should see the element                      jQuery = h2:contains("Congratulations, your application has been successful")
    And the user should see the element                      jQuery = h3:contains("Eoi Evidence")
    And the user checks file is downloaded                   ${contract_pdf}

Partner completes project finances and terms and conditions in full application
    [Documentation]  IFS-12958
    Given log in as a different user                             ${collaborator1_credentials["email"]}  ${short_password}
    When the user navigates to the page                          ${server}/application/${preregReleaseFeedbackApplicationID}
    Then the user completes the project location
    And the user accept the competition terms and conditions     Back to application overview

Lead applicant completes remaining questions and submits full application
    [Documentation]  IFS-12958
    Given log in as a different user                            &{lead_applicant_credentials}
    When the user navigates to the page                         ${server}/application/${preregReleaseFeedbackApplicationID}
    And the user completes remaining application questions
    And the user completes the project location
    And the user accept the competition terms and conditions    Back to application overview
    And the user clicks the button/link                         id = application-overview-submit-cta
    And the user clicks the button/link                         id = submit-application-button
    Then the user should see the element                        jQuery = h2:contains("Application submitted")
    And the user reads his email                                steve.smith@empire.com  ${preregReleaseFeedbackApplicationID}: Successful submission of application   You have successfully submitted an application for funding to ${hecpPreregReleaseFeedbackCompName}.

Internal user marks the full application as successful and sent a notification
    [Documentation]  IFS-12958
    Given Log in as a different user                                &{Comp_admin1_credentials}
    And The user clicks the button/link                             link = ${hecpPreregReleaseFeedbackCompName}
    When the internal team mark the application as successful       ${hecpPreregReleaseFeedbackAppName}   FUNDED
    And the user clicks the button/link                             link = Competition
    And the internal team notifies all applicants                   ${preregReleaseFeedbackApplicationID}
    Then the user reads his email                                   steve.smith@empire.com  Important message about your application '${hecpPreregReleaseFeedbackAppName}' for the competition '${hecpPreregReleaseFeedbackCompName}'  ${fullApplicationSuccessfulEmail}

Auditor can view and download evidence file submitted
    [Documentation]  IFS-12958
    Given log in as a different user            &{auditorCredentials}
    When the user navigates to the page         ${server}/management/competition/${preregReleaseFeedbackCompetitionId}/application/${preregReleaseFeedbackApplicationID}
    And the user clicks the button/link         link = Expression of interest
    Then the user checks file is downloaded     ${contract_pdf}
    And the user should see the element         jQuery = h3:contains("Eoi Evidence")


*** Keywords ***
Requesting IDs of this hecp pre reg competition
    [Arguments]  ${competitionName}
    ${hecpPreregCompId} =  get comp id from comp title  ${hecpPreregReleaseFeedbackCompName}
    Set suite variable  ${hecpPreregCompId}

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom Suite Teardown
    the user closes the browser
    Disconnect from database

Requesting application ID of prereg application
    [Arguments]  ${applicationName}
    ${preregReleaseFeedbackApplicationID} =  get application id by name  ${applicationName}
    Set suite variable    ${preregReleaseFeedbackApplicationID}

Requesting application ID of unsuccessful prereg application
    ${unSuccessfulReleaseFeedbackPreRegApplicationID} =  get application id by name  ${unSuccessPreregReleaseFeedbackAppName}
    Set suite variable    ${unSuccessfulReleaseFeedbackPreRegApplicationID}

the user should see EOI labels for prereg application
    the user should see the element      jQuery = h1:contains("Expression of interest overview")
    the user should see the element      jQuery = h2:contains("Expression of interest progress")
    the user should see the element      jQuery = h2:contains("Expression of interest questions")

the user completes prereg project finances
    [Arguments]  ${Application}   ${Project_growth_table}
    the user clicks the button/link                     link = Your project finances
    The user is able to complete hecp project costs
    Run Keyword if  '${Project_growth_table}' == 'no'   the user fills in the organisation information  ${Application}  ${SMALL_ORGANISATION_SIZE}
    Run Keyword if  '${Project_growth_table}' == 'yes'  the user fills the organisation details with Project growth table  ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user completes prereg funding section           ${Application}
    the user clicks the button/link                     link = Back to expression of interest overview

The user is able to complete hecp project costs
    the user clicks the button/link           link = Your project costs
    the user should see the element           jQuery = h1:contains("Your project costs")
    the user enters text to a text field      id = personnel  50000
    the user enters text to a text field      id = subcontracting  50000
    the user enters text to a text field      id = travel  10000
    the user enters text to a text field      id = equipment  30000
    the user enters text to a text field      id = otherGoods  20000
    the user enters text to a text field      id = other  40000
    the user enters text to a text field      id = hecpIndirectCosts  0
    the user clicks the button/link           jQuery = button:contains("Mark")
    the user should see the element           jQuery = li:contains("Your project costs") > .task-status-complete

the user completes prereg funding section
    [Arguments]  ${Application}
    the user clicks the button/link             link = Your funding
    the user fills in the funding information   ${Application}   no

the competition admin creates prereg competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user navigates to the page                              ${CA_UpcomingComp}
    the user clicks the button/link                             jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details                    ${competition}  ${month}  ${nextyear}  ${compType_HORIZON_EUROPE}  ${fundingRule}  HECP
    the user selects the Terms and Conditions                   ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user completes project impact section                   No
    the user fills in the CS Project eligibility                ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user fills in the CS funding eligibility                false   ${compType_HORIZON_EUROPE}  ${fundingRule}
    the user selects the organisational eligibility to no       false
    the user completes milestones with out assessment
    the user marks the prereg application question as done
    the user fills in the CS Documents in other projects
    the user clicks the button/link                             link = Public content
    the user fills in the Public content and publishes          ${extraKeyword}
    the user clicks the button/link                             link = Return to setup overview
    the user clicks the button/link                             jQuery = a:contains("Complete")
    the user clicks the button/link                             jQuery = button:contains('Done')
    the user navigates to the page                              ${CA_UpcomingComp}
    the user should see the element                             jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user marks the prereg application question as done
    the user clicks the button/link                                 link = Application
    the user marks each question as complete                        Application details
    the assessed questions are marked complete(HECP type)
    the user clicks the button/link                                 jQuery = .govuk-heading-s a:contains("Finances")
    the user clicks the button/link                                 jQuery = button:contains("Done")
    the user clicks the button/link                                 jQuery = button:contains("Done")
    the user clicks the button/link                                 link = Back to competition details
    the user should see the element                                 jQuery = div:contains("Application") ~ .task-status-complete

the user completes milestones with out assessment
    the user clicks the button/link                     link = Milestones
    the user clicks the button twice                    jQuery = label:contains("Release feedback")
    the user clicks the button/link                     jQuery = button:contains("Done")
    the user completes application submission page      Yes
    the user inputs application expression of interest  Yes
    the user inputs application assessment decision     No
    the user clicks the button/link                     jQuery = button:contains("Done")
    the user clicks the button/link                     link = Back to competition details
    the user should see the element                     jQuery = div:contains("Milestones") ~ .task-status-complete

Get competitions id and set it as suite variable
    [Arguments]  ${competitionTitle}
    ${preregReleaseFeedbackCompetitionId} =  get comp id from comp title  ${competitionTitle}
    Set suite variable  ${preregReleaseFeedbackCompetitionId}

the user should not see subsection
    [Arguments]   ${subSectionName}
    the user clicks the button/link         link = Your project finances
    the user should not see the element     link = ${subSectionName}

Comp admin set the competion as prereg comp and hide the question, section and subsection
    set subsection as hidden in pre reg application      ${preregReleaseFeedbackCompetitionId}
    set section as hidden in pre reg application         ${preregReleaseFeedbackCompetitionId}
    update milestone to yesterday                        ${preregReleaseFeedbackCompetitionId}  OPEN_DATE

Existing user creates and submits new application for unsuccessful EOI journey
    log in as a different user                                                &{lead_applicant_credentials}
    Existing applicant creates a new application with same organisation       ${hecpPreregReleaseFeedbackCompName}
    the user completes the application details section                        ${unSuccessPreregReleaseFeedbackAppName}  ${tomorrowday}  ${month}  ${nextyear}   23
    the applicant completes Application Team                                  COMPLETE  steve.smith@empire.com
    the user complete pre reg work programme
    #the user is able to complete horizon grant agreement section
    the lead applicant fills all the questions and marks as complete(prereg)
    the user completes prereg project finances                                ${unSuccessPreregReleaseFeedbackAppName}   no
    the user clicks the button/link                                           id = application-overview-submit-cta
    the user clicks the button/link                                           id = submit-application-button

Internal user marks the EOI as successful/unsuccessful
    [Arguments]  ${applicationName}  ${decision}
    the user navigates to the page                      ${server}/management/competition/${preregReleaseFeedbackCompetitionId}/applications/eoi
    the user clicks the button/link                     jQuery = tr:contains("${applicationName}") label
    the user clicks the button/link                     css = [type="submit"][value="${decision}"]

Internal user sends a decision notifications to applicants
    Requesting application ID of prereg application     ${applicationName}
    the internal team notifies all applicants           ${preregReleaseFeedbackApplicationID}

Internal user closes the competition
    log in as a different user          &{ifs_admin_user_credentials}
    update milestone to yesterday       ${preregReleaseFeedbackCompetitionId}  SUBMISSION_DATE
    the user navigates to the page      ${server}/management/competition/${preregReleaseFeedbackCompetitionId}
    the user clicks the button/link     link = Close competition
    the user clicks the button/link     jQuery = button:contains("Close competition")

Lead applicant deletes the unsubmitted EOI application
    log in as a different user                          &{lead_applicant_credentials}
    the user clicks the application tile if displayed
    the user should see the element                     jQuery = li:contains("${unSubmittedPreregReleaseFeedbackAppName}") .status-msg:contains("Expression of interest")
    Requesting application ID of prereg application     ${unSubmittedPreregReleaseFeedbackAppName}
    the user clicks the button/link                     name = delete-application-${preregReleaseFeedbackApplicationID}
    the user clicks the button/link                     jQuery = li:contains("${unSubmittedPreregReleaseFeedbackAppName}") button:contains("Delete application")

the user complete pre reg work programme
    the user clicks the button/link     jQuery = a:contains("Work programme")
    the user clicks the button twice    jQuery = label:contains("Culture, Creativity and Inclusive Society (CL2)")
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user clicks the button twice    jQuery = label:contains("HORIZON-CL2-2021-DEMOCRACY-01")
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user clicks the button/link     id = application-question-complete
    the user clicks the button/link     link = Back to application overview
    the user should see the element     jQuery = li:contains("Work programme") > .task-status-complete

User should see EOI Related content
    the user should see the element     jQuery = th:contains("Select applications")
    the user should see the element     jQuery = th:contains("Application number")
    the user should see the element     jQuery = th:contains("Project title")
    the user should see the element     jQuery = th:contains("Lead organisation")
    the user should see the element     jQuery = th:contains("Expression of interest decision")
    the user should see the element     jQuery = th:contains("Email status")
    the user should see the element     jQuery = th:contains("Date sent")

Lead applicant should see new questions added in full application
    the user clicks the button/link     link = Back to application overview
    the user should see the element     link = 2. Participating Organisation project region
    the user should see the element     link = Award terms and conditions
    the user clicks the button/link     link = Your project finances
    the user should see the element     link = Your project location

the user completes remaining application questions
    the user clicks the button/link     link = 2. Participating Organisation project region
    wait until keyword succeeds without screenshots   10s    200ms     input text       id = multipleChoiceOptionId  London
    Execute Javascript                  document.evaluate("//li[text()='London']",document.body,null,9,null).singleNodeValue.click();
    the user clicks the button/link     id = application-question-complete
    the user clicks the button/link     link = Back to application overview
    the user should see the element     jQuery = li:contains("2. Participating Organisation project region") > .task-status-complete

the user completes the project location
    the user clicks the button/link         link = Your project finances
    the user enters the project location
    the user clicks the button/link         link = Back to application overview
    the user should see the element         jQuery = li:contains("Your project finances") > .task-status-complete

the internal team mark the application as successful
    [Arguments]   ${applicationName}   ${decision}
    the user navigates to the page      ${server}/management/competition/${preregReleaseFeedbackCompetitionId}
    the user clicks the button/link     link = Input and review funding decision
    the user clicks the button/link     jQuery = tr:contains("${applicationName}") label
    the user clicks the button/link     css = [type="submit"][value="${decision}"]

Update competition to have evidence required
    execute sql string    INSERT INTO `ifs`.`competition_eoi_evidence_config` (`id`, `evidence_required`, `evidence_title`, `evidence_guidance`) VALUES ('51', 1, 'Eoi Evidence', 'upload eoi evidence');
    execute sql string    UPDATE `ifs`.`competition` SET `competition_eoi_evidence_config_id` = '51' WHERE id = '${preregReleaseFeedbackCompetitionId}';
    execute sql string    INSERT INTO `ifs`.`eoi_evidence_config_file_type` (`id`, `competition_eoi_evidence_config_id`, `file_type_id`) VALUES ('41', '51', '1');

Partner applicant completes prereg project finances
    [Arguments]   ${application_title}  ${collaboratorEmail}  ${collaboratorPassword}
    logging in and error checking                    ${collaboratorEmail}  ${collaboratorPassword}
    the user clicks the button/link                  css = .govuk-button[type="submit"]    #Save and continue
    the user completes prereg project finances       ${hecpPreregReleaseFeedbackAppName}   no

Update application evidence has uploaded
    [Arguments]  ${dbValue}  ${applicationID}  ${fileID}
    execute sql string    INSERT INTO `ifs`.`application_eoi_evidence_response` (`id`, `application_id`, `organisation_id`, `file_entry_id`) VALUES ('${dbValue}', '${applicationID}', '21', '${fileID}');

The user should see valid evidence upload content
    the user clicks the button/link   jQuery = span:contains("What should I include?")
    the user should see the element   jQuery = p:contains("upload eoi evidence")
    the user should see the element   jQuery = h2:contains("Eoi Evidence")
    the user should see the element   jQuery = p:contains("Accepted file types")+ul:contains("PDF")
    the user should see the element   jQuery = p:contains(" It must be less than 32MB in size.")

Lead applicant submits evidence for review
    [Arguments]  ${applicationId}  ${fileName}
    Log in as a different user          &{lead_applicant_credentials}
    the user navigates to the page      ${server}/application/${applicationId}/track
    the user uploads the file           eoiEvidenceFile    ${fileName}
    the user clicks the button/link     id = submit-eoi-evidence

the user checks file is downloaded
    [Arguments]  ${fileName}
    the user clicks the button/link         jQuery = a:contains("${fileName}")
    Select Window                           NEW
    the user should not see internal server and forbidden errors
    the user closes the last opened tab

Internal user can view and download evidence file
    [Arguments]  ${competitionID}  ${applicationID}  ${fileName}
    the user navigates to the page          ${server}/management/competition/${competitionID}/application/${applicationID}
    the user clicks the button/link         link = Expression of interest
    the user should see the element         jQuery = h3:contains("Eoi Evidence")
    the user checks file is downloaded      ${fileName}

the user marks the competition application section as complete
    the user clicks the button/link  link = Back to application
    the user clicks the button/link  jQuery = button:contains("Done")
