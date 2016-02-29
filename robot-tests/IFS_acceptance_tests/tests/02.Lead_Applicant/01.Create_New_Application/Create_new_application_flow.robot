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
    [Tags]    Create application    HappyPath
    Given user navigates to the page    ${COMPETITION_DETAILS_URL}
    When user clicks the button/link    jQuery=.column-third .button:contains("Sign in to apply")
    And user clicks the button/link    jQuery=.button:contains("Create")
    and user enters text to a text field    id=org-name    Innovate
    And user clicks the button/link    id=org-search
    And user clicks the button/link    LINK=INNOVATE LTD
    and user enters text to a text field    css=#postcode-check    2234
    And user clicks the button/link    id=postcode-lookup
    And user clicks the button/link    css=#select-address-block > button
    And user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And user clicks the button/link    jQuery=.button:contains("Save")
    and the user enters the details and clicks the create account
    And user clicks the button/link    jQuery=.button:contains("Begin application")
    Then user should see the text in the page    Application overview
    And user should see the text in the page    Technology Inspired - Application number 0000

Create application flow for non registered users non CH route
    [Documentation]    INNFUND-669
    [Tags]    Create application    HappyPath       Pending
    # Pending due to INFUND-2019
    Given user navigates to the page    ${COMPETITION_DETAILS_URL}
    When user clicks the button/link    jQuery=.column-third .button:contains("Sign in to apply")
    And user clicks the button/link    jQuery=.button:contains("Create")
    and the user clicks the Not on company house link
    And user clicks the button/link    jQuery=.button:contains("Save")
    And the user enters the details for the non CH
    And user clicks the button/link    JQuery=.button:contains("Begin application")
    Then user should see the text in the page    Application overview
    And user should see the text in the page    Technology Inspired - Application number 0000

Verify the name of the new application
    [Documentation]    INFUND-669
    ...
    ...    INFUND-1163
    [Tags]    Applicant    New application    HappyPath
    When the guest user enters the log in credentials    robot@test.com    testtest
    And user clicks the button/link    css=input.button
    and the user edits the competition title
    Then user should see the text in the page    test title - Application number 0000
    And the progress indicator should show 0
    And user clicks the button/link    link=View team members and add collaborators
    and user should see the text in the page    Application team
    and user should see the text in the page    View and manage your partner companies
    And the new application should be visible in the dashboard page
    And user clicks the button/link    link=test title
    and user should see the text in the page    test title

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
    Input Text    id=email    robot@test.com
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
    Input Text    id=email    robot2@test.com
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
