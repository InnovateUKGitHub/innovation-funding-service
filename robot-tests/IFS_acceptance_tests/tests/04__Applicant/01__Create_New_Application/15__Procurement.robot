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
...             IFS-7596 Print Preview Format
...
...             IFS-8779 Subsidy Control - Create a New Competition - Initial Details
...
...             IFS-8938 SBRI Milestones - Non JS Milestones Page - Application
...
...             IFS-8958 SBRI Milestones - Application overview / summary
...
...             IFS-8940 SBRI Milestones - Edit project duration in application
...
...             IFS-8944 SBRI Milestones - Record changes to milestones
...
...             IFS-9214 Add dual T&Cs to Subsidy Control Competitions
...
...             IFS-8947 SBRI Milestones - Reset finances
...
...             IFS-9359 Create new project IDs for successful applications to avoid duplication in IFS PA
...
...             IFS-8945 SBRI milestones - Map milestones to IFS PA
...
...             IFS-8847 Always open competitions: new comp setup configuration
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
${appl_name2}                 SBRI application
${ods_file}                   file_example_ODS.ods
${excel_file}                 testing.xlsx
${pdf_file}                   testing.pdf
${multiple_choice_answer}     option2


*** Test Cases ***
Comp Admin creates procurement competition
    [Documentation]  IFS-6368   IFS-7310  IFS-7703  IFS-7700  IFS-8779  IFS-9124  IFS-8847
    Given Logging in and Error Checking                          &{Comp_admin1_credentials}
    Then the competition admin creates competition               ${rto_type_id}  ${comp_name}  procurement  Programme  STATE_AID  PROCUREMENT  PROJECT_SETUP  no  2  false  single-or-collaborative  No

Applicant applies to newly created procurement competition
    [Documentation]  IFS-2688
    [Setup]  get competition id and set open date to yesterday     ${comp_name}
    Given Log in as a different user                               &{RTO_lead_applicant_credentials}
    Then logged in user applies to competition                     ${comp_name}  3

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
    When the user navigates to Your-finances page          ${appl_name}
    Given the user fills the procurement project costs     Calculate  52,214
    When the user checks the VAT calculations
    Then the user enters the project location
    And the user fills in the organisation information     ${appl_name}  ${SMALL_ORGANISATION_SIZE}

Applicant fills in payment milestones
    [Documentation]  IFS-8938
    Given the user clicks the button/link                           link = Your payment milestones
    And the user clicks the button/link                             jQuery = button:contains("Open all")
    When applicant fills in payment milestone                       accordion-finances-content  2  Milestone 1  £72,839  taskOrActivity 1  deliverable 1  successCriteria 1
    And the user clicks the button/link                             id = mark-all-as-complete
    Then applicant views saved payment milestones                   2  £72,839  Milestone 1  100%  £72,839  100%
    And applicant views readonly payment milestones subsections     taskOrActivity 1  deliverable 1  successCriteria 1
    And the user should see the element                             jQuery = li:contains("Your payment milestones") > .task-status-complete
    And the user clicks the button/link                             link = Back to application overview
    And the user should see the element                             jQuery = li:contains("Your project finances") > .task-status-complete

Applicant is shown a validation message when the project duration is less than allowed
    [Documentation]  IFS-8940
    Given the user clicks the button/link         link = Application details
    And the user clicks the button/link           id = edit-application-details-button
    When the user enters text to a text field     id = durationInMonths  1
    And the user clicks the button/link           id = application-question-complete
    Then the user should see a field error        This cannot be less than your stated payment milestones. You will need to adjust these to change the duration.

Applicant can edit the project duration before application submission
    [Documentation]  IFS-8940
    Given the user enters text to a text field     id = durationInMonths  3
    When the user clicks the button/link           id = application-question-complete
    Then the user should see the element           jQuery = dd:contains("3 months")
    And the user clicks the button/link            link = Back to application overview

Applicant can view payment milestones table when reviewing and submitting application
    [Documentation]  IFS-8958
    Given the user clicks the button/link                                   jQuery = a:contains("Review and submit")
    And the user should see the element                                     jQuery = h1:contains("Application summary")
    When the user clicks the button/link                                    jQuery = .govuk-accordion__section-heading button:contains("Funding breakdown")
    Then the payment milestone table is visible in application summary

