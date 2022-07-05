*** Settings ***
Documentation     IFS-12177 Pre-reg/EOI next stage decision - input
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${quarantine_warning}    This file has been found to be unsafe

*** Test Cases ***
Application Dashboard
    [Documentation]    IFS-12177
    Given The user logs-in in new browser           &{Comp_admin1_credentials}
    when the user navigates to the page             ${SERVER}/management/competition/99
    And the user clicks the button/link             jQuery = a:contains("Applications: All, submitted, ineligible")
    And the user clicks the button/link             link = Expression of interest
    And the user should see the element             jQuery = h1:contains("Expression of interest")

Filter on application number
    [Documentation]    IFS-12177
    Given the user enters text to a text field          id = stringFilter    85
    When the user clicks the button/link                jQuery = button:contains("Filter")
    Then the user should see the element                jQuery = td:contains("Horizon Europe Guarantee Eoi Application3")
    And the user should see the element                 jQuery = td:contains("Successful")
    And the user should see the element                 jQuery = td:contains("85")
    And the user clicks the button/link                 jQuery = a:contains("Clear all filters")
    And the user should see the element                 jQuery = td:contains("Horizon Europe Guarantee Eoi Application1")
    And the user should see the element                 jQuery = td:contains("Successful")
    And the user should see the element                 jQuery = td:contains("83")

Filter on Sent
    [Documentation]    IFS-12177
    the user selects the option from the drop-down menu  Yes    id= sendFilter
    When the user clicks the button/link                 jQuery = button:contains("Filter")
    Then the user should see the element                 jQuery = td:contains("Horizon Europe Guarantee Eoi Application3")
    And the user should see the element                  jQuery = td:contains("Successful")
    And the user should see the element                  jQuery = td:contains("85")
    And the user should see the element                  jQuery = td:contains("Horizon Europe Guarantee Eoi Application1")
    And the user should see the element                  jQuery = td:contains("Successful")
    And the user should see the element                  jQuery = td:contains("83")
    And the user clicks the button/link                  jQuery = a:contains("Clear all filters")
    And the user should see the element                  jQuery = td:contains("Horizon Europe Guarantee Eoi Application2")
    And the user should see the element                  jQuery = td:contains("Unsuccessful")
    And the user should see the element                  jQuery = td:contains("84")

Filter on Expression of interest decision
    [Documentation]    IFS-12177
    the user selects the option from the drop-down menu  Successful    id= fundingFilter
    When the user clicks the button/link                 jQuery = button:contains("Filter")
    Then the user should see the element                 jQuery = td:contains("Horizon Europe Guarantee Eoi Application3")
    And the user should see the element                  jQuery = td:contains("Successful")
    And the user should see the element                  jQuery = td:contains("85")
    And the user should see the element                  jQuery = td:contains("Horizon Europe Guarantee Eoi Application1")
    And the user should see the element                  jQuery = td:contains("Successful")
    And the user should see the element                  jQuery = td:contains("83")
    And the user clicks the button/link                  jQuery = a:contains("Clear all filters")
    And the user should see the element                  jQuery = td:contains("Horizon Europe Guarantee Eoi Application2")
    And the user should see the element                  jQuery = td:contains("Unsuccessful")
    And the user should see the element                  jQuery = td:contains("84")


*** Keywords ***
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
