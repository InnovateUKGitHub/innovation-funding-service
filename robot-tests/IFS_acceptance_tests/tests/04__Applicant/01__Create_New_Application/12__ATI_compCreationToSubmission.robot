*** Settings ***
Documentation     IFS-2396  ATI Competition type template
...
...               IFS-2332  Project Finance user is not able to download the overheads file
...
...               IFS-1497  As an applicant I am able to confirm the project location for my organisation
...
...               IFS-3421  As a Lead applicant I am unable submit an ineligible application to a Collaborative competition
...
...               IFS-6725  Guidance Improvement to 'Funding level' in 'Your Funding' in application
...
...               IFS-7718  EDI question - application form
...
...               IFS-7547  Lead applicant can reopen a submitted application
...
...               IFS-7550  Lead applicant can edit and resubmit opened application
...
...               IFS-7647 MO visibility of submitted applications
...
...               IFS-8779 Subsidy Control - Create a New Competition - Initial Details
...
...               IFS-8729 ATI Assessor 'doesn't have the right permissions' to access appendices
...
...               IFS-7723 Improvement to company search results
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${ATIcompetitionTitle}                ATI Competition
${ATIapplicationTitle}                ATI application
${project_team_question}              8. Project team
${technicalApproach_question}         5. Technical approach
${answerToSelect}                     answer2
${partnerEmail}                       test1@test.com
${fundingSoughtValidationMessage}     Your total funding sought exceed
${assessor1_to_add}                   Addison Shannon
${assessor2_to_add}                   Alexis Colon
${assessor1_email}                    addison.shannon@gmail.com
${assessor2_email}                    alexis.colon@gmail.com

*** Test Cases ***
Comp Admin creates an ATI competition
    [Documentation]  IFS-2396  IFS-8779
    Given The user logs-in in new browser               &{Comp_admin1_credentials}
    Then the competition admin creates competition      ${business_type_id}  ${ATIcompetitionTitle}  ATI  ${compType_ATI}  SUBSIDY_CONTROL  GRANT  PROJECT_SETUP  yes  1  true  collaborative

Applicant applies to newly created ATI competition
    [Documentation]  IFS-2286
    Given get competition id and set open date to yesterday  ${ATIcompetitionTitle}
    When log in as a different user                          &{lead_applicant_credentials}
    Then logged in user applies to competition               ${ATIcompetitionTitle}  1

Single applicant cannot submit his application to a collaborative comp
    [Documentation]  IFS-2286  IFS-2332  IFS-1497  IFS-3421  IFS-5920  IFS-6725  IFS-7703  IFS-7718
    When the user completes the application
    Then the application cannot be submited

The lead invites a collaborator and sets the max available funding to 50000
    [Documentation]  IFS-3421  IFS-5920
    Given the lead invites already registered user            ${collaborator1_credentials["email"]}  ${ATIcompetitionTitle}
    When partner applicant completes the project finances     ${ATIapplicationTitle}  no  ${collaborator1_credentials["email"]}  ${short_password}
    Then the user sets max available funding                  50000   ${competitionId}

lead completes the application team and assigns an application question to partner organisation
     [Documentation]  IFS-7703
     [Setup]  Log in as a different user                      ${lead_applicant}  ${short_password}
     Given the user clicks the button/link                    link = ${ATIapplicationTitle}
     When the applicant completes Application Team
     Then lead assigns a question to partner organisation     ${project_team_question}

The partner answers the question and assigns the question back to lead for review
     [Documentation]  IFS-7703
     [Setup]  log in as a different user            &{collaborator1_credentials}
     Given the user clicks the button/link          link = ${ATIapplicationTitle}
     When the user clicks the button/link           link = ${project_team_question}
     Then the partner selects new answer choice

The lead should see the answer selected by partner and mark it as complete
     [Documentation]  IFS-7703
     [Setup]  log in as a different user                 &{lead_applicant_credentials}
     Given the user clicks the button/link               link = ${ATIapplicationTitle}
     When the user clicks the button/link                link = ${project_team_question}
     Then the user should not see the element            link = testing.pdf (opens in a new window)
     And the user can mark the question as complete

Finance overview incomplete when over max funding
    [Documentation]  IFS-7866
    Given the user should see the element     jQuery = li:contains("Finances overview") .task-status-incomplete
    When the user clicks the button/link      link = Finances overview
    Then the user should see the element      jQuery = p:contains("${fundingSoughtValidationMessage}")

Your funding validation when over max funding
    [Documentation]  IFS-7866
    Given update project costs
    When the user edits your funding
    Then the user should see a field and summary error     Your funding sought exceeds £50,000. You must lower your funding level percentage or your project costs.

Update your funding to be valid
    [Documentation]  IFS-7866
    Given the user enters text to a text field                     css = [name^="grantClaimPercentage"]  20
    When The user clicks the button/link                           id = mark-all-as-complete
    Then the user should see the finances overview as complete

