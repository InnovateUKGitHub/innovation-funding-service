*** Settings ***
Documentation     INFUND-524 As an applicant I want to see the finance summary updated and recalculated as each partner adds their finances.
Test Teardown    User closes the browser
Default Tags      Autosave    Calculations    Finance    Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***

The lead applicant can add finances
    [Documentation]     INFUND-524
    [Tags]  Finance    Finance Section   Collaboration  Failing
    Given the user logs in as lead applicant
    When the applicant goes to the Finance section
    Then the applicant enters a valid set of financial data
    And the applicant sees that this financial data has been entered
    And the applicant's data shows on the Finances overview page
    And the applicant logs out

The first collaborator can add finances
    [Documentation]     INFUND-524
    [Tags]  Finance    Finance Section   Collaboration  Failing
    Given the user logs in as first collaborator
    And the collaborator goes to the Finance section
    When the collaborator enters a valid set of financial data
    Then the collaborator sees that this financial data has been entered
    And the financial data has been updated on the Finances overview page
    And the collaborator logs out

The second collaborator can see the finances, but leaves the fields empty
    [Documentation]     INFUND-524
    [Tags]  Finance    Finance Section   Collaboration  Failing
    Given the user logs in as second collaborator
    When The collaborator goes to the Finance section
    Then the collaborator sees the option to enter financial data but there is no data entered
    And the collaborator logs out

The lead applicant can see all of the finances of the collaborators
    [Documentation]     INFUND-524
    [Tags]  Finance    Finance Section   Collaboration  Failing
    Given the user logs in as lead applicant
    Then the financial data has been updated on the Finances overview page




*** Keywords ***

The applicant goes to the Finance section
    go to   ${your_finances_url}

The applicant enters a valid set of financial data
    the applicant enters some valid labour data
    the applicant enters some valid materials data



the applicant enters some valid labour data

        Click Element    xpath=//*[@aria-controls="collapsible-1"]
        Click Element    link=Add another role
        Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
        Clear Element Text    css=#cost-labour-1-workingDays
        Input Text    css=#cost-labour-1-workingDays    230
        Sleep   1s
        Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    Project Manager
        Sleep   1s
        Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
        Sleep   1s
        Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
        Sleep   1s
        Click Element   link=Add another role
        Sleep   1s
        Submit Form



the applicant enters some valid materials data
        go to       ${your_finances_url}
        Click Element    xpath=//*[@aria-controls="collapsible-3"]
        Click Element    link=Add another materials cost
        Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
        Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    Laptops
        Sleep   1s
        Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
        Sleep   1s
        Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    1,000
        Sleep   1s
        Click Element   link=Add another materials cost
        Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
        Submit Form



The applicant sees that this financial data has been entered
        go to       ${your_finances_url}
        Click Element    xpath=//*[@aria-controls="collapsible-1"]
        Wait Until Page Contains Element    link=Add another role
        Textfield Value Should Be     css=.labour-costs-table tr:nth-of-type(2) td:nth-of-type(1) input       £ 52,174


the applicant's data shows on the Finances overview page
    go to       ${finances_overview_url}
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[1]/td[2]   £52,174

The applicant logs out
    Logout as user

The user logs in as first collaborator
    Login as user   &{collaborator1_credentials}

The collaborator goes to the Finance section
    go to       ${your_finances_url}


The collaborator enters a valid set of financial data
    the collaborator enters some valid labour data
    the collaborator enters some valid travel cost data



the collaborator enters some valid labour data
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
    Click Element    link=Add another role
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    Clear Element Text    css=#cost-labour-16-workingDays
    Input Text    css=#cost-labour-16-workingDays    230
    Sleep   1s
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    Developer
    Sleep   1s
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    20000
    Sleep   1s
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    12
    Sleep   1s
    Click Element   link=Add another role
    Sleep   1s
    Submit Form



the collaborator enters some valid travel cost data
    go to   ${your_finances_url}
    Click Element    xpath=//*[@aria-controls="collapsible-6"]
    Click Element    link=Add another travel cost
    Wait Until Page Contains Element    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1)
    Sleep   1s
    Input Text    css=#travel-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    Going down to Swindon
    Sleep   1s
    Input Text    css=#travel-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    2
    Sleep   1s
    Input Text    css=#travel-costs-table tr:nth-of-type(1) td:nth-of-type(3) input    1,000
    Sleep   1s
    Click Element   link=Add another travel cost
    Sleep   1s
    Submit Form




The collaborator sees that this financial data has been entered
    go to       ${your_finances_url}
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
    Wait Until Page Contains Element    link=Add another role
    Textfield Value Should Be     css=.labour-costs-table tr:nth-of-type(2) td:nth-of-type(1) input       £ 1,043


the financial data has been updated on the Finances overview page
    go to   ${finances_overview_url}
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[1]/td[1]      £53,217
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[1]/td[2]      £52,174
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[1]/td[3]      £1,043
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[1]/td[4]      £0
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[3]/td[1]      £10,000
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[3]/td[2]      £10,000
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[3]/td[3]      £0
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[3]/td[4]      £0
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[6]/td[1]      £2,000
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[6]/td[2]      £0
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[6]/td[3]      £2,000
    Element Should Contain        xpath=/html/body/main/form/div/div/div[3]/div/table/tbody/tr[6]/td[4]      £0

The collaborator logs out
    Logout as user

The user logs in as second collaborator
    Login as user   &{collaborator2_credentials}

The collaborator sees the option to enter financial data but there is no data entered
     Click Element    xpath=//*[@aria-controls="collapsible-1"]
     Click Element    link=Add another role
     Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input
     Textfield Value Should Be      css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input   ${EMPTY}
     Element Should Contain      css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input      ${EMPTY}
     Element Should Contain      css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input      ${EMPTY}


The user logs in as lead applicant
    Login as user   &{lead_applicant_credentials}


