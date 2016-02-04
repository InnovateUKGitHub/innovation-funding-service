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
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${APPLICATION_DETAILS_APPLICATION8}    ${SERVER}/application/8/form/question/9

*** Test Cases ***
Create application flow for non registered users CH route
    [Documentation]    INNFUND-669
    [Tags]    Create application
    Given the user goes to the create application page
    When the user clicks the sign in to apply button
    and the user clicks the create button
    and the user enters an organisation and clicks search
    and the applicant clicks the INNOVATE LTD
    And the user enters the post code and clicks Find UK address
    and the user saves the organisation
    and the user enters the details and clicks the create account
    and the user clicks the begin application
    Then the user should be in the application overview page

Create application flow for non registered users non CH route
    [Documentation]    INNFUND-669
    [Tags]    Create application
    Given the user goes to the create application page
    When the user clicks the sign in to apply button
    and the user clicks the create button
    and the user clicks the Not on company house link
    and the user clicks save
    And the user enters the details for the non CH
    and the user clicks the begin application
    Then the user should be in the application overview page

Verify the name of the new application
    [Documentation]    INFUND-669
    ...
    ...    INFUND-1163
    [Tags]    Applicant    New application
    Given the user logs-in as robot@test.com
    When the user edits the competition title
    Then the title of the new application should be visible in the overview page
    And the title should be visible in the Application team page
    And the new application should be visible in the dashboard page
    and the title should be visible in the application form

*** Keywords ***
the title of the new application should be visible in the overview page
    Page Should Contain    test title - Application number 000008

the new application should be visible in the dashboard page
    Click Link    link= My dashboard
    sleep    1s
    Wait Until Page Contains    test title
    Page Should Contain    Application number: 000008

the user goes to the create application page
    go to    ${COMPETITION_DETAILS_URL}

the user clicks the sign in to apply button
    click element    jQuery=.column-third .button:contains("Sign in to apply")

the user clicks the create button
    Click Element    jQuery=.button:contains("Create")

the user enters an organisation and clicks search
    Input Text    id=org-name    Innovate
    Click Element    id=org-search

the applicant clicks the INNOVATE LTD
    Click element    LINK=INNOVATE LTD

the user enters the post code and clicks Find UK address
    Input Text    css=#postcode-check    postcode
    Click Element    id=postcode-lookup
    Click Element    css=#select-address-block > button

the user saves the organisation
    click element    jQuery=.button:contains("Save organisation and")

the user enters the details and clicks the create account
    Input Text    id=firstName    John
    Input Text    id=lastName    Smith
    Input Text    id=phoneNumber    23232323
    Input Text    id=email    robot@test.com
    Input Password    id=password    testtest
    Input Password    id=retypedPassword    testtest
    Select Checkbox    termsAndConditions
    Submit Form

the user clicks the begin application
    Click Element    JQuery=.button:contains("Begin application")

the user should be in the application overview page
    page should contain    Application overview

the user clicks the Not on company house link
    Click Element    name=not-in-company-house
    Click Element    name=manual-address
    Input Text    id=street    street
    Input Text    id=street-2    street
    Input Text    id=street-3    street3
    Input Text    id=town    town
    Input Text    id=county    country
    Input Text    id=postcode    post code
    Input Text    name=companyHouseName    org1
    Input Text    name=organisationName    org2
    Input Text    id=postcode-check    2323
    Click Element    jQuery=.button:contains("Continue")

the user clicks save
    Click Element    jQuery=.button:contains("Save")

the user enters the details for the non CH
    Input Text    id=firstName    tester
    Input Text    id=lastName    tester
    Input Text    id=phoneNumber    23232323
    Input Text    id=email    robot2@test.com
    Input Password    id=password    testtest
    Input Password    id=retypedPassword    testtest
    Select Checkbox    termsAndConditions
    Submit Form

the user logs-in as robot@test.com
    Input Text    id=id_email    robot@test.com
    Input Password    id=id_password    testtest
    Click Button    css=input.button

the user edits the competition title
    go to    ${APPLICATION_DETAILS_APPLICATION8}
    Input Text    id=application_details-title    test title
    Click Element    jQuery=button:contains("Save and return")

the title should be visible in the Application team page
    Click Element    link=View team members and add collaborators
    Wait Until Page Contains    View and manage your partner companies
    Page Should Contain    test title - Application number 000008

the title should be visible in the application form
    go to    ${APPLICATION_DETAILS_APPLICATION8}
    Wait Until Page Contains    Project title
    Page Should Contain    test title - Application number 000008
