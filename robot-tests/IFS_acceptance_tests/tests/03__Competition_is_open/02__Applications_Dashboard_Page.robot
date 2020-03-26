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
Resource          ../04__Applicant/Applicant_Commons.robot

*** Variables ***
${quarantine_warning}    This file has been found to be unsafe

*** Test Cases ***
Application Dashboard
    [Documentation]    INFUND-7369
    Given the user clicks the button/link    link = ${openCompetitionRTO_name}
    When the user clicks the button/link     jQuery = a:contains("Applications: All, submitted, ineligible")
    Then The user should see the element     jQuery = a:contains("Submitted applications")
    And The user should see the element      link = All applications

List of all Applications
    [Documentation]    INFUND-7367 INFUND-3063
    When the user clicks the button/link             link = All applications
    Then the user should see the element             jQuery = h1:contains("All applications")
    And the user should see the element              jQuery = th:contains("Application number")
    And the user should see the element              jQuery = th:contains("Project title")
    And the user should see the element              jQuery = th:contains("Innovation area")
    And the user should see the element              jQuery = th:contains("Lead")
    And the user should see the element              jQuery = th:contains("Status")
    And the user should see the element              jQuery = th:contains("Percentage complete")

All Applications page: calculation in the table header
    [Documentation]    INFUND-7369
    Then the table header matches correctly

The applications can be sorted by application number
    [Documentation]    INFUND-8010  INFUND-8582
    [Tags]    Failing
    When the application list is sorted by              Application no.
    Then the applications should be sorted by column    1

The applications can be sorted by lead applicant
    [Documentation]    INFUND-8010
    When the application list is sorted by              Lead
    Then the applications should be sorted by column    3

Filter on application number
    [Documentation]    INFUND-8010
    Given the user enters text to a text field          id = filterSearch    ${OPEN_COMPETITION_APPLICATION_6_NUMBER}
    When the user clicks the button/link                jQuery = button:contains("Filter")
    Then the user should see the element                jQuery = td:contains("Safeguarding pollinators and their values to human well-being")
    And the user should not see the element             jQuery = td:contains("Climate science the history of Greenland's ice")
    And the user clicks the button/link                 jQuery = a:contains("Clear all filters")
    And the user should see the element                 jQuery = td:contains("Climate science the history of Greenland's ice")

All Applications page: Key Statistics
    [Documentation]    INFUND-2259 INFUND-7369
    Then the totals in the Key statistics should be correct

Application has application team details
    [Documentation]  IFS-43  IFS-6152
    Given the user clicks the button/link          link = ${OPEN_COMPETITION_APPLICATION_1_NUMBER}
    Then the user should see the element           jQuery = h1 span:contains("${OPEN_COMPETITION_APPLICATION_NAME}")
    When the user clicks the button/link           id = accordion-questions-heading-1-1     #Application team
    Then the user should should see lead and partners details

Comp admin can open the view mode of the application
    [Documentation]    INFUND-2300,INFUND-2304, INFUND-2435, INFUND-7503
    [Setup]  The user logs-in in new browser                               &{lead_applicant_credentials}
    When the user can see the option to upload a file on the page          ${APPLICATION_OVERVIEW_URL}
    Then the user uploads the file                                         css = input.inputfile  ${5mb_pdf}
    When log in as a different user                                        &{Comp_admin1_credentials}
    And the user navigates to the page                                     ${applicationsForRTOComp}
    #Then the user should see the element  id = sort-by
    #And the user selects the option from the drop-down menu               id  id = sort-by
    And the user clicks the button/link                                    link = All applications
    When the user clicks the button/link                                   link = ${OPEN_COMPETITION_APPLICATION_1_NUMBER}
    Then the user should be redirected to the correct page                 ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    And the user should see the element                                    link = Print application
    And the user should see the element                                    jQuery = h1 span:contains("Climate science the history of Greenland's ice")
    And the user should see the element                                    jQuery = h3:contains("Appendix") ~ a:contains("testing_5MB.pdf, 4 MB")
    And open pdf link                                                      jQuery = a:contains(${5mb_pdf}, 4 MB)
#        And the user should see the text in the page    ${quarantine_pdf}
#        And the user cannot see this file but gets a quarantined message
#     TODO when working on Guarantined files. Variable has been removed

Comp admin can link to the comp page from application overview
    [Documentation]  IFS-6060
    Given the user clicks the button/link  link = ${openCompetitionRTO_name}
    Then the user should be redirected to the correct page                 ${server}/management/competition/${openCompetitionRTO}

