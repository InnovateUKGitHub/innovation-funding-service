*** Settings ***
Documentation  IFS-5158 - Competition Template
...
...            IFS-5247 - Application details page
...
...            IFS-5700 - Create new project team page to manage roles in project setup
...
...            IFS-7195  Organisational eligibility category in Competition setup
Suite Setup       Custom Suite Setup
Suite Teardown    Custom Suite Teardown
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${CA_UpcomingComp}           ${server}/management/dashboard/upcoming
${competitionTitle}          H2020 Grant Transfer
${H2020_Project_Name}        Project name
${externalUsrProjectPage}    ${server}/project-setup/project/${HProjectID}

*** Test Cases ***
User can select H2020 Competition Template and complete Initial details
    [Documentation]  IFS-5158
    Given a user starts a new competition
    When the user clicks the button/link                               link = Initial details
    Then the user selects the option from the drop-down menu           Horizon 2020   name = competitionTypeId
    And the user is able to complete Initial details section
    And the user should see the read-only view of the initial details
    [Teardown]  the user clicks the button/link                        link = Return to setup overview

User can populate the Completion Stage, Milestones and Public content
    [Documentation]  IFS-5158
     Given the user fills in the CS Milestones                                      project-setup-completion-stage  1  ${nextyear}
     When the user clicks the button/link                                           link = Public content
     Then The user completes Public content for H2020 registration and publishes
     [Teardown]  the user clicks the button/link                                    link = Return to setup overview

User can populate Terms and Conditions
    [Documentation]  IFS-5158
    Given the user clicks the button/link                   link = Terms and conditions
    Then the user clicks the button/link                    jQuery = button:contains("Done")
    [Teardown]  the user clicks the button/link             link = Return to setup overview

User can populate Funding information and Project eligibility
    [Documentation]  IFS-5158
    Given the user clicks the button/link                                 link = Funding information
    When the user completes funding information
    Then the user clicks the button/link                                  link = Return to setup overview
    And the user fills in the Competition Setup Project eligibility section       ${BUSINESS_TYPE_ID}  4

User can complete the Application
    [Documentation]  IFS-5158
    Given the user clicks the button/link                      link = Application
    When the user completes the application proccess details
    Then the user clicks the button/link                       link = Return to setup overview

User can complete Organisational eligibility
    [Documentation]     IFS-7195 IFS-7246
    [Tags]  HappyPath
    Given the user clicks the button/link                     link = ${organisationalEligibilityTitle}
    When the user selects the radio button                    internationalOrganisationsApplicable       false
    And the user clicks the button/link                       jQuery = button:contains("Save and continue")
    And the user clicks the button/link                       link = Competition details
    Then the user should see the element                      jQuery = li:contains("Organisational eligibility") .task-status-complete

User can finish setting up the grant transfer
    [Documentation]  IFS-5158
    Given the user completes grant transfer setup
    Then the user should see the element                           jQuery = h2:contains("Ready to open") ~ ul a:contains("${competitionTitle}")
    [Teardown]  Get competition id and set open date to yesterday  ${competitionTitle}

Applicant user can complete an H2020 grant transfer
    [Documentation]  IFS-5158
    [Setup]  log in as a different user                   &{collaborator1_credentials}
    Given the user starts an H2020 applcation
    When the user is able to complete Horizon 2020 Grant transfer application
    Then the user reads his email                         ${collaborator1_credentials["email"]}   Submitted application for your Horizon 2020 grant transfer of Project name   You have submitted your application to transfer your Horizon 2020 grant funding to UK Research and Innovation.

Application validation checks
    Given the user starts an H2020 applcation
    Then the user is able to verify validation on each page

An internal user is able to progress an H2020 grant transfer to project set up
    [Documentation]  IFS-5700  IFS-6629
    [Setup]  log in as a different user   &{Comp_admin1_credentials}
    Given the internal user is able to progress an application to project set up
    And the user is able to filter on status
    Then the user is able to see that the application is now in project setup
    [Teardown]  get project id

The user is able to complete Project details section
    [Documentation]  IFS-5700
    [Setup]  the user logs-in in new browser     &{collaborator1_credentials}
    Given the user navigates to the page         ${server}/project-setup/project/${HProjectID}
    When the user is able to complete project details section
    Then the user should see the element         css = ul li.complete:nth-child(1)

