*** Settings ***
Documentation     INFUND-1480 As an assessor I want to be able to update/edit my profile information so that it is up to date.
Suite Setup       Run Keywords    guest user log-in    &{assessor2_credentials}
...               AND    User opens the edit details form
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***

Cancel button returns to read only view
    [Documentation]    INFUND-8009
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains(Cancel)
    Then the user should be redirected to the correct page    ${assessment_details_url}

Back button returns to assessor dashboard
    [Documentation]    INFUND-8009
    [Tags]
    Given the user clicks the button/link  link=Assessor dashboard
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}
    [Teardown]    user opens the edit details form

Back button from edit returns to read only view
    [Documentation]    INFUND-8009
    [Tags]
    Given the user clicks the button/link  link=Your details
    Then the user should be redirected to the correct page    ${assessment_details_url}
    [Teardown]    the user clicks the button/link    jQuery=a:contains("Edit")

Validations for invalid inputs
    [Documentation]    INFUND-1480
    [Tags]
    Given the user should see the text in the page    Edit your details
    And the user should see the element    jQuery=h3:contains("Email") ~ p:contains("felix.wilson@gmail.com")
    When The user enters text to a text field    id=firstName    Joy12
    And The user enters text to a text field    id=lastName    Archer12
    And the user enters text to a text field    id=phoneNumber    18549731414test
    And the user enters text to a text field    id=addressForm.addressLine1    ${EMPTY}
    And the user enters text to a text field    id=addressForm.town    ${EMPTY}
    And the user enters text to a text field    id=addressForm.postcode    ${EMPTY}
    And the user clicks the button/link    jQuery=button:contains("Save and return to your details")
    Then the user should see an error    Please enter a first name.
    And the user should see an error    Please enter a last name.
    And the user should see an error    Please enter a valid phone number.
    And the user should see an error    The address cannot be blank.
    And the user should see an error    The postcode cannot be blank.
    And the user should see an error    The town cannot be blank.

Valid Profile Update
    [Documentation]    INFUND-1480
    [Tags]    HappyPath
    When the assessor updates profile details
    And the user clicks the button/link    jQuery=a:contains("your details")
    Then the saved changes are visible

*** Keywords ***
the assessor updates profile details
    The user enters text to a text field    id=firstName    Joy
    The user enters text to a text field    id=lastName    Archer
    the user moves focus to the element    id=firstName
    the user enters text to a text field    id=addressForm.addressLine1    7, Phoenix house
    the user enters text to a text field    id=addressForm.town    Reading
    the user enters text to a text field    id=addressForm.postcode    RG1 7UH
    the user enters text to a text field    id=phoneNumber    18549731414
    the user clicks the button/link    jQuery=button:contains("Save and return to your details")

the saved changes are visible
    the user should see the text in the page    Joy
    the user should see the text in the page    Archer
    the user should see the text in the page    18549731414

User opens the edit details form
    Given the user clicks the button/link    jQuery=a:contains("your details")
    And the user clicks the button/link    jQuery=a:contains("Edit")
