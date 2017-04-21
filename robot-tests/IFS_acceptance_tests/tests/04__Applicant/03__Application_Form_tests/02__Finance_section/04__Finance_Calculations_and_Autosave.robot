*** Settings ***
Documentation     INFUND-736: As an applicant I want to be able to add all the finance details for all the sections so I can sent in all the info necessary to apply
...
...               INFUND-438: As an applicant and I am filling in the finance details I want a fully working Other funding section
...
...               INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes
...
...               INFUND-6390 As an Applicant I will be invited to add project costs, organisation and funding details via links within the Finances section of my application
Suite Setup       log in and create new application if there is not one already with complete application details
Suite Teardown    the user closes the browser    # this keyword no longer needs to mark the application details as incomplete, due to the recent addition of research category this section is already incomplete
Force Tags        HappyPath    Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../FinanceSection_Commons.robot

*** Variables ***
${OTHER_FUNDING_SOURCE}    Alice
${OTHER_FUNDING_AMOUNT}    10000
${OTHER_FUNDING_DATE}    12-2008

*** Test Cases ***
Labour
    [Documentation]    INFUND-192, INFUND-736, INFUND-1256, INFUND-6390
    [Tags]
    [Setup]    Applicant navigates to the finances of the Robot application
    Given the user clicks the button/link    link=Your project costs
    When the Applicant fills in the Labour costs for two rows
    Then Totals should be correct    css=#section-total-189    £ 104,348    css=[data-mirror="#section-total-189"]    £ 104,348
    And the user clicks the button/link    name=remove_cost
    And The row should be removed    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    And the user reloads the page
    Then Totals should be correct    css=#section-total-189    £ 52,174    css=[data-mirror="#section-total-189"]    £ 52,174
    And the applicant edits the working days field
    Then Totals should be correct    css=#section-total-189    £ 48,000    css=[data-mirror="#section-total-189"]    £ 48,000
    [Teardown]    the user clicks the button/link    jQuery=button:contains("Labour")

Overhead costs
    [Documentation]    INFUND-192, INFUND-736, INFUND -6390 , INFUND-6788
    [Tags]
    # Check for No overheads costs option
    When the user clicks the button/link    jQuery=button:contains("Overhead costs")
    And The user clicks the button/link    jQuery=label:contains("No overhead costs")
    then the user should see the element    jQuery=h3:contains("No overhead costs")
    then the user clicks the button/link    jQuery=button:contains("Overhead costs")
    # Check for calculate overheads
    When the user clicks the button/link    jQuery=button:contains("Overhead costs")
    then the user clicks the button/link    css=label[data-target="overhead-total"]
    and the user should see the element    jQuery=a:contains("overhead calculation spreadsheet.xlsx")
    and the user should see the element    jQuery=a:contains("overhead calculation spreadsheet.ods")
    # Check for 20% Labour costs option
    When the user clicks the button/link    jQuery=button:contains("Overhead costs")
    then the user chooses 20% overheads option
    and admin costs total should be correct    id=section-total-190-default    £ 9,600
    [Teardown]    the user clicks the button/link    jQuery=button:contains("Overhead costs")

Materials
    [Documentation]    INFUND-192, INFUND-736, INFUND-6390
    [Tags]
    When the Applicant fills the Materials fields
    Then Totals should be correct with the old styling    css=#section-total-191    £ 2,000    css=[data-mirror="#section-total-191"]    £ 2,000
    And the user clicks the button/link    css=#material-costs-table tbody tr:nth-child(1) button
    And the user reloads the page
    Then Totals should be correct with the old styling   css=#section-total-191    £ 1,000    css=[data-mirror="#section-total-191"]    £ 1,000
    [Teardown]    the user clicks the button/link    jQuery=button:contains("Materials")

Capital usage
    [Documentation]    INFUND-736, INFUND-6390
    [Tags]
    When the applicant fills the 'capital usage' field
    Then Totals should be correct with the old styling   css=#section-total-192    £ 200    css=[data-mirror="#section-total-192"]    £ 200
    And the user clicks the button/link    css=#capital_usage [data-repeatable-row]:nth-child(1) button
    And the user reloads the page
    Then Totals should be correct with the old styling   css=#section-total-192    £ 100    css=[data-mirror="#section-total-192"]    £ 100
    And the user clicks the button/link    css=#capital_usage [data-repeatable-row]:nth-child(1) button
    [Teardown]    the user clicks the button/link    jQuery=button:contains("Capital usage")

