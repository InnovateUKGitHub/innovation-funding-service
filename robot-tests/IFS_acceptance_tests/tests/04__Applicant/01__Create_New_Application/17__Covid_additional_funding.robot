*** Settings ***
Documentation     IFS-7365 DocuSign Integration
...
...               IFS-7460 Enter funding sought, not funding level
...
...               IFS-7357 Allowing external users to complete viability & eligibility checks
...
...               IFS-7441 Allow a competition to remain open whilst applicants proceed through PS
...
...               IFS-7452 COVID-19 continuity awards - project costs
...
...               IFS-7440 Allow applicants to edit a submitted application
...
...               IFS-7552 Provide External Finance user with access to download appendix
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${COVIDcompetitionTitle}   596 Covid grants framework group
${COVIDcompetitionId}      ${competition_ids['${COVIDcompetitionTitle}']}
${COVIDapplicationTitle1}  Covid Application
${COVIDapplicationTitle2}  Covid Application2
${exfinanceemail}          exfinance2@example.com
${project_team_question}   8. Project team

*** Test Cases ***
IFS admin is able to invite an external PF
    [Documentation]  IFS-7357
    [Setup]  Log in as a different user  &{ifs_admin_user_credentials}
    Given the user navigates to the page        ${server}/management/competition/setup/${COVIDcompetitionId}
    When the user clicks the button/link        link = External finance reviewers
    And the user clicks the button/link         jQuery = span:contains("Invite a new external finance reviewer")
    Then complete external project finance details
    [Teardown]    logout as user

New external project finance can create account
    [Documentation]  IFS-7357
    Given the user reads his email and clicks the link   ${exfinanceemail}  Invitation to review an Innovation Funding Service competition  You have been invited  1
    When external project finance creates account
    Then The user should not see the element             link = ${COVIDcompetitionTitle}

Create application to covid comp
    [Documentation]  IFS-7441
    [Setup]  log in as a different user    &{lead_applicant_credentials}
    Given the user navigates to the page   ${server}/competition/${COVIDcompetitionId}/overview
    Then the user completes covid application

Applicant is asked for funding sought
    [Documentation]  IFS-7460
    Given the user clicks the button/link        link = Your funding
    And the user enters text to a text field     id = amount   21
    When the user selects the radio button       otherFunding  false
    And the user clicks the button/link          id = mark-all-as-complete
    Then the user should see all finance subsections complete
    [Teardown]  the user clicks the button/link  link = Back to application overview

Submit application
    [Documentation]  IFS-7440
    Given the user can submit the application
    Then the user should see the element          link = Reopen application

Non lead cannot reopen competition
    [Documentation]  IFS-7440
    Given log in as a different user           collaborator@example.com  ${correct_password}
    When the user should see the element       link = ${COVIDapplicationTitle1}
    Then the user should not see the element   jQuery = li:contains("${COVIDapplicationTitle1}") a:contains("Reopen")

Lead can reopen application
   [Documentation]  IFS-7440
   [Setup]  log in as a different user   &{lead_applicant_credentials}
   Given the user clicks the button/link  link = Dashboard
   When the user clicks the button/link   jQuery = li:contains("${COVIDapplicationTitle1}") a:contains("Reopen")
   And the user clicks the button/link    css = input[type="submit"]
   Then the user should see the element   jQuery = .message-alert:contains("Now your application is complete")
   And the user reads his email           collaborator@example.com     	An Innovation Funding Service funding application has been reopened   The application was reopened by
   And the user reads his email           steve.smith@empire.com           An Innovation Funding Service funding application has been reopened   You reopened this application

Lead can make changes and resubmit
    [Documentation]  IFS-7440  IFS-7552
    When the user uploads an appendix             ${project_team_question}  ${5mb_pdf}
    Then the user can submit the application

Internal user cannot invite to assesment
    [Documentation]  IFS-7441
    Given Log in as a different user       &{Comp_admin1_credentials}
    When The user clicks the button/link   link = ${COVIDcompetitionTitle}
    Then The user should see the element   jQuery = .disabled:contains("Invite assessors to assess the competition")
    And The user should see the element    jQuery = .disabled:contains("Manage assessments")

Internal user can send funding notification
    [Documentation]  IFS-7441
    [Setup]  get application id by name and set as suite variable   ${COVIDapplicationTitle1}
    Given the user clicks the button/link    link = Input and review funding decision
    Then the user can send successful funding notification

