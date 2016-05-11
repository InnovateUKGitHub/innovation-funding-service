*** Settings ***
Documentation     INFUND-844: As an applicant I want to receive a validation error in the finance sections if I my input is invalid in a particular field so that I am informed how to correctly submit the information
...
...               INFUND-2214: As an applicant I want to be prevented from marking my finances as complete if I have not fully completed the Other funding section so that I can be sure I am providing all the required information
Suite Setup       Run keywords    Guest user log-in    &{lead_applicant_credentials}
...               AND    Given the user navigates to the page    ${YOUR_FINANCES_URL}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Finances
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Test Cases ***
Labour server side validations
    [Documentation]    INFUND-844
    [Tags]    Pending
    #Pending due to infund 2687
    Given the user clicks the button/link    jQuery=button:contains("Labour")
    When the user clicks the button/link    jQuery=button:contains('Add another role')
    And the user enters text to a text field    css=#cost-labour-1-labourDaysYearly    366
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    123456789101112131415161718192021
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    -1
    And the user marks the finances as complete
    Then the user should see an error    This field should be 365 or lower
    And the user should see an error    This field cannot be left blank
    And the user should see an error    This field should be 1 or higher
    And the user should see an error    You must enter a value less than 20 digits
    And the user should see the element    css=.error-summary-list
    [Teardown]

Labour client side validations
    [Documentation]    INFUND-844
    [Tags]    Pending
    When the user enters text to a text field    css=#cost-labour-1-labourDaysYearly    -1
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    123456789101112
    Then the user gets the expected validation errors    You must enter a value less than 10 digits    This field should be 1 or higher
    #And the user gets the expected validation errors    This field cannot be left blank    #we
    [Teardown]    Remove row    jQuery=button:contains("Labour")

Administration costs validations (custom cost)
    [Documentation]    INFUND-844
    [Tags]    Pending
    #Pending due to infund-2693
    When the user clicks the button/link    jQuery=button:contains("Administration support costs")
    And user selects the admin costs    overheads-rateType-29-51    CUSTOM_RATE
    And the user enters text to a text field    id=cost-overheads-51-customRate    ${EMPTY}
    And the user marks the finances as complete
    Then the user should see an error    This field should be 1 or higher
    And the user enters text to a text field    id=cost-overheads-51-customRate    101
    And the user marks the finances as complete
    Then the user should see an error    This field should be 100 or lower
    And the user should see the element    css=.error-summary-list
    And the user enters text to a text field    id=cost-overheads-51-customRate    -1
    And the user marks the finances as complete
    Then the user should see an error    This field should be 1 or higher
    And the user should see the element    css=.error-summary-list
    [Teardown]    When the user clicks the button/link    jQuery=button:contains("Administration support costs")

Administration costs validations (special rate)
    [Documentation]    INFUND-844
    [Tags]    Pending
    #Pending due to infund-2693
    When the user clicks the button/link    jQuery=button:contains("Administration support costs")
    And user selects the admin costs    overheads-rateType-29-51    SPECIAL_AGREED_RATE
    And the user enters text to a text field    id=cost-overheads-51-agreedRate    ${EMPTY}
    And the user marks the finances as complete
    Then the user should see an error    This field should be 1 or higher
    And the user enters text to a text field    id=cost-overheads-51-agreedRate    101
    And the user marks the finances as complete
    Then the user should see an error    This field should be 100 or lower
    And the user should see the element    css=.error-summary-list
    And the user enters text to a text field    id=cost-overheads-51-agreedRate    -1
    And the user marks the finances as complete
    Then the user should see an error    This field should be 1 or higher
    And the user should see the element    css=.error-summary-list
    [Teardown]    When the user clicks the button/link    jQuery=button:contains("Administration support costs")

Materials calculations
    [Documentation]    INFUND-844
    [Tags]    Pending
    #Pending due to infund 2687
    Given the user clicks the button/link    jQuery=button:contains("Materials")
    And the user clicks the button/link    jQuery=button:contains('Add another materials cost')
    When the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    #And the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    13245678910111213141516171819202122
    And the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -1
    And the user marks the finances as complete
    Then the user should see an error    This field cannot be left blank
    #And the user should see an error    You must enter a value less than 20 digits
    And the user should see an error    This field should be 1 or higher
    And the user should see the element    css=.error-summary-list
    [Teardown]    Remove row    jQuery=button:contains("Materials")

Capital usage validations
    [Documentation]    INFUND-844
    [Tags]    Pending
    # Pending INFUND-2693
    Given the user clicks the button/link    jQuery=button:contains("Capital usage")
    And the user clicks the button/link    jQuery=button:contains('Add another asset')
    And the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    -1
    And the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-residual-value    -2
    And the user enters text to a text field    css=.form-finances-capital-usage-utilisation    101
    And the user enters text to a text field    css=.form-finances-capital-usage-depreciation    ${EMPTY}
    And the user marks the finances as complete
    Then the user should see an error    This field should be 0 or higher
    And the user should see an error    Please enter a valid value.
    And the user should see an error    This field should be 0 or higher
    And the user should see an error    This field should be 100 or lower
    And the user should see the element    css=.error-summary-list
    [Teardown]    Remove row    jQuery=button:contains("Capital usage")

