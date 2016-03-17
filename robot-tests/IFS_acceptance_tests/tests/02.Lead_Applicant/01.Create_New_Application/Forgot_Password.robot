*** Settings ***
Documentation     INFUND-1889: As a registered user of IFS I want to understand what I need to do to reset my log-in password so that I can be permitted access to IFS if I have forgotten this information
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Create new application
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***


*** Test Cases ***
Verify password reset request is sent to the user
    [Tags]    Pending
    # NOTE: INFUND-1942 - Nico will add hash for reset password in the database and will give the url, also need to create separate user for this action
    Given the user is not logged-in
    When the guest user enters the log in credentials    steve.smith@empire.com    Passw0rd2
    And the user clicks the button/link
    Then the guest user should get an error message
    And the user click on link Forgot your password
    And the user enters email address

Verify password reset from email
    [Tags]    Pending
    # NOTE: INFUND-1942 - Nico will add hash for reset password in the database and will give the url, also need to create separate user for this action


*** Keywords ***
the user is not logged-in
    Element Should Not Be Visible    link=My dashboard
    Element Should Not Be Visible    link=Logout

the guest user clicks the log-in button
    Click Button    css=button[name="_eventId_proceed"]

the guest user should get an error message
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work
    Page Should Not Contain Element    link=Logout

the user click on link Forgot your password
    Click Link    link=Forgot your password?
    Page Should Contain    Forgotten password

the user enters email address
    Input Text    id=id_email    steve.smith@empire.com
    Click Element    css=.button
    Sleep    1s
    Page Should Contain    If your email address is recognised, you’ll receive an email with instructions about how to reset your password.