The user is able to complete Project team section
    [Documentation]  IFS-5700
    [Setup]  the user clicks the button/link       link = Project team
    Given the user completes the project team section
    Then the user should see the element          jQuery = .progress-list li:nth-child(2):contains("Completed")

The user is able to complete the Bank details section
    [Documentation]  IFS-5700
    Given the user enters bank details
    When the user clicks the button/link      link = Set up your project
    Then the user should see the element      jQuery = .progress-list li:nth-child(5):contains("Awaiting review")

The user is able to complete the Documents section
    [Documentation]  IFS-5700
    Given the user clicks the button/link     link = Documents
    When the user uploads the exploitation plan
    And the user clicks the button/link       link = Set up your project
    Then the user should see the element      jQuery = .progress-list li:nth-child(3):contains("Awaiting review")

Internal user is able to approve documents
    [Documentation]  IFS-5700
    [Setup]  log in as a different user         &{Comp_admin1_credentials}
    Given Internal user is able to approve documents
    When the user navigates to the page        ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element       css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(3)

Internal user is able to assign an MO
    [Documentation]  IFS-5700
    [Setup]  the user navigates to the page        ${server}/project-setup-management/project/${HProjectID}/monitoring-officer
    Given Search for MO                            Orvill  Orville Gibbs
    When The internal user assign project to MO    ${HApplicationID}  ${H2020_Project_Name}
    And the user navigates to the page             ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element           css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(4)

Finance user approves bank details
    [Setup]  log in as a different user                      &{internal_finance_credentials}
    Given the project finance user approves bank details     ${HProjectID}
    When the user navigates to the page                      ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element                     css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(5)

Internal user is able to approve Finance checks and generate spend profile
    [Documentation]  IFS-5700
    [Setup]  the user navigates to the page        ${server}/project-setup-management/project/${HProjectID}/finance-check
    Given the user approves h2020 finance checks
    When the user navigates to the page           ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element           css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(6)
    And the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(7)

User is able to submit the spend profile
    [Documentation]  IFS-5700
    [Setup]  log in as a different user      &{collaborator1_credentials}
    Given the user navigates to the page     ${server}/project-setup/project/${HProjectID}/partner-organisation/${organisationLudlowId}/spend-profile/review  
    When the user submits the spend profile
    Then the user should see the element     jQUery = .progress-list li:nth-child(7):contains("Awaiting review")

Internal user is able to approve Spend profile and generates the GOL
    [Documentation]  IFS-5700
    Given proj finance approves the spend profiles  ${HProjectID}
    Then the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(7)
    And internal user generates the GOL             NO  ${HProjectID}

Applicant is able to upload the GOL
    [Documentation]  IFS-5700
    Given log in as a different user         &{collaborator1_credentials}
    When Applicant uploads the GOL           ${HProjectID}
    Then the user should see the element     jQUery = .progress-list li:nth-child(8):contains("Awaiting review")

Internal user is able to approve the GOL and the project is now Live
    [Documentation]  IFS-5700
    Given the internal user approve the GOL                                           ${HProjectID}
    When log in as a different user                                                   &{collaborator1_credentials}
    And the user navigates to the page                                                ${server}/project-setup/project/${HProjectID}
    Then the user checks for review its progress link with project is live message

*** Keywords ***
The user approves h2020 finance checks
    the user should see the element     jQuery = table.table-progress span.viability-0:contains("Auto approved")
    the user clicks the button/link     jQuery = table.table-progress a.eligibility-0
    the user approves project costs
    the user clicks the button/link     link = Return to finance checks
    the user should see the element     jQuery = table.table-progress a.eligibility-0:contains("Approved")
    the user clicks the button/link     link = Generate spend profile
    the user clicks the button/link     css = #generate-spend-profile-modal-button
    the user should see the element     jQuery = .success-alert p:contains("The finance checks have been approved and profiles generated.")

Internal user is able to approve documents
    the user navigates to the page         ${server}/project-setup-management/project/${HProjectID}/document/all
    the user clicks the button/link        link = Exploitation plan
    internal user approve uploaded documents

