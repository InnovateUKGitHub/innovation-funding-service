*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-4145: As an Assessor and I am accepting an invitation to assess within a competition and I don't have an account, I need to select that I create an account in order to be available to assess applications.
...
...               INFUND-1478 As an Assessor creating an account I need to supply my contact details so that Innovate UK can contact me to assess applications.
...
...               INFUND-4919 As an assessor and I have completed setting up my account I can see my dashboard so that I can see the competitions I have accepted to assess.
...
...               INFUND-5165 As an assessor attempting to accept/reject an invalid invitation to assess in a competition, I will receive a notification that I cannot reject the competition as soon as I attempt to reject it.
...
...               INFUND-4895 Securing of services related to Assessor Journey changes
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${Invitation_nonregistered_assessor2}    ${server}/assessment/invite/competition/2abe401d357fc486da56d2d34dc48d81948521b372baff98876665f442ee50a1474a41f5a0964720 #invitation for assessor:worth.email.test+assessor2@gmail.com
${Invitation_nonregistered_assessor3}    ${server}/assessment/invite/competition/1e05f43963cef21ec6bd5ccd6240100d35fb69fa16feacb9d4b77952bf42193842c8e73e6b07f932 #invitation for assessor:worth.email.test+assessor3@gmail.com

*** Test Cases ***
Registered user should not allowed to accept other assessor invite
    [Documentation]    INFUND-4895
    [Tags]
    [Setup]    guest user log-in    paul.plum@gmail.com    Passw0rd
    Given the user navigates to the page    ${Invitation_nonregistered_assessor3}
    When the user clicks the button/link    jQuery=.button:contains("Accept")
    Then The user should see permissions error message
    [Teardown]    logout as user

Non-registered assessor: Accept invitation
    [Documentation]    INFUND-228
    ...
    ...    INFUND-4145
    [Tags]    HappyPath
    Given the user navigates to the page    ${Invitation_nonregistered_assessor3}
    And the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user should see the text in the page    You are invited to act as an assessor for the competition 'Juggling Craziness'.
    When the user clicks the button/link    jQuery=.button:contains("Accept")
    Then the user should see the text in the page    Become an assessor for Innovate UK
    And the user should see the element    jQuery=.button:contains("Create account")

User can navigate back to Become an Assessor page
    [Documentation]    INFUND-4145
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Create account")
    Then the user should see the text in the page    Create assessor account
    And the user should see the text in the page    worth.email.test+assessor3@gmail.com
    And the user clicks the button/link    Link=Back
    And the user should see the text in the page    Become an assessor for Innovate UK

Create assessor account: server-side validations
    [Documentation]    INFUND-1478
    [Tags]    HappyPath
    Given the user clicks the button/link    jQuery=.button:contains("Create account")
    When the user clicks the button/link    jQuery=button:contains("Continue")
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

Create assessor account: client-side validations
    [Documentation]    INFUND-1478
    [Tags]    HappyPath
    When The user enters text to a text field    id=firstName    Thomas
    Then the user should not see the validation error in the create assessor form    Please enter a first name
    When The user enters text to a text field    id=lastName    Fister
    Then the user should not see the validation error in the create assessor form    Please enter a last name
    When the user selects the radio button    gender    gender2
    Then the user should not see the validation error in the create assessor form    Please select a gender
    When the user selects the radio button    ethnicity    ethnicity2
    Then the user should not see the validation error in the create assessor form    Please select an ethnicity
    When the user selects the radio button    disability    disability2
    Then the user should not see the validation error in the create assessor form    Please select a disability
    When the user enters text to a text field    id=phoneNumber    123123123123
    Then the user should not see the validation error in the create assessor form    Please enter a phone number
    And the user should not see the validation error in the create assessor form    Please enter a valid phone number
    And the user should not see the validation error in the create assessor form    Input for your phone number has a minimum length of 8 characters
    When The user enters text to a text field    id=password    Passw0rd123
    And The user enters text to a text field    id=retypedPassword    Passw0rd123
    Then the user should not see the validation error in the create assessor form    Please enter your password
    And the user should not see the validation error in the create assessor form    Password must at least be 10 characters
    When the user clicks the button/link    id=postcode-lookup
    And The user should see the text in the page    Please enter a valid postcode    # empty postcode check

