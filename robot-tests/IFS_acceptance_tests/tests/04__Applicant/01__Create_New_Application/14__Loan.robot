*** Settings ***
Documentation   IFS-6237 Loans - Application submitted screen
...
...             IFS-6238 Loans - Application submitted email
...
...             IFS-6205 Loans - T&Cs select page
...
...             IFS-6207 Loans - Your Funding - How much funding is required
...
...             IFS-6208 Loans - Updates to Finance Summary Table
...
...             IFS-6369 Loans - Remove Financial year table from Spend Profile
...
...             IFS-6292 Loans - Finance Checks - Remove 'Approved' and link to viability checks
...
...             IFS-6285 Loans - Remove Bank Details - External Journey - Project Setup
...
...             IFS-6307 Loans - Remove Bank Details - Internal Journey - Project Setup
...
...             IFS-6363 Loans - Project Setup Complete - Internal Screen & Submission
...
...             IFS-6294 Loans - Project Setup Complete External Journey
...
...             IFS-6298 Loans - Project Setup Content Review
...
...             IFS-6368 Loans - Remove Documents
...
...             IFS-7244 - Loans should not have change funding percentage
...
...             IFS-8944 SBRI milestones - Record changes to milestones
...
...             IFS-9483 Loans: Content changes and banner
...
...             IFS-9484 Loans: Applicant journey
...
Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Resource        ../../../resources/defaultResources.robot
Resource        ../../../resources/common/Applicant_Commons.robot
Resource        ../../../resources/common/PS_Common.robot

*** Variables ***
${loan_comp_PS}                            Project setup loan comp
${loan_comp_PS_Id}                         ${competition_ids["${loan_comp_PS}"]}
${loan_comp_application}                   Loan Competition
${loanApplicationName}                     Loan Application
${loan_comp_appl_id}                       ${competition_ids["${loan_comp_application}"]}
${loanApplicationID}                       ${application_ids["${loanApplicationName}"]}
${loan_PS_application1}                    Loan Project 1
${loan_PS_application2}                    Loan Project 2
${loan_PS_application_Id}                  ${application_ids["${loan_PS_application1}"]}
${loan_PS_project_Id}                      ${project_ids["${loan_PS_application1}"]}
${loan_PS_project_Id2}                     ${project_ids["${loan_PS_application2}"]}
${loan_PS}                                 ${server}/project-setup/project/${loan_PS_project_Id}
${loan_PS_Url}                             ${loan_PS}/details
${loan_finance_checks}                     ${server}/project-setup-management/project/${loan_PS_project_Id}/finance-check
${eligibility_changes}                     ${loan_finance_checks}/organisation/${EMPIRE_LTD_ID}/eligibility/changes
${spend_profile}                           ${server}/project-setup-management/project/${loan_PS_project_Id}/spend-profile/approval


*** Test Cases ***
The user can see qualtrics survey fields in business and financial information application question
    [Documentation]    IFS-9484
    Given the user clicks the button/link                link = Business and financial information
    And the user clicks the button/link                  id = edit
    When the user clicks the button/link                 link = Complete the online business survey (opens in a new window)
    And select window                                    Innovate UK - Innovation Continuity Loans
    And the user clicks the button/link                  id = NextButton
    Then the user should see qualtrics survey fields
    [Teardown]  the user closes the last opened tab

The user will not be able to mark the application as complete without completing business and financial information
    [Documentation]    IFS-9484
    Given the user clicks the button/link                     link = Back to application overview
    When the user clicks the button/link                      id = application-overview-submit-cta
    Then the user should see that the element is disabled     id = submit-application-button
    And the user should see the element                       jQuery = .section-incomplete + button:contains("Business and financial information")
    And the user should see the element                       jQuery = p:contains("You must ensure that the business information and financial spreadsheet have been completed before you click submit below.")
    And the user should see the element                       jQuery = h2:contains("Applicant details")
    And the user should see the element                       jQuery = h2:contains("Project finance")

The user can complete the business and financial information application question
    [Documentation]    IFS-9484
    Given the user clicks the button/link          link = Application overview
    And the user clicks the button/link            link = Business and financial information
    And the user enters text to a text field       css = * .editor    This is the applicant response for have you completed the business information, including uploading your financial submission.
    When the user clicks the button/link           id = application-question-complete
    And the user clicks the button/link            link = Back to application overview
    Then the user should see the element           jQuery = div:contains("Business and financial information") ~ .task-status-complete

