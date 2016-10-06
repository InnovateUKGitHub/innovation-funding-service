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
Resource          ../../resources/keywords/EMAIL_KEYWORDS.robot
Resource          Guest_commons.robot

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
    [Tags]
    ${correct_email}    ${incorrect_password}

Email persists with correct email address and invalid password
    [Documentation]    INFUND-703
    [Tags]
    ${correct_email}    ${invalid_password}

Email persists with correct email address and empty password
    [Documentation]    INFUND-703
    [Tags]
    ${correct_email}    ${EMPTY}

Email persists with wrong email address and correct password
    [Documentation]    INFUND-703
    [Tags]
    ${incorrect_email}    ${correct_password}

Email persists with wrong email address and wrong password
    [Documentation]    INFUND-703
    [Tags]
    ${incorrect_email}    ${incorrect_password}

Email persists with wrong email address and invalid password
    [Documentation]    INFUND-703
    [Tags]
    ${incorrect_email}    ${invalid_password}

Email persists with wrong email address and empty password
    [Documentation]    INFUND-703
    [Tags]
    ${incorrect_email}    ${EMPTY}

Email persists with invalid email address and correct password
    [Documentation]    INFUND-703
    [Tags]
    ${invalid_email}    ${correct_password}

Email persists with invalid email address and wrong password
    [Documentation]    INFUND-703
    [Tags]
    ${invalid_email}    ${incorrect_password}

Email persists with invalid email address and invalid password
    [Documentation]    INFUND-703
    [Tags]
    ${invalid_email}    ${invalid_password}

Email persists with invalid email address and empty password
    [Documentation]    INFUND-703
    [Tags]
    ${invalid_email}    ${EMPTY}

*** Keywords ***
