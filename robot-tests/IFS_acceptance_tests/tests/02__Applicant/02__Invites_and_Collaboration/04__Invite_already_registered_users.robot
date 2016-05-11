*** Settings ***
Documentation     INFUND-1458 As a existing user with an invitation to collaborate on an application and I am already registered with IFS I want to be able to use my existing credentials and confirm my details so that I don't have to follow the registration process again.
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Invite    Email
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** variables ***
${INVITE_COLLABORATORS_PAGE_APPL1}    ${SERVER}application/1/contributors/invite

*** Test Cases ***
The invited registered user should redirect to the correct page
    [Documentation]    INFUND-1458
    [Tags]
    [Setup]    Delete the emails from the test mailbox
    Given we create a new user    worth.email.test+invitedregistered@gmail.com
    Given the lead applicant invites a registered user    worth.email.test+invite2@gmail.com    worth.email.test+invitedregistered@gmail.com
    When the user opens the mailbox and accepts the invitation to collaborate
    Then the user should see the text in the page    We found a user account with the invited email address

The user clicks the login link
    [Documentation]    INFUND-1458
    [Tags]
    When the user clicks the button/link    link=Click here to login
    And the guest user inserts user email & password    worth.email.test+invitedregistered@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    Then the user should see the text in the page    Confirm your organisation

The user should see the correct content in the confirm page
    [Documentation]    INFUND-1458
    [Tags]
    Then the user should see the text in the page    INNOVATE LTD
    And the user should see the text in the page    BH12 4NZ
    And the user should see the element    link=email the application lead

The continue button should redirect to the overview page
    [Documentation]    INFUND-1458
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Continue to application")
    Then the user should see the text in the page    Application overview

*** Keywords ***