Loan application shows correct T&C's
    [Documentation]    IFS-6205  IFS-9483  IFS-9716
    Given the user clicks the button/link   link = Loan terms and conditions
    And the user should see the element     jQuery = h1:contains("Loans terms and conditions")
    When the user clicks the button/link    link = Back to application overview
    Then the user should see the element    jQuery = li:contains("Loan terms and conditions") .task-status-complete

Max funding sought validation
    [Documentation]  IFS-7866
    Given the user sets max available funding              60000  ${loan_comp_appl_id}
    When the user enters a value over the max funding
    Then the user should see a field and summary error     Your funding sought exceeds £60,000. You must lower your funding level percentage or your project costs.

Loan application Your funding
    [Documentation]  IFS-6207
    Given the user enters empty funding amount
    When the user enters text to a text field  id = amount   57,803
    And the user clicks the button/link        id = mark-all-as-complete
    Then the user should see the element       jQuery = td:contains("200,903") ~ td:contains("57,803") ~ td:contains("30.00%") ~ td:contains("2,468") ~ td:contains("140,632")

Loan application finance overview
    [Documentation]  IFS-6208
    Given the user clicks the button/link  link = Back to application overview
    When the user clicks the button/link   link = Finances overview
    Then the user should see the element   jQuery = td:contains("200,903") ~ td:contains("57,803") ~ td:contains("30.00%") ~ td:contains("2,468") ~ td:contains("140,632")

Loan application submission
    [Documentation]  IFS-6237  IFS-6238  IFS-9483
    Given the user submits the loan application
    When the user clicks the button/link            link = View application
    Then the user should see the element            jQuery = h1:contains("Application overview")
    And the user reads his email                    ${lead_applicant_credentials["email"]}   Complete your application for Loan Competition   You have completed your application for Loan Competition.

Applicant complete the project setup details
    [Documentation]  IFS-6369  IFS-6285  IFS-9483
    Given the user completes the project details
    And the user completes the project team details
    Then the user should not see the element    jQuery = h2:contains("Bank details")

The user is unable to change funding percentage
    [Documentation]  IFS-7244
    [Setup]  log in as a different user         &{internal_finance_credentials}
    Given the user navigates to the page        ${loan_finance_checks}
    When the user clicks the button/link        link = View finances
    Then the user should not see the element    link = Change funding level percentages

Funding sought validations
    [Documentation]  IFS-6293
    Given the user selects to change funding sought
    When the user enters text to a text field           id = partners[${EMPIRE_LTD_ID}].funding  ${EMPTY}
    And the user clicks the button/link                 jQuery = button:contains("Save and return to project finances")
    Then the user should see a field and summary error  Enter the amount of funding sought.

Fund sought changes
    [Documentation]  IFS-6293  IFS-6298  IFS-8944
    Given the user enters text to a text field     id = partners[${EMPIRE_LTD_ID}].funding  6000
    When the user clicks the button/link           jQuery = button:contains("Save and return to project finances")
    Then the user should see the element           jQuery = h3:contains("Finance summary") ~ div td:contains("£200,903") ~ td:contains("4.21%") ~ td:contains("6,000") ~ td:contains("2,468") ~ td:contains("192,435")

Project finance completes all project setup steps
    [Documentation]  IFS-6369  IFS-6292  IFS-6307  IFS-6298  IFS-6368
    [Setup]  log in as a different user               &{internal_finance_credentials}
    Given internal user assign MO to loan project
    And internal user generate SP
    When the user navigates to the page               ${server}/project-setup-management/competition/${loan_comp_PS_Id}/status/all
    Then the user should not see the element          jQuery = th:contains("Bank details")

Applicant checks the generated SP
    [Documentation]  IFS-6369  IFS-6298
    Given log in as a different user       &{lead_applicant_credentials}
    And the user should see the finished finance checks
    When the user navigates to the page    ${loan_PS}/partner-organisation/${EMPIRE_LTD_ID}/spend-profile/review
    Then the user should not see the financial year table on SP

