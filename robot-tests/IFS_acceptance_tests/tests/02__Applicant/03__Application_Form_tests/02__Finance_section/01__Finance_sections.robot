*** Settings ***
Documentation     INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes.
...
...               INFUND-1815: Small text changes to registration journey following user testing
...
...
...               INFUND-2965: Investigation into why financials return to zero when back spacing
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Test Cases ***
Finance sub-sections
    [Documentation]    INFUND-192
    [Tags]    Applicant    Finance
    When the user navigates to the page    ${YOUR_FINANCES_URL}
    Then the Applicant should see all the "Your Finance" Sections

Organisation name visible in the Finance section
    [Documentation]    INFUND-1815
    [Tags]    Applicant    Finance
    Then the user should see the text in the page    Provide the project costs for 'Empire Ltd'
    And the user should see the text in the page    'Empire Ltd' Total project costs

Guidance in the 'Your Finances' section
    [Documentation]    INFUND-192
    [Tags]    Applicant    Finance
    When the user clicks the button/link    jQuery=button:contains("Labour")
    And the user clicks the button/link    css=#collapsible-0 summary
    Then the user should see the element    css=#details-content-0 p

User presses back button should get the correct version of the page
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    When the user adds three material rows
    And the user clicks the button/link    link=Please refer to our guide to project costs for further information.
    And the user should see the text in the page    Guide on eligible project costs and completing the finance form
    And the user goes back to the previous page
    Then the user should see the element     css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    [Teardown]    and the user removes the materials rows

*** Keywords ***
the Applicant should see all the "Your Finance" Sections
    the user should see the element    css=.question section:nth-of-type(1) button
    the user should see the element    css=.question section:nth-of-type(2) button
    the user should see the element    css=.question section:nth-of-type(3) button
    the user should see the element    css=.question section:nth-of-type(4) button
    the user should see the element    css=.question section:nth-of-type(5) button
    the user should see the element    css=.question section:nth-of-type(6) button
    the user should see the element    css=.question section:nth-of-type(7) button

the user adds three material rows
    Click Element    jQuery=button:contains("Materials")
    Focus    jQuery=button:contains('Add another materials cost')
    Click Element    jQuery=button:contains('Add another materials cost')
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Focus    jQuery=button:contains('Add another materials cost')
    Click Element    jQuery=button:contains('Add another materials cost')
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Focus    jQuery=button:contains('Add another materials cost')
    Click Element    jQuery=button:contains('Add another materials cost')
    Wait Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    Focus    link=Please refer to our guide to project costs for further information.

the user removes the materials rows
    [Documentation]    INFUND-2965
    click element    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    Focus    jQuery=#material-costs-table button:contains("Remove")
    click element    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Focus    jQuery=#material-costs-table button:contains("Remove")
    click element    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Click Element    jQuery=button:contains("Materials")
