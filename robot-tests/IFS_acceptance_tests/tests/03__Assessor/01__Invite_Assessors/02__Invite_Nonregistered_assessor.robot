*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-4145: As an Assessor and I am accepting an invitation to assess within a competition and I don't have an account, I need to select that I create an account in order to be available to assess applications.
...
...               INFUND-1478 As an Assessor creating an account I need to supply my contact details so that Innovate UK can contact me to assess applications.
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot
Resource          ../../../resources/variables/PASSWORD_VARIABLES.robot

*** Variables ***
${Invitation_nonregistered_assessor2}    ${server}/assessment/invite/competition/2abe401d357fc486da56d2d34dc48d81948521b372baff98876665f442ee50a1474a41f5a0964720 #invitation for assessor:worth.email.test+assessor2@gmail.com
${Invitation_nonregistered_assessor3}    ${server}/assessment/invite/competition/1e05f43963cef21ec6bd5ccd6240100d35fb69fa16feacb9d4b77952bf42193842c8e73e6b07f932 #invitation for assessor:worth.email.test+assessor3@gmail.com
${Create_account_contact_details_assessor3}    ${server}/assessment/registration/1e05f43963cef21ec6bd5ccd6240100d35fb69fa16feacb9d4b77952bf42193842c8e73e6b07f932/register
${Personal_info_password}    thomas01234
${Password_all_character}    PasswordPassword
*** Test Cases ***
Non-registered assessor: Accept invitation
    [Documentation]    INFUND-228
    ...
    ...    INFUND-4145
    [Tags]
    Given the user navigates to the page    ${Invitation_nonregistered_assessor3}
    And the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user should see the text in the page    You are invited to act as an assessor for the competition 'Juggling Craziness'.
    When the user clicks the button/link    jQuery=.button:contains("Accept")
    Then the user should see the text in the page    Become an assessor for Innovate UK
    And the user should see the element    jQuery=.button:contains("Create account")

Register as an assessor
    [Documentation]    INFUND-4145
    When the user clicks the button/link    jQuery=.button:contains("Create account")
    Then the user should see the text in the page    Create assessor account
    And the user clicks the button/link    Link=Back
    And the user should see the text in the page    Become an assessor for Innovate UK

Create assessor account: Contact details server-side validations
    [Documentation]    INFUND-4916
    [Tags]    HappyPath
    Given the user clicks the button/link    jQuery=.button:contains("Create account")
    And The user should be redirected to the correct page    ${Create_account_contact_details_assessor3}
    When Then the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see an error    Please enter a first name
    And the user should see an error    Please enter a last name
    And the user should see an error    Please select a gender
    And the user should see an error    Please select an ethnicity
    And the user should see an error    Please select a disability
    And the user should see an error    Please enter a phone number
    And the user should see an error    Please enter your password
    And the user should see an error    Please re-type your password
    And the user should see an error    Please enter a valid phone number
    And the user should see an error    Input for your phone number has a minimum length of 8 characters
    And the user should see an error    Your last name should have at least 2 characters
    And the user should see an error    Your first name should have at least 2 characters
    And the user should see an error    Password must at least be 10 characters
    And the user should see an error    Please enter your address details

