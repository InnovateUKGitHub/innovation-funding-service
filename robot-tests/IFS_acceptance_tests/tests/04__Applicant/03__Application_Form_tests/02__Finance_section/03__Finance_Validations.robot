*** Settings ***
Documentation     INFUND-844: As an applicant I want to receive a validation error in the finance sections if I my input is invalid in a particular field so that I am informed how to correctly submit the information
...
...               INFUND-2214: As an applicant I want to be prevented from marking my finances as complete if I have not fully completed the Other funding section so that I can be sure I am providing all the required information
...
...               IFS-4569: As an applicant I am able to input a non-UK postcode for Project location
...
...               IFS-5920 Acceptance tests for T's and C's
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot

*** Test Cases ***
Your funding: client and server side validations
    [Documentation]    INFUND-2214  IFS-5920
    [Tags]
    Given the user clicks the button/link                link = Your funding
    And the user selects the radio button                requestingFunding   true
    When the user enters text to a text field            css = [name^="grantClaimPercentage"]  ${EMPTY}
    Then the user should see validations on your funding page

Other funding client side
    [Documentation]    INFUND-2214
    [Tags]
    When the user selects the radio button   otherFunding  true
    And the user enters invalid inputs in the other funding fields  ${EMPTY}  132020
    Then the user should see the element     css = #other-funding-table[aria-hidden="false"]
    # This line should be after css = label[for$="otherPublicFunding-yes"], but it requires a bit more time to be loaded, thus is put here.
    When the user should see a field error   Enter a funding source.
    Then the user should see a field error   Enter date secured.

Other funding server side
    [Documentation]    INFUND-2214
    [Tags]
    When the user enters invalid inputs in the other funding fields    ${EMPTY}    13-2020
    And the user clicks the button/link                  jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error   Enter a funding source.
    And the user should see a field and summary error    Enter date secured.

Select NO Other Funding and mark as complete should be possible
    [Documentation]    INFUND-2214
    [Tags]
    Given the user selects the radio button     requestingFunding   false
    When the user selects the radio button      otherFunding  false
    Then the user clicks the button/link        jQuery = button:contains("Mark as complete")
    And the user should not see an error in the page

Labour client side
    [Documentation]    INFUND-844
    [Tags]
    Given the user clicks the button/link       link = Your project costs
    And the user clicks the button/link         jQuery = button:contains("Labour")
    When the user enters text to a text field   id = working-days-per-year    -1
    And the user enters text to a text field    css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    Then the user should see a field error      ${field_should_be_1_or_higher}
    When the user enters text to a text field   id = working-days-per-year    366
    And the user enters text to a text field    css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    12121212121212121212121212
    And the user enters text to a text field    css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    123456789101112
    And the user enters text to a text field    css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    Then the user should see a field error      This field should be 9999999999999999999 or lower.
    And the user should see a field error       You must enter a value less than 10 digits.
    And the user should see a field error       This field should be 365 or lower.
    And the user should see a field error       ${empty_field_warning_message}
    When the user enters text to a text field   css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    123456789101112131415161718192021
    When the user enters text to a text field   id = working-days-per-year    120
    And the user enters text to a text field    css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    -1
    Then the user should see a field error      This field should be 9999999999999999999 or lower.
    And the user should see a field error       ${field_should_be_1_or_higher}

Labour server side
    [Documentation]    INFUND-844
    [Tags]
    When the user enters text to a text field   id = working-days-per-year    366
    And the user enters text to a text field    css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    ${EMPTY}
    And the user enters text to a text field    css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    -1
    And the user selects the checkbox           stateAidAgreed
    And the user clicks the button/link         jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error   ${field_should_be_1_or_higher}
    And the user should see a field and summary error    ${empty_field_warning_message}
    And the user should see a field and summary error    This field should be 365 or lower
    [Teardown]    Run keywords    the user enters text to a text field    id = working-days-per-year    21
    ...    AND    Remove row    jQuery = button:contains("Labour")    jQuery = #labour-costs-table button:contains("Remove")

Overhead cost client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link    jQuery = button:contains("Overhead costs")
    When the user selects the radio button   overhead.rateType  overhead-rate-type-total
    And the user uploads the file            id = overhead.file  ${text_file}
    Then the user should see a field error    Please upload a file in .xls, .xlsx or .ods format only.
    #TODO Add validaions for text feild once IFS-2555 done

Overhead cost server side
    [Documentation]    INFUND-844
    When the user clicks the button/link       jQuery = button:contains("Mark as complete")
    Then The user should see a summary error   You should upload a completed overheads spreadsheet.