Comp admin should be able to view but not edit the finances for every partner
    [Documentation]    INFUND-2443, INFUND-2483
    Given the user navigates to the page             ${COMP_MANAGEMENT_APPLICATION_2_OVERVIEW}
    When the user clicks the button/link             jQuery = button:contains("Finances summary")
    Then the user should not see the element         link = your project finances
    And the user should see the element              jQuery = h2:contains("Finances summary")
    And the user should see the element              jQuery = h2:contains("Funding breakdown")
    And the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    When Log in as a different user                  &{collaborator1_credentials}
    Then the user navigates to Your-finances page    ${newOpenComp}
    And the applicant edits the Subcontracting costs section
    And the user reloads the page
    When Log in as a different user                  &{Comp_admin1_credentials}
    And the user navigates to the page               ${COMP_MANAGEMENT_APPLICATION_2_OVERVIEW}
    Then the user should see the correct finances change

*** Keywords ***
the user can see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page              ${url}
    the user clicks the button/link             jQuery = a:contains("Technical approach")
    the user should see the element             jQuery = label:contains("Upload")

the user can view this file without any errors
    the user clicks the button/link   jQuery = a:contains(${5mb_pdf}, 4 MB)
    the user should not see an error in the page

the user cannot see this file but gets a quarantined message
    [Documentation]    Currently not used. It was used in Comp admin can open the view mode of the application
    the user clicks the button/link       test_quarantine.pdf, 7 KB
    the user should not see an error in the page
    the user should see the text in the page    ${quarantine_warning}

the finance summary calculations should be correct
    The user should see the element    jQuery = .finance-summary tr:contains("Empire") td:contains("£${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}") + td:contains("${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}") + td:contains("${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}") ~ td:contains("${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}")
    The user should see the element    jQuery = .finance-summary tr:contains("HIVE") td:contains("£${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}") + td:contains("${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}") + td:contains("${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}") ~ td:contains("${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}")
    The user should see the element    jQuery = .finance-summary tr:contains("Ludlow") td:contains("£${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}") + td:contains("${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}") + td:contains("${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}") ~ td:contains("${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}")
    The user should see the element    jQuery = .finance-summary tr:contains("EGGS") td:contains("£${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}") + td:contains("${DEFAULT_ACADEMIC_GRANT_RATE_WITH_PERCENTAGE}") + td:contains("${DEFAULT_ACADEMIC_CONTRIBUTION_TO_PROJECT}") ~ td:contains("${DEFAULT_ACADEMIC_CONTRIBUTION_TO_PROJECT}")

the finance Project cost breakdown calculations should be correct
    The user should see the element    jQuery = .project-cost-breakdown tr:contains("Empire") td:contains("${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}")
    The user should see the element    jQuery = .project-cost-breakdown tr:contains("HIVE") td:contains("${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}")
    The user should see the element    jQuery = .project-cost-breakdown tr:contains("Ludlow") td:contains("${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}")
    The user should see the element    jQuery = .project-cost-breakdown tr:contains("EGGS") td:contains("${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}")

the applicant edits the Subcontracting costs section
    the user clicks the button/link             link = Your project costs
    the user clicks the button/link             jQuery = button:contains("Subcontracting costs")
    the user should see the element             jQuery = label:contains("Subcontractor name")
    The user enters text to a text field        css = #accordion-finances-content-5 .form-row:nth-child(2) input[name$=".cost"]    2000
    The user enters text to a text field        css = .form-row:nth-child(2) [name$=".name"]    Jackson Ltd
    The user enters text to a text field        css = .form-row:nth-child(2) [name$=".country"]    Romania
    The user enters text to a text field        css = .form-row:nth-child(2) [name$=".role"]    Contractor
    the user selects the checkbox               stateAidAgreed
    the user clicks the button/link             jQuery = button:contains("Mark as complete")

the user should see the correct finances change
    the user should see the element    jQuery = .finance-summary tr:contains("Ludlow") td:contains("${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}")
    the user should see the element    jQuery = .project-cost-breakdown tr:contains("Ludlow") td:contains("£${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}") ~ td:contains("${DEFAULT_SUBCONTRACTING_COSTS_WITH_COMMAS_PLUS_2000}")

The calculations should be correct
    [Arguments]    ${LIST_LOCATOR}    ${SUMMARY_LOCATOR}
    ${pagination} =     Run Keyword And Ignore Error Without Screenshots    the user clicks the button/link    name = page
    run keyword if    ${pagination}  ==  'PASS'    check calculations on both pages    ${LIST_LOCATOR}    ${SUMMARY_LOCATOR}
    run keyword if    ${pagination}  ==  'FAIL'    check calculations on one page      ${LIST_LOCATOR}    ${SUMMARY_LOCATOR}

check calculations on one page
    [Arguments]    ${list_locator}    ${summary_locator}
    ${element} =     Get Webelements    ${list_locator}
    ${length_list} =     Get Length     ${element}
    log    ${length_list}
    ${length_summary} =     Get text    ${summary_locator}
    log    ${length_summary}
    Should Be Equal As Integers    ${length_summary}    ${length_list}

