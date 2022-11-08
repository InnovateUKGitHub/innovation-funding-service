*** Settings ***
Documentation     IFS-10926: HECP T&C changes
...
...               IFS-10696: HECP Phase 1 Terms and conditions content
...
...               IFS-13212 HECP Phase 2 - Horizon Europe Subsidy Basis T&Cs
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Variables ***
${hecpKICsCompetitionName}                      HECP KICs competition
${hecpCompetitionName}                          HECP competition
${hecpSubsidyControlCompetitionName}            HECP competition
${hecpKicsTermsAndConditionsLink}               Horizon Europe Guarantee – EIT KICs 2021 (opens in a new window)
${hecpKicsTermsAndConditionsTitle}              Terms and conditions for Horizon Europe Guarantee – EIT KICs 2021 grant - Innovation Funding Service
${hecpTermsAndConditionsLink}                   Horizon Europe Guarantee (opens in a new window)
${hecpTermsAndConditionsTitle}                  Terms and conditions for Horizon Europe Guarantee grant - Innovation Funding Service
${hecpSubsidyControlTermsAndConditionsLink}     Horizon Europe Guarantee - Subsidy control (opens in a new window)
${hecpSubsidyControlTermsAndConditionsTitle}    Terms and conditions for Horizon Europe Guarantee grant - Innovation Funding Service


*** Test Cases ***
HECP terms and conditions pre selected for hecp funding or competition type
    [Documentation]  IFS-10696
    Given the user navigates to the page             ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     ${hecpKICsCompetitionName}  ${month}  ${nextyear}  ${compType_HESTA}  STATE_AID  HECP
    When the user clicks the button/link             link = Terms and conditions
    Then the user should see the element             css = [id="termsAndConditionsId12"]:checked ~ label

Comp admin marks HECP terms and conditions section as complete
    [Documentation]  IFS-10926
    When the user selects the radio button    termsAndConditionsId    termsAndConditionsId12
    Then the user clicks the button/link      jQuery = button:contains("Done")
    And the user clicks the button/link       link = Back to competition details
    Then the user should see the element      jQuery = li:contains("Terms and conditions") .task-status-complete

HECP terms and conditions are correct
    [Documentation]  IFS-10926
    Given the user clicks the button/link                link = Terms and conditions
    When the user clicks the button/link                 link = ${hecpTermsAndConditionsLink}
    And select window                                    title = ${hecpTermsAndConditionsTitle}
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions for Horizon Europe Guarantee grant")
    [Teardown]   the user closes the last opened tab

HECP KICs terms and conditions not pre selected for any funding or competition type
    [Documentation]  IFS-10926
    Given the user navigates to the page             ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     ${hecpCompetitionName}  ${month}  ${nextyear}  ${compType_HORIZON_EUROPE}  STATE_AID  GRANT
    When the user clicks the button/link             link = Terms and conditions
    Then the user should see the element             css = [id="termsAndConditionsId14"]:not(:checked) ~ label

Comp admin marks HECP terms and conditions section as complete
    [Documentation]  IFS-10926
    When the user clicks the button twice     jQuery = label:contains("Horizon Europe Guarantee – EIT KICs 2021")
    And the user clicks the button/link       jQuery = button:contains("Done")
    And the user clicks the button/link       link = Back to competition details
    Then the user should see the element      jQuery = li:contains("Terms and conditions") .task-status-complete

HECP EIT KICs terms and conditions are correct
    [Documentation]  IFS-10926
    Given the user clicks the button/link                link = Terms and conditions
    When the user clicks the button/link                 link = ${hecpKicsTermsAndConditionsLink}
    And select window                                    title = Terms and conditions for Horizon Europe Guarantee – EIT KICs 2021 grant - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions for Horizon Europe Guarantee – EIT")
    [Teardown]   the user closes the last opened tab

HECP subsidy control terms and conditions not pre selected for any funding or competition type
    [Documentation]  IFS-13212
    Given the user navigates to the page             ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     ${hecpKICsCompetitionName}  ${month}  ${nextyear}  ${compType_HORIZON_EUROPE}  STATE_AID  HECP
    When the user clicks the button/link             link = Terms and conditions
    Then the user should see the element             css = [id="termsAndConditionsId13"]:not(:checked) ~ label

Comp admin marks HECP terms and conditions section as complete
    [Documentation]  IFS-13212
    When the user clicks the button twice     jQuery = label:contains("Horizon Europe Guarantee - Subsidy control")
    And the user clicks the button/link       jQuery = button:contains("Done")
    And the user clicks the button/link       link = Back to competition details
    Then the user should see the element      jQuery = li:contains("Terms and conditions") .task-status-complete

HECP EIT KICs terms and conditions are correct
    [Documentation]  IFS-13212
    Given the user clicks the button/link                link = Terms and conditions
    When the user clicks the button/link                 link = ${hecpSubsidyControlTermsAndConditionsLink}
    And select window                                    title = Terms and conditions for Horizon Europe Guarantee grant - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions for Horizon Europe Guarantee grant")
    [Teardown]   the user closes the last opened tab

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database