*** Settings ***
Documentation     IFS-8638: Create new competition type
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${heukarCompTypeSelector}             dt:contains("Competition type") ~ dd:contains("${compType_HEUKAR}")

*** Test Cases ***
Comp admin can select the competition type option Heukar in Initial details on competition setup
    [Documentation]  IFS-8638
    Given the user logs-in in new browser             &{Comp_admin1_credentials}
    When the user navigates to the page               ${CA_UpcomingComp}
    And the user clicks the button/link               jQuery = .govuk-button:contains("Create competition")
    Then the user fills in the CS Initial details     ${heukarCompetitionName}  ${month}  ${nextyear}  ${compType_HEUKAR}  2  GRANT

Comp admin can view Heukar competition type in Initial details read only view
    [Documentation]  IFS-8638
    Given the user clicks the button/link    link = Initial details
    Then the user can view Heukar competition type in Initial details read only view

*** Keywords ***
the user can view Heukar competition type in Initial details read only view
    the user should see the element     jQuery = ${heukarCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Edit")
    the user should see the element     jQuery = ${heukarCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Done")
    the user clicks the button/link     link = Back to competition details

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom Suite Teardown
    the user closes the browser
    Disconnect from database
