*** Settings ***
Documentation     INFUND-1987
...
...               INFUND-2307 Acceptance test: List of applications which are in assessment
...
...               INFUND-2411 When the competition is in assessment the total costs are showingn as zero in the list
...
...               INFUND-6602 As a member of the competitions team I can navigate to the dashboard of an 'In assessment' competition so that I can see information and further actions for the competition
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Competition status should be correct
    [Documentation]    INFUND-2307
    ...
    ...    INFUND-6602
    [Tags]    HappyPath
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    When The user clicks the button/link    jQuery=.button:contains("Applications")
    Then the user should see the text in the page    In assessment
    And the user should not see the text in the page    Competition open

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

Excel download button should be visible
    [Documentation]    INFUND-2307
    [Tags]    HappyPath
    Then the user should see the element    link=Export application data (.xls)

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

Only applications from this competition should be visible
    [Documentation]    INFUND-2311
    Then the user should not see the element    link=${OPEN_COMPETITION_APPLICATION_5_NUMBER}

Columns for not submitted applications
    [Documentation]    INFUND-2307
    [Tags]    Failing
    #TODO Failing due to INFUND-7848
    When the user clicks the button/link    link=Applications not submitted
    Then the user should see the text in the page    Application no
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Lead
    And the user should see the text in the page    Percentage complete

Summary of the not submitted applications
    [Documentation]    INFUND-2307
    [Tags]    Failing
    #TODO Failing due to INFUND-7848
    Then the calculations should be correct    css=.info-area p:nth-child(3) span
    And both calculations in the page should show the same    css=.info-area p:nth-child(3) span

Sorted by percentage
    [Documentation]    INFUND-2307
    [Tags]    Failing
    #TODO Failing due to INFUND-7848
    When the application list is sorted by    Project title
    Then the applications should be sorted by column    2

Non submitted applications from this competition should be visible
    [Documentation]    INFUND-2311
    [Tags]    Failing
    #TODO Failing due to INFUND-7848
    Then the user should not see the element    link=${IN_ASSESSMENT_APPLICATION_3_NUMBER}

Excel export
    [Documentation]    INFUND-1987, INFUND-4039
    [Tags]    HappyPath    Download
    When the admin downloads the excel
    And user opens the excel and checks the content
    [Teardown]    Remove the file from the operating system    submitted_applications.xlsx

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

the admin downloads the excel
    the user downloads the file    ${Comp_admin1_credentials["email"]}    ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/download    ${DOWNLOAD_FOLDER}/submitted_applications.xlsx

User opens the excel and checks the content
    ${Excel1}    Open Excel File    ${DOWNLOAD_FOLDER}/submitted_applications.xlsx
    ${APPLICATION_ID_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    A2
    Should Be Equal    ${APPLICATION_ID_1}    ${IN_ASSESSMENT_APPLICATION_4_NUMBER}
    ${APPLICATION_TITLE_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    B2
    should be equal    ${APPLICATION_TITLE_1}    ${IN_ASSESSMENT_APPLICATION_4_TITLE}
    ${LEAD_ORGANISATION_EMAIL_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    F2
    should be equal    ${LEAD_ORGANISATION_EMAIL_1}    ${IN_ASSESSMENT_APPLICATION_4_LEAD_PARTNER_EMAIL}
    ${APPLICATION_ID_2}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    A3
    Should Be Equal    ${APPLICATION_ID_2}    ${IN_ASSESSMENT_APPLICATION_5_NUMBER}
    ${APPLICATION_TITLE_2}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    B3
    should be equal    ${APPLICATION_TITLE_2}    ${IN_ASSESSMENT_APPLICATION_5_TITLE}
    ${LEAD_ORGANISATION_EMAIL_2}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    F3
    should be equal    ${LEAD_ORGANISATION_EMAIL_2}    ${IN_ASSESSMENT_APPLICATION_5_LEAD_PARTNER_EMAIL}
    ${APPLICATION_ID_3}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    A4
    Should Be Equal    ${APPLICATION_ID_3}    ${IN_ASSESSMENT_APPLICATION_3_NUMBER}
    ${APPLICATION_TITLE_3}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    B4
    should be equal    ${APPLICATION_TITLE_3}    ${IN_ASSESSMENT_APPLICATION_3_TITLE}
    ${LEAD_ORGANISATION_EMAIL_3}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    F4
    should be equal    ${LEAD_ORGANISATION_EMAIL_3}    ${IN_ASSESSMENT_APPLICATION_3_LEAD_PARTNER_EMAIL}
