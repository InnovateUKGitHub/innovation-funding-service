*** Settings ***
Documentation     INFUND-736: As an applicant I want to be able to add all the finance details for all the sections so I can sent in all the info necessary to apply
...
...               INFUND-438: As an applicant and I am filling in the finance details I want a fully working Other funding section
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        HappyPath
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${OTHER_FUNDING_SOURCE}    Alice
${OTHER_FUNDING_AMOUNT}    10000
${OTHER_FUNDING_DATE}    12-2008

*** Test Cases ***
Labour
    [Documentation]    INFUND-192
    ...    Acceptance tests for the Labour section calculations
    ...    INFUND-736
    ...    INFUND-1256
    [Tags]    Labour    Autosave    Calculations    Finance
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    And the Applicant fills in the Labour costs
    And the calculations of the labour should be correct
    When the applicant fills a second row in the labour costs
    And the total labour cost calculation should be correct
    And the applicant removes one labour row
    Then the labour total should be correct again
    And the user reloads the page
    And the labour total should be correct again
    And the applicant edits the working days field
    And the labour total should have been updated

Overheads
    [Documentation]    INFUND-192
    ...    Acceptance tests for the Overheads section calculations
    ...    INFUND-736
    [Tags]    Overheads    Autosave    Calculations    Finance
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    And the Applicant fills in the Overheads costs
    And the calculations of the overheads should be correct
    When the applicant changes the data in the overheads section
    And the calculations of the overheads should still be correct
    And the applicant changes the data in the overheads section again
    Then the calculations of the overheads should be correct again
    And the user reloads the page
    And the calculations of the overheads should be correct once more

Materials
    [Documentation]    INFUND-192
    ...    INFUND-736
    [Tags]    Materials    Autosave    Calculations    Finance
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    And the Applicant fills the Materials fields
    And the calculations of the Materials should be correct
    When the applicant fills a second row in the materials section
    And the total materials costs calculations should be correct
    And the applicant removes the material rows
    Then the calculations of the Materials should be correct again
    And the user reloads the page
    And the calculations of the Materials should be correct again

Capital usage
    [Documentation]    INFUND-736
    [Tags]    Capital Usage    Autosave    Calculations    Finance
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    And the applicant fills the 'capital usage' field
    And the calculations of the 'capital usage' should be correct
    When the applicant fills a new subcontractor
    And the total calculation of the capital usage should be correct
    And the applicant removes one subcontractor row
    Then the total of the capital usage should be correct again
    And the user reloads the page
    And the total of the capital usage should be correct again

Subcontracting costs
    [Documentation]    INFUND-192
    ...    INFUND-736
    [Tags]    Subcontracting Costs    Autosave    Calculations    Finance
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    And the applicant edits the Subcontracting costs section
    When the applicant adds a new row in the subcontracting costs
    And the total subcontracting costs should be correct
    And the applicant removes one subcontracting row
    Then the total subcontracting total should be correct again
    And the user reloads the page
    And the total subcontracting total should be correct again

Travel and subsistence
    [Documentation]    INFUND-736
    [Tags]    Travel and subsistence    Autosave    Calculations    Finance
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    And the Applicant fills the Travel fields
    And the calculations of the Travel and subsistence should be correct
    When the applicant fills a second row in the travel and subsistence section
    And the total travel and subsistence costs calculations should be correct
    And the applicant removes one travel and subsistence row
    Then the calculations of the travel and subsistence should be correct
    And the user reloads the page
    And the calculations of the travel and subsistence should be correct

Other costs
    [Documentation]    INFUND-736
    [Tags]    Other costs    Autosave    Calculations    Finance
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    And the applicant adds one row for the other costs
    When the applicant adds a second row for the other costs fields
    And the other costs total should be correct
    Then the user reloads the page
    And the other costs total should be correct