Materials client side
    [Documentation]    INFUND-844
    [Tags]
    Given the user clicks the button/link       jQuery = button:contains("Materials")
    When the user enters text to a text field   css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    1234567810111213141516171819202122
    And the user enters text to a text field    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -1
    And Set Focus To Element                    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(4) input
    Then the user should see a field error      You must enter a value less than 10 digits.
    And the user should see a field error       ${field_should_be_1_or_higher}

Materials server side
    [Documentation]    INFUND-844
    [Tags]
    When the user enters text to a text field   css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    1212121212121212121212
    And the user selects the checkbox           stateAidAgreed
    And the user clicks the button/link         jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error   ${empty_field_warning_message}
    And the user should see a field and summary error    ${only_accept_whole_numbers_message}
    And the user should see a field and summary error    ${field_should_be_1_or_higher}
    When the user enters text to a text field   css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    1
    And the user enters text to a text field    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -1
    And the user clicks the button/link         jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error   ${empty_field_warning_message}
    And the user should see a field and summary error    ${field_should_be_1_or_higher}
    [Teardown]    Remove row    jQuery = button:contains("Material")    jQuery = #material-costs-table button:contains("Remove")

Capital usage client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery = button:contains("Capital usage")
    When the user enters text to a text field   css = .form-finances-capital-usage-depreciation    ${EMPTY}
    And the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-residual-value    12121212121212121212121212121
    And the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-npv    -1
    And the user enters text to a text field    css = .form-finances-capital-usage-utilisation    101
    Then the user should see a field error      ${field_should_be_1_or_higher}
    Then the user should see a field error      You must enter a value less than 20 digits.
    And the user should see a field error       ${empty_field_warning_message}
    And the user should see a field error       This field should be 100 or lower.
    When the user enters text to a text field   css = .form-finances-capital-usage-depreciation    12121212121212121212121212121
    And the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-residual-value    -1
    And the user enters text to a text field    css = .form-row:nth-child(1) .form-finances-capital-usage-npv    -1
    And the user enters text to a text field    css = .form-finances-capital-usage-utilisation    101
    Then the user should see a field error      You must enter a value less than 10 digits.
    And the user should see a field error       ${field_should_be_1_or_higher}
    And the user should see a field error       This field should be 0 or higher.
    And the user should see a field error       This field should be 100 or lower.

Capital usage server side
    [Documentation]    INFUND-844
    When the user enters text to a text field  css = .form-row:nth-child(1) .form-finances-capital-usage-npv    66.66
    And the user enters text to a text field   css = .form-row:nth-child(1) .form-finances-capital-usage-residual-value    66.67
    And the user enters text to a text field   css = .form-finances-capital-usage-utilisation    50.58
    And the user enters text to a text field   css = .form-finances-capital-usage-depreciation    ${EMPTY}
    And the user clicks the button/link        jQuery = button:contains("Mark as complete")
    Then the user should see a summary error   ${empty_field_warning_message}
    [Teardown]    Remove row    jQuery = button:contains("Capital usage")    jQuery = #capital-usage button:contains("Remove")

Subcontracting costs client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery = button:contains("Subcontracting")
    When the user enters text to a text field   css = #accordion-finances-content-5 .form-row:nth-child(1) input[id$="cost"]    ${EMPTY}
    And the user enters text to a text field    css = #accordion-finances-content-5 .form-row:nth-child(1) input[id$="name"]   ${EMPTY}
    And the user enters text to a text field    css = #accordion-finances-content-5 .form-row:nth-child(1) input[id$="country"]   ${EMPTY}
    And the user enters text to a text field    css = #accordion-finances-content-5 .form-row:nth-child(1) textarea[id$="role"]   ${EMPTY}
    Then the user should see a field error      ${empty_field_warning_message}

Subcontracting costs server side
    [Documentation]    INFUND-844
    When the user enters text to a text field            css = #accordion-finances-content-5 .form-row:nth-child(1) input[id$="cost"]    -100
    And the user enters text to a text field             css = #accordion-finances-content-5 .form-row:nth-child(1) input[id$="name"]     ${EMPTY}
    And the user clicks the button/link                  jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error   ${field_should_be_1_or_higher}
    And the user should see a field and summary error    ${empty_field_warning_message}
    [Teardown]    Remove row    jQuery = button:contains("Subcontracting")    jQuery = #subcontracting button:contains("Remove")

