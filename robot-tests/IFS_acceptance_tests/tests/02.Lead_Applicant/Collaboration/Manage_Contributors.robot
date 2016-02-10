*** Settings ***
Documentation     INFUND-929: As a lead applicant i want to be able to have a separate screen, so that i can invite contributers to the application
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Create new application    collaboration
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***


*** Test Cases ***
Other than Lead Applicant should not be able to access Manage Contributors page
    [Documentation]    INFUND-929
    [Tags]    pending
    Given Applicant is in Logout page
    When Applicant logs in as Assessor and not Lead Applicant
    And Assessor clicks the competition
    Then Assessor should have the link to view the team members and add collaborators

Only the Lead Applicant should be able to access the Manage Contributors page
    [Documentation]    INFUND-929
    [Tags]
    Given Lead Applicant is in Application Team Page
    When Applicant clicks "Invite new contributors" button

The Lead Applicant should be able to add/remove a collaborator
     [Documentation]    INFUND-929
     [Tags]
     Given Lead Applicant is in the Manage Contributors page
     And the applicant clicks the add person link
     When a new line is added to the collaborator table
     And the applicant clicks the remove link
     Then the line should be removed

Link to remove partner organisation
     [Documentation]    INFUND-1039
     [Tags]
     Given Lead Applicant is in the Manage Contributors page
     And the applicant clicks link "Add partner organisation"
     And the applicant inputs details    1
     And the applicant should see link "Remove"
     When the applicant click on link "Remove"
     Then the organisation section is removed

Verify the invited collaborators are not editable
    [Documentation]    INFUND-929
    [Tags]
    Given Lead Applicant is in the Manage Contributors page
    And the applicant clicks the add person link
    Then the user fills the name and email field
    And the applicant clicks link "Add partner organisation"
    And the applicant can enter Organisation name, Name and E-mail
    And Applicant clicks on "Save Changes"
    Then Applicant is redirected to Application Team page
    And Applicant clicks "Invite new contributors" button
    And the invited collaborators are visible

*** Keywords ***
Lead Applicant is in Application Team Page
    go to    ${APPLICATION_TEAM_URL}

Applicant clicks "Invite new contributors" button
    click element    jQuery=.button:contains("Invite new contributors")
    page should contain    Manage Contributors

Lead Applicant is in the Manage Contributors page
    go to    ${MANAGE_CONTRIBUTORS_URL}

the applicant clicks the add person link
    Click Element    jquery=li:nth-child(1) button:contains('Add person')

the user fills the name and email field
    Wait Until Element Is Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Roger Axe
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    roger.axe@gmail.com
    sleep    1s
    capture page screenshot

the applicant clicks link "Add partner organisation"
    Click Element    jquery=li:nth-last-child(1) button:contains('Add partner organisation')
    capture page screenshot

the applicant can enter Organisation name, Name and E-mail
    Input Text    name=organisations[1].organisationName    Acme Ltd
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Elvis Furcic
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    elvis.furcic@gmail.com
    Click Element    jquery=li:nth-child(2) button:contains('Add person')
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Christopher Johnson
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(2) input    christopher.johnson@gmail.com
    Sleep    1s
    capture page screenshot

Applicant clicks on "Save Changes"
    click element    jQuery=.button:contains("Save Changes")
    Sleep    1s

Applicant is redirected to Application Team page
    Location Should be    ${APPLICATION_TEAM_URL}

the invited collaborators are visible
    #Element Should Contain    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(3)    (pending)
     page should contain element    jQuery=table tr:first-child td:nth-child(1) [readonly]
     page should contain element    jQuery=table tr:first-child td:nth-child(2) [readonly]
     page should contain element    jQuery=table tr:first-child td:nth-child(2) [readonly]
     page should contain element    jQuery=table tr:first-child td:nth-child(2) [readonly]

a new line is added to the collaborator table
    Wait Until Element Is Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)

the applicant clicks the remove link
    Click Element    jquery=li:nth-child(1) button:contains('Remove')
    sleep    1s

the line should be removed
    Element Should Not Be Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)

the applicant inputs details
    [Arguments]    ${group_number}
    Input Text    name=organisations[${group_number}].organisationName    Fannie May
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    Input Text    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com

the applicant should see link "Remove"
    Element Should Be Visible    jquery=li:nth-child(2) button:contains('Remove')

the applicant click on link "Remove"
    Click Element    jquery=li:nth-child(2) button:contains('Remove')

the organisation section is removed
    Page Should Not Contain    Partner Organisation

Applicant is in Logout page
    go to    ${LOG_OUT}

Applicant logs in as Assessor and not Lead Applicant
    Login as user    &{assessor_credentials}

Assessor should have the link to view the team members and add collaborators
    page should not contain link    View team members and add collaborators