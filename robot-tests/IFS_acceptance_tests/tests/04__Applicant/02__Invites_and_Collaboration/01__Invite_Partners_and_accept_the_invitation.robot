*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so...
...
...               INFUND-928: As a lead applicant i want a separate screen within the application form...
...
...               INFUND-929: As a lead applicant i want to be able to have a separate screen...
...
...               INFUND-1463: As a user with an invitation to collaborate on an application but not registered with IFS I want to be able to confirm my organisation ...
...
...               INFUND-3742: The overview with contributors is not matching with actual invites
...
...               INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application...
...
...               INFUND-2375: Error message needed on contributors invite if user tries to add duplicate email address
...
...               INFUND-4807 As an applicant (lead) I want to be able to remove an invited collaborator who is still pending registration...
...
...               INFUND-7974 As a lead applicant I want to edit my organisation
...
...               INFUND-7973 As a lead applicant I want to view my application team
...
...               INFUND-7979 As an lead applicant I want to add a new organisation
...
...               INFUND-7977 As a non lead applicant I want to edit my application team
Suite Setup       log in and create new application for collaboration if there is not one already
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${application_name}    Invite robot test application
${INVITE_COLLABORATORS2_PAGE}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_3}/contributors/invite?newApplication

*** Test Cases ***
Application team page
    [Documentation]    INFUND-928
    ...
    ...    INFUND-7973
    [Tags]    HappyPath
    [Setup]    The user navigates to the page    ${DASHBOARD_URL}
    Given the user clicks the button/link    link=Invite robot test application
    When the user clicks the button/link    link=view team members and add collaborators
    Then the user should see the text in the page    Application team
    And the user should see the text in the page    View and manage your participants in the application.
    And the lead applicant should have the correct status
    And the user should see the element    link=Application overview

Lead Adds/Removes rows
    [Documentation]    INFUND-901
    ...
    ...    INFUND-7974
    [Tags]    HappyPath
    When The user clicks the button/link    jquery=a:contains("Update Empire Ltd")
    And the user clicks the button/link    jQuery=button:contains("Add new applicant")
    Then The user should see the element    jQuery=.table-overflow tr:nth-of-type(2) td:nth-of-type(1)
    And The user clicks the button/link    jQuery=button:contains('Remove')
    Then The user should not see the element    jQuery=.table-overflow tr:nth-of-type(2) td:nth-of-type(1)

Lead cannot be removed
    [Documentation]    INFUND-901
    ...
    ...    INFUND-7974
    [Tags]
    Then the lead applicant cannot be removed

Lead organisation server-side validations
    [Documentation]    INFUND-901
    ...
    ...    INFUND-7974
    [Tags]    HappyPath
    When The user clicks the button/link    jQuery=button:contains("Add new applicant")
    And The user enters text to a text field    jQuery=tr:nth-of-type(2) td:nth-of-type(1) input    ${EMPTY}
    And The user enters text to a text field    jQuery=tr:nth-of-type(2) td:nth-of-type(2) input    @test.co.uk
    And browser validations have been disabled
    And the user clicks the button/link    jQuery=.button:contains("Update organisation")
    Then the user should see an error    Please enter a valid email address.
    And the user should see an error    Please enter a name.

Lead organisation client-side validations
    [Documentation]    INFUND-901
    ...
    ...    INFUND-7974
    [Tags]    HappyPath
    When The user enters text to a text field    jQuery=tr:nth-of-type(2) td:nth-of-type(1) input    Florian
    And The user enters text to a text field    jQuery=tr:nth-of-type(2) td:nth-of-type(2) input    florian21@florian.com
    Then the user cannot see a validation error in the page
    [Teardown]    The user clicks the button/link    link=Application team

Lead Adds/Removes partner organisation
    [Documentation]    INFUND-1039
    ...
    ...    INFUND-7973
    ...
    ...    INFUND-7979
    [Tags]    HappyPath
    When The user clicks the button/link    jQuery=a:contains('Add partner organisation')
    And The user enters text to a text field    name=organisationName    Fannie May
    And The user enters text to a text field    name=applicants[0].name    Collaborator 2
    And The user enters text to a text field    name=applicants[0].email    ewan+10@hiveit.co.uk
    And The user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")
    And the user clicks the button/link    jQuery=a:contains("Update Fannie May")
    Then The user clicks the button/link    jQuery=a:contains('Delete organisation')
    And The user clicks the button/link     jQuery=.modal-delete-organisation button:contains('Delete organisation')
    Then The user should not see the text in the page    Fannie May
    And the user should see the text in the page    Application team

