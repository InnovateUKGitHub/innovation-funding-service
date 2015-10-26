*** Settings ***
Documentation     INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes.
Test Setup       Login as User    &{lead_applicant_credentials}
Test Teardown    User closes the browser
Resource          ../GLOBAL_LIBRARIES.robot
Resource          ../GLOBAL_VARIABLES.robot
Resource          ../Login_actions.robot
Resource          ../USER_CREDENTIALS.robot
Resource          ../Applicant_actions.robot

*** variables ***
${APPLICANT_USERNAME}    applicant@innovateuk.gov.uk
${APPLICANT_PSW}    test

*** Test Cases ***
Verify the Finance sections
    [Documentation]    INFUND-192
    [Tags]    Applicant
    When the applicant is in the "Your Finances" sub-section
    Then the Applicant should see all the "Your Finance" Sections

Verify the test guidance in the "Your Finance section"
    [Documentation]    INFUND-192
    [Tags]    Applicant
    When the applicant is in the "Your Finances" sub-section
    When the Applicant is in the Labour sub-section
    And the Applicant clicks the "Labour costs guidance"
    Then the guidance text should be visible

Verify the " Labour" calculations
    [Documentation]    INFUND-192
    [Tags]    Not ready
    Given the applicant is in the "Your Finances" sub-section
    When the Applicant fills the Labour costs
    Then the calculations of the labour should be correct

Verify the fields that should be read-only are not editable
    [Documentation]    INFUND-192
    [Tags]    not ready
    Given the applicant is in the "Your Finances" sub-section
    When the Applicant is in the Labour sub-section
    Then the read-only fields are not editable

Verify the "Materials" calculations
    [Documentation]    INFUND-192
    [Tags]    Not ready
    Given the applicant is in the "Your Finances" sub-section
    When the Applicant fills the Materials fields
    Then the calculations of the Materials should be correct

Verify the "Subcontracting costs" calculations
    [Documentation]    INFUND-192
    #Given the applicant is in the "Your Finances" sub-section
    #When the applicant edits the Subcontracting costs section
    #The total subcontracting costs should be 100

Verify the auto-save on the "Your Finance" section
    [Documentation]    INFUND-192
    [Tags]    Not Ready
    #Given the user is in the Labor form
    #When the Applicant edits the "The Gross Annual Salary"
    #and the Applicant refreshes the page
    #Then the new "Gross Annual" amount should be visible

*** Keywords ***
the applicant is in the "Your Finances" sub-section
    Applicant is in the 'Your Finance' sub-section

the Applicant should see all the "Your Finance" Sections
    Page Should Contain Element    css=h2:nth-child(1) button
    Page Should Contain Element    css=#question-20 > div > h2:nth-child(3) > button
    Page Should Contain Element    css=#question-20 > div > h2:nth-child(5) > button
    Page Should Contain Element    css=#collapsible-2+ h2 button
    Page Should Contain Element    css=#question-20 > div > h2:nth-child(9) > button
    Page Should Contain Element    css=#question-20 > div > h2:nth-child(11) > button
    Page Should Contain Element    css=#question-20 > div > h2:nth-child(13) > button

the Applicant is in the Labour sub-section
    Click Element    css=h2:nth-child(1) button

the Applicant clicks the "Labour costs guidance"
    Click Element    css=#collapsible-1 summary

the guidance text should be visible
    Element Should Be Visible    css=#details-content-0 p

the read-only fields are not editable
    Element Should Be Disabled    css=#labour-costs-table tbody td:nth-of-type(3) input
    Element Should Be Disabled    xpath=//*[@id="collapsible-1"]//td[contains(text(),"Total costs")]/input
    Element Should Be Disabled    xpath=//*[@id="collapsible-1"]//*[contains(text(), "Total labour costs")]//input

the Applicant fills the Labour costs
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
    Click Element    link=Add another role
    Wait Until Page Contains Element    css=#labour-costs-table tbody td:nth-of-type(2) input
    Clear Element Text    css=#cost-labour-1-workingDays
    Input Text    css=#cost-labour-1-workingDays    230
    Input Text    css=#labour-costs-table tbody td:nth-of-type(2) input    120000
    Input Text    css=#labour-costs-table tbody td:nth-of-type(4) input    100
    Sleep    1s

the calculations of the labour should be correct
    Reload Page
    Textfield Value Should Be    css=#labour-costs-table tbody td:nth-of-type(3) input    £ 522
    Textfield Value Should Be    xpath=//*[@id="collapsible-1"]//td[contains(text(),"Total costs")]/input    £ 52,174

the Applicant fills the Materials fields
    Click Element    xpath=//*[@aria-controls="collapsible-3"]
    Click Element    link=Add another materials cost
    Wait Until Page Contains Element    css=#material-costs-table tbody td:nth-of-type(2) input
    Input Text    css=#material-costs-table tbody td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody td:nth-of-type(3) input    100

the calculations of the Materials should be correct
    Reload Page
    Confirm Action
    Textfield Value Should Be    css=#material-costs-table tbody td:nth-of-type(4) input    £ 1000
    Textfield Value Should Be    css=#material-costs-total-field    £ 1,000

the user is in the Labor form
    Applicant is in the 'Your Finance' sub-section
    Click Element    css=#question-20 > div.collapsible > h2:nth-child(1) > button

the Applicant edits the "The Gross Annual Salary"
    Wait Until Page Contains Element    css=#labour-costs-table tbody td:nth-of-type(2) input
    Clear Element Text    css=#cost-labour-1-workingDays
    Input Text    css=#cost-labour-1-workingDays    256
    Input Text    css=#labour-costs-table tbody td:nth-of-type(2) input    2000
    Input Text    css=#labour-costs-table tbody td:nth-of-type(4) input    150
    Focus    css=#content > div.grid-row > div.column-two-thirds > form > div.alignright-button > a

the Applicant refreshes the page
    Sleep    2s
    Reload Page

the new "Gross Annual" amount should be visible
    Click Element    css=#question-20 > div.collapsible > h2:nth-child(1) > button
    Wait Until Element Contains    css=#cost-labour-1-workingDays    256
    #Textfield Value Should Be    css=#cost-labour-1-workingDays    256
    #Textfield Value Should Be    css=#labour-costs-table tbody td:nth-of-type(2) input    2000
    Textfield Value Should Be    css=#labour-costs-table tbody td:nth-of-type(4) input    150

the applicant edits the Subcontracting costs section
    Click Element    css=#question-20 > div.collapsible > h2:nth-child(9) > button
    Click Element    link=Add another subcontractor
    Wait Until Page Contains Element    css=[finance-subsection-table-container=32] \ > .form-row > div:nth-child(1) .form-finances-subcontracting-cost
    Input Text    css=[finance-subsection-table-container=32] \ > .form-row > div:nth-child(1) .form-finances-subcontracting-cost    100
    #Click Element    link=Add another subcontractor
    #Wait Until Page Contains Element    css=[finance-subsection-table-container=32] \ > .form-row > div:nth-child(2) .form-finances-subcontracting-cost
    #Input Text    css=[finance-subsection-table-container=32] \ > .form-row > div:nth-child(2) .form-finances-subcontracting-cost    100

The total subcontracting costs should be 100
    Textfield Value Should Be    css=#cost-subcontracting-total    £ 100

Input has value
    ${input_value} =    Get Value    ${input_selector}
    Should Be Equal As Strings    ${input_value} =    £ 522
