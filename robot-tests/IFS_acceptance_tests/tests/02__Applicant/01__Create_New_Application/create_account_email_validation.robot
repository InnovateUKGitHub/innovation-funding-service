*** Settings ***
Documentation     -INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile so I can log into the system
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Test Template     Invalid Email Check
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${valid_email}    ewan@hiveit.co.uk
${invalid_email_plain}    notavalidemail
${invalid_email_symbols}    @%^%#$@#$@#.com
${invalid_email_no_username}    @example.com
${invalid_email_format}    Joe Smith <email@example.com>
${invalid_email_no_at}    email.example.com
${invalid_email_no_domain}    joesmith@example

*** Test Cases ***    email
Invalid email plaintext
                      [Documentation]                 INFUND-885
                      [Tags]                          Account
                      ${invalid_email_plain}

Invalid email disallowed symbols
                      [Documentation]                 INFUND-885
                      [Tags]                          Account
                      ${invalid_email_symbols}

Invalid email no username
                      [Documentation]                 INFUND-885
                      [Tags]                          Account
                      ${invalid_email_no_username}

Invalid email format
                      [Documentation]                 INFUND-885
                      [Tags]                          Account
                      ${invalid_email_format}

Invalid email no @ symbol
                      [Documentation]                 INFUND-885
                      [Tags]                          Account
                      ${invalid_email_no_at}

*** Keywords ***
Invalid Email Check
    [Arguments]    ${invalid_email}
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ${invalid_email}
    And the user enters text to a text field    id=password    password
    And the user enters text to a text field    id=retypedPassword    password
    And the user submits their information
    Then the user should see an error    We were unable to create your account
    And the user cannot login with the invalid email    ${invalid_email}

the user submits their information
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Select Checkbox    termsAndConditions
    Submit Form

the user cannot login with the invalid email
    [Arguments]    ${invalid_email_addy}
    go to    ${LOGIN_URL}
    Input Text    id=username    ${invalid_email_addy}
    Input Password    id=password    password
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    Please enter a valid e-mail address

    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work
