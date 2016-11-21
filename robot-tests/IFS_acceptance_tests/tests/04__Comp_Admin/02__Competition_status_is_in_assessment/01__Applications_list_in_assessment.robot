*** Settings ***
Documentation     INFUND-1987
...
...               INFUND-2307 Acceptance test: List of applications which are in assessment
...
...               INFUND-2411 When the competition is in assessment the total costs are showingn as zero in the list
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Competition status should be correct
    [Documentation]    INFUND-2307
    [Tags]    HappyPath
    Given the user navigates to the page    ${COMP_ADMINISTRATOR_IN_ASSESSMENT}
    Then the user should see the text in the page    In assessment
    Then the user should not see the text in the page    Competition open

Columns show of the submitted applications
    [Documentation]    INFUND-2307
    [Tags]    HappyPath
    Then the user should see the text in the page    Application no
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Lead
    And the user should see the text in the page    No. of partners
    And the user should see the text in the page    Grant requested (£)
    And the user should see the text in the page    Total project cost (£)
    And the user should see the text in the page    Duration (months)

Summary of the submitted applications
    [Documentation]    INFUND-2307
    [Tags]    HappyPath
    Then the calculations should be correct    css=.info-area p:nth-child(2) span
    And both calculations in the page should show the same    css=.info-area p:nth-child(2) span

Sort by Lead
    [Documentation]    INFUND-2307
    [Tags]    HappyPath
    When the application list is sorted by    Lead
    Then the applications should be sorted by column    3

Sort by Grant requested
    [Documentation]    INFUND-2411
    When the application list is sorted by    Grant requested
    Then the applications should be sorted by column    5

Sort by Total project cost
    [Documentation]    INFUND-2411
    When the application list is sorted by    Total project cost
    Then the applications should be sorted by column    6

Finances are showing in the list
    [Documentation]    INFUND-2411
    [Tags]    HappyPath
    Then the user should see the text in the page    ${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITHOUT_COMMAS}
    And the user should see the text in the page    ${DEFAULT_TOTAL_PROJECT_COST_WITHOUT_COMMAS}

Excel download button should be visible
    [Documentation]    INFUND-2307
    [Tags]    HappyPath
    Then the user should see the element    link=Export application data (.xls)

Only applications from this competition should be visible
    [Documentation]    INFUND-2311
    Then the user should not see the element    link=${OPEN_COMPETITION_APPLICATION_5_NUMBER}

Columns for not submitted applications
    [Documentation]    INFUND-2307
    When the user clicks the button/link    link=Applications not submitted
    Then the user should see the text in the page    Application no
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Lead
    And the user should see the text in the page    Percentage complete

Summary of the not submitted applications
    [Documentation]    INFUND-2307
    Then the calculations should be correct    css=.info-area p:nth-child(3) span
    And both calculations in the page should show the same    css=.info-area p:nth-child(3) span

Sorted by percentage
    [Documentation]    INFUND-2307
    When the application list is sorted by    Project title
    Then the applications should be sorted by column    2

Non submitted applications from this competition should be visible
    [Documentation]    INFUND-2311
    Then the user should not see the element    link=${IN_ASSESSMENT_APPLICATION_3_NUMBER}

Excel export
    [Documentation]    INFUND-1987, INFUND-4039
    [Tags]    HappyPath    Pending
    #TODO Pending due to INFUND-6187
    # TODO we need to adjust this test in sprint 8 when the new competition will be ready. For now we are using the download url. And add an extra check to see if we have the correct number of rows
    Given the user navigates to the page    ${COMP_ADMINISTRATOR_OPEN}
    When the admin downloads the excel
    And user opens the excel and checks the content
    [Teardown]    Empty the download directory

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

Empty the download directory
    Empty Directory    ${DOWNLOAD_FOLDER}

Download File
    [Arguments]    ${COOKIE_VALUE}    ${URL}    ${FILENAME}
    log    ${COOKIE_VALUE}
    Run and Return RC    curl -v --insecure --cookie "${COOKIE_VALUE}" ${URL} > ${DOWNLOAD_FOLDER}/${/}${FILENAME}

the admin downloads the excel
    ${ALL_COOKIES} =    Get Cookies
    Log    ${ALL_COOKIES}
    Download File    ${ALL_COOKIES}    ${server}/management/competition/${OPEN_COMPETITION}/download    submitted_applications.xlsx
    wait until keyword succeeds    300ms    1 seconds    Download should be done

User opens the excel and checks the content
    ${Excel1}    Open Excel File    ${DOWNLOAD_FOLDER}/submitted_applications.xlsx
    ${APPLICATION_ID_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    A4
    Should Be Equal    ${APPLICATION_ID_1}    ${OPEN_COMPETITION_APPLICATION_5_NUMBER}
    ${APPLICATION_TITLE_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    B4
    should be equal    ${APPLICATION_TITLE_1}    A new innovative solution
    ${LEAD_ORRGANISATION_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    C4
    should be equal    ${LEAD_ORRGANISATION_1}    Empire Ltd
    ${FIRST_NAME_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    D4
    should be equal    ${FIRST_NAME_1}    Steve
    ${LAST_NAME_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    E4
    should be equal    ${LAST_NAME_1}    Smith
    ${EMAIL_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    F4
    should be equal    ${EMAIL_1}    steve.smith@empire.com
    ${DURATION_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    G4
    Should Be Equal As Numbers    ${DURATION_1}    20.0
    ${NUMBER_OF_PARTNERS_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    H4
    Should Be Equal As Numbers    ${NUMBER_OF_PARTNERS_1}    4.0
    ${SUMMARY_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    I4
    Should contain    ${SUMMARY_1}    The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.
    ${TOTAL_COST_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    J4
    Should Be Equal    ${TOTAL_COST_1}    £398,324.29
    ${FUNDING_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    K4
    Should Be Equal    ${FUNDING_1}    £8,000.00
