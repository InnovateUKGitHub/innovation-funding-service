*** Settings ***
Documentation     INFUND-1147: Further acceptance tests for the create account page
...
...               INFUND-2497: As a new user I would like to have an indication that my password is correct straight after typing so I know that I have to fix this before submitting my form
Suite Setup       Run keywords    The guest user opens the browser
...               AND    The user follows the flow to register their organisation
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../../resources/variables/PASSWORD_VARIABLES.robot
Resource          ../../../resources/keywords/EMAIL_KEYWORDS.robot

*** Test Cases ***
Password from the blacklist
    [Documentation]    INFUND-1147
    [Tags]
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${blacklisted_password}
    And the user enters text to a text field    id=retypedPassword    ${blacklisted_password}
    And the user submits their information
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password is too weak

Password all lower case
    [Documentation]    INFUND-1147
    [Tags]    HappyPath
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${lower_case_password}
    And the user enters text to a text field    id=retypedPassword    ${lower_case_password}
    And the user submits their information
    #due to INFUND-2567    Then the user should see an error    Password must contain at least one upper case letter
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password must contain at least one upper case letter

Password all upper case
    [Documentation]    INFUND-1147
    [Tags]    Pending    # TODO
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${upper_case_password}
    And the user enters text to a text field    id=retypedPassword    ${upper_case_password}
    And the user submits their information
    #due to INFUND-2567    Then the user should see an error    Password must contain at least one lower case letter
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password must contain at least one lower case letter

Password without numbers
    [Documentation]    INFUND-1147
    [Tags]
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${no_numbers_password}
    And the user enters text to a text field    id=retypedPassword    ${no_numbers_password}
    And the user submits their information
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password must contain at least one number

Password with personal information
    [Documentation]    INFUND-1147
    [Tags]
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${personal_info_password}
    And the user enters text to a text field    id=retypedPassword    ${personal_info_password}
    And the user submits their information
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Password should not contain either your first or last name

Password is too long
    [Documentation]    INFUND-885
    ...
    ...    INFUND-2497
    [Tags]
    And browser validations have been disabled
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user accepts the terms and conditions
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
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    0114123456778
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${short_password}
    And the user enters text to a text field    id=retypedPassword    ${short_password}
    And the user submits their information
    Then the user should see an error    Password must at least be 10 characters
    And The user should see the text in the page    We were unable to create your account

Password and re-typed password do not match
    [Documentation]    INFUND-885
    [Tags]
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${incorrect_password}
    And the user submits their information
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Passwords must match

Re-type password left blank
    [Documentation]    INFUND-885
    [Tags]
    When the user enters text to a text field    id=firstName    ${EMPTY}
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${EMPTY}
    And the user submits their information
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Passwords must match
    And The user should see the text in the page    Please re-type your password

Password left blank
    [Documentation]    INFUND-885
    [Tags]
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user accepts the terms and conditions
    And the user enters text to a text field    id=email    ${valid_email2}
    And the user enters text to a text field    id=password    ${EMPTY}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    And The user should see the text in the page    We were unable to create your account
    And The user should see the text in the page    Passwords must match
    And The user should see the text in the page    Please enter your password

User cannot login with invalid password
    [Tags]
    Then the user cannot login with their new details    ${valid_email2}    ${short_password}

*** Keywords ***
the user cannot login with the invalid password
    [Arguments]    ${invalid_password}
    The user navigates to the page    ${LOGIN_URL}
    Input Text    id=username    ewan+40@hiveit.co.uk
    Input Password    id=password    ${invalid_password}
    The user clicks the button/link    css=button[name="_eventId_proceed"]
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    The user should see the text in the page    css=button[name="_eventId_proceed"]
    The user should see the text in the page    ${unsuccessful_login_message}
    The user should see the text in the page    Your username/password combination doesn't seem to work

the user accepts the terms and conditions
    Run Keyword And Ignore Error    Select Checkbox    termsAndConditions