The lead can now submit the application
     [Documentation]  IFS-3421  IFS-5920  IFS-7703
     Given the applicant submits the application

Comp admin can see the ATI application submitted
    [Documentation]  IFS-7550
    [Setup]  log in as a different user      &{Comp_admin1_credentials}
    When the user navigates to the page      ${server}/management/competition/${competitionId}/applications/submitted
    Then the user should see the element     jQuery = td:contains("${ATIapplicationTitle}")

Comp admin amends the assessment panel and interview stage options
    [Documentation]  IFS-8729
    Given the user navigates to the page       ${server}/management/competition/setup/${competitionId}/section/assessors
    When the user clicks the button/link       jQuery = button:contains("Edit")
    Then the user selects the radio button     hasAssessmentPanel  1
    And the user selects the radio button      hasInterviewStage  1
    And the user clicks the button/link        jQuery = button:contains("Done")

Collaborator cannot reopen the application
    [Documentation]  IFS-7547
    Given log in as a different user             &{collaborator1_credentials}
    When the user should see the element         link = ${ATIapplicationTitle}
    Then the user should not see the element     jQuery = li:contains("${ATIapplicationTitle}") a:contains("Reopen")

Lead can reopen application and gets an email notification including collaborators
    [Documentation]  IFS-7547  IFS-7550  IFS-7549
    [Setup]  log in as a different user      &{lead_applicant_credentials}
    When the user clicks the button/link     link = Dashboard
    Then the user can reopen application     ${ATIapplicationTitle}
    And the user reads his email             ${collaborator1_credentials["email"]}     	 An Innovation Funding Service funding application has been reopened   The application was reopened by
    And the user reads his email             ${lead_applicant_credentials["email"]}      An Innovation Funding Service funding application has been reopened   You reopened this application

Lead can make changes to the application and assign a question to collaborator
    [Documentation]  IFS-7547  IFS-7550
    When the user uploads an appendix                       ${project_team_question}  ${5mb_pdf}
    And lead assigns a question to partner organisation     ${technicalApproach_question}
    Then the user should not see the element                id = edit

Comp Admin should not see the ATI application in submitted applications
    [Documentation]  IFS-7550
    [Setup]  log in as a different user          &{Comp_admin1_credentials}
    When the user navigates to the page          ${server}/management/competition/${competitionId}/applications/submitted
    Then the user should not see the element     jQuery = td:contains("${ATIapplicationTitle}")

Collaborator can see the application is reopenend and complete the assigned question
    [Documentation]  IFS-7550
    [Setup]  log in as a different user                  &{collaborator1_credentials}
    When the user clicks the button/link                 link = ${ATIapplicationTitle}
    Then the user can complete the assigned question     ${technicalApproach_question}
    And the user should see the element                  jQuery = p:contains("This application was reopened by the lead applicant")

Lead can review the question and submit the application
    [Documentation]  IFS-7550
    [Setup]  log in as a different user                  &{lead_applicant_credentials}
    Given the user clicks the button/link                link = ${ATIapplicationTitle}
    When the user clicks the button/link                 link = ${technicalApproach_question}
    Then the user can mark the question as complete
    And the user can submit the application

Lead does not see reopen when the comp is closed
    [Documentation]  IFS-7547
    Given Log in as a different user             &{Comp_admin1_credentials}
    When moving competition to Closed            ${competitionId}
    And log in as a different user               &{lead_applicant_credentials}
    Then the user should not see the element     jQuery = li:contains("${ATIapplicationTitle}") a:contains("Reopen")

Comp admin assigns assessors to the competition and assigns the application to an assessor
    [Documentation]  IFS-8729
    [Setup]  log in as a different user                                      &{Comp_admin1_credentials}
    Given the user navigates to the page                                     ${server}/management/competition/${competitionId}/assessors/find
    When the user invites assessors to assess the ATI competition
    And the assessors accept the invitation to assess the ATI competition
    Then the application is assigned to a assessor

Comp admin invites a different assessor through interview panel and assign the application
    [Documentation]  IFS-8729
    [Setup]  log in as a different user                          &{Comp_admin1_credentials}
    Given the user navigates to the page                         ${server}/management/competition/${competitionId}
    And the user clicks the button/link                          jQuery = button:contains("Close assessment")
    When the user invites an assessor through interview panel
    And the user assigns the application to the assessor
    Then the assessor checks the appendices

Internal user marks ATI application to successful
    [Documentation]  IFS-2332
    Given Log in as a different user                                        &{internal_finance_credentials}
    Then making the application a successful project from correct state     ${competitionId}  ${ATIapplicationTitle}

