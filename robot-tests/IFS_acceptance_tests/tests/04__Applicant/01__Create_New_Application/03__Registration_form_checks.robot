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
Suite Setup       the guest user opens the browser
Suite Teardown    Close browser and delete emails
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../10__Project_setup/PS_Common.robot

*** Test Cases ***
Your details: Server-side validations
    [Documentation]    -INFUND-885
    [Tags]    HappyPath
    [Setup]    Applicant goes to the registration form
    Given the user fills in valid details
    And the user enters text to a text field   id=password    ${blacklisted_password}
    And the user selects the checkbox          termsAndConditions
    And the user clicks the button/link        jQuery=button:contains("Create account")
    And the user should see an error           Password is too weak.
    And the user enters text to a text field   id=firstName   !@£$
    And the user enters text to a text field   id=lastName    &*(^
    And the user enters text to a text field   id=password    ${correct_password}
    And the user clicks the button/link        jQuery=button:contains("Create account")
    And the user should see an error           Invalid first name.
    And the user should see an error           Invalid last name.
    When the user enters text to a text field  id=firstName    ${EMPTY}
    And the user enters text to a text field   id=lastName    ${EMPTY}
    And the user enters text to a text field   id=phoneNumber    ${EMPTY}
    And the user enters text to a text field   id=email    ${invalid_email_no_at}
    And the user enters text to a text field   id=password    ${EMPTY}
    And browser validations have been disabled
    And the user clicks the button/link        css=[name="create-account"]
    Then the user should see an error          Please enter a first name.
    And the user should see an error           We were unable to create your account
    And the user should see an error           Please enter a last name.
    And the user should see an error           Please enter a phone number.
    And the user should see an error           Please enter a valid email address.
    And the user should see an error           Please enter your password.

Your details: client-side password hint validation
    [Documentation]    -INFUND-9293
    [Tags]
    Given the user navigates to the page       ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field  id=password    ${lower_case_password}
    And the user moves focus to the element    css=[name="create-account"]
    Then the user should see the element       css=.list.status [data-minlength-validationstatus][data-valid="true"]
    And the user should see the element        css=.list.status [data-containsuppercase-validationstatus][data-valid="false"]
    And the user should see the element        css=.list.status [data-containsnumber-validationstatus][data-valid="true"]
    When the user enters text to a text field  id=password    ${EMPTY}
    Then the user should see the element       css=.list.status [data-minlength-validationstatus][data-valid="false"]
    And the user should see the element        css=.list.status [data-containsnumber-validationstatus][data-valid="false"]

Your details: client-side validation
    [Documentation]    -INFUND-885
    [Tags]    HappyPath
    Given the user navigates to the page                 ${ACCOUNT_CREATION_FORM_URL}
    When the user fills in valid details
    Then The user should not see the text in the page  Please enter a first name.
    And The user should not see the text in the page   Please enter a last name.
    And The user should not see the text in the page   Please enter a phone number.
    And The user should not see the text in the page   Please enter a valid email address.
    And The user should not see the text in the page   Please enter your password.
    And The user should not see the text in the page   In order to register an account you have to agree to the Terms and Conditions.
    And the user submits their information

User can not login with the invalid email
    [Tags]
    [Setup]    the user navigates to the page          ${SERVER}
    Then the user cannot login with the invalid email  ${invalid_email_no_at}

Email duplication check
    [Documentation]    INFUND-886
    [Tags]
    Given Applicant goes to the registration form
    When the user enters text to a text field  id=firstName    John
    And the user enters text to a text field   id=lastName    Smith
    And the user enters text to a text field   id=phoneNumber    01141234567
    And the user enters text to a text field   id=email    ${lead_applicant}
    And the user enters text to a text field   id=password    ${correct_password}
    And the user submits their information
    Then the user should see an error          The email address is already registered with us. Please sign into your account

*** Keywords ***
the user cannot login with the invalid email
    [Arguments]    ${invalid_email_addy}
    go to                                     ${LOGIN_URL}
    Input Text                                id=username    ${invalid_email_addy}
    Input Password                            id=password  ${correct_password}
    Click Button                              css=button[name="_eventId_proceed"]
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    The user should see the text in the page    Please enter a valid e-mail address
    Run Keyword If    '${status}' == 'FAIL'   The user should see the text in the page    Please enter a valid email address
    Execute Javascript                        jQuery('form').attr('novalidate','novalidate');
    Click Button                              css=button[name="_eventId_proceed"]
    The user should see the text in the page  ${unsuccessful_login_message}
    The user should see the text in the page  Your email/password combination doesn't seem to work.

Applicant goes to the registration form
    the user navigates to the page             ${frontDoor}
    Given the user clicks the button/link      link=Home and industrial efficiency programme
    When the user clicks the button/link       link=Start new application
    And the user clicks the button/link        jQuery=.button:contains("Create account")
    When the user enters text to a text field  id=organisationSearchName    Hive IT
    And the user clicks the button/link        id=org-search
    And the user clicks the button/link        Link=${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}
    And the user selects the checkbox          address-same
    And the user clicks the button/link        jQuery=button:contains("Continue")
    And the user selects the radio button      organisationTypeId    radio-1
    And the user clicks the button/link        jQuery=button:contains("Save and continue")
    And the user clicks the button/link        jQuery=a:contains("Save and continue")

the user fills in valid details
    the user enters text to a text field  id=firstName    O'Brian Elliot-Murray    #First and last name containing hyphen, space and aposthrophe check
    the user enters text to a text field  id=lastName    O'Brian Elliot-Murray
    the user enters text to a text field  id=phoneNumber    01141234567
    the user enters text to a text field  id=email    ${valid_email}
    the user enters text to a text field  id=password    ${correct_password}

