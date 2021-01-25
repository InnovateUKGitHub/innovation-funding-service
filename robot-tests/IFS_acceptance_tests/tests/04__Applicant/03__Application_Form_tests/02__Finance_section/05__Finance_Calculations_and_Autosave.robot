*** Settings ***
Documentation     INFUND-736: As an applicant I want to be able to add all the finance details for all the sections so I can sent in all the info necessary to apply
...
...               INFUND-438: As an applicant and I am filling in the finance details I want a fully working Other funding section
...
...               INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes
...
...               INFUND-6390 As an Applicant I will be invited to add project costs, organisation and funding details via links within the Finances section of my application
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot

*** Variables ***
${OTHER_FUNDING_SOURCE}    Alice
${OTHER_FUNDING_AMOUNT}    10000
${OTHER_FUNDING_DATE}    12-2008

*** Test Cases ***
Labour
    [Documentation]    INFUND-192, INFUND-736, INFUND-1256, INFUND-6390
    [Tags]  HappyPath
    [Setup]    Applicant navigates to the finances of the Robot application
    Given the user clicks the button/link                       link = Your project costs
    When the Applicant fills in the Labour costs for two rows
    And the user expands the section                            Labour
    Then Totals should be correct                               jQuery = h4:contains("Total labour costs") [data-mirror^="#section-total"]  £104,348  jQuery = [data-mirror^="#section-total-labour"]  £104,348
    And the user clicks the button/link                         name = remove_row
    And The row should be removed                               css = .labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    And the user reloads page with autosave
    Then Totals should be correct                               jQuery = h4:contains("Total labour costs") [data-mirror^="#section-total"]  £52,174   jQuery = [data-mirror^="#section-total-labour"]  £52,174
    And the applicant edits the working days field
    Then Totals should be correct                               jQuery = h4:contains("Total labour costs") [data-mirror^="#section-total"]  £48,000   jQuery = [data-mirror^="#section-total-labour"]  £48,000
    [Teardown]    the user clicks the button/link               jQuery = button:contains("Labour")

Overhead costs
    [Documentation]    INFUND-192, INFUND-736, INFUND -6390 , INFUND-6788
    [Tags]  HappyPath
    # Check for No overheads costs option
    Given the user expands the section            Overhead costs
    When The user clicks the button/link          jQuery = label:contains("No overhead costs")
    Then the user should see the element          jQuery = span:contains("£0")
    # Check for calculate overheads
    Then the user clicks the button/link          css = [data-target="overhead-total"] label
    And the user should see the element           jQuery = a:contains("overhead calculation spreadsheet.xlsx")
    And the user should see the element           jQuery = a:contains("overhead calculation spreadsheet.ods")
    # Check for 20% Labour costs option
    When the user clicks the button/link          jQuery = button:contains("Overhead costs")
    Then the user chooses 20% overheads option
    And admin costs total should be correct       jQuery = [data-mirror^="#section-total-overhead"]  £9,600
    [Teardown]  the user clicks the button/link   jQuery = button:contains("Overhead costs")

Materials
    [Documentation]    INFUND-192, INFUND-736, INFUND-6390
    [Tags]  HappyPath
    When the Applicant fills the Materials fields
    Then Totals should be correct                  jQuery = h4:contains("Total materials costs") [data-mirror^="#section-total"]  £2,000  jQuery = [data-mirror^="#section-total-material"]  £2,000
    And the user clicks the button/link            css = #material-costs-table tbody tr:nth-of-type(1) button
    And the user reloads page with autosave
    Then Totals should be correct                  jQuery = h4:contains("Total materials costs") [data-mirror^="#section-total"]    £1,000  jQuery = [data-mirror^="#section-total-material"]  £1,000
    [Teardown]    the user clicks the button/link  jQuery = button:contains("Materials")

Capital usage
    [Documentation]    INFUND-736, INFUND-6390
    [Tags]  HappyPath
    When the applicant fills the 'capital usage' field
    Then Totals should be correct                       jQuery = h4:contains("Total capital usage costs") [data-mirror^="#section-total"]  £200  jQuery = [data-mirror^="#section-total-capital-usage"]  £200
    And the user clicks the button/link                 css = #capital-usage [data-repeatable-row]:nth-child(1) button
    And the user reloads page with autosave
    Then Totals should be correct                       jQuery = h4:contains("Total capital usage costs") [data-mirror^="#section-total"]  £100  jQuery = [data-mirror^="#section-total-capital-usage"]  £100
    And the user clicks the button/link                 css = #capital-usage [data-repeatable-row]:nth-child(1) button
    [Teardown]    the user clicks the button/link       jQuery = button:contains("Capital usage")

