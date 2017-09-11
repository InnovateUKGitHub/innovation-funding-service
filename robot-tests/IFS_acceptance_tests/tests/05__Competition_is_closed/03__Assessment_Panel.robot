*** Settings ***
Documentation     IFS-786
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

Assement panel link is active if the assesment panel is set
    [Documentation]  IFS-786
    [Tags]
    [Setup]  enable assessment panel for the competition
    Given the user clicks the button/link  link=Manage assessment panel
    Then the user navigates to the page  ${assessment_panel}

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
