*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-304: As an assessor I want to be able to accept the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
Suite Setup       Guest user log-in    &{nonexisting_assessor2_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Test Cases ***
New Assessor - Accept invitation
    [Documentation]    INFUND-4649
    Given the user navigates to the page    ${Invitation_to_assess_nonexisting}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user should see the text in the page    You are invited to act as an assessor for the competition 'Juggling Craziness'.
    # And the user clicks the button/link    jQuery=.button:contains("Accept")
    # TODO when INFUND-304 is ready to test
    #    Then the user should be redirected to the correct page    ${Become_an_Assessor}
    #    And the user should see the text in the page    Become an Assessor for Innovate UK
    #    Then the user clicks the button/link    jQuery=.button:contains("Create account")

Create Assessor account
    [Tags]    Pending
    When the user navigates to the page
    Then the Assessor fills the create account form
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the Assessor fills the additional information
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the Assessor fills the declaration of interest
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the Assessor fills the Terms of contract
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should be redirected to the correct page    ${Assessor_Dashboard}

New Assessor - Reject invitation
    [Documentation]    INFUND-4636
    [Tags]    Pending
    Given the user navigates to the page    ${Invitation_to_assess_nonexisting}
    Then the user should see the text in the page    Invitation to assess '(different name)'
    And the user clicks the button/link    jQuery=.button:contains("Reject")
    And guest user log-in    worth.email.test+assessor1@gmail.com    Passw0rd123
    Then the user should not see the text in the page

*** Keywords ***
the Assessor fills the create account form
    the user should see the element    id=
    Select From List By Index    id=    4
    Input Text    id=firstName
    Input Text    id=lastName
    Select Radio Button
    the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    the address fields should be filled with valid data
    Input Text    id=phoneNumber    +-0123456789
    Input Text    id=password

the Assessor fills the additional information
    Input Text (skill area)
    Select Radio Button (assessor type)

the Assessor fills the declaration of interest
    Input Text (Principal employer)
    Input Text (Role)
    Input Text (Professional affiliations)
    Select Radio Button (appointments)
    Select Radio Button (financial interests)
    Select Radio Button (family member)
    Select Radio Button (family member)
    Select Checkbox
