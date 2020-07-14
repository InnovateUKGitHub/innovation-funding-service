*** Settings ***
Documentation  IFS-7146  KTP - New funding type
...
...            IFS-7147  KTP - New set of Terms & Conditions
...
...            IFS-7148  Replace maximum funding level drop down menu with free type field in comp setup
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${KTPcompetitionTitle}  KTP Competition
${KTPapplicationTitle}  KTP Application

*** Test Cases ***
Comp Admin creates an KTP competition
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user logs-in in new browser               &{Comp_admin1_credentials}
    Then the competition admin creates competition      ${business_type_id}  ${KTPcompetitionTitle}  KTP  ${compType_Programme}  2  KTP  PROJECT_SETUP  yes  1  true  single

Comp Admin is able to see KTP funding type has been selected
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  the user clicks the button/link      link = ${KTPcompetitionTitle}
    Given the user clicks the button/link         link = View and update competition details
    When the user clicks the button/link          link = Initial details
    Then the user should see the element          jQuery = dt:contains("Funding type") ~ dd:contains("Knowledge Transfer Partnership (KTP)")
    [Teardown]  the user clicks the button/link   link = Competition details

Comp Admin is able to see KTP T&C's have been selected
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user clicks the button/link     link = Terms and conditions
    Then the user should see the element      link = Knowledge Transfer Partnership (KTP)

Applicant applies to newly created KTP competition
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given get competition id and set open date to yesterday  ${KTPcompetitionTitle}
    When log in as a different user                          &{lead_applicant_credentials}
    Then logged in user applies to competition               ${KTPcompetitionTitle}  1

Applicant is able to complete and submit an application
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user completes the application
    Then the applicant submits the application

Moving KTP Competition to Project Setup
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given Log in as a different user                   &{internal_finance_credentials}
    Then moving competition to Closed                  ${competitionId}
    And making the application a successful project    ${competitionId}  ${KTPapplicationTitle}
    And moving competition to Project Setup            ${competitionId}
    [Teardown]  Requesting IDs of this Project
    
The user is able to complete Project details section
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  the user logs-in in new browser     &{lead_applicant_credentials}
    Given the user navigates to the page         ${server}/project-setup/project/${ProjectID}
    When the user is able to complete project details section
    Then the user should see the element         css = ul li.complete:nth-child(1)

The user is able to complete Project team section
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  the user clicks the button/link       link = Project team
    Given the user completes the project team section
    Then the user should see the element           jQuery = .progress-list li:nth-child(2):contains("Completed")

The user is able to complete the Documents section
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user clicks the button/link     link = Documents
    When the user uploads the exploitation plan
    And the user uploads the Test document type
    And the user clicks the button/link       link = Set up your project
    Then the user should see the element      jQuery = .progress-list li:nth-child(3):contains("Awaiting review")

The user is able to complete the Bank details section
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user enters bank details
    When the user clicks the button/link      link = Set up your project
    Then the user should see the element      jQuery = .progress-list li:nth-child(5):contains("Awaiting review")

Internal user is able to approve documents
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  log in as a different user         &{Comp_admin1_credentials}
    Given Internal user is able to approve documents
    When the user navigates to the page        ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element       css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(3)

Internal user is able to assign an MO
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  the user navigates to the page        ${server}/project-setup-management/project/${ProjectID}/monitoring-officer
    Given Search for MO                            Orvill  Orville Gibbs
    When The internal user assign project to MO    ${ApplicationID}  ${KTPapplicationTitle}
    And the user navigates to the page             ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element           css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(4)

Finance user approves bank details
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  log in as a different user                      &{internal_finance_credentials}
    Given the project finance user approves bank details     ${ProjectID}
    When the user navigates to the page                      ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element                     css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(5)

Internal user is able to approve Finance checks and generate spend profile
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  the user navigates to the page        ${server}/project-setup-management/project/${ProjectID}/finance-check
    Given the user approves Eligibility and Viability
    When the user navigates to the page            ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element           css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(6)
    And the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(7)

User is able to submit the spend profile
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  log in as a different user      &{lead_applicant_credentials}
    Given the user navigates to the page     ${server}/project-setup/project/${ProjectID}/partner-organisation/${EMPIRE_LTD_ID}/spend-profile/review
    When the user submits the spend profile
    Then the user should see the element     jQUery = .progress-list li:nth-child(7):contains("Awaiting review")

Internal user is able to approve Spend profile and generates the GOL
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given proj finance approves the spend profiles  ${ProjectID}
    Then the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(7)
    And internal user generates the GOL             NO  ${ProjectID}

Applicant is able to upload the GOL
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given log in as a different user         &{lead_applicant_credentials}
    When Applicant uploads the GOL           ${ProjectID}
    Then the user should see the element     jQUery = .progress-list li:nth-child(8):contains("Awaiting review")

Internal user is able to approve the GOL and the project is now Live
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the internal user approve the GOL                                    ${ProjectID}
    When log in as a different user                                            &{lead_applicant_credentials}
    And the user navigates to the page                                         ${server}/project-setup/project/${ProjectID}
    Then the user should see project is live with review its progress link

*** Keywords ***
The user approves Eligibility and Viability
    the user clicks the button/link      jQuery = table.table-progress a.viability-0
    the user selects the checkbox        costs-reviewed
    the user selects the checkbox        project-viable
    Set Focus To Element                 link = Contact us
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link      css = #confirm-button
    the user clicks the button/link      jQuery = .modal-confirm-viability .govuk-button:contains("Confirm viability")
    the user clicks the button/link      link = Return to finance checks
    the user clicks the button/link      jQuery = table.table-progress a.eligibility-0
    the user approves project costs
    the user clicks the button/link      link = Return to finance checks
    the user should see the element      jQuery = table.table-progress a.eligibility-0:contains("Approved")
    the user clicks the button/link      link = Generate spend profile
    the user clicks the button/link      css = #generate-spend-profile-modal-button
    the user should see the element      jQuery = .success-alert p:contains("The finance checks have been approved and profiles generated.")

Internal user is able to approve documents
    the user navigates to the page         ${server}/project-setup-management/project/${ProjectID}/document/all
    the user clicks the button/link        link = Exploitation plan
    internal user approve uploaded documents
    the user clicks the button/link        link = Return to documents
    the user clicks the button/link        link = Test document type
    internal user approve uploaded documents

The user completes the application
    the user clicks the button/link                          link = Application details
    the user fills in the Application details                ${KTPapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant completes Application Team
    the applicant marks EDI question as complete
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                 ${KTPapplicationTitle}
    the user marks the finances as complete                  ${KTPapplicationTitle}   Calculate  52,214  yes
    the user accept the competition terms and conditions     Return to application overview

The user completes the research category
    [Arguments]  ${res_category}
    the user clicks the button/link      link=Research category
    the user selects the checkbox        researchCategory
    the user clicks the button/link      jQuery=label:contains("${res_category}")
    the user clicks the button/link      id=application-question-complete
    the user should see the element      jQuery=li:contains("Research category") > .task-status-complete

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Requesting IDs of this Project
    ${ProjectID} =  get project id by name    ${KTPapplicationTitle}
    Set suite variable    ${ProjectID}
    ${ApplicationID} =  get application id by name    ${KTPapplicationTitle}
    Set suite variable    ${ApplicationID}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database