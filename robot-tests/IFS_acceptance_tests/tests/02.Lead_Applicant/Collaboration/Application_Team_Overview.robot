*** Settings ***
Documentation     INFUND-928: As a lead applicant i want a separate screen within the application form, so that i can invite/track partners/contributors throughout the application process
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
The Lead Applicant can view and add details of other partner organisation
    [Documentation]    INFUND-928
    When Applicant goes to the Overview page
    Then Lead Applicant should see the link "View team members and add collaborators"
    And Lead Applicant clicks on the link
    Then Lead Applicant name and organisation should be as Lead Applicant and Lead organisation

The Lead Applicant can send the invitation to collaborators
    [Documentation]    INFUND-928
    When Application goes to the Application Team page
    Then Applicant clicks on "Invite new contributors"
    And the applicant clicks the add person link
    Then the user fills the name and email field
    And the applicant clicks link "Add partner organisation"
    Then the applicant can enter Organisation name, Name and E-mail
    And Applicant clicks on "Save Changes"
    Then Applicant is redirected to Application Team page
    And Applicant can view the status of invitation

The Lead Applicant should not be able to assign a question to more than one collaborator at a time
    [Documentation]    INFUND-928
    [Tags]    Pending


The Lead Applicant should not be able to assign a question to a collaborator still pending their invitation acceptance
    [Documentation]    INFUND-928
    [Tags]    Pending
    Given Applicant goes to the Overview page

*** Keywords ***
Applicant goes to the Overview page
    go to    ${APPLICATION_OVERVIEW_URL}

Lead Applicant should see the link "View team members and add collaborators"
    page should contain link    View team members and add collaborators

Lead Applicant clicks on the link
    click link  View team members and add collaborators
    page should contain    Application team
    #wait until page contains    View and manage your partner companies and individuals contributing to the application. If a partner is ‘pending’ they have not yet confirmed their role within this project. Click on a name to send this person an email. To change the lead applicant for this application please contact Innovate UK : phone 0300 321 4357.

Lead Applicant name and organisation should be as Lead Applicant and Lead organisation
    page should contain element  css=#content h2.heading-medium
    ${input_value} =    get text    css=#content h2.heading-medium
    Should Be Equal As Strings    ${input_value}    Empire Ltd (Lead organisation)
    page should contain link    Steve Smith
    ${input_value} =    get text    css=.list-bullet li small
    Should Be Equal As Strings    ${input_value}    (Lead Applicant)

Applicant clicks on "Invite new contributors"
    click Element    css=a.button
    wait until page contains    Manage Contributors

the applicant clicks the add person link
    Click Element    jquery=li:nth-child(1) button:contains('Add person')

the user fills the name and email field
    Wait Until Element Is Visible    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Roger Axe
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    roger.axe@gmail.com
    focus    jquery=li:nth-child(1) button:contains('Add person')
    sleep    1s

the applicant clicks link "Add partner organisation"
    Click Element    jquery=li:nth-last-child(1) button:contains('Add partner organisation')

the applicant can enter Organisation name, Name and E-mail
    Input Text    name=organisations[1].organisationName    Acme Ltd
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Elvis Furcic
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    elvis.furcic@gmail.com
    Click Element    jquery=li:nth-child(2) button:contains('Add person')
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Christopher Johnson
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(2) input    christopher.johnson@gmail.com
    Click Element    jquery=li:nth-child(2) button:contains('Add person')
    Sleep    2s

Applicant clicks on "Save Changes"
    Click Element    css=.contributorsForm button.button
    Sleep    1s

Applicant is redirected to Application Team page
    Location Should be    ${APPLICATION_TEAM_URL}

Applicant can view the status of invitation
    wait until page contains    Roger Axe (pending)
    page should contain    Acme Ltd (pending)
    page should contain    Elvis Furcic (pending)
    page should contain    Christopher Johnson (pending)