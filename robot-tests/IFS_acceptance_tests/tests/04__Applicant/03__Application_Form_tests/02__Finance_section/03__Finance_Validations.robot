*** Settings ***
Documentation     INFUND-844: As an applicant I want to receive a validation error in the finance sections if I my input is invalid in a particular field so that I am informed how to correctly submit the information
...
...               INFUND-2214: As an applicant I want to be prevented from marking my finances as complete if I have not fully completed the Other funding section so that I can be sure I am providing all the required information
Suite Setup       Custom Suite Setup
Suite Teardown    The user closes the browser      #Mark application details as incomplete and the user closes the browser  Robot test application
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot

*** Test Cases ***
Mark as complete Your funding with only one input should not be possible
    [Documentation]    INFUND-2214
    [Tags]
    When the user clicks the button/link      link=Your funding
    And the user enters text to a text field  css=[name^="finance-grantclaimpercentage"]  70
    And the user moves focus to the element   css=[data-target="other-funding-table"] label
    Then the user should see the element      jQuery=.disabled:contains("Mark as complete")

Other funding client side
    [Documentation]    INFUND-2214
    [Tags]
    When the user clicks the button twice    css=label[for$="otherPublicFunding-yes"]
    And the user enters invalid inputs in the other funding fields  ${EMPTY}  132020  -6565
    Then the user should see the element     css=#other-funding-table[aria-hidden="false"]
    # This line should be after css=label[for$="otherPublicFunding-yes"], but it requires a bit more time to be loaded, thus is put here.
    When the user should see a field error   Funding source cannot be blank.
    Then the user should see a field error   Invalid secured date.
    And the user should see a field error    This field should be 1 or higher.

Other funding server side
    [Documentation]    INFUND-2214
    [Tags]
    [Setup]
    When the user enters invalid inputs in the other funding fields    ${EMPTY}    13-2020    -6565
    And the user selects the checkbox    agree-terms-page
    And the user clicks the button/link  jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error   Funding source cannot be blank.
    And the user should see a field and summary error    Please use MM-YYYY format.
    And the user should see a field and summary error    This field should be 1 or higher.

Select NO Other Funding and mark as complete should be possible
    [Documentation]    INFUND-2214
    [Tags]
    Given the user enters text to a text field  css=[name^="finance-grantclaimpercentage"]  50
    When the user clicks the button/link        jQuery=label:contains("No")
    And the user selects the checkbox           agree-terms-page
    Then the user clicks the button/link        jQuery=button:contains("Mark as complete")
    And the user should not see an error in the page

Labour client side
    [Documentation]    INFUND-844
    [Tags]
    Given the user clicks the button/link       link=Your project costs
    And the user clicks the button/link         jQuery=button:contains("Labour")
    When the user enters text to a text field   css=[name^="labour-labourDaysYearly"]    -1
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    Then the user should see a field error      This field should be 1 or higher.
    When the user enters text to a text field   css=[name^="labour-labourDaysYearly"]    366
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    12121212121212121212121212
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    123456789101112
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    Then the user should see a field error      This field should be 9999999999999999999 or lower.
    And the user should see a field error       You must enter a value less than 10 digits.
    And the user should see a field error       This field should be 365 or lower.
    And the user should see a field error       This field cannot be left blank.
    When the user enters text to a text field   css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    123456789101112131415161718192021
    When the user enters text to a text field   css=[name^="labour-labourDaysYearly"]    120
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    -1
    Then the user should see a field error      This field should be 9999999999999999999 or lower.
    And the user should see a field error       This field should be 1 or higher.

Labour server side
    [Documentation]    INFUND-844
    [Tags]
    When the user enters text to a text field   css=[name^="labour-labourDaysYearly"]    366
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    ${EMPTY}
    And the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    -1
    And the user selects the checkbox           stateAidAgreed
    And the user clicks the button/link         jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error   This field should be 1 or higher.
    And the user should see a field and summary error    This field cannot be left blank.
    And the user should see a field and summary error    This field should be 365 or lower
    [Teardown]    Run keywords    the user enters text to a text field    css=[name^="labour-labourDaysYearly"]    21
    ...    AND    Remove row    jQuery=button:contains("Labour")    jQuery=.labour-costs-table button:contains("Remove")