Get project id
    ${HProjectID} =  get project id by name            ${H2020_Project_Name}
    ${HApplicationID} =  get application id by name    ${H2020_Project_Name}
    get competitions id and set it as suite variable   ${competitionTitle}
    Set suite variable           ${HProjectID}
    Set suite variable           ${HApplicationID}

Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    ${month} =  get next month
    Set suite variable  ${month}
    ${lastYear} =  get last year
    Set suite variable  ${lastYear}
    Connect to database  @{database}

The user is able to see that the application is now in project setup
    the user clicks the button/link   jQuery = a:contains("Project setup")
    the user should see the element   link = H2020 Grant Transfer

The internal user is able to progress an application to project set up
    the user clicks the button/link       link = H2020 Grant Transfer
    the user should see the element       jQuery = h1:contains("Open")
    the user clicks the button/link       link = Input and review funding decision
    the user selects the checkbox         app-row-1
    the user clicks the button/link       jQuery = button:contains("Successful")
    the user clicks the button/link       link = Competition
    the user clicks the button/link       jQuery = a:contains("Manage funding notifications")
    ${id} =  get application id by name   Project name
    Set suite variable  ${id}
    the user selects the checkbox         app-row-${id}
    the user clicks the button/link       jQuery = button:contains("Write and send email")
    the user clicks the button/link       css = button[data-js-modal="send-to-all-applicants-modal"]
    the user clicks the button/link       jQuery = .send-to-all-applicants-modal button:contains("Send email to all applicants")
    the user clicks the button/link       link = Competition
    the user clicks the button/link       link = Manage funding notifications

The user starts an H2020 applcation
   the user navigates to the page                  ${server}/competition/${competitionId}/overview
   the user clicks the button/link                  jQuery = a:contains("Start new application")
   check if there is an existing application in progress for this competition
   the user clicks the button/link                  jQuery=.govuk-button:contains("Save and continue")
   the user should see the element                  jQuery = h1:contains("Application overview")

A user starts a new competition
    the user navigates to the page        ${CA_UpcomingComp}
    the user clicks the button/link       jQuery = .govuk-button:contains("Create competition")

The user is able to complete Initial details section
    the user enters text to a text field                            css = #title  ${competitionTitle}
    the user selects the radio button                               fundingType  GRANT
    the user selects the option from the drop-down menu             None  id = innovationSectorCategoryId
    the user selects the value from the drop-down menu              67  name = innovationAreaCategoryIds[0]
    the user enters text to a text field                            id = openingDateDay    10
    the user enters text to a text field                            id = openingDateMonth    1
    the user enters text to a text field                            id = openingDateYear     ${nextyear}
    the user selects the option from the drop-down menu             Ian Cooper    id = innovationLeadUserId
    the user selects the option from the drop-down menu             John Doe   id = executiveUserId
    the user clicks the button twice                                css = label[for = "stateAid2"]
    the user clicks the button/link                                 jQuery = button:contains("Done")
    the user should see the read-only view of the initial details

The user should see the read-only view of the initial details
    the user should see the element    jQuery = dd:contains("H2020 Grant Transfer")
    the user should see the element    jQuery = dt:contains("Funding type") ~ dd:contains("Grant")
    the user should see the element    jQuery = dd:contains("None")
    the user should see the element    jQuery = dd:contains("None")
    the user should see the element    jQuery = dd:contains("10 January ${nextyear}")
    the user should see the element    jQuery = dd:contains("Ian Cooper")
    the user should see the element    jQuery = dd:contains("John Doe")
    the user should see the element    jQuery = dt:contains("State aid") ~ dd:contains("No")

The user completes funding information
    the user clicks the button/link         id = generate-code
    the user enters text to an autocomplete field  id = funders[0].funder   Aerospace Technology Institute (ATI)
    the user clicks the button/link         id = funders[0].funder
    click element                           id = funders[0].funder__option--0
    the user enters text to a text field    id = funders[0].funderBudget    20000
    the user enters text to a text field    id = pafNumber    2016
    the user enters text to a text field    id = budgetCode    2004
    the user enters text to a text field    id = activityCode    4242
    the user clicks the button/link         jQuery = button:contains("Done")
    the user should see the element         jQuery = td:contains(" Aerospace Technology Institute")

The user completes Public content for H2020 registration and publishes
    the user fills in the public content competition inforation and search
    the user fills in the public content summary
    the user fills in public content eligibility
    the user fills in public content scope
    the user fills in public content save the dates
    the user fills in public content how to apply section
    the user fills in public content supporting information section
    the user clicks the button/link         jQuery = button:contains("Publish content")