Internal user can see application details in project setup
    [Documentation]  IFS-9483
    Given Log in as a different user         &{internal_finance_credentials}
    When the user navigates to the page      ${server}/management/competition/${loan_comp_PS_Id}/application/${loan_PS_application_Id}
    Then the user should see the element     jQuery = h2:contains("Applicant details")
    And the user should see the element      jQuery = h2:contains("Project finance")

Internal user can mark project as successful
    [Documentation]  IFS-6363
    Given the user approves the spend profile
    When the user navigates to the page     ${server}/project-setup-management/competition/${loan_comp_PS_Id}/status/all
    And the user clicks the button/link     jQuery = tr:contains("${loan_PS_application1}") td:contains("Review") a
    Then the user marks loan as complete    successful  ${loan_PS_application1}

Internal user can mark project as unsuccessful
    [Documentation]  IFS-6363
    Given the user navigates to the page     ${server}/project-setup-management/competition/${loan_comp_PS_Id}/status/all
    When the user clicks the button/link     jQuery = tr:contains("${loan_PS_application2}") td:contains("Review") a
    Then the user marks loan as complete     unsuccessful  ${loan_PS_application2}

Applicant checks successful and unsuccessful project status
    [Documentation]  IFS-6294
    Given log in as a different user                          &{lead_applicant_credentials}
    And the user clicks the application tile if displayed
    Then the applicant checks for project status

*** Keywords ***
Custom suite setup
    the user logs-in in new browser       &{lead_applicant_credentials}
    the user clicks the button/link       link = Loan Application
    Connect to database  @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user enters empty funding amount
    the user enters text to a text field           id = amount  ${EMPTY}
    the user clicks the button/link                id = mark-all-as-complete
    the user should see a field and summary error  Enter the amount of funding sought.

the user submits the loan application
    the user clicks the button/link           link = Application overview
    the user clicks the button/link           link = Review and submit
    the user clicks the button/link           id = submit-application-button
    the user should see the element           link = Reopen application

the user completes the project details
    log in as a different user            &{lead_applicant_credentials}
    the user navigates to the page        ${loan_PS}
    the user should not see the element   css = .message-alert
    the user clicks the button/link       link = view application feedback
    the user should see the element       jQuery = h2:Contains("Your application has progressed to project setup.") ~ p:contains("Scores and written feedback from each assessor can be found below.")
    the user should see the element       jQuery = h2:contains("Applicant details")
    the user should see the element       jQuery = h2:contains("Project finance")
    the user clicks the button/link       link = Back to set up your project
    the user clicks the button/link       link = Project details
    the user clicks the button/link       link = Correspondence address
    the user enter the Correspondence address
    the user clicks the button/link       link = Return to set up your project
    the user should see the element       css = ul li.complete:nth-child(1)

the user completes the project team details
    the user clicks the button/link     link = Project team
    the user clicks the button/link     link = Your finance contact
    the user selects the radio button   financeContact   financeContact1
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user clicks the button/link     link = Project manager
    the user selects the radio button   projectManager   projectManager1
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user clicks the button/link     link = Back to project setup

internal user assign MO to loan project
    the user navigates to the page           ${server}/project-setup-management/project/${loan_PS_project_Id}/monitoring-officer
    Search for MO                            Orvill  Orville Gibbs
    The internal user assign project to MO   ${loan_PS_application_Id}  ${loan_PS_application1}

internal user generate SP
    the user navigates to the page           ${loan_finance_checks}
    the user should see the element          jQuery = table.table-progress tr:nth-child(1) td:nth-child(3) span:contains("Not set")
    the user should see the element          jQuery = dt:contains("Other funding")
    the user should see the element          jQuery = dt:contains("Funding sought")
    the user should see the element          jQuery = dt:contains("Total percentage loan")
    the user clicks the button/link          jQuery = table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Review")
    the user selects the checkbox            project-viable
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link          css = #confirm-button
    the user clicks the button/link          jQuery = .modal-confirm-viability .govuk-button:contains("Confirm viability")
    the user clicks the button/link          link = Return to finance checks
    the user clicks the button/link          jQuery = table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")
    the user selects the checkbox            project-eligible
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link          css = #confirm-button
    the user clicks the button/link          css = [name="confirm-eligibility"]
    the user should see the element          jQuery = .govuk-body:contains("The organisation’s finance eligibility has been approved by")
    the user clicks the button/link          link = Return to finance checks
    the user clicks the button/link          css = .generate-spend-profile-main-button
    the user clicks the button/link          css = #generate-spend-profile-modal-button
    the user navigates to the page           ${server}/project-setup-management/project/${loan_PS_project_Id}/finance-check-overview
    the user should see the element          jQuery = th:contains("Loan applied for (£)")
    the user should see the element          jQuery = th:contains("Total % loan")

