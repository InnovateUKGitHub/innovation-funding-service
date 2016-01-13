*** Settings ***
Documentation     INFUND-736: As an applicant I want to be able to add all the finance details for all the sections so I can sent in all the info necessary to apply
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Failing
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Labour
    [Documentation]    INFUND-192
    ...    Acceptance tests for the Labour section calculations
    ...    INFUND-736
    [Tags]    Labour    Autosave    Calculations    Finance
    Given Applicant goes to the Your finances section
    When the Applicant fills the Labour costs
    Then the calculations of the labour should be correct
    and when the applicant fills a second row in the labour costs
    Then the total labour cost calculation should be correct
    and when the applicant removes one labour row
    Then the labour total should be correct again
    and when the user reloads the page
    Then the labour total should be correct again

Materials
    [Documentation]    INFUND-192
    ...    INFUND-736
    [Tags]    Materials    Autosave    Calculations    Finance
    Given Applicant goes to the Your finances section
    When the Applicant fills the Materials fields
    Then the calculations of the Materials should be correct again
    and when the applicant fills a second row in the materials section
    Then the total materials costs calculations should be correct
    and when the applicant removes one material row
    Then the calculations of the Materials should be correct again
    and when the user reloads the page
    Then the calculations of the Materials should be correct again

Capital usage
    [Documentation]    INFUND-736
    [Tags]    Capital Usage    Autosave    Calculations    Finance
    Given Applicant goes to the Your finances section
    When the applicant fills the 'capital usage' field
    Then the calculations of the 'capital usage' should be correct
    and when the applicant fills a new subcontractor
    Then the total calculation of the capital usage should be correct
    and when the the applicant removes one subcontractor row
    then the total of the capital usage should be correct again
    and when the user reloads the page
    then the total of the capital usage should be correct again

Subcontracting costs
    [Documentation]    INFUND-192
    ...    INFUND-736
    [Tags]    Subcontracting Costs    Autosave    Calculations    Finance
    Given Applicant goes to the Your finances section
    When the applicant edits the Subcontracting costs section
    And the applicant adds a new row in the subcontracting costs
    The total subcontracting costs should correct
    and when the applicant removes one subcontracting row
    then the total subcontracting total should be correct again
    #and when the user reloads the page
    #then the total subcontracting total should be correct again

Travel and subsistence
    [Documentation]    INFUND-736
    [Tags]    Travel and subsistence    Autosave    Calculations    Finance
    Given Applicant goes to the Your finances section
    When the Applicant fills the Travel fields
    Then the calculations of the Travel and subsistence should be correct
    and when the applicant fills a second row in the travel and subsistence section
    Then the total travel and subsistence costs calculations should be correct
    and when the applicant removes one travel and subsistence row
    Then the calculations of the travel and subsistence should be correct
    #and when the user reloads the page
    #Then the calculations of the travel and subsistence should be correct

Other costs
    [Documentation]    INFUND-736
    [Tags]    Other costs    Autosave    Calculations    Finance
    Given Applicant goes to the Your finances section
    When the applicant adds one row for the other costs
    and the applicant adds a second row for the other costs fields
    then the other costs total should be correct
    #and when the user reloads the page
    #then the other costs total should be correct

*** Keywords ***
the Applicant fills the Labour costs
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
    Click Element    link=Add another role
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    Clear Element Text    css=#cost-labour-1-workingDays
    Input Text    css=#cost-labour-1-workingDays    230
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    Mouse Out    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input
    focus    css=.app-submit-btn
    Sleep    1s

the calculations of the labour should be correct
    #Reload Page
    Sleep    2s
    #Alert Should Be Present
    Textfield Value Should Be    css=.labour-costs-table tbody td:nth-of-type(3) input    £ 522
    Textfield Value Should Be    xpath=//*[@id="collapsible-1"]//td[contains(text(),"Total costs")]/input    £ 52,174

when the applicant removes one labour row
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
    click element    Link=Remove

the labour total should be correct again
    Textfield Value Should Be    css=#cost-labour-total-field    £ 52,174

the total labour cost calculation should be correct
    #Reload Page
    Sleep    2s
    #Alert Should Be Present
    Textfield Value Should Be    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(3) input    £ 522
    Textfield Value Should Be    css=.labour-costs-table tr:nth-of-type(2) input    £ 52,174
    Textfield Value Should Be    css=#cost-labour-total-field    £ 104,348
    Element Should Contain    css=#form-input-20 > div.collapsible > h2:nth-child(1) > span > span    £ 104,348

when the applicant fills a second row in the labour costs
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
    Click Element    link=Add another role
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    Sleep    1s

the Applicant fills the Materials fields
    Click Element    xpath=//*[@aria-controls="collapsible-3"]
    Click link    Add another materials cost
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    mouse out    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input
    focus    css=.app-submit-btn
    sleep    1s

when the applicant removes one material row
    Click Element    xpath=//*[@aria-controls="collapsible-3"]
    click element    link=Remove

when the applicant fills a second row in the materials section
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(5) > button
    Click link    link=Add another materials cost
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    mouse out    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input
    focus    css=.app-submit-btn
    sleep    1s

the calculations of the Materials should be correct again
    #focus    css=.add-another-row
    #Reload Page
    #Click Element    xpath=//*[@aria-controls="collapsible-3"]
    Textfield Value Should Be    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(4) input    £ 1000
    Textfield Value Should Be    css=#material-costs-total-field    £ 1,000

the total materials costs calculations should be correct
    focus    css=.app-submit-btn
    #Reload Page
    Sleep    1s
    #Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(5) > button
    Textfield Value Should Be    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(4) input    £ 1000
    Textfield Value Should Be    css=#material-costs-total-field    £ 2,000

