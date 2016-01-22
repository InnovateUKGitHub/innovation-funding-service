*** Settings ***
Documentation   INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application, so that i can create the consortium needed to complete the proposed project
Suite Setup     Login as User    &{lead_applicant_credentials}
Suite Teardown  User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot
*** Variables ***
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/1/contributors/invite
*** Test Cases ***
# NOTE: Saving is not actually testable here.  Because we don't have a log of invited organisations and persons yet anywhere
# on the user interface.  All we can test is that the state is saved in cookie, so not lost on page reload.

Link to remove partner organisation
    [Documentation]     INFUND-1039
    [Tags]  Collaboration
    Given the applicant is in the invite contributors page
    And the applicant clicks link "Add partner organisation"
    And the applicant inputs details   1
    And the applicant should see link "Remove"
    When the applicant click on link "Remove"
    Then the organisation section is removed

Applicant inputs Organisation and other details should be autosaved (in cookie)
    [Documentation]     INFUND-1039
    [Tags]  Collaboration
    Given the applicant is in the invite contributors page
    And the applicant clicks link "Add partner organisation"
    And the applicant can enter Organisation name, Name and E-mail
    And reloads the page
    Then the applicant's inputs should be visible

Blank organisation name is not allowed
   [Documentation]      INFUND-896
   [Tags]  Collaboration
   Given the applicant is in the invite contributors page
   And the applicant clicks link "Add partner organisation"
   And the applicant leaves organisation name blank   3
   And the applicant clicks begin application
   Then a validation error is shown on organisation name    3

Blank person name is not allowed
   [Documentation]      INFUND-896
   [Tags]  Collaboration
   Given the applicant is in the invite contributors page
   And the applicant clicks link "Add partner organisation"
   And the applicant leaves person name blank   4
   And the applicant clicks begin application
   Then a validation error is shown on the person name field

Blank email is not allowed
   [Documentation]      INFUND-896
   [Tags]  Collaboration
   Given the applicant is in the invite contributors page
   And the applicant clicks link "Add partner organisation"
   And the applicant leaves email name blank   5
   And the applicant clicks begin application
   Then a validation error is shown on email field

Special characters not allowed in organisation name
   [Documentation]      INFUND-896
   [Tags]  Collaboration    Pending
   Given the applicant is in the invite contributors page
   And the applicant clicks link "Add partner organisation"
   And the applicant inputs organisation name with special characters   6
   And the applicant clicks begin application
   Then a validation error is shown on organisation name    6

Numbers not allowed in organisation name
   [Documentation]      INFUND-896
   [Tags]  Collaboration    Pending
   Given the applicant is in the invite contributors page
   And the applicant clicks link "Add partner organisation"
   And the applicant inputs organisation name with numbers  7
   And the applicant clicks begin application
   Then a validation error is shown on organisation name    7

Special characters not allowed in person name
   [Documentation]      INFUND-896
   [Tags]  Collaboration    Pending
   Given the applicant is in the invite contributors page
   And the applicant clicks link "Add partner organisation"
   And the applicant inputs member name with special characters     8
   And the applicant clicks begin application
   Then a validation error is shown on the person name field

Numbers not allowed in person name
   [Documentation]      INFUND-896
   [Tags]  Collaboration    Pending
   Given the applicant is in the invite contributors page
   And the applicant clicks link "Add partner organisation"
   And the applicant inputs member name with numbers    9
   And the applicant clicks begin application
   Then a validation error is shown on the person name field

Invalid email address is not allowed
   [Documentation]      INFUND-896
   [Tags]  Collaboration
   Given the applicant is in the invite contributors page
   And the applicant clicks link "Add partner organisation"
   And the applicant inputs invalid email address    10
   And the applicant clicks begin application
   Then a validation error is shown on email field

# NOTE: Commenting this one out as it was messing up the counts and other tests as it added this field without removing it
Link to add multiple partner organisation
    [Setup]         Login as User    &{lead_applicant_credentials}
    [Teardown]      User closes the browser
    Given the applicant is in the invite contributors page
    And the applicant should see the link "Add partner organisation"
    And the applicant clicks link "Add partner organisation"
    And the applicant should see another link "Add partner organisation" below the previously clicked partner organisation

*** Keywords ***
Given the applicant is in the invite contributors page
    go to    ${INVITE_COLLABORATORS_PAGE}

the applicant should see the link "Add partner organisation"
    Element Should Be Visible    jquery=li:nth-child(2) button:contains('Add partner organisation')

the applicant should see another link "Add partner organisation" below the previously clicked partner organisation
    Element Should Be Visible    jquery=li:nth-child(3) button:contains('Add partner organisation')

the applicant clicks link "Add partner organisation"
    Click Element    jquery=li:nth-last-child(1) button:contains('Add partner organisation')

the applicant can enter Organisation name, Name and E-mail
    Input Text    name=organisations[1].organisationName    Fannie May
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com
    Click Element    jquery=li:nth-child(2) button:contains('Add person')
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator 3
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(2) input    collaborator3@fanniemay.com
    Click Element    jquery=li:nth-child(2) button:contains('Add person')
    Sleep    2s

reloads the page
    Reload Page

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

the applicant should see link "Remove"
    Element Should Be Visible    jquery=li:nth-child(2) button:contains('Remove')

the applicant click on link "Remove"
    Click Element    jquery=li:nth-child(2) button:contains('Remove')

the organisation section is removed
    Page Should Not Contain    Partner Organisation "

the applicant inputs details
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com

the applicant inputs organisation name with special characters
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie$@! May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 3
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator3@fanniemay.com

the applicant inputs organisation name with numbers
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    123456
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 4
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator4@fanniemay.com

the applicant inputs member name with special characters
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    @$@%@£%
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator5@fanniemay.com

the applicant inputs member name with numbers
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    123456
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator6@fanniemay.com

the applicant inputs invalid email address
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 10
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator10_invalid_email

the applicant leaves organisation name blank
    [Arguments]    ${group_number}
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 7
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator7@fanniemay.com

the applicant leaves person name blank
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName                  Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator8@fanniemay.com

the applicant leaves email name blank
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName                  Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 9

the applicant clicks begin application
    Click Element    jquery=button:contains('Begin application')

a validation error is shown on the person name field
    Wait Until Element Is Visible    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input.field-error

a validation error is shown on email field
    Wait Until Element Is Visible    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input.field-error

a validation error is shown on organisation name
    [Arguments]    ${group_number}
    Wait Until Element Is Visible    css=input[name='organisations[${group_number}].organisationName'].field-error