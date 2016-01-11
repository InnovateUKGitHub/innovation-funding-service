*** Settings ***
Documentation     -INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile so I can log into the system
...
...               -INFUND-886:As an applicant I want the system to recognise an existing user profile if I try to create a new account with matching details so that I am prevented from creating a new duplicate profile
Suite Setup        The guest user opens the browser
Suite Teardown     TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${correct_password}    password
${incorrect_password}    wrongpassword
${long_password}    passwordpasswordpasswordpasswordpasswordpasswordpassword
${short_password}    pass
${valid_email}    ___ewan_@worth.systems

*** Test Cases ***
First name left blank
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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

Password and re-typed password do not match
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations
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
    [Documentation]    -INFUND-885
    [Tags]    Account    Validations    Failing
    # tagged as failing due to a bug which will be raised in jira
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

Email duplication check
    [Documentation]    INFUND-886
    [Tags]    Account    Validations    Failing
    # tagged aS failing due to a bug which wqill be raised in jira
    Given the user is on the account creation page
    When the user inputs a first name
    And the user inputs a last name
    And the user inputs a phone number
    And the user inputs a valid email address
    And the user inputs a valid password
    And the user retypes the password correctly
    And the user submits their information
    Then the user should see an error for the email duplication

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
    Select Checkbox    termsAndConditions
    Submit Form

the user should see an error
    Page Should Contain    We were unable to create your account

the user cannot login with their new details
    go to    ${LOGIN_URL}
    Input Text    id=id_email    ${valid_email}
    Input Password    id=id_password    ${correct_password}
    Submit Form
    Page Should Contain    Please try again

the user cannot login with the invalid email
    go to    ${LOGIN_URL}
    Input Text    id=id_email    ${invalid_email}
    Input Password    id=id_password    ${correct_password}
    Submit Form
    Page Should Contain    Please try again

the user cannot login with the short password
    go to    ${LOGIN_URL}
    Input Text    id=id_email    ${valid_email}
    Input Password    id=id_password    ${short_password}
    Submit Form
    Page Should Contain    Please try again

the user cannot login with the long password
    go to    ${LOGIN_URL}
    Input Text    id=id_email    ${valid_email}
    Input Password    id=id_password    ${long_password}
    Submit Form
    Page Should Contain    Please try again

the user cannot login with either password
    go to    ${LOGIN_URL}
    Input Text    id=id_email    ${valid_email}
    Input Password    id=id_password    ${correct_password}
    Submit Form
    Page Should Contain    Please try again
    go to    ${LOGIN_URL}
    Input Text    id=id_email    ${valid_email}
    Input Password    id=id_password    ${incorrect_password}
    Submit Form
    Page Should Contain    Please try again

the user should be redirected to the login page
    go to    ${LOGIN_URL}

the user can login with their new details
    go to    ${LOGIN_URL}
    Input Text    id=id_email    ${valid_email}
    Input Password    id=id_password    ${correct_password}
    Submit Form

the user can see the organisation they are associated with
    go to    ${ACCOUNT_CREATION_FORM_URL}
    Page Should Contain    Nomensa

the user should see an error for the email duplication
    Page Should Contain    We were unable to create your account
    page should contain    Email address is already in use