Applicant can no longer reopen the competition
    [Documentation]  IFS-7440
    Given Log in as a different user           &{lead_applicant_credentials}
    When The user should see the element       link = ${COVIDapplicationTitle1}
    Then the user should not see the element   jQuery = li:contains("${COVIDapplicationTitle1}") a:contains("Reopen")

Competition is in Live and PS tabs
    [Documentation]   IFS-7441
    [Setup]  log in as a different user     &{Comp_admin1_credentials}
    Given the user clicks the button/link   jQuery = a:contains("Live (")
    And the user should see the element     link = ${COVIDcompetitionTitle}
    when the user clicks the button/link    jQuery = a:contains("Project setup (")
    Then the user should see the element    link = ${COVIDcompetitionTitle}

External project finance can see Project details
    [Documentation]  IFS-7357
    [Setup]   Complete all PS steps before finance
    Log in as a different user               ${exfinanceemail}   ${short_password}
    Given the user clicks the button/link    link = Dashboard
    And The user clicks the button/link      link = ${COVIDcompetitionTitle}
    When the user clicks the button/link     jQuery = td.ok:nth-of-type(1)
    Then the user should see the element     jQuery = h1:contains("Project details")
    [Teardown]  the user clicks the button/link  link = Back to project setup

External project finance can see Project team
    [Documentation]  IFS-7357
    Given the user clicks the button/link     jQuery = td.ok:nth-of-type(2)
    Then the user should see the element      jQuery = h1:contains("Project team")
    [Teardown]  the user clicks the button/link  link = Back to project setup

External project finance can see the application finances
    [Documentation]  IFS-7357
    Given the user clicks the button/link    link = ${application_id}
    And the user clicks the button/link      id = accordion-questions-heading-3-1
    When the user clicks the button/link     jQuery = tr:contains("Empire") a:contains("View finances")
    And the user clicks the button/link      link = Your project costs
    Then the user should see the element     jQuery = h1:contains("Your project costs")

External finance can access appendix
    [Documentation]  IFS-7552
    Given the user navigates to the page   ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    When the user clicks the button/link   link = ${application_id}
    Then open pdf link                     link = ${5mb_pdf}, 4 MB (opens in a new window)

External project finance cannot access documents or MO
    [Documentation]  IFS-7357
    Given the user clicks the button/link     link = Dashboard
    When the user clicks the button/link      link = ${COVIDcompetitionTitle}
    Then the user should not have access to documents MO or bank details
    [Teardown]  the project finance approves all steps before finance

External project finance can raise a query
    [Documentation]  IFS-7357
    [Setup]    log in as a different user      ${exfinanceemail}  ${short_password}
    Given the user clicks the button/link      link = ${COVIDcompetitionTitle}
    And the user clicks the button/link        jQuery = td.ok + td.ok + td.ok + td.ok + td.ok + td.action a
    When the user raises a query
    Then the user should see the element       jQuery = button:contains("a viability query's title")
    [Teardown]  the user clicks the button/link   link = Finance checks

External project finance can raise a note
    [Documentation]  IFS-7357
    Given the user clicks the button/link   css = .eligibility-0
    When the user raises a note
    Then the user should see the element    jQuery = h2:contains("an eligibility note's title")

External project finance can approve viabilty
    [Documentation]  IFS-7357
    Given the user navigates to the page  ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    When the user clicks the button/link   jQuery = td.ok + td.ok + td.ok + td.ok + td.ok + td.action a
    Then confirm viability    0
    And confirm eligibility  0

External project finance can generate spend profile
    [Documentation]  IFS-7357
    Given the user clicks the button/link    css = .generate-spend-profile-main-button
    And the user clicks the button/link    css = #generate-spend-profile-modal-button
    Then the internal user can complete PS

Internal user is able to approve Spend profile and generates the GOL
    [Documentation]  IFS-7365
    [Setup]  Requesting Project ID of this Project
    Given proj finance approves the spend profiles  ${ProjectID}
    Then the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(7)
    And check activity log
    And internal user generates the GOL             YES  ${ProjectID}

Applicant is able to upload the GOL
    [Documentation]  IFS-7365
    Given log in as a different user               &{lead_applicant_credentials}
    When applicant uploads the GOL using Docusign  ${ProjectID}  ${tomorrowday}/${nextmonth}/${nextyear}

Internal user is able to reject the GOL and applicant can re-upload
    [Documentation]  IFS-7365
    Given the internal user rejects the GOL             ${ProjectID}
    When log in as a different user                     &{lead_applicant_credentials}
    Then the applicant is able to see the rejected GOL  ${ProjectID}
    And applicant uploads the GOL using Docusign        ${ProjectID}  ${tomorrowday}/${nextmonth}/${nextyear}

