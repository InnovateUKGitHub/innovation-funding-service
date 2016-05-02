*** Settings ***
Documentation     INFUND-2214: As an applicant I want to be prevented from marking my finances as complete if I have not fully completed the Other funding section so that I can be sure I am providing all the required information
Suite Setup       Run keywords    Guest user log-in    &{lead_applicant_credentials}
...               AND    the user navigates to the page    ${YOUR_FINANCES_URL}
...               AND    Focus    jQuery=button:contains('Add another source of funding')
...               AND    the user clicks the button/link    jQuery=button:contains('Add another source of funding')
Suite Teardown    TestTeardown User closes the browser
Force Tags        Finances
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Test Cases ***
When the other funding row is empty mark as complete is impossible
    [Documentation]    INFUND-2214
    [Tags]    Pending
    [Setup]    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    #pending 2690
    When the user clicks marks as complete the finance section
    Then the user should see the element    css=.error-summary-list

Other funding validations
    [Documentation]    INFUND-2214
    [Tags]    Pending
    [Setup]    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    # Pending due to INFUND-2688
    When the user enters invalid inputs in the other funding fields
    And the user clicks marks as complete the finance section
    #Then the user should see an error    This field cannot be left blank
    And the user should see an error    This field should be 0 or higher
    And the user should see the element    css=.error-summary-list
    #add an extra validation for the date field

When the selection is NO the user should be able to mark as complete
    [Documentation]    INFUND-2214
    [Tags]    Pending
    # Pending INFUND-2690
    Given the users selects no in the other fundings section
    When the user clicks marks as complete the finance section
    Then the user should be redirected to the correct page    ${APPLICATION_OVERVIEW_URL}
    [Teardown]    Run keywords    When the user navigates to the page    ${YOUR_FINANCES_URL}
    ...    AND    the user clicks the button/link    jQuery=button:contains("Edit")

*** Keywords ***
the users selects no in the other fundings section
    Select Radio button    other_funding-otherPublicFunding-35-54    No

the user clicks marks as complete the finance section
    Focus    jQuery=button:contains("Mark all as complete")
    the user clicks the button/link    jQuery=button:contains("Mark all as complete")

the user enters invalid inputs in the other funding fields
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${EMPTY}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -6565