Capital usage - negative total
    [Documentation]    INFUND-4879, INFUND-6390
    [Tags]
    When the applicant fills the 'capital usage' field to a negative value
    Then Totals should be correct with the old styling   css=#section-total-192    £ 0    css=[data-mirror="#section-total-192"]    £ 0
    And the user clicks the button/link    css=#capital_usage [data-repeatable-row]:nth-child(1) button
    [Teardown]    the user clicks the button/link    jQuery=button:contains("Capital usage")

Subcontracting costs
    [Documentation]    INFUND-192, INFUND-736, INFUND-2303, INFUND-6390
    [Tags]
    When the applicant edits the Subcontracting costs section
    Then the user should see the element    jQuery=button:contains("Subcontracting") > *:contains("£ 200")
    [Teardown]    the user clicks the button/link    jQuery=button:contains("Subcontracting costs")

Travel and subsistence
    [Documentation]    INFUND-736, INFUND-6390
    [Tags]
    When the Applicant fills the Travel fields
    Then Totals should be correct with the old styling    css=#section-total-194    £ 2,000    css=[data-mirror="#section-total-194"]    £ 2,000
    And the user clicks the button/link    css=#travel-costs-table [data-repeatable-row]:nth-child(1) button
    And the user reloads the page
    Then Totals should be correct with the old styling    css=#section-total-194    £ 1,000    css=[data-mirror="#section-total-194"]    £ 1,000
    [Teardown]    the user clicks the button/link    jQuery=button:contains("Travel and subsistence")

Other costs
    [Documentation]    INFUND-736, INFUND-6390
    [Tags]
    When the applicant adds one row for the other costs
    Then Totals should be correct with the old styling    id=section-total-195    £ 200    css=[data-mirror="#section-total-195"]    £ 200
    Then the user reloads the page
    Then Totals should be correct with the old styling    id=section-total-195    £ 200    css=[data-mirror="#section-total-195"]    £ 200
    [Teardown]    the user clicks the button/link    jQuery=button:contains("Other costs")

*** Keywords ***
the Applicant fills in the Labour costs for two rows
    the user clicks the button/link    jQuery=button:contains("Labour")
    the user should see the element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    the user clears the text from the element    css=[name^="labour-labourDaysYearly"]
    the user enters text to a text field    css=[name^="labour-labourDaysYearly"]    230
    the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
    the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    the user moves focus to the element    jQuery=button:contains('Add another role')
    the user clicks the button/link    jQuery=button:contains('Add another role')
    the user should see the element    css=.labour-costs-table tr:nth-of-type(2) td:nth-of-type(4) input
    the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(2) td:nth-of-type(2) input    120000
    the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(2) td:nth-of-type(4) input    100
    the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(2) td:nth-of-type(1) input    test

the applicant edits the working days field
    the user should see the element    css=[name^="labour-labourDaysYearly"]
    the user clears the text from the element    css=[name^="labour-labourDaysYearly"]
    the user enters text to a text field    css=[name^="labour-labourDaysYearly"]    250
    the user moves focus to the element    jQuery=button:contains("Labour")
    wait for autosave

the Applicant fills the Materials fields
    the user clicks the button/link    jQuery=button:contains("Materials")
    the user should see the element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user moves focus to the element    jQuery=button:contains(Add another materials cost)
    the user clicks the button/link    jQuery=button:contains(Add another materials cost)
    the user should see the element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    test
    the user moves focus to the element    jQuery=button:contains("Materials")

the applicant edits the Subcontracting costs section
    the user clicks the button/link    jQuery=button:contains("Subcontracting costs")
    the user should see the text in the page    Subcontractor name
    the user enters text to a text field    css=#collapsible-4 .form-row:nth-child(1) input[id$=subcontractingCost]    100
    the user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-name"]    test1
    the user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-country-"]    test2
    the user moves focus to the element    jQuery=button:contains(Add another subcontractor)
    the user clicks the button/link    jQuery=button:contains(Add another subcontractor)
    the user should see the element    css=#collapsible-4 .form-row:nth-child(2)
    the user enters text to a text field    css=.form-row:nth-child(2) [name^="subcontracting-name"]    test1
    the user enters text to a text field    css=.form-row:nth-child(2) [name^="subcontracting-country-"]    test2
    the user enters text to a text field    css=.form-row:nth-child(2) [name^="subcontracting-role"]    test3
    the user enters text to a text field    css=#collapsible-4 .form-row:nth-child(2) input[id$=subcontractingCost]    100
    the user enters text to a text field    css=#collapsible-4 .form-row:nth-child(1) input[id$=name]    test
    the user moves focus to the element    jQuery=button:contains("Subcontracting costs")

