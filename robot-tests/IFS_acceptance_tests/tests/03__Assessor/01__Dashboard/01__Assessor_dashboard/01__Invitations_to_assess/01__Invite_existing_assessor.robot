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

*** Variables ***
# TODO the token needs to be added
${Assessor_Invite_Link}

*** Test Cases ***
Existing assessor - Accept invitation
    [Documentation]    INFUND-4200
    Given the user navigates to the page    ${Assessor_Invite_Link}
    When the user clicks the button/link
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user clicks the button/link    jQuery=.button:contains("Accept")
    And guest user log-in    worth.email.test+assessor1@gmail.com    Passw0rd123
    Then the user should be redirected to the correct page    ${Assessor_Dashboard}    #The variable needs to be declared

Existing assessor - Reject invitation
    [Documentation]    INFUND-4636
    Given the user navigates to the page    ${Assessor_Invite_Link}
    When the user clicks the button/link
    Then the user should see the text in the page    Invitation to assess ''
    And the user clicks the button/link    jQuery=.button:contains("Reject")
    And guest user log-in    worth.email.test+assessor1@gmail.com    Passw0rd123
    Then the user should not see the text in the page