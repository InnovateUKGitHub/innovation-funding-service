*** Settings ***
Documentation     INFUND-2214: As an applicant I want to be prevented from marking my finances as complete if I have not fully completed the Other funding section so that I can be sure I am providing all the required information
Suite Setup       Run keywords    Guest user log-in    &{lead_applicant_credentials}
...               AND    the user navigates to the page    ${YOUR_FINANCES_URL}
...               AND    Focus    jQuery=button:contains('Add another source of funding')
...               AND    the user clicks the button/link    jQuery=button:contains('Add another source of funding')
Suite Teardown    TestTeardown User closes the browser
Force Tags        Finances    Pending
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
    [Tags]
    [Setup]    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    When the user enters invalid inputs in the other funding fields    ${EMPTY}    13-2020    -6565
    And the user clicks marks as complete the finance section
    Then the user should see an error    Funding source cannot be blank
    And the user should see an error    Please use MM-YYYY format
    And the user should see an error    This field should be 0 or higher
    And the user should see the element    css=.error-summary-list
    When the user enters invalid inputs in the other funding fields    ${EMPTY}    ${EMPTY}    ${EMPTY}
    Then the user should see an error    Funding source cannot be blank
    And the user should see an error    This field cannot be left blank
    And the user should see an error    This field should be a number
    When the user enters invalid inputs in the other funding fields    ${EMPTY}    12-2017    012345678910111213141516171819202122
    Then the user should see an error    You must enter a value less than 20 digits

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
    [Arguments]    ${SOURCE}    ${DATE}    ${FUNDING}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${SOURCE}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${DATE}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    ${FUNDING}
    Mouse out    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input
    Focus    jQuery=button:contains("Mark all as complete")
