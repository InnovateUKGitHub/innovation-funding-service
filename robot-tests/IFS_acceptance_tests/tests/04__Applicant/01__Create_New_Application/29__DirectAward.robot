*** Settings ***
Documentation     IFS-11682  Direct Award: New competition type
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown

Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/defaultResources.robot


*** Variables ***
${openEndedCompName}               Open ended Direct Award competition


*** Test Cases ***
the user creates a new open ended competiton
    [Documentation]  IFS-11682
    Given The user logs-in in new browser           &{Comp_admin1_credentials}
    When the user navigates to the page             ${CA_UpcomingComp}
    And the user clicks the button/link             jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details    ${openEndedCompName}  ${month}  ${nextyear}  ${compType_DirectAward}  STATE_AID  GRANT
    And the user clicks the button/link             link = Initial details
    Then the user should see the element            jQuery = dt:contains("Competition type") ~ dd:contains("Direct award")

the user should see open ended is selected by default for direct award competition type
    [Documentation]  IFS-11682
    When the user clicks the button/link                  link = Back to competition details
    And the user completes milestones
    Then the user sees that the radio button is selected  alwaysOpen  true

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The guest user opens the browser

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the user completes milestones
    the user clicks the button/link     link = Milestones
    the user clicks the button twice    jQuery = label:contains("Project setup")
    the user clicks the button/link     jQuery = button:contains("Done")

