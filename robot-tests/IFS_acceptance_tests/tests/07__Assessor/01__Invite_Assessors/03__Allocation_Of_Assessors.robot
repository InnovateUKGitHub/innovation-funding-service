*** Settings ***
Documentation    IFS-7080  Add sort option to table: assign assessors to applications
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Resource          ../../../resources/defaultResources.robot
Resource          ../Assessor_Commons.robot
*** Test Cases ***
Competition in Assessment: Application progress page Sort by: Total applications
    [Documentation]  IFS-7080
    Given the user sorts by                    Total applications
    And the user clicks the button/link        jQuery = .pagination-links a:contains('3')
    Then The table should be sorted by column  3

Competition in Assessment: Application progress page Sort by: Assigned
    [Documentation]  IFS-7080
    Given the user sorts by                    Assigned
    And the user clicks the button/link        jQuery = .pagination-links a:contains('3')
    Then The table should be sorted by column  4

Competition in Assessment: Application progress page Sort by: Assessor
    [Documentation]  IFS-7080
    Given the user sorts by                    Assessor
    Then The table should be sorted by column  1

Competition in Assessment: Application progress page Sort by: Skill Areas
    [Documentation]  IFS-7080
    Given the user sorts by                    Skill areas
    And the user clicks the button/link        jQuery = .pagination-links a:contains('2')
    Then The table should be sorted by column  2

Competition in Assessment: Application progress page Sort by: Submitted
    [Documentation]  IFS-7080
    Given the user sorts by                    Submitted
    And the user clicks the button/link        jQuery = .pagination-links a:contains('3')
    Then The table should be sorted by column  5

Competition is Closed: Assign to application page Sort by: Total applications
    [Documentation]  IFS-7080
    [Setup]  the user navigates to the page        ${server}/management/assessment/competition/${CLOSED_COMPETITION}/application/${CLOSED_COMPETITION_APPLICATION}/assessors
    Given the user sorts by                        Total applications
    When The table should be sorted by column      3

Competition in Closed: Assign to application page Sort by: Assigned
    [Documentation]  IFS-7080
    Given the user sorts by                    Assigned
    Then The table should be sorted by column  4

Competition in Closed: Assign to application page Sort by: Assessor
    [Documentation]  IFS-7080
    Given the user sorts by                    Assessor
    Then The table should be sorted by column  1

Competition in Closed: Assign to application page Sort by: Skill Areas
    [Documentation]  IFS-7080
    Given the user sorts by                    Skill areas
    Then The table should be sorted by column  2

Competition in Closed: Assign to application page Sort by: Submitted
    [Documentation]  IFS-7080
    Given the user sorts by                    Submitted
    Then The table should be sorted by column  5

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{ifs_admin_user_credentials}
    the user navigates to the page   ${server}/management/assessment/competition/${IN_ASSESSMENT_COMPETITION}/application/${IN_ASSESSMENT_APPLICATION_4_NUMBER}/assessors

The user sorts by
    [Arguments]  ${sortOption}
    the user selects the option from the drop-down menu  ${sortOption}  id = sort-by
    the user clicks the button/link                      jQuery = button:contains("Sort")