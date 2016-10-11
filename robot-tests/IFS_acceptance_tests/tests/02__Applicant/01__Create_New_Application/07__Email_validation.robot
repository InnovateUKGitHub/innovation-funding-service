*** Settings ***
Documentation     -INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile so I can log into the system
Suite Setup       The guest user opens the browser
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
Invalid email plaintext
    [Documentation]    INFUND-885
    [Tags]
    Given the user follows the flow to register their organisation
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${invalid_email_plain}
    And the user enters text to a text field    id=password    Passw0rd123
    And the user enters text to a text field    id=retypedPassword    Passw0rd123
    And the user submits their information
    Then the user should see an error    We were unable to create your account
    And the user should see an error    Please enter a valid email address

Invalid email disallowed symbols
    [Documentation]    INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${invalid_email_symbols}
    And the user enters text to a text field    id=password    Passw0rd123
    And the user enters text to a text field    id=retypedPassword    Passw0rd123
    And the user submits their information
    Then the user should see an error    We were unable to create your account
    And the user should see an error    Please enter a valid email address

Invalid email no username
    [Documentation]    INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${invalid_email_no_username}
    And the user enters text to a text field    id=password    Passw0rd123
    And the user enters text to a text field    id=retypedPassword    Passw0rd123
    And the user submits their information
    Then the user should see an error    We were unable to create your account
    And the user should see an error    Please enter a valid email address

Invalid email format
    [Documentation]    INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${invalid_email_format}
    And the user enters text to a text field    id=password    Passw0rd123
    And the user enters text to a text field    id=retypedPassword    Passw0rd123
    And the user submits their information
    Then the user should see an error    We were unable to create your account
    And the user should see an error    Please enter a valid email address

Email left blank
    [Documentation]    -INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${EMPTY}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    Please enter your email

Invalid email no @ symbol
    [Documentation]    INFUND-885
    [Tags]
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${invalid_email_no_at}
    And the user enters text to a text field    id=password    Passw0rd123
    And the user enters text to a text field    id=retypedPassword    Passw0rd123
    And the user submits their information
    Then the user should see an error    We were unable to create your account
    And the user should see an error    Please enter a valid email address

User can not login with the invalid email
    [Tags]
    Then the user cannot login with the invalid email    ${invalid_email_no_at}

Valid account creation
    [Documentation]    -INFUND-885
    [Tags]    HappyPath
    Given the user follows the flow to register their organisation
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information

Email duplication check
    [Documentation]    INFUND-886
    [Tags]    HappyPath
    Given the user follows the flow to register their organisation
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${valid_email}
    And the user enters text to a text field    id=password    ${correct_password}
    And the user enters text to a text field    id=retypedPassword    ${correct_password}
    And the user submits their information
    Then the user should see an error    The email address is already registered with us. Please sign into your account
    And the user logs out if they are logged in

*** Keywords ***
the user submits their information
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Select Checkbox    termsAndConditions
    Submit Form

the user cannot login with the invalid email
    [Arguments]    ${invalid_email_addy}
    go to    ${LOGIN_URL}
    Input Text    id=username    ${invalid_email_addy}
    Input Password    id=password    Passw0rd123
    Click Button    css=button[name="_eventId_proceed"]
    The user should see the text in the page    Please enter a valid e-mail address
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Click Button    css=button[name="_eventId_proceed"]
    The user should see the text in the page    ${unsuccessful_login_message}
    The user should see the text in the page    Your username/password combination doesn't seem to work
