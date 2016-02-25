*** Settings ***
Documentation     INFUND-1005: As a collaborator I want to select my organisation type, so that I can create the correct account type for my organisation
...
...               INFUND-1779: As a collaborator registering my company as Business, I want to be able provide my organisation name and address details so I can successfully register for the competition
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Create new application    collaboration
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${INVITE_LINK}    ${SERVER}/accept-invite/4e09372b85241cb03137ffbeb2110a1552daa1086b0bce0ff7d8ff5d2063c8ffc10e943acf4a3c7a
${SELECT_ORGANISATION}    ${SERVER}/accept-invite/new-account-organisation-type

*** Test Cases ***
Lead applicant details should show in the invite page
    [Documentation]    INFUND-1005
    Given user navigates to the page    ${INVITE_LINK}
    When user clicks the button/link    jQuery=.button:contains("Create")
    Then user should see the text in the page    Lead organisation: Empire Ltd
    And user should see the text in the page    Lead applicant: Steve Smith

User can not continue if an organisation type is not selected
    [Documentation]    INFUND-1005
    ...
    ...    INFUND-1780
    [Tags]    Pending
    #pending because there is no validation and the user gets an error page
    When user clicks the button/link    jQuery=.button:contains("Continue")
    Then user should see the text in the page    Please select your organisation type

User is able to select only one type
    [Documentation]    Infund-1005
    When user selects the radio button    2
    And user selects the radio button    1
    Then the radio button should have the new selection    1

User can go back and change the selection
    [Documentation]    INFUND-1780
    Given user clicks the button/link    jQuery=.button:contains("Continue")
    And user should be redirected to the correct page    ${SERVER}/accept-invite/create-organisation/?organisationType=1
    When user goes back to the previous page
    Then user should be redirected to the correct page    ${SELECT_ORGANISATION}

Accept Invitation flow (Business organisation)
    [Documentation]    INFUND-1005
    ...    INFUND-1779
    [Tags]    HappyPath
    Given user navigates to the page    ${INVITE_LINK}
    When user clicks the button/link    jQuery=.button:contains("Create")
    And user selects the radio button    1
    And user clicks the button/link    jQuery=.button:contains("Continue")
    Then user should be redirected to the correct page    ${SERVER}/accept-invite/create-organisation/?organisationType=1
    When user enters text to a text field    id=org-name    Empire
    And user clicks the button/link    id=org-search
    And user clicks the button/link    link=EMPIRE LTD
    and user enters text to a text field    css=#postcode-check    postcode
    And user clicks the button/link    id=postcode-lookup
    And user clicks the button/link    css=#select-address-block > button
    And user clicks the button/link    jQuery=.button:contains("Save organisation and")
    And the user fills the create account form
    Then user should be redirected to the correct page    ${DASHBOARD_URL}

User who accepted the invite should be able to log-in
    Given user navigates to the page    ${INVITE_LINK}
    When the guest user enters the login credentials    rogier@worth.systems    testtest
    And user clicks the button/link    css=input.button
    Then user should be redirected to the correct page    ${DASHBOARD_URL}
    And user should see the text in the page    A novel solution to an old problem
    [Teardown]    User closes the browser

The collaborator who accepted the invite should be visible in the assign list
    [Documentation]    INFUND-1779
    [Tags]    HappyPath
    Guest user log-in    steve.smith@empire.com    test
    And user navigates to the page    ${PROJECT_SUMMARY_URL}
    When user clicks the button/link    css=.assign-button
    Then user should see the element    jQuery=button:contains("Rogier De Regt")

*** Keywords ***
user selects the radio button
    [Arguments]    ${ORG_TYPE}
    Select Radio Button    organisationType    ${ORG_TYPE}

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

user goes back to the previous page
    Go Back
