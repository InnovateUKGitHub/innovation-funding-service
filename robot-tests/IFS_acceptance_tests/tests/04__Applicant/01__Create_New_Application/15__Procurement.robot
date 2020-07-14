*** Settings ***
Documentation   IFS-6096 SBRI - Project Cost Guidance Review
...
...             IFS-5097 Update to overhead costs in procurement application
...
...             IFS-6368 Loans - Remove Documents
...
...             IFS-7310 Internal user can allow multiple appendices in comp creation
...
...             IFS-7311 Applicant can upload multiple appendices of allowed file types
...
...             IFS-7703 Applicant can answer multiple choice questions
...
...             IFS-7700 EDI application question configuration
...
...             IFS-7718 EDI question - application form
...
Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Resource        ../../../resources/defaultResources.robot
Resource        ../../../resources/common/Applicant_Commons.robot
Resource        ../../../resources/common/Competition_Commons.robot
Resource        ../../../resources/common/Assessor_Commons.robot
Resource        ../../../resources/common/PS_Common.robot

*** Variables ***
${comp_name}                  Procurement AT Comp
${appl_name}                  Procurement app
${ods_file}                   file_example_ODS.ods
${excel_file}                 testing.xlsx
${pdf_file}                   testing.pdf
${multiple_choice_answer}     option2

*** Test Cases ***
Comp Admin creates procurement competition
    [Documentation]  IFS-6368   IFS-7310  IFS-7703  IFS-7700
    Given Logging in and Error Checking                          &{Comp_admin1_credentials}
    Then the competition admin creates competition               ${rto_type_id}  ${comp_name}  procurement  Programme  2  PROCUREMENT  PROJECT_SETUP  no  2  true  single-or-collaborative

Applicant applies to newly created procurement competition
    [Documentation]  IFS-2688
    [Setup]  get competition id and set open date to yesterday    ${comp_name}
    Given Log in as a different user                              &{RTO_lead_applicant_credentials}
    Then logged in user applies to competition                    ${comp_name}  3

Applicant completes Application questions
    [Documentation]  IFS-2688 IFS-3287  IFS-5920  IFS-6096  IFS-5097  IFS-7311  IFS-7703  IFS-7718
    Given the user clicks the button/link                                                        link=Application details
    When the user fills in procurement Application details                                       ${appl_name}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team
    And the applicant marks EDI question as complete
    Then the lead applicant fills all the questions and marks as complete(procurement)
    And the lead completes the questions with multiple answer choice and multiple appendices

Applicant fills in project costs with VAT
    [Documentation]  IFS-5098
    When the user navigates to Your-finances page       ${appl_name}
    Given the user fills the procurement project costs  Calculate  52,214
    When the user checks the VAT calculations
    Then the user enters the project location
    And the user fills in the organisation information  ${appl_name}  ${SMALL_ORGANISATION_SIZE}
    And the user clicks the button/link                 link = Back to application overview
    And the user should see the element                 jQuery = li:contains("Your project finances") > .task-status-complete

Applicant submits the application
    [Documentation]  IFS-2688 IFS-3287  IFS-5920  IFS-6096  IFS-5097
    [Setup]  get application id by name and set as suite variable  ${appl_name}
    Given the user accept the procurement terms and conditions
    When the user selects research category                      Feasibility studies
    Then the applicant submits the procurement application
    [Teardown]  update milestone to yesterday                    ${competitionId}  SUBMISSION_DATE

Invite a registered assessor
    [Documentation]  IFS-2376
    Given log in as a different user                          &{Comp_admin1_credentials}
    When the user clicks the button/link                      link = ${comp_name}
    And the user clicks the button/link                       link = Invite assessors to assess the competition
    And the user enters text to a text field                  id = assessorNameFilter   Paul Plum
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Filter")
    Then the user clicks the button/link                      jQuery = tr:contains("Paul Plum") label[for^="assessor-row"]
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Add selected to invite list")
    And the user clicks the button/link                       link = Invite
    And the user clicks the button/link                       link = Review and send invites
    And the user enters text to a text field                  id = message    This is custom text
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Send invite")

