*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-304: As an assessor I want to be able to accept the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-3716: As an Assessor when I have accepted to assess within a competition and the assessment period is current, I can see the number of competitions and their titles on my dashboard, so that I can plan my work. \
...               INFUND-3720 As an Assessor I can see deadlines for the assessment of applications currently in assessment on my dashboard, so that I am reminded to deliver my work on time
Suite Setup       log in as user    &{existing_assessor1_credentials}
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
Assessor dashboard should be empty
    [Documentation]    INFUND-3716
    [Tags]    HappyPath
    [Setup]
    Given the user should see the text in the page    Assessor Dashboard
    Then The user should not see the element    css=.my-applications h2
    And The user should not see the text in the page    Competitions for assessment

Existing assessor: Reject invitation
    [Documentation]    INFUND-4631
    [Tags]
    When the user navigates to the page    ${Invitation_existing_assessor2}
    And the user should see the text in the page    Invitation to assess 'Sarcasm Stupendousness'
    And the user clicks the button/link    css=form a
    And The user enters text to a text field    id=rejectComment    a a a a a a a a \ a a a a \ a a a a a a \ a a a a a \ a a a a \ a a a a \ a a a a a a a a a a a \ a a \ a a a a a a a a a a \ a a a a a a a a a a a a a a a a a a a \ a a a a a a a \ a a a \ a a \ aa \ a a a a a a a a a a a a a a \ a
    And the user clicks the button/link    jQuery=button:contains("Reject")
    Then the user should see an error    This field cannot be left blank
    And the user should see an error    This field cannot contain more than 100 words
    And the assessor fills all fields with valid inputs
    And the user clicks the button/link    jQuery=button:contains("Reject")
    And the user should see the text in the page    Thank you for letting us know you are unable to assess applications within this competition.
    And the user shouldn't be able to accept the rejected competition

Existing assessor: Accept invitation
    [Documentation]    INFUND-228
    ...
    ...    INFUND-304
    ...
    ...    INFUND-3716
    [Tags]    HappyPath
    Given the user navigates to the page    ${Invitation_existing_assessor1}
    and the user should see the text in the page    Invitation to assess 'Juggling Craziness'
    And the user should see the text in the page    You are invited to act as an assessor for the competition 'Juggling Craziness'
    When the user clicks the button/link    jQuery=.button:contains("Accept")
    Then The user should see the text in the page    Assessor Dashboard
    And the user should see the element    link=Juggling Craziness

Milestone date for assessment submission is visible
    [Documentation]    INFUND-3720
    [Tags]
    Then the assessor should see the date for submission of assessment

Number of days remaining until assessment submission
    [Documentation]    INFUND-3720
    [Tags]
    Then the assessor should see the number of days remaining
    And the days remaining should be correct

Calculation of the Competitions for assessment should be correct
    [Documentation]    INFUND-3716
    [Tags]
    Then the calculation should be correct    Competitions for assessment    //div[2]/ul/li

Competition link should navigate to the applications
    [Documentation]    INFUND-3716
    [Tags]    HappyPath
    [Setup]    Run Keywords    logout as user
    ...    AND    Log in as user    email=paul.plum@gmail.com    password=Passw0rd    # Note that for this test we want to check what the application list looks like, so we need to log in as a user that has per-existing applications assigned to them
    When The user clicks the button/link    link=Juggling Craziness
    Then The user should see the text in the page    Applications for Assessment
    And the calculation should be correct    Applications for Assessment    //div/form/ul/li

Existing assessor shouldn't be able to accept other assessor's invitation
    [Documentation]    INFUND-228
    ...
    ...    INFUND-304
    [Tags]
    Given the user navigates to the page    ${Invitation_nonexisting_assessor2}
    when the user clicks the button/link    jQuery=button:contains(Accept)
    Then The user should see permissions error message

*** Keywords ***
the assessor fills all fields with valid inputs
    Select From List By Index    id=rejectReason    2
    The user should not see the text in the page    This field cannot be left blank
    Input Text    id=rejectComment    Unable to assess this application.

the user shouldn't be able to accept the rejected competition
    When the user navigates to the page    ${Invitation_existing_assessor2}
    And the user clicks the button/link    jQuery=button:contains("Accept")
    Page Should Contain    You are unable to access this page
    Page Should Contain    You have already rejected the invitation

the calculation should be correct
    [Arguments]    ${TEXT}    ${Section_Xpath}
    [Documentation]    This keyword uses 2 arguments. The first one is about the page's text (competition or application) and the second is about the Xpath selector.
    ${NO_OF_COMP_OR_APPL}=    Get Matching Xpath Count    ${Section_Xpath}
    Page Should Contain    ${TEXT} (${NO_OF_COMP_OR_APPL})

the assessor should see the date for submission of assessment
    the user should see the element    css=.my-applications div:nth-child(2) .competition-deadline .day
    the user should see the element    css=.my-applications div:nth-child(2) .competition-deadline .month

the assessor should see the number of days remaining
    the user should see the element    css=.my-applications div:nth-child(2) .pie-container .pie-overlay .day

the days remaining should be correct
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    2016-12-31    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.my-applications div:nth-child(2) .pie-container .pie-overlay .day
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}