the user should not see the financial year table on SP
    the user should not see the element     jQuery = h2:contains("Project costs for financial year")
    the user should not see the element     jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link         link = Edit spend profile
    the user should not see the element     jQuery = h2:contains("Project costs for financial year")
    the user should not see the element     jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link         jQuery = button:contains("Save and return to spend profile overview")
    the user clicks the button/link         jQuery = button:contains("Mark as complete")
    the user clicks the button/link         link = Empire Ltd
    the user should not see the element     jQuery = p:contains("Your submitted spend profile will be used as the base for your project spend over the following financial years.")
    the user should not see the element     jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link         link = Spend profile overview
    the user clicks the button/link         jQuery = a:contains("Review and submit project spend profile")
    the user should see the element         jQuery = h2:contains("Project - Spend profile")
    the user should not see the element     jQuery = h2:contains("Project costs for financial year")
    the user should not see the element     jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link         jQuery = a:contains("Submit project spend profile")
    the user clicks the button/link         id = submit-send-all-spend-profiles

the user selects to change funding sought
    log in as a different user            &{internal_finance_credentials}
    the user navigates to the page        ${loan_finance_checks}
    the user clicks the button/link       link = View finances
    the user clicks the button/link       link = Change funding sought

the internal user should see the funding changes
    the user clicks the button/link     link = View changes to finances
    the user should see the element     jQuery = th:contains("Funding sought (£)") ~ td:contains("12,000") ~ td:contains("6,000") ~ td:contains("- 6000")
    the user should see the element     jQuery = th:contains("Other funding (£)") ~ td:contains("2,468")
    the user should see the element     jQuery = th:contains("Contribution to project (£)") ~ td:contains("186,435") ~ td:contains("196,335") ~ td:contains("+ 9900")
    the user should see the element     jQuery = th:contains("Funding level (%)") ~ td:contains("7") ~ td:contains("4") ~ td:contains("- 3.07")
    the user should see the element     jQuery = th:contains("Total project costs") ~ td:contains("£203,371") ~ td:contains("£207,271") ~ td:contains("£3900")
    the user should see the element     jQuery = th:contains("Other costs") ~ td:contains("1,100") ~ td:contains("5,000") ~ td:contains("+ 3900")
    the user should see the element     jQuery = th:contains("Labour") ~ td:contains("3,081")
    the user should see the element     jQuery = th:contains("Overheads") ~ td:contains("0")
    the user should see the element     jQuery = th:contains("Materials") ~ td:contains("100,200")
    the user should see the element     jQuery = th:contains("Capital usage") ~ td:contains("552")
    the user should see the element     jQuery = th:contains("Subcontracting") ~ td:contains("90,000")
    the user should see the element     jQuery = th:contains("Travel and subsistence") ~ td:contains("5,970")
    the user should see the element     jQuery = th:contains("Total project costs") ~ td:contains("£203,371") ~ td:contains("£207,271") ~ td:contains("£3900")

