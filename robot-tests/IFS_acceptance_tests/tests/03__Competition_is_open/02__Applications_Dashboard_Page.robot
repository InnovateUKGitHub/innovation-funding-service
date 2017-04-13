*** Settings ***
Documentation     INFUND-2135 As a Competition Administrator I want to be able to view a listing of applications for an open competition, so that I have the latest status of the applications
...
...               INFUND-2259 As a competitions administrator I want to see summary details of all applications in a competition displayed alongside the list of applications so that I can reference information relating to the status of the competition
...
...               INFUND-3006 As a Competition Management I want the ability to view the name of the lead on the 'all applications' page so I can better support the Customer Support Service.
...
...               INFUND-7367 Competition management: Applications dashboard
...
...               INFUND-7369 Competition management: View list of all applications
...
...               INFUND-8010 Filter, sorting and pagination on 'All applications' dashboard
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Variables ***
${valid_pdf}      testing.pdf
${quarantine_warning}    This file has been found to be unsafe

*** Test Cases ***
Application Dashboard
    [Documentation]    INFUND-7369
    [Tags]    HappyPath
    Given the user clicks the button/link    link=${OPEN_COMPETITION_NAME}
    When the user clicks the button/link    jQuery=a:contains("Applications: All, submitted")
    Then The user should see the element    jQuery=a:contains("Submitted applications")
    And The user should see the element    link=All applications

List of all Applications
    [Documentation]    INFUND-7367
    ...
    ...    INFUND-3063
    [Tags]    HappyPath
    When the user clicks the button/link    link=All applications
    Then the user should see the text in the page    All applications
    And the user should see the text in the page    Application number
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Innovation area
    And the user should see the text in the page    Lead
    And the user should see the text in the page    Status
    And the user should see the text in the page    Percentage complete

All Applications page: calculation in the table header
    [Documentation]    INFUND-7369
    [Tags]    HappyPath
    Then the table header matches correctly

The applications can be sorted by application number
    [Documentation]    INFUND-8010
    [Tags]    HappyPath    Failing    INFUND-8582
    When the application list is sorted by    Application no.
    Then the applications should be sorted by column    1

The applications can be sorted by lead applicant
    [Documentation]    INFUND-8010
    [Tags]
    When the application list is sorted by    Lead
    Then the applications should be sorted by column    3

Filter on application number
    [Documentation]    INFUND-8010
    [Tags]    HappyPath
    Given the user enters text to a text field    id=filterSearch    105
    When the user clicks the button/link    jQuery=button:contains("Filter")
    Then the user should see the text in the page    Safeguarding pollinators and their values to human well-being
    And the user should not see the text in the page    Climate science the history of Greenland's ice
    And the user clicks the button/link    jQuery=a:contains("Clear all filters")
    And the user should see the text in the page    Climate science the history of Greenland's ice

All Applications page: Key Statistics
    [Documentation]    INFUND-2259 INFUND-7369
    [Tags]
    Then the totals in the Key statistics should be correct

Comp admin can open the view mode of the application
    [Documentation]    INFUND-2300,INFUND-2304, INFUND-2435, INFUND-7503
    [Tags]    HappyPath
    [Setup]    Run keywords    Guest user log-in    &{lead_applicant_credentials}
    ...    AND    the user can see the option to upload a file on the page    ${technical_approach_url}
    ...    AND    the user uploads the file to the 'technical approach' question    ${valid_pdf}
    Given log in as a different user    &{Comp_admin1_credentials}
    And the user navigates to the page    ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    #Then the user should see the element    id=sort-by
    #And the user selects the option from the drop-down menu    id    id=sort-by
    And the user clicks the button/link    link=All applications
    When the user clicks the button/link    link=${OPEN_COMPETITION_APPLICATION_1_NUMBER}
    Then the user should be redirected to the correct page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    And the user should see the element    link=Print application
    And the user should see the text in the page    Climate science the history of Greenland's ice
    And the user should see the text in the page    ${valid_pdf}
    And the user can view this file without any errors
    #    And the user should see the text in the page    ${quarantine_pdf}
    #    And the user cannot see this file but gets a quarantined message
    # TODO when working on Guarantined files. Variable has been removed