Other Funding
    [Documentation]    INFUND-438
    [Tags]    Applicant    Application    Finances    Other funding     Pending
    # Pending due to bug INFUND-1943
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    And Applicant selects 'Yes' for other funding
    And Applicant chooses to add another source of funding
    When Applicant can see a new row
    And Applicant enters some details into this row
    And Applicant chooses to add yet another source of funding
    And the applicant enters some details into the second row
    Then the total of the other funding should be correct
    And Applicant can leave the 'Your finances' page but the details are still saved
    And applicant selects 'No' for other funding
    And applicant can see that the 'No' radio button is selected
    And applicant cannot see the 'other funding' details

Mark all section as complete
    [Documentation]    INFUND-446
    [Tags]    Applicant    Application    Finances
    When Applicant clicks on "Mark all as complete"
    Then the user should be redirected to the correct page    ${APPLICATION_OVERVIEW_URL}
    And the user clicks "Your finances"
    And the user will be in Finance section


*** Keywords ***
the Applicant fills in the Labour costs
    Click Element    css=[aria-controls="collapsible-1"]
    Click Element    jQuery=button:contains('Add another role')
    Sleep    1s
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    Clear Element Text    css=#cost-labour-1-workingDays
    Input Text    css=#cost-labour-1-workingDays    230
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    test

the calculations of the labour should be correct
    Sleep    1s
    Textfield Value Should Be    css=.labour-costs-table tbody td:nth-of-type(3) input    £ 522
    Textfield Value Should Be    jQuery=#collapsible-1 td:contains("Total costs") input    £ 52,174

the applicant removes one labour row
    Click Element    name=remove_cost

the labour total should be correct again
    sleep    1s
    Textfield Value Should Be    css=#section-total-9    £ 52,174
    Element Should Contain    css=[data-mirror="#section-total-9"]    £ 52,174

the total labour cost calculation should be correct
    Sleep    1s
    Textfield Value Should Be    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(3) input    £ 522
    Textfield Value Should Be    css=.labour-costs-table tr:nth-of-type(2) input    £ 52,174
    Textfield Value Should Be    css=#section-total-9    £ 104,348
    Element Should Contain    css=[data-mirror="#section-total-9"]    £ 104,348

the applicant fills a second row in the labour costs
    Click Element    jQuery=button:contains('Add another role')
    Sleep    1s
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(1) input    test
    Sleep    1s

the applicant edits the working days field
    Click Element    css=[aria-controls="collapsible-1"]
    Wait Until Element Is Visible    css=#cost-labour-1-workingDays
    Clear Element Text    css=#cost-labour-1-workingDays
    Input Text    css=#cost-labour-1-workingDays    250
    Focus    css=.app-submit-btn
    Sleep    1s

the labour total should have been updated
    Textfield Value Should Be    css=#section-total-9    £ 48,000
    Element Should Contain    css=[data-mirror="#section-total-9"]    £ 48,000

the applicant fills in the overheads costs
    Click Element    css=[aria-controls="collapsible-2"]
    Select Radio Button    overheads-rateType-29-51    DEFAULT_PERCENTAGE
    focus    css=.app-submit-btn
    sleep    1s

the calculations of the overheads should be correct
    Wait Until Element Is Visible    id=section-total-10-default
    Textfield Value Should Be    id=section-total-10-default    £ 9,600
    Element Should Contain    css=[aria-controls="collapsible-2"] [data-mirror]    £ 9,600

the applicant changes the data in the overheads section
    Select Radio Button    overheads-rateType-29-51    CUSTOM_RATE
    Input Text    id=cost-overheads-51-customRate    30
    focus    css=.app-submit-btn
    sleep    1s

the calculations of the overheads should still be correct
    Wait Until Element Is Visible    id=section-total-10-custom
    Textfield Value Should Be    id=section-total-10-custom    £ 14,400
    Element Should Contain    css=[aria-controls="collapsible-2"] [data-mirror]    £ 14,400

the applicant changes the data in the overheads section again
    Select Radio Button    overheads-rateType-29-51    SPECIAL_AGREED_RATE
    Input Text    id=cost-overheads-51-agreedRate    40
    focus    css=.app-submit-btn
    sleep    1s

the calculations of the overheads should be correct again
    Wait Until Element Is Visible    id=section-total-10-special
    Textfield Value Should Be    id=section-total-10-special    £ 19,200
    Element Should Contain    css=[aria-controls="collapsible-2"] [data-mirror]    £ 19,200

