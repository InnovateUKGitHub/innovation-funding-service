*** Settings ***
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown
Default Tags  Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot
*** Variables ***
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/1/contributors/invite
*** Test Cases ***
Link to add multiple partner organisation
    Given the applicant is in the invite contributors page
    And the applicant should see the link "Add partner organisation"
    And the applicant should see another link "Add partner organisation" below the previously clicked partner organisation
Applicant inputs Organisation and other details should be autosaved
    Given the applicant is in the invite contributors page
    testing
    Then the applicant can enter Organisation name, Name and E-mail"
    And reloads the page
    Then the applicant's inputs should be visible
Link to remove parner organisation
    Given the applicant is in the invite contributors page
    When the applicant clicks link "Add partner organisation"test
    Then the applicant inputs details
    And the applicant should see link "Remove"
    Then the applicant click on link "Remove"
    And the organisation section is removed
*** Keywords ***
Given the applicant is in the invite contributors page
    go to    ${INVITE_COLLABORATORS_PAGE}
the applicant should see the link "Add partner organisation"
    Element Should Be Visible    jquery=li:nth-child(2) button:contains('Add partner organisation')
    Click Element    jquery=li:nth-child(2) button:contains('Add partner organisation')
the applicant should see another link "Add partner organisation" below the previously clicked partner organisation
    Element Should Be Visible    jquery=li:nth-child(3) button:contains('Add partner organisation')
    #Click Element    jquery=li:nth-child(3) button:contains('Add partner organisation')
the applicant clicks link "Add partner organisation"
    Click Element    jquery=li:nth-child(2) button:contains('Add partner organisation')
the applicant can enter Organisation name, Name and E-mail"
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
    #Textfield Value Should Be    id=organisations1.invites0.email    collaborator2@fanniemay.com
    #Textfield Value Should Be    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com
    #${input_value} =    Get Value    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input
    #Should Be Equal As Strings    ${input_value}    collaborator2@fanniemay.com
    Textfield Value Should Be    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator 3
    ${input_value} =    Get Value    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input
    Should Be Equal As Strings    ${input_value}    Collaborator 3
    #Textfield Value Should Be    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(2) input    collaborator3@fanniemay.com
    #${input_value} =    Get Value    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(2) input
    #Should Be Equal As Strings    ${input_value}    collaborator3@fanniemay.com
the applicant should see link "Remove"
    Element Should Be Visible    jquery=li:nth-child(2) button:contains('Remove')
the applicant click on link "Remove"
    #Click Element    jquery=li:nth-child(2) button:contains('Remove')
    #Click Element    jquery=li:nth-child(3) button:contains('Remove')
    Click Link    link=Remove
the organisation section is removed
    Page Should Not Contain    Partner Organisation "
the applicant inputs details
    Input Text    name=organisations[1].organisationName    Fannie May
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com
the applicant clicks link "Add partner organisation"test
    Click Element    jquery=li:nth-child(3) button:contains('Add partner organisation')
testing
    Click Element    jquery=li:nth-child(2) button:contains('Add partner organisation')