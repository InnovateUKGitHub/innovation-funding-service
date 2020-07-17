*** Settings ***
Documentation     INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile so...
...
...               INFUND-886:As an applicant I want the system to recognise an existing user profile if I try to create a new account with matching details so...
...
...               INFUND-6387 As an Applicant creating an account I will be invited to answer questions for diversity monitoring purposes so...
...
...               INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile...
...
...               INFUND-1147: Further acceptance tests for the create account page
...
...               INFUND-2497: As a new user I would like to have an indication that my password is correct straight after typing...
...
...               IFS-4298 Registration redirect doesn't check results of verification
...
...               IFS-4048 Server side validation on password does not disappear on registration page
Suite Setup       the guest user opens the browser
Suite Teardown    Close browser and delete emails
Force Tags        Applicant  ATS2020
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/PS_Common.robot

*** Test Cases ***
Your details: Password is too weak server-side validations
    [Documentation]    INFUND-885
    [Tags]
    Given Applicant goes to the registration form
    When the user enters the details and clicks the create account     O'Brian Elliot-Murray  O'Dean Elliot-Manor  ${valid_email}  ${blacklisted_password}
    Then the user should see a field and summary error                 Password is too weak.

Your details: invalid email, firstname and lastname server-side validations
    [Documentation]    INFUND-885
    [Tags]
    When the user enters the details and clicks the create account     !@£$  &*(^  ${valid_email}  ${correct_password}
    Then the user should see a field and summary error                 Invalid first name.
    And the user should see a field and summary error                  Invalid last name.

Your details: empty form inputs server-side validations
    [Documentation]    INFUND-885
    [Tags]
    When the user fills in registration form                                    ${EMPTY}  ${EMPTY}  ${EMPTY}  ${invalid_email_no_at}  ${EMPTY}
    And browser validations have been disabled
    And the user clicks the button/link                                         css = [name="create-account"]
    Then the user should see field and summary validations for empty inputs

Your details: client-side password hint validation
    [Documentation]    -INFUND-9293
    [Tags]
    Given the user navigates to the page                   ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field              id = password    ${lower_case_password}
    And Set Focus To Element                               css = [name="create-account"]
    Then the user should see password hint validations

Your details: client-side empty password hint validation
    [Documentation]    -INFUND-9293
    [Tags]
    When the user enters text to a text field     id = password    ${EMPTY}
    Then the user should see the element          css = .govuk-list.status [data-minlength-validationstatus][data-valid="false"]
    And the user should see the element           css = .govuk-list.status [data-containsnumber-validationstatus][data-valid="false"]

Your details: server-side password validation
    [Documentation]  IFS-4048
    [Tags]
    Given the user navigates to the page                    ${ACCOUNT_CREATION_FORM_URL}
    #When the user fills in registration form                Brian  Test  123456789  test@test.com  Brian123
    And the user enters text to a text field                id = firstName   Brian
    And the user enters text to a text field                id = lastName    Test
    And the user enters text to a text field                id = phoneNumber    123456789
    And the user enters text to a text field                id = email    test@test.com
    When the user enters text to a text field               id = password    Brian123
    And the user selects the checkbox                       termsAndConditions
    And the user clicks the button/link                     css = [name="create-account"]
    Then the user should see a field and summary error      Password should not contain either your first or last name.

Your details: client-side validation
    [Documentation]    -INFUND-885
    [Tags]
    Given the user navigates to the page                               ${ACCOUNT_CREATION_FORM_URL}
    When the user enters the details and clicks the create account     O'Brian Elliot-Murray   O'Brian Elliot-Murray  ${valid_email}  Inn0vat3
    Then the user should not see an error in the page

Email duplication check
    [Documentation]    INFUND-886
    [Tags]  HappyPath
    Given Applicant goes to the registration form
    When the user enters the details and clicks the create account     John  Smith  ${lead_applicant}  ${correct_password}
    Then the user should see a field and summary error                 The email address is already registered with us. Please sign into your account

User can not verify email with invalid hash
    [Documentation]  IFS-4298
    [Tags]
    When the user navigates to the page      ${SERVER}/registration/verify-email/200b9a1534649f4ba1dc581c9da2a77
    Then the user should see the element     jQuery = h1:contains("Invalid URL")

*** Keywords ***
Applicant goes to the registration form
    the user navigates to the page                               ${frontDoor}
    the user clicks the button/link in the paginated list        link = ${createApplicationOpenCompetition}
    the user follows the flow to register their organisation     radio-1

the user fills in registration form
    [Arguments]  ${firstName}  ${lastName}  ${phoneNumber}  ${email}  ${password}
    the user enters text to a text field     id = firstName    ${firstName}
    the user enters text to a text field     id = lastName    ${lastName}
    the user enters text to a text field     id = phoneNumber   ${phoneNumber}
    the user enters text to a text field     id = email    ${email}
    the user enters text to a text field     id = password     ${password}

the user should see field and summary validations for empty inputs
    the user should see a field and summary error     ${enter_a_first_name}
    the user should see a field and summary error     ${enter_a_last_name}
    the user should see a field and summary error     ${enter_a_phone_number}
    the user should see a field and summary error     ${enter_a_valid_email}
    the user should see a field and summary error     Please enter your password.

the user should see password hint validations
    the user should see the element     css = .govuk-list.status [data-minlength-validationstatus][data-valid="true"]
    the user should see the element     css = .govuk-list.status [data-containsuppercase-validationstatus][data-valid="false"]
    the user should see the element     css = .govuk-list.status [data-containsnumber-validationstatus][data-valid="true"]