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
${continuityLoanPSCompName}          Innovation continuity loan competition
${continuityLoanPSApplication}       Innovation continuity loan ps application
${continuityLoanCompId}              ${competition_ids["${continuityLoanCompName}"]}
${continuityLoanPSCompId}            ${competition_ids["${continuityLoanPSCompName}"]}
${continuityLoanPSApplicationId}     ${application_ids["${continuityLoanPSApplication}"]}
${continuityLoanApplicationLink}     ${server}/management/competition/${continuityLoanPSCompId}/application/${continuityLoanPSApplicationId}
${continuityLoanFeedbackLink}        ${server}/application/${continuityLoanPSApplicationId}/feedback
${continuityLoanT&C'sSubTitle}       General terms and conditions of an innovation continuity
${continuityLoanT&C'sTitle}          Loans terms and conditions
${continuityLoanT&CLink}             Innovation Continuity Loan
${applicationT&CLink}                Award terms and conditions



*** Test Cases ***
Innovation continuity loan T&C's can be confirmed
    [Documentation]  IFS-8002
    Given the user fills in initial details
    When the user clicks the button/link             link = Terms and conditions
    And the user confirmed terms and conditions
    Then the user should see the element             link = ${continuityLoanT&CLink}
    And the user should see the element              jQuery = p:contains("These are the terms and conditions applicants must accept for this competition.")

Innovation continuity loan T&C's can be edited
    [Documentation]  IFS-8002
    Given the user clicks the button/link       jQuery = button:contains("Edit")
    When the user selects the radio button      termsAndConditionsId  termsAndConditionsId12
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then the user should see the element        link = ${continuityLoanT&CLink}

Internal user is able to see correct T&C's
    [Documentation]  IFS-8002
    Given the user clicks the button/link     link = ${continuityLoanT&CLink}
    Then the user should see the element      jQuery = h1:contains("${continuityLoanT&C'sSubTitle}")

Lead applicant is able to see correct T&C's
    [Documentation]  IFS-8002
    [Setup]  Update the competition with innovation continuity loan T&C's     ${continuityLoanCompId}
    Given Log in as a different user               &{lead_applicant_credentials}
    And Existing user starts a new application     ${continuityLoanCompName}
    When the user clicks the button/link           link = ${applicationT&CLink}
    Then the user should see the element           jQuery = h1:contains("${continuityLoanT&C'sSubTitle}")
    And the user should see the element            jQuery = h1:contains("${continuityLoanT&C'sTitle}")

Lead applicant can confirm T&C's
    [Documentation]  IFS-8002
    Given the user selects the checkbox      agreed
    When the user clicks the button/link     css = button[type="submit"]
    And the user clicks the button/link      link = Return to application overview
    Then the user should see the element     jQuery = li:contains("${applicationT&CLink}") .task-status-complete

Partner applicant is able to see correct T&C's
    [Documentation]  IFS-8002
    [Setup]  the lead invites already registered user    ${existing_lead_ktp_email}  ${continuityLoanCompName}
    Given logging in and error checking      &{troy_ward_crendentials}
    When the user clicks the button/link     id = save-organisation-button
    And the user selects the radio button    selectedOrganisationId   ${organisationWardId} 
    And the user clicks the button/link      link = ${applicationT&CLink}
    Then the user should see the element     jQuery = h1:contains("${continuityLoanT&C'sSubTitle}")
    And the user should see the element      jQuery = h1:contains("${continuityLoanT&C'sTitle}")

Partner applicant can confirm T&C's
    [Documentation]  IFS-8002
    Given the user selects the checkbox      agreed
    When the user clicks the button/link     css = button[type="submit"]
    And the user clicks the button/link      link = Return to application overview
    Then the user should see the element     jQuery = li:contains("${applicationT&CLink}") .task-status-complete

Internal user sees correct T&C's in project setup
    [Documentation]  IFS-8002
    [Setup]  Update the competition with innovation continuity loan T&C's     ${continuityLoanPSCompId}
    Given Log in as a different user                    &{Comp_admin1_credentials}
    When the user navigates to terms and conditions
    Then the user should see the element                jQuery = h1:contains("${continuityLoanT&C'sSubTitle}")
    And the user should see the element                 jQuery = h1:contains("${continuityLoanT&C'sTitle}")

Application feedback page shows the correct T&C's
    [Documentation]  IFS-8002
    Given Log in as a different user         &{troy_ward_crendentials}
    When The user navigates to the page      ${continuityLoanFeedbackLink}
    And the user clicks the button/link      link = View award terms and conditions
    Then the user should see the element     jQuery = h1:contains("${continuityLoanT&C'sSubTitle}")
    And the user should see the element      jQuery = h1:contains("${continuityLoanT&C'sTitle}")

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser     &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the user fills in initial details
    the user navigates to the page               ${CA_UpcomingComp}
    the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details     Innovation continuity comp  ${month}  ${nextyear}  ${compType_Programme}  1  GRANT

navigate to comp setup of investor comp
    the user clicks the button/link             jQuery = button:contains("Done")
    the user clicks the button/link             link = Competition details

the user confirmed terms and conditions
    the user selects the radio button     termsAndConditionsId  termsAndConditionsId12
    the user clicks the button/link       jQuery = button:contains("Done")

the user completes the competition setup
    the user navigates to the page      ${server}/management/dashboard/upcoming
    the user clicks the button/link     link = ${continuityLoanCompName}
    the user clicks the button/link     id = compCTA
    the user clicks the button/link     jQuery = button:contains("Done")

Update the competition with innovation continuity loan T&C's
    [Arguments]  ${competitionID}
    Execute SQL String  UPDATE `${database_name}`.`competition` SET `terms_and_conditions_id`='32' WHERE `id`='${competitionID}'

the user navigates to terms and conditions
    the user navigates to the page       ${continuityLoanApplicationLink}
    the user clicks the button/link      jQuery = button:contains("${applicationT&CLink}")
    the user clicks the button/link      link = View terms and conditions