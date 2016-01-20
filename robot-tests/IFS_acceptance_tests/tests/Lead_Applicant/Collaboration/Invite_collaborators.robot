*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so that they can contribute to the application in a collaborative competition
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Create new application    collaboration
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/1/contributors/invite
${INVITE_COLLABORATORS2_PAGE}    ${SERVER}/application/2/contributors/invite

*** Test Cases ***
The lead applicant should be able to add a collaborator
    [Documentation]    INFUND-901
    Given the applicant is in the invite contributors page
    When the applicant clicks the add person link
    Then a new line should be added
    and when the applicant clicks the remove link
    Then the line should be removed

The user's inputs should be autosaved
    [Documentation]    INFUND-901
    Given the applicant is in the invite contributors page
    And the applicant clicks the add person link
    When the user fils the mane and email field
    And reloads the page
    The users inputs should still be visible
    and when the user goes to the second invite page
    Then the inputs of the first invite should not be visible

The lead applicant shouldn't be able to remove himself
    [Documentation]    INFUND-901
    Given the applicant is in the invite contributors page
    Then the lead applicant should not be able to to be removed

Validations for the Email field
    [Documentation]    INFUND-901
    Given the applicant is in the invite contributors page
    When the applicant enters some invalid emails
    Then the applicant should not redirected to the next page

Validation for the name field
    [Documentation]    INFUND-901
    Given the applicant is in the invite contributors page
    When the applicant submit the page without to enter a name
    Then the applicant should get a vallidation error for the name field

Valid submit
    [Documentation]    INFUND-901
    Given the applicant is in the invite contributors page
    When the applicant enters valid inputs
    Then the applicant should be redirected to the overview page

*** Keywords ***
the applicant is in the invite contributors page
    go to    ${INVITE_COLLABORATORS_PAGE}

the applicant clicks the add person link
    Click Element    jquery=li:nth-child(1) button:contains('Add person')

a new line should be added
    Wait Until Element Is Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)

when the applicant clicks the remove link
    Click Element    jquery=li:nth-child(1) button:contains('Remove')
    sleep    1s

the line should be removed
    Element Should Not Be Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)

the user fils the mane and email field
    Wait Until Element Is Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    tester@test.com
    Click Element    jquery=li:nth-child(1) button:contains('Add person')
    sleep    1s

reloads the page
    Reload Page

The users inputs should still be visible
    Textfield Value Should Be    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    #Element Should Contain    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    tester@test.com
    ${input_value} =    Get Value    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input
    Should Be Equal As Strings    ${input_value}    tester@test.com

the lead applicant should not be able to to be removed
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(1) td:nth-of-type(3)    That's you!

when the user goes to the second invite page
    go to    ${INVITE_COLLABORATORS2_PAGE}

the inputs of the first invite should not be visible
    Element Should Not Be Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input

the applicant enters some invalid emails
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    @example.com
    Click Element    jquery=button:contains("Begin application")
    sleep    1s

the applicant should not redirected to the next page
    Location Should Be    ${INVITE_COLLABORATORS_PAGE}

the applicant submit the page without to enter a name
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    ${EMPTY}
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    test@example.com
    Click Element    jquery=button:contains("Begin application")
    sleep    1s

the applicant should get a vallidation error for the name field
    Element Should Be Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) .error

the applicant enters valid inputs
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    tester
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    test@example.com
    Click Element    jquery=button:contains("Begin application")
    sleep    1s

the applicant should be redirected to the overview page
    Page Should Contain    Application overview
