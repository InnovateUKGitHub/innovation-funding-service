*** Settings ***
Documentation     IFS-12886 Terms and Conditions of Defra
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Variables ***
${defraCompetitionName}             Farming Innovation Programme
${defraTermsAndConditionsLink}      Farming Innovation Programme (opens in a new window)
${defraTermsAndConditionsTitle}     Terms and conditions of an Innovate UK Grant Award


*** Test Cases ***
Defra terms and conditions not pre selected for any funding or competition type
    [Documentation]  IFS-12886
    Given the user navigates to the page             ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     ${defraCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  STATE_AID  GRANT
    When the user clicks the button/link             link = Terms and conditions
    Then the user should see the element             jQuery = label:contains("Farming Innovation Programme"):not(:checked)

Comp admin marks Defra terms and conditions section as complete
    [Documentation]  IFS-12886
    When the user clicks the button twice     jQuery = label:contains("Farming Innovation Programme")
    And the user clicks the button/link       jQuery = button:contains("Done")
    And the user clicks the button/link       link = Back to competition details
    Then the user should see the element      jQuery = li:contains("Terms and conditions") .task-status-complete

Defra terms and conditions are correct
    [Documentation]  IFS-12886
    Given the user clicks the button/link                link = Terms and conditions
    When the user clicks the button/link                 link = ${defraTermsAndConditionsLink}
    And select window                                    title = ${defraTermsAndConditionsTitle} - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("${defraTermsAndConditionsTitle}")
    [Teardown]   the user closes the last opened tab


*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database