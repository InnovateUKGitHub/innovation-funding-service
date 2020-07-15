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
Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Resource        ../../../resources/defaultResources.robot
Resource        ../../../resources/common/Applicant_Commons.robot
Resource        ../../../resources/common/PS_Common.robot

*** Variables ***
${loan_comp_PS}              Project setup loan comp
${loan_comp_PS_Id}           ${competition_ids["${loan_comp_PS}"]}
${loan_PS_application1}      Loan Project 1
${loan_PS_application2}      Loan Project 2
${loan_PS_application_Id}    ${application_ids["${loan_PS_application1}"]}
${loan_PS_project_Id}        ${project_ids["${loan_PS_application1}"]}
${loan_PS_project_Id2}       ${project_ids["${loan_PS_application2}"]}
${loan_PS}                   ${server}/project-setup/project/${loan_PS_project_Id}
${loan_PS_Url}               ${loan_PS}/details
${loan_finance_checks}       ${server}/project-setup-management/project/${loan_PS_project_Id}/finance-check
${eligibility_changes}       ${loan_finance_checks}/organisation/${EMPIRE_LTD_ID}/eligibility/changes
${spend_profile}             ${server}/project-setup-management/project/${loan_PS_project_Id}/spend-profile/approval

*** Test Cases ***
Loan application shows correct T&C's
    [Documentation]    IFS-6205
    Given the user clicks the button/link   link = Award terms and conditions
    And the user should see the element     jQuery = h1:contains("Loans terms and conditions")
    When the user clicks the button/link    link = Back to application overview
    Then the user should see the element    jQuery = li:contains("Award terms and conditions") .task-status-complete

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
    [Documentation]  IFS-6237  IFS-6238
    Given the user submits the loan application
    And the user should see the element            jQuery = h2:contains("Part A: Innovation Funding Service application")
    #When the user clicks the button/link           link = startup high growth index survey
    #TODO
    #the user should be on the right page.  Update once we have this link
    #And the user closes the last opened tab
    When the user clicks the button/link            link = View part A
    Then the user should see the element            jQuery = h1:contains("Application overview")
    And the user reads his email                    ${lead_applicant_credentials["email"]}  Complete your application for Loan Competition  To finish your application, you must complete part B

Applicant complete the project setup details
    [Documentation]  IFS-6369  IFS-6285
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

Found sought changes
    [Documentation]  IFS-6293  IFS-6298
    Given the user enters text to a text field   id = partners[${EMPIRE_LTD_ID}].funding  6000
    When the user clicks the button/link         jQuery = button:contains("Save and return to project finances")
    Then the user should see the element         jQuery = h3:contains("Finances summary") ~ div td:contains("£200,903") ~ td:contains("4.21%") ~ td:contains("6,000") ~ td:contains("2,468") ~ td:contains("192,435")
    And the internal user should see the funding changes
    And the external user should see the funding changes

Project finance completes all project setup steps
    [Documentation]  IFS-6369  IFS-6292  IFS-6307  IFS-6298  IFS-6368
    [Setup]  log in as a different user        &{internal_finance_credentials}
    Given internal user assign MO to loan project
    And internal user generate SP
    When the user navigates to the page         ${server}/project-setup-management/competition/${loan_comp_PS_Id}/status/all
    Then the user should not see the element    jQuery = th:contains("Bank details")

Applicant checks the generated SP
    [Documentation]  IFS-6369  IFS-6298
    Given log in as a different user       &{lead_applicant_credentials}
    And the user should see the finished finance checks
    When the user navigates to the page    ${loan_PS}/partner-organisation/${EMPIRE_LTD_ID}/spend-profile/review
    Then the user should not see the financial year table on SP

Internal user can mark project as successful
    [Documentation]  IFS-6363
    [Setup]  Log in as a different user     &{internal_finance_credentials}
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
    Given log in as a different user    &{lead_applicant_credentials}
    Then the applicant checks for project status

*** Keywords ***
Custom suite setup
    the user logs-in in new browser       &{lead_applicant_credentials}
    the user clicks the button/link       link = Loan Application

Custom suite teardown
    The user closes the browser

the user enters empty funding amount
    the user clicks the button/link                link = Your project finances
    the user clicks the button/link                link = Your funding
    the user clicks the button/link                jQuery = button:contains("Edit your funding")
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
    the user should see the element       jQuery = h2:Contains("Your application has progressed to project setup.") ~ .govuk-body:contains("Scores and written feedback")
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
    the user clicks the button/link     jQuery = button:contains("Save finance contact")
    the user clicks the button/link     link = Project manager
    the user selects the radio button   projectManager   projectManager1
    the user clicks the button/link     jQuery = button:contains("Save project manager")
    the user clicks the button/link     link = Set up your project
    the user should see the element     jQuery = .progress-list li:nth-child(2):contains("Completed")

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
    the user should not see the element   jQuery = h2:contains("Project costs for financial year")
    the user should not see the element   jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link       link = Edit spend profile
    the user should not see the element   jQuery = h2:contains("Project costs for financial year")
    the user should not see the element   jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link       jQuery = button:contains("Save and return to spend profile overview")
    the user clicks the button/link       jQuery = button:contains("Mark as complete")
    the user clicks the button/link       link = Empire Ltd
    the user should not see the element   jQuery = p:contains("Your submitted spend profile will be used as the base for your project spend over the following financial years.")
    the user should not see the element   jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link       link = Spend profile overview
    the user clicks the button/link       jQuery = a:contains("Review and submit project spend profile")
    the user should see the element       jQuery = h2:contains("Project - Spend profile")
    the user should not see the element   jQuery = h2:contains("Project costs for financial year")
    the user should not see the element   jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link       jQuery = a:contains("Submit project spend profile")
    the user clicks the button/link       id = submit-send-all-spend-profiles

the user selects to change funding sought
    log in as a different user       &{internal_finance_credentials}
    the user navigates to the page   ${loan_finance_checks}
    the user clicks the button/link  link = View finances
    the user clicks the button/link  link = Change funding sought

the internal user should see the funding changes
    the user navigates to the page    ${eligibility_changes}
    the user should see the element   jQuery = p:contains("Submitted funding sought: £12,000") ~ p:contains("New funding sought: £6,000")

the external user should see the funding changes
    log in as a different user        &{lead_applicant_credentials}
    the user navigates to the page    ${loan_PS}/finance-checks/eligibility
    the user should see the element   jQuery = p:contains("All members of your organisation can access and edit your project")
    the user clicks the button/link   link = View changes to finances
    the user should see the element   jQuery = p:contains("Submitted funding sought: £12,000") ~ p:contains("New funding sought: £6,000")

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
    the user navigates to the page    ${loan_PS}/finance-checks
    the user should see the element   jQuery = .message-alert p:contains("We have finished checking your finances.")
    the user clicks the button/link   link = finances.
    the user should see the element   jQuery = .message-alert p:contains("We have finished checking your finances.")