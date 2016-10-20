*** Settings ***
Resource          ../../resources/defaultResources.robot

*** Variables ***
${correct_email}    steve.smith@empire.com
${incorrect_email}    steve.smith@idontexist.com
${invalid_email}    notavalidemailaddress
${correct_password}    Passw0rd
${incorrect_password}    wrongPassw0rd
${invalid_password}    allinlowercaseandnonumbers

*** Keywords ***
Email persists on invalid login
    [Arguments]    ${email_address}    ${password}
    Given the guest user inserts user email & password    ${email_address}    ${password}
    When the user tries to log in
    Then the user is not logged-in
    And the email address should persist    ${email_address}

the user is not logged-in
    The user should not see the element    link=My dashboard
    The user should not see the element    link=Logout

the email address should persist
    [Arguments]    ${email_address}
    # Note: we have to do it this way rather than the more straightforward eg Textfield Value Should Be
    # due to a bug in selenium2library
    ${stored_data}=    Get Value    id=username
    Should Be Equal    ${stored_data}    ${email_address}

the user tries to log in
    the user clicks the button/link    css=button[name="_eventId_proceed"]
    the user should see the text in the page    ${unsuccessful_login_message}
