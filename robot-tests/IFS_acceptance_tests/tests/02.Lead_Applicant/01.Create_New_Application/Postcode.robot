*** Settings ***
Documentation     INFUND-890 : As an applicant I want to use UK postcode lookup function to look up and enter my business address details as they won't necessarily be the same as the address held by Companies House, so that the system has accurate record of my contact details
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Enter Valid Postcode and the results should be displayed in the dropdown
    [Documentation]    INFUND-890    # note that I have used the word "postcode" as a postcode - any actual postcode will fail as the postcode lookup    # functionality does not yet exist
    [Tags]    HappyPath
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Sign in to apply")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=org-name    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    LINK=INNOVATE LTD
    When the user enters text to a text field    css=#postcode-check    postcode
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    And the address fields should be filled

Empty Postcode field
    [Documentation]    INFUND-890
    [Tags]    Failing
    # TODO EC Note that the test expects an error message which no longer exists - check whether it should or not!
    Given the user navigates to the page    ${POSTCODE_LOOKUP_URL}
    When the user enters text to a text field    css=#postcode-check    ${EMPTY}
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    css=.form-label .error-message

Same Operating address
    [Documentation]    INFUND-890
    [Tags]    HappyPath    Failing
    Given the user navigates to the page    ${POSTCODE_LOOKUP_URL}
    When the user selects the checkbox "The registered test is the same as the operating address"
    Then the user should not see the element    css=#postcode-check
    And the user unselects the checkbox "The registered test is the same as the operating address"
    And the user should see the element    css=#postcode-check

*** Keywords ***
the user selects the checkbox "The registered test is the same as the operating address"
    Select Checkbox    id=address-same

the user unselects the checkbox "The registered test is the same as the operating address"
    Unselect Checkbox    id=address-same

the address fields should be filled
    Textfield Should Contain    id=street    Montrose House
    Textfield Should Contain    id=street-2    Clayhill Park
    Textfield Should Contain    id=town    Neston
    Textfield Should Contain    id=county    Cheshire
    Textfield Should Contain    id=postcode    CH64 3RU
