*** Settings ***
Documentation     IFS-11664: Hydrogen - New Terms and Conditions for BEIS 21/04
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Variables ***
${hydrogenCompetitionName}             BEIS Net Zero Competition
${hydrogenTermsAndConditionsLink}      BEIS Net Zero (opens in a new window)
${hydrogenTermsAndConditionsTitle}     BEIS Net Zero Hydrogen Fund Terms and Conditions


*** Test Cases ***
Hydrogen Grant erms and conditions not pre selected for any funding or competition type
    [Documentation]  IFS-11664
    Given the user navigates to the page             ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     ${hydrogenCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  STATE_AID  GRANT
    When the user clicks the button/link             link = Terms and conditions
    Then the user should see the element             css = [id="termsAndConditionsId7"]:not(:checked) ~ label

Comp admin marks Hydrogen Grant terms and conditions section as complete
    [Documentation]  IFS-11664
    When the user clicks the button twice     jQuery = label:contains("BEIS Net Zero")
    And the user clicks the button/link       jQuery = button:contains("Done")
    And the user clicks the button/link       link = Back to competition details
    Then the user should see the element      jQuery = li:contains("Terms and conditions") .task-status-complete

Hydrogen Grant terms and conditions are correct
    [Documentation]  IFS-11664
    Given the user clicks the button/link                link = Terms and conditions
    When the user clicks the button/link                 link = ${hydrogenTermsAndConditionsLink}
    And select window                                    title = ${hydrogenTermsAndConditionsTitle} - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("${hydrogenTermsAndConditionsTitle}")
    [Teardown]   the user closes the last opened tab


*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database