Internal user is able to approve the GOL and the project is now Live
    [Documentation]  IFS-7365
    Given the internal user approve the GOL                                    ${ProjectID}
    When log in as a different user                                            &{lead_applicant_credentials}
    And the user navigates to the page                                         ${server}/project-setup/project/${ProjectID}
    Then the user should see project is live with review its progress link

Competition goes into previous
    [Documentation]   IFS-7441
    [Setup]  log in as a different user  &{Comp_admin1_credentials}
    Given the user clicks the button/link    jQuery = a:contains("Project setup (")
    And The user should not see the element  link = ${COVIDcompetitionTitle}
    when the user clicks the button/link     jQuery = a:contains("Previous (")
    Then the user should see the element     link = ${COVIDcompetitionTitle}

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser   &{lead_applicant_credentials}
    Set predefined date variables
    Connect to database  @{database}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the applicant adds contributor to Application Team
    the user clicks the button/link  link = Application team
    then the user clicks the button/link  jQuery = button:contains("Add person to Empire Ltd")
    the user invites a person to the same organisation   Collaborator  collaborator@example.com
    logout as user
    When the user reads his email and clicks the link      collaborator@example.com    Invitation to contribute in ${COVIDcompetitionTitle}     You will be joining as part of the organisation    2
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Yes, accept invitation")
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Confirm and continue")
    And the invited user fills the create account form     Collaborator  Axe
    And the user reads his email and clicks the link       collaborator@example.com    Please verify your email address    Once verified you can sign into your account
    the user clicks the button/link       jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    Logging in and Error Checking           &{lead_applicant_credentials}
    then the user clicks the button/link    link = ${COVIDapplicationTitle1}
    the applicant completes Application Team

the user fills in bank details
    the user clicks the button/link                      link = Set up your project
    the user clicks the button/link                      link = Bank details
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    applicant user enters bank details

the project finance approves all steps before finance
    log in as a different user                   &{ifs_admin_user_credentials}
    the user navigates to the page               ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(3) a
    the user clicks the button/link              link = Exploitation plan
    internal user approve uploaded documents
    the user navigates to the page               ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(4)
    search for MO                                Orvill  Orville Gibbs
    And the internal user assign project to MO   ${application_id}  ${COVIDapplicationTitle1}
    the user navigates to the page               ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(5)
    approve bank account details

confirm viability
    [Arguments]  ${viability}
    the user clicks the button/link   css = .viability-${viability}
    the user selects the checkbox                        project-viable
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link                      id = confirm-button      #Page confirmation button
    the user clicks the button/link                      name = confirm-viability   #Pop-up confirmation button
    the user clicks the button/link                      link = Return to finance checks

confirm eligibility
    [Arguments]  ${eligibility}
    the user clicks the button/link                     css = .eligibility-${eligibility}
    the user selects the checkbox                        project-eligible
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link                      css = #confirm-button        #Page confirmation button
    the user clicks the button/link                      name = confirm-eligibility   #Pop-up confirmation button
    the user clicks the button/link                      link = Return to finance checks

approve bank account details
    the user clicks the button/link    jQuery = button:contains("Approve bank account details")
    the user clicks the button/link    jQuery = button:contains("Approve account")
    the user should see the element    jQuery = h2:contains("The bank details provided have been approved.")
    the user clicks the button/link    link = Back to project setup

check activity log
    the user navigates to the page     ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    the user clicks the button/link    link = View activity log
    the user clicks the button/link    jQuery = li:contains("Application reopened") a:contains("View application overview")
    the user should see the element    jQuery = h1:contains("Application overview")

the external finance cannot access spend profile
    log in as a different user            ${exfinanceemail}  ${short_password}
    the user navigates to the page        ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    the user should see the element       jQuery = td.action:nth-of-type(7)
    the user should not see the element   jQuery = td.action:nth-of-type(7) a

Complete external project finance details
    the user enters text to a text field    id = firstName     External
    the user enters text to a text field    id = lastName      Finance
    the user enters text to a text field    id = emailAddress  ${exfinanceemail}
    the user clicks the button/link         css = button[name = "inviteFinanceUser"]

External project finance creates account
    the user clicks the button/link          jQuery = .govuk-button:contains("Create account")
    the user enters text to a text field     id = firstName  External
    the user enters text to a text field     id = lastName  Finance
    the user enters text to a text field     id = password  ${short_password}
    the user clicks the button/link          jQuery = .govuk-button:contains("Create account")
    the user clicks the button/link          link = Sign into your account
    Logging in and Error Checking            ${exfinanceemail}   ${short_password}