Travel and subsistence client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery = button:contains("Travel and subsistence")
    When the user enters text to a text field   css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    0123456789101112131415161718192021
    And the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    -1
    Then the user should see a field error      ${empty_field_warning_message}
    Then the user should see a field error      You must enter a value less than 10 digits.
    And the user should see a field and summary error    ${field_should_be_1_or_higher}
    When the user enters text to a text field   css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    0
    And the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    13123232134234234234234234423
    Then the user should see a field error      ${empty_field_warning_message}
    Then the user should see a field error      You must enter a value less than 20 digits.
    And the user should see a field and summary error    ${field_should_be_1_or_higher}

Travel and subsistence server side
    [Documentation]    INFUND-844
    When the user enters text to a text field   css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${EMPTY}
    And the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    0123456789101112131415161718192021
    And the user clicks the button/link         jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error   ${empty_field_warning_message}
    And the user should see a field and summary error    ${field_should_be_1_or_higher}
    And the user should see a field and summary error    ${only_accept_whole_numbers_message}
    [Teardown]    Remove row    jQuery = button:contains("Travel")    jQuery = #travel-costs-table button:contains("Remove")

Other costs client side
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery = button:contains("Other costs")
    When the user enters text to a text field   css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${EMPTY}
    And the user enters text to a text field    css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) textarea    ${EMPTY}
    Then the user should see a field error      ${empty_field_warning_message}
    Then the user should see a field error      ${empty_field_warning_message}
    When the user enters text to a text field   css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) textarea    ${EMPTY}
    Then the user should see a field error      ${empty_field_warning_message}
    Then the user should see a field error      ${field_should_be_1_or_higher}

Other costs server side
    [Documentation]    INFUND-844
    When the user enters text to a text field   css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    -1
    And the user enters text to a text field    css = #other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) textarea    ${EMPTY}
    And the user clicks the button/link         jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error   ${field_should_be_1_or_higher}
    And the user should see a field and summary error    ${empty_field_warning_message}
    [Teardown]    Remove row    jQuery = button:contains("Other costs")    jQuery = #other-costs-table button:contains("Remove")

Project location client side validations
    [Documentation]  IFS-4681
    [Setup]  the user clicks the button/link   link = Your project finances
    Given the user clicks the button/link      link = Your project location
    When the user enters text to a text field  id = postcode  ${EMPTY}
    Then the user should see a field error     Enter a valid postcode.

Project location server-side validations
    [Documentation]  IFS-4569
    Given the user clicks the button/link               id = mark_as_complete
    Then The user should see a field and summary error  Enter a valid postcode.
    And the user enters text to a text field            id = postcode  BAN
    And the user cannot see a validation error in the page
#Funding level client side is covered in 02__Org_size_validation.robot

Funding level server side
    [Documentation]    INFUND-844
    [Setup]  the user clicks the button/link     link = Your project finances
    Given the user clicks the button/link        link = Your funding
    And the user clicks the button/link          jQuery = button:contains("Edit your funding")
    And the user selects the radio button        requestingFunding   true
    When the user enters text to a text field    css = [name^="grantClaimPercentage"]  71
    And the user clicks the button/link          jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error   Funding level must be 70% or lower.
    And the user selects the radio button        requestingFunding   true
    Then the user enters text to a text field    css = [name^="grantClaimPercentage"]  69
    And Set Focus To Element                     css = [data-target="other-funding-table"] label

*** Keywords ***
Custom Suite Setup
    the user logs-in in new browser       	malcom.jones@load.example.com  Passw0rd
    the user clicks the button/link       link = New algorithms to improve machine efficiency
    the user selects Research category    Feasibility studies
    the user fills in the organisation information  New algorithms to improve machine efficiency  ${SMALL_ORGANISATION_SIZE}

the user enters invalid inputs in the other funding fields
    [Arguments]    ${SOURCE}    ${DATE}
    the user enters text to a text field    css = #other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${SOURCE}
    the user enters text to a text field    css = #other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${DATE}
    Set Focus To Element                    css = button.govuk-button[type="submit"]

Remove row
    [Arguments]    ${section}    ${close button}
    Set Focus To Element               ${close button}
    wait for autosave
    the user clicks the button/link    ${close button}
    the user clicks the button/link    ${section}

the user should see validations on your funding page
    the user should see a field error               ${empty_field_warning_message}
    the user clicks the button/link                 jQuery = button:contains("Mark as complete")
    the user should see a field and summary error   ${empty_field_warning_message}

Custom suite teardown
    the user closes the browser