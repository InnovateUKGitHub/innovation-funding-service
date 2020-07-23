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
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${ATIcompetitionTitle}            ATI Competition
${ATIapplicationTitle}            ATI application
${project_team_question}          8. Project team
${technicalApproach_question}     5. Technical approach
${answerToSelect}                 answer2

*** Test Cases ***
Comp Admin creates an ATI competition
    [Documentation]  IFS-2396
    Given The user logs-in in new browser               &{Comp_admin1_credentials}
    Then the competition admin creates competition      ${business_type_id}  ${ATIcompetitionTitle}  ATI  ${compType_Programme}  2  GRANT  PROJECT_SETUP  yes  1  true  collaborative
    And user fills in funding overide

Applicant applies to newly created ATI competition
    [Documentation]  IFS-2286
    Given get competition id and set open date to yesterday  ${ATIcompetitionTitle}
    When log in as a different user                          &{lead_applicant_credentials}
    Then logged in user applies to competition               ${ATIcompetitionTitle}  1

Single applicant cannot submit his application to a collaborative comp
    [Documentation]  IFS-2286  IFS-2332  IFS-1497  IFS-3421  IFS-5920  IFS-6725  IFS-7703  IFS-7718
    When the user completes the application
    Then the application cannot be submited

The lead invites a collaborator
    [Documentation]  IFS-3421  IFS-5920
    Given the lead invites already registered user

Assign an application question to partner organisation
     [Documentation]  IFS-7703
     Given lead assigns a question to partner organisation     ${project_team_question}

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

The lead can now submit the application
     [Documentation]  IFS-3421  IFS-5920  IFS-7703
     Given the applicant submits the application

Comp admin can see the ATI application submitted
    [Documentation]  IFS-7550
    [Setup]  log in as a different user      &{Comp_admin1_credentials}
    When the user navigates to the page      ${server}/management/competition/${competitionId}/applications/submitted
    Then the user should see the element     jQuery = td:contains("${ATIapplicationTitle}")

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

Moving ATI Competition to Project Setup
    [Documentation]  IFS-2332
    Given Log in as a different user                     &{internal_finance_credentials}
    Then making the application a successful project     ${competitionId}  ${ATIapplicationTitle}
    And moving competition to Project Setup              ${competitionId}

Internal user add new partner orgnisation
    [Documentation]  IFS-6725
    [Setup]  Requesting Project ID of this Project
    ${applicationId} =  get application id by name  ${ATIapplicationTitle}
    Given the user navigates to the page                       ${server}/project-setup-management/competition/${competitionId}/project/${ProjectID}/team/partner
    When the user adds a new partner organisation              Testing Admin Organisation  Name Surname  test1@test.nom
    Then a new organisation is able to accept project invite   Name  Surname  test1@test.nom  innovate  INNOVATE LTD  ${applicationId}  ${ATIapplicationTitle}

New partner orgination checks for funding level guidance
    [Documentation]  IFS-6725
    Given log in as a different user                                test1@test.nom    ${short_password}
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
    the user checks the override value is applied
    the user selects research category                                                       Feasibility studies
    the finance overview is marked as incomplete

the partner selects new answer choice
     input text                          id = multipleChoiceOptionId  ${answerToSelect}
     the user clicks the button/link     jQuery = ul li:contains("${answerToSelect}")
     the user clicks the button/link     name = removeAppendix
     the user clicks the button/link     jQuery = button:contains("Assign to lead for review")

User fills in funding overide
    the user clicks the button/link                      link = ${ATIcompetitionTitle}
    the user clicks the button/link                      link = View and update competition details
    the user clicks the button/link                      link = Project eligibility
    the user clicks the button/link                      css = .govuk-button[type=submit]
    the user clicks the button twice                     css = label[for="comp-overrideFundingRules-yes"]
    the user enters text to a text field                 id = fundingLevelPercentageOverride  100
    the user clicks the button/link                      jQuery = button:contains("Done")
    the user should see the element                      jQuery = dt:contains("Funding level") ~ dd:contains("100%")
    the user clicks the button/link                      link = Competition details
    the user clicks the button/link                      jQuery = a:contains("Complete")
    the user clicks the button/link                      css = button[type="submit"]

the user checks the override value is applied
    the user clicks the button/link     link = Your project finances
    the user clicks the button/link     link = Your funding
    the user clicks the button/link     jQuery = button:contains("Edit your funding")
    the user should see the element     jQuery = span:contains("The maximum you can enter is 100%")
    the user clicks the button/link     jQuery = button:contains("Mark as complete")
    the user clicks the button/link     link = Back to application overview

the finance overview is marked as incomplete
    the user clicks the button/link    link = Finances overview
    the user should see the element    jQuery = .warning-alert:contains("This competition only accepts collaborations. At least 2 partners must request funding.")
    the user clicks the button/link    link = Application overview

the application cannot be submited
    the user clicks the button/link                   link = Review and submit
    the user should see that the element is disabled  jQuery = button:contains("Submit application")
    the user clicks the button/link                   link = Application overview

the lead invites already registered user
    the user fills in the inviting steps           ${collaborator1_credentials["email"]}
    Logout as user
    the user reads his email and clicks the link   ${collaborator1_credentials["email"]}   Invitation to collaborate in ${ATIcompetitionTitle}    You will be joining as part of the organisation    2
    the user clicks the button/link                link = Continue
    logging in and error checking                  &{collaborator1_credentials}
    the user clicks the button/link                css = .govuk-button[type="submit"]    #Save and continue
    the user clicks the button/link                link = Your project finances
    the user marks the finances as complete        ${ATIapplicationTitle}   Calculate  52,214  yes
    the user accept the competition terms and conditions     Return to application overview
    Log in as a different user                     &{lead_applicant_credentials}
    the user clicks the button/link                link = ${ATIapplicationTitle}
    the applicant completes Application Team

the user does not see state aid information
    the user clicks the button/link      link = Your organisation
    the user should not see the element  jQuery = p:contains("If we decide to award you funding you must be eligible to receive State aid at the point of the award.)
    the user clicks the button/link      link = Your project finances

Custom suite teardown
    Close browser and delete emails
    Disconnect from database