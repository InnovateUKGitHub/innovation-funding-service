*** Settings ***
Documentation     IFS-9468: New set of terms & conditions required in IFS - ATF
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Variables ***
${atfCompetitionName}             ATF competition
${atfTermsAndConditionsLink}      Automotive Transformation Fund (ATF) (opens in a new window)
${atfTermsAndConditionsTitle}     Innovate UK terms and conditions of an Automotive Transformation Fund (ATF) competition


*** Test Cases ***
ATF terms and conditions not pre selected for any funding or competition type
    [Documentation]  IFS-9468
    Given the user navigates to the page             ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     ${atfCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  STATE_AID  GRANT
    When the user clicks the button/link             link = Terms and conditions
    Then the user should see the element             css = [id="termsAndConditionsId6"]:not(:checked) ~ label

Comp admin marks ATF terms and conditions section as complete
    [Documentation]  IFS-9468
    When the user clicks the button twice     jQuery = label:contains("Automotive Transformation Fund (ATF)")
    And the user clicks the button/link       jQuery = button:contains("Done")
    And the user clicks the button/link       link = Back to competition details
    Then the user should see the element      jQuery = li:contains("Terms and conditions") .task-status-complete

ATF terms and conditions are correct
    [Documentation]  IFS-9468
    Given the user clicks the button/link                link = Terms and conditions
    When the user clicks the button/link                 link = ${atfTermsAndConditionsLink}
    And select window                                    title = ${atfTermsAndConditionsTitle} - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("${atfTermsAndConditionsTitle}")
    [Teardown]   the user closes the last opened tab


*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database