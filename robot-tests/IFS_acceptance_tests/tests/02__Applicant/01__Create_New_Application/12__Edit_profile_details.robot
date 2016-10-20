*** Settings ***
Documentation     INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
View and edit profile link is visible in the Dashboard page
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]    HappyPath
    When the user navigates to the page    ${DASHBOARD_URL}
    Then the user should see the element    link=View and edit your profile details

View and edit profile link redirects to the Your profile page
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]    HappyPath
    When the user clicks the button/link    link=View and edit your profile details
    Then the user should see the element    link=Edit your details

Edit the profile and verify if the changes are saved
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]    HappyPath    SmokeTest
    Given the user navigates to the page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=View and edit your profile details
    And the user clicks the button/link    link=Edit your details
    And the user enters profile details
    Then the user should see the text in the page    Chris
    And the user should see the text in the page    Brown
    And the user should see the text in the page    0123456789
    And the user can change their details back again

Verify that the applicant's name has been changed on other parts of the site
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=View and edit your profile details
    And the user clicks the button/link    link=Edit your details
    When the user enters profile details
    And the user navigates to the page    ${APPLICATION_TEAM_URL}
    Then the user should see the text in the page    Chris Brown
    And other contributors should see the applicant's updated name for the assignation options
    And the user can change their details back again

Display errors for invalid inputs of the First name
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]
    Given the user navigates to the page    ${EDIT_PROFILE_URL}
    And browser validations have been disabled
    When the user fills in the first name    ${EMPTY}
    Then the user should see an error    Please enter a first name
    And browser validations have been disabled
    And browser validations have been disabled
    And the user fills in the first name    testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes
    And the user clicks the button/link    css=[name="create-account"]
    And the user should see an error    Your first name cannot have more than 70 characters
    And browser validations have been disabled
    And the user fills in the first name    A
    And the user should see an error    Your first name should have at least 2 characters

Display errors for invalid inputs of the Last name
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]
    Given the user navigates to the page    ${EDIT_PROFILE_URL}
    And browser validations have been disabled
    When the user fills in the last name    ${EMPTY}
    Then the user should see an error    Please enter a last name
    And browser validations have been disabled
    And browser validations have been disabled
    And the user fills in the last name    testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes
    And the user clicks the button/link    css=[name="create-account"]
    And the user should see an error    Your last name cannot have more than 70 characters
    And browser validations have been disabled
    And the user fills in the last name    B
    And the user should see an error    Your last name should have at least 2 characters

Display errors for invalid inputs of the Phone field
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]
    Given the user navigates to the page    ${EDIT_PROFILE_URL}
    And browser validations have been disabled
    When the user fills in the Phone field    ${EMPTY}
    Then the user should see an error    Please enter a phone number
    And browser validations have been disabled
    And the user fills in the Phone field    121212121212121212121
    And the user clicks the button/link    css=[name="create-account"]
    And the user should see an error    Input for your phone number has a maximum length of 20 characters
    And browser validations have been disabled
    And the user fills in the Phone field    12
    And the user should see an error    Input for your phone number has a minimum length of 8 characters

*** Keywords ***
the user enters profile details
    the user should see the element    id=title
    Select From List By Index    id=title    4
    The user enters text to a text field    id=firstName    Chris
    The user enters text to a text field    id=lastName    Brown
    The user enters text to a text field    id=phoneNumber    +-0123456789
    the user clicks the button/link    css=[name="create-account"]

the user fills in the first name
    [Arguments]    ${first name}
    The user enters text to a text field    id=firstName    ${first_name}
    The user enters text to a text field    id=lastName    Brown
    The user enters text to a text field    id=phoneNumber    0123456789
    the user clicks the button/link    css=[name="create-account"]

the user fills in the last name
    [Arguments]    ${Last_name}
    The user enters text to a text field    id=firstName    Chris
    The user enters text to a text field    id=lastName    ${Last_name}
    The user enters text to a text field    id=phoneNumber    0123456789
    the user clicks the button/link    css=[name="create-account"]

the user fills in the phone field
    [Arguments]    ${phone_field}
    The user enters text to a text field    id=firstName    Chris
    The user enters text to a text field    id=lastName    Brown
    The user enters text to a text field    id=phoneNumber    ${phone_field}
    the user clicks the button/link    css=[name="create-account"]

the user can change their details back again
    Guest user log-in    &{lead_applicant_credentials}
    the user navigates to the page    ${DASHBOARD_URL}
    The user clicks the button/link    link=View and edit your profile details
    The user clicks the button/link    link=Edit your details
    the user enters their old profile details

the user enters their old profile details
    the user should see the element    id=title
    Select From List By Index    id=title    4
    The user enters text to a text field    id=firstName    Steve
    The user enters text to a text field    id=lastName    Smith
    The user enters text to a text field    id=phoneNumber    +-0123456789
    the user clicks the button/link    css=[name="create-account"]

other contributors should see the applicant's updated name for the assignation options
    Logout as user
    Guest user log-in    &{collaborator1_credentials}
    go to    ${APPLICATION_OVERVIEW_URL}
    The user should see the text in the page    Chris Brown
