*** Settings ***
Documentation     INFUND-1005: As a collaborator I want to select my organisation type, so that I can create the correct account type for my organisation
...
...               INFUND-1779: As a collaborator registering my company as Business, I want to be able provide my organisation name and address details so I can successfully register for the competition
...
...               INFUND-1231: As a collaborator registering my company as Academic, I want to be able to enter full or partial details of the Academic organisation's name so I can select my Academic organisation from a list
...
...
...               INFUND-1166: As a collaborator registering my company as Research organisation type, I need to inform Innovate UK what type of research organisation I am so that this can inform how I provide my finances
...
...               INFUND-917: As an academic partner i want to input my finances according to the JES field headings, so that i enter my figures into the correct sections
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Create new application    collaboration
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${INVITE_LINK}    ${SERVER}/accept-invite/4e09372b85241cb03137ffbeb2110a1552daa1086b0bce0ff7d8ff5d2063c8ffc10e943acf4a3c7a
${SELECT_ORGANISATION}    ${SERVER}/organisation/create/type/new-account-organisation-type
${INVITE_LINK_2}    ${SERVER}/accept-invite/1d92a6ace9030f2d992f47ea60529028fd49542dffd6b179f68fae072b4f1cc61f12a419b79a5267

*** Test Cases ***
Lead applicant details should show in the invite page
    [Documentation]    INFUND-1005
    Given the user navigates to the page    ${INVITE_LINK}
    When the user clicks the button/link    jQuery=.button:contains("Create")
    Then the user should see the text in the page    Lead organisation: Empire Ltd
    And the user should see the text in the page    Lead applicant: Steve Smith
    And the user should see the element    link=Technology Inspired
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
    Then the user should see the text in the page    may not be null
    Given user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Given browser validations have been disabled
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    may not be null

User is able to select only one type
    [Documentation]    INFUND-1005
    Given the user navigates to the page    ${SELECT_ORGANISATION}
    When user selects the radio button    organisationType    2
    And user selects the radio button    organisationType    1
    Then the radio button should have the new selection    1

The type of organisation navigates to the correct page
    [Documentation]    INFUND-1780
    ...
    ...    INFUND-1231
    [Tags]    HappyPath
    When user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Find your business on Companies House
    When the user goes back to the previous page
    Given user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Please select your type of research organisation
    When the user goes back to the previous page
    Given user selects the radio button    organisationType    3
    and the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Public Sector
    And the user should see the text in the page    Create your account
    When the user goes back to the previous page
    Given user selects the radio button    organisationType    4
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Charity
    And the user should see the text in the page    Create your account
    And the user goes back to the previous page

The type of the sub organisation navigates to the correct page
    [Documentation]    INFUND-1166
    Given user selects the radio button    organisationType    2
    and the user clicks the button/link    jQuery=.button:contains("Continue")
    When user selects the radio button    organisationType    5
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Find your academic organisation
    When the user goes back to the previous page
    When user selects the radio button    organisationType    6
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Non profit distributing Research & Technology Organisation (RTO)
    When the user should see the text in the page    Postcode
    When the user goes back to the previous page
    When user selects the radio button    organisationType    7
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Catapult
    When the user should see the text in the page    Postcode
    When the user goes back to the previous page
    When user selects the radio button    organisationType    8
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Public sector research establishment
    When the user should see the text in the page    Postcode
    When the user goes back to the previous page
    When user selects the radio button    organisationType    9
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Research council institute
    When the user should see the text in the page    Postcode
    When the user goes back to the previous page

Academic organisations search (empty, invalid & valid inputs)
    [Documentation]    INFUND-1231
    [Tags]    HappyPath
    When user selects the radio button    organisationType    5
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see an error    This field cannot be left blank
    When the user enters text to a text field    id=organisationSearchName    abcd
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    Sorry we couldn't find any results.
    When the user enters text to a text field    id=organisationSearchName    !!
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    Please enter valid characters
    When the user enters text to a text field    id=organisationSearchName    Liv
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    University of Liverpool
    When the user clicks the button/link    link= University of Liverpool
    Then the user should see the text in the page    Enter address manually
    And the user should see the text in the page    University (HEI)

Business organisation (accept invitation flow)
    [Documentation]    INFUND-1005
    ...    INFUND-2286
    ...    INFUND-1779
    ...    INFUND-2336
    [Tags]    HappyPath    FailingForLocal      Failing
    Given the user navigates to the page    ${INVITE_LINK}
    When the user clicks the button/link    jQuery=.button:contains("Create")
    And user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user enters text to a text field    id=organisationSearchName    Nomensa
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=Nomensa
    and the user enters text to a text field    id=addressForm.postcodeInput    2233
    And the user clicks the button/link    id=postcode-lookup
    And the user clicks the button/link    css=#select-address-block > button
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user fills the create account form    Rogier    De Regt
    And the user tries to go back, but can't modify their details
    And the user verifies the email
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    And the user clicks the button/link    jQuery=.button:contains("Log in")
    And guest user log-in    worth.email.test+invite1@gmail.com    Passw0rd
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    And the user can see the updated company name throughut the application

