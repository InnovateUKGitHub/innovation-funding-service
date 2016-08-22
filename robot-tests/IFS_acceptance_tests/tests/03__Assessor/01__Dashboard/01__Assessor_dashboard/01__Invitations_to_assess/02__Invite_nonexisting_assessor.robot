*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-304: As an assessor I want to be able to accept the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Assessor    Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
New Assessor - Accept invitation
    [Documentation]    INFUND-4200
    Given the user navigates to the page    ${Assessor_Invite_Link}
    When the user clicks the button/link
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user clicks the button/link    jQuery=.button:contains("Accept")
    Then the user should be redirected to the correct page    ${Become_an_Assessor}
    And the user should see the text in the page    Become an Assessor for Innovate UK
    Then the user clicks the button/link    jQuery=.button:contains("Create account")


Create Assessor account
    [Documentation]
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
    Given the user navigates to the page    ${Assessor_Invite_Link}
    When the user clicks the button/link
    Then the user should see the text in the page    Invitation to assess ''
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