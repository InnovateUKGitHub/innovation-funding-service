*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-304: As an assessor I want to be able to accept the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
Suite Setup       Guest user log-in    &{existing_assessor1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***
${Invitation_to_assess_existing}     ${server}/assessment/invite/competition/bcbf56004fddd137ea29d4f8434d33f62e7a7552a3a084197c7dfebce774c136c10bb26e1c6c989e?accept=accepted

*** Test Cases ***
Existing assessor - Accept invitation
    [Documentation]    INFUND-4649
    [Tags]
    When the user navigates to the page    ${Invitation_to_assess_existing}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user should see the text in the page    You are invited to act as an assessor for the competition 'Juggling Craziness'.
    #And the user clicks the button/link    jQuery=.button:contains("Accept")
    # TODO when INFUND-304 is ready to test
    #And guest user log-in    worth.email.test+assessor1@gmail.com    Passw0rd123
    #Then the user should be redirected to the correct page
    [Teardown]

Existing assessor - Reject invitation
    [Documentation]    INFUND-4631
    [Tags]    Pending
    Given the user navigates to the page    ${Invitation_to_assess_existing}
    Then the user should see the text in the page    Invitation to assess '(different)'
    And the user clicks the button/link    jQuery=.button:contains("Reject")
    And guest user log-in    worth.email.test+assessor1@gmail.com    Passw0rd123
    [Teardown]
