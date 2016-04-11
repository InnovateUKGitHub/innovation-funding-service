*** Settings ***
Documentation     INFUND-2129 As an internal Innovate UK user I want to be able to register with IFS as a competition administrator so that I can access the system with appropriate permissions for my role
...
...
...               INFUND-1987 As a Competition Administrator I want to be able to export specified data from all successfully submitted applications so that the competitions team can work with this data in the existing competitions database
Suite Setup       The guest user opens the browser
Suite Teardown    User closes the browser
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Force Tags          FailingForDev

*** Test Cases ***
When user from the list is not registered shouldn't be able to login
    [Documentation]    INFUND-2129
    Given the user navigates to the page    ${LOGIN_URL}
    When the guest user enters the log in credentials    worth.email.test+admin2@gmail.com    Passw0rd
    And the user clicks the button/link    css=button[name="_eventId_proceed"]
    Then the user should see the text in the page    Your username/password combination doesn't seem to work

Registration for a user who is in the list
    [Documentation]    INFUND-2129
    [Tags]    HappyPath    FailingForLocal
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And User creates new account verifies email and login    worth.email.test+admin1@gmail.com
    Then the user should be redirected to the correct page    ${COMP_ADMINISTRATOR}

Excel export
    [Documentation]    INFUND-1987
    [Tags]    HappyPath
    # we need to adjust this test in sprint 8 when the new competition will be ready. For now we are using the download url. And add an extra check to see if we have the correct number of rows
    Given the guest user enters the log in credentials    john.doe@innovateuk.test    Passw0rd
    And the user clicks the button/link    css=button[name="_eventId_proceed"]
    And the user should be redirected to the correct page    ${COMP_ADMINISTRATOR}
    When the admin downloads the excel
    And user opens the excel and checks the content
    [Teardown]    Then empty the download directory

*** Keywords ***
User creates new account verifies email and login
    [Arguments]    ${CREATE_ACCOUNT_EMAIL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Sign in to apply")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=organisationSearchName    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    LINK=INNOVATE LTD
    Select Checkbox    id=address-same
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user enters the details and clicks the create account    ${CREATE_ACCOUNT_EMAIL}
    And the user should be redirected to the correct page    ${REGISTRATION_SUCCESS}
    the user clicks the link from the appropriate email sender
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    And the user clicks the button/link    jQuery=.button:contains("Log in")
    And the guest user inserts user email & password    ${CREATE_ACCOUNT_EMAIL}    Passw0rd
    And the guest user clicks the log-in button

the user enters the details and clicks the create account
    [Arguments]    ${CREATE_ACCOUNT_EMAIL}
    Input Text    id=firstName    Eric
    Input Text    id=lastName    Cantona
    Input Text    id=phoneNumber    0505050508
    Input Text    id=email    ${CREATE_ACCOUNT_EMAIL}
    Input Password    id=password    Passw0rd
    Input Password    id=retypedPassword    Passw0rd
    Select Checkbox    termsAndConditions
    Submit Form

Download File
    [Arguments]    ${COOKIE_VALUE}    ${URL}    ${FILENAME}
    log    ${COOKIE_VALUE}
    Run and Return RC    curl -v --insecure --cookie "${COOKIE_VALUE}" ${URL} > download_files/${/}${FILENAME}

the admin downloads the excel
    ${ALL_COOKIES} =    Get Cookies
    Log    ${ALL_COOKIES}
    Download File    ${ALL_COOKIES}    https://ifs-local-dev/management/competition/1/download    submitted_applications.xls
    sleep    2s

User opens the excel and checks the content
    Open Excel    download_files/submitted_applications.xls
    ${APPLICATION_ID_1}=    read Cell Data by name    Submitted Applications    A2
    Should Be Equal    ${APPLICATION_ID_1}    00000005
    ${APPLICATION_TITLE_1}=    read Cell Data by name    Submitted Applications    B2
    should be equal    ${APPLICATION_TITLE_1}    A new innovative solution
    ${LEAD_ORRGANISATION_1}=    read Cell Data by name    Submitted Applications    C2
    should be equal    ${LEAD_ORRGANISATION_1}    Empire Ltd
    ${FIRST_NAME_1}=    read Cell Data by name    Submitted Applications    D2
    should be equal    ${FIRST_NAME_1}    Steve
    ${LAST_NAME_1}=    read Cell Data by name    Submitted Applications    E2
    should be equal    ${LAST_NAME_1}    Smith
    ${EMAIL_1}=    read Cell Data by name    Submitted Applications    F2
    should be equal    ${EMAIL_1}    steve.smith@empire.com
    ${DURATION_1}=    read Cell Data by name    Submitted Applications    G2
    Should Be Equal As Numbers    ${DURATION_1}    20.0
    ${NUMBER_OF_PARTNERS_1}=    read Cell Data by name    Submitted Applications    H2
    Should Be Equal As Numbers    ${NUMBER_OF_PARTNERS_1}    4.0
    ${SUMMARY_1}=    read Cell Data by name    Submitted Applications    I2
    Should contain    ${SUMMARY_1}    The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.
    ${TOTAL_COST_1}=    read Cell Data by name    Submitted Applications    J2
    Should Be Equal    ${TOTAL_COST_1}    £398,324.29
    ${FUNDING_1}=    read Cell Data by name    Submitted Applications    K2
    Should Be Equal    ${FUNDING_1}    £8,000.00

Empty the download directory
    Empty Directory    download_files
