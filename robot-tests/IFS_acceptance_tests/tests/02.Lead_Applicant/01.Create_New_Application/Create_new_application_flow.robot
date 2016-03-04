*** Settings ***
Documentation     INNFUND-669 As an applicant I want to create a new application so that I can submit an entry into a relevant competition
...
...
...               INFUND-1163 As an applicant I want to create a new application so that I can submit an entry into a relevant competition
Test Setup        The guest user opens the browser
Test Teardown     User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${APPLICATION_DETAILS_APPLICATION8}    ${SERVER}/application/8/form/question/9

*** Test Cases ***
Create application flow for non registered users CH route
    [Documentation]    INNFUND-669
    [Tags]    Create application    HappyPath   FailingForDev
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Sign in to apply")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=org-name    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    LINK=INNOVATE LTD
    And the user enters text to a text field    css=#postcode-check    2234
    And the user clicks the button/link    id=postcode-lookup
    And the user clicks the button/link    css=#select-address-block > button
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user enters the details and clicks the create account
    And the user verifies their email       ${verify_link_2}
    # And the user logs back in
    # And the user clicks the button/link    jQuery=.button:contains("Begin application")
    # Then the user should see the text in the page    Application overview
    # And the user should see the text in the page    Technology Inspired


Create application flow for non registered users non CH route
    [Documentation]    INNFUND-669
    [Tags]    Create application    HappyPath
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Sign in to apply")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user clicks the Not on company house link
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user enters the details for the non CH
    # And the user clicks the button/link    JQuery=.button:contains("Begin application")
    # Then the user should see the text in the page    Application overview
    # And the user should see the text in the page    Technology Inspired


Verify the name of the new application
    [Documentation]    INFUND-669
    ...
    ...    INFUND-1163
    [Tags]    Applicant    New application    HappyPath     FailingForDev
    When the guest user enters the log in credentials    ewan+2@hiveit.co.uk    testtest
    And the user clicks the button/link    css=input.button
    And the user edits the competition title
    Then the user should see the text in the page    test title
    And the progress indicator should show 0
    And the user clicks the button/link    link=View team members and add collaborators
    And the user should see the text in the page    Application team
    And the user should see the text in the page    View and manage your contributors and partners
    And the new application should be visible in the dashboard page
    And the user clicks the button/link    link=test title
    And the user should see the text in the page    test title


Verify that the options will load properly with spaces in the name
    [Documentation]     INFUND-1757
    [Tags]  Create application
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Sign in to apply")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=org-name    Hive IT
    And the user clicks the button/link    id=org-search
    Then The user should see the text in the page       Hive IT


*** Keywords ***
the new application should be visible in the dashboard page
    Click Link    link= My dashboard
    sleep    1s
    Wait Until Page Contains    test title
    Page Should Contain    Application number: 0000

the user enters the details and clicks the create account
    Input Text    id=firstName    John
    Input Text    id=lastName    Smith
    Input Text    id=phoneNumber    23232323
    Input Text    id=email    ewan+2@hiveit.co.uk
    Input Password    id=password    testtest
    Input Password    id=retypedPassword    testtest
    Select Checkbox    termsAndConditions
    Submit Form

the user clicks the Not on company house link
    Click Element    name=not-in-company-house
    Click Element    name=manual-address
    Input Text    id=street    street
    Input Text    id=street-2    street
    Input Text    id=street-3    street3
    Input Text    id=town    town
    Input Text    id=county    country
    Input Text    id=postcode    post code
    #Input Text    id=org-name    org1
    Input Text    name=organisationName    org2
    Input Text    id=postcode-check    2323
    Click Element    jQuery=.button:contains("Continue")

the user enters the details for the non CH
    Input Text    id=firstName    tester
    Input Text    id=lastName    tester
    Input Text    id=phoneNumber    23232323
    Input Text    id=email    ewan+3@hiveit.co.uk
    Input Password    id=password    testtest
    Input Password    id=retypedPassword    testtest
    Select Checkbox    termsAndConditions
    Submit Form

the user edits the competition title
    click link    Technology Inspired
    sleep    2s
    click link    Application details
    Input Text    id=application_details-title    test title
    Click Element    jQuery=button:contains("Save and return")

the progress indicator should show 0
    Element Should Contain    css=.progress-indicator    0

the user logs back in
    guest user log-in   ewan+2@hiveit.co.uk     testtest