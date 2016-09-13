*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-304: As an assessor I want to be able to accept the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-3716: As an Assessor when I have accepted to assess within a competition and the assessment period is current, I can see the number of competitions and their titles on my dashboard, so that I can plan my work.
Suite Setup
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
${Invitation_existing_assessor2}    ${server}/assessment/invite/competition/469ffd4952ce0a4c310ec09a1175fb5abea5bc530c2af487f32484e17a4a3776c2ec430f3d957471
${Invitation_nonexisting_assessor2}    ${server}/assessment/invite/competition/2abe401d357fc486da56d2d34dc48d81948521b372baff98876665f442ee50a1474a41f5a0964720 #invitation for assessor:worth.email.test+assessor2@gmail.com

*** Test Cases ***
Verify number of competitions for assessment updates: Initially there will be none
    [Documentation]    INFUND-4219
    [Tags]
    [Setup]    log in as user    &{existing_assessor1_credentials}
    When the user navigates to the page    ${Assessor_competition_dashboard}
    Then the user should see the text in the page    Assessor Dashboard
    And The user should not see the element    css=.my-applications h2

Existing assessor: Reject invitation
    [Documentation]    INFUND-4631
    [Tags]
    When the user navigates to the page    ${Invitation_existing_assessor2}
    Then the user should see the text in the page    Invitation to assess 'Sarcasm Stupendousness'
    And the user clicks the button/link    css=form a
    When the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see an error    This field cannot be left blank
    And the assessor fills in all fields
    And the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see the text in the page    Thank you for letting us know you are unable to assess applications within this competition.
    And the user shouldn't be able to accept the rejected competition

Existing assessor: Accept invitation
    [Documentation]    INFUND-228
    ...
    ...    INFUND-304
    [Tags]
    Given the user navigates to the page    ${Invitation_existing_assessor1}
    and the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user should see the text in the page    You are invited to act as an assessor for the competition 'Juggling Craziness'
    When the user clicks the button/link    jQuery=.button:contains("Accept")
    Then The user should see the text in the page    Assessor Dashboard
    And the user should see the element    link=Juggling Craziness

Verify number of competitions for assessment updates: blank to 1
    [Documentation]    INFUND-4219
    [Tags]
    Then the number of accepted for assessment is displayed in the dashboard
    And the title of the competition accepted for assessment is displayed

Existing assessor shouldn't be able to accept other assessor's invitation
    [Documentation]    INFUND-228
    ...
    ...    INFUND-304
    [Tags]
    Given the user navigates to the page    ${Invitation_nonexisting_assessor2}
    when the user clicks the button/link    jQuery=button:contains(Accept)
    Then The user should see permissions error message

Existing assessor shouldn't be able to reject other assessor's invitation
    [Documentation]    INFUND-4631
    [Tags]    Pending
    # Pending INFUND-5003
    Given the user navigates to the page    ${Invitation_nonexisting_assessor2}
    when the user clicks the button/link    css=form a
    And the assessor fills in all fields
    And the user clicks the button/link    jQuery=button:contains("Reject")
    Then The user should see permissions error message

*** Keywords ***
the assessor fills in all fields
    Select From List By Index    id=rejectReason    2
    The user should not see the text in the page    This field cannot be left blank
    Input Text    id=rejectComment    Unable to assess this application.

the user shouldn't be able to accept the rejected competition
    When the user navigates to the page    ${Invitation_existing_assessor2}
    And the user clicks the button/link    jQuery=button:contains("Accept")
    Page Should Contain    Sorry, you are unable to accept this invitation

the number of accepted for assessment is displayed in the dashboard
    The user should see the text in the page    Competitions for assessment (1)
    the user should see the element    css=.my-applications h2

the title of the competition accepted for assessment is displayed
    The user should see the text in the page    Juggling Craziness
    the user clicks the button/link    link=Juggling Craziness
    The user is on the page    ${Assessor_application_dashboard}