Applicant submits the application
    [Documentation]  IFS-2688 IFS-3287  IFS-5920  IFS-6096  IFS-5097  IFS-7596
    [Setup]  get application id by name and set as suite variable     ${appl_name}
    Given The user clicks the button/link                             jQuery = a:contains("Application overview")
    When the user accept the procurement terms and conditions
    Then the applicant submits the procurement application
    [Teardown]  update milestone to yesterday                         ${competitionId}  SUBMISSION_DATE

Invite a registered assessor
    [Documentation]  IFS-2376
    Given log in as a different user             &{Comp_admin1_credentials}
    When the user clicks the button/link         link = ${comp_name}
    And the user clicks the button/link          link = Invite assessors to assess the competition
    And the user enters text to a text field     id = assessorNameFilter   Paul Plum
    And the user clicks the button/link          jQuery = .govuk-button:contains("Filter")
    Then the user clicks the button/link         jQuery = tr:contains("Paul Plum") label[for^="assessor-row"]
    And the user clicks the button/link          jQuery = .govuk-button:contains("Add selected to invite list")
    And the user clicks the button/link          link = Invite
    And the user clicks the button/link          link = Review and send invites
    And the user enters text to a text field     id = message    This is custom text
    And the user clicks the button/link          jQuery = .govuk-button:contains("Send invitation")

Allocated assessor accepts invite to assess the competition
    [Documentation]  IFS-2376
    Given Log in as a different user                           &{assessor_credentials}
    When The user clicks the button/link                       Link = ${comp_name}
    And the user selects the radio button                      acceptInvitation  true
    And The user clicks the button/link                        jQuery = button:contains("Confirm")
    Then the user should be redirected to the correct page     ${server}/assessment/assessor/dashboard

Comp Admin allocates assessor to application
    [Documentation]  IFS-2376
    Given log in as a different user                 &{Comp_admin1_credentials}
    When The user clicks the button/link             link = Dashboard
    And The user clicks the button/link              link = ${comp_name}
    And The user clicks the button/link              jQuery = a:contains("Manage assessments")
    And the user clicks the button/link              jQuery = a:contains("Allocate applications")
    Then the user clicks the button/link             jQuery = tr:contains("${appl_name}") a:contains("Assign")
    And the user adds an assessor to application     jQuery = tr:contains("Paul Plum") :checkbox
    When the user navigates to the page              ${server}/management/competition/${competitionId}
    Then the user clicks the button/link             jQuery = button:contains("Notify assessors")

Allocated assessor assess the application
    [Documentation]  IFS-2376  IFS-7311 IFS-7703
    Given Log in as a different user                                                   &{assessor_credentials}
    And the user accepts the application to assess
    And the user should be redirected to the correct page                              ${server}/assessment/assessor/dashboard/competition/${competitionId}
    When the user clicks the button/link                                               link = ${appl_name}
    Then the user can see multiple appendices uploaded to the application question
    And the assessor submits the assessment

User migrates application to avoid duplication in IFS PA
    [Documentation]  IFS-9359
    When the user checks null for previous application id
    Then the user migrates application

Comp admin closes the assessment and releases feedback
    [Documentation]  IFS-2376  IFS-9359
    Given log in as a different user                     &{Comp_admin1_credentials}
    When making the application a successful project     ${competitionId}    ${appl_name}
    And moving competition to Project Setup              ${competitionId}
    Then the user should not see an error in the page
    [Teardown]  the user checks migration is successful

Procurement comp moves to project setup tab
    [Documentation]  IFS-2376  IFS-6368
    Given the user clicks the button/link       link = Dashboard
    When the user clicks the button/link        jQuery = a:contains("Project setup")
    Then the user clicks the button/link        link = ${comp_name}
    And the user should see the element         jQuery = h1:contains("${comp_name}")
    And the user should not see the element     jQuery = th:contains("Documents")
    And the user should not see the element     jQuery = #table-project-status th:contains("${comp_name}") ~ td:contains("Pending")

Applicant completes project setup details
    [Documentation]  IFS-6368
    [Setup]  Requesting Project ID of this Project
    Given the user completes the project details
    And the user completes the project team details
    And the user enters bank details