Capital usage - negative total
    [Documentation]    INFUND-4879, INFUND-6390
    [Tags]  HappyPath
    When the applicant fills the 'capital usage' field to a negative value
    Then Totals should be correct                  jQuery = h4:contains("Total capital usage costs") [data-mirror^="#section-total"]  £0  jQuery = [data-mirror^="#section-total-capital-usage"]  £0
    And the user clicks the button/link            css = #capital-usage [data-repeatable-row]:nth-child(1) button
    [Teardown]    the user clicks the button/link  jQuery = button:contains("Capital usage")

Subcontracting costs
    [Documentation]    INFUND-192, INFUND-736, INFUND-2303, INFUND-6390
    [Tags]  HappyPath
    When the applicant edits the Subcontracting costs section
    Then Wait Until Element Contains Without Screenshots    jQuery = [data-mirror^="#section-total-subcontracting"]  £200
    [Teardown]    the user clicks the button/link           jQuery = button:contains("Subcontracting")

Travel and subsistence
    [Documentation]    INFUND-736, INFUND-6390
    [Tags]  HappyPath
    When the Applicant fills the Travel fields
    Then Totals should be correct                jQuery = h4:contains("Total travel and subsistence costs") [data-mirror^="#section-total"]  £2,000  jQuery = [data-mirror^="#section-total-travel"]  £2,000
    And the user clicks the button/link          css = #travel-costs-table [data-repeatable-row]:nth-child(1) button
    And the user reloads page with autosave
    Then Totals should be correct                jQuery = h4:contains("Total travel and subsistence costs") [data-mirror^="#section-total"]  £1,000  jQuery = [data-mirror^="#section-total-travel"]  £1,000
    [Teardown]  the user clicks the button/link  jQuery = button:contains("Travel and subsistence")

Other costs
    [Documentation]    INFUND-736, INFUND-6390
    [Tags]  HappyPath
    When the applicant adds one row for the other costs
    Then Totals should be correct                        jQuery = h4:contains("Total other costs") [data-mirror^="#section-total"]  £200  jQuery = [data-mirror^="#section-total-other"]  £200
    Then the user reloads page with autosave
    Then Totals should be correct                        jQuery = h4:contains("Total other costs") [data-mirror^="#section-total"]  £200  jQuery = [data-mirror^="#section-total-other"]  £200
    [Teardown]    the user clicks the button/link        jQuery = button:contains("Other costs")

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    Connect to database  @{database}
    log in and create new application if there is not one already with complete application details  Robot test application  ${tomorrowday}  ${month}  ${nextyear}

the Applicant fills in the Labour costs for two rows
    the user clicks the button/link            jQuery = button:contains("Labour")
    the user should see the element            css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field       id = working-days-per-year    230
    the user enters text to a text field       css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user enters text to a text field       css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
    the user enters text to a text field       css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    Set Focus to Element                       jQuery = button:contains('Add another role')
    the user clicks the button/link            jQuery = button:contains('Add another role')
    the user should see the element            css = #labour-costs-table tr:nth-of-type(2) td:nth-of-type(4) input
    the user enters text to a text field       css = #labour-costs-table tr:nth-of-type(2) td:nth-of-type(2) input    120000
    the user enters text to a text field       css = #labour-costs-table tr:nth-of-type(2) td:nth-of-type(4) input    100
    the user enters text to a text field       css = #labour-costs-table tr:nth-of-type(2) td:nth-of-type(1) input    test

the applicant edits the working days field
    the user should see the element            id = working-days-per-year
    the user enters text to a text field       id = working-days-per-year    250
    Set Focus to Element                       jQuery = button:contains("Labour")
    wait for autosave

the Applicant fills the Materials fields
    the user clicks the button/link       jQuery = button:contains("Materials")
    the user should see the element       css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    Set Focus To Element                  jQuery = button:contains(Add another materials cost)
    the user clicks the button/link       jQuery = button:contains(Add another materials cost)
    the user should see the element       css = #material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    test
    Set Focus To Element                  jQuery = button:contains("Materials")

the applicant edits the Subcontracting costs section
    the user clicks the button/link             jQuery = button:contains("Subcontracting")
    the user should see the element             jQuery = label:contains("Subcontractor name")
    the user enters text to a text field        css = #accordion-finances-content-5 .form-row:nth-child(1) [name$=".cost"]   100
    the user enters text to a text field        css = #accordion-finances-content-5 .form-row:nth-child(1) [name$=".name"]    test1
    the user enters text to a text field        css = #accordion-finances-content-5 .form-row:nth-child(1) [name$=".country"]    test2
    the user clicks the button/link             jQuery = button:contains(Add another subcontractor)
    the user should see the element             css = #accordion-finances-content-5 .form-row:nth-child(2)
    the user enters text to a text field        css = .form-row:nth-child(2) [name$=".name"]    test1
    the user enters text to a text field        css = .form-row:nth-child(2) [name$=".country"]    test2
    the user enters text to a text field        css = .form-row:nth-child(2) [name$=".role"]    test3
    the user enters text to a text field        css = #accordion-finances-content-5 .form-row:nth-child(2) [name$=".cost"]   100
    the user enters text to a text field        css = #accordion-finances-content-5 .form-row:nth-child(1) [name$=".name"]    test
    Set Focus To Element                        jQuery = button:contains("Subcontracting")

