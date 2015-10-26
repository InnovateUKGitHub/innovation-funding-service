*** Settings ***
Documentation     -INFUND-399: As a client, I would like to demo the system with real-like logins per user role
...
...
...               -INFUND-171: As a user, I am able to sign in providing a emailaddress and password, so I have access to my data
...
Suite Setup
Suite Teardown    User closes the browser
Resource          ../GLOBAL_VARIABLES.robot
Resource          ../GLOBAL_LIBRARIES.robot
Resource          ../Login_actions.robot
Resource          ../USER_CREDENTIALS.robot
Resource          ../Applicant_actions.robot

*** Test Cases ***
Log-out
    [Tags]    Applicant
    [Setup]    Login as user    &{lead_applicant_credentials}
    Given the Applicant is logged-in
    When The Applicant clicks the log-out button
    Then the applicant should be logged-out

Invalid Login
    [Tags]    Applicant
    Given the user is not logged-in
    When the guest user inserts correct username
    And the guest user inserts wrong password
    And the guest user clicks the log-in button
    Then the guest user should get an error message

Valid login as Applicant
    [Tags]    Applicant
    Given the user is not logged-in
    When the guest user inserts applicant user name    ${lead_applicant_credentials["email"]}
    And the user inserts password    ${lead_applicant_credentials["password"]}
    And the guest user clicks the log-in button
    Then the user should be logged in
    And the user is redirected to the Applicant dashboard page
    [Teardown]    Logout as user

Valid login as Collaborator
    Given the user is not logged-in
    When the guest user inserts applicant user name    ${collaborator1_credentials["email"]}
    And the user inserts password    ${collaborator1_credentials["password"]}
    And the guest user clicks the log-in button
    Then the user should be logged in
    And the user is redirected to the Applicant dashboard page
    [Teardown]    Logout as user

Valid login as Assessor
    [Documentation]    INFUND-286
    [Tags]    Assessor
    Given the user is not logged-in
    When the guest user inserts applicant user name    ${assessor_credentials["email"]}
    And the user inserts password    ${assessor_credentials["password"]}
    And the guest user clicks the log-in button
    Then the user should be logged in
    And the user is redirected to the Assessor dashboard page
    And the user should be logged-in as an Assessor
    [Teardown]    Logout as user

*** Keywords ***
the user is not logged-in
    Element Should Not Be Visible    link=My dashboard
    Element Should Not Be Visible    link=Logout

the guest user inserts correct username
    Input Text    id=id_email    steve.smith@empire.com

the guest user inserts wrong password
    Input Password    id=id_password    testtest

the guest user should get an error message
    Element Should Be Visible    id=error-summary-heading-example-1
    Page Should Not Contain Element    link=Logout

the guest user inserts applicant user name
    [Arguments]    ${email}
    Input Text    id=id_email    ${email}

the user inserts password
    [Arguments]    ${password}
    Input Password    id=id_password    ${password}

the guest user clicks the log-in button
    Click Button    css=input.button

the user should be logged in
    Element Should Be Visible    link=Logout

The Applicant clicks the log-out button
    Click Element    link=Logout

the applicant should be logged-out
    Location Should Be    ${LOGIN_URL}

the Applicant is logged-in
    Element Should Be Visible    link=Logout

the user is redirected to the Applicant dashboard page
    Location Should Be    ${applicant_dashboard_url}

the user should be logged-in as an Assessor
    Title Should Be    Innovation Funding Service - Assessor Dashboard

the user is redirected to the Assessor dashboard page
    Location Should Be    ${assessor_dashboard_url}