MO can see application summary page for the ATI application in project setup before releasing the feedback
    [Documentation]  IFS-7647
    [Setup]  Requesting Application ID of this application
    Given Internal user assigns MO to application              ${atiApplicationID}  ${ATIapplicationTitle}  Orvill  Orville Gibbs
    When Log in as a different user                            &{monitoring_officer_one_credentials}
    And the user navigates to the page                         ${server}/application/${atiApplicationID}/summary
    And the user should see the element                        jQuery = h1:contains("Application overview")

Internal user add new partner orgnisation after moving competition to project setup
    [Documentation]  IFS-6725  IFS-7723
    [Setup]  Requesting Project ID of this Project
    Given Log in as a different user                             &{internal_finance_credentials}
    And moving competition to Project Setup                      ${competitionId}
    When the user navigates to the page                          ${server}/project-setup-management/competition/${competitionId}/project/${ProjectID}/team/partner
    And the user adds a new partner organisation                 Testing Admin Organisation  Name Surname  ${partnerEmail}
    Then a new organisation is able to accept project invite     Name  Surname  ${partnerEmail}  ROYAL  ROYAL MAIL PLC  ${atiApplicationID}  ${ATIapplicationTitle}

New partner orgination checks for funding level guidance
    [Documentation]  IFS-6725
    Given log in as a different user                                ${partnerEmail}   ${short_password}
    When the user clicks the button/link                            link = ${ATIapplicationTitle}
    And The new partner can complete Your organisation
    Then the user checks for funding level guidance at PS level

Applicant completes Project Details
    [Documentation]  IFS-2332
    When log in as a different user              &{lead_applicant_credentials}
    Then project lead submits project address    ${ProjectID}

Project Finance is able to see the Overheads costs file
    [Documentation]  IFS-2332
    Given Log in as a different user            &{internal_finance_credentials}
    When the user navigates to the page         ${SERVER}/project-setup-management/project/${ProjectID}/finance-check/
    And the user clicks the button/link         jQuery = tr:contains("Empire Ltd") td:nth-child(4) a:contains("Review")
    And the user expands the section            Overhead costs
    Then the user should see the element        jQuery = a:contains("${excel_file}")
    And the user should not see the element     jQuery = .govuk-details__summary span:contains("Overheads costs guidance")
    And the project finance user is able to download the Overheads file    ${ProjectID}  22
    # TODO IFS-2599 Raised to improve this as we cannot rely on hard-coded values.

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Requesting Project ID of this Project
    ${ProjectID} =  get project id by name    ${ATIapplicationTitle}
    Set suite variable    ${ProjectID}

Requesting Application ID of this application
    ${atiApplicationID} =  get application id by name  ${ATIapplicationTitle}
    Set suite variable    ${atiApplicationID}

the user can complete the assigned question
    [Arguments]  ${question_link}
    the user clicks the button/link          link = ${question_link}
    the user clicks the button/link          jQuery = label:contains("option1")
    the user clicks the button/link          jQuery = button:contains("Assign to lead for review")
    the user clicks the button/link          link = Back to application overview