Materials client side
    [Documentation]    INFUND-844
    [Tags]  HappyPath
    Given the user clicks the button/link       jQuery=button:contains("Materials")
    When the user enters text to a text field   css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    1234567810111213141516171819202122
    And the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -1
    the user moves focus to the element         css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(4) input
    Then the user should see a field error      You must enter a value less than 10 digits.
    And the user should see a field error       This field should be 1 or higher.

Materials server side
    [Documentation]    INFUND-844
    [Tags]  HappyPath
    When the user enters text to a text field   css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    1212121212121212121212
    And the user selects the checkbox           stateAidAgreed
    And the user clicks the button/link         jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error   This field cannot be left blank.
    And the user should see a field and summary error    This field can only accept whole numbers.
    And the user should see a field and summary error    This field should be 1 or higher.
    When the user enters text to a text field   css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    1
    And the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -1
    And the user clicks the button/link         jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error   This field cannot be left blank.
    And the user should see a field and summary error    This field should be 1 or higher.
    [Teardown]    Remove row    jQuery=button:contains("Material")    jQuery=#material-costs-table button:contains("Remove")

Capital usage client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery=button:contains("Capital usage")
    When the user enters text to a text field   css=.form-finances-capital-usage-depreciation    ${EMPTY}
    And the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-residual-value    12121212121212121212121212121
    And the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    -1
    And the user enters text to a text field    css=.form-finances-capital-usage-utilisation    101
    Then the user should see a field error      This field should be 1 or higher.
    Then the user should see a field error      You must enter a value less than 20 digits.
    And the user should see a field error       This field cannot be left blank.
    And the user should see a field error       This field should be 100 or lower.
    When the user enters text to a text field   css=.form-finances-capital-usage-depreciation    12121212121212121212121212121
    And the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-residual-value    -1
    And the user enters text to a text field    css=.form-row:nth-child(1) .form-finances-capital-usage-npv    -1
    And the user enters text to a text field    css=.form-finances-capital-usage-utilisation    101
    Then the user should see a field error      You must enter a value less than 10 digits.
    And the user should see a field error       This field should be 1 or higher.
    And the user should see a field error       This field should be 0 or higher.
    And the user should see a field error       This field should be 100 or lower.

Capital usage server side
    [Documentation]    INFUND-844
    When the user enters text to a text field  css=.form-row:nth-child(1) .form-finances-capital-usage-npv    -1
    And the user enters text to a text field   css=.form-row:nth-child(1) .form-finances-capital-usage-residual-value    -2
    And the user enters text to a text field   css=.form-finances-capital-usage-utilisation    -1
    And the user enters text to a text field   css=.form-finances-capital-usage-depreciation    ${EMPTY}
    And the user clicks the button/link        jQuery=button:contains("Mark as complete")
    Then the user should see a summary error   This field should be 1 or higher.
    And the user should see a summary error    This field should be 1 or higher.
    And the user should see a summary error    This field should be 1 or higher.
    [Teardown]    Remove row    jQuery=button:contains("Capital usage")    jQuery=#capital_usage button:contains("Remove")

Subcontracting costs client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery=button:contains("Subcontracting costs")
    When the user enters text to a text field   css=#collapsible-4 .form-row:nth-child(1) input[id="formInput[cost-1847-cost]"]    ${EMPTY}
    And the user enters text to a text field    css=#collapsible-4 .form-row:nth-child(1) input[id="formInput[cost-1847-name]"]   ${EMPTY}
    And the user enters text to a text field    css=#collapsible-4 .form-row:nth-child(1) input[id="formInput[cost-1847-country]"]   ${EMPTY}
    And the user enters text to a text field    css=#collapsible-4 .form-row:nth-child(1) textarea[id="formInput[cost-1847-role]"]   ${EMPTY}
    Then the user should see a field error   This field cannot be left blank.
    And the user should see a field error    This field cannot be left blank.
    And the user should see a field error    This field cannot be left blank.
    And the user should see a field error    This field cannot be left blank.

