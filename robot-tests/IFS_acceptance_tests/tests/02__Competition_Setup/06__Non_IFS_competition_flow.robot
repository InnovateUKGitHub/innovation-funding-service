*** Settings ***
Documentation     INFUND-7963: Create Non-IFS tab in 'Competition dashboard' for adding non-IFS competitions to front door search
...
...               INFUND-7964: Create Non-IFS 'Competition details page' for adding non-IFS competitions to front door
...
...               INFUND-7965: Update 'Public content' for adding non-IFS competitions to front door
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot

*** Test Cases ***
Create new non-IFS competition
    [Documentation]    INFUND-7963 INFUND-7964
    When the user clicks the button/link    jQuery=a:contains(Non-IFS)    # We have used the JQuery selector for the link because the title will change according to the competitions number
    And the user clicks the button/link     link=Create non-IFS competition
    Then the user should not see the text in the page       Non-IFS competition details
    When the user clicks the button/link    link=Back to all competitions
    And the user clicks the button/link    jQuery=a:contains(Non-IFS)
    Then the user should not see the element  link=No competition title defined

Validation errors on non-IFS competition details
    [Documentation]    INFUND-7964
    When the user clicks the button/link    link=No competition title defined
    Then the user fills out invalid non-ifs details



*** Keywords ***
the user fills out invalid non-ifs details
    When the user enters text to a text field   id=heading-0    Heading 1
    And the user enters text to a text field    jQuery=.editor:eq(0)     Content 1



