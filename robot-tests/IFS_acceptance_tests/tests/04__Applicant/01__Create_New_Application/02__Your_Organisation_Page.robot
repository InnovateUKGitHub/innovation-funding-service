*** Settings ***
Documentation     INFUND-887 : As an applicant I want the option to look up my business organisation's details using Companies House lookup so...
...
...               INFUND-890 : As an applicant I want to use UK postcode lookup function to look up and enter my business address details as they won't necessarily be the same as the address held by Companies House, so ...
Suite Setup       Applicant goes to the organisation search page
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../10__Project_setup/PS_Common.robot

*** Test Cases ***
Not in Companies House: Enter details manually link
    [Documentation]    INFUND-888
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=summary:contains("Enter details manually")
    Then the user should see the element    jQuery=button:contains("Find UK address")

Companies House: Valid company name
    [Documentation]    INFUND-887
    [Tags]    HappyPath
    When the user enters text to a text field    id=organisationSearchName    Hive IT
    And the user clicks the button/link    id=org-search
    Then the user should see the element    Link=${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}
    [Teardown]    The user goes back to the previous page

Companies House: User can choose the organisation and same operating address
    [Tags]    HappyPath
    When the user clicks the button/link    Link=${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}
    And the user should see the text in the page    Registered name
    And the user should see the text in the page    Registered Address
    And the user should see the text in the page    Registration number
    And the user selects the checkbox    address-same
    Then the user should not see the element    id=manual-company-input
    And the user unselects the checkbox    address-same
    And the user should see the element    id=manual-company-input
    [Teardown]    And the user goes back to the previous page

Companies House: Invalid company name
    [Documentation]    INFUND-887
    [Tags]
    When the user enters text to a text field    id=organisationSearchName    innoavte
    And the user clicks the button/link    id=org-search
    Then the user should see the text in the page    No results found.

Companies House: Valid registration number
    [Documentation]    INFUND-887
    [Tags]    HappyPath
    When the user enters text to a text field    id=organisationSearchName    05493105
    And the user clicks the button/link    id=org-search
    Then the user should see the element    Link=INNOVATE LTD
    [Teardown]    The user goes back to the previous page

Companies House: Empty company name field
    Given the user should see the text in the page    Create your account
    When the user enters text to a text field    id=organisationSearchName    ${EMPTY}
    And the user clicks the button/link    id=org-search
    Then the user should see an error    Please enter an organisation name to search

Enter address manually: Postcode Validations
    [Documentation]    INFUND-888
    [Tags]    HappyPath
    Given the user expands enter details manually
    Then the user enters text to a text field    id=addressForm.postcodeInput    ${EMPTY}
    And the user clicks the button/link    jQuery=button:contains("Find UK address")
    And the user expands enter details manually
    And the user should see the element    css=.form-label .error-message
    And the user moves focus to the element       css=[name="manual-address"]
    And the user enters text to a text field    id=addressForm.postcodeInput    BS14NT/
    And the user clicks the button/link    jQuery=button:contains("Find UK address")
    Then the user should see the element    id=addressForm.selectedPostcodeIndex
    When the user enters text to a text field    id=addressForm.postcodeInput    BS14NT\\
    Then the user clicks the button/link    jQuery=button:contains("Find UK address")
    And the backslash doesnt give errors

Enter Valid Postcode and see the results in the dropdown
    [Documentation]    INFUND-890
    [Tags]    HappyPath
    When the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    And the address fields should be filled

Manually add the details and pass to the confirmation page
    [Documentation]    INFUND-888
    [Tags]    HappyPath
    When the user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    The East Wing
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine2    Popple Manor
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine3    1, Popple Boulevard
    And the user enters text to a text field    id=addressForm.selectedPostcode.town    Poppleton
    And the user enters text to a text field    id=addressForm.selectedPostcode.county    Poppleshire
    And the user enters text to a text field    id=addressForm.selectedPostcode.postcode    POPPS123
    And the user enters text to a text field    name=organisationName    Top of the Popps
    And the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see the text in the page    The East Wing
    And the user should see the text in the page    Popple Manor
    And the user should see the text in the page    1, Popple Boulevard
    And the user should see the text in the page    Poppleton
    And the user should see the text in the page    POPPS123

*** Keywords ***
Applicant goes to the organisation search page
    Given the guest user opens the browser
    the user navigates to the page    ${frontDoor}
    Given the user clicks the button/link    link=Home and industrial efficiency programme
    When the user clicks the button/link    link=Start new application
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=span:contains("Business")
    And the user clicks the button/link    jQuery=button:contains("Save and continue")

the backslash doesnt give errors
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    the user should see the element    id=addressForm.selectedPostcodeIndex
    Run Keyword If    '${status}' == 'FAIL'    Wait Until Page Contains Without Screenshots    No results were found

the user expands enter details manually
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=summary:contains("Enter details manually")[aria-expanded="false"]
    run keyword if  '${status}'=='PASS'  the user clicks the button/link  jQuery=summary:contains("Enter details manually")[aria-expanded="false"]
