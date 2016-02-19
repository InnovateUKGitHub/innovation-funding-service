*** Settings ***
Documentation     -INFUND-399: As a client, I would like to demo the system with real-like logins per user role
...
...
...               -INFUND-171: As a user, I am able to sign in providing a emailaddress and password, so I have access to my data
Suite Teardown    TestTeardown User closes the browser
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Log-out
    [Tags]    Guest    HappyPath
    [Setup]    Login as user    &{lead_applicant_credentials}
    Given the Applicant is logged-in
    When The Applicant clicks the log-out button
    # TODO DW - INFUND-936 - reinstate expectations
    # Then user should be redirected to the correct page    ${LOGIN_URL}
    
    Go to   ${LOGIN_URL}

Invalid Login
    [Tags]    Guest
    Given the user is not logged-in
    When the guest user enters the log in credentials    steve.smith@empire.com    testtest
    And the guest user clicks the log-in button
    Then the guest user should get an error message

Valid login as Applicant
    [Tags]    Guest    HappyPath
    Given the user is not logged-in
    When the guest user enters the log in credentials    steve.smith@empire.com    test
    And the guest user clicks the log-in button
    Then the Applicant is logged-in
    And user should be redirected to the correct page    ${applicant_dashboard_url}
    [Teardown]    Logout as user

Valid login as Collaborator
    [Tags]    Guest    HappyPath
    Given the user is not logged-in
    When the guest user enters the log in credentials    ${collaborator1_credentials["email"]}    ${collaborator1_credentials["password"]}
    And the guest user clicks the log-in button
    Then the Applicant is logged-in
    And user should be redirected to the correct page    ${applicant_dashboard_url}
    [Teardown]    Logout as user

Valid login as Assessor
    [Documentation]    INFUND-286
    [Tags]    Assessor    Guest    HappyPath
    Given the user is not logged-in
    When the guest user enters the log in credentials    ${assessor_credentials["email"]}    ${assessor_credentials["password"]}
    And the guest user clicks the log-in button
    Then the Applicant is logged-in
    And user should be redirected to the correct page    ${assessor_dashboard_url}
    And the user should be logged-in as an Assessor
    [Teardown]    Logout as user

*** Keywords ***
the user is not logged-in
    Element Should Not Be Visible    link=My dashboard
    Element Should Not Be Visible    link=Logout

the guest user enters the log in credentials
    [Arguments]    ${USER_NAME}    ${PASSWORD}
    Input Text    id=username    ${USER_NAME}
    Input Password    id=password    ${PASSWORD}

the guest user should get an error message
    # TODO DW - INFUND-936 - reinstate expected text
    # Page Should Contain    Your login was unsuccessful because of the following issue(s)
    # Page Should Contain    Your username/password combination doesn't seem to work
    Page Should Not Contain Element    link=Logout

the guest user clicks the log-in button
    Click Button    css=button[name="_eventId_proceed"]

The Applicant clicks the log-out button
    Click Element    link=Logout

the Applicant is logged-in
    Element Should Be Visible    link=Logout

the user should be logged-in as an Assessor
    Title Should Be    Innovation Funding Service - Assessor Dashboard
