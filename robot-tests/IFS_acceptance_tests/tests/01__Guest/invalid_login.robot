*** Settings ***
Documentation     -INFUND-703: As a user and I have provided the wrong login details I want to have the email address kept so I don't need to retype it
Suite Setup       Run Keywords    The guest user opens the browser
...               AND    the user navigates to the page    ${LOGIN_URL}
Suite Teardown    The user closes the browser
Force Tags        Guest
Test Template     Email persists on invalid login
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Email persists with correct email address and wrong password
    [Documentation]    INFUND-703
    [Tags]
    ${lead_applicant}    ${incorrect_password}

Email persists with correct email address and invalid password
    [Documentation]    INFUND-703
    [Tags]
    ${lead_applicant}    ${lower_case_password}

Email persists with correct email address and empty password
    [Documentation]    INFUND-703
    [Tags]
    ${lead_applicant}    ${EMPTY}

Email persists with wrong email address and correct password
    [Documentation]    INFUND-703
    [Tags]
    ${valid_email2}    ${correct_password}

Email persists with wrong email address and wrong password
    [Documentation]    INFUND-703
    [Tags]
    ${valid_email2}    ${incorrect_password}

Email persists with wrong email address and invalid password
    [Documentation]    INFUND-703
    [Tags]
    ${valid_email2}    ${lower_case_password}

Email persists with wrong email address and empty password
    [Documentation]    INFUND-703
    [Tags]
    ${valid_email2}    ${EMPTY}

Email persists with invalid email address and correct password
    [Documentation]    INFUND-703
    [Tags]
    ${invalid_email_plain}    ${correct_password}

Email persists with invalid email address and wrong password
    [Documentation]    INFUND-703
    [Tags]
    ${invalid_email_plain}    ${incorrect_password}

Email persists with invalid email address and invalid password
    [Documentation]    INFUND-703
    [Tags]
    ${invalid_email_plain}    ${lower_case_password}

Email persists with invalid email address and empty password
    [Documentation]    INFUND-703
    [Tags]
    ${invalid_email_plain}    ${EMPTY}

*** Keywords ***
Email persists on invalid login
    [Arguments]    ${email_address}    ${password}
    Given The guest user inserts user email and password    ${email_address}    ${password}
    When the user tries to log in
    Then the email address should persist    ${email_address}

the email address should persist
    [Arguments]    ${email_address}
    # Note: we have to do it this way rather than the more straightforward eg Textfield Value Should Be
    # due to a bug in selenium2library
    ${stored_data}=    Get Value    id=username
    Should Be Equal    ${stored_data}    ${email_address}

the user tries to log in
    the user clicks the button/link    css=button[name="_eventId_proceed"]
    the user should see the text in the page    ${unsuccessful_login_message}