Allocated assessor accepts invite to assess the competition
    [Documentation]  IFS-2376
    Given Log in as a different user                        &{assessor_credentials}
    When The user clicks the button/link                    Link = ${comp_name}
    And the user selects the radio button                   acceptInvitation  true
    And The user clicks the button/link                     jQuery = button:contains("Confirm")
    Then the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard

Comp Admin allocates assessor to application
    [Documentation]  IFS-2376
    Given log in as a different user              &{Comp_admin1_credentials}
    When The user clicks the button/link          link = Dashboard
    And The user clicks the button/link           link = ${comp_name}
    And The user clicks the button/link           jQuery = a:contains("Manage assessments")
    And the user clicks the button/link           jQuery = a:contains("Allocate applications")
    Then the user clicks the button/link          jQuery = tr:contains("${appl_name}") a:contains("Assign")
    And the user adds an assessor to application  jQuery = tr:contains("Paul Plum") :checkbox
    When the user navigates to the page           ${server}/management/competition/${competitionId}
    Then the user clicks the button/link          jQuery = button:contains("Notify assessors")

Allocated assessor assess the application
    [Documentation]  IFS-2376  IFS-7311 IFS-7703
    Given Log in as a different user                                                  &{assessor_credentials}
    And the user accepts the application to assess
    And the user should be redirected to the correct page                             ${server}/assessment/assessor/dashboard/competition/${competitionId}
    When the user clicks the button/link                                              link = ${appl_name}
    Then the user can see multiple appendices uploaded to the application question
    And the assessor submits the assessment

the comp admin closes the assessment and releases feedback
    [Documentation]  IFS-2376
    Given log in as a different user                  &{Comp_admin1_credentials}
    When making the application a successful project  ${competitionId}    ${appl_name}
    And moving competition to Project Setup           ${competitionId}
    Then the user should not see an error in the page

the procurement comp moves to project setup tab
    [Documentation]  IFS-2376  IFS-6368
    Given the user clicks the button/link    link = Dashboard
    When the user clicks the button/link     jQuery = a:contains("Project setup")
    Then the user clicks the button/link     link = ${comp_name}
    And the user should see the element      jQuery = h1:contains("${comp_name}")
    And the user should not see the element  jQuery = th:contains("Documents")
    And the user should not see the element  jQuery = #table-project-status th:contains("${comp_name}") ~ td:contains("Pending")

Applicant completes project setup details
    [Documentation]  IFS-6368
    [Setup]  Requesting Project ID of this Project
    Given the user completes the project details
    And the user completes the project team details
    And the user enters bank details

Project finance completes all project setup steps
    [Documentation]  IFS-6368
    [Setup]  log in as a different user        &{internal_finance_credentials}
    Given internal user assign MO to loan project
    And internal user approve bank details
    And internal user generate SP

Internal user generate the GOL
    [Documentation]  IFS-6368
    Given applicant send project spend profile
    When the internal user approve SP and issue GOL
    Then applicant upload the GOL
    And the internal user approve the GOL   ${ProjectID}

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

the user accepts the application to assess
    the user clicks the button/link       link = ${comp_name}
    the user clicks the button/link       jQuery = li:contains("${appl_name}") a:contains("Accept or reject")
    the user selects the radio button     assessmentAccept  true
    the user clicks the button/link       jQuery = .govuk-button:contains("Confirm")

the user can see multiple appendices uploaded to the application question
    the user clicks the button/link     jQuery = a:contains("Technical approach")
    the user should see the element     jQuery = p:contains("${multiple_choice_answer}")
    the user should see the element     jQuery = a:contains("${pdf_file}")
    the user should see the element     jQuery = a:contains("${ods_file}")
    the user should see the element     jQuery = a:contains("${excel_file}")
    the user clicks the button/link     link = Back to your assessment overview