Project finance completes finance checks then reverts them
    [Documentation]  IFS-8947
    [Setup]  log in as a different user                               &{internal_finance_credentials}
    Given the user navigates to the page                              ${server}/project-setup-management/project/${ProjectID}/finance-check
    When confirm viability and eligibility
    Then confirm milestone                                            0
    And the user reverts the milestones eligibility and viability

Project finance completes all project setup steps
    [Documentation]  IFS-6368  IFS-8947  IFS-8945
    Given internal user assign MO to loan project
    And internal user approve bank details
    And internal user approves the finance checks

Internal user generate the contract
    [Documentation]  IFS-6368  IFS-8945
    Given the internal user approve SP and issue contract
    When Lead applicant upload the contract
    Then the internal user approve the contract               ${ProjectID}

Internal user makes changes to the finance payment milestones
    [Documentation]   IFS-8944
    Given Requesting SBRI Project ID of this Project
    And log in as a different user                            &{ifs_admin_user_credentials}
    When the user navigates to the page                       ${server}/project-setup-management/project/${SBRI_projectID}/finance-check/organisation/${Dreambit_Id}/procurement-milestones
    And the user makes changes to the payment milestones table
    Then the user should see the element                      jQuery = td:contains("12,523") ~ td:contains("- 100")
    And the user should see the element                       jQuery = td:contains("12,121") ~ td:contains("+ 100")
    And the user should see the element                       jQuery = th:contains("Total payment requested") ~ td:contains("£265,084")

Internal user makes changes to project finances
    [Documentation]   IFS-8944
    Given the user navigates to the page                    ${server}/project-setup-management/project/${SBRI_projectID}/finance-check/organisation/${Dreambit_Id}/eligibility
    When the user makes changes to the project finances
    And the user navigates to the page                      ${server}/project-setup-management/project/${SBRI_projectID}/finance-check/organisation/${Dreambit_Id}/eligibility/changes
    Then the user should see the element                    jQuery = td:contains("90,000") + td:contains("80,000") + td:contains("- 10,000")
    And the user should see the element                     jQuery = td:contains("1,100") + td:contains("11,100") + td:contains("+ 10,000")
    And the user should see the element                     jQuery = td:contains("£243,484")

Internal user removes payment milestones
    [Documentation]   IFS-8944
    Given the user navigates to the page                     ${server}/project-setup-management/project/${SBRI_projectID}/finance-check/organisation/${Dreambit_Id}/procurement-milestones
    And the user clicks the button/link                      link = Edit payment milestones
    When the user removes a payment milestone
    Then the user should not see the element                 jQuery = button:contains("Milestone for month 21")
    And the user should see the element                      jQuery = h3:contains("Total payment requested") ~ h3:contains("67.84%") ~ h3:contains("£165,171")

Internal user adds payment milestones
    [Documentation]   IFS-8944
    Given the user navigates to the page                     ${server}/project-setup-management/project/${SBRI_projectID}/finance-check/organisation/${Dreambit_Id}/procurement-milestones
    And the user clicks the button/link                      link = Edit payment milestones
    And the user clicks the button/link                      jQuery = button:contains("Open all")
    And the user clicks the button/link                      jQuery = button:contains("Close all")
    And the user clicks the button/link                      jQuery = button:contains("Add another project milestone")
    And the user clicks the button/link                      jQuery = div[id='accordion-finances'] div:nth-of-type(22) span:nth-of-type(4)
    When the user creates a new payment milestone
    And the user clicks the button/link                      jQuery = button:contains("Save and return to payment milestone check")
    And the user navigates to the page                       ${server}/project-setup-management/project/${SBRI_projectID}/finance-check/organisation/${Dreambit_Id}/procurement-milestones
    Then the user should see the element                     jQuery = h3:contains("100%") ~ h3:contains("£243,484")