Partner organisation Server-side validations
    [Documentation]    INFUND-896
    ...
    ...    INFUND-7979
    [Tags]    HappyPath
    Given the user clicks the button/link    jQuery=a:contains('Add partner organisation')
    When The user enters text to a text field    name=organisationName    ${EMPTY}
    And The user enters text to a text field    name=applicants[0].name    ${EMPTY}
    And The user enters text to a text field    name=applicants[0].email    ${EMPTY}
    And browser validations have been disabled
    And the user clicks the button/link    jQuery=.button:contains("Add organisation and invite applicants")
    Then the user should see an error    An organisation name is required.
    And the user should see an error    Please enter a name.
    And the user should see an error    Please enter an email address.

Partner organisation Client-side validations
    [Documentation]    INFUND-7979
    [Tags]    HappyPath
    When The user enters text to a text field    name=organisationName    Fannie May
    And The user enters text to a text field    name=applicants[0].name    Adrian Booth
    And The user enters text to a text field    name=applicants[0].email    ${test_mailbox_one}+inviteorg${unique_email_number}@gmail.com
    Then the user cannot see a validation error in the page

Valid invitation submit
    [Documentation]    INFUND-901
    [Tags]    HappyPath    SmokeTest
    [Setup]    Delete the emails from both test mailboxes
    When The user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")
    Then the user should see the element    jQuery=.table-overflow tr:contains("Steve Smith") td:nth-child(3):contains("Lead")
    And the user should see the element    jQuery=.table-overflow tr:contains("Adrian Booth") td:nth-child(3):contains("Invite pending")

The Lead's inputs should not be visible in other application invites
    [Documentation]    INFUND-901
    [Tags]
    When the user navigates to the page    ${INVITE_COLLABORATORS2_PAGE}
    Then the user should not see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input

Pending users visible in the assign list but not clickable
    [Documentation]    INFUND-928
    ...
    ...    INFUND-1962
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    And the user clicks the button/link    link=Project summary
    Then the applicant cannot assign to pending invitees
    And the user should see the text in the page    Adrian Booth (pending)
    [Teardown]    Logout as user

Business organisation (partner accepts invitation)
    [Documentation]    INFUND-1005
    ...    INFUND-2286
    ...    INFUND-1779
    ...    INFUND-2336
    [Tags]    HappyPath    Email    SmokeTest
    [Setup]    The guest user opens the browser
    When the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+inviteorg1@gmail.com    Invitation to collaborate in ${OPEN_COMPETITION_NAME}    You will be joining as part of the organisation    3
    And the user clicks the button/link    jQuery=.button:contains("Yes, accept invitation")
    And the user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user enters text to a text field    id=organisationSearchName    Nomensa
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=NOMENSA LTD
    And the user selects the checkbox    address-same
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Confirm")
    And the user fills the create account form    Adrian    Booth
    And the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+inviteorg1@gmail.com    Please verify your email address    Once verified you can sign into your account
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}

Partner should be able to log-in and see the new company name
    [Documentation]    INFUND-2083
    ...
    ...    INFUND-7976
    [Tags]    Email    HappyPath    SmokeTest
    Given the user clicks the button/link    jQuery=.button:contains("Sign in")
    When guest user log-in    ${test_mailbox_one}+inviteorg${unique_email_number}@gmail.com    ${correct_password}
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    And the user can see the updated company name throughout the application

Parner can see the Application team
    [Documentation]    INFUND-7976
    When the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    And the user clicks the button/link    link=view team members and add collaborators
    Then the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(1):contains("Steve Smith")
    And the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(2):contains("steve.smith@empire.com")
    And the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(3):contains("Lead")
    And The user should see the element    link=Application overview
    And The user should not see the element    link=Update Empire Ltd

Partner can invite others to his own organisation
    [Documentation]    INFUND-2335
    ...
    ...    INFUND-7977
    [Tags]    Email
    When the user clicks the button/link    jQuery=a:contains("Update NOMENSA LTD")
    And the user clicks the button/link    jQuery=button:contains("Add new applicant")
    And The user enters text to a text field    jQuery=tr:nth-of-type(2) td:nth-of-type(1) input    Mark
    And The user enters text to a text field    jQuery=tr:nth-of-type(2) td:nth-of-type(2) input    mark21@innovateuk.com
    And the user clicks the button/link    jQuery=button:contains("Update organisation")
    Then The user should see the element    jQuery=td:contains("mark21@innovateuk.com") + td:contains("Invite pending")