the user fills in procurement Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user should see the element       jQuery = h1:contains("Application details")
    the user enters text to a text field  css = [id="name"]  ${appTitle}
    the user enters text to a text field  id = startDate  ${tomorrowday}
    the user enters text to a text field  css = #application_details-startdate_month  ${month}
    the user enters text to a text field  css = #application_details-startdate_year  ${nextyear}
    the user enters text to a text field  css = [id="durationInMonths"]  24
    the user selects the value from the drop-down menu   INNOVATE_UK_WEBSITE   id = competitionReferralSource
    the user selects the radio button     START_UP_ESTABLISHED_FOR_LESS_THAN_A_YEAR   company-age-less-than-one
    the user selects the value from the drop-down menu   BANKS_AND_INSURANCE   id = companyPrimaryFocus
    the user clicks the button twice      css = label[for="resubmission-no"]
    the user should not see the element   link = Choose your innovation area
    the user clicks the button/link       id = application-question-complete
    the user clicks the button/link       link = Back to application overview
    the user should see the element       jQuery = li:contains("Application details") > .task-status-complete

the user marks the procurement finances as complete
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}  ${Project_growth_table}
    the user clicks the button/link                  link = Your project costs
    the user clicks the button/link                  jQuery = button:contains("Overhead costs")
    the user should see the element                  jQuery = .govuk-details__summary span:contains("Overheads costs guidance")
    the user clicks the button/link                  link = Your project finances
    the user fills in the procurement project costs  ${overheadsCost}  ${totalCosts}
    the user enters the project location
    the user fills in the organisation information   ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user should not see the element              css = table
    the user should see all procurement finance subsections complete
    the user clicks the button/link                  link = Back to application overview
    the user should see the element                  jQuery = li:contains("Your project finances") > .task-status-complete
    the user should not see the element              link = Finances overview

the user should see all procurement finance subsections complete
    the user should see the element  css = li:nth-of-type(1) .task-status-complete
    the user should see the element  css = li:nth-of-type(2) .task-status-complete
    the user should see the element  css = li:nth-of-type(3) .task-status-complete

the applicant submits the procurement application
    the user clicks the button/link                             link = Review and submit
    the user should not see the element                         jQuery = .task-status-incomplete
    the user clicks the button/link                             jQuery = .govuk-button:contains("Submit application")
    the user clicks the button/link                             jQuery = .govuk-button:contains("Yes, I want to submit my application")
    the user should be redirected to the correct page           track
    the user should see the element                             link = Print application
    the user navigates to the page without the usual headers    ${SERVER}/application/${application_id}/print?noprint    #This URL its only for testing purposes
    the user should see the element                             jQuery = .govuk-button:contains("Print your application")
    the user navigates to the page                              ${server}

Competition is closed
    Get competitions id and set it as suite variable    ${comp_name}
    update milestone to yesterday                       ${competitionId}  SUBMISSION_DATE

the assessor submits the assessment
    the user clicks the button/link               link = Finances overview
    the user should see the element               jQuery = h2:contains("Project cost breakdown") ~ div:contains("Total VAT")
    the user clicks the button/link               link = Back to your assessment overview
    the assessor adds score and feedback for every question    11   # value 5: is the number of questions to loop through to submit feedback
    the user clicks the button/link               link = Review and complete your assessment
    the user selects the radio button             fundingConfirmation  true
    the user enters text to a text field          id = feedback    Procurement application assessed
    the user clicks the button/link               jQuery = .govuk-button:contains("Save assessment")
    the user clicks the button/link               jQuery = li:contains("${appl_name}") label[for^="assessmentIds"]
    the user clicks the button/link               jQuery = .govuk-button:contains("Submit assessments")
    the user clicks the button/link               jQuery = button:contains("Yes I want to submit the assessments")
    the user should see the element               jQuery = li:contains("${appl_name}") strong:contains("Recommended")

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

the user checks the VAT calculations
    the user clicks the button/link                css = label[for="stateAidAgreed"]
    the user clicks the button/link                jQuery = button:contains("Mark as complete")
    the user should see a field and summary error  Select if you are VAT registered
    the user selects the radio button              vatForm.registered  false
    the user should not see the element            id = vat-total
    the user selects the radio button              vatForm.registered  true
    the user should see the element                jQuery = #vatRegistered-totals div:contains("Total VAT") ~ div:contains("£12,140") ~ div:contains("project costs") ~ div:contains("72,839")
    the user clicks the button/link                jQuery = button:contains("Mark as complete")
    the user clicks the button/link                link = Back to application overview
    the user clicks the button/link                link = Review and submit
    the user expands the section                   Funding breakdown
    the user should see the element                jQuery = th:contains("Total VAT")
    the user should see the element                jQuery = td:contains("£72,839") ~ td:contains("12,140")
    the user clicks the button/link                link = Application overview
    the user clicks the button/link                link = Your project finances

