*** Settings ***
Documentation     INFUND-1147: Further acceptance tests for the create account page
...
...               INFUND-2497: As a new user I would like to have an indication that my password is correct straight after typing so I know that I have to fix this before submitting my form
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/Variables/EMAIL_VARIABLES.robot
Resource          ../../../resources/Variables/PASSWORD_VARIABLES.robot

*** Test Cases ***
Invalid password (from the blacklist)
    [Documentation]    INFUND-1147
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${blacklisted_password}
    And the user enters text to a text field    id=retypedPassword    ${blacklisted_password}
    And the user submits their information
     #due to INFUND-2567    Then the user should see an error    Password is too weak
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password is too weak

Invalid password (all lower case)
    [Documentation]    INFUND-1147
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${lower_case_password}
    And the user enters text to a text field    id=retypedPassword    ${lower_case_password}
    And the user submits their information
     #due to INFUND-2567    Then the user should see an error    Password must contain at least one lower case letter
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password must contain at least one lower case letter

Invalid password (all upper case)
    [Documentation]    INFUND-1147
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${upper_case_password}
    And the user enters text to a text field    id=retypedPassword    ${upper_case_password}
    And the user submits their information
     #due to INFUND-2567    Then the user should see an error    Password must contain at least one upper case letter
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password must contain at least one upper case letter

Invalid password (no numbers)
    [Documentation]    INFUND-1147
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${no_numbers_password}
    And the user enters text to a text field    id=retypedPassword    ${no_numbers_password}
    Capture Page Screenshot
    And the user submits their information
     #due to INFUND-2567    Then the user should see an error    Password must contain at least one number
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password must contain at least one number

Invalid password (personal information)
    [Documentation]    INFUND-1147
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${personal_info_password}
    And the user enters text to a text field    id=retypedPassword    ${personal_info_password}
    And the user submits their information
     #due to INFUND-2567    Then the user should see an error    Password should not contain your last name
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password should not contain your last name

Password is too long
    [Documentation]    INFUND-885
    ...
    ...    INFUND-2497
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    And browser validations have been disabled
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${long_password}
    And the user enters text to a text field    id=retypedPassword    ${long_password}
    And the user submits their information
    Then the user should see an error    Password must not be more than 30 characters
    And The user should see the text in the page    We were unable to create your account

Password is too short
    [Documentation]    INFUND-885
    ...
    ...    INFUND-2497
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    0114123456778
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${short_password}
    And the user enters text to a text field    id=retypedPassword    ${short_password}
    And the user submits their information
    Then the user should see an error    Password must at least be 10 characters
    And The user should see the text in the page    We were unable to create your account

Password and re-typed password do not match
    [Documentation]    INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${incorrect_password}
    And the user submits their information
     #due to INFUND-2567    Then the user should see the text in the page    Passwords must match
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Passwords must match

Re-type password left blank
    [Documentation]    INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    ${EMPTY}
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${EMPTY}
    And the user submits their information
     #due to INFUND-2567    Then the user should see an error    Please re-type your password
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Passwords must match
    And The user should see the text in the page    Please re-type your password

Password left blank
    [Documentation]    INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${EMPTY}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
     #due to INFUND-2567    Then the user should see an error    Please enter your password
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Passwords must match
    And The user should see the text in the page    Please enter your password

User can not login with invalid password
    [Tags]    Pending
    Then the user cannot login with their new details    ${valid_email2}    ${short_password}

*** Keywords ***
the user cannot login with the invalid password
    [Arguments]    ${invalid_password}
    The user navigates to the page    ${LOGIN_URL}
    Input Text    id=username    ewan+40@hiveit.co.uk
    Input Password    id=password    ${invalid_password}
    Click Button    css=button[name="_eventId_proceed"]
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work