the user completes covid application
    the user clicks the button/link                          jQuery = a:contains("Start new application")
    the user clicks the button/link                          jQuery = button:contains("Save and continue")
    the user clicks the button/link                          link = Application details
    the user fills in the Application details                ${COVIDapplicationTitle1}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant marks EDI question as complete
    the applicant adds contributor to Application Team
    the user selects research category                       Feasibility studies
    the lead applicant fills all the questions and marks as complete(programme)
    the user accept the competition terms and conditions     Return to application overview
    the user navigates to Your-finances page                 ${COVIDapplicationTitle1}
    the user clicks the button/link                          link = Your project costs
    the user should see the element                          jQuery = .message-alert:contains("Covid-19 pandemic")
    the user fills in Other costs
    the user clicks the button/link                          css = label[for="stateAidAgreed"]
    the user clicks the button/link                          jQuery = button:contains("Mark as complete")
    the user enters the project location
    the user fills in the organisation information           ${COVIDapplicationTitle1}  ${SMALL_ORGANISATION_SIZE}

the user can send successful funding notification
    the user selects the checkbox      app-row-1
    the user clicks the button/link    jQuery = button:contains("Successful")
    the user clicks the button/link    link = Competition
    the user clicks the button/link    link = Manage funding notifications
    the user selects the checkbox      app-row-${application_id}
    the user clicks the button/link    id = write-and-send-email
    the user clicks the button/link    jQuery = button:contains("Send email")[data-js-modal = "send-to-all-applicants-modal"]
    the user clicks the button/link    jQuery = .send-to-all-applicants-modal button:contains("Send email")

Complete all PS steps before finance
    Log in as a different user           &{lead_applicant_credentials}
    the user clicks the button/link    link = ${COVIDapplicationTitle1}
    the user clicks the button/link    link = Project details
    the user clicks the button/link    link = Correspondence address
    the user enter the Correspondence address
    the user clicks the button/link    id = return-to-set-up-your-project-button
    the user clicks the button/link    link = Project team
    the user clicks the button/link    link = Project manager
    the user selects the radio button  projectManager    projectManager1
    the user clicks the button/link    id = save-project-manager-button
    The user selects their finance contact  financeContact1
    the user clicks the button/link    link = Set up your project
    the user clicks the button/link      link = Documents
    the user clicks the button/link        link = Exploitation plan
    the user uploads the file              css = .inputfile  ${valid_pdf}
    the user should see the element        jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm
    the user clicks the button/link    link = Back to document overview
    the user fills in bank details

the user should not have access to documents MO or bank details
    the user should not see the element  jQuery = td.action:nth-of-type(3) a
    the user should not see the element   jQuery = td.action:nth-of-type(4) a
    the user should not see the element   jQuery = td.action:nth-of-type(5) a

the user raises a query
    the user clicks the button/link                        jQuery = tr:contains("Empire") td:contains("View"):nth-of-type(5)
    the user clicks the button/link                        css = a[id = "post-new-query"]
    the user enters text to a text field                   id = queryTitle  a viability query's title
    the user selects the option from the drop-down menu    Viability    id = section
    the user enters text to a text field                   css = .editor    another query body
    the user clicks the button/link                        css = .govuk-grid-column-one-half button[type = "submit"]  # Post query

the user raises a note
    the user clicks the button/link         jQuery = a:contains("Notes")
    the user should see the element         jQuery = h2:contains("Review notes")
    the user clicks the button/link         jQuery = .govuk-button:contains("Create a new note")
    the user enters text to a text field    id = noteTitle    an eligibility note's title
    the user enters text to a text field    css = .editor    this is some note text
    the user clicks the button/link         jQuery = .govuk-button:contains("Save note")

the internal user can complete PS
    log in as a different user         &{lead_applicant_credentials}
    the user clicks the button/link    link = ${COVIDapplicationTitle1}
    the user clicks the button/link    link = Spend profile
    the user clicks the button/link    link = Empire Ltd
    the user clicks the button/link    id = spend-profile-mark-as-complete-button
    the user clicks the button/link    jQuery = a:contains("Review and submit project spend profile")
    the user clicks the button/link    jQuery = a:contains("Submit project spend profile")
    the user clicks the button/link    id = submit-send-all-spend-profiles
    the external finance cannot access spend profile
    log in as a different user         &{ifs_admin_user_credentials}

Requesting Project ID of this Project
    ${ProjectID} =  get project id by name   ${COVIDapplicationTitle1}
    Set suite variable    ${ProjectID}