Applicant can view changes made to project finances
    [Documentation]  IFS-8944
    Given Log in as a different user                        &{becky_mason_credentials}
    When the user navigates to the page                     ${server}/project-setup/project/${SBRI_projectID}/finance-check
    And the user clicks the button/link                     link = view any changes to finances
    Then the user should see all project finance changes

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
    the user should see the element                        jQuery = h1:contains("Application details")
    the user enters text to a text field                   css = [id="name"]  ${appTitle}
    the user enters text to a text field                   id = startDate  ${tomorrowday}
    the user enters text to a text field                   css = #application_details-startdate_month  ${month}
    the user enters text to a text field                   css = #application_details-startdate_year  ${nextyear}
    the user enters text to a text field                   css = [id="durationInMonths"]  24
    the user selects the value from the drop-down menu     INNOVATE_UK_WEBSITE   id = competitionReferralSource
    the user selects the radio button                      START_UP_ESTABLISHED_FOR_LESS_THAN_A_YEAR   company-age-less-than-one
    the user selects the value from the drop-down menu     BANKS_AND_INSURANCE   id = companyPrimaryFocus
    the user clicks the button twice                       css = label[for="resubmission-no"]
    the user should not see the element                    link = Choose your innovation area
    the user can mark the question as complete
    the user should see the element                        jQuery = li:contains("Application details") > .task-status-complete

the user marks the procurement finances as complete
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}  ${Project_growth_table}
    the user clicks the button/link                                      link = Your project costs
    the user clicks the button/link                                      jQuery = button:contains("Overhead costs")
    the user should see the element                                      jQuery = .govuk-details__summary span:contains("Overheads costs guidance")
    the user clicks the button/link                                      link = Your project finances
    the user fills in the procurement project costs                      ${overheadsCost}  ${totalCosts}
    the user enters the project location
    the user fills in the organisation information                       ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user should not see the element                                  css = table
    the user should see all procurement finance subsections complete
    the user clicks the button/link                                      link = Back to application overview
    the user should see the element                                      jQuery = li:contains("Your project finances") > .task-status-complete
    the user should not see the element                                  link = Finances overview

the user should see all procurement finance subsections complete
    the user should see the element     css = li:nth-of-type(1) .task-status-complete
    the user should see the element     css = li:nth-of-type(2) .task-status-complete
    the user should see the element     css = li:nth-of-type(3) .task-status-complete

the payment milestone table is visible in application summary
    the user expands the section                Funding breakdown
    the user should see the element             jQuery = h1:contains("Application summary")
    the user should see the element             jQuery = h3:contains("Payment milestones") + * tfoot:contains("£72,839") th:contains("100%")
    the user should see the element             jQuery = h3:contains("Project cost breakdown") + * td:contains("£72,839")

the user removes a payment milestone
    the user clicks the button/link             jQuery = button:contains("Milestone for month 21")
    the user clicks the button/link             xpath = //*[@id="accordion-finances-content-27"]/p/button

the applicant submits the procurement application
    the user clicks the button/link                              link = Review and submit
    the user should not see the element                          jQuery = .task-status-incomplete
    the user clicks the button/link                              jQuery = .govuk-button:contains("Submit application")
    the user should be redirected to the correct page            track
    the user should see the element                              link = Print application
    the user should see the element                              link = Reopen application
    the user navigates to the page without the usual headers     ${SERVER}/application/${application_id}/print?noprint    #This URL its only for testing purposes
    the user should see the element                              jQuery = .govuk-button:contains("Print your application")
    the user navigates to the page                               ${server}

Competition is closed
    Get competitions id and set it as suite variable    ${comp_name}
    update milestone to yesterday                       ${competitionId}  SUBMISSION_DATE

the assessor submits the assessment
    the user clicks the button/link                             link = Finances overview
    the user should see the element                             jQuery = h2:contains("Project cost breakdown") ~ div:contains("Total VAT")
    the user clicks the button/link                             link = Back to your assessment overview
    the assessor adds score and feedback for every question     11   # value 5: is the number of questions to loop through to submit feedback
    the user clicks the button/link                             link = Review and complete your assessment
    the user selects the radio button                           fundingConfirmation  true
    the user enters text to a text field                        id = feedback    Procurement application assessed
    the user clicks the button/link                             jQuery = .govuk-button:contains("Save assessment")
    the user clicks the button/link                             jQuery = li:contains("${appl_name}") label[for^="assessmentIds"]
    the user clicks the button/link                             jQuery = .govuk-button:contains("Submit assessments")
    the user clicks the button/link                             jQuery = button:contains("Yes I want to submit the assessments")
    the user should see the element                             jQuery = li:contains("${appl_name}") strong:contains("Recommended")

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