Subcontracting costs validations
    [Documentation]    INFUND-844
    [Tags]    Pending
    # Pending INFUND-2706
    Given the user clicks the button/link    jQuery=button:contains("Subcontracting costs")
    And the user clicks the button/link    jQuery=button:contains('Add another subcontractor')
    When the user enters text to a text field    css=#collapsible-4 .form-row:nth-child(1) input[id$=subcontractingCost]    -100
    And the user marks the finances as complete
    Then the user should see an error    This field should be 0 or higher
    When the user enters text to a text field    css=#collapsible-4 .form-row:nth-child(1) input[id$=subcontractingCost]    4565464563463456345645634563463456345
    And the user marks the finances as complete
    Then the user should see an error    You must enter a value less than 20 digits
    And the user should see the element    css=.error-summary-list
    [Teardown]    Remove row    jQuery=button:contains("Subcontracting costs")

Travel and subsistence validations
    [Documentation]    INFUND-844
    [Tags]    Pending
    #Pending due to infund 2687
    Given the user clicks the button/link    jQuery=button:contains("Travel and subsistence")
    And the user clicks the button/link    jQuery=button:contains('Add another travel cost')
    #When the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    0123456789101112131415161718192021
    And the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -1
    And the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    Test
    And the user marks the finances as complete
    Then the user should see an error    This field should be 0 or higher
    #And the user should see an error    You must enter a value less than 20 digits
    And the user should see the element    css=.error-summary-list
    [Teardown]    Run keywords    Focus    jQuery=button:contains("Remove")
    ...    AND    the user clicks the button/link    jQuery=button:contains("Remove")
    ...    AND    the user clicks the button/link    jQuery=button:contains("Travel and subsistence")

Other costs validations
    [Documentation]    INFUND-844
    [Tags]    Pending
    #Pending due to infund 2687
    Given the user clicks the button/link    jQuery=button:contains("Other Costs")
    And the user clicks the button/link    jQuery=button:contains('Add another cost')
    When the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user marks the finances as complete
    Then the user should see an error    This field should be 0 or higher
    And the user should see an error    This field cannot be left blank
    And the user should see the element    css=.error-summary-list
    #When the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    435454454454544544545454545445
    #And the user marks the finances as complete
    #Then the user should see an error    You must enter a value less than 20 digits
    #And the user should see an error    This field cannot be left blank
    #And the user should see the element    css=.error-summary-list

Save other costs when there are validation errors
    [Tags]    Pending
    When the user reloads the page with validation errors
    Then the field with the wrong input should be saved
    [Teardown]    Run keywords    the user clicks the button/link    jQuery=button:contains("Remove")
    ...    AND    the user clicks the button/link    jQuery=button:contains("Other costs")

Grand field validations
    [Tags]    Pending
    #pending 1417
    When the user enters text to a text field    id=cost-financegrantclaim    -1
    And the user marks the finances as complete
    Then the user should see an error    This field should be 0 or higher
    #When the user enters text to a text field    id=cost-financegrantclaim    ${EMPTY}
    #And the user marks the finances as complete
    #Then the user should see an error    This field should be 0 or higher
    #And the user should see an error    This field should be a number

When the other funding row is empty mark as complete is impossible
    [Documentation]    INFUND-2214
    [Tags]    Pending
    #pending 2690
    Given the user clicks the button/link    jQuery=button:contains('Add another source of funding')
    And the user should see the element    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    And the user marks the finances as complete
    Then the user should see the element    css=.error-summary-list

Other funding validations
    [Documentation]    INFUND-2214
    [Tags]    Pending
    [Setup]    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    When the user enters invalid inputs in the other funding fields    ${EMPTY}    13-2020    -6565
    And the user marks the finances as complete
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
    And the user marks the finances as complete
    Then the user should be redirected to the correct page    ${APPLICATION_OVERVIEW_URL}
    [Teardown]    Run keywords    When the user navigates to the page    ${YOUR_FINANCES_URL}
    ...    AND    the user clicks the button/link    jQuery=button:contains("Edit")

*** Keywords ***
the user marks the finances as complete
    #Sleep    300ms
    Focus    jQuery=button:contains("Mark all as complete")
    click element    jQuery=button:contains("Mark all as complete")
    #Sleep    300ms

user selects the admin costs
    [Arguments]    ${RADIO_BUTTON}    ${SELECTION}
    Select Radio Button    ${RADIO_BUTTON}    ${SELECTION}
    focus    css=.app-submit-btn

the field with the wrong input should be saved
    Textfield Should Contain    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1

the user reloads the page with validation errors
    Reload Page

the users selects no in the other fundings section
    Select Radio button    other_funding-otherPublicFunding-35-54    No

the user enters invalid inputs in the other funding fields
    [Arguments]    ${SOURCE}    ${DATE}    ${FUNDING}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${SOURCE}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${DATE}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    ${FUNDING}
    Mouse out    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input
    Focus    jQuery=button:contains("Mark all as complete")

Remove row
    [Arguments]    ${section}
    the user clicks the button/link    jQuery=button:contains("Remove")
    the user clicks the button/link    ${section}

The user gets the expected validation errors
    [Arguments]    ${ERROR1}    ${ERROR2}
    Focus    jQuery=button:contains("Mark all as complete")
    Then the user should see an error    ${ERROR1}
    And the user should see an error    ${ERROR2}
