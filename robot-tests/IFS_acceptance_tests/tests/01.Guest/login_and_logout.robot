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
Resource          ../../resources/keywords/User_actions.robot

*** Test Cases ***
Log-out
    [Tags]    Guest    HappyPath
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    Given the Applicant is logged-in
    When the user clicks the button/link    link=Logout
    Then the user should be redirected to the correct page    ${LOGIN_URL}

Invalid Login
    [Tags]    Guest
    Given the user is not logged-in
    When the guest user enters the log in credentials    steve.smith@empire.com    testtest
    And the user clicks the button/link    css=input.button
    Then the guest user should get an error message

Valid login as Applicant
    [Tags]    Guest    HappyPath
    Given the user is not logged-in
    When the guest user enters the log in credentials    steve.smith@empire.com    test
    And the user clicks the button/link    css=input.button
    Then the Applicant is logged-in
    And the user should be redirected to the correct page    ${applicant_dashboard_url}
    [Teardown]    Logout as user

Valid login as Collaborator
    [Tags]    Guest    HappyPath
    Given the user is not logged-in
    When the guest user enters the log in credentials    ${collaborator1_credentials["email"]}    ${collaborator1_credentials["password"]}
    And the user clicks the button/link    css=input.button
    Then the Applicant is logged-in
    And the user should be redirected to the correct page    ${applicant_dashboard_url}
    [Teardown]    Logout as user

Valid login as Assessor
    [Documentation]    INFUND-286
    [Tags]    Assessor    Guest    HappyPath
    Given the user is not logged-in
    When the guest user enters the log in credentials    ${assessor_credentials["email"]}    ${assessor_credentials["password"]}
    And the user clicks the button/link    css=input.button
    Then the Applicant is logged-in
    And the user should be redirected to the correct page    ${assessor_dashboard_url}
    And the user should be logged-in as an Assessor
    [Teardown]    Logout as user

*** Keywords ***
the user is not logged-in
    Element Should Not Be Visible    link=My dashboard
    Element Should Not Be Visible    link=Logout

the guest user should get an error message
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work
    Page Should Not Contain Element    link=Logout

the Applicant is logged-in
    Element Should Be Visible    link=Logout

the user should be logged-in as an Assessor
    Title Should Be    Innovation Funding Service - Assessor Dashboard