Lead should see the accepted partner in the assign list
    [Documentation]    INFUND-1779
    [Tags]    HappyPath    Email
    [Setup]    Log in as a different user    &{lead_applicant_credentials}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    And the user clicks the button/link    link=Project summary
    When the user clicks the button/link    css=.assign-button > button
    Then the user should see the element    jQuery=button:contains("Adrian Booth")

Lead applicant invites a non registered user in the same organisation
    [Documentation]    INFUND-928
    ...
    ...    INFUND-1463
    ...
    ...    INFUND-7979
    [Tags]
    [Setup]    Delete the emails from both test mailboxes
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    When the user clicks the button/link    link=view team members and add collaborators
    When the user clicks the button/link    jQuery=a:contains("Update Empire Ltd")
    Then the user should see the text in the page    Update Empire Ltd
    And the user clicks the button/link    jQuery=button:contains("Add new applicant")
    When The user enters text to a text field    name= applicants[0].name    Roger Axe
    And The user enters text to a text field    name=applicants[0].email    ${test_mailbox_one}+inviteorg2@gmail.com
    And the user clicks the button/link    jQuery=button:contains("Update organisation")
    Then the user should see the text in the page    Application team
    And the user should see the text in the page    View and manage your participants in the application
    [Teardown]    the user closes the browser

Registered partner should not create new org but should follow the create account flow
    [Documentation]    INFUND-1463
    [Tags]    Email
    [Setup]    The guest user opens the browser
    When the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com    Invitation to collaborate in ${OPEN_COMPETITION_NAME}    You will be joining as part of the organisation    3
    And the user should see the text in the page    Join an application
    And the user clicks the button/link    jQuery=.button:contains("Yes, accept invitation")
    And the user should see the text in the page    Confirm your organisation
    And the user should see the element    link=email the lead applicant
    And the user clicks the button/link    jQuery=.button:contains("Confirm and continue")
    And the user fills the create account form    Roger    Axe
    And the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com    Please verify your email address    Once verified you can sign into your account
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}

*** Keywords ***
The lead applicant should have the correct status
    the user should see the element    jQuery=h2:contains("Empire Ltd"):contains("(Lead)")
    the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(1):contains("Steve Smith")
    the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(2):contains("steve.smith@empire.com")
    the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(3):contains("Lead")

the applicant cannot assign to pending invitees
    the user clicks the button/link    jQuery=button:contains("Assign this question to someone else")
    the user should not see the element    jQuery=button:contains("Adrian Booth")

the status of the people should be correct in the Manage contributors page
    the user should see the element    jQuery=.table-overflow tr:contains("Steve Smith") td:nth-child(3):contains("Lead")
    the user should see the element    jQuery=.table-overflow tr:contains("Adrian Booth") td:nth-child(3):contains("Invite pending")

the user can see the updated company name throughout the application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${application_name}
    And the user clicks the button/link    link=Your finances
    And the user should see the element    link=Your project costs
    And the user should see the element    link=Your organisation
    And the user should see the element    jQuery=h3:contains("Your funding")
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${application_name}
    When the user clicks the button/link    link=view team members and add collaborators
    Then the user should see the element    jQuery=h2:contains("NOMENSA LTD")

the lead applicant cannot be removed
    the user should see the text in the element    jQuery=tr:nth-of-type(1) td:nth-of-type(3)    Lead
    the user should not see the element    jQuery=#applicant-table tbody > tr:nth-child(1) button:contains("Remove")

the applicant's inputs should be visible
    Textfield Value Should Be    name=organisations[1].organisationName    Fannie May
    ${input_value} =    Get Value    name=organisationName
    Should Be Equal As Strings    ${input_value}    Fannie May
    Textfield Value Should Be    name=applicants[0].name    Collaborator 2
    ${input_value} =    Get Value    name=applicants[0].name
    Should Be Equal As Strings    ${input_value}    Collaborator 2
    Textfield Value Should Be    name=applicants[1].name    Collaborator 3
    ${input_value} =    Get Value    name=applicants[1].name
    Should Be Equal As Strings    ${input_value}    Collaborator 3

Login and create a new application
    Given Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jquery=a:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Invitation page test
    And the user clicks the button/link    jQuery=button:contains("Save and return")