User who accepted the invite should be able to log-in
    [Tags]    FailingForLocal           Failing
    Given the user navigates to the page    ${INVITE_LINK}
    When the guest user enters the login credentials    worth.email.test+invite1@gmail.com    Passw0rd
    And the user clicks the button/link    css=button[name="_eventId_proceed"]
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    And the user should see the text in the page    A novel solution to an old problem
    [Teardown]    User closes the browser


User who accepted the invite can invite others to their own organisation
    [Documentation]     INFUND-2335
    [Tags]      FailingForLocal         Pending
    # pending as not yet finished
    Given guest user log-in             worth.email.test+invite1@gmail.com      Passw0rd
    When the user navigates to the page     ${MANAGE_CONTRIBUTORS_URL}
    Then the user should see the element        foobar


User who accepted the invite cannot invite others to other organisations
    [Documentation]     INFUND-2335
    [Tags]      FailingForLocal     Pending
    # pending as not yet finished
    Given guest user log-in             worth.email.test+invite1@gmail.com      Passw0rd
    When the user navigates to the page     ${MANAGE_CONTRIBUTORS_URL}
    Then the user should not see the element        foobar

The collaborator who accepted the invite should be visible in the assign list
    [Documentation]    INFUND-1779
    [Tags]    HappyPath    FailingForLocal      Failing
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${PROJECT_SUMMARY_URL}
    And the user clicks the button/link    css=.assign-button
    Then the user should see the element    jQuery=button:contains("Rogier De Regt")
    [Teardown]    TestTeardown User closes the browser


Academic organisation (accept invitation flow)
    [Documentation]    INFUND-1166
    ...
    ...    INFUND-917
    [Tags]    HappyPath    FailingForLocal      Failing
    [Setup]    The guest user opens the browser
    Given the user navigates to the page    ${INVITE_LINK_2}
    When the user clicks the button/link    jQuery=.button:contains("Create")
    And user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    When user selects the radio button    organisationType    5
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see an error    This field cannot be left blank
    When the user enters text to a text field    id=organisationSearchName    Liverpool
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    University of Liverpool
    When the user clicks the button/link    link= University of Liverpool
    and the user enters text to a text field    id=addressForm.postcodeInput    postcode
    And the user clicks the button/link    id=postcode-lookup
    And the user clicks the button/link    css=#select-address-block > button
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user fills the create account form    Steven    Gerrard
    And the user verifies the email
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    And the user clicks the button/link    jQuery=.button:contains("Log in")
    And guest user log-in    worth.email.test+invite2@gmail.com    testtest
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=A novel solution to an old problem
    and the user clicks the button/link    link=Your finances
    Then the user should see the text in the page    TSB reference
    and the user should not see the text in the page    Labour

*** Keywords ***
user selects the radio button
    [Arguments]    ${RADIO_BUTTON}    ${ORG_TYPE}
    Select Radio Button    ${RADIO_BUTTON}    ${ORG_TYPE}

the user fills the create account form
    [Arguments]    ${NAME}    ${LAST_NAME}
    Input Text    id=firstName    ${NAME}
    Input Text    id=lastName    ${LAST_NAME}
    Input Text    id=phoneNumber    0612121212
    Input Password    id=password    Passw0rd2
    Input Password    id=retypedPassword    Passw0rd2
    Select Checkbox    termsAndConditions
    Submit Form

the radio button should have the new selection
    [Arguments]    ${ORG_TYPE}
    Radio Button Should Be Set To    organisationType    ${ORG_TYPE}

the user verifies the email
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    ${LATEST} =    wait for email    fromEmail=noresponse@innovateuk.gov.uk
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${LATEST}
    log    ${LINK}
    ${VERIFY_EMAIL}=    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox


the user tries to go back, but can't modify their details
    Go Back
    the user should not see the text in the page        Create your account
    the user should see the text in the page            Sign in

the user can see the updated company name throughut the application
    the user navigates to the page    ${FINANCES_OVERVIEW_URL}
    the user should see the text in the page        Nomensa
    the user navigates to the page      ${APPLICATION_TEAM_URL}
    the user should see the text in the page        Nomensa
    the user navigates to the page    ${MANAGE_CONTRIBUTORS_URL}
    the user should see the text in the page        Nomensa