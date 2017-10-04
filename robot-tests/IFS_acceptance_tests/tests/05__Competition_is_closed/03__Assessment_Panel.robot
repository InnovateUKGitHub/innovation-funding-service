*** Settings ***
Documentation     IFS-786 Assessment panels - Manage assessment panel link on competition dashboard
...
...               IFS-31 Assessment panels - Invite assessors to panel- Find and Invite Tabs
...               IFS-1564 Assessment panels - Invite assessors to panel - Key statistics
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Variables ***
${assessment_panel}  ${server}/management/assessment/panel/competition/${CLOSED_COMPETITION}
*** Test Cases ***
Assement panel link is deactivated if the assessment panel is not set
    [Documentation]  IFS-786
    [Tags]
    Given The user clicks the button/link  link=${CLOSED_COMPETITION_NAME}
    Then the user should see the element   jQuery=.disabled:contains("Manage assessment panel")

Assessment panel links are active if the assessment panel has been set
    [Documentation]  IFS-786
    [Tags]
    [Setup]  enable assessment panel for the competition
    Given the user clicks the button/link  link=Manage assessment panel
    When the user clicks the button/link   link=Invite assessors to attend
    Then the user should see the element   jQuery=h1:contains("Invite assessors to panel")

CompAdmin can add an assessor to invite list
    [Documentation]  IFS-31  IFS-1564
    [Tags]
    Given the user clicks the button/link    jQuery=tr:contains("Benjamin Nixon") label
    When the user clicks the button/link     jQuery=button:contains("Add selected to invite list")
    Then the user should see the element     jQuery=td:contains("Benjamin Nixon") + td:contains("benjamin.nixon@gmail.com")
    And the user clicks the button/link      link=Find
    And the user should not see the element  jQuery=td:contains("Benjamin Nixon")
    And the user should see the element      jQuery=.column-quarter:contains("0") small:contains("Invited")
    And the user should see the element      jQuery=.column-quarter:contains("0") small:contains("Pending")

Bulk add assessor to invite list
    [Documentation]  IFS-31
    [Tags]
    Given the user selects the checkbox  select-all-check
    And the user clicks the button/link     jQuery=button:contains("Add selected to invite list")
    And the user should see the element     jQuery=td:contains("Joel George") + td:contains("joel.george@gmail.com")
    When the user clicks the button/link    link=Find
    Then the user should see the element    jQuery=td:contains("No available assessors found")


*** Keywords ***

enable assessment panel for the competition
    the user clicks the button/link  link=View and update competition setup
    the user clicks the button/link  link=Assessors
    the user clicks the button/link  jQuery=button:contains("Edit")
    the user selects the radio button  hasAssessmentPanel  hasAssessmentPanel-0
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user clicks the button/link  link=All competitions
    the user clicks the button/link  link=${CLOSED_COMPETITION_NAME}