The user fills in the public content competition inforation and search
    the user clicks the button/link         link = Competition information and search
    the user enters text to a text field    id = shortDescription  Horizon 2020 competition
    the user enters text to a text field    id = projectFundingRange  Up to £5million
    the user enters text to a text field    css = [aria-labelledby = "eligibilitySummary-label"]  Summary of eligiblity
    the user selects the radio button       publishSetting  invite
    the user enters text to a text field    id = keywords  Search, Testing, Robot
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Competition information and search") ~ .task-status-complete

The user fills in the public content summary
    the user clicks the button/link         link = Summary
    the user enters text to a text field    css = .editor  This is a Summary description
    the user enters text to a text field    id = projectSize   10 millions
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Summary") ~ .task-status-complete

The user fills in public content eligibility
    the user clicks the button/link          link = Eligibility
    the user enters text to a text field     id = contentGroups[0].heading  Heading 1
    the user enters text to a text field     jQuery = div.editor:first-of-type  Content 1
    the user clicks the button/link          jQuery = button:contains("Save and review")
    the user clicks the button/link          link = Return to public content
    the user should see the element          jQuery = div:contains("Eligibility") ~ .task-status-complete

The user fills in public content scope
    the user clicks the button/link         link = Scope
    the user enters text to a text field    id = contentGroups[0].heading  Heading 1
    the user enters text to a text field    jQuery = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Scope") ~ .task-status-complete

The user fills in public content save the dates
    the user clicks the button/link         link = Dates
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Dates") ~ .task-status-complete

The user fills in public content how to apply section
    the user clicks the button/link         link = How to apply
    the user enters text to a text field    id = contentGroups[0].heading    Heading 1
    the user enters text to a text field    css = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("How to apply") ~ .task-status-complete

The user fills in public content supporting information section
    the user clicks the button/link         link = Supporting information
    the user enters text to a text field    id = contentGroups[0].heading    Heading 1
    the user enters text to a text field    css = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Supporting information") ~ .task-status-complete

The user completes the application proccess details
    the user clicks the button/link         link = Application details
    the user clicks the button/link         jQuery = button:contains("Done")
    the user clicks the button/link         link = Public description
    the user clicks the button/link         jQuery = button:contains("Done")
    the user clicks the button/link         link = Finances
    the user selects the radio button       applicationFinanceType  STANDARD
    the user clicks the button twice        css = label[for = "include-growth-table-no"]
    the user selects the radio button       includeJesForm  false
    the user selects the radio button       includeYourOrganisationSection  false
    the user enters text to a text field    css = .editor  Those are the rules that apply to Finances
    the user clicks the button/link         jQuery = button:contains("Done")
    the user clicks the button/link         jQuery = button:contains("Done")

The user completes grant transfer setup
    the user clicks the button/link         jQuery = a:contains("Complete")
    the user clicks the button/link         css = button[type="submit"]
    the user navigates to the page          ${CA_UpcomingComp}

The user is able to go to Application overview
    the user clicks the button/link  jQuery = .govuk-button:contains("Save and continue")
    the user should see the element  link = Application details
    the user should see the element  link = Application team
    the user should see the element  link = Public description
    the user should see the element  link = Horizon 2020 grant agreement

The user fills in the Competition Setup Project eligibility section
    [Arguments]  ${organisationType}  ${researchParticipation}
    the user clicks the button/link                      link = Project eligibility
    the user clicks the button twice                     css = label[for="single-or-collaborative-single"]
    the user selects the radio button                    researchCategoriesApplicable    false
    the user selects the option from the drop-down menu  100%  fundingLevelPercentage
    the user clicks the button twice                     css = label[for="lead-applicant-type-${organisationType}"]
    the user selects the option from the drop-down menu  None     researchParticipation
    the user clicks the button/link                      css = label[for="comp-resubmissions-no"]
    the user clicks the button/link                      css = label[for="comp-resubmissions-no"]
    the user clicks the button/link                      jQuery = button:contains("Done")
    the user clicks the button/link                      link = Competition details
    the user should see the element                      jQuery = div:contains("Project eligibility") ~ .task-status-complete