Subcontracting costs server side
    [Documentation]    INFUND-844
    When the user enters text to a text field            css=#collapsible-4 .form-row:nth-child(1) input[id="formInput[cost-1847-cost]"]    -100
    And the user enters text to a text field             css=#collapsible-4 .form-row:nth-child(1) input[id="formInput[cost-1847-name]"]     ${EMPTY}
    And the user clicks the button/link                  jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error   This field should be 1 or higher.
    And the user should see a field and summary error    This field cannot be left blank.
    [Teardown]    Remove row    jQuery=button:contains("Subcontracting")    jQuery=#subcontracting button:contains("Remove")

Travel and subsistence client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery=button:contains("Travel and subsistence")
    When the user enters text to a text field   css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    0123456789101112131415161718192021
    And the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -1
    Then the user should see a field error      This field cannot be left blank.
    Then the user should see a field error      You must enter a value less than 10 digits.
    And the user should see an error            This field should be 1 or higher.
    When the user enters text to a text field   css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    0
    And the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    13123232134234234234234234423
    Then the user should see a field error      This field cannot be left blank.
    Then the user should see a field error      You must enter a value less than 20 digits.
    And the user should see an error            This field should be 1 or higher.

Travel and subsistence server side
    [Documentation]    INFUND-844
    When the user enters text to a text field   css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    0123456789101112131415161718192021
    And the user clicks the button/link         jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error   This field cannot be left blank.
    And the user should see a field and summary error    This field should be 1 or higher.
    And the user should see a field and summary error    This field can only accept whole numbers.
    [Teardown]    Remove row    jQuery=button:contains("Travel")    jQuery=#travel-costs-table button:contains("Remove")

Other costs client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery=button:contains("Other costs")
    When the user enters text to a text field   css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${EMPTY}
    And the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) textarea    ${EMPTY}
    Then the user should see a field error      This field cannot be left blank.
    Then the user should see a field error      This field cannot be left blank.
    When the user enters text to a text field   css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) textarea    ${EMPTY}
    Then the user should see a field error      This field cannot be left blank.
    Then the user should see a field error      This field should be 1 or higher.

Other costs server side
    [Documentation]    INFUND-844
    When the user enters text to a text field   css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) textarea    ${EMPTY}
    And the user clicks the button/link         jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error   This field should be 1 or higher.
    And the user should see a field and summary error    This field cannot be left blank.
    [Teardown]    Remove row    jQuery=button:contains("Other costs")    jQuery=#other-costs-table button:contains("Remove")

#Funding level client side is covered in 02__Org_size_validation.robot

Funding level server side
    [Documentation]    INFUND-844
    [Setup]  the user clicks the button/link     link=Your finances
    Given the user clicks the button/link        link=Your funding
    And the user clicks the button/link          jQuery=button:contains("Edit your funding")
    When the user enters text to a text field    css=[name^="finance-grantclaimpercentage"]  71
    And the user selects the checkbox            agree-terms-page
    And the user clicks the button/link          jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error   This field should be 70% or lower.
    Then the user enters text to a text field    css=[name^="finance-grantclaimpercentage"]  69
    And the user moves focus to the element      css=[data-target="other-funding-table"] label

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    the user logs-in in new browser       &{lead_applicant_credentials}
    log in and create new application if there is not one already with complete application details  ${OPEN_COMPETITION_APPLICATION_5_NAME}  ${tomorrowday}  ${month}  ${nextyear}
    the user selects Research category    Feasibility studies
    the user fills in the organisation information  ${OPEN_COMPETITION_APPLICATION_5_NAME}  ${SMALL_ORGANISATION_SIZE}

the user enters invalid inputs in the other funding fields
    [Arguments]    ${SOURCE}    ${DATE}    ${FUNDING}
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${SOURCE}
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${DATE}
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    ${FUNDING}
    the user moves focus to the element     css=button.button[type="submit"]

Remove row
    [Arguments]    ${section}    ${close button}
    the user moves focus to the element    ${close button}
    wait for autosave
    the user clicks the button/link    ${close button}
    the user clicks the button/link    ${section}