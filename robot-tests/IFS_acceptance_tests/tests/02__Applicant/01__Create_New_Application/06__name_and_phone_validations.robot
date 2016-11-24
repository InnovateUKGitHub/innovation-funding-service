*** Settings ***
Documentation     -INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile so I can log into the system
...
...               -INFUND-886:As an applicant I want the system to recognise an existing user profile if I try to create a new account with matching details so that I am prevented from creating a new duplicate profile
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
First name left blank
    [Documentation]    -INFUND-885
    [Tags]
    Given the user follows the flow to register their organisation
    # Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    ${EMPTY}
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter a first name
    And the user should see an error    We were unable to create your account
    And the user cannot login with their new details    ${valid_email}    ${correct_password}
    And the user logs out if they are logged in

Last name left blank
    [Documentation]    -INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    ${EMPTY}
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter a last name

Phone number left blank
    [Documentation]    -INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    ${EMPTY}
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter a phone number

Phone number validation
    [Documentation]    -INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    invalidphone
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter a valid phone number

Phone number too short
    [Documentation]    -INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    0123
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Input for your phone number has a minimum length of 8 characters

*** Keywords ***
the user submits their information
    the user selects the checkbox    termsAndConditions
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Submit Form
