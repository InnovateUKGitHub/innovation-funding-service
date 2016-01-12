*** Settings ***
Documentation     More test cases should follow when all the sections are ready
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Default Tags      Autosave    Calculations    Finance    Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
'Labour' Calculations/Autosave
    [Documentation]    INFUND-192
    ...    Acceptance tests for the Labour section calculations
    [Tags]    Labour
    Given Applicant goes to the Your finances section
    When the Applicant fills the Labour costs
    Then the calculations of the labour should be correct
    and when the applicant fills a second row in the labour costs
    Then the total labour cost calculation should be correct

'Materials' Calculations/Autosave
    [Documentation]    INFUND-192
    [Tags]    Materials
    Given Applicant goes to the Your finances section
    When the Applicant fills the Materials fields
    Then the calculations of the Materials should be correct
    and when the applicant fills a second row in the materials section
    Then the total materials costs calculations should be correct

'Subcontracting costs' Calculations/Autosave
    [Documentation]    INFUND-192
    [Tags]    Subcontracting Costs
    Given Applicant goes to the Your finances section
    When the applicant edits the Subcontracting costs section
    And the applicant adds a new row in the subcontracting costs
    The total subcontracting costs should correct

'Capital usage' Calculations/Autosave
    Given Applicant goes to the Your finances section
    When the applicant fills the 'capital usage' field
    #Then the calculations of the 'capital usage' should be correct

*** Keywords ***
the Applicant fills the Labour costs
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
    Click Element    link=Add another role
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    Clear Element Text    css=#cost-labour-1-workingDays
    Input Text    css=#cost-labour-1-workingDays    230
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    focus    css=.add-another-row
    Sleep    1s

the calculations of the labour should be correct
    Reload Page
    Sleep    2s
    #Alert Should Be Present
    Textfield Value Should Be    css=.labour-costs-table tbody td:nth-of-type(3) input    £ 522
    Textfield Value Should Be    xpath=//*[@id="collapsible-1"]//td[contains(text(),"Total costs")]/input    £ 52,174

the Applicant fills the Materials fields
    Click Element    xpath=//*[@aria-controls="collapsible-3"]
    Click link    Add another materials cost
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    focus    css=.add-another-row
    sleep    1s

the calculations of the Materials should be correct
    focus    css=.add-another-row
    Reload Page
    #Sleep    2s
    #Click Element    xpath=//*[@aria-controls="collapsible-3"]
    Textfield Value Should Be    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(4) input    £ 1000
    Textfield Value Should Be    css=#material-costs-total-field    £ 1,000

the applicant edits the Subcontracting costs section
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(9) > button
    Click Link    link=Add another subcontractor
    Wait Until Page Contains Element    css=.form-row:nth-child(1) .form-finances-subcontracting-cost
    Input Text    css=.form-row:nth-child(1) .form-finances-subcontracting-cost    100
    focus    css=.add-another-row
    sleep    3s

The total subcontracting costs should correct
    Reload Page
    Sleep    1s
    #    Alert Should Be Present
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(9) > button
    Textfield Value Should Be    css=#cost-subcontracting-total    £ 200
    Element Should Contain    css=#form-input-20 > div.collapsible > h2:nth-child(9) > span > span    £ 200

when the applicant fills a second row in the labour costs
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
    Click Element    link=Add another role
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    Sleep    1s

the total labour cost calculation should be correct
    Reload Page
    Sleep    2s
    #Alert Should Be Present
    Textfield Value Should Be    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(3) input    £ 522
    Textfield Value Should Be    css=.labour-costs-table tr:nth-of-type(2) input    £ 52,174
    Textfield Value Should Be    css=#cost-labour-total-field    £ 104,348
    Element Should Contain    css=#form-input-20 > div.collapsible > h2:nth-child(1) > span > span    £ 104,348

when the applicant fills a second row in the materials section
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(5) > button
    Click link    link=Add another materials cost
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    focus    css=.add-another-row
    sleep    1s

the total materials costs calculations should be correct
    focus    css=.add-another-row
    Reload Page
    #Sleep    1s
    #Alert Should Be Present
    #Confirm Action
    #Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(5) > button
    Textfield Value Should Be    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(4) input    £ 1000
    Textfield Value Should Be    css=#material-costs-total-field    £ 2,000

the applicant adds a new row in the subcontracting costs
    Reload Page
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(9) > button
    Click link    Link=Add another subcontractor
    Wait Until Page Contains Element    css=.form-row:nth-child(2) .form-finances-subcontracting-cost
    Input Text    css=.form-row:nth-child(2) .form-finances-subcontracting-cost    100
    sleep    1s
    focus    css=.add-another-row

the applicant fiills the Overhead fields
    click element    xpath=//*[@id="form-input-20"]/div[1]/h2[2]/button

the applicant fills the 'capital usage' field
    Click Element    //*[@id="form-input-20"]/div[1]/h2[4]/button
    Click Element    link=Add another asset
    Wait Until Element Is Visible    link=Remove
    Input Text    css=.form-row:nth-child(1) .form-finances-subcontracting-cost    100
