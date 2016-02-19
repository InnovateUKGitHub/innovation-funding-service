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
    [Tags]          HappyPath
    When the Applicant is in Dashboard page
    Then the link to Edit Profile should be present

View and edit profile link redirects to the Your profile page
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]          HappyPath
    When the Applicant clicks the link Edit profile from Dashboard page
    Then the link to Edit Profile should be present in Your profile page

Edit the profile and verify if the changes are saved
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]      HappyPath
    When the Applicant is in Dashboard page
    And the Applicant clicks the link Edit profile from Dashboard page
    And the applicant clicks the Edit your details link
    And the Applicant enters the profile details
    Then the Applicant should see the saved changes in Your profile page
    And the Applicant can change their details back again

Display errors for invalid inputs of the First name
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    Given the Applicant is in Your details page
    When the Applicant fills the First name    ${EMPTY}
    Then an Error message is displayed for First name
    And the Applicant fills the First name    testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes
    Then the error message for the min/max allowed characters should be visible
    And the Applicant fills the First name    A
    Then the error message for the min/max allowed characters should be visible

Display errors for invalid inputs of the Last name
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    Given the Applicant is in Your details page
    When the Applicant fills the last name    ${EMPTY}
    Then an Error message is displayed for Last name
    And the Applicant fills the last name    testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes
    Then the error message for the min/max allowed characters should be visible
    And the Applicant fills the last name    B
    Then the error message for the min/max allowed characters should be visible

Display errors for invalid inputs of the Phone field
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    Given the Applicant is in Your details page
    When the Applicant fills the Phone field    ${EMPTY}
    Then an Error message is displayed for Phone number
    When the Applicant fills the Phone field    121212121212121212121
    Then the applicant should get a validation error for the phone number
    When the Applicant fills the Phone field    12
    Then the applicant should get a validation error for the phone number

*** Keywords ***
the Applicant is in Dashboard page
    Go To    ${DASHBOARD_URL}

the link to Edit Profile should be present
    Wait Until Element Is Visible    link=View and edit your profile details

the Applicant clicks the link Edit profile from Dashboard page
    Click Element    link=View and edit your profile details

the link to Edit Profile should be present in Your profile page
    Wait Until Element Is Visible    link=Edit your details

the Applicant enters the profile details
    Select From List By Index    id=title    4
    Input Text    id=firstName    Chris
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    +-0123456789
    Click Element    css=.extra-margin

the Applicant is in Your details page
    Go To    ${EDIT_PROFILE_URL}

an Error message is displayed for First name
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    Please enter a first name

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

an Error message is displayed for Last name
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    Please enter a last name

the Applicant fills the Phone field
    [Arguments]    ${Phone_field}
    Input Text    id=firstName    Chris
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    ${Phone_field}
    Click Element    css=.extra-margin

an Error message is displayed for Phone number
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    Please enter a phone number

the Applicant should see the saved changes in Your profile page
    Page Should Contain    Chris
    Page Should Contain    Brown
    Page Should Contain    0123456789

the applicant clicks the Edit your details link
    click element    link=Edit your details

the applicant should get a validation error for the phone number
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    Input for your phone number has a maximum length of 20 characters

the error message for the min/max allowed characters should be visible
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    length must be between 2 and 70

the Applicant can change their details back again
    the Applicant is in Dashboard page
    the Applicant clicks the link Edit profile from Dashboard page
    the applicant clicks the Edit your details link
    the Applicant enters their old profile details

the Applicant enters their old profile details
    Select From List By Index    id=title    4
    Input Text    id=firstName    Steve
    Input Text    id=lastName    Smith
    Input Text    id=phoneNumber    +-0123456789
    Click Element    css=.extra-margin