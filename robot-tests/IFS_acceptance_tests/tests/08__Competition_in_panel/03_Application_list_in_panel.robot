*** Settings ***
Documentation     IFS-25 Assessment panels - Applications list


Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Resource          ../../resources/defaultResources.robot
Force Tags


*** Variables ***


*** Test Cases ***
Assign application link decativated if competition is in close state
    [Documentation]   IFS-25
    [Tags]
    Given the user clicks the button/link   link=${CLOSED_COMPETITION_NAME}
    When the user clicks the button/link     link=Manage assessment panel
    Then the user should see the element     jQuery=.disabled:contains("Assign applications to panel")

Assign application link activate if competition is in panel state
    [Documentation]   IFS-25
    [Tags]
    [Setup]  the user move the closed competition to in panel
    Given the assessment panel period changes in the db
    When the user clicks the button/link                        link=Manage assessment panel
    #Then the user should see the element                        jQuery=.disabled:contains("Assign applications to panel")

*** Keywords ***

the user move the closed competition to in panel
    the user clicks the button/link     jQuery=button:contains("Notify assessors")
    the user clicks the button/link     jQuery=button:contains("Close assessment")

the assessment panel period changes in the db
    Connect to Database    @{database}
    Execute sql string    UPDATE `${database_name}`.`competition` SET `has_assessment_panel`=1 WHERE name="${CLOSED_COMPETITION_NAME}";
