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
...
...               IFS-5915 Assessor Filter Option
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown
#The user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot

*** Variables ***
${availableApp}  Machine learning for driverless cars

*** Test Cases ***
# Search for applications is covered in 'Filtering of the applications' inside file 03__Manage_applications.robot
Filtering the Assessors in the Allocate Applications page
    [Documentation]    INFUND-7042  INFUND-7729  INFUND-8062  IFS-5915
    Given the user navigates to allocate applications page
    When the user verfies that the list of assessors in alphabetical order
    Then the user filters the applications by first or last name

Filtering Assessors in the Assign assessors page
    [Documentation]    INFUND-8062  IFS-5915
    Given the user adds an assessor to application            assessor-row-1
    When the user enters text to a text field                 id = assessorNameFilter  Paige
    And the user clicks the button/link                       jQuery = button:contains(Filter)
    Then the user should see the element                      jQuery = td:contains("Paige Godfrey")
    And the user clicks the button/link                       jQuery = a:contains("Clear filter")
    Then the user should see the element                      jQuery = td:contains("Riley Butler")
    [Teardown]  the user clicks the button/link               link = Allocate applications

Manage assessor list is correct
    [Documentation]    IFS-17
    [Setup]  the user clicks the button/link  link = Manage assessments
    Given the user clicks the button/link     link = Allocate assessors
    Then the assessor list is correct before changes

Filter assessors
    [Documentation]    IFS-399  IFS-5915
    Given the user enters text to a text field            id = assessorNameFilter   Madeleine
    And the user clicks the button/link                   jQuery = .govuk-button:contains("Filter")
    Then the assessor list is correct before changes
    [Teardown]    the user clicks the button/link         link = Clear filter

Assessor link goes to the assessor profile
    [Documentation]  IFS-17
    Given the user clicks the button/link        link = Madeleine Martin
    Then the user should see the element         jQuery = dt:contains("Name") ~ dd:contains("Madeleine Martin")
    [Teardown]  the user clicks the button/link  link = Back to allocate assessors

Assessor Progress page
    [Documentation]  IFS-156
    Given the user clicks the button/link  jQuery = td:contains("Madeleine Martin") ~ td a:contains("Assign")
    Then the user should see the element   jQuery = h2:contains("Assigned (2)") + .table-overflow tr:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") + tr:contains("Machine learning applied to the traveler experience")

Filtering applications on the assessor progress page
    [Documentation]    IFS-400
    When the user enters text to a text field  css = #filterSearch  ${application_ids["${availableApp}"]}
    And the user clicks the button/link        jQuery = .govuk-button:contains("Filter")
    Then the user should see the element       jQuery = .applications-available tr:contains("${availableApp}"):contains("Enterprise Engineering")

Assessor removal
    [Documentation]  IFS-1079
    Given the user clicks the button/link     link = Allocate assessors
    Then the user clicks the button/link      jQuery = td:contains("Benjamin Nixon") ~ td a:contains("Assign")
    When the user clicks the button/link      jQuery = td:contains("${CLOSED_COMPETITION_APPLICATION}") ~ td:contains("Remove")
    Then the user should not see the element  jQuery = td:contains("${CLOSED_COMPETITION_APPLICATION}") ~ td:contains("Remove")
    And the user should see the element       jQuery = .applications-available td:contains("${CLOSED_COMPETITION_APPLICATION}") + td:contains("Neural") + td:contains("Neural Industries")

*** Keywords ***
the assessor list is correct before changes
    the user should see the element    jQuery = td:contains("Madeleine Martin") ~ td:contains("0") ~ td:contains("0")

the user navigates to allocate applications page
    the user clicks the button/link      link = ${CLOSED_COMPETITION_NAME}
    the user clicks the button/Link      link = Manage assessments
    the user clicks the button/link      link = Allocate applications

the user verfies that the list of assessors in alphabetical order
    the user clicks the button/Link     jQuery = td:contains("Neural Industries") ~ td a
    the user should see the element     jQuery = h3:contains("Innovation area") ~ span:contains("Smart infrastructure")
    the user should see the element     jQuery = tr:nth-child(1) td:contains("Benjamin Nixon")    #this check verfies that the list of assessors in alphabetical order

the user filters the applications by first or last name
    the user enters text to a text field            id = assessorNameFilter   Benjamin
    the user clicks the button/link                 jQuery = .govuk-button:contains("Filter")
    the user should see the element                 jQuery = td:contains("Benjamin Nixon")
    the user should not see the element             jQuery = td:contains("Riley Butler")
    the user clicks the button/link                 jQuery = a:contains("Clear filter")
    the user should see the element                 jQuery = td:contains("Riley Butler")
    the user enters text to a text field            id = assessorNameFilter   Wilson
    the user clicks the button/link                 jQuery = .govuk-button:contains("Filter")
    the user should see the element                 jQuery = td:contains("Felix Wilson")
    the user clicks the button/link                 jQuery = a:contains("Clear filter")