The user is able to complete Horizon 2020 Grant transfer application
    the user is able to complete Application details section  Project name  ${month}  ${nextyear}  ${lastYear}
    the applicant completes Application Team
    the user is able to complete Public description section
    the user is able to complete Horizon 2020 grant agreement section
    the user is able to complete finance details section
    the user accept the competition terms and conditions      Return to application overview
    the user is able to submit the application

The user is able to complete Application details section
    [Arguments]  ${projectName}  ${month}  ${nextyear}  ${lastYear}
    the user clicks the button/link                      jQuery = a:contains("Application details")
    the user should see the element                      jQuery = h1:contains("Application details")
    the user enters text to a text field                 id = projectName   ${projectName}
    the user enters text to a text field                 id = startDateMonth  ${month}
    the user enters text to a text field                 id = startDateYear  ${lastYear}
    the user enters text to a text field                 id = endDateMonth  ${month}
    the user enters text to a text field                 id = endDateYear  ${nextyear}
    the user enters text to a text field                 id = grantAgreementNumber            123456
    the user enters text to a text field                 id = participantId                   123456789
    input text                                           id = actionType    (CSA) Coordination and Support Actions
    the user clicks the button/link                      jQuery = ul li:contains("(CSA) Coordination and Support Actions")
    the user enters text to a text field                 id = fundingContribution             123456
    the user clicks the button/link                      jQuery = label:contains("No")
    the user clicks the button/link                      jQuery = label:contains("No")
    the user clicks the button/link                      id = mark-as-complete
    the user clicks the button/link                      link = Return to application overview
    the user should see the element                      jQuery = li:contains("Application details") > .task-status-complete

The user is able to complete Public description section
    the user clicks the button/link           jQuery = a:contains("Public description")
    the user should see the element           jQuery = h1:contains("Public description")
    the user enters text to a text field      css=.textarea-wrapped .editor    This is some random text
    the user clicks the button/link           id = application-question-complete
    the user clicks the button/link           jQuery = a:contains("Return to application overview")
    the user should see the element           jQuery = li:contains("Public description") > .task-status-complete

The user is able to complete Horizon 2020 grant agreement section
    the user clicks the button/link           jQuery = a:contains("Horizon 2020 grant agreement")
    the user should see the element           jQuery = h1:contains("Horizon 2020 grant agreement")
    the user uploads the file                 id = grantAgreement  ${valid_pdf}
    the user clicks the button/link           id = mark-as-complete
    the user clicks the button/link           link = Return to application overview
    the user should see the element           jQuery = li:contains("Horizon 2020 grant agreement") > .task-status-complete

The user is able to complete Finance details section
    the user clicks the button/link           jQuery = a:contains("Your project finances")
    the user should see the element           jQuery = h1:contains("Your project finances")
    the user is able to complete your project location section
    the user is able to complete your organisation section
    the user is able to complete your project costs section
    the user clicks the button/link            link = Return to application overview
    the user should see the element           jQuery = li:contains("Your project finances") > .task-status-complete

The user is able to complete Your project location section
     the user clicks the button/link           jQuery = a:contains("Your project location")
     the user should see the element           jQuery = h1:contains("Your project location")
     the user enters text to a text field      id = postcode   SE1 9HB
     the user clicks the button/link           jQuery = button:contains("Mark")
     the user should see the element           jQuery = li:contains("Your project location") > .task-status-complete

The user is able to complete Your organisation section
     the user clicks the button/link           link = Your organisation
     the user should see the element           jQuery = h1:contains("Your organisation")
     the user selects the radio button         organisationSize   MEDIUM
     the user enters text to a text field      id = turnover   500000
     the user enters text to a text field      id = headCount  100
     the user clicks the button/link           jQuery = button:contains("Mark")
     the user should see the element           jQuery = li:contains("Your organisation") > .task-status-complete

The user is able to complete your project costs section
    the user clicks the button/link           link = Your project costs
    the user should see the element           jQuery = h1:contains("Your project costs")
    the user is able to validate conversion spredsheet links works
    the user enters text to a text field      id = labour  50000
    the user enters text to a text field      id = overhead  40000
    the user enters text to a text field      id = material  30000
    the user enters text to a text field      id = capital  20000
    the user enters text to a text field      id = subcontracting  15000
    the user enters text to a text field      id = travel  10000
    the user enters text to a text field      id = other  0
    the user clicks the button/link           jQuery = button:contains("Mark")
    the user should see the element           jQuery = li:contains("Your project costs") > .task-status-complete

