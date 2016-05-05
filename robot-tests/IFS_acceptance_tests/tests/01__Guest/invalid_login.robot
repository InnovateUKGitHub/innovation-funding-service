*** Settings ***
Documentation     -INFUND-703: As a user and I have provided the wrong login details I want to have the email address kept so I don't need to retype it
Suite Setup       Run Keywords    The guest user opens the browser
...               AND    the user navigates to the page    ${LOGIN_URL}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Guest
Test Template     Email persists on invalid login
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot

*** Variables ***
${correct_email}    steve.smith@empire.com
${incorrect_email}    steve.smith@idontexist.com
${invalid_email}    notavalidemailaddress
${correct_password}    Passw0rd
${incorrect_password}    wrongPassw0rd
${invalid_password}    allinlowercaseandnonumbers

*** Test Cases ***
Email persists with correct email address and wrong password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${correct_email}    ${incorrect_password}

Email persists with correct email address and invalid password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${correct_email}    ${invalid_password}

Email persists with correct email address and empty password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${correct_email}    ${EMPTY}

Email persists with wrong email address and correct password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${incorrect_email}    ${correct_password}

Email persists with wrong email address and wrong password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${incorrect_email}    ${incorrect_password}

Email persists with wrong email address and invalid password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${incorrect_email}    ${invalid_password}

Email persists with wrong email address and empty password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${incorrect_email}    ${EMPTY}

Email persists with invalid email address and correct password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${invalid_email}    ${correct_password}

Email persists with invalid email address and wrong password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${invalid_email}    ${incorrect_password}

Email persists with invalid email address and invalid password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${invalid_email}    ${invalid_password}

Email persists with invalid email address and empty password
    [Documentation]    INFUND-703
    [Tags]    Log in
    ${invalid_email}    ${EMPTY}

*** Keywords ***
Email persists on invalid login
    [Arguments]    ${email_address}    ${password}
    Given the guest user enters the login credentials    ${email_address}    ${password}
    When the user tries to log in
    Then the user is not logged-in
    And the email address should persist    ${email_address}

the user is not logged-in
    Element Should Not Be Visible    link=My dashboard
    Element Should Not Be Visible    link=Logout

the email address should persist
    [Arguments]    ${email_addy}
    # Note: we have to do it this way rather than the more straightforward eg Textfield Value Should Be
    # due to a bug in selenium2library
    ${stored_data}=    Get Value    id=username
    Should Be Equal    ${stored_data}    ${email_addy}

the user tries to log in
    the user clicks the button/link    css=button[name="_eventId_proceed"]
    wait until page contains    Your login was unsuccessful
