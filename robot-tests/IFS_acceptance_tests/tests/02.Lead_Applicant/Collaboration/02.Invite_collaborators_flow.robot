*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so that they can contribute to the application in a collaborative competition
...
...
...               INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application, so that i can create the consortium needed to complete the proposed project
...
...
...               INFUND-928: As a lead applicant i want a separate screen within the application form, so that i can invite/track partners/contributors throughout the application process
...
...
...               INFUND-929: As a lead applicant i want to be able to have a separate screen, so that i can invite contributors to the application
...
...
...               INFUND-1815: Small text changes to registration journey following user testing
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Create new application    collaboration
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/1/contributors/invite?newApplication
${INVITE_COLLABORATORS2_PAGE}    ${SERVER}/application/2/contributors/invite?newApplication
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/1/contributors/invite?newApplication
${APPLICATION_TEAM_PAGE}    ${SERVER}/application/1/contributors
${YOUR_FINANCES_URL}    ${SERVER}/application/1/form/section/7

*** Test Cases ***
Valid invitation submit
    [Documentation]    INFUND-901
    [Tags]    HappyPath
    Given user navigates to the page    ${INVITE_COLLABORATORS_PAGE}
    When the applicant enters valid inputs
    Then user should see the text in the page    Application overview
    And the invite notification should be visible

Lead applicant can access the Application team page(Link in the overview page)
    [Documentation]    INFUND-928
    [Tags]    HappyPath
    Given user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    And user should see the text in the page    View team members and add collaborators
    When user clicks the button/link    link=View team members and add collaborators
    Then user should see the text in the page    Application team
    And user should see the text in the page    View and manage your partner companies and individuals contributing to the application. If a partner is ‘pending’ they have not yet confirmed their role within this project. Click on a name to send this person an email
    And Lead Applicant should have the correct status

Status of the invited people(Application team page)
    [Documentation]    INFUND-929
    [Tags]    HappyPath
    Given user navigates to the page    ${APPLICATION_TEAM_PAGE}
    Then The status of the Invited people should be correct in the application team page

Status of the invited people(Manage contributors page)
    [Documentation]    INFUND-928
    [Tags]    HappyPath
    Given user navigates to the page    ${APPLICATION_TEAM_URL}
    When user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    Then user should see the text in the page    Manage Contributors
    And the status of the people should be correct in the Manage contributors page

The Lead Applicant can add new collaborators
    [Documentation]    INFUND-928
    [Tags]    HappyPath
    Given user navigates to the page    ${APPLICATION_TEAM_URL}
    When user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    Then user should see the text in the page    Manage Contributors
    And user clicks the button/link    jquery=li:nth-child(1) button:contains('Add person')
    When the user adds new collaborator
    And the applicant can enter Organisation name, Name and E-mail
    And user clicks the button/link    jquery=button:contains("Save Changes")
    Then user should be redirected to the correct page    ${APPLICATION_TEAM_URL}

Verify the invited collaborators are not editable
    [Documentation]    INFUND-929
    [Tags]
    Given user navigates to the page    ${APPLICATION_TEAM_URL}
    When user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    Then user should see the text in the page    Manage Contributors
    And the invited collaborators are not editable

Pending collaborators should not be available in the assign list
    [Documentation]    INFUND-928
    [Tags]
    Given user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then the applicant should not be able to assign the question to the users that still pending the invite    tester

Organisation name visible in the Finance section
    [Documentation]    INFUND-1815
    [Tags]
    Given user navigates to the page    ${YOUR_FINANCES_URL}
    Then the Organisation name should be seen in the Finance section

*** Keywords ***
the applicant enters valid inputs
    click element    jquery=button:contains('Add person')
    Input Text    css=li:nth-child(1) tr:nth-of-type(3) td:nth-of-type(1) input    tester
    Input Text    css=li:nth-child(1) tr:nth-of-type(3) td:nth-of-type(2) input    test@example.com
    Click Element    jquery=li:nth-last-child(1) button:contains('Add partner organisation')
    Input Text    name=organisations[2].organisationName    Fannie May
    Input Text    css=li:nth-child(3) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    Input Text    css=li:nth-child(3) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com
    Click Element    jquery=li:nth-child(3) button:contains('Add person')
    Input Text    css=li:nth-child(3) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator 3
    Input Text    css=li:nth-child(3) tr:nth-of-type(2) td:nth-of-type(2) input    collaborator3@fanniemay.com
    focus    jquery=li:nth-child(3) button:contains('Add person')
    Sleep    1s
    Click Element    jquery=button:contains("Begin application")

Lead Applicant should have the correct status
    page should contain element    css=#content h2.heading-medium
    ${input_value} =    get text    css=#content h2.heading-medium
    Should Be Equal As Strings    ${input_value}    Empire Ltd (Lead organisation)
    page should contain link    Steve Smith
    ${input_value} =    get text    css=.list-bullet li small
    Should Be Equal As Strings    ${input_value}    (Lead Applicant)

the user adds new collaborator
    Wait Until Element Is Visible    css=li:nth-child(1) tr:nth-of-type(4) td:nth-of-type(1)
    Input Text    css=li:nth-child(1) tr:nth-of-type(4) td:nth-of-type(1) input    Roger Axe
    Input Text    css=li:nth-child(1) tr:nth-of-type(4) td:nth-of-type(2) input    roger.axe@gmail.com
    focus    jquery=li:nth-child(1) button:contains('Add person')
    sleep    1s

the applicant can enter Organisation name, Name and E-mail
    Click Element    jquery=li:nth-last-child(1) button:contains('Add partner organisation')
    Input Text    name=organisations[3].organisationName    Z Ltd
    Input Text    css=li:nth-child(4) tr:nth-of-type(1) td:nth-of-type(1) input    Elvis Furcic
    Input Text    css=li:nth-child(4) tr:nth-of-type(1) td:nth-of-type(2) input    elvis.furcic@gmail.com
    focus    jquery=li:nth-child(2) button:contains('Add person')
    Sleep    2s

The status of the Invited people should be correct in the application team page
    Element Should Contain    css=#content ul li:nth-child(1)    (Lead Applicant)
    Element Should Contain    css=#content ul li:nth-child(2)    (pending)
    Element Should Contain    css=p+ div .heading-medium small    (Lead organisation)
    Element Should Contain    css=div+ div .heading-medium small    (pending)

the invited collaborators are not editable
    #Element Should Contain    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(3)    (pending)
    page should contain element    jQuery=li:nth-child(1) tr:nth-of-type(1) td:nth-child(1) [readonly]
    page should contain element    jQuery=li:nth-child(1) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(2) tr:nth-of-type(1) td:nth-child(1) [readonly]
    page should contain element    jQuery=li:nth-child(2) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(3) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(3) tr:nth-of-type(1) td:nth-child(1) [readonly]

the applicant should not be able to assign the question to the users that still pending the invite
    [Arguments]    ${assignee_name}
    Click Element    css=#form-input-12 .assign-button button
    Page Should Not Contain Element    xpath=//div[@id="form-input-12"]//button[contains(text(),"${assignee_name}")]

the invite notification should be visible
    Wait Until Element Is Visible    css=.event-alert
    page should contain    Invites send

the status of the people should be correct in the Manage contributors page
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(1) td:nth-child(3)    That's you!
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(2) td:nth-child(3)    (pending)
    Element Should Contain    css=li:nth-child(2) tr:nth-of-type(1) td:nth-child(3)    (pending)

the Organisation name should be seen in the Finance section
    page should contain    Provide the project costs for 'Empire Ltd'
    page should contain    'Empire Ltd' Total project costs
