*** Settings ***
Documentation     INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes.
...
...               INFUND-1815: Small text changes to registration journey following user testing
...
...
...               INFUND-2965: Investigation into why financials return to zero when back spacing
...
...               INFUND-2051: Remove the '0' in finance fields
...
...               INFUND-2961: ‘Working Days Per Year’ in Labour Costs do not default to 232.
Suite Setup       Run keywords    log in and create new application if there is not one already
...               AND    Applicant navigates to the finances of the Robot application
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          FinanceSection_Commons.robot

*** Test Cases ***
Finance sub-sections
    [Documentation]    INFUND-192
    [Tags]    HappyPath
    Then the user should see all the Your-Finances Sections

Organisation name visible in the Finance section
    [Documentation]    INFUND-1815
    [Tags]
    When the user clicks the button/link             link=Your project costs
    Then the user should see the text in the page    Provide the project costs for 'Empire Ltd'
    And the user should see the text in the page    'Empire Ltd' Total project costs

Guidance in the Your Finances section
    [Documentation]    INFUND-192
    [Tags]
    [Setup]  Applicant navigates to the finances of the Robot application
    Given the user clicks the button/link   link=Your project costs
    When the user clicks the button/link    jQuery=button:contains("Labour")
    And the user clicks the button/link    css=#collapsible-0 summary
    Then the user should see the element    css=#details-content-0 p

Working days per year should be 232
    [Documentation]    INFUND-2961
    Then the working days per year should be 232 by default

Finance fields are empty
    [Documentation]    INFUND-2051: Remove the '0' in finance fields
    [Tags]    HappyPath
    Then the user should see the element  jQuery=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input[value=""]

User pressing back button should get the correct version of the page
    [Documentation]    INFUND-2695
    [Tags]
    [Setup]  Applicant navigates to the finances of the Robot application
    And the user clicks the button/link  link=Your project costs
    Given The user adds three material rows
    When the user navigates to another page
    And the user should see the text in the page    Guide on eligible project costs and completing the finance form
    And the user goes back to the previous page
    Then the user should see the element    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    [Teardown]    the user removes the materials rows

*** Keywords ***
the user adds three material rows
    the user clicks the button/link    jQuery=button:contains("Materials")
    the user should see the element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    01
    the user moves focus to the element    jQuery=button:contains(Add another materials cost)
    the user clicks the button/link    jQuery=button:contains(Add another materials cost)
    the user should see the element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    01
    the user moves focus to the element    jQuery=button:contains(Add another materials cost)
    the user clicks the button/link    jQuery=button:contains(Add another materials cost)
    the user should see the element    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input    01
    the user moves the mouse away from the element    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    the user moves focus to the element    link=Please refer to our guide to project costs for further information.

the user removes the materials rows
    [Documentation]    INFUND-2965
    the user clicks the button/link    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible    css=#material-costs-table tbody tr:nth-of-type(4) td:nth-of-type(2) input    10s
    the user moves focus to the element    jQuery=#material-costs-table button:contains("Remove")
    the user clicks the button/link    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input    10s
    the user clicks the button/link    jQuery=#material-costs-table button:contains("Remove")
    Run Keyword And Ignore Error    the user clicks the button/link    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10s
    the user clicks the button/link    jQuery=button:contains("Materials")

the working days per year should be 232 by default
    the user should see the element    css=[name^="labour-labourDaysYearly"]
    ${Days_value} =    Get Value    css=[name^="labour-labourDaysYearly"]
    Should Be Equal As Strings    ${Days_value}    232

the user navigates to another page
    the user clicks the button/link    link=Please refer to our guide to project costs for further information.
    Run Keyword And Ignore Error    Confirm Action
