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
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot

*** Test Cases ***
Your details: Server-side validations
    [Documentation]    INFUND-885
    [Tags]
    [Setup]    Applicant goes to the registration form
    When the user enters the details and clicks the create account  O'Brian Elliot-Murray  O'Dean Elliot-Manor  ${valid_email}  ${blacklisted_password}
    Then the user should see a field and summary error              Password is too weak.
    When the user enters the details and clicks the create account  !@£$  &*(^  ${valid_email}  ${correct_password}
    Then the user should see a field and summary error              Invalid first name.
    And the user should see a field and summary error               Invalid last name.
    When the user enters text to a text field                       id = firstName    ${EMPTY}
    And the user enters text to a text field                        id = lastName    ${EMPTY}
    And the user enters text to a text field                        id = phoneNumber    ${EMPTY}
    And the user enters text to a text field                        id = email    ${invalid_email_no_at}
    And the user enters text to a text field                        id = password    ${EMPTY}
    And browser validations have been disabled
    And the user clicks the button/link                             css = [name="create-account"]
    Then the user should see a field and summary error              ${enter_a_first_name}
    And the user should see a field and summary error               ${enter_a_last_name}
    And the user should see a field and summary error               ${enter_a_phone_number}
    And the user should see a field and summary error               ${enter_a_valid_email}
    And the user should see a field and summary error               Please enter your password.

Your details: client-side password hint validation
    [Documentation]    -INFUND-9293
    [Tags]
    Given the user navigates to the page       ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field  id = password    ${lower_case_password}
    And Set Focus To Element                   css = [name="create-account"]
    Then the user should see the element       css = .govuk-list.status [data-minlength-validationstatus][data-valid="true"]
    And the user should see the element        css = .govuk-list.status [data-containsuppercase-validationstatus][data-valid="false"]
    And the user should see the element        css = .govuk-list.status [data-containsnumber-validationstatus][data-valid="true"]
    When the user enters text to a text field  id = password    ${EMPTY}
    Then the user should see the element       css = .govuk-list.status [data-minlength-validationstatus][data-valid="false"]
    And the user should see the element        css = .govuk-list.status [data-containsnumber-validationstatus][data-valid="false"]

Your details: server-side password validation
    [Documentation]  IFS-4048
    [Tags]
    Given the user navigates to the page       ${ACCOUNT_CREATION_FORM_URL}
    And the user enters text to a text field   id = firstName   Brian
    And the user enters text to a text field   id = lastName    Test
    And the user enters text to a text field   id = phoneNumber    123456789
    And the user enters text to a text field   id = email    test@test.com
    And the user selects the checkbox          termsAndConditions
    When the user enters text to a text field  id = password    Brian123
    And the user clicks the button/link        css = [name="create-account"]
    Then the user should see a field and summary error  Password should not contain either your first or last name.

Your details: client-side validation
    [Documentation]    -INFUND-885
    [Tags]
    Given the user navigates to the page                 ${ACCOUNT_CREATION_FORM_URL}
    When the user enters the details and clicks the create account  O'Brian Elliot-Murray   O'Brian Elliot-Murray  ${valid_email}  Inn0vat3
    Then the user should not see an error in the page

Email duplication check
    [Documentation]    INFUND-886
    [Tags]  HappyPath
    Given Applicant goes to the registration form
    When the user enters the details and clicks the create account   John  Smith  ${lead_applicant}  ${correct_password}
    Then the user should see a field and summary error               The email address is already registered with us. Please sign into your account

User can not verify email with invalid hash
    [Documentation]  IFS-4298
    [Tags]
    When the user navigates to the page        ${SERVER}/registration/verify-email/200b9a1534649f4ba1dc581c9da2a77
    Then the user should see the element       jQuery = h1:contains("Invalid URL")

*** Keywords ***
Applicant goes to the registration form
    the user navigates to the page                            ${frontDoor}
    the user clicks the button/link in the paginated list     link = ${createApplicationOpenCompetition}
    the user follows the flow to register their organisation  radio-1