the user checks the VAT calculations
    the user clicks the button/link                   css = label[for="stateAidAgreed"]
    the user clicks the button/link                   jQuery = button:contains("Mark as complete")
    the user should see a field and summary error     Select if you are VAT registered
    the user selects the radio button                 vatForm.registered  false
    the user should not see the element               id = vat-total
    the user selects the radio button                 vatForm.registered  true
    the user should see the element                   jQuery = #vatRegistered-totals div:contains("Total VAT") ~ div:contains("£12,140") ~ div:contains("project costs") ~ div:contains("72,839")
    the user clicks the button/link                   jQuery = button:contains("Mark as complete")
    the user clicks the button/link                   link = Back to application overview
    the user clicks the button/link                   link = Review and submit
    the user expands the section                      Funding breakdown
    the user should see the element                   jQuery = th:contains("Total VAT")
    the user should see the element                   jQuery = td:contains("£72,839") ~ td:contains("12,140")
    the user clicks the button/link                   link = Application overview
    the user clicks the button/link                   link = Your project finances

the user completes the project details
    log in as a different user                    &{RTO_lead_applicant_credentials}
    the user navigates to the page                ${server}/project-setup/project/${ProjectID}
    the user clicks the button/link               link = view application feedback
    the user clicks the button/link               jQuery = button:contains("Technical approach")
    the user should see the element               jQuery = p:contains("${multiple_choice_answer}")
    the user clicks the button/link               link = Back to set up your project
    the user should not see the element           jQuery = h2:contains("Documents")
    the user clicks the button/link               link = Project details
    the user clicks the button/link               link = Correspondence address
    the user enter the Correspondence address
    the user clicks the button/link               link = Return to set up your project
    the user should see the element               css = ul li.complete:nth-child(1)

Requesting Project ID of this Project
    ${ProjectID} =  get project id by name     ${appl_name}
    Set suite variable    ${ProjectID}

Requesting SBRI Project ID of this Project
    ${SBRI_ProjectID} =  get project id by name    ${appl_name2}
    Set suite variable    ${SBRI_ProjectID}

the user makes changes to the payment milestones table
    the user navigates to the page      ${server}/project-setup-management/project/${SBRI_projectID}/finance-check/organisation/${Dreambit_Id}/procurement-milestones?editMilestones=true
    clear element text                  id = milestones[7].payment
    input text                          id = milestones[7].payment  12523
    clear element text                  id = milestones[8].payment
    input text                          id = milestones[8].payment  12121
    the user clicks the button/link     jQuery = button:contains("Save and return to payment milestone check")
    the user navigates to the page      ${server}/project-setup-management/project/${SBRI_projectID}/finance-check/organisation/${Dreambit_Id}/eligibility/changes

the user makes changes to the project finances
    the user clicks the button/link     jQuery = a:contains("Edit project costs")
    clear element text                  jQuery = div[id='accordion-finances'] div:nth-of-type(2) input[id^="procurementOverheadRows"][id$="projectCost"]
    input text                          jQuery = div[id='accordion-finances'] div:nth-of-type(2) input[id^="procurementOverheadRows"][id$="projectCost"]   100
    the user clicks the button/link     jQuery = button:contains("Subcontracting")
    clear element text                  jQuery = div[id='accordion-finances'] div:nth-of-type(6) input[id^="subcontractingRows"][id$="cost"]
    input text                          jQuery = div[id='accordion-finances'] div:nth-of-type(6) input[id^="subcontractingRows"][id$="cost"]   80000
    the user clicks the button/link     jQuery = button:contains("Other costs")
    clear element text                  css = div[id='accordion-finances'] div:nth-of-type(8) input[id^="otherRows"][id$="estimate"]
    clear element text                  css = div[id='accordion-finances'] div:nth-of-type(8) textarea
    input text                          css = div[id='accordion-finances'] div:nth-of-type(8) input[id^="otherRows"][id$="estimate"]   11100
    input text                          css = div[id='accordion-finances'] div:nth-of-type(8) textarea   Some other costs
    the user clicks the button/link     id = save-eligibility

