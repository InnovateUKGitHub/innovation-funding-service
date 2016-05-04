*** Settings ***
Documentation     INFUND-1987
...
...               INFUND-2307 Acceptance test: List of applications which are in assessment
...
...               INFUND-2411 When the competition is in assessment the total costs are showingn as zero in the list
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    User closes the browser
Force Tags
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Competition status should be correct
    [Documentation]    INFUND-2307
    Given the user navigates to the page    ${COMP_ADMINISTRATOR_IN_ASSESSMENT}
    Then the user should see the text in the page    In assessment
    Then the user should not see the text in the page    Competition open

The correct columns show for the submitted applications
    [Documentation]    INFUND-2307
    Then the user should see the text in the page    Application no
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Lead
    And the user should see the text in the page    No. of partners
    And the user should see the text in the page    Grant requested (£)
    And the user should see the text in the page    Total project cost (£)
    And the user should see the text in the page    Duration (months)

Summary of the applications submitted
    [Documentation]    INFUND-2307
    Then The calculations should be correct    css=.info-area p:nth-child(2) span
    And Both calculations in the page should show the same    css=.info-area p:nth-child(2) span

Sort by Lead
    [Documentation]    INFUND-2307
    When The application list is sorted by    Lead
    Then The applications should be sorted by column    3

Sort by Grant requested
    [Documentation]    INFUND-2411
    When The application list is sorted by    Grant requested
    Then The applications should be sorted by column    5

Sort by Total project cost
    [Documentation]    INFUND-2411
    When The application list is sorted by    Total project cost
    Then The applications should be sorted by column    6

Finances are showing in the list
    [Documentation]    INFUND-2411
    Then the user should see the text in the page    4608.00
    And the user should see the text in the page    7680.00

Excel download button should be visible
    [Documentation]    INFUND-2307
    Then the user should see the element    link=Export application data (.xls)

Submitted applications from a different competition should not be visible
    [Documentation]    INFUND-2311
    Then the user should not see the element    link=00000005

The correct columns show for the not submitted applications
    [Documentation]    INFUND-2307
    When the user clicks the button/link    link=Applications not submitted
    Then the user should see the text in the page    Application no
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Lead
    And the user should see the text in the page    Percentage complete

Summary of the applications not submitted
    [Documentation]    INFUND-2307
    Then The calculations should be correct    css=.info-area p:nth-child(3) span
    And Both calculations in the page should show the same    css=.info-area p:nth-child(3) span

The applications can be sorted by percentage
    [Documentation]    INFUND-2307
    When The application list is sorted by    Project title
    Then The applications should be sorted by column    2

Not submitted applications from different competitions should not be visible
    [Documentation]    INFUND-2311
    Then the user should not see the element    link=00000001

*** Keywords ***
The application list is sorted by
    [Arguments]    ${sorting_factor}
    Select From List    name=sort    ${sorting_factor}

The applications should be sorted by column
    [Arguments]    ${column_number}
    ${row_count}=    get matching xpath count    //*[td]
    @{sorted_column_contents}=    Create List
    : FOR    ${row}    IN RANGE    2    ${row_count}
    \    ${cell_contents}=    get table cell    css=table    ${row}    ${column_number}
    \    append to list    ${sorted_column_contents}    ${cell_contents}
    ${test_sorting_list}=    Copy List    ${sorted_column_contents}
    Sort List    ${test_sorting_list}
    Lists Should Be Equal    ${sorted_column_contents}    ${test_sorting_list}

The calculations should be correct
    [Arguments]    ${SUMMARY_LOCATOR}
    ${ELEMENT}=    get matching xpath count    //*[td]
    log    ${ELEMENT}
    ${LENGTH_SUMMARY}=    Get text    ${SUMMARY_LOCATOR}
    log    ${LENGTH_SUMMARY}
    Should Be Equal As Integers    ${LENGTH_SUMMARY}    ${ELEMENT}

Both calculations in the page should show the same
    [Arguments]    ${SUMMARY_LOCATOR}
    ${APPLICATIONS_NUMBER_SUMMARY}=    get text    ${SUMMARY_LOCATOR}
    ${APPLICATIONS_NUMBER_LIST}=    Get text    css=.column-two-thirds span
    Should Be Equal As Integers    ${APPLICATIONS_NUMBER_LIST}    ${APPLICATIONS_NUMBER_SUMMARY}
