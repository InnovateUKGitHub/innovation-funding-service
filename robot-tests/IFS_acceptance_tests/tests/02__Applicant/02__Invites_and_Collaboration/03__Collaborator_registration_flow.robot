*** Settings ***
Documentation     INFUND-1231: As a collaborator registering my company as Academic, I want to be able to enter full or partial details of the Academic organisation's name so I can select my Academic organisation from a list    #Invite flow without email. This test is using the old application
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${INVITE_LINK}    ${SERVER}/accept-invite/4e09372b85241cb03137ffbeb2110a1552daa1086b0bce0ff7d8ff5d2063c8ffc10e943acf4a3c7a
${SELECT_ORGANISATION}    ${SERVER}/organisation/create/type/new-account-organisation-type

*** Test Cases ***

Start by deleting emails from the test mailboxes
    [Tags]    Email
    delete the emails from both default remote test mailboxes



Lead applicant details should show in the invite page
    [Documentation]    INFUND-1005
    Given the user navigates to the page    ${INVITE_LINK}
    When the user clicks the button/link    jQuery=.button:contains("Create")
    Then the user should see the text in the page    Lead organisation: Empire Ltd
    And the user should see the text in the page    Lead applicant: Steve Smith
    And the user should see the element    link=${OPEN_COMPETITION_LINK}
    And the user should see the text in the page    Worth Internet Systems

User cannot continue if an organisation type is not selected
    [Documentation]    INFUND-1005
    ...
    ...    INFUND-1780
    ...
    ...    INFUND-1166
    [Tags]
    Given browser validations have been disabled
    When the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Please select an organisation type
    Given the user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Given browser validations have been disabled
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Please select an organisation type

User is able to select only one type
    [Documentation]    INFUND-1005
    Given the user navigates to the page    ${SELECT_ORGANISATION}
    When the user selects the radio button    organisationType    2
    And the user selects the radio button    organisationType    1
    Then the radio button should have the new selection    1

The type of organisation navigates to the correct page
    [Documentation]    INFUND-1780
    ...
    ...    INFUND-1231
    [Tags]
    When the user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Find your business on Companies House
    When the user goes back to the previous page
    Given the user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Please select your type of research organisation
    When the user goes back to the previous page
    Given the user selects the radio button    organisationType    3
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Public Sector
    And the user should see the text in the page    Create your account
    When the user goes back to the previous page
    Given the user selects the radio button    organisationType    4
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Charity
    And the user should see the text in the page    Create your account
    And the user goes back to the previous page

The type of the sub organisation navigates to the correct page
    [Documentation]    INFUND-1166
    Given the user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    When the user selects the radio button    organisationType    5
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Find your academic organisation
    When the user goes back to the previous page
    When the user selects the radio button    organisationType    6
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Non profit distributing Research & Technology Organisation (RTO)
    When the user should see the text in the page    Postcode
    When the user goes back to the previous page
    When the user selects the radio button    organisationType    7
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Catapult
    When the user should see the text in the page    Postcode
    When the user goes back to the previous page
    When the user selects the radio button    organisationType    8
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Public sector research establishment
    When the user should see the text in the page    Postcode
    When the user goes back to the previous page
    When the user selects the radio button    organisationType    9
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Research council institute
    When the user should see the text in the page    Postcode
    When the user goes back to the previous page

Catapult search (empty, invalid & valid inputs)
    [Documentation]    INFUND-1230
    [Tags]    HappyPath
    Given the user navigates to the page    ${INVITE_LINK}
    When the user clicks the button/link    jQuery=.button:contains("Create")
    And the user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Please select your type of research organisation
    And the user selects the radio button    organisationType    7
    When the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user should see the text in the page    Catapult
    When the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    An organisation name is required
    When the user enters text to a text field    name=organisationName    Digital Catapult
    When the user clicks the button/link    jQuery=.button:contains("Find UK address")
    And the user should see the text in the page    Please enter a UK postcode
    When the user enters text to a text field    name=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    And the user clicks the button/link    jQuery=.button:contains("Use selected address")
    Then the address fields should be filled

Catapult search (accept invitation flow)
    [Documentation]    INFUND-1230
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Digital Catapult
    And the user should see the text in the page    Operating Address
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user fills the create account form    Thierry    Henry

Catapult search (accept invitation flow with email step)
    [Documentation]    INFUND-1230
    [Tags]    Email    HappyPath
    Given the user reads his email from the default mailbox and clicks the link    worth.email.test+invite1@gmail.com    Please verify your email address    If you did not request an account with us
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And guest user log-in    worth.email.test+invite1@gmail.com    Passw0rd123
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=A novel solution to an old problem
    And the user clicks the button/link    link=Your finances
    And the user should see the text in the page    Digital Catapult

*** Keywords ***
the user selects the radio button
    [Arguments]    ${RADIO_BUTTON}    ${ORG_TYPE}
    Select Radio Button    ${RADIO_BUTTON}    ${ORG_TYPE}

the radio button should have the new selection
    [Arguments]    ${ORG_TYPE}
    Radio Button Should Be Set To    organisationType    ${ORG_TYPE}