Create assessor account: Postcode lookup and save
    [Documentation]    INFUND-1478
    [Tags]    HappyPath
    When The user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    id=addressForm.selectedPostcodeIndex
    And the user clicks the button/link    css=#select-address-block button
    And the address fields should be filled
    And The user enters text to a text field    id=password    Passw0rd123
    And The user enters text to a text field    id=retypedPassword    Passw0rd123
    And the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should be redirected to the correct page    ${LOGIN_URL}

Create assessor account: Accepted competitions should be displayed in dashboard
    [Documentation]    INFUND-4919
    [Tags]
    When The user enters text to a text field    id=username    worth.email.test+assessor3@gmail.com
    And The user enters text to a text field    id=password    Passw0rd123
    And the user clicks the button/link    css=button[name="_eventId_proceed"]
    Then the user should see the element    link=Juggling Craziness
    And the user clicks the button/link    link=Juggling Craziness
    And The user should see the text in the page    Juggling Craziness
    [Teardown]    Logout as user

Non-registered assessor: Reject invitation
    [Documentation]    INFUND-4631
    ...
    ...    INFUND-4636
    ...
    ...    INFUND-5165
    [Tags]
    When the user navigates to the page    ${Invitation_nonregistered_assessor2}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user clicks the button/link    css=form a
    When the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see an error    The reason cannot be blank
    And the assessor fills in all fields
    And the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see the text in the page    Thank you for letting us know you are unable to assess applications within this competition.
    And the assessor shouldn't be able to reject the rejected competition
    # TODO due to INFUND-5566
    # And the assessor shouldn't be able to accept the rejected competition
    [Teardown]    The user closes the browser

Assessor attempts to accept/reject an invitation which is already accepted
    [Documentation]    INFUND-5165
    [Tags]    Pending
    [Setup]    The guest user opens the browser
    # TODO INFUND-5566
    Then the assessor shouldn't be able to accept the accepted competition
    And the assessor shouldn't be able to reject the accepted competition

*** Keywords ***
the assessor fills in all fields
    Select From List By Index    id=rejectReason    3
    The user should not see the text in the page    This field cannot be left blank
    The user enters text to a text field    id=rejectComment    Unable to assess this application.

the user should not see the validation error in the create assessor form
    [Arguments]    ${ERROR_TEXT}
    run keyword and ignore error    mouse out    css=input
    Focus    jQuery=button:contains("Continue")
    Wait for autosave
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Wait Until Element Does Not Contain    css=.error-message    ${ERROR_TEXT}
    Run Keyword If    '${status}' == 'FAIL'    Page Should not Contain    ${ERROR_TEXT}

the assessor shouldn't be able to reject the rejected competition
    the user navigates to the page    ${Invitation_nonregistered_assessor2}
    the user clicks the button/link    css=form a
    the assessor fills all fields with valid inputs
    the user clicks the button/link    jQuery=button:contains("Reject")
    The user should see the text in the page    We were unable to reject the competition:
    The user should see the text in the page    You have already rejected the invitation for this competition.

the assessor fills all fields with valid inputs
    Select From List By Index    id=rejectReason    2
    The user should not see the text in the page    This field cannot be left blank
    The user enters text to a text field    id=rejectComment    Unable to assess this application.

the assessor shouldn't be able to accept the accepted competition
    When the user navigates to the page    ${Invitation_nonregistered_assessor3}
    And the user clicks the button/link    jQuery=button:contains("Accept")
    The user should see the text in the page    You are unable to access this page
    The user should see the text in the page    This invite has already been accepted.

the assessor shouldn't be able to reject the accepted competition
    When the user navigates to the page    ${Invitation_nonregistered_assessor3}
    And the user clicks the button/link    css=form a
    the assessor fills all fields with valid inputs
    the user clicks the button/link    jQuery=button:contains("Reject")
    The user should see the text in the page    We were unable to reject the competition:
    The user should see the text in the page    You have already accepted the invitation to assess this competition.

the assessor shouldn't be able to accept the rejected competition
    When the user navigates to the page    ${Invitation_nonregistered_assessor2}
    And the user clicks the button/link    jQuery=button:contains("Accept")
    The user should see the text in the page    You are unable to access this page
    The user should see the text in the page    You have already rejected the invitation