check calculations on both pages
    [Arguments]    ${list_locator}    ${summary_locator}
    ${element_page_two} =     Get Webelements    ${list_locator}
    ${length_list_page_two} =     Get Length    ${element_page_two}
    log    ${length_list_page_two}
    the user navigates to the page    ${applicationsForRTOComp}
    ${element} =     Get Webelements    ${list_locator}
    ${length_list} =     Get Length    ${element}
    log    ${length_list}
    ${total_length} =     Evaluate    ${length_list}+${length_list_page_two}
    log    ${total_length}
    ${length_summary} =     Get text    ${summary_locator}
    log    ${length_summary}
    Should Be Equal As Integers    ${length_summary}    ${total_length}

both calculations in the page should show the same
    ${APPLICATIONS_NUMBER_SUMMARY} =     get text    css = .column-two-thirds p span:nth-child(1)
    ${APPLICATIONS_NUMBER_LIST} =     Get text       css = .column-two-thirds span
    Should Be Equal As Integers    ${APPLICATIONS_NUMBER_LIST}    ${APPLICATIONS_NUMBER_SUMMARY}

open application calculations are correct
    the calculations should be correct    jQuery = td:contains("open")      css = .column-two-thirds p span:nth-child(1)

submitted application calculations are correct
    the calculations should be correct    jQuery = td:contains("submitted")    css = .info-area p:nth-child(5) span

the table header matches correctly
    ${pagination} =     Run Keyword And Ignore Error Without Screenshots    the user clicks the button/link    name = page
    Run Keyword If    ${pagination} == 'PASS'    check both pages of applications
    Run Keyword If    ${pagination} == 'FAIL'    check applications on one page

check both pages of applications
    ${row_count_second_page} =     Get Element Count    //*[td]
    convert to integer    ${row_count_second_page}
    log    ${row_count_second_page}
    the user navigates to the page    ${applicationsForRTOComp}
    ${row_count_first_page} =     Get Element Count    //*[td]
    convert to integer    ${row_count_first_page}
    log    ${row_count_first_page}
    ${total_application_count} =     evaluate    ${row_count_first_page}+${row_count_second_page}
    log    ${total_application_count}
    $[apps_string} =     Catenate    ${total_application_count}    applications
    the user should see the element       jQuery = .govuk-body span:contains("${apps_string}")

check applications on one page
    ${total_row_count} =     Get Element Count    //*[td]
    convert to integer    ${total_row_count}
    log    ${total_row_count}
    ${apps_string} =     Catenate    ${total_application_count}    applications
    the user should see the element       jQuery = .govuk-body span:contains("${apps_string}")

The calculation for the submited applications should be correct
    ${submitted_count} =     Get Element Count    //*[text()="submitted"]
    Run keyword if    ${submitted_count} ! =  0    submitted application calculations are correct

The calculation of the open applications should be correct
    ${open_count} =     Get Element Count    //*[text()="open"]
    Run keyword if    ${open_count} ! =  0    open application calculations are correct

The totals in the Key statistics should be correct
    #Calculation of the total number of Applications
    ${TOTAL_APPLICATIONS} =     Get Element Count    //table/tbody/tr
    ${TOTAL_COUNT} =     Get text    css = li:nth-child(1) > div > span
    Should Be Equal As Integers    ${TOTAL_APPLICATIONS}    ${TOTAL_COUNT}
    #Calculation of the Started Applications
    ${STARTED_APPLICATIONS} =     Get Element Count    //*[text()="Started"]
    ${STARTED_COUNT} =     Get text    css = li:nth-child(2) > div > span
    Should Be Equal As Integers    ${STARTED_APPLICATIONS}    ${STARTED_COUNT}
    #Calculation of the Submitted Applications
    ${SUBMITTED_APPLICATIONS} =     Get Element Count    //*[text()="Submitted"]
    ${SUBMITTED_COUNT} =     Get text    css = li:nth-child(4) > div > span
    Should Be Equal As Integers    ${SUBMITTED_APPLICATIONS}    ${SUBMITTED_COUNT}
    #TODO ADD Check for the beyond 50% counts when we will have test data

the user should should see lead and partners details
    the user should see the element    jQuery = #accordion-questions-content-1-1 h2:contains("Empire Ltd")+h3:contains("Organisation type")+p:contains("Business")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("Steve Smith")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("${lead_applicant}")
    the user should see the element    jQuery = #accordion-questions-content-1-1 h2:contains("EGGS")+h3:contains("Organisation type")+p:contains("Research")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("Pete Tom")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("81877706440")
    the user should see the element    jQuery = #accordion-questions-content-1-1 h2:contains("${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}")+h3:contains("Organisation type")+p:contains("Business")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("Ewan Cormack")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_EMAIL}")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("36267829240")
    the user should see the element    jQuery = #accordion-questions-content-1-1 h2:contains("${organisationLudlowName}")+h3:contains("Organisation type")+p:contains("Business")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("Jessica Doe")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("${PROJECT_SETUP_APPLICATION_1_PARTNER_EMAIL}")
    the user should see the element    jQuery = #accordion-questions-content-1-1 td:contains("15247172589")