the applicant fills the 'capital usage' field
    the user clicks the button/link         jQuery = button:contains("Capital usage")
    the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-residual-value    900
    the user enters text to a text field    css = .form-finances-capital-usage-utilisation    100
    the user enters text to a text field    css = .form-finances-capital-usage-depreciation    11
    the user enters text to a text field    css = .form-row:nth-child(1) [name$="item"]    Test
    the user clicks the button/link         jQuery = .form-row:nth-child(1) label:contains(Existing)
    wait for autosave
    Set Focus To Element                    jQuery = button:contains(Add another asset)
    the user clicks the button/link         jQuery = button:contains(Add another asset)
    the user should see the element         css = .form-row:nth-child(2) .form-finances-capital-usage-npv
    the user enters text to a text field    css = .form-row:nth-child(2) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css = .form-row:nth-child(2) .form-finances-capital-usage-residual-value    900
    the user enters text to a text field    css = .form-row:nth-child(2) .form-finances-capital-usage-utilisation    100
    the user enters text to a text field    css = .form-row:nth-child(2) .form-finances-capital-usage-depreciation    10
    the user enters text to a text field    css = .form-row:nth-child(2) [name$="item"]    Test
    the user clicks the button twice        jQuery = .form-row:nth-child(2) label:contains("Existing")
    Set Focus To Element                    jQuery = button:contains("Capital usage")

the applicant fills the 'capital usage' field to a negative value
    the user clicks the button/link         jQuery = button:contains("Capital usage")
    the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-residual-value    9000
    the user enters text to a text field    css = .form-finances-capital-usage-utilisation    100
    the user enters text to a text field    css = .form-finances-capital-usage-depreciation    11
    the user enters text to a text field    css = .form-row:nth-child(1) [name$="item"]    Test
    the user clicks the button twice        jQuery = .form-row:nth-child(1) label:contains("Existing")
    Set Focus To Element                    jQuery = button:contains("Capital usage")

the Applicant fills the Travel fields
    the user clicks the button/link         jQuery = button:contains("Travel and subsistence")
    the user should see the element         css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    Set Focus To Element                    jQuery = button:contains(Add another travel cost)
    the user clicks the button/link         jQuery = button:contains(Add another travel cost)
    the user should see the element         css = #travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    test
    Set Focus To Element                    jQuery = button:contains("Travel and subsistence")

the applicant adds one row for the other costs
    the user clicks the button/link         jQuery = button:contains("Other costs")
    the user should see the element         css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    100
    the user enters text to a text field    css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) textarea    test
    the user clicks the button/link         jQuery = button:contains(Add another cost)
    the user should see the element         css = #other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    the user enters text to a text field    css = #other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) textarea    test
    the user enters text to a text field    css = #other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    100
    Set Focus To Element                    jQuery = button:contains("Other costs")

Totals should be correct
    [Arguments]    ${TOTAL_FIELD}    ${FIELD_VALUE}    ${TOTAL_COLLAPSIBLE}    ${COLLAPSIBLE_VALUE}
    the user should see the element    ${total_field}
    the user should see the element    ${total_collapsible}
    Wait Until Element Contains Without Screenshots     ${TOTAL_FIELD}    ${FIELD_VALUE}
    Wait Until Element Contains Without Screenshots    ${TOTAL_COLLAPSIBLE}    ${COLLAPSIBLE_VALUE}

Totals should be correct with the old styling
    [Arguments]    ${TOTAL_FIELD}    ${FIELD_VALUE}    ${TOTAL_COLLAPSIBLE}    ${COLLAPSIBLE_VALUE}
    the user should see the element    ${total_field}
    the user should see the element    ${total_collapsible}
    Textfield Value Should Be     ${TOTAL_FIELD}    ${FIELD_VALUE}
    Wait Until Element Contains Without Screenshots    ${TOTAL_COLLAPSIBLE}    ${COLLAPSIBLE_VALUE}

Admin costs total should be correct
    [Arguments]    ${ADMIN_TOTAL}    ${ADMIN_VALUE}
    Wait Until Element Contains Without Screenshots    ${ADMIN_TOTAL}    ${ADMIN_VALUE}
    Element Should Contain    ${ADMIN_TOTAL}    ${ADMIN_VALUE}

The row should be removed
    [Arguments]    ${ROW}
    the user should not see the element    ${ROW}

Custom suite teardown
    The user closes the browser
    Disconnect from database