the external user should see the funding changes
    log in as a different user          &{lead_applicant_credentials}
    the user navigates to the page      ${loan_PS}/finance-check/eligibility
    the user should see the element     jQuery = p:contains("All members of your organisation can access and edit your project")
    the user clicks the button/link     link = View changes to finances
    the user should see the element     jQuery = p:contains("Funding sought: £12,000") ~ p:contains("New funding sought: £6,000")
    the user should see the element     jQuery = th:contains("Other funding (£)") ~ td:contains("2,468")
    the user should see the element     jQuery = th:contains("Contribution to project (£)") ~ td:contains("186,435") ~ td:contains("196,335") ~ td:contains("+ 9900")
    the user should see the element     jQuery = th:contains("Funding level (%)") ~ td:contains("7") ~ td:contains("4") ~ td:contains("- 3.07")
    the user should see the element     jQuery = th:contains("Total project costs") ~ td:contains("£203,371") ~ td:contains("£207,271") ~ td:contains("£3900")
    the user should see the element     jQuery = th:contains("Other costs") ~ td:contains("1,100") ~ td:contains("5,000") ~ td:contains("+ 3900")
    the user should see the element     jQuery = th:contains("Labour") ~ td:contains("3,081")
    the user should see the element     jQuery = th:contains("Overheads") ~ td:contains("0")
    the user should see the element     jQuery = th:contains("Materials") ~ td:contains("100,200")
    the user should see the element     jQuery = th:contains("Capital usage") ~ td:contains("552")
    the user should see the element     jQuery = th:contains("Subcontracting") ~ td:contains("90,000")
    the user should see the element     jQuery = th:contains("Travel and subsistence") ~ td:contains("5,970")
    the user should see the element     jQuery = th:contains("Total project costs") ~ td:contains("£203,371") ~ td:contains("£207,271") ~ td:contains("£3900")

the user marks loan as complete
    [Arguments]  ${status}  ${appl_name}
    the user selects the radio button     successful   ${status}
    the user selects the checkbox         ${status}Confirmation
    the user clicks the button/link       id = mark-as-${status}
    the user should see the element       jQuery = p:contains("Project setup is complete and was ${status}.")
    the user clicks the button/link       link = Back to project setup
    the user should see the element       jQuery = tr:contains("${appl_name}") .ifs-project-status-${status}

the user approves the spend profile
    the user navigates to the page   ${spend_profile}
    the user selects the checkbox    approvedByLeadTechnologist
    the user clicks the button/link  jQuery = button:contains("Approved")
    the user clicks the button/link  jQuery = .modal-accept-profile button:contains("Approve")
    the applicant should see the project setup complete stage enabled

the applicant should see the project setup complete stage enabled
    log in as a different user       &{lead_applicant_credentials}
    the user navigates to the page   ${loan_PS}
    the user should see the element  jQuery = .waiting span:contains("Awaiting review")
    the user clicks the button/link  link = Project setup complete
    the user navigates to the page   ${loan_PS}/setup
    the user should see the element  jQuery = h1:contains("Project setup complete")
    the user should see the element  jQuery = h2:contains("Your project will be reviewed")
    Log in as a different user       &{internal_finance_credentials}

the applicant checks for project status
    the user should see the element   jQuery = li:contains("${loan_PS_application1}") .status-and-action:contains("Live project")
    the user should see the element   jQuery = li:contains("${loan_PS_application2}") .status-and-action:contains("Unsuccessful")
    the user navigates to the page    ${loan_PS}
    the user should see the element   jQuery = .progress-list li:nth-child(6):contains("Completed")
    the user clicks the button/link   link = Project setup complete
    the user navigates to the page    ${loan_PS}/setup
    the user should see the element   jQuery = h2:contains("We have approved your loan")
    the user navigates to the page    ${server}/project-setup/project/${loan_PS_project_Id2}/setup
    the user should see the element   jQuery = h2:contains("We have not approved your loan")

the user should see the finished finance checks
    the user navigates to the page    ${loan_PS}/finance-check
    the user should see the element   jQuery = .message-alert p:contains("We have finished checking your finances.")
    the user clicks the button/link   link = finances.
    the user should see the element   jQuery = .message-alert p:contains("We have finished checking your finances.")

the user enters a value over the max funding
    the user clicks the button/link                link = Your project finances
    the user clicks the button/link                link = Your funding
    the user clicks the button/link                jQuery = button:contains("Edit your funding")
    the user enters text to a text field           id = amount  65000
    the user clicks the button/link                id = mark-all-as-complete

the user should see qualtrics survey fields
    the user should see the element     xpath = //span[contains(text(),'${EMPIRE_LTD_NAME}')]
    the user should see the element     xpath = //span[contains(text(),'60674010')]
    the user should see the element     xpath = //span[contains(text(),'${loanApplicationID}')]
