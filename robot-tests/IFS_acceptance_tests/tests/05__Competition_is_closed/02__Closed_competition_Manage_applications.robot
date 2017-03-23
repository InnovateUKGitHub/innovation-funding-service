*** Settings ***
Documentation     INFUND-7734 Competition Management: Assign to application dashboard in Closed competition
...
...               INFUND-7729 Competition management: Allocate application dashboard on Closed competition
...
...               INFUND-8061 Filter and pagination on Allocate Applications (Closed competition) and Manage applications (In assessment) dashboards
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Search for applications
    [Documentation]    INFUND-8061
    Given The user clicks the button/link    link=${CLOSED_COMPETITION_NAME}
    And the user clicks the button/Link    jQuery=a:contains("Assessor management: Assignments")
    When The user enters text to a text field    css=#filterSearch    95
    and The user clicks the button/link    jQuery=button:contains(Filter)
    Then the user should see the element    jQuery=tr:nth-child(1) td:nth-child(1):contains("95")
    And The user clicks the button/link    link=Clear all filters
    then the user should not see the element    jQuery=tr:nth-child(1) td:nth-child(1):contains("96")

Allocate Applications
    [Documentation]    INFUND-7042
    ...    INFUND-7729
    When the user clicks the button/Link    jQuery=tr:contains(Neural Industries) .no-margin
    Then The user should see the text in the page    12: Machine learning for transport infrastructure
    And the user should see the element    jQuery=h3:contains("Innovation area") ~ span:contains("Urban living")
