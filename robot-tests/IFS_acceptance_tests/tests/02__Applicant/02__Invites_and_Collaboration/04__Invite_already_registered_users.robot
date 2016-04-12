*** Settings ***
Documentation     INFUND-1458 \ As a existing user with an invitation to collaborate on an application and I am already registered with IFS I want to be able to use my existing credentials and confirm my details so that I don't have to follow the registration process again.
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** variables ***
${INVITE_COLLABORATORS_PAGE_APPL1}    ${SERVER}application/1/contributors/invite

*** Test Cases ***
The invited user should see the organisation details
    [Documentation]    INFUND-1458
    [Tags]
    [Setup]    The guest user opens the browser
    Given the we create a new user
    Given the lead applicant invites a registered user
    And the guest user opens the browser
    When The user clicks the link from the appropriate email sender
    #Then the user should see the text in the page
    #And the user should see the text in the page

email link should be visible
    [Documentation]    INFUND-1458

Contact innovate option should be visible
    [Documentation]    INFUND-1458

The continue button should redirect to the overview page
    [Documentation]    INFUND-1458

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
    user closes the browser

the we create a new user
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
