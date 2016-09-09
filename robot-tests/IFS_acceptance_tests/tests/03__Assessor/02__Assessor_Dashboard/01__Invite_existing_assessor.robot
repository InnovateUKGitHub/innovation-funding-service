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
${Invitation_existing_assessor1}    ${server}/assessment/invite/competition/bcbf56004fddd137ea29d4f8434d33f62e7a7552a3a084197c7dfebce774c136c10bb26e1c6c989e
#${Invitation_existing_assessor2}
${Invitation_nonexisting_assessor2}    ${server}/assessment/invite/competition/2abe401d357fc486da56d2d34dc48d81948521b372baff98876665f442ee50a1474a41f5a0964720 #invitation for assessor:worth.email.test+assessor2@gmail.com

*** Test Cases ***
Existing assessor: Reject invitation
    [Documentation]    INFUND-4631
    [Tags]
    [Setup]    log in as user    &{existing_assessor1_credentials}
    When the user navigates to the page    ${Invitation_existing_assessor1}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user clicks the button/link    css=form a
    When the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see an error    This field cannot be left blank
    And the assessor fills in all fields
    And the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see the text in the page    Thank you for letting us know you are unable to assess applications within this competition.
    And the user shouldn't be able to accept the rejected competition
    [Teardown]    logout as user

Existing assessor: Accept invitation
    [Documentation]    INFUND-228
    [Tags]    Pending
    [Setup]    log in as user    &{assessor2_credentials}
    When the user navigates to the page    ${Invitation_existing_assessor2}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user should see the text in the page    You are invited to act as an assessor for the competition 'Juggling Craziness'.
    #And the user clicks the button/link    jQuery=.button:contains("Accept")
    # TODO when INFUND-304 is ready to test
    [Teardown]

Existing assessor shouldn't be able to accept other assessor's invitation
    [Documentation]    INFUND-228
    [Tags]    Pending
    # Pending 304 to be ready for test
    Given the user navigates to the page    ${Invitation_nonexisting_assessor2}
    when the user clicks the button/link    jQuery=button:contains(Accept)
    # TODO when INFUND-304 is ready to test

Existing assessor shouldn't be able to reject other assessor's invitation
    [Documentation]    INFUND-4631
    [Tags]    Pending
    [Setup]    log in as user    &{assessor_credentials}

*** Keywords ***
the assessor fills in all fields
    Select From List By Index    id=rejectReason    2
    The user should not see the text in the page    This field cannot be left blank
    Input Text    id=rejectComment    Unable to assess this application.

the user shouldn't be able to accept the rejected competition
    When the user navigates to the page    ${Invitation_existing_assessor1}
    Then the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user clicks the button/link    jQuery=button:contains("Accept")
    Page Should Contain    Sorry, you are unable to accept this invitation