the applicant fills the 'capital usage' field
    the user clicks the button/link    jQuery=button:contains("Capital usage")
    the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-residual-value    900
    the user enters text to a text field    css=.form-finances-capital-usage-utilisation    100
    the user enters text to a text field    css=.form-finances-capital-usage-depreciation    11
    the user enters text to a text field    css=.form-row:nth-child(1) [name^="capital_usage-description"]    Test
    the user clicks the button/link    jQuery=.form-row:nth-child(1) label:contains(Existing)
    wait for autosave
    the user moves focus to the element    jQuery=button:contains(Add another asset)
    the user clicks the button/link    jQuery=button:contains(Add another asset)
    the user should see the element    css=.form-row:nth-child(2) .form-finances-capital-usage-npv
    the user enters text to a text field    css=.form-row:nth-child(2) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css=.form-row:nth-child(2) .form-finances-capital-usage-residual-value    900
    the user enters text to a text field    css=.form-row:nth-child(2) .form-finances-capital-usage-utilisation    100
    the user enters text to a text field    css=.form-row:nth-child(2) .form-finances-capital-usage-depreciation    10
    the user enters text to a text field    css=.form-row:nth-child(2) [name^="capital_usage-description"]    Test
    the user clicks the button/link    jQuery=.form-row:nth-child(2) label:contains(Existing)
    the user moves focus to the element    jQuery=button:contains("Capital usage")

the applicant fills the 'capital usage' field to a negative value
    the user clicks the button/link    jQuery=button:contains("Capital usage")
    the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-residual-value    9000
    the user enters text to a text field    css=.form-finances-capital-usage-utilisation    100
    the user enters text to a text field    css=.form-finances-capital-usage-depreciation    11
    the user enters text to a text field    css=.form-row:nth-child(1) [name^="capital_usage-description"]    Test
    the user clicks the button/link    jQuery=.form-row:nth-child(1) label:contains(Existing)
    the user moves focus to the element    jQuery=button:contains("Capital usage")

the Applicant fills the Travel fields
    the user clicks the button/link    jQuery=button:contains("Travel and subsistence")
    the user should see the element    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user moves focus to the element    jQuery=button:contains(Add another travel cost)
    the user clicks the button/link    jQuery=button:contains(Add another travel cost)
    the user should see the element    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    test
    the user moves focus to the element    jQuery=button:contains("Travel and subsistence")

the applicant adds one row for the other costs
    the user clicks the button/link    jQuery=button:contains("Other costs")
    the user should see the element    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    100
    the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) textarea    test
    the user clicks the button/link    jQuery=button:contains(Add another cost)
    the user should see the element    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) textarea    test
    the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    100
    the user moves focus to the element    jQuery=button:contains("Other costs")

the total of the other funding should be correct
    the user should see the element    id=other-funding-total
    Textfield Value Should Be    id=other-funding-total    £ 20,000

The applicant cannot see the 'other funding' details
    the user should not see the text in the page    ${OTHER_FUNDING_SOURCE}
    the user should not see the text in the page    ${OTHER_FUNDING_DATE}
    the user should not see the text in the page    ${OTHER_FUNDING_AMOUNT}

The applicant can leave the 'Your finances' page but the details are still saved
    Execute Javascript    jQuery('form').attr('data-test','true');
    the user reloads the page
    the user should see the element    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input
    Textfield Should Contain    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    Textfield Should Contain    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}

The applicant selects 'Yes' and fills two rows
    the user clicks the button/link    jQuery=label:contains(Yes)
    Run Keyword And Ignore Error Without Screenshots    Click element    jQuery=#other-funding-table button:contains("Remove")
    the user should see the element    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2)
    the user should see the element    id=other-funding-table
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    ${OTHER_FUNDING_AMOUNT}
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    the user moves focus to the element    jQuery=button:contains(Add another source of funding)
    the user clicks the button/link    jQuery=button:contains(Add another source of funding)
    the user should see the element    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(2)
    the user clicks the button/link    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    ${OTHER_FUNDING_AMOUNT}
    the user should see the element    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    the user moves focus to the element    jQuery=button:contains(Add another source of funding)

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
    Textfield Should Contain    ${ADMIN_TOTAL}    ${ADMIN_VALUE}
    Element Should Contain    jQuery=button:contains("Overhead costs")    ${ADMIN_VALUE}

the total costs should reflect overheads
    [Arguments]    ${ADMIN_TOTAL}    ${ADMIN_VALUE}
    Textfield Value Should Be    ${ADMIN_TOTAL}    ${ADMIN_VALUE}

The row should be removed
    [Arguments]    ${ROW}
    the user should not see the element    ${ROW}