Create assessor account: Contact details client-side validations
    [Documentation]    INFUND-4916
    [Tags]    HappyPath    Pending
    When the user enters text to a text field    id=firstName    T
    And the user moves focus away from the element    id=firstName
    Then The user should not see the text in the page    Please enter a first name
    And the user should see an error    Your first name should have at least 2 characters
    When The user enters text to a text field    id=firstName    Thomas
    And the user moves focus away from the element    id=firstName
    Then The user should not see the text in the page    Your first name should have at least 2 characters
    When the user enters text to a text field    id=lastName    F
    And the user moves focus away from the element    id=lastName
    Then The user should not see the text in the page    Please enter a last name
    And the user should see an error    Your last name should have at least 2 characters
    When The user enters text to a text field    id=lastName    Fister
    And the user moves focus away from the element    id=lastName
    Then The user should not see the text in the page    Your last name should have at least 2 characters
    When the user selects the radio button    gender    gender2
    Then The user should not see the text in the page    Please select a gender
    When the user selects the radio button    ethnicity    ethnicity2
    Then The user should not see the text in the page    Please select an ethnicity
    When the user selects the radio button    disability    disability2
    Then The user should not see the text in the page    Please select a disability
    When the user enters text to a text field    id=phoneNumber    invalidphone
    And the user moves focus away from the element    id=phoneNumber
    Then The user should not see the text in the page    Please enter a phone number
    And the user should see an error    Please enter a valid phone number
    When the user enters text to a text field    id=phoneNumber    0123
    And the user moves focus away from the element    id=phoneNumber
    Then The user should not see the text in the page    Please enter a valid phone number
    And the user should see an error    Input for your phone number has a minimum length of 8 characters
    When the user enters text to a text field    id=phoneNumber    08549741414
    And the user moves focus away from the element    id=phoneNumber
    Then The user should not see the text in the page    Input for your phone number has a minimum length of 8 characters
    When the user enters text to a text field    id=password    ${lower_case_password}
    And The user enters text to a text field    id=retypedPassword    ${lower_case_password}
    And the user moves focus away from the element    id=retypedPassword
    Then The user should not see the text in the page    Please enter your password
    And the user should see an error    Password must contain at least one upper case letter
    When the user enters text to a text field    id=password    ${upper_case_password}
    And The user enters text to a text field    id=retypedPassword    ${upper_case_password}
    And the user moves focus away from the element    id=retypedPassword
    Then The user should not see the text in the page    Password must contain at least one upper case letter
    And the user should see an error    Password must contain at least one lower case letter
    When the user enters text to a text field    id=password    ${Personal_info_password}
    And The user enters text to a text field    id=retypedPassword    ${Personal_info_password}
    And the user moves focus away from the element    id=retypedPassword
    Then the user should see an error    Password should not contain either your first or last name
    When the user enters text to a text field    id=password    ${Password_all_character}
    And The user enters text to a text field    id=retypedPassword    ${Password_all_character}
    And the user moves focus away from the element    id=retypedPassword
    Then the user should see an error    Password must contain at least one number
    When the user enters text to a text field    id=password    ${short_password}
    And The user enters text to a text field    id=retypedPassword    ${short_password}
    And the user moves focus away from the element    id=retypedPassword
    Then the user should see an error    Password must at least be 10 characters
    When The user enters text to a text field    id=password    Passw0rdPassw0rd
    And The user enters text to a text field    id=retypedPassword    Password1Password1
    And the user moves focus away from the element    id=retypedPassword
    Then the user should see an error    Passwords must match
    When The user enters text to a text field    id=password    Password1Password1
    And The user enters text to a text field    id=retypedPassword    Passw0rdPassw0rd
    And the user moves focus away from the element    id=retypedPassword
    Then the user should see an error    Passwords must match
    When The user enters text to a text field    id=password    Passw0rdPassw0rd
    And The user enters text to a text field    id=retypedPassword    Passw0rdPassw0rd
    And the user moves focus away from the element    id=retypedPassword
    Then The user should not see the text in the page    Password must contain at least one upper case letter
    And The user should not see the text in the page    Password must contain at least one lower case letter
    And The user should not see the text in the page    Password should not contain either your first or last name
    And The user should not see the text in the page    Password must at least be 10 characters
    And The user should not see the text in the page    Password must contain at least one number
    And The user should not see the text in the page    Passwords must match
   # And the user should see an error    Please enter your address details
    # When the user clicks the button/link    id=postcode-lookup
    # And the user should see the element    css=.form-label .error-message   # empty postcode check

Create assessor account: Contact details
    [Documentation]
    [Tags]    HappyPath    Pending
    When The user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    id=addressForm.selectedPostcodeIndex
    And the user clicks the button/link    css=#select-address-block button
    And the email displayed should be correct
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should be redirected to the correct page    ${LOGIN_URL}

Non-registered assessor: Reject invitation
    [Documentation]    INFUND-4631, INFUND-4636
    [Tags]
    When the user navigates to the page    ${Invitation_nonregistered_assessor2}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user clicks the button/link    css=form a
    When the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see an error    The reason cannot be blank
    And the assessor fills in all fields
    And the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see the text in the page    Thank you for letting us know you are unable to assess applications within this competition.
    # TODO remove the comment after 5165 is ready to test
    # And the user shouldn't be able to reject the rejected competition

*** Keywords ***
the assessor fills in all fields
    Select From List By Index    id=rejectReason    3
    The user should not see the text in the page    This field cannot be left blank
    The user enters text to a text field    id=rejectComment    Unable to assess this application.

the assessor fills in contact details
    Select From List By Index    id=title    0
    The user enters text to a text field    id=firstName    Thomas
    The user enters text to a text field    id=lastName    Fister
    the user selects the radio button    gender    gender2
    the user selects the radio button    ethnicity    ethnicity2
    the user selects the radio button    disability    disability2
    The user enters text to a text field    id=phoneNumber    08549741414
    The user enters text to a text field    id=password    Password1Password1
    The user enters text to a text field    id=retypedPassword    Password1Password1

the email displayed should be correct
    ${Email}=    Get Text    css=div:nth-child(10) p strong
    Should Be Equal    ${Email}    worth.email.test+assessor3@gmail.com

the user moves focus away from the element
    [Arguments]    ${element}
    mouse out    ${element}
    focus    jQuery=.button:contains("Continue")

