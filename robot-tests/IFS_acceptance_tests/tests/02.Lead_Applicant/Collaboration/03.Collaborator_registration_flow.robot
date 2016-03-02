*** Settings ***
Documentation     INFUND-1005: As a collaborator I want to select my organisation type, so that I can create the correct account type for my organisation
...
...               INFUND-1779: As a collaborator registering my company as Business, I want to be able provide my organisation name and address details so I can successfully register for the competition
...
...               INFUND-1231: As a collaborator registering my company as Academic, I want to be able to enter full or partial details of the Academic organisation's name so I can select my Academic organisation from a list
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

*** Test Cases ***
Lead applicant details should show in the invite page
    [Documentation]    INFUND-1005
    Given the user navigates to the page    ${INVITE_LINK}
    When the user clicks the button/link    jQuery=.button:contains("Create")
    Then the user should see the text in the page    Lead organisation: Empire Ltd
    And the user should see the text in the page    Lead applicant: Steve Smith
    And the user should see the element    link=Technology Inspired
    And the user should see the text in the page    Worth Internet Systems

User can not continue if an organisation type is not selected
    [Documentation]    INFUND-1005
    ...
    ...    INFUND-1780
    [Tags]
    Given browser validations have been disabled
    When the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    may not be null

User is able to select only one type
    [Documentation]    INFUND-1005
    When user selects the radio button    organisationType    2
    And user selects the radio button    organisationType    1
    Then the radio button should have the new selection    1

The type of organisation navigates to the correct page according to the selection
    [Documentation]    INFUND-1780
    ...
    ...    INFUND-1231
    [Tags]    HappyPath
    When user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Find your organisation on Companies House
    When the user goes back to the previous page
    Given user selects the radio button    organisationType    2
    and the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Please select your type or research organisation
    When the user goes back to the previous page
    Given user selects the radio button    organisationType    3
    and the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Public Sector
    And the user should see the text in the page    Create your account
    When the user goes back to the previous page
    Given user selects the radio button    organisationType    4
    and the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Charity
    And the user should see the text in the page    Create your account
    And the user goes back to the previous page

Academic organisations search (empty, invalid & valid inputs)
    [Documentation]    INFUND-1231
    [Tags]    HappyPath
    and user selects the radio button    organisationType    2
    and the user clicks the button/link    jQuery=.button:contains("Continue")
    and user selects the radio button    organisationType    5
    and the user clicks the button/link    jQuery=.button:contains("Continue")
    and the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see an error    may not be empty
    When the user enters text to a text field    id=org-name    abcd
    and the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    Sorry we couldn't find any results.
    When the user enters text to a text field    id=org-name    !!
    and the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    Please enter valid characters
    When the user enters text to a text field    id=org-name    Liv
    and the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    University of Liverpool
    when the user clicks the button/link    link= University of Liverpool
    Then the user should see the text in the page    Enter address manually
    And the user should see the text in the page    Academic

Accept Invitation flow (Business organisation)
    [Documentation]    INFUND-1005
    ...    INFUND-1779
    [Tags]    HappyPath
    Given the user navigates to the page    ${INVITE_LINK}
    When the user clicks the button/link    jQuery=.button:contains("Create")
    And user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should be redirected to the correct page    ${SERVER}/organisation/create/find-organisation
    When the user enters text to a text field    id=org-name    Empire
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=EMPIRE LTD
    and the user enters text to a text field    css=#postcode-check    postcode
    And the user clicks the button/link    id=postcode-lookup
    And the user clicks the button/link    css=#select-address-block > button
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user fills the create account form
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}

User who accepted the invite should be able to log-in
    [Tags]
    Given the user navigates to the page    ${INVITE_LINK}
    When the guest user enters the login credentials    rogier@worth.systems    testtest
    And the user clicks the button/link    css=input.button
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    And the user should see the text in the page    A novel solution to an old problem
    [Teardown]    User closes the browser

The collaborator who accepted the invite should be visible in the assign list
    [Documentation]    INFUND-1779
    [Tags]    HappyPath
    Guest user log-in    steve.smith@empire.com    test
    And the user navigates to the page    ${PROJECT_SUMMARY_URL}
    When the user clicks the button/link    css=.assign-button
    Then the user should see the element    jQuery=button:contains("Rogier De Regt")

*** Keywords ***
user selects the radio button
    [Arguments]    ${RADIO_BUTTON}    ${ORG_TYPE}
    Select Radio Button    ${RADIO_BUTTON}    ${ORG_TYPE}

the user fills the create account form
    Input Text    id=firstName    Rogier
    Input Text    id=lastName    De Regt
    Input Text    id=phoneNumber    0612121212
    Input Password    id=password    testtest
    Input Password    id=retypedPassword    testtest
    Select Checkbox    termsAndConditions
    Submit Form

the radio button should have the new selection
    [Arguments]    ${ORG_TYPE}
    Radio Button Should Be Set To    organisationType    ${ORG_TYPE}
