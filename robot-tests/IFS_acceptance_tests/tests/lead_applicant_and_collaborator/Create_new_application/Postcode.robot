*** Settings ***
Documentation     INFUND-890 : As an applicant I want to use UK postcode lookup function to look up and enter my business address details as they won't necessarily be the same as the address held by Companies House, so that the system has accurate record of my contact details
Suite Setup        The guest user opens the browser
Suite Teardown     TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Enter Valid Postcode and the results should be displayed in the dropdown
    [Documentation]    INFUND-890
    Given the user is in Create your account page
    When the user enters the Valid Postcode
    Then the user should see the results based on the entered Postcode in "Select your address" dropdown
    and the applicant use the selected address
    and the address fields should be filled

Empty Postcode field
    [Documentation]    INFUND-890
    Given the user is in Create your account page
    When the user leaves the Postcode field empty
    Then user should be displayed with warning message

Same Operating address
    [Documentation]    INFUND-890
    Given the user is in Create your account page
    When the user selects the checkbox "The registered test is the same as the operating address"
    Then the address fields should not be displayed on the page
    And the user unselects the checkbox "The registered test is the same as the operating address"
    Then the user should be able enter the postcode and find the address

*** Keywords ***
the user is in Create your account page
    go to    ${POSTCODE_LOOKUP_URL}

the user enters the Valid Postcode
    Wait Until Element Is Visible    css=#postcode-check
    Input Text    css=#postcode-check    SN2 1FF
    Click Element    id=postcode-lookup

the user should see the results based on the entered Postcode in "Select your address" dropdown
    Wait Until Element Is Visible    css=#select-address-block

the user leaves the Postcode field empty
    Clear Element Text    css=#postcode-check
    Click Element    id=postcode-lookup

the user selects the checkbox "The registered test is the same as the operating address"
    Wait Until Element Is Visible    css=#address-same
    Click Element    css=#address-same

the address fields should not be displayed on the page
    Element Should not Be Visible    css=#manual-company-input

the user unselects the checkbox "The registered test is the same as the operating address"
    Unselect Checkbox    css=#address-same

the user should be able enter the postcode and find the address
    Element Should Be Visible    css=#manual-company-input

the applicant use the selected address
    Click Element    css=#select-address-block > button

the address fields should be filled
    Textfield Should Contain    id=street    Montrose House
    Textfield Should Contain    id=street-2    Clayhill Park
    Textfield Should Contain    id=town    Neston
    Textfield Should Contain    id=county    Cheshire
    Textfield Should Contain    id=postcode    CH64 3RU

user should be displayed with warning message
    Element Should Be Visible    css=.form-label .error-message
