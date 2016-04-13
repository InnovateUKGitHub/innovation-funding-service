*** Settings ***
Documentation     INFUND-1458 As a existing user with an invitation to collaborate on an application and I am already registered with IFS I want to be able to use my existing credentials and confirm my details so that I don't have to follow the registration process again.
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags
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
    Given we create a new user
    Given the lead applicant invites a registered user
    When the user opens the mailbox and accepts the invitation to collaborate
    Then the user should see the text in the page    We found a user account with the invited email address

The user clicks the login link
    [Documentation]    INFUND-1458
    When the user clicks the button/link    link=Click here to login
    And the guest user inserts user email & password    worth.email.test+reg2@gmail.com    Passw0rd2
    And the guest user clicks the log-in button
    Then the user should see the text in the page    Confirm your organisation

The user should see the correct content in the confirm page
    [Documentation]    INFUND-1458
    Then the user should see the text in the page    INNOVATE LTD
    And the user should see the text in the page    BH14 0HU
    And the user should see the element    link=email the application lead

The continue button should redirect to the overview page
    [Documentation]    INFUND-1458
    When the user clicks the button/link    jQuery=.button:contains("Continue to application")
    Then the user should see the text in the page    Application overview

*** Keywords ***
the lead applicant invites a registered user
    The guest user opens the browser
    The user navigates to the page    ${COMPETITION_DETAILS_URL}
    The user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    The user clicks the button/link    jQuery=.button:contains("Sign in to apply")
    The user clicks the button/link    jQuery=.button:contains("Create")
    The user enters text to a text field    id=organisationSearchName    Innovate
    The user clicks the button/link    id=org-search
    The user clicks the button/link    LINK=INNOVATE LTD
    select Checkbox    id=address-same
    The user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    The user clicks the button/link    jQuery=.button:contains("Save")
    The user enters the details and clicks the create account    worth.email.test+invite2@gmail.com
    The user should be redirected to the correct page    ${REGISTRATION_SUCCESS}
    The user clicks the link from the appropriate email sender
    The user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    The user clicks the button/link    jQuery=.button:contains("Log in")
    The guest user inserts user email & password    worth.email.test+invite2@gmail.com    Passw0rd2
    The guest user clicks the log-in button
    The user clicks the button/link    link=Technology Inspired
    Click Element    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    Input Text    name=organisations[1].organisationName    innovate
    Input Text    name=organisations[1].invites[0].personName    Registered user
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    worth.email.test+reg2@gmail.com
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user should see the text in the page    Application overview
    Wait Until Element Is Visible    link=Logout
    sleep    4s
    Click Element    link=Logout

we create a new user
    The user navigates to the page    ${COMPETITION_DETAILS_URL}
    The user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    The user clicks the button/link    jQuery=.button:contains("Sign in to apply")
    The user clicks the button/link    jQuery=.button:contains("Create")
    The user enters text to a text field    id=organisationSearchName    Innovate
    The user clicks the button/link    id=org-search
    The user clicks the button/link    LINK=INNOVATE LTD
    select Checkbox    id=address-same
    The user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    The user clicks the button/link    jQuery=.button:contains("Save")
    The user enters the details and clicks the create account    worth.email.test+reg2@gmail.com
    The user should be redirected to the correct page    ${REGISTRATION_SUCCESS}
    The user clicks the link from the appropriate email sender
    The user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    The user clicks the button/link    jQuery=.button:contains("Log in")
    The guest user inserts user email & password    worth.email.test+reg2@gmail.com    Passw0rd2
    The guest user clicks the log-in button
    user closes the browser
