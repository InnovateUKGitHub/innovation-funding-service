*** Settings ***
Documentation     IFS-8164  KTP AFRICA - T&Cs
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${ktpAfricaCompName}            KTP Africa Comp
${ktpAfricaCompId}              ${competition_ids["${ktpAfricaCompName}"]}
${ktpAfricaApplication}         KTP africa application
${ktpAfricaApplicationId}       ${application_ids["${ktpAfricaApplication}"]}
${ktpAfricatandcLink}           Award terms and conditions
${ktpAfricaPSApplication}       KTP africa ps application
${ktpAfricaPSApplicationId}     ${application_ids["${investorPSApplication}"]}
${ktpAfricaApplicationLink}     ${server}/management/competition/${ktpAfricaCompId}/application/${ktpAfricaApplicationId}
${ktpAfricaFeedbackLink}        ${server}/application/${ktpAfricaPSApplicationId}/summary


*** Test Cases ***
Creating a new KTP africa comp to confirm T&c's
    [Documentation]  IFS-8164
    Given the user fills in initial details
    When the user clicks the button/link        link = Terms and conditions
    And the user selects the radio button       termsAndConditionsId  termsAndConditionsId13
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then the user should see the element        link = Knowledge Transfer Partnership (KTP) Africa

KTP africa t&c's are correct
    [Documentation]  IFS-8164
    When the user clicks the button/link     link = Knowledge Transfer Partnership (KTP) Africa
    Then the user should see the element     jQuery = h1:contains("Terms and conditions of an African Agriculture Knowledge Transfer Partnership award")
    [Teardown]   the user goes back to the previous page

T&c's section should be completed
    [Documentation]  IFS-8164
    When the user clicks the button/link     link = Competition details
    Then the user should see the element     jQuery = li:contains("Terms and conditions") .task-status-complete

Applicant is able to see correct T&C's
    [Documentation]  IFS-8164
    [Setup]  Update the competition with KTP africa T&C's     ${ktpAfricaCompId}
    Given Log in as a different user         ${peter_styles_email}  ${short_password}
    when the user clicks the button/link     link = KTP africa application
    And the user clicks the button/link      link = ${ktpAfricatandcLink}
    Then the user should see the element     jQuery = h2:contains("Terms and conditions of an Innovate UK investor partnerships competition")

Applicant can confirm t&c's
    [Documentation]  IFS-8164
    Given the user selects the checkbox      agreed
    When the user clicks the button/link     css = button[type="submit"]
    And the user clicks the button/link      link = Back to application overview
    Then the user should see the element     jQuery = li:contains("${ktpAfricatandcLink}") .task-status-complete

Internal user sees correct label for T&C's
    [Documentation]  IFS-8164
    Given Log in as a different user         &{Comp_admin1_credentials}
    When the user navigates to the page      ${ktpAfricaApplicationLink}
    Then the user should see the element     jQuery = button:contains("${ktpAfricatandcLink}")

Application feedback page shows the correct link for t&c's
    [Documentation]  IFS-8164
    Given Log in as a different user         &{troy_ward_crendentials}
    When The user navigates to the page      ${ktpAfricaFeedbackLink}
    And the user clicks the button/link      jQuery = button:contains("Investor Partnerships terms and conditions")
    Then the user should see the element     link = View ${ktpAfricatandcLink}

*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails

the user fills in initial details
    the user navigates to the page               ${CA_UpcomingComp}
    the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details     Investor comp  ${month}  ${nextyear}  ${compType_Programme}  1  KTP_Africa_Comp

#navigate to comp setup of investor comp
#    the user clicks the button/link             jQuery = button:contains("Done")
#    the user clicks the button/link             link = Competition details

Update the competition with KTP africa T&C's
    [Arguments]  ${competitionID}
    Execute SQL String  UPDATE `${database_name}`.`competition` SET `terms_and_conditions_id`='32' WHERE `id`='${competitionID}'