The user is able to validate conversion spredsheet links works
    the user clicks the button/link                 link = funding conversion spreadsheet
    Select Window                                   title = 404 - UK Research and Innovation
    the user should see the element                 jQuery = p:contains("Go back")
    the user closes the last opened tab

The user is able to submit the application
    the user clicks the button/link           link = Review and submit
    the user should see the element           jQuery = h1:contains("Application summary")
    the user clicks the button/link           id = submit-application-button-modal
    the user clicks the button/link           jQuery = button:contains("Yes, I want to submit my application")

The user is able to verify validation on each page
    validate errors on Application details page
    validate errors on public description page
    validate errors on h2020 grant agreement page
    validate errors on your project finances section
    validate the user is unable to submit an incomplete application

Validate errors on Application details page
    the user clicks the button/link                      jQuery = a:contains("Application details")
    the user clicks the button/link                      id = mark-as-complete
    the user should see a field and summary error        Enter a project name.
    the user should see a field and summary error        ${enter_a_valid_date}
    the user should see a field and summary error        Please enter a future date.
    the user should see a field and summary error        ${enter_a_valid_date}
    the user should see a field and summary error        Enter a grant agreement number.
    the user should see a field and summary error        Enter a valid PIC.
    the user should see a field and summary error        Select a type of action.
    the user should see a field and summary error        Enter the EU funding contribution.
    the user should see a field and summary error        Select a project co-ordinator option.
    the user clicks the button/link                      jQuery = button:contains("Save and return to application overview")

Validate errors on Public description page
    the user clicks the button/link                      jQuery = a:contains("Public description")
    the user clicks the button/link                      jQuery = button:contains("Mark")
    the user should see a field and summary error        Please enter some text.
    the user clicks the button/link                      jQuery = button:contains("Save and return to application overview")

Validate errors on H2020 grant agreement page
    the user clicks the button/link                      jQuery = a:contains("Horizon 2020 grant agreement")
    the user clicks the button/link                      id = mark-as-complete
    the user should see a field and summary error        ${empty_field_warning_message}
    the user clicks the button/link                      jQuery = button:contains("Save and return to application overview")

Validate errors on Your project Finances section
    the user clicks the button/link                      jQuery = a:contains("Your project finances")
    the user clicks the button/link                      jQuery = a:contains("Your project location")
    the user clicks the button/link                      jQuery = button:contains("Mark")
    the user should see a field and summary error        Enter a valid postcode.
    the user clicks the button/link                      jQuery = button:contains("Save and return to project finances")
    the user clicks the button/link                      jQuery = a:contains("Your organisation")
    the user clicks the button/link                      jQuery = button:contains("Mark")
    the user should see a field and summary error        Enter your organisation size.
    the user should see a field and summary error        ${empty_field_warning_message}
    the user should see a field and summary error        ${empty_field_warning_message}
    the user clicks the button/link                      jQuery = button:contains("Save and return to project finances")
    the user clicks the button/link                      jQuery = a:contains("Return to application overview")

Validate the user is unable to submit an incomplete application
    the user clicks the button/link    jQuery = a:contains("Review and submit")
    the user should see the element    jQuery = div:contains(" Incomplete") button:contains("Application details")
    the user should see the element    jQuery = div:contains(" Incomplete") button:contains("Public description")
    the user should see the element    jQuery = div:contains(" Incomplete") button:contains("Horizon 2020 grant agreement")
    the user should see the element    jQuery = div:contains(" Incomplete") button:contains("Funding breakdown")

Custom Suite Teardown
    the user closes the browser
    disconnect from database

the user is able to filter on status
    the user selects the option from the drop-down menu  Unsuccessful  id = fundingFilter
    the user clicks the button/link                      jQuery = button:contains("Filter")
    the user should not see the element                  jQuery = td:contains("${id}")
    the user selects the option from the drop-down menu  Successful  id = fundingFilter
    the user clicks the button/link                      jQuery = button:contains("Filter")
    the user should see the element                      jQuery = td:contains("${id}")
    the user clicks the button/link                      link = Dashboard