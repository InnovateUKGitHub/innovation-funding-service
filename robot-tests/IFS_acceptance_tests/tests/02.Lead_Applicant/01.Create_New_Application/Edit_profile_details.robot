*** Settings ***
Documentation     INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
View and edit profile link is visible in the Dashboard page
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]    HappyPath
    Given user navigates to the page    ${DASHBOARD_URL}
    Then user should see the element    link=View and edit your profile details

View and edit profile link redirects to the Your profile page
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]    HappyPath
    When user clicks the button/link    link=View and edit your profile details
    Then user should see the element    link=Edit your details

Edit the profile and verify if the changes are saved
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]    HappyPath
    Given user navigates to the page    ${DASHBOARD_URL}
    When user clicks the button/link    link=View and edit your profile details
    And user clicks the button/link    link=Edit your details
    And the Applicant enters the profile details
    Then user should see the text in the page    Chris
    And user should see the text in the page    Brown
    And user should see the text in the page    0123456789
    And the Applicant can change their details back again

Verify that the applicant's name has been changed on other parts of the site
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]    Pending
    # Pending due to bug INFUND-1967
    Given user navigates to the page    ${APPLICATION_TEAM_URL}
    Then user should see the text in the page    Chris Brown
    And other contributors should see the Applicant's updated name for the assignation options
    And the Applicant can change their details back again

Display errors for invalid inputs of the First name
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    Given user navigates to the page    ${EDIT_PROFILE_URL}
    When the Applicant fills the First name    ${EMPTY}
    Then user should see an error    Please enter a first name
    And the Applicant fills the First name    testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes
    Then user should see an error    length must be between 2 and 70
    And the Applicant fills the First name    A
    Then user should see an error    length must be between 2 and 70

Display errors for invalid inputs of the Last name
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    Given user navigates to the page    ${EDIT_PROFILE_URL}
    When the Applicant fills the last name    ${EMPTY}
    Then user should see an error    Please enter a last name
    And the Applicant fills the last name    testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes
    Then user should see an error    length must be between 2 and 70
    And the Applicant fills the last name    B
    Then user should see an error    length must be between 2 and 70

Display errors for invalid inputs of the Phone field
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    Given user navigates to the page    ${EDIT_PROFILE_URL}
    When the Applicant fills the Phone field    ${EMPTY}
    Then user should see an error    Please enter a phone number
    When the Applicant fills the Phone field    121212121212121212121
    Then user should see an error    Input for your phone number has a maximum length of 20 characters
    When the Applicant fills the Phone field    12
    Then user should see an error    Input for your phone number has a maximum length of 20 characters

*** Keywords ***
the Applicant enters the profile details
    Select From List By Index    id=title    4
    Input Text    id=firstName    Chris
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    +-0123456789
    Click Element    css=.extra-margin

the Applicant fills the First name
    [Arguments]    ${First name}
    Input Text    id=firstName    ${First_name}
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    0123456789
    Click Element    css=.extra-margin

the Applicant fills the last name
    [Arguments]    ${Last_name}
    Input Text    id=firstName    Chris
    Input Text    id=lastName    ${Last_name}
    Input Text    id=phoneNumber    0123456789
    Click Element    css=.extra-margin

the Applicant fills the Phone field
    [Arguments]    ${Phone_field}
    Input Text    id=firstName    Chris
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    ${Phone_field}
    Click Element    css=.extra-margin

the Applicant can change their details back again
    Login as User    &{lead_applicant_credentials}
    user navigates to the page    ${DASHBOARD_URL}
    user clicks the button/link    link=View and edit your profile details
    user clicks the button/link    link=Edit your details
    the Applicant enters their old profile details

the Applicant enters their old profile details
    Select From List By Index    id=title    4
    Input Text    id=firstName    Steve
    Input Text    id=lastName    Smith
    Input Text    id=phoneNumber    +-0123456789
    Click Element    css=.extra-margin

other contributors should see the Applicant's updated name for the assignation options
    Logout as user
    Login as user    &{collaborator1_credentials}
    go to    ${APPLICATION_OVERVIEW_URL}
    page should contain    chris brown
