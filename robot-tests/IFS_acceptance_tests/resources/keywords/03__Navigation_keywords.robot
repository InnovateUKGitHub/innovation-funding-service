*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
The user navigates to the page
    [Arguments]    ${TARGET_URL}
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    Go To    ${TARGET_URL}
    # Error checking
    the user should not see an error in the page

The user navigates to the page without the usual headers
    [Arguments]    ${TARGET_URL}
    Go To    ${TARGET_URL}
    # Error checking
    the user should not see an error in the page

The user should be redirected to the correct page
    [Arguments]    ${URL}
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    Location Should Contain    ${URL}
    the user should not see an error in the page

the user should be redirected to the correct page without the usual headers
    [Arguments]    ${URL}
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    Location Should Contain    ${URL}
    the user should not see an error in the page

The user goes back to the previous page
    Go Back

the user reloads the page
    Reload Page
    # Error checking
    the user should not see an error in the page

the user reloads page with autosave
    wait for autosave
    Reload Page
    # Error checking
    the user should not see an error in the page

The table should be sorted by column
    [Arguments]    ${column_number}
    ${row_count}=    Get Element Count    css=#application-list tr
    @{sorted_column_contents}=    Create List
    : FOR    ${row}    IN RANGE    2    ${row_count} + 1
    \    ${cell_contents}=    get table cell    css=#application-list    ${row}    ${column_number}
    \    append to list    ${sorted_column_contents}    ${cell_contents}
    ${test_sorting_list}=    Copy List    ${sorted_column_contents}
    Sort List    ${test_sorting_list}
    Lists Should Be Equal    ${sorted_column_contents}    ${test_sorting_list}