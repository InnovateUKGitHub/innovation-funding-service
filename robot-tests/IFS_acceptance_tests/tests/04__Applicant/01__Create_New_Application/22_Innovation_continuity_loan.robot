*** Settings ***
Documentation     IFS-8002  New set of T&Cs for innovation continuity loan
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${continuityLoanCompName}            Innovation continuity loan
${continuityLoanCompId}              ${competition_ids["${continuityLoanCompName}"]}
${continuityLoanApplication}         Innovation continuity loan application
${continuityLoanApplicationId}       ${application_ids["${continuityLoanApplication}"]}
${continuityLoanT&CLink}             Innovation Continuity Loan
${continuityLoanPSApplication}       Innovation continuity loan ps application
${continuityLoanPSApplicationId}     ${application_ids["${continuityLoanPSApplication}"]}
${continuityLoanApplicationLink}     ${server}/management/competition/${continuityLoanCompId}/application/${continuityLoanApplicationId}
${continuityLoanFeedbackLink}        ${server}/application/${continuityLoanPSApplicationId}/feedback
${continuityLoanT&C'sSubTitle}       General terms and conditions of an innovation continuity loan from Innovate UK Loans Limited.
${continuityLoanT&CLink}             Award terms and conditions


*** Test Cases ***
#Investor partnership initial details
#    [Documentation]  IFS-7213
#    Given the user fills in initial details
#    When the user clicks the button/link      link = Initial details
#    Then the user should see the element      jQuery = dt:contains("Funding type") ~ dd:contains("Investor Partnership")
#
#Edit view of initial details
#    [Documentation]  IFS-7213
#    Given the user clicks the button/link     css = button[type="submit"]
#    Then the user should see the element      jQuery = dt:contains("Funding type") ~ dd:contains("Investor Partnership")
#    [Teardown]   navigate to comp setup of investor comp

Innovation continuity loan T&C's can be confirmed
    [Documentation]  IFS-8002
    Given the user fills in initial details
    When the user clicks the button/link        link = Terms and conditions
    And the user confirmed T&C's
    Then the user should see the element        link = Innovation Continuity Loan
    And the user should see the element         jQuery = p:contains("These are the terms and conditions applicants must accept for this competition.")

Innovation continuity loan T&C's can be edited
    [Documentation]  IFS-8002
    [Setup]  Change the milestone in the database to tomorrow     ${continuityLoanCompId}    OPEN_DATE
    Given the user navigates to the page     ${server}/management/competition/setup/${continuityLoanCompId}
    When the user edited T&C's
    Then the user should see the element     link = Innovation Continuity Loan

Internal user is able to see correct T&C's and completes the competition setup
    [Documentation]  IFS-8002
    Given the user clicks the button/link            link = Innovation Continuity Loan
    Then the user should see the element             jQuery = h1:contains("${continuityLoanT&C'sSubTitle}")
    And the user completes the competition setup

Applicant is able to see correct T&C's
    [Documentation]  IFS-8002
    [Setup]  update milestone to yesterday     ${continuityLoanCompId}    OPEN_DATE
    Given Log in as a different user         ${peter_styles_email}  ${short_password}
    when the user clicks the button/link     link = ${continuityLoanApplication}
    And the user navigates to the page       ${server}/application/${continuityLoanApplicationId}/form/question/2071/terms-and-conditions
    Then the user clicks the button/link     jQuery = ${continuityLoanT&C'sSubTitle}
    Then the user should see the element     jQuery = h1:contains("Loans terms and conditions")

Applicant can confirm t&c's
    [Documentation]  IFS-7235
    Given the user selects the checkbox      agreed
    When the user clicks the button/link     css = button[type="submit"]add
    And the user clicks the button/link      link = Back to application overview
    Then the user should see the element     jQuery = li:contains("${continuityLoanT&CLink}") .task-status-complete

Internal user sees correct label for T&C's
    [Documentation]  IFS-7235
    Given Log in as a different user         &{Comp_admin1_credentials}
    When the user navigates to the page      ${investorApplicationLink}
    Then the user should see the element     jQuery = button:contains("${continuityLoanT&CLink}")

Application feedback page shows the correct link for t&c's
    [Documentation]  IFS-7235
    Given Log in as a different user         &{troy_ward_crendentials}
    When The user navigates to the page      ${investorFeedbackLink}
    Then the user should see the element     link = View ${tandcLink}

*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails

the user fills in initial details
    the user navigates to the page               ${CA_UpcomingComp}
    the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details     Investor comp  ${month}  ${nextyear}  ${compType_Programme}  1  INVESTOR_PARTNERSHIPS

navigate to comp setup of investor comp
    the user clicks the button/link             jQuery = button:contains("Done")
    the user clicks the button/link             link = Competition details

the user confirmed T&C's
    the user clicks the button/link                     link = Terms and conditions
    the user sees that the radio button is selected     termsAndConditionsId  termsAndConditionsId12
    the user clicks the button/link                     jQuery = button:contains("Done")

the user edited T&C's
    the user clicks the button/link                     link = Terms and conditions
    the user clicks the button/link                     jQuery = button:contains("Edit")
    the user sees that the radio button is selected     termsAndConditionsId  termsAndConditionsId12
    the user clicks the button/link                     jQuery = button:contains("Done")

the user completes the competition setup
    the user clicks the button/link     link = Competition details
    the user clicks the button/link     id = compCTA
    the user clicks the button/link     jQuery = button:contains("Done")
