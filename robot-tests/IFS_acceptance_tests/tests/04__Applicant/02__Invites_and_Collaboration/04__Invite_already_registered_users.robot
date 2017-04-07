*** Settings ***
Documentation     INFUND-1458 As a existing user with an invitation to collaborate on an application and I am already registered with IFS I want to be able to use my existing credentials and confirm my details so that I don't have to follow the registration process again.
...
...               INFUND-2716: Error in where the name of an invited partner doesn't update in 'view team members and add collaborators'
...
...               INFUND-3759: Existing Applicant should be able to accept invitations for other applications in the same organisation
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Email    Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***

*** Test Cases ***
The invited user should not follow the registration flow again
    [Documentation]    INFUND-1458
    [Tags]    HappyPath
    [Setup]    Delete the emails from both test mailboxes
    Given we create a new user    ${test_mailbox_one}+invitedregistered@gmail.com
    Given the lead applicant invites a registered user    ${test_mailbox_one}+invite2@gmail.com    ${test_mailbox_one}+invitedregistered@gmail.com
    When the user reads his email and clicks the link    ${test_mailbox_one}+invitedregistered@gmail.com    Invitation to collaborate in ${OPEN_COMPETITION_NAME}    You will be joining as part of the organisation    3
    Then the user should see the text in the page    We have found an account with the invited email address

The user clicks the login link
    [Documentation]    INFUND-1458
    [Tags]    HappyPath
    When the user clicks the button/link    link=Continue or sign in
    And the guest user inserts user email & password    ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    And the guest user clicks the log-in button
    Then the user should see the text in the page    Confirm your organisation

The user should see the correct content in the confirm page
    [Documentation]    INFUND-1458
    [Tags]    HappyPath
    Then the user should see the text in the page    INNOVATE LTD
    And the user should see the text in the page    BH2 5QY
    And the user should see the element    link=email the lead applicant

The continue button should redirect to the overview page
    [Documentation]    INFUND-1458
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Confirm and accept invitation")
    Then the user should see the text in the page    Application overview

The user edits the name this should be changed in the View team page
    [Documentation]    INFUND-2716: Error in where the name of an invited partner doesn't update in 'view team members and add collaborators'.
    [Tags]    HappyPath
    Given the user navigates to the page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=view and edit your profile details
    And the user clicks the button/link    link=Edit your details
    And the user enters profile details
    Then the user should see the change in the view team members page
    [Teardown]    TestTeardown User closes the browser

Invite a user with the same organisation under the same organisation
    [Documentation]    INFUND-3759
    [Setup]    Guest user log-in    ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    When Existing user creates a new application and invites a user from the same organisation
    Then the invited user should get a message to contact the helpdesk    ${test_mailbox_one}+invite2@gmail.com    Invitation to collaborate in ${OPEN_COMPETITION_NAME}    You will be joining as part of the organisation

*** Keywords ***
the user enters profile details
    The user enters text to a text field    id=firstName    Dennis
    The user enters text to a text field    id=lastName    Bergkamp
    focus    css=[name="create-account"]
    The user clicks the button/link    css=[name="create-account"]

the user should see the change in the view team members page
    The user clicks the button/link    link=My dashboard
    The user clicks the button/link    css=#content section:nth-of-type(1) li:nth-child(2) h3 a
    The user clicks the button/link    link=view team members and add collaborators
    The user should see the element    jQuery=.table-overflow:eq(1) td:nth-child(1):contains("Dennis Bergkamp")

Existing user creates a new application and invites a user from the same organisation
    the user navigates to the page    ${COMPETITION_DETAILS_URL}
    the user clicks the button/link    jQuery=.button:contains("Apply now")
    the user clicks the button/link    jQuery=.button:contains("Apply now")
    the user clicks the button/link    jQuery=Label:contains("Yes, I want to create a new application.")
    the user clicks the button/link    jQuery=.button:contains("Continue")
    the user clicks the button/link    jQuery=a:contains("Update INNOVATE LTD")
    The user clicks the button/link    jQuery=button:contains("Add new applicant")
    The user enters text to a text field    name=applicants[0].name    Olivier Giroud
    The user enters text to a text field    name=applicants[0].email    ${test_mailbox_one}+invite2@gmail.com
    #the user clicks the button/link    jQuery=a:contains("Update organisation")
    the user clicks the button/link    jQuery=button:contains("Update organisation")
    the user clicks the button/link    jQuery=a:contains("Begin application")
    the user clicks the button/link    link=Application details
    the user enters text to a text field    id=application_details-title    Invite a user with the same org@
    the user clicks the button/link    jQuery=button:contains("Save and return")
    the user closes the browser

The invited user should get a message to contact the helpdesk
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    And the guest user opens the browser
    When the user reads his email and clicks the link    ${recipient}    ${subject}    ${pattern}   3
    When the user clicks the button/link    link=Continue or sign in
    And the guest user inserts user email & password    ${recipient}  ${correct_password}
    And the guest user clicks the log-in button
    Then the user should see the text in the page    Sorry, you are unable to accept this invitation
    And the user should see the text in the page    If you want to remain in the same organisation but join a different application, please contact the helpdesk on 0300 321 4357
