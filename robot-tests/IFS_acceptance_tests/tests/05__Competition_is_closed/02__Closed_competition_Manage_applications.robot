*** Settings ***
Documentation     INFUND-7734 Competition Management: Assign to application dashboard in Closed competition
...
...               INFUND-7729 Competition management: Allocate application dashboard on Closed competition
...
...               INFUND-8061 Filter and pagination on Allocate Applications (Closed competition) and Manage applications (In assessment) dashboards
...
...               INFUND-8062 Filter and pagination on Assign to application (Closed competition) and Application progress dashboards
...
...               IFS-17 View list of accepted assessors - Closed state
...
...               IFS-1079 Remove an application - Closed and In assessment states
...
...               IFS-400 Filter by application number on Assessor progress dashboard - Closed and in assessments state
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Variables ***
${availableApp}  Machine learning for driverless cars

*** Test Cases ***
# Search for applications is covered in 'Filtering of the applications' inside file 03__Manage_applications.robot
Filtering the Assessors in the Allocate Applications page
    [Documentation]    INFUND-7042  INFUND-7729  INFUND-8062
    [Tags]
    Given The user clicks the button/link                     link=${CLOSED_COMPETITION_NAME}
    And the user clicks the button/Link                       link=Manage assessments
    And the user clicks the button/link                       link=Allocate applications
    When the user clicks the button/Link                      jQuery=tr:contains(Neural Industries) .no-margin
    And the user should see the element                       jQuery=h3:contains("Innovation area") ~ span:contains("Smart infrastructure")
    Then the user should see the element                      jQuery=tr:nth-child(1) td:contains("Benjamin Nixon")    #this check verfies that the list of assessors in alphabetical order
    When the user selects the option from the drop-down menu  Materials, process and manufacturing design technologies    id=filterInnovationArea
    And the user clicks the button/link                       jQuery=button:contains(Filter)
    Then the user should see the element                      jQuery=td:contains("Benjamin Nixon")
    And the user should see the element                       jQuery=td:contains("Paige Godfrey")
    And the user should not see the element                   jQuery=td:contains("Riley Butler")
    And the user clicks the button/link                       jQuery=a:contains("Clear all filters")
    And the user should see the element                       jQuery=td:contains("Riley Butler")

Filtering Assessors in the Assign assessors page
    [Documentation]    INFUND-8062
    [Tags]
    Given the user clicks the button/Link                     jQuery=tr:contains(Benjamin) .no-margin
    When the user selects the option from the drop-down menu  Materials, process and manufacturing design technologies    id=filterInnovationArea
    And the user clicks the button/link                       jQuery=button:contains(Filter)
    Then the user should see the element                      jQuery=td:contains("Paige Godfrey")
    And the user clicks the button/link                       jQuery=a:contains("Clear all filters")
    Then the user should see the element                      jQuery=td:contains("Riley Butler")
    [Teardown]  the user clicks the button/link               link=Allocate applications

Manage assessor list is correct
    [Documentation]    IFS-17
    [Tags]
    [Setup]  the user clicks the button/link  link=Manage assessments
    Given the user clicks the button/link     link=Allocate assessors
    Then the assessor list is correct before changes

Filter assessors
    [Documentation]    IFS-399
    [Tags]
    Given the user selects the option from the drop-down menu  Materials and manufacturing  id=innovationSector
    And the user clicks the button/link                        jQuery=.button:contains("Filter")
    Then the assessor list is correct before changes
    [Teardown]    the user clicks the button/link  link=Clear all filters

Assessor link goes to the assessor profile
    [Documentation]  IFS-17
    [Tags]
    Given the user clicks the button/link        link=Madeleine Martin
    Then the user should see the element         jQuery=h1:contains("Assessor profile") ~ p:contains("Madeleine Martin")
    [Teardown]  the user clicks the button/link  link=Back

Assessor Progress page
    [Documentation]  IFS-156
    [Tags]
    Given the user clicks the button/link  jQuery=td:contains("Madeleine Martin") ~ td a:contains("Assign")
    Then the user should see the element   jQuery=h2:contains("Assigned (2)") + .table-overflow tr:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") + tr:contains("Machine learning applied to the traveler experience")

Filtering applications on the assessor progress page
    [Documentation]    IFS-400
    [Tags]
    When the user enters text to a text field  css=#filterSearch  ${application_ids["${availableApp}"]}
    And the user clicks the button/link        jQuery=.button:contains("Filter")
    Then the user should see the element       jQuery=.applications-available tr:contains("${availableApp}") td:contains("Enterprise Engineering") ~ td:contains("Assign")

Assessor removal
    [Documentation]  IFS-1079
    [Tags]
    Given the user clicks the button/link     link=Allocate assessors
    Then the user clicks the button/link      jQuery=td:contains("Benjamin Nixon") ~ td a:contains("Assign")
    When the user clicks the button/link      jQuery=td:contains("${CLOSED_COMPETITION_APPLICATION}") ~ td:contains("Remove")
    Then the user should not see the element  jQuery=td:contains("${CLOSED_COMPETITION_APPLICATION}") ~ td:contains("Remove")
    And the user should see the element       jQuery=.applications-available td:contains("${CLOSED_COMPETITION_APPLICATION}") + td:contains("Neural") + td:contains("Neural Industries")

*** Keywords ***
the assessor list is correct before changes
    the user should see the element  jQuery=td:contains("Madeleine Martin") ~ td:contains("2") ~ td:contains("2")