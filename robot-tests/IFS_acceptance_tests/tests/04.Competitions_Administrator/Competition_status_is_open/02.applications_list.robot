*** Settings ***
Documentation     INFUND-2135 As a Competition Administrator I want to be able to view a listing of applications for an open competition, so that I have the latest status of the applications
...
...               INFUND-2259 \ As a competitions administrator I want to see summary details of all applications in a competition displayed alongside the list of applications so that I can reference \ information relating to the status of the competition
Suite Setup       Run Keywords    Log in as user    &{Comp_admin1_credentials}
...               AND    Given the user navigates to the page    ${COMP_MANAGEMENT_APPLICATIONS_LIST}
Suite Teardown    User closes the browser
Force Tags
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Competitions admin should be able to see the list of applications
    [Documentation]    INFUND-2135: listing of applications for an open competition
    [Tags]    Competition management
    Then the user should see the text in the page    Application list


The correct columns show for the application list table
    [Documentation]    INFUND-2135: listing of applications for an open competition
    [Tags]    Competition management
    Then the user should see the text in the page    Application no.
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Lead
    And the user should see the text in the page    Status
    And the user should see the text in the page    Percentage complete


The correct number of applications shows in the table header
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management
    Then the table header matches the number of rows in the applications list table


The applications can be sorted by application number
    [Documentation]    INFUND-2135: listing of applications for an open competition
    [Tags]    Competition management
    When the application list is sorted by    Application no.
    Then the applications should be sorted by column        1


The applications can be sorted by project title
    [Documentation]    INFUND-2135: listing of applications for an open competition
    [Tags]    Competition management
    When the application list is sorted by    Project title
    Then the applications should be sorted by column        2


The applications can be sorted by project lead
    [Documentation]    INFUND-2300: listing of applications for an open competition
    [Tags]    Competition management    Pending
    # pending until has been refactored(INFUND-2176)
    When the application list is sorted by    Lead
    Then the applications should be sorted by column        3


The applications can be sorted by percentage complete
    [Documentation]    INFUND-2300: listing of applications for an open competition
    [Tags]    Competition management
    When the application list is sorted by    Percentage complete
    Then the applications should be sorted by column        4


Calculations of the open applications
    [Documentation]    INFUND-2259
    [Tags]      Competition management
    # extra validation to only check the calculation if there are open applications
    ${open_count}=       Get matching xpath count    //*[text()="open"]
    Run keyword if            ${open_count} != 0      open application calculations are correct


Calculations of the submitted application
    [Documentation]    INFUND-2259
    [Tags]  Competition management
    # extra validation to only check the calculation if there are submitted applications
    ${submitted_count}=     Get matching xpath count    //*[text()="submitted"]
    Run keyword if           ${submitted_count} != 0    submitted application calculations are correct


Calculations for the Number of applications
    [Documentation]    INFUND-2259
    Then the calculations should be correct    jQuery=td:contains("00000")    css=.info-area p:nth-child(2) span
    And both calculations in the page should show the same


Comp admin can open the view mode of the application
    [Documentation]    INFUND-2300: listing of applications for an open competition
    ...
    ...    INFUND-2304: Read only view mode of applications from the application list page
    [Tags]    Competition management
    When the user clicks the button/link    link=00000001
    Then the user should be redirected to the correct page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    And the user should see the text in the page    A novel solution to an old problem
    And the user can see the upload for the 'Technical approach' question


*** Keywords ***
the application list is sorted by
    [Arguments]    ${sorting_factor}
    Select From List    name=sort    ${sorting_factor}

#the applications should be sorted by application number
#    @{app_number_list}=        Create List
#    ${row_count}=       get matching xpath count        //*[td]
#    : FOR       ${row}      IN RANGE        1       ${row_count}
#    \   ${cell_read}=       get table cell      css=table       ${row}      1
#    \   log to console       ${cell_read}
#    \   append to list       @{app_number_list}      ${cell_read}
#
#    ${sorting_list}=    Copy List       ${app_number_list}
#    Sort List       ${sorting_list}
#    #just checking that this is doing what I expect...
#    Reverse List    ${sorting_list}
#    Lists Should Be Equal       ${app_number_list}      ${sorting_list}


the applications should be sorted by column
    [Arguments]     ${column_number}
    ${row_count}=       get matching xpath count    //*[td]
    @{sorted_column_contents}=     Create List
    : FOR       ${row}      IN RANGE        1       ${row_count}
    \   ${cell_contents}=       get table cell      css=table       ${row}      1
    \   append to list       ${sorted_column_contents}      ${cell_contents}

    ${test_sorting_list}=    Copy List       ${sorted_column_contents}
    Sort List       ${test_sorting_list}
    # just checking that this is doing what I expect...
    Reverse List    ${test_sorting_list}
    Lists Should Be Equal          ${sorted_column_contents}        ${test_sorting_list}

    # element should contain    css=table tbody tr td a    00000001

the applications should be sorted by project title
    element should contain    css=table tbody tr td a    00000008

the applications should be sorted by Project lead
    element should contain    css=table tbody tr td a    foo

the applications should be sorted by Percentage complete
    element should contain    css=table tbody tr td a    00000007

the user can see the upload for the 'Technical approach' question
    the user clicks the button/link    css=[aria-controls="collapsible-8"]
    the user should see the text in the page    testing.pdf

The calculations should be correct
    [Arguments]    ${LIST_LOCATOR}    ${SUMMARY_LOCATOR}
    ${ELEMENT}=    Get Webelements    ${LIST_LOCATOR}
    ${LENGTH_LIST}=    Get Length    ${ELEMENT}
    log    ${LENGTH_LIST}
    ${LENGTH_SUMMARY}=    Get text    ${SUMMARY_LOCATOR}
    log    ${LENGTH_SUMMARY}
    Should Be Equal As Integers    ${LENGTH_SUMMARY}    ${LENGTH_LIST}

both calculations in the page should show the same
    ${APPLICATIONS_NUMBER_SUMMARY}=    get text    css=.info-area p:nth-child(2) span
    ${APPLICATIONS_NUMBER_LIST}=    Get text    css=.column-two-thirds span
    Should Be Equal As Integers    ${APPLICATIONS_NUMBER_LIST}    ${APPLICATIONS_NUMBER_SUMMARY}

open application calculations are correct
    the calculations should be correct    jQuery=td:contains("open")    css=.info-area p:nth-child(3) span

submitted application calculations are correct
    the calculations should be correct    jQuery=td:contains("submitted")    css=.info-area p:nth-child(5) span


the table header matches the number of rows in the applications list table
    ${row_count}=       get matching xpath count        //*[td]
    ${apps_string}=      Catenate            ${row_count}        applications
    The user should see the text in the page     ${apps_string}