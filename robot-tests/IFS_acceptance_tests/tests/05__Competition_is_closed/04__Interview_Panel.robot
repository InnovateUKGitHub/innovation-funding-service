*** Settings ***
Documentation     IFS-2637 Manage interview panel link on competition dashboard - Internal
...
...               IFS-2633 Manage interview panel dashboard - Internal
Suite Setup       Custom Suite Setup
Suite Teardown    The user closes the browser
Force Tags        CompAdmin  Assessor
Resource          ../../resources/defaultResources.robot


*** Variables ***

*** Test Cases ***
User navigates to the Manage interview panel
    [Documentation]  IFS-2633 IFS-2637
    [Tags]
    Given the user clicks the button/link  link=${CLOSED_COMPETITION_NAME}
    When enable Interview panel for the competition
    Then the user clicks the button/link   jQuery=a:contains("Manage interview panel")
    And the user should see the element    jQuery=h1:contains("Manage interview panel")
    And the user should see the element    jQuery=a:contains("Assign applications")[aria-disabled="true"]
    And the user should see the element    jQuery=a:contains("Invite assessors")[aria-disabled="true"]
    And the user should see the element    jQuery=a:contains("Allocate applications to assessors")[aria-disabled="true"]
    #TODO The above tests will need to be removed once the links are active.

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}

enable Interview panel for the competition
    the user clicks the button/link    link=View and update competition setup
    the user clicks the button/link    link=Assessors
    the user clicks the button/link    jQuery=button:contains("Edit")
    the user selects the radio button  hasInterviewStage  hasInterviewStage-0
    the user clicks the button/link    jQuery=button:contains("Done")
    the user clicks the button/link    link=Competition setup
    the user clicks the button/link    link=All competitions
    the user clicks the button/link    link=${CLOSED_COMPETITION_NAME}