the user completes the project details
    log in as a different user                   &{RTO_lead_applicant_credentials}
    the user navigates to the page                ${server}/project-setup/project/${ProjectID}
    the user clicks the button/link               link = view application feedback
    the user clicks the button/link               link = 5. Technical approach
    the user should see the element               jQuery = p:contains("${multiple_choice_answer}")
    the user clicks the button/link               link = Feedback overview
    the user clicks the button/link               link = Back to set up your project
    the user should not see the element           jQuery = h2:contains("Documents")
    the user clicks the button/link               link = Project details
    the user clicks the button/link               link = Correspondence address
    the user enter the Correspondence address
    the user clicks the button/link               link = Return to set up your project
    the user should see the element               css = ul li.complete:nth-child(1)

Requesting Project ID of this Project
    ${ProjectID} =  get project id by name    ${appl_name}
    Set suite variable    ${ProjectID}

internal user assign MO to loan project
    the user navigates to the page           ${server}/project-setup-management/project/${ProjectID}/monitoring-officer
    Search for MO                            Orvill  Orville Gibbs
    The internal user assign project to MO   ${application_id}  ${appl_name}

internal user approve bank details
    the user navigates to the page           ${server}/project-setup-management/project/${ProjectID}/review-all-bank-details
    the user clicks the button/link          jQuery = button:contains("Approve bank account details")
    the user clicks the button/link          id = submit-approve-bank-details

internal user generate SP
    the user navigates to the page           ${server}/project-setup-management/project/${ProjectID}/finance-check
    the user clicks the button/link          jQuery = table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Review")
    the user selects the checkbox            project-viable
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link          css = #confirm-button
    the user clicks the button/link          css = [name="confirm-viability"]
    the user clicks the button/link         link = Finance checks
    the user clicks the button/link          jQuery = table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")
    the user selects the checkbox            project-eligible
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link          css = #confirm-button
    the user clicks the button/link          css = [name="confirm-eligibility"]
    the user clicks the button/link          link = Return to finance checks
    the user should see the element          jQuery = table.table-progress tr:nth-child(1) td:nth-child(5) span:contains("Green")
    the user clicks the button/link          css = .generate-spend-profile-main-button
    the user clicks the button/link          css = #generate-spend-profile-modal-button

applicant send project spend profile
    Log in as a different user            &{RTO_lead_applicant_credentials}
    the user navigates to the page        ${server}/project-setup/project/${ProjectID}
    the user clicks the button/link       link = Spend profile
    the user clicks the button/link       link = ${Crystalrover_Name}
    the user clicks the button/link       jQuery = button:contains("Mark as complete")
    the user clicks the button/link       jQuery = a:contains("Review and submit project spend profile")
    the user clicks the button/link       jQuery = a:contains("Submit project spend profile")
    the user clicks the button/link       id = submit-send-all-spend-profiles

the internal user approve SP and issue GOL
    log in as a different user        &{internal_finance_credentials}
    the user navigates to the page    ${server}/project-setup-management/project/${ProjectID}/spend-profile/approval
    the user selects the checkbox     approvedByLeadTechnologist
    the user clicks the button/link   id = accept-profile
    the user clicks the button/link   jQuery = button:contains("Approve")
    the user navigates to the page    ${server}/project-setup-management/project/${ProjectID}/grant-offer-letter/send
    the user uploads the file          grantOfferLetter  ${valid_pdf}
    the user selects the checkbox      confirmation
    the user clicks the button/link    jQuery = button:contains("Send letter to project team")
    the user clicks the button/link    jQuery = button:contains("Send grant offer letter")

applicant upload the GOL
    Log in as a different user            &{RTO_lead_applicant_credentials}
    the user navigates to the page        ${server}/project-setup/project/${ProjectID}
    the user clicks the button/link       jQuery = a:contains("Grant offer letter")
    the user uploads the file             signedGrantOfferLetter    ${valid_pdf}
    the user clicks the button/link       css = .govuk-button[data-js-modal = "modal-confirm-grant-offer-letter"]
    the user clicks the button/link       id = submit-gol-for-review