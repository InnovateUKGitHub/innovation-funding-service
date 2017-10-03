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
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../10__Project_setup/PS_Common.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${valid_pdf}      testing.pdf
${quarantine_warning}    This file has been found to be unsafe

*** Test Cases ***
Application Dashboard
    [Documentation]    INFUND-7369
    [Tags]    HappyPath
    Given the user clicks the button/link    link=${openCompetitionRTO_name}
    When the user clicks the button/link    jQuery=a:contains("Applications: All, submitted, ineligible")
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
    Given the user enters text to a text field    id=filterSearch    ${application_ids["Safeguarding pollinators and their values to human well-being"]}
    When the user clicks the button/link    jQuery=button:contains("Filter")
    Then the user should see the text in the page    Safeguarding pollinators and their values to human well-being
    And the user should not see the text in the page    Climate science the history of Greenland's ice
    And the user clicks the button/link    jQuery=a:contains("Clear all filters")
    And the user should see the text in the page    Climate science the history of Greenland's ice

All Applications page: Key Statistics
    [Documentation]    INFUND-2259 INFUND-7369
    [Tags]
    Then the totals in the Key statistics should be correct

Application has team link and team details
    [Documentation]  IFS-43
    [Tags]  HappyPath
    Given the user clicks the button/link    link=${OPEN_COMPETITION_APPLICATION_1_NUMBER}
    Then the user should see the element  link=view application team details
    And the user should see the text in the page  ${OPEN_COMPETITION_APPLICATION_NAME}
    When the user clicks the button/link  link=view application team details
    Then the user should see the text in the page  Application team
    And the user should see the text in the page  View team members for both the lead and collaborating organisations.
    And the user should see the element    jQuery=h2:nth-of-type(1):contains("${EMPIRE_LTD_NAME} (Lead)")+h3:contains("Organisation type")+p:contains("Business")
    And the user should see the element    jQuery=div#applicationTeamOrganisationRegisteredAddress0 span:nth-of-type(1):contains("1")
    And the user should see the element    jQuery=div#applicationTeamOrganisationRegisteredAddress0 span:nth-of-type(2):contains("Empire Road")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser0 tbody tr:nth-of-type(1) td:contains("Steve Smith (Lead)")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser0 tbody tr:nth-of-type(1) td:contains("${lead_applicant}")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser0 tbody tr:nth-of-type(1) td:contains("46439359578")
    And the user should see the element    jQuery=h2:nth-of-type(2):contains("${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}")+h3:contains("Organisation type")+p:contains("Research")
    And the user should see the element    jQuery=div#applicationTeamOrganisationRegisteredAddress1 span:nth-of-type(1):contains("43")
    And the user should see the element    jQuery=div#applicationTeamOrganisationRegisteredAddress1 span:nth-of-type(2):contains("Deer Rise")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser1 tbody tr:nth-of-type(1) td:contains("Pete Tom")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser1 tbody tr:nth-of-type(1) td:contains("${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser1 tbody tr:nth-of-type(1) td:contains("81877706440")
    And the user should see the element    jQuery=h2:nth-of-type(3):contains("${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}")+h3:contains("Organisation type")+p:contains("Business")
    And the user should see the element    jQuery=div#applicationTeamOrganisationRegisteredAddress2 span:nth-of-type(1):contains("Electric Works, Sheffield Digital Campus")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser2 tbody tr:nth-of-type(1) td:contains("Ewan Cormack")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser2 tbody tr:nth-of-type(1) td:contains("${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_EMAIL}")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser2 tbody tr:nth-of-type(1) td:contains("36267829240")
    And the user should see the element    jQuery=h2:nth-of-type(4):contains("${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}")+h3:contains("Organisation type")+p:contains("Business")
    And the user should see the element    jQuery=div#applicationTeamOrganisationRegisteredAddress3 span:nth-of-type(1):contains("20")
    And the user should see the element    jQuery=div#applicationTeamOrganisationRegisteredAddress3 span:nth-of-type(2):contains("Fallow Lane")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser3 tbody tr:nth-of-type(1) td:contains("Jessica Doe")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser3 tbody tr:nth-of-type(1) td:contains("${PROJECT_SETUP_APPLICATION_1_PARTNER_EMAIL}")
    And the user should see the element    jQuery=table#applicationTeamOrganisationUser3 tbody tr:nth-of-type(1) td:contains("15247172589")

Comp admin can open the view mode of the application
    [Documentation]    INFUND-2300,INFUND-2304, INFUND-2435, INFUND-7503
    [Tags]    HappyPath
    [Setup]  The user logs-in in new browser                             &{lead_applicant_credentials}
    When the user can see the option to upload a file on the page        ${technical_approach_url}
    Then the user uploads the file to the 'technical approach' question  ${valid_pdf}
    When log in as a different user                         &{Comp_admin1_credentials}
    And the user navigates to the page                      ${applicationsForRTOComp}
    #Then the user should see the element  id=sort-by
    #And the user selects the option from the drop-down menu  id  id=sort-by
    And the user clicks the button/link                     link=All applications
    When the user clicks the button/link                    link=${OPEN_COMPETITION_APPLICATION_1_NUMBER}
    Then the user should be redirected to the correct page  ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    And the user should see the element                     link=Print application
    And the user should see the text in the page            Climate science the history of Greenland's ice
    And the user should see the text in the page            ${valid_pdf}
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
    Choose File    css=input.inputfile   ${UPLOAD_FOLDER}/${file_name}

the user can see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page    ${url}
    the user should see the text in the page    Upload

the user can view this file without any errors
    The user opens the link in new window  ${valid_pdf}, 10 KB
    the user goes back to the previous tab

the user cannot see this file but gets a quarantined message
    [Documentation]    Currently not used. It was used in Comp admin can open the view mode of the application
    The user opens the link in new window  test_quarantine.pdf, 7 KB
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
    the user clicks the button/link    jQuery=button:contains("Subcontracting costs")
    the user should see the text in the page    Subcontractor name
    The user enters text to a text field    css=#collapsible-4 .form-row:nth-child(2) input[name^=subcontracting-subcontractingCost]    2000
    The user enters text to a text field    css=.form-row:nth-child(2) [name^="subcontracting-name"]    Jackson Ltd
    The user enters text to a text field    css=.form-row:nth-child(2) [name^="subcontracting-country-"]    Romania
    The user enters text to a text field    css=.form-row:nth-child(2) [name^="subcontracting-role"]    Contractor
    the user selects the checkbox      stateAidAgreed
    the user clicks the button/link    jQuery=button:contains("Mark as complete")

the user should see the correct finances change
    Wait Until Element Contains Without Screenshots    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}
    Wait Until Element Contains Without Screenshots    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}
    Wait Until Element Contains Without Screenshots    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(6)    £${DEFAULT_SUBCONTRACTING_COSTS_WITH_COMMAS_PLUS_2000}

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
    the user navigates to the page    ${applicationsForRTOComp}
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
    the user navigates to the page    ${applicationsForRTOComp}
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