the user completes the application
    the user clicks the button/link                                                         link=Application details
    the user fills in the Application details                                               ${ATIapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant completes Application Team
    the applicant marks EDI question as complete
    the lead applicant fills all the questions and marks as complete(programme ATI)
    the lead completes the questions with multiple answer choice and multiple appendices
    the user navigates to Your-finances page                                                ${ATIapplicationTitle}
    the user does not see state aid information
    the user marks the finances as complete                                                  ${ATIapplicationTitle}   Calculate  52,214  yes
    the user clicks the button/link                                                          link = Your project finances
    the user checks for funding level guidance at application level
    the user accept the competition terms and conditions                                     Return to application overview
    the user selects research category                                                       Feasibility studies
    the finance overview is marked as incomplete

the partner selects new answer choice
     the user clicks the button/link                    name = removeAppendix
     the user can remove file with multiple uploads     removeAppendix    ${valid_pdf}
     input text                                         id = multipleChoiceOptionId  ${answerToSelect}
     the user clicks the button/link                    jQuery = ul li:contains("${answerToSelect}")
     the user clicks the button/link                    jQuery = button:contains("Assign to lead for review")
     the user should see the element                    jQuery = p:contains("This question is assigned to"):contains("Steve Smith")

the finance overview is marked as incomplete
    the user clicks the button/link    link = Finances overview
    the user should see the element    jQuery = .warning-alert:contains("This competition only accepts collaborations. At least 2 partners must request funding.")
    the user clicks the button/link    link = Application overview

the application cannot be submited
    the user clicks the button/link                   link = Review and submit
    the user should see that the element is disabled  jQuery = button:contains("Submit application")
    the user clicks the button/link                   link = Application overview

the user does not see state aid information
    the user clicks the button/link      link = Your organisation
    the user should not see the element  jQuery = p:contains("If we decide to award you funding you must be eligible to receive State aid at the point of the award.)
    the user clicks the button/link      link = Your project finances

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

update project costs
    the user clicks the button/link       link = View finances
    the user clicks the button/link       link = Your project costs
    the user clicks the button/link       id = edit
    the user clicks the button/link       jQuery = button:contains("Other costs")
    the user enters text to a text field  css = textarea.govuk-textarea[name$=description]  some other costs
    the user enters text to a text field  css = input.govuk-input[name$=estimate]  60000
    the user clicks the button/link       jQuery = button:contains("Other costs")
    the user selects the checkbox         stateAidAgreed
    the user clicks the button/link       jQuery = button:contains("Mark as complete")

the user edits your funding
    the user clicks the button/link      link = Your funding
    the user clicks the button/link      jQuery = button:contains("Edit your funding")
    the user clicks the button/link      id = mark-all-as-complete

the user should see the finances overview as complete
    the user clicks the button/link          link = Back to finances overview
    the user should not see the element      jQuery = p:contains("${fundingSoughtValidationMessage}")
    the user clicks the button/link          link = Application overview
    Then the user should see the element     jQuery = li:contains("Finances overview") .task-status-complete

the user invites assessors to assess the ATI competition
    the user clicks the button/link     link = 1 to 20
    the user selects the checkbox       assessor-row-1
    the user selects the checkbox       assessor-row-2
    the user clicks the button/link     jQuery = button:contains("Add selected to invite list")
    the user should see the element     jQuery = td:contains("${assessor1_to_add}")
    the user should see the element     jQuery = td:contains("${assessor2_to_add}")
    the user clicks the button/link     jQuery = a:contains("Review and send invites")
    the user clicks the button/link     jQuery = .govuk-button:contains("Send invite")

the assessors accept the invitation to assess the ATI competition
    log in as a different user                            ${assessor1_email}   ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = ATI Competition
    the user selects the radio button                     acceptInvitation   true
    the user clicks the button/link                       jQuery = button:contains("Confirm")
    log in as a different user                            ${assessor2_email}   ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = ATI Competition
    the user selects the radio button                     acceptInvitation   true
    the user clicks the button/link                       jQuery = button:contains("Confirm")

the application is assigned to a assessor
    log in as a different user            &{Comp_admin1_credentials}
    the user navigates to the page        ${server}/management/assessment/competition/${competitionId}/applications
    the user clicks the button/link       link = Assign
    the user selects the checkbox         assessor-row-1
    the user clicks the button/link       jQuery = button:contains("Add to application")
    the user navigates to the page        ${server}/management/competition/${competitionId}
    the user clicks the button/link       jQuery = button:contains("Notify assessors")
    log in as a different user            ${assessor1_email}   ${short_password}
    the user navigates to the page        ${server}/assessment/assessor/dashboard/competition/${competitionId}
    the user clicks the button/link       link = ${ATIapplicationTitle}
    the user selects the radio button     assessmentAccept  true
    the user clicks the button/link       jQuery = button:contains("Confirm")

The user invites an assessor through interview panel
    the user clicks the button/link       link = Manage interview panel
    the user clicks the button/link       link = Invite assessors
    the user selects the checkbox         assessor-row-2
    the user clicks the button/link       jQuery = button:contains("Add selected to invite list")
    the user clicks the button/link       link = Review and send invites
    the user clicks the button/link       jQuery = button:contains("Send invite")
    log in as a different user            ${assessor2_email}   ${short_password}
    the user navigates to the page        ${server}/assessment/assessor/dashboard
    the user clicks the button/link       link = ATI Competition
    the user selects the radio button     acceptInvitation  true
    the user clicks the button/link       jQuery = button:contains("Confirm")

the user assigns the application to the assessor
    log in as a different user            &{Comp_admin1_credentials}
    the user navigates to the page        ${server}/management/assessment/interview/competition/${competitionId}/applications/find
    the user selects the checkbox         assessor-row-1
    the user clicks the button/link       jQuery = button:contains("Add selected to invite list")
    the user clicks the button/link       link = Review and send invites
    the user clicks the button/link       jQuery = button:contains("Send invite")
    the user navigates to the page        ${server}/management/assessment/interview/competition/${competitionId}/assessors/allocate-assessors
    the user clicks the button/link       jQuery = a:contains("Allocate")
    the user selects the checkbox         assessor-row-1
    the user clicks the button/link       jQuery = button:contains("Allocate")
    the user clicks the button/link       css = input[value='Notify']

the assessor checks the appendices
    log in as a different user          ${assessor2_email}   ${short_password}
    the user navigates to the page      ${server}/assessment/assessor/dashboard/competition/${competitionId}/interview
    the user clicks the button/link     link = ATI application
    the user clicks the button/link     jQuery = button:contains("5. Technical approach")
    open pdf link                       jQuery = a:contains("testing.pdf (opens in a new window)")
