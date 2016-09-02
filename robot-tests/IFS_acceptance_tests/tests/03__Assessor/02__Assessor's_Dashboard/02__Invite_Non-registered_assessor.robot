*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***
${Invitation_nonexisting_assessor2}    ${server}/assessment/invite/competition/2abe401d357fc486da56d2d34dc48d81948521b372baff98876665f442ee50a1474a41f5a0964720 #invitation for assessor:worth.email.test+assessor2@gmail.com
${Invitation_nonexisting_assessor3}    ${server}/assessment/invite/competition/1e05f43963cef21ec6bd5ccd6240100d35fb69fa16feacb9d4b77952bf42193842c8e73e6b07f932 #invitation for assessor:worth.email.test+assessor3@gmail.com

*** Test Cases ***
Non-registered assessor: Accept invitation
    [Documentation]    INFUND-228
    Given the user navigates to the page    ${Invitation_nonexisting_assessor2}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user should see the text in the page    You are invited to act as an assessor for the competition 'Juggling Craziness'.
    # And the user clicks the button/link    jQuery=.button:contains("Accept")
    # TODO when INFUND-304 is ready to test

Non-registered assessor: Reject invitation
    [Documentation]    INFUND-4631
    [Tags]
    Given the user navigates to the page    ${Invitation_nonexisting_assessor3}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    # TODO add more steps when the Reject flow will be ready
    #And the user clicks the button/link    jQuery=.button:contains("Reject")

*** Keywords ***
