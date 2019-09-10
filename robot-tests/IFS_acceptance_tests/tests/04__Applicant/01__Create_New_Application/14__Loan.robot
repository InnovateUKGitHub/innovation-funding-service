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
Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../10__Project_setup/PS_Common.robot

*** Variables ***
${loan_comp_PS}              Project setup loan comp
${loan_comp_PS_Id}           ${competition_ids["${loan_comp_PS}"]}
${loan_PS_application1}      Loan Project 1
${loan_PS_application_Id}    ${application_ids["${loan_PS_application1}"]}
${loan_PS_project_Id}        ${project_ids["${loan_PS_application1}"]}
${loan_PS}                   ${server}/project-setup/project/${loan_PS_project_Id}
${loan_PS_Url}               ${loan_PS}/details
${loan_finance_checks}       ${server}/project-setup-management/project/${loan_PS_project_Id}/finance-check
${eligibility_changes}       ${loan_finance_checks}/organisation/${EMPIRE_LTD_ID}/eligibility/changes

*** Test Cases ***
Loan application shows correct T&C's
    [Documentation]    IFS-6205
    Given the user clicks the button/link   link = Award terms and conditions
    And the user should see the element     jQuery = h1:contains("Loans terms and conditions")
    When the user clicks the button/link     link = Back to application overview
    Then the user should see the element    jQuery = li:contains("Award terms and conditions") .task-status-complete

Loan application Your funding
    [Documentation]  IFS-6207
    Given the user enters empty funding amount
    When the user enters text to a text field  id = amount   57,803
    And the user clicks the button/link        id = mark-all-as-complete
    Then the user should see the element       jQuery = td:contains("200,903") ~ td:contains("57,803") ~ td:contains("30%") ~ td:contains("2,468") ~ td:contains("140,632")

Loan application finance overview
    [Documentation]  IFS-6208
    Given the user clicks the button/link  link = Back to application overview
    When the user clicks the button/link   link = Finances overview
    Then the user should see the element   jQuery = td:contains("200,903") ~ td:contains("57,803") ~ td:contains("30%") ~ td:contains("2,468") ~ td:contains("140,632")

Loan application submission
    [Documentation]  IFS-6237  IFS-6238
    Given the user submits the loan application
    And the user should see the element            jQuery = h2:contains("Part A: Innovation Funding Service application")
    When the user clicks the button/link           link = startup high growth index survey
    #TODO
    #the user should be on the right page.  Update once we have this link
    And the user closes the last opened tab
    When the user clicks the button/link            link = View part A
    Then the user should see the element            jQuery = h1:contains("Application overview")
    And the user reads his email                    ${lead_applicant_credentials["email"]}  Complete your application for Loan Competition  To finish your application, you must complete part B

Applicant complete the project setup details
    [Documentation]  IFS-6369
    Given the user completes the project details
    And the user completes the project team details
    And the user submits the project document

Funding sought validations
    [Documentation]  IFS-6293
    Given the user selects to change funding sought
    When the user enters text to a text field           id = partners[${EMPIRE_LTD_ID}].funding  ${EMPTY}
    And the user clicks the button/link                 jQuery = button:contains("Save and return to finances")
    Then the user should see a field and summary error  Enter the amount of funding sought.

Found sought changes
    [Documentation]  IFS-6293
    Given the user enters text to a text field   id = partners[${EMPIRE_LTD_ID}].funding  6000
    When the user clicks the button/link         jQuery = button:contains("Save and return to finances")
    Then the user should see the element         jQuery = h3:contains("Finances summary") ~ div td:contains("£200,903") ~ td:contains("4%") ~ td:contains("6,000") ~ td:contains("2,468") ~ td:contains("192,435")
    And the internal user should see the funding changes

Project finance completes all project setup steps
    [Documentation]  IFS-6369
    Given internal user approve project documents
    And internal user assign MO to loan project
    And internal user generate SP

Applicant checks the generated SP
    [Documentation]  IFS-6369
    Given log in as a different user       &{lead_applicant_credentials}
    When the user navigates to the page    ${loan_PS}/partner-organisation/${EMPIRE_LTD_ID}/spend-profile/review
    Then the user should not see the financial year table on SP

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
    the user clicks the button/link           jQuery = button:contains("Yes, I want to submit my application")

the user completes the project details
    log in as a different user         &{lead_applicant_credentials}
    the user navigates to the page     ${loan_PS_Url}
    the user clicks the button/link    link = Correspondence address
    the user enter the Correspondence address
    the user clicks the button/link    link = Return to set up your project
    the user should see the element    css = ul li.complete:nth-child(1)

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

the user submits the project document
    the user navigates to the page       ${loan_PS}/document/all
    the user clicks the button/link      link = Exploitation plan
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user clicks the button/link      id = submitDocumentButton
    the user clicks the button/link      id = submitDocumentButtonConfirm
    the user goes to documents page      Back to document overview  Set up your project
    the user should see the element      jQuery = li:contains("Documents") span:contains("Awaiting review")

internal user approve project documents
    Log in as a different user            &{internal_finance_credentials}
    the user navigates to the page        ${server}/project-setup-management/project/${loan_PS_project_Id}/document/all
    the user clicks the button/link       link = Exploitation plan
    internal user approve uploaded documents

internal user assign MO to loan project
    the user navigates to the page           ${server}/project-setup-management/project/${loan_PS_project_Id}/monitoring-officer
    Search for MO                            Orvill  Orville Gibbs
    The internal user assign project to MO   ${loan_PS_application_Id}  ${loan_PS_application1}

internal user generate SP
    the user navigates to the page           ${server}/project-setup-management/project/${loan_PS_project_Id}/finance-check
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
    the user clicks the button/link          link = Return to finance checks
    the user clicks the button/link          css = .generate-spend-profile-main-button
    the user clicks the button/link          css = #generate-spend-profile-modal-button

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
    the user clicks the button/link       link = Review and send total project spend profile
    the user should not see the element   jQuery = h2:contains("Project costs for financial year")
    the user should not see the element   jQuery = th:contains("Financial year ") ~ th:contains("Project spend")
    the user clicks the button/link       link = Send project spend profile
    the user clicks the button/link       id = submit-send-all-spend-profiles

the user selects to change funding sought
    log in as a different user       &{internal_finance_credentials}
    the user navigates to the page   ${loan_finance_checks}
    the user clicks the button/link  link = View finances
    the user clicks the button/link  link = Change funding sought

the internal user should see the funding changes
    the user navigates to the page    ${eligibility_changes}
    the user should see the element   jQuery = p:contains("Submitted funding sought: £12,000") ~ p:contains("Changed funding sought: £6,000")