Comp admin should be able to view but not edit the finances for every partner
    [Documentation]    INFUND-2443, INFUND-2483
    [Tags]
    Given the user navigates to the page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    When the user clicks the button/link    jQuery=button:contains("Finances summary")
    Then the user should not see the element    link=your finances
    And the user should see the element    jQuery=h3:contains("Finances summary")
    And the user should see the element    jQuery=h2:contains("Funding breakdown")
    And the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    When Log in as a different user    &{collaborator1_credentials}
    Then the user navigates to the page    ${YOUR_FINANCES_URL}
    And the applicant edits the Subcontracting costs section
    And the user reloads the page
    When Log in as a different user    &{Comp_admin1_credentials}
    And the user navigates to the page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    Then the user should see the correct finances change

*** Keywords ***
the user uploads the file to the 'technical approach' question
    [Arguments]    ${file_name}
    Choose File    name=formInput[1062]    ${UPLOAD_FOLDER}/${file_name}

the user can see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page    ${url}
    the user should see the text in the page    Upload

the user can view this file without any errors
    the user clicks the button/link    link=${valid_pdf}, 10 KB (opens in a new window)
    the user goes back to the previous tab

the user cannot see this file but gets a quarantined message
    [Documentation]    Currently not used. It was used in Comp admin can open the view mode of the application
    the user clicks the button/link    link=test_quarantine.pdf, 7 KB (opens in a new window)
    the user goes back to the previous tab
    the user should see the text in the page    ${quarantine_warning}

