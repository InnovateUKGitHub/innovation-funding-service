*** Settings ***
Documentation     INFUND-2135 As a Competition Administrator I want to be able to view a listing of applications for an open competition, so that I have the latest status of the applications
...
...               INFUND-2259 As a competitions administrator I want to see summary details of all applications in a competition displayed alongside the list of applications so that I can reference information relating to the status of the competition
...
Suite Setup       Run Keywords    Log in as user    &{Comp_admin1_credentials}
...               AND    Given the user navigates to the page    ${COMP_MANAGEMENT_APPLICATIONS_LIST}
Suite Teardown    the user closes the browser
Force Tags
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${valid_pdf}      testing.pdf

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
    [Documentation]    INFUND-2135: listing of applications for an open competition
    [Tags]    Competition management     Failing
    Then the table header matches the number of rows in the applications list table

The applications can be sorted by application number
    [Documentation]    INFUND-2135: listing of applications for an open competition
    [Tags]    Competition management
    When the application list is sorted by    Application no.
    Then the applications should be sorted by column    1

The applications can be sorted by project title
    [Documentation]    INFUND-2135: listing of applications for an open competition
    [Tags]    Competition management
    When the application list is sorted by    Project title
    Then the applications should be sorted by column    2

The applications can be sorted by project lead
    [Documentation]    INFUND-2300: listing of applications for an open competition
    [Tags]    Competition management
    When the application list is sorted by    Lead
    Then the applications should be sorted by column    4

The applications can be sorted by percentage complete
    [Documentation]    INFUND-2300: listing of applications for an open competition
    [Tags]    Competition management
    When the application list is sorted by    Percentage complete
    Then the applications should be sorted in reverse order by column    6

Calculations of the open applications
    [Documentation]    INFUND-2259
    [Tags]    Competition management
    # extra validation to only check the calculation if there are open applications
    ${open_count}=    Get matching xpath count    //*[text()="open"]
    Run keyword if    ${open_count} != 0    open application calculations are correct

Calculations of the submitted application
    [Documentation]    INFUND-2259
    [Tags]    Competition management
    # extra validation to only check the calculation if there are submitted applications
    ${submitted_count}=    Get matching xpath count    //*[text()="submitted"]
    Run keyword if    ${submitted_count} != 0    submitted application calculations are correct

Calculations for the Number of applications
    [Documentation]    INFUND-2259
    [Tags]     Failing
    Then the calculations should be correct    jQuery=td:contains("00000")    css=.info-area p:nth-child(2) span
    And both calculations in the page should show the same

*** Keywords ***
the application list is sorted by
    [Arguments]    ${sorting_factor}
    Select From List    name=sort    ${sorting_factor}

the applications should be sorted by column
    [Arguments]    ${column_number}
    ${row_count}=    get matching xpath count    //*[td]
    @{sorted_column_contents}=    Create List
    : FOR    ${row}    IN RANGE    2    ${row_count}
    \    ${cell_contents}=    get table cell    css=table    ${row}    ${column_number}
    \    append to list    ${sorted_column_contents}    ${cell_contents}
    ${test_sorting_list}=    Copy List    ${sorted_column_contents}
    Sort List    ${test_sorting_list}
    Lists Should Be Equal    ${sorted_column_contents}    ${test_sorting_list}

the applications should be sorted in reverse order by column
    [Arguments]    ${column_number}
    ${row_count}=    get matching xpath count    //*[td]
    ${column_contents}=    Create List
    ${also_column_contents}=    Create List
    : FOR    ${row}    IN RANGE    2    ${row_count}
    \    ${cell_contents}=    get table cell    css=table    ${row}    ${column_number}
    \    ${cell_contents_number}=      convert to number    ${cell_contents}
    \    append to list    ${column_contents}    ${cell_contents_number}
    \    append to list     ${also_column_contents}     ${cell_contents_number}
    Sort List    ${column_contents}
    Reverse List    ${also_column_contents}
    Lists Should Be Equal    ${column_contents}    ${also_column_contents}

The calculations should be correct
    [Arguments]    ${LIST_LOCATOR}    ${SUMMARY_LOCATOR}
    ${ELEMENT}=    Get Webelements    ${LIST_LOCATOR}
    ${LENGTH_LIST}=    Get Length    ${ELEMENT}
    log    ${LENGTH_LIST}
    ${pagination}=    run keyword and ignore error       the user clicks the button/link    name=page
    run keyword if      ${pagination} == 'PASS'       ${LENGTH_LIST_PAGE_TWO}=        get matching xpath count     //*[td]
    ${LENGTH_LIST_NUMBER}=    convert to integer     ${LENGTH_LIST}
    ${LENGTH_LIST_PAGE_TWO_NUMBER}=      convert to integer      ${LENGTH_LIST_PAGE_TWO}
    ${total_length_list}=       evaluate    ${LENGTH_LIST_NUMBER}+${LENGTH_LIST_PAGE_TWO_NUMBER}
    ${LENGTH_SUMMARY}=    Get text    ${SUMMARY_LOCATOR}
    log    ${LENGTH_SUMMARY}
    Should Be Equal As Integers    ${LENGTH_SUMMARY}    ${total_length_list}

both calculations in the page should show the same
    ${APPLICATIONS_NUMBER_SUMMARY}=    get text    css=.info-area p:nth-child(2) span
    ${APPLICATIONS_NUMBER_LIST}=    Get text    css=.column-two-thirds span
    Should Be Equal As Integers    ${APPLICATIONS_NUMBER_LIST}    ${APPLICATIONS_NUMBER_SUMMARY}

open application calculations are correct
    the calculations should be correct    jQuery=td:contains("open")    css=.info-area p:nth-child(3) span

submitted application calculations are correct
    the calculations should be correct    jQuery=td:contains("submitted")    css=.info-area p:nth-child(5) span

the table header matches the number of rows in the applications list table
    ${pagination}=   Run Keyword and Ignore Error      the user clicks the button/link     name=page
    Run Keyword If     ${pagination} == 'PASS'      ${row_count_second_page}=       get matching xpath count      //*[td]
    the user navigates to the page        ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    ${row_count_first_page}=      get matching xpath count      //*[td]
    ${row_count_first_page_number}=    convert to integer      ${row_count_first_page}
    ${row_count_second_page_number}=     convert to integer    ${row_count_second_page}
    log       ${row_count_second_page_number}
    ${total_row_count}=       Evaluate        ${row_count_first_page_number}+${row_count_second_page_number}

    ${apps_string}=    Catenate    ${total_row_count}    applications
    the user navigates to the page      ${comp_management_applications_list}
    The user should see the text in the page    ${apps_string}

the user can see the option to upload a file on the page
    [Arguments]    ${url}
    the user navigates to the page    ${url}
     the user should see the text in the page   Upload

the user uploads the file to the 'project team' question
    [Arguments]    ${file_name}
    Choose File    name=formInput[18]    ${UPLOAD_FOLDER}/${file_name}
    Sleep    500ms