the user creates a new payment milestone
    the user selects the option from the drop-down menu       21   jQuery = div[id='accordion-finances'] div:nth-of-type(22) select
    the user enters text to a text field                      css = [id^="accordion-finances-content-unsaved"] input[id^="milestones"][id$="description"]   Milestone month 21
    the user enters text to a text field                      css = div[id='accordion-finances'] div:nth-of-type(22) textarea[id^="milestones"][id$="taskOrActivity"]    Task Or Activity 21
    the user enters text to a text field                      css = div[id='accordion-finances'] div:nth-of-type(22) textarea[id^="milestones"][id$="deliverable"]   Deliverable 21
    the user enters text to a text field                      css = div[id='accordion-finances'] div:nth-of-type(22) textarea[id^="milestones"][id$="successCriteria"]   Success Criteria 21
    the user enters text to a text field                      css = div[id='accordion-finances'] div:nth-of-type(22) input[id^="milestones"][id$="payment"]   78313

the user should see all project finance changes
    the user should see the element                    jQuery = td:contains("90,000") + td:contains("80,000") + td:contains("- 10,000")
    the user should see the element                    jQuery = td:contains("1,100") + td:contains("11,100") + td:contains("+ 10,000")
    the user should see the element                    jQuery = td:contains("12,523") ~ td:contains("- 100")
    the user should see the element                    jQuery = td:contains("12,121") ~ td:contains("+ 100")

internal user assign MO to loan project
    the user navigates to the page             ${server}/project-setup-management/project/${ProjectID}/monitoring-officer
    Search for MO                              Orvill  Orville Gibbs
    The internal user assign project to MO     ${migrated_application_id}  ${appl_name}

internal user approve bank details
    the user navigates to the page      ${server}/project-setup-management/project/${ProjectID}/review-all-bank-details
    the user clicks the button/link     jQuery = button:contains("Approve bank account details")
    the user clicks the button/link     id = submit-approve-bank-details

internal user approves the finance checks
    the user navigates to the page                        ${server}/project-setup-management/project/${ProjectID}/finance-check
    the user clicks the button/link                       jQuery = table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Review")
    the user selects the checkbox                         project-viable
    the user selects the option from the drop-down menu   Green  id = rag-rating
    the user clicks the button/link                       css = #confirm-button
    the user clicks the button/link                       css = [name="confirm-viability"]
    the user clicks the button/link                       link = Back to finance checks
    the user clicks the button/link                       jQuery = table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")
    the user selects the checkbox                         project-eligible
    the user selects the option from the drop-down menu   Green  id = rag-rating
    the user clicks the button/link                       css = #confirm-button
    the user clicks the button/link                       css = [name="confirm-eligibility"]
    the user clicks the button/link                       link = Return to finance checks
    the user should see the element                       jQuery = table.table-progress tr:nth-child(1) td:nth-child(5) span:contains("Green")
    the user clicks the button/link                       jQuery = tr:nth-child(1) td:nth-child(6) a:contains("Review")
    the internal user approves payment milestone
    the user clicks the button/link                       link = Return to finance checks
    the user clicks the button/link                       jQuery = button:contains("Approve finance checks")

applicant send project spend profile
    Log in as a different user            &{RTO_lead_applicant_credentials}
    the user navigates to the page        ${server}/project-setup/project/${ProjectID}
    the user clicks the button/link       link = Spend profile
    the user clicks the button/link       link = ${Crystalrover_Name}
    the user clicks the button/link       jQuery = button:contains("Mark as complete")
    the user clicks the button/link       jQuery = a:contains("Review and submit project spend profile")
    the user clicks the button/link       jQuery = a:contains("Submit project spend profile")
    the user clicks the button/link       id = submit-send-all-spend-profiles

the internal user approve SP and issue contract
    log in as a different user          &{internal_finance_credentials}
    the user navigates to the page      ${server}/project-setup-management/project/${ProjectID}/grant-offer-letter/send
    the user uploads the file           grantOfferLetter  ${valid_pdf}
    the user selects the checkbox       confirmation
    the user clicks the button/link     jQuery = button:contains("Send contract to project team")
    the user clicks the button/link     jQuery = button:contains("Send contract")

Lead applicant upload the contract
    Log in as a different user         &{RTO_lead_applicant_credentials}
    the user navigates to the page     ${server}/project-setup/project/${ProjectID}
    Applicant uploads the contract

the user checks null for previous application id
    ${query} =  user queries previous application id     ${application_id}
    Should be true     ${query} is None

the user migrates application
    user inserts application into application migration table     ${application_id}

the user checks migration is successful
    ${migrated_application_id} =  user queries migrated application id    ${application_id}
    Should be true         ${migrated_application_id} != ${application_id}
    Set suite variable     ${migrated_application_id}
