*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-4145: As an Assessor and I am accepting an invitation to assess within a competition and I don't have an account, I need to select that I create an account in order to be available to assess applications.
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
${Invitation_nonregistered_assessor2}    ${server}/assessment/invite/competition/2abe401d357fc486da56d2d34dc48d81948521b372baff98876665f442ee50a1474a41f5a0964720 #invitation for assessor:worth.email.test+assessor2@gmail.com
${Invitation_nonregistered_assessor3}    ${server}/assessment/invite/competition/1e05f43963cef21ec6bd5ccd6240100d35fb69fa16feacb9d4b77952bf42193842c8e73e6b07f932 #invitation for assessor:worth.email.test+assessor3@gmail.com

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
