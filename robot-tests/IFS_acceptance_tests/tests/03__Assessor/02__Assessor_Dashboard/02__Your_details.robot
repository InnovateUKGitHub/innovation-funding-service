*** Settings ***
Documentation     INFUND-1480 As an assessor I want to be able to update/edit my profile information so that it is up to date.
Suite Setup       guest user log-in    &{assessor2_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor    Pending
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Validations for invalid inputs
    [Documentation]    INFUND-1480
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("Your details")
    When the user clicks the button/link    jQuery=a:contains("Edit your details")
    Then the user should see the text in the page    Edit your details
    And The user enters text to a text field    id=firstName    Joy12
    And The user enters text to a text field    id=lastName    Archer12
    And the user enters text to a text field    id=phoneNumber    18549731414test
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    ${Empty}
    And the user enters text to a text field    id=addressForm.selectedPostcode.town    ${Empty}
    And the user enters text to a text field    id=addressForm.selectedPostcode.postcode    ${Empty}
    And the user clicks the button/link    jQuery=button:contains("Save changes")
    Then the user should see an error    Please enter a first name
    And the user should see an error    Please enter a last name
    And the user should see an error    Please enter a valid phone number
    And the user should see an error    The address cannot be blank
    And the user should see an error    The postcode cannot be blank
    And the user should see an error    The town cannot be blank

Update profile
    [Documentation]    INFUND-1480
    [Tags]
    When the assessor updates profile details
    And the user clicks the button/link    jQuery=a:contains("Your details")
    Then the saved changes are visible

*** Keywords ***
the assessor updates profile details
    Select From List By Index    id=title    4
    The user enters text to a text field    id=firstName    Joy
    The user enters text to a text field    id=lastName    Archer
    the user moves focus to the element    id=gender2
    the user selects the radio button    gender    gender2
    the user selects the radio button    ethnicity    ethnicity1
    the user selects the radio button    disability    disability3
    the user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    7, Phoenix house
    the user enters text to a text field    id=addressForm.selectedPostcode.town    Reading
    the user enters text to a text field    id=addressForm.selectedPostcode.postcode    RG1 7UH
    the user enters text to a text field    id=phoneNumber    18549731414
    the user clicks the button/link    jQuery=button:contains("Save changes")

the saved changes are visible
    the user should see the text in the page    Dr
    the user should see the text in the page    Joy
    the user should see the text in the page    Archer
    the user should see the text in the page    Male
    the user should see the text in the page    White
    the user should see the text in the page    Prefer not to say
    the user should see the text in the page    18549731414