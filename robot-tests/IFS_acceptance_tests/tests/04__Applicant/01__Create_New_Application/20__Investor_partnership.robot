*** Settings ***
Documentation     IFS-7213  Investor Partnerships Funding type & T&Cs
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Test Cases ***
Investor partnership initial details
    [Documentation]  IFS-7213
    Given the user fills in initial details
    When the user clicks the button/link      link = Initial details
    Then the user should see the element      jQuery = dt:contains("Funding type") ~ dd:contains("Investor Partnership")

Edit view of initial details
    [Documentation]  IFS-7213
    Given the user clicks the button/link    css = button[type="submit"]
    Then the user should see the element     jQuery = dt:contains("Funding type") ~ dd:contains("Investor Partnership")
    [Teardown]   navigate to comp setup of investor comp

Creating a new investor comp points to the correct T&C
    [Documentation]  IFS-7213
    Given the user fills in initial details
    When the user clicks the button/link      link = Terms and conditions
    Then the user sees that the radio button is selected    termsAndConditionsId  termsAndConditionsId11

The Investor partnership t&c's are correct
    [Documentation]  IFS-7213
    When the user clicks the button/link       link = Investor Partnerships
    Then the user should see the element       jQuery = h1:contains("Terms and conditions of an Innovate UK investor partnerships competition")
    [Teardown]   the user goes back to the previous page

T&c's can be confirmed
    [Documentation]  IFS-7213
    Given the user clicks the button/link    jQuery = button:contains("Done")
    When the user clicks the button/link     link = Competition setup
    Then the user should see the element     jQuery = li:contains("Terms and conditions") .task-status-complete

Applicant is able to see correct T&C's
    [Documentation]  IFS-7213
    Given Log in as a different user         ${peter_styles_email}  ${short_password}
    when the user clicks the button/link     link = Investor partnership
    And the user clicks the button/link      link = Investor Partnerships terms and conditions
    Then the user should see the element     jQuery = h2:contains("Terms and conditions of an Innovate UK investor partnerships competition")

Internal user sees correct label for T&C's
    [Documentation]
    Given Log in as a different user

*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails

the user fills in initial details
    the user navigates to the page              ${CA_UpcomingComp}
    the user clicks the button/link             jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details    Investor comp  ${month}  ${nextyear}  ${compType_Programme}  1  INVESTOR_PARTNERSHIPS

navigate to comp setup of investor comp
    the user clicks the button/link             jQuery = button:contains("Done")
    the user clicks the button/link             link = Competition setup