the calculations of the overheads should be correct once more
    Click Element    css=[aria-controls="collapsible-2"]
    the calculations of the overheads should be correct again

the Applicant fills the Materials fields
    Click Element    xpath=//*[@aria-controls="collapsible-3"]
    Click Element    jQuery=button:contains('Add another materials cost')
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    input text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    focus    css=.app-submit-btn

the applicant removes the material rows
    click Element    css=#material-costs-table tbody tr:nth-child(1) button
    sleep    1s
    click Element    css=#material-costs-table tbody tr:nth-child(1) button
    sleep    1s

the applicant fills a second row in the materials section
    Click Element    jQuery=button:contains('Add another materials cost')
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    test
    sleep    1s

the calculations of the Materials should be correct again
    Textfield Value Should Be    css=#section-total-11    £ 0

the total materials costs calculations should be correct
    Textfield Value Should Be    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(4) input    £ 1,000
    Textfield Value Should Be    css=#section-total-11    £ 2,000

the applicant edits the Subcontracting costs section
    Click Element    css=[aria-controls="collapsible-5"]
    Click Element    jQuery=button:contains('Add another subcontractor')
    Wait Until Page Contains Element    css=#collapsible-5 .form-row:nth-child(1)
    Input Text    css=#collapsible-5 .form-row:nth-child(1) input[id$=subcontractingCost]    100
    input text    css=#collapsible-5 .form-row:nth-child(1) input[id$=companyName]    test
    focus    css=.app-submit-btn
    sleep    1s

The total subcontracting costs should be correct
    Sleep    1s
    Textfield Value Should Be    css=#section-total-13    £ 200
    Element Should Contain    css=[aria-controls="collapsible-5"] [data-mirror]    £ 200

the applicant adds a new row in the subcontracting costs
    Click Element    jQuery=button:contains('Add another subcontractor')
    Wait Until Page Contains Element    css=#collapsible-5 .form-row:nth-child(2)
    Input Text    css=#collapsible-5 .form-row:nth-child(2) input[id$=subcontractingCost]    100
    input text    css=#collapsible-5 .form-row:nth-child(2) input[id$=companyName]    test
    sleep    1s
    focus    css=.app-submit-btn

the applicant fills the 'capital usage' field
    Click Element    css=[aria-controls="collapsible-4"]
    Click Element    jQuery=button:contains('Add another asset')
    Wait Until Element Is Visible    css=#capital_usage [name="remove_cost"]
    Input Text    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    input text    css=.form-row:nth-child(1) .form-finances-capital-usage-residual-value    900
    input text    css=.form-finances-capital-usage-utilisation    100
    input text    css=.form-finances-capital-usage-depreciation    11
    focus    css=.app-submit-btn

the calculations of the 'capital usage' should be correct
    Textfield Value Should Be    css=#section-total-12    £ 100

the applicant fills a new subcontractor
    Click Element    jQuery=button:contains('Add another asset')
    Wait Until Element Is Visible    css=.form-row:nth-child(2) .form-finances-capital-usage-npv
    Input Text    css=.form-row:nth-child(2) .form-finances-capital-usage-npv    1000
    input text    css=.form-row:nth-child(2) .form-finances-capital-usage-residual-value    900
    input text    css=.form-row:nth-child(2) .form-finances-capital-usage-utilisation    100
    Input Text    css=.form-row:nth-child(2) .form-finances-capital-usage-depreciation    10

the total calculation of the capital usage should be correct
    focus    css=.app-submit-btn
    sleep    1s
    Textfield Value Should Be    css=#section-total-12    £ 200

the applicant removes one subcontractor row
    click Element    css=#capital_usage [data-repeatable-row]:nth-child(1) button

the total of the capital usage should be correct again
    sleep    1s
    Textfield Value Should Be    css=#section-total-12    £ 100

the applicant removes one subcontracting row
    click Element    css=#subcontracting_costs [data-repeatable-row]:nth-child(1) button

the total subcontracting total should be correct again
    Sleep    1s
    Click Element    css=[aria-controls="collapsible-5"]
    Textfield Value Should Be    css=#section-total-13    £ 100
    Element Should Contain    css=[aria-controls="collapsible-5"] [data-mirror]    £ 100

