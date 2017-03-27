*** Settings ***
Documentation     INFUND-7734 Competition Management: Assign to application dashboard in Closed competition
...
...               INFUND-7729 Competition management: Allocate application dashboard on Closed competition
...
...               INFUND-8061 Filter and pagination on Allocate Applications (Closed competition) and Manage applications (In assessment) dashboards
...
...               INFUND-8062 Filter and pagination on Assign to application (Closed competition) and Application progress dashboards
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

Filtering the Assessors in the Allocate Applications page
    [Documentation]    INFUND-7042
    ...
    ...    INFUND-7729
    ...
    ...    INFUND-8062
    Given the user clicks the button/Link    jQuery=tr:contains(Neural Industries) .no-margin
    And the user should see the element    jQuery=h3:contains("Innovation area") ~ span:contains("Urban living")
    Then the user should see the element    jQuery=tr:nth-child(1) td:contains("Benjamin Nixon")    #this check verfies that the list of assessors in alphabetical order
    When the user selects the option from the drop-down menu    Materials, process and manufacturing design technologies    id=filterInnovationArea
    And the user clicks the button/link    jQuery=button:contains(Filter)
    Then the user should see the element    jQuery=td:contains("Benjamin Nixon")
    And the user should see the element    jQuery=td:contains("Paige Godfrey")
    And the user should not see the element    jQuery=td:contains("Riley Butler")
    And the user clicks the button/link    jQuery=a:contains("Clear all filters")
    And the user should see the element    jQuery=td:contains("Riley Butler")

Filtering Assessors in the Assign assessors page
    [Documentation]    INFUND-8062
    Given the user clicks the button/Link    jQuery=tr:contains(Benjamin) .no-margin
    When the user selects the option from the drop-down menu    Materials, process and manufacturing design technologies    id=filterInnovationArea
    And the user clicks the button/link    jQuery=button:contains(Filter)
    Then the user should see the element    jQuery=td:contains("Paige Godfrey")
    And the user clicks the button/link    jQuery=a:contains("Clear all filters")
    Then the user should see the element    jQuery=td:contains("Riley Butler")
