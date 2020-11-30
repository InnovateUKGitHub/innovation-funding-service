*** Settings ***
Documentation     IFS-8638: Create new competition type
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***

*** Test Cases ***
#User can select the competition type option Heukar in initial details of competition setup
#    [Documentation]  IFS-8638
#    Given the user navigates to competition setup
#    When the user clicks the button/link                         link = Initial details
#    Then the user selects the option from the drop-down menu     ${competitionType}  id = competitionTypeId

User can setup Heukar competition
    [Documentation]  IFS-8638
    Given the user logs-in in new browser               &{Comp_admin1_credentials}
    Then the competition admin creates HEUKAR competition      ${BUSINESS_TYPE_ID}  ${heukarCompetitionName}  ${compType_HEUKAR}  ${compType_HEUKAR}  2  GRANT  RELEASE_FEEDBACK  no  1  false  single-or-collaborative

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom Suite Teardown
    the user closes the browser
    Disconnect from database