the applicant edits the Subcontracting costs section
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(9) > button
    Click Link    link=Add another subcontractor
    Wait Until Page Contains Element    css=.form-row:nth-child(1) .form-finances-subcontracting-cost
    Input Text    css=.form-row:nth-child(1) .form-finances-subcontracting-cost    100
    mouse out    css=.form-row:nth-child(1) .form-finances-subcontracting-cost
    focus    css=.app-submit-btn
    sleep    3s

The total subcontracting costs should correct
    #Reload Page
    Sleep    1s
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(9) > button
    Textfield Value Should Be    css=#cost-subcontracting-total    £ 200
    Element Should Contain    css=#form-input-20 > div.collapsible > h2:nth-child(9) > span > span    £ 200

the applicant adds a new row in the subcontracting costs
    #Reload Page
    #Alert Should Be Present
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(9) > button
    Click link    Link=Add another subcontractor
    Wait Until Page Contains Element    css=.form-row:nth-child(2) .form-finances-subcontracting-cost
    Input Text    css=.form-row:nth-child(2) .form-finances-subcontracting-cost    100
    mouse out    css=.form-row:nth-child(2) .form-finances-subcontracting-cost
    sleep    1s
    focus    css=.app-submit-btn

the applicant fills the 'capital usage' field
    Click Element    //*[@id="form-input-20"]/div[1]/h2[4]/button
    Click Element    link=Add another asset
    Wait Until Element Is Visible    link=Remove
    Input Text    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    1000
    input text    css=.form-row:nth-child(1) .form-finances-capital-usage-residual-value    900
    input text    css=.form-finances-capital-usage-utilisation    100
    Mouse Out    css=.form-finances-capital-usage-utilisation
    focus    css=.app-submit-btn

the calculations of the 'capital usage' should be correct
    focus    css=.app-submit-btn
    #Reload Page
    #Alert Should Be Present
    #Textfield Value Should Be
    Textfield Value Should Be    css=#cost-capital-usage-costs-total-field    £ 100

when the applicant fills a new subcontractor
    Click Element    //*[@id="form-input-20"]/div[1]/h2[4]/button
    Click Element    link=Add another asset
    Wait Until Element Is Visible    css=.form-row:nth-child(2) .form-finances-capital-usage-npv
    Input Text    css=.form-row:nth-child(2) .form-finances-capital-usage-npv    1000
    input text    css=.form-row:nth-child(2) .form-finances-capital-usage-residual-value    900
    input text    css=.form-row:nth-child(2) .form-finances-capital-usage-utilisation    100
    Mouse Out    css=.form-row:nth-child(2) .form-finances-capital-usage-utilisation

the total calculation of the capital usage should be correct
    focus    css=.app-submit-btn
    Reload Page
    Alert Should Be Present
    #Textfield Value Should Be
    Textfield Value Should Be    css=#cost-capital-usage-costs-total-field    £ 200

when the the applicant removes one subcontractor row
    Click Element    //*[@id="form-input-20"]/div[1]/h2[4]/button
    click element    link=Remove

the total of the capital usage should be correct again
    Textfield Value Should Be    css=#cost-capital-usage-costs-total-field    £ 100

when the applicant removes one subcontracting row
    #Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(9) > button
    click element    link=Remove

the total subcontracting total should be correct again
    #Reload Page
    Sleep    1s
    #Alert Should Be Present
    Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(9) > button
    Textfield Value Should Be    css=#cost-subcontracting-total    £ 100
    Element Should Contain    css=#form-input-20 > div.collapsible > h2:nth-child(9) > span > span    £ 100

the Applicant fills the Travel fields
    Click Element    xpath=//*[@id="form-input-20"]/div[1]/h2[6]/button
    Click link    Add another travel cost
    Wait Until Page Contains Element    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    mouse out    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input
    focus    css=.app-submit-btn
    sleep    1s

the calculations of the Travel and subsistence should be correct
    #focus    css=.add-another-row
    #Reload Page
    #Alert Should Be Present
    #Click Element    xpath=//*[@aria-controls="collapsible-3"]
    Textfield Value Should Be    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(4) input    £ 1,000
    Textfield Value Should Be    css=#travel-costs-total-field    £ 1,000

when the applicant fills a second row in the travel and subsistence section
    Click Element    xpath=//*[@id="form-input-20"]/div[1]/h2[6]/button
    Click link    Add another travel cost
    Wait Until Page Contains Element    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    mouse out    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input
    focus    css=.app-submit-btn
    sleep    1s

the total travel and subsistence costs calculations should be correct
    focus    css=.app-submit-btn
    #Reload Page
    #Sleep    1s
    #Alert Should Be Present
    #Confirm Action
    #Click Element    css=#form-input-20 > div.collapsible > h2:nth-child(5) > button
    Textfield Value Should Be    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(4) input    £ 1,000
    Textfield Value Should Be    css=#travel-costs-total-field    £ 2,000

when the applicant removes one travel and subsistence row
    Click Element    xpath=//*[@id="form-input-20"]/div[1]/h2[6]/button
    click element    link=Remove

the applicant adds one row for the other costs
    Click Element    xpath=//*[@id="form-input-20"]/div[1]/h2[7]/button
    click element    link=Add another cost
    Wait Until Element Is Visible    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    100

the applicant adds a second row for the other costs fields
    click element    link=Add another cost
    Wait Until Element Is Visible    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Input Text    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    100
    Mouse Out    css=#other-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    focus    css=.app-submit-btn

the other costs total should be correct
    Textfield Value Should Be    id=other-costs-total-field    £ 200

when the user reloads the page
    Reload page
