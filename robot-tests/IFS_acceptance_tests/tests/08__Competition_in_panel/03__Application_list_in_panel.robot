*** Settings ***
Documentation     IFS-25 Assessment panels - Applications list
...
...               IFS-2049 Assessment panels - Filter on applications list
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Resource          ../../resources/defaultResources.robot

*** Variables ***
${Neural_network}   ${application_ids["Neural networks to optimise freight train routing"]}

*** Test Cases ***
Assign application link decativated if competition is in close state
    [Documentation]   IFS-25
    [Tags]
    [Setup]  activate manage assessment panel link in the db
    Given the user clicks the button/link                    link=${CLOSED_COMPETITION_NAME}
    When the user clicks the button/link                     link=Manage assessment panel
    Then the user should see the element                     jQuery=.disabled:contains("Assign applications to panel")

Assign application link activate if competition is in panel state
    [Documentation]   IFS-25
    [Tags]
    [Setup]  the user move the closed competition to in panel
    Given activate manage assessment panel link in the db
    When the user clicks the button/link                      link=Manage assessment panel
    And the user clicks the button/link                       jQuery=a:contains("Assign applications to panel")
    Then the user should see the element                      jQuery=h1:contains("Assign applications to panel")

Filter by application number
    [Documentation]  IFS-2049
    [Tags]
    Given the user enters text to a text field     id=filterSearch   ${Neural_network}
    When the user clicks the button/link           jQuery=.button:contains("Filter")
    Then the user should see the element           jQuery=td:contains("Neural networks to optimise freight train routing")
    #TODO IFS-2069 need to add more checks once the webtest data is ready.

*** Keywords ***
the user move the closed competition to in panel
    the user clicks the button/link     link=Competition
    the user clicks the button/link     jQuery=button:contains("Notify assessors")
    the user clicks the button/link     jQuery=button:contains("Close assessment")

activate manage assessment panel link in the db
    Connect to Database    @{database}
    Execute sql string    UPDATE `${database_name}`.`competition` SET has_assessment_panel=1 WHERE name='${CLOSED_COMPETITION_NAME}';
