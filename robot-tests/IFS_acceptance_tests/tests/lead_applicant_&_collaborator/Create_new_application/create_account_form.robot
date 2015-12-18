*** Settings ***
Documentation     -INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile so I can log into the system
Suite Setup       Login as user    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot




*** Variables ***


${correct_password}     password
${incorrect_password}   wrongpassword
${long_password}        passwordpasswordpasswordpasswordpasswordpasswordpassword
${short_password}       pass
${valid_email}          ewan@worth.systems
${invalid_email}        notavalidemail



*** Test Cases ***
First name left blank
    [Tags]    Account
    Given the user is on the account creation page
    When the user leaves the first name field blank
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user inputs a valid password
    And the user retypes the password correctly
    And the user submits their information
    Then the user should see an error
    And the user cannot login with their new details

Last name left blank
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user leaves the last name field blank
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user inputs a valid password
    And the user retypes the password correctly
    And the user submits their information
    Then the user should see an error
    And the user cannot login with their new details

Phone number left blank
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user leaves the phone number field blank
    And the user inputs a valid email address
    And the user inputs a valid password
    And the user retypes the password correctly
    And the user submits their information
    Then the user should see an error
    And the user cannot login with their new details

Email left blank
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user inputs a phone number
    And the user leaves the email address field blank
    And the user inputs a valid password
    And the user retypes the password correctly
    And the user submits their information
    Then the user should see an error

Password left blank
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user leaves the password field blank
    And the user retypes the password correctly
    And the user submits their information
    Then the user should see an error
    And the user cannot login with their new details

Re-type password left blank
    [Tags]    Account
    Given the user is on the account creation page
    When the user leaves the first name field blank
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user inputs a valid password
    And the user leaves the re-type password field blank
    And the user submits their information
    Then the user should see an error
    And the user cannot login with their new details

Invalid email used
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs an invalid email address
    And the user inputs a valid password
    And the user retypes the password correctly
    And the user submits their information
    Then the user should see an error
    And the user cannot login with the invalid email

Password and re-typed password do not match
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user inputs a valid password
    And the user retypes the password incorrectly
    And the user submits their information
    Then the user should see an error
    And the user cannot login with either password

Password is too short
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user enters a short password
    And the user re-enters the short password
    And the user submits their information
    Then the user should see an error
    And the user cannot login with the short password

Password is too long
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user enters a long password
    And the user re-enters the long password
    And the user submits their information
    Then the user should see an error
    And the user cannot login with the long password

Valid account creation
    [Tags]    Account
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user inputs a valid password
    And the user retypes the password correctly
    And the user submits their information
    Then the user should be redirected to the login page
    And the user can login with their new details
    And the user can see the organisation they are associated with

*** Keywords ***
the user is on the account creation page
    go to    ${ACCOUNT_CREATION_FORM_URL}

the user inputs a first name
    Input Text    id=firstName    John

the user inputs a last name
    Input Text    id=lastName    Smith

the user inputs a phone number
    Input Text    id=phoneNumber    01141234567

the user inputs a valid email address
    Input Text    id=email    ${valid_email}

the user inputs a valid password
    Input Password    id=password    ${correct_password}

the user retypes the password correctly
    Input Password    id=retypedPassword    ${correct_password}

the user leaves the first name field blank
    Input Text    id=firstName    ${EMPTY}

the user leaves the last name field blank
    Input Text    id=lastName    ${EMPTY}

the user leaves the phone number field blank
    Input Text    id=phoneNumber    ${EMPTY}

the user leaves the email address field blank
    Input Text    id=email    ${EMPTY}

the user inputs an invalid email address
    Input Text    id=email    notavalidemailaddress

the user leaves the password field blank
    Input Password    id=password    ${EMPTY}

the user leaves the re-type password field blank
    Input Password    id=retypedPassword    ${EMPTY}

the user retypes the password incorrectly
    Input Password    id=retypedPassword    ${incorrect_password}

the user enters a short password
    Input Password    id=password    ${short_password}

the user re-enters the short password
    Input Password    id=retypedPassword    ${short_password}

the user enters a long password
    Input Password    id=password    ${long_password}

the user re-enters the long password
    Input Password    id=retypedPassword    ${long_password}

the user submits their information
    Submit Form

the user should see an error
    Page Should Contain     We were unable to create your account

the user cannot login with their new details
    go to    ${LOGIN_URL}
    Input Text  id=id_email  ${valid_email}
    Input Password  id=id_password      ${correct_password}
    Submit Form
    Page Should Contain     Please try again



the user cannot login with the invalid email
    go to    ${LOGIN_URL}
    Input Text   id=id_email    ${invalid_email}
    Input Password  id=id_password      ${correct_password}
    Submit Form
    Page Should Contain     Please try again

the user cannot login with the short password
    go to      ${LOGIN_URL}
    Input Text   id=id_email    ${valid_email}
    Input Password  id=id_password      ${short_password}
    Submit Form
    Page Should Contain     Please try again


the user cannot login with the long password
    go to    ${LOGIN_URL}
    Input Text   id=id_email    ${valid_email}
    Input Password  id=id_password      ${long_password}
    Submit Form
    Page Should Contain     Please try again

the user cannot login with either password
    go to   ${LOGIN_URL}
    Input Text  id=id_email     ${valid_email}
    Input Password  id=id_password      ${correct_password}
    Submit Form
    Page Should Contain     Please try again
    go to   ${LOGIN_URL}
    Input Text  id=id_email     ${valid_email}
    Input Password  id=id_password      ${incorrect_password}
    Submit Form
    Page Should Contain     Please try again

the user should be redirected to the login page
    go to    ${LOGIN_URL}

the user can login with their new details
    go to   ${LOGIN_URL}
    Input Text  id=id_email    ${valid_email}
    Input Password  id=id_password      ${correct_password}
    Submit Form

the user can see the organisation they are associated with
    go to   ${ACCOUNT_CREATION_FORM_URL}
    Page Should Contain     Nomensa
