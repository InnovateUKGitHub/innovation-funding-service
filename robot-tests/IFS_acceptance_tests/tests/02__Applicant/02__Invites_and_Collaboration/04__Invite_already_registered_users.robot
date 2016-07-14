*** Settings ***
Documentation     INFUND-1458 As a existing user with an invitation to collaborate on an application and I am already registered with IFS I want to be able to use my existing credentials and confirm my details so that I don't have to follow the registration process again.
...
...
...               INFUND-2716: Error in where the name of an invited partner doesn't update in 'View team members and add collaborators'.
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Invite    Email
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***

*** Test Cases ***
The invited user should not follow the registration flow again
    [Documentation]    INFUND-1458
    [Tags]
    [Setup]    Delete the emails from both test mailboxes
    Given we create a new user    ${test_mailbox_one}+invitedregistered@gmail.com
    Given the lead applicant invites a registered user    ${test_mailbox_one}+invite2@gmail.com    ${test_mailbox_one}+invitedregistered@gmail.com
    When the user opens the mailbox and accepts the invitation to collaborate
    Then the user should see the text in the page    We've found an existing user account with the invited email address

The user clicks the login link
    [Documentation]    INFUND-1458
    [Tags]
    When the user clicks the button/link    link=Click here to sign in
    And the guest user inserts user email & password    ${test_mailbox_one}+invitedregistered@gmail.com    Passw0rd123
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

When this user edits the name this should be changed in the View team page
    [Documentation]    INFUND-2716: Error in where the name of an invited partner doesn't update in 'View team members and add collaborators'.
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=View and edit your profile details
    And the user clicks the button/link    link=Edit your details
    And the user enters profile details
    Then the user should see the change in the view team members page

*** Keywords ***
the user enters profile details
    Wait Until Element Is Visible    id=title
    Input Text    id=firstName    Dennis
    Input Text    id=lastName    Bergkamp
    Click Element    css=[name="create-account"]

the user should see the change in the view team members page
    click element    link=My dashboard
    click element    xpath=//*[@id="content"]/div[2]/section[1]/ul/li[2]/div/div[1]/h3/a
    click element    link=View team members and add collaborators
    Page Should Contain Element    link= Dennis Bergkamp
    Capture Page Screenshot