the finance summary calculations should be correct
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(1) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(2) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(4) td:nth-of-type(1)    £${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(1) td:nth-of-type(2)    ${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(2) td:nth-of-type(2)    ${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(3) td:nth-of-type(2)    ${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(4) td:nth-of-type(2)    ${DEFAULT_ACADEMIC_GRANT_RATE_WITH_PERCENTAGE}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(1) td:nth-of-type(3)    £${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(2) td:nth-of-type(3)    £${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(3) td:nth-of-type(3)    £${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(4) td:nth-of-type(3)    £${DEFAULT_ACADEMIC_FUNDING_SOUGHT_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(1) td:nth-of-type(5)    £${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(2) td:nth-of-type(5)    £${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(3) td:nth-of-type(5)    £${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}
    Wait Until Element Contains Without Screenshots    css=.finance-summary tbody tr:nth-of-type(4) td:nth-of-type(5)    £${DEFAULT_ACADEMIC_CONTRIBUTION_TO_PROJECT}

the finance Project cost breakdown calculations should be correct
    Wait Until Element Contains Without Screenshots    css=.project-cost-breakdown tbody tr:nth-of-type(1) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.project-cost-breakdown tbody tr:nth-of-type(2) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.project-cost-breakdown tbody tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains Without Screenshots    css=.project-cost-breakdown tbody tr:nth-of-type(4) td:nth-of-type(1)    £${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}

the applicant edits the Subcontracting costs section
    the user clicks the button/link    link=Your project costs
    the user clicks the button/link    jQuery=#form-input-1085 button:contains("Subcontracting costs")
    the user should see the text in the page    Subcontractor name
    The user enters text to a text field    css=#collapsible-4 .form-row:nth-child(2) input[id$=subcontractingCost]    2000
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-name"]    Jackson Ltd
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-country-"]    Romania
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-role"]    Contractor
    the user selects the checkbox    css=#agree-state-aid-page
    the user clicks the button/link    jQuery=button:contains("Mark as complete")

the user should see the correct finances change
    Wait Until Element Contains Without Screenshots    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}
    Wait Until Element Contains Without Screenshots    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}
    Wait Until Element Contains Without Screenshots    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(6)    £${DEFAULT_SUBCONTRACTING_COSTS_WITH_COMMAS_PLUS_2000}

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
    \    ${cell_contents_number}=    convert to number    ${cell_contents}
    \    append to list    ${column_contents}    ${cell_contents_number}
    \    append to list    ${also_column_contents}    ${cell_contents_number}
    Sort List    ${column_contents}
    Reverse List    ${also_column_contents}
    Lists Should Be Equal    ${column_contents}    ${also_column_contents}

The calculations should be correct
    [Arguments]    ${LIST_LOCATOR}    ${SUMMARY_LOCATOR}
    ${pagination}=    Run Keyword And Ignore Error Without Screenshots    the user clicks the button/link    name=page
    run keyword if    ${pagination} == 'PASS'    check calculations on both pages    ${LIST_LOCATOR}    ${SUMMARY_LOCATOR}
    run keyword if    ${pagination} == 'FAIL'    check calculations on one page    ${LIST_LOCATOR}    ${SUMMARY_LOCATOR}

check calculations on one page
    [Arguments]    ${list_locator}    ${summary_locator}
    ${element}=    Get Webelements    ${list_locator}
    ${length_list}=    Get Length    ${element}
    log    ${length_list}
    ${length_summary}=    Get text    ${summary_locator}
    log    ${length_summary}
    Should Be Equal As Integers    ${length_summary}    ${length_list}

check calculations on both pages
    [Arguments]    ${list_locator}    ${summary_locator}
    ${element_page_two}=    Get Webelements    ${list_locator}
    ${length_list_page_two}=    Get Length    ${element_page_two}
    log    ${length_list_page_two}
    the user navigates to the page    ${comp_management_applications_list}
    ${element}=    Get Webelements    ${list_locator}
    ${length_list}=    Get Length    ${element}
    log    ${length_list}
    ${total_length}=    Evaluate    ${length_list}+${length_list_page_two}
    log    ${total_length}
    ${length_summary}=    Get text    ${summary_locator}
    log    ${length_summary}
    Should Be Equal As Integers    ${length_summary}    ${total_length}

both calculations in the page should show the same
    ${APPLICATIONS_NUMBER_SUMMARY}=    get text    css=.column-two-thirds p span:nth-child(1)
    ${APPLICATIONS_NUMBER_LIST}=    Get text    css=.column-two-thirds span
    Should Be Equal As Integers    ${APPLICATIONS_NUMBER_LIST}    ${APPLICATIONS_NUMBER_SUMMARY}

open application calculations are correct
    the calculations should be correct    jQuery=td:contains("open")    css=.column-two-thirds p span:nth-child(1)

submitted application calculations are correct
    the calculations should be correct    jQuery=td:contains("submitted")    css=.info-area p:nth-child(5) span

the table header matches correctly
    ${pagination}=    Run Keyword And Ignore Error Without Screenshots    the user clicks the button/link    name=page
    Run Keyword If    ${pagination} == 'PASS'    check both pages of applications
    Run Keyword If    ${pagination} == 'FAIL'    check applications on one page

check both pages of applications
    ${row_count_second_page}=    get matching xpath count    //*[td]
    convert to integer    ${row_count_second_page}
    log    ${row_count_second_page}
    the user navigates to the page    ${comp_management_applications_list}
    ${row_count_first_page}=    get matching xpath count    //*[td]
    convert to integer    ${row_count_first_page}
    log    ${row_count_first_page}
    ${total_application_count}=    evaluate    ${row_count_first_page}+${row_count_second_page}
    log    ${total_application_count}
    $[apps_string}=    Catenate    ${total_application_count}    applications
    the user should see the text in the page    ${apps_string}

check applications on one page
    ${total_row_count}=    get matching xpath count    //*[td]
    convert to integer    ${total_row_count}
    log    ${total_row_count}
    ${apps_string}=    Catenate    ${total_application_count}    applications
    the user should see the text in the page    ${apps_string}

The calculation for the submited applications should be correct
    ${submitted_count}=    Get matching xpath count    //*[text()="submitted"]
    Run keyword if    ${submitted_count} != 0    submitted application calculations are correct

The calculation of the open applications should be correct
    ${open_count}=    Get matching xpath count    //*[text()="open"]
    Run keyword if    ${open_count} != 0    open application calculations are correct

The totals in the Key statistics should be correct
    #Calculation of the total number of Applications
    ${TOTAL_APPLICATIONS}=    Get matching xpath count    //table/tbody/tr
    ${TOTAL_COUNT}=    Get text    css=li:nth-child(1) > div > span
    Should Be Equal As Integers    ${TOTAL_APPLICATIONS}    ${TOTAL_COUNT}
    #Calculation of the Started Applications
    ${STARTED_APPLICATIONS}=    Get matching xpath count    //*[text()="Started"]
    ${STARTED_COUNT}=    Get text    css=li:nth-child(2) > div > span
    Should Be Equal As Integers    ${STARTED_APPLICATIONS}    ${STARTED_COUNT}
    #Calculation of the Submitted Applications
    ${SUBMITTED_APPLICATIONS}=    Get matching xpath count    //*[text()="Submitted"]
    ${SUBMITTED_COUNT}=    Get text    css=li:nth-child(4) > div > span
    Should Be Equal As Integers    ${SUBMITTED_APPLICATIONS}    ${SUBMITTED_COUNT}
    #TODO ADD Check for the beyond 50% counts when we will have test data
