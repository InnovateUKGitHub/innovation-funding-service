*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so that they can contribute to the application in a collaborative competition
...
...
...               INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application, so that i can create the consortium needed to complete the proposed project
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Create new application    collaboration
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/2/contributors/invite?newApplication
${INVITE_COLLABORATORS2_PAGE}    ${SERVER}/application/3/contributors/invite?newApplication
${APPLICATION_3_TEAM_PAGE}    ${SERVER}/application/3/contributors

*** Test Cases ***
The lead applicant should be able to add/remove a collaborator
    [Documentation]    INFUND-901
    [Tags]    HappyPath
    Given the user navigates to the page    ${INVITE_COLLABORATORS_PAGE}
    And The user clicks the button/link    jquery=li:nth-child(1) button:contains('Add person')
    When The user should see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    And The user clicks the button/link    jquery=li:nth-child(1) button:contains('Remove')
    Then The user should not see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)

The lead applicant shouldn't be able to remove himself
    [Documentation]    INFUND-901
    Given the user navigates to the page    ${INVITE_COLLABORATORS_PAGE}
    Then the lead applicant cannot be removed

Validations for the Email field user remains in the invite page
    [Documentation]    INFUND-901
    [Tags]
    When The user clicks the button/link    jquery=li:nth-child(1) button:contains('Add person')
    And the applicant enters some invalid emails
    Then The user should see the text in the page    Inviting Contributors

Validation for the name field user remains in the invite page
    [Documentation]    INFUND-901
    [Tags]
    When the applicant submits the page without entering a name
    Then The user should see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) .field-error
    And The user should see the text in the page    Inviting Contributors

Link to remove partner organisation
    [Documentation]    INFUND-1039
    [Tags]    Collaboration
    # on the user interface.    All we can test is that the state is saved in cookie, so not lost on page reload.
    When The user clicks the button/link    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    And the applicant inputs details    1
    Then The user should see the element    jquery=li:nth-child(2) button:contains('Remove')
    When The user clicks the button/link    jquery=li:nth-child(2) button:contains('Remove')
    Then The user should not see the text in the page    Organisation name

Applicant inputs Organisation and other details should be autosaved (in cookie)
    [Documentation]    INFUND-1039
    [Tags]    Collaboration    HappyPath
    When The user clicks the button/link    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    And the applicant can enter Organisation name, Name and E-mail
    Then the applicant's inputs should be visible

Blank organisation name is not allowed
    [Documentation]    INFUND-896
    [Tags]    Collaboration
    And the applicant leaves organisation name blank    1
    And The user clicks the button/link    jquery=button:contains('Begin application')
    Then a validation error is shown on organisation name    1

Blank person name is not allowed
    [Documentation]    INFUND-896
    [Tags]    Collaboration
    When the applicant leaves person name blank    1
    And The user clicks the button/link    jquery=button:contains('Begin application')
    #user should get validation error
    Then The user should see the element    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input.field-error

Blank email is not allowed
    [Documentation]    INFUND-896
    [Tags]    Collaboration
    When the applicant leaves email name blank    1
    And The user clicks the button/link    jquery=button:contains('Begin application')
    #user should get validation error
    Then The user should see the element    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input.field-error

Invalid email address is not allowed
    [Documentation]    INFUND-896
    [Tags]    Collaboration
    And the applicant inputs invalid email address    1
    And The user clicks the button/link    jquery=button:contains('Begin application')
    #user should get validation error
    Then The user should see the element    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input.field-error

Already invite email should not allowed
    When the applicant inserts and already invited email    1
    And The user clicks the button/link    jquery=button:contains('Begin application')
    #user should get validation error
    Then The user should see the element    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input.field-error

Link to add multiple partner organisation
    [Tags]    Failing
    When The user clicks the button/link    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    And The user should see the element    css=li:nth-child(3)
    And The user clicks the button/link    jQuery=li:nth-child(3) button:contains("Remove")
    Then The user should not see the element    jQuery=li:nth-child(3) button:contains("Remove")

The user's inputs should be autosaved
    [Documentation]    INFUND-901
    When the user fills the name and email field and reloads the page    1
    Then the user's inputs should still be visible    1
    And the user navigates to the page    ${INVITE_COLLABORATORS2_PAGE}
    And the inputs of the first invite should not be visible
    And the user goes to the application team page of application 3
    And the inputs of the first invite should not be visible

*** Keywords ***
the user fills the name and email field and reloads the page
    [Arguments]    ${group_number}
    Wait Until Element Is Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    tester@test.com
    Input Text    name=organisations[${group_number}].organisationName    Test name
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator test
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaboratortest@fanniemay.com
    sleep    1s
    focus    jquery=li:nth-child(1) button:contains('Add person')
    reload page

the user's inputs should still be visible
    [Arguments]    ${group_number}
    Textfield Value Should Be    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    ${input_value} =    Get Value    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input
    Should Be Equal As Strings    ${input_value}    tester@test.com
    Textfield Value Should Be    name=organisations[${group_number}].organisationName    Test name
    Textfield Value Should Be    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator test
    ${input_value} =    Get Value    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input
    Should Be Equal As Strings    ${input_value} =    ${input_value} =

the lead applicant cannot be removed
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(1) td:nth-of-type(3)    That's you!

the inputs of the first invite should not be visible
    Element Should Not Be Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input
    Page Should Not Contain    Collaborator01
    Page Should Not Contain    tester@test.com
    Page Should Not Contain    Test name
    Page Should Not Contain    Collaborator test

the applicant enters some invalid emails
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    @example.com
    # the following keyword disables the browser's validation
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Click Element    jquery=button:contains("Begin application")
    sleep    1s

the applicant submits the page without entering a name
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    ${EMPTY}
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    test@example.com
    Click Element    jquery=button:contains("Begin application")
    sleep    1s

the applicant can enter Organisation name, Name and E-mail
    Input Text    name=organisations[1].organisationName    Fannie May
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com
    Click Element    jquery=li:nth-child(2) button:contains('Add person')
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator 3
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(2) input    collaborator3@fanniemay.com
    focus    jquery=li:nth-child(2) button:contains('Add person')
    Sleep    1s
    reload page

the applicant's inputs should be visible
    Textfield Value Should Be    name=organisations[1].organisationName    Fannie May
    ${input_value} =    Get Value    name=organisations[1].organisationName
    Should Be Equal As Strings    ${input_value}    Fannie May
    Textfield Value Should Be    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    ${input_value} =    Get Value    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input
    Should Be Equal As Strings    ${input_value}    Collaborator 2
    Textfield Value Should Be    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator 3
    ${input_value} =    Get Value    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input
    Should Be Equal As Strings    ${input_value}    Collaborator 3

the applicant inputs details
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com

the applicant inputs invalid email address
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 10
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator10_invalid_email
    # the following keyword disables the browser's validation
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');

the applicant leaves organisation name blank
    [Arguments]    ${group_number}
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 7
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator7@fanniemay.com
    Clear Element Text    name=organisations[${group_number}].organisationName

the applicant leaves person name blank
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator8@fanniemay.com
    Clear Element Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input

the applicant leaves email name blank
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 9
    Clear Element Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input

a validation error is shown on organisation name
    [Arguments]    ${group_number}
    Wait Until Element Is Visible    css=input[name='organisations[${group_number}].organisationName'].field-error

the applicant inserts and already invited email
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 10
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    test@example.com
    # the following keyword disables the browser's validation
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');

the user goes to the application team page of application 3
    the user navigates to the page    ${APPLICATION_3_TEAM_PAGE}
    Click Element    jQuery=.button:contains("Invite new contributors")
