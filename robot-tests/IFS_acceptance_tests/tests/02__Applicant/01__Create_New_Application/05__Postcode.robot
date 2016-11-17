*** Settings ***
Documentation     INFUND-890 : As an applicant I want to use UK postcode lookup function to look up and enter my business address details as they won't necessarily be the same as the address held by Companies House, so that the system has accurate record of my contact details
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Enter Valid Postcode and see the results in the dropdown
    [Documentation]    INFUND-890
    [Tags]    HappyPath
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=organisationSearchName    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=INNOVATE LTD
    And the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    And the address fields should be filled

Other Postcode values
    [Documentation]    INFUND-890, INFUND-2960
    [Tags]
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=organisationSearchName    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=INNOVATE LTD
    Then the user enters text to a text field    id=addressForm.postcodeInput    ${EMPTY}
    And the user clicks the button/link    id=postcode-lookup
    And the user should see the element    css=.form-label .error-message
    When the user enters text to a text field    id=addressForm.postcodeInput    BS14NT/
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    id=addressForm.selectedPostcodeIndex
    When the user enters text to a text field    id=addressForm.postcodeInput    BS14NT\\
    Then the user clicks the button/link    id=postcode-lookup
    And the backslash doesnt give errors

Same Operating address
    [Documentation]    INFUND-890
    [Tags]    HappyPath
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=organisationSearchName    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=INNOVATE LTD
    And the user selects the checkbox    id=address-same
    Then the user should not see the element    id=manual-company-input
    And the user unselects the checkbox    id=address-same
    And the user should see the element    id=manual-company-input

*** Keywords ***
the backslash doesnt give errors
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    the user should see the element    id=addressForm.selectedPostcodeIndex
    Run Keyword If    '${status}' == 'FAIL'    Wait Until Page Contains    No results were found
