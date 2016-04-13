*** Settings ***
Documentation     -INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile so I can log into the system
...
...               -INFUND-886:As an applicant I want the system to recognise an existing user profile if I try to create a new account with matching details so that I am prevented from creating a new duplicate profile
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${correct_password}    Passw0rd
${incorrect_password}    wrongpassword
${long_password}    passwordpasswordpasswordpasswordpasswordpasswordpassword
${short_password}    pass
${valid_email}    ewan+14@hiveit.co.uk
${email_already_in_db}    ewan+12@hiveit.co.uk

*** Test Cases ***
First name left blank
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
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
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    ${EMPTY}
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter a last name
    And the user cannot login with their new details    ${valid_email}    ${correct_password}
    And the user logs out if they are logged in

Phone number left blank
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    ${EMPTY}
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter a phone number
    And the user cannot login with their new details    ${valid_email}    ${correct_password}
    And the user logs out if they are logged in

Phone number validation
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    invalidphone
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter a valid phone number
    And the user cannot login with their new details    ${valid_email}    ${correct_password}
    And the user logs out if they are logged in

Phone number too short
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    0123
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Input for your phone number has a minimum length of 8 characters
    And the user cannot login with their new details    ${valid_email}    ${correct_password}
    And the user logs out if they are logged in

Email left blank
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${EMPTY}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter your email
    And the user logs out if they are logged in

Password left blank
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${EMPTY}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter your password
    And the user cannot login with their new details    ${valid_email}    ${correct_password}
    And the user logs out if they are logged in

Re-type password left blank
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    ${EMPTY}
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${EMPTY}
    And the user submits their information
    Then the user should see an error    Please re-type your password
    And the user cannot login with their new details    ${valid_email}    ${correct_password}
    And the user logs out if they are logged in

Password and re-typed password do not match
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${incorrect_password}
    And the user submits their information
    Then the user should see the text in the page    Passwords must match
    And the user cannot login with either password
    And the user logs out if they are logged in

Password is too short
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${short_password}
    And the user enters text to a text field    id=retypedPassword    ${short_password}
    And the user submits their information
    Then the user should see an error    Your password must be between 8 and 30 characters
    And the user cannot login with their new details    ${valid_email}    ${short_password}
    And the user logs out if they are logged in

Password is too long
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    Given browser validations have been disabled
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${long_password}
    And the user enters text to a text field    id=retypedPassword    ${long_password}
    And the user submits their information
    Then the user should see an error    Your password must be between 8 and 30 characters
    And the user cannot login with their new details    ${valid_email}    ${long_password}
    And the user logs out if they are logged in

Valid account creation
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations    HappyPath
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    And the user verifies their email    ${verify_link_1}
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    And the user clicks the button/link    jQuery=.button:contains("Log")
    Then the user can login with their new details
    And the user should see the element    link=Logout
    And the user clicks the button/link    link=Logout

Email duplication check
    [Documentation]    INFUND-886
    [Tags]    Account    Validations
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${email_already_in_db}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Email address is already in use
    And the user logs out if they are logged in

*** Keywords ***
the user submits their information
    Select Checkbox    termsAndConditions
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Submit Form

the user cannot login with their new details
    [Arguments]    ${email}    ${password}
    The user navigates to the page    ${LOGIN_URL}
    Input Text    id=username    ${email}
    Input Password    id=password    ${password}
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work

the user cannot login with either password
    The user navigates to the page    ${LOGIN_URL}
    Input Text    id=username    ${valid_email}
    Input Password    id=password    ${correct_password}
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work
    go to    ${LOGIN_URL}
    Input Text    id=username    ${valid_email}
    Input Password    id=password    ${incorrect_password}
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work

the user can login with their new details
    The user navigates to the page    ${LOGIN_URL}
    Input Text    id=username    ${email_already_in_db}
    Input Password    id=password    ${correct_password}
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Not Contain    something has gone wrong

the user logs out if they are logged in
    run keyword and ignore error    log out as user