the Applicant fills the Travel fields
    Click Element    css=[aria-controls="collapsible-6"]
    Click Element    jQuery=button:contains('Add another travel cost')
    Wait Until Page Contains Element    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    focus    css=.app-submit-btn
    sleep    1s

the calculations of the Travel and subsistence should be correct
    sleep    1s
    Textfield Value Should Be    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(4) input    £ 1,000
    Textfield Value Should Be    css=#section-total-14    £ 1,000

the applicant fills a second row in the travel and subsistence section
    Click Element    jQuery=button:contains('Add another travel cost')
    Wait Until Page Contains Element    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    test
    focus    css=.app-submit-btn
    sleep    1s

the total travel and subsistence costs calculations should be correct
    focus    css=.app-submit-btn
    Textfield Value Should Be    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(4) input    £ 1,000
    Textfield Value Should Be    css=#section-total-14    £ 2,000

the applicant removes one travel and subsistence row
    click Element    css=#travel-costs-table [data-repeatable-row]:nth-child(1) button

the applicant adds one row for the other costs
    Click Element    css=[aria-controls="collapsible-7"]
    click Element    jQuery=button:contains('Add another cost')
    Wait Until Element Is Visible    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    100

the applicant adds a second row for the other costs fields
    click Element    jQuery=button:contains('Add another cost')
    Wait Until Element Is Visible    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Input Text    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    100
    Mouse Out    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    focus    css=.app-submit-btn
    sleep    1s

the other costs total should be correct
    Textfield Value Should Be    id=section-total-15    £ 200

the user reloads the page
    Reload page

the calculations of the Materials should be correct
    Textfield Value Should Be    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(4) input    £ 1,000
    Textfield Value Should Be    css=#section-total-11    £ 1,000

the total of the other funding should be correct
    Textfield Value Should Be    id=other-funding-total    £ 20,000

the applicant enters some details into the second row
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}
    Sleep    1s
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(3) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    ${OTHER_FUNDING_AMOUNT}
    Sleep    1s
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    Sleep    1s
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    focus    css=.app-submit-btn
    Sleep    1s

Applicant cannot see the 'other funding' details
    Page Should Not Contain    ${OTHER_FUNDING_SOURCE}
    Page Should Not Contain    ${OTHER_FUNDING_DATE}
    Page Should Not Contain    ${OTHER_FUNDING_AMOUNT}

Applicant can leave the 'Your finances' page but the details are still saved
    Reload Page
    #Alert Should Be Present
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input
    Textfield Should Contain    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    Textfield Should Contain    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}

Applicant enters some details into this row
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    Sleep    1s
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}
    Sleep    1s
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    ${OTHER_FUNDING_AMOUNT}
    Sleep    1s

Applicant can see a new row
    Element Should Be Visible    id=other-funding-table

Applicant selects 'No' for other funding
    Select Radio button    other_funding-otherPublicFunding-35-54    No

Applicant chooses to add yet another source of funding
    Select Radio button    other_funding-otherPublicFunding-35-54    Yes
    Click Element    jQuery=button:contains('Add another source of funding')
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Click Element    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input
    Sleep    2s

Applicant chooses to add another source of funding
    Click Element    jQuery=button:contains('Add another source of funding')
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input

Applicant selects 'Yes' for other funding
    Select Radio button    other_funding-otherPublicFunding-35-54    Yes

Applicant can see that the 'No' radio button is selected
    Radio Button Should Be Set To    other_funding-otherPublicFunding-35-54    No

Applicant clicks on "Mark all as complete"
    Focus    jQuery=.button:contains("Mark all as complete")
    Click button    jQuery=.button:contains("Mark all as complete")

the user clicks "Your finances"
    Wait Until Element Is Visible    link=Your finances
    Sleep    1s
    Focus    link=Your finances
    Click Element    link=Your finances
    Location Should Be    ${YOUR_FINANCES_URL}

the user will be in Finance section
    Page Should Not Contain Element    jQuery=.button:contains("Mark all as complete")
