*** Settings ***
Documentation     INFUND-1987
...
...               INFUND-2307 Acceptance test: List of applications which are in assessment
...
...               INFUND-2411 When the competition is in assessment the total costs are showingn as zero in the list
...
...               INFUND-6602 As a member of the competitions team I can navigate to the dashboard of an 'In assessment' competition so...
...
...               INFUND-7367 Competition management: Applications dashboard
...
...               INFUND-7371 Competition management: View list of submitted applications
...
...               INFUND-7696 Competition management: View in progress/completed applications
...
...               INFUND-8012 Filter, sorting and pagination on 'Submitted applications' dashboard
...
...               INFUND-8010 Filter, sorting and pagination on 'All applications' dashboard
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Applications Dashboard
    [Documentation]    INFUND-7367
    [Tags]    HappyPath
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    When The user clicks the button/link    jQuery=a:contains("Applications: All, submitted")
    Then The user should see the element    jQuery=a:contains(All applications)
    And The user should see the element    link=Submitted applications

Submitted applications
    [Documentation]    INFUND-7367 INFUND-7371
    [Tags]    HappyPath
    When the user clicks the button/link    link=Submitted applications
    Then the user should see the text in the page    Application number
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Lead
    And the user should see the text in the page    Innovation area
    And the user should see the text in the page    Number of partners
    And the user should see the text in the page    Grant requested (£)
    And the user should see the text in the page    Total project cost (£)
    And the user should see the text in the page    Duration (months)
    And the user should see the text in the page    Filter applications
    And the user should see the element    jQuery=td:contains("Intelligent Building") ~ td:nth-child(4):contains("Forming technologies")
    And the user should see the element    jQuery=.pagination-part-title:contains(21 to 40)

Submitted applications Key Statistics
    [Documentation]    INFUND-7371
    [Tags]    HappyPath
    # TODO Resolve issue with count Then the calculations should be correct    css=.grid-row li:nth-child(2) span
    Then both calculations in the page should show the same    css=.grid-row li:nth-child(2) span

Submitted applications View completed applications
    [Documentation]    INFUND-7351
    [Tags]    HappyPath
    Given the user clicks the button/link    link=19
    And the user should see the text in the page    Application overview
    When the user clicks the button/link    link=Back
    Then the user should see the text in the page    Submitted applications

Sort by Lead
    [Documentation]    INFUND-8012
    [Tags]    HappyPath
    When the application list is sorted by    Lead
    Then the applications should be sorted by column    3

Sort by Application number
    [Documentation]    INFUND-8012
    [Tags]
    When the application list is sorted by    Application no.
    Then the applications should be sorted by column    1

Finances are showing in the list
    [Documentation]    INFUND-7371
    [Tags]    HappyPath    Pending
    #TODO \ BAs need to discuss rounding discrepencies between this page and Applicant view - Agreed with VF
    Then the user should see the text in the page    ${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}
    And the user should see the text in the page    ${DEFAULT_TOTAL_PROJECT_COST_WITH_COMMAS}

Only applications from this competition should be visible
    [Documentation]    INFUND-2311
    Then the user should not see the element    link=${OPEN_COMPETITION_APPLICATION_5_NUMBER}

Filter by application number
    [Documentation]    INFUND-8012
    [Tags]    HappyPath
    Given the user enters text to a text field    id=filterSearch    45
    When the user clicks the button/link    jQuery=button:contains(Filter)
    Then the user should see the text in the page    Living with Social Media
    And the user should not see the element    jQuery=.pagination-label:contains(Next)
    And the user clicks the button/link    jQuery=a:contains(Clear all filters)
    Then the user should see the element    jQuery=.pagination-label:contains(Next)

Next/Previous pagination on submitted applications
    [Documentation]    INFUND-8012
    [Tags]
    When the user clicks the button/link    jQuery=.pagination-label:contains(Next)
    Then the user should see the element    jQuery=.pagination-part-title:contains(1 to 20)
    And the user should see the element    jQuery=.pagination-part-title:contains(41 to 41)
    And the user clicks the button/link    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-part-title:contains(41 to 41)

Page list pagination on submitted applications
    [Documentation]    INFUND-8012
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=a:contains(41 to 41)
    Then the user should see the element    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-label:contains(Next)
    [Teardown]    the user clicks the button/link    link=Applications

Next/Previous pagination on all applications
    [Documentation]    INFUND-8010
    [Tags]    HappyPath
    [Setup]    the user clicks the button/link    link=All applications
    When the user clicks the button/link    jQuery=.pagination-label:contains(Next)
    Then the user should see the element    jQuery=.pagination-part-title:contains(1 to 20)
    And the user should see the element    jQuery=.pagination-part-title:contains(41 to 46)
    And the user clicks the button/link    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-part-title:contains(41 to 46)

Page list pagination on all applications
    [Documentation]    INFUND-8010
    [Tags]
    When the user clicks the button/link    jQuery=a:contains(41 to 46)
    Then the user should see the element    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-label:contains(Next)

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
    ${APPLICATIONS_NUMBER_LIST}=    Get text    css=.column-half span
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
