*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so that they can contribute to the application in a collaborative competitio...
...
...               INFUND-928: As a lead applicant i want a separate screen within the application form, so that i can invite/track partners/contributors throughout the application process
...
...               INFUND-929: As a lead applicant i want to be able to have a separate screen, so that i can invite contributors to the application
...
...               INFUND-1463: As a user with an invitation to collaborate on an application but not registered with IFS I want to be able to confirm my organisation so that I only have to create my account to work on the application
...
...               INFUND-3742: The overview with contributors is not matching with actual invites
...
...               INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application, so that i can create the consortium needed to complete the proposed project
...
...               INFUND-2375: Error message needed on contributors invite if user tries to add duplicate email address
...
...               INFUND-4807 As an applicant (lead) I want to be able to remove an invited collaborator who is still pending registration so that I can manage members no longer required to be part of the consortium
...
...               INFUND-7974 As a lead applicant I want to edit my organisation
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
    [Tags]    HappyPath
    [Setup]    The user navigates to the page    ${DASHBOARD_URL}
    Given the user clicks the button/link    link=Invite robot test application
    When the user clicks the button/link    link=view and add participants to your application
    Then the user should see the text in the page    Application team
    And the user should see the text in the page    View and manage your contributors and partners in the application.
    And the lead applicant should have the correct status

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
    And the applicant fills the lead organisation fields    ${EMPTY}    @test.co.uk
    And browser validations have been disabled
    And the user clicks the button/link    jQuery=.button:contains("Update organisation")
    Then the user should see an error    Please enter a valid email address.
    And the user should see an error    Please enter a name.

Lead organisation client-side validations
    [Documentation]    INFUND-901
    ...
    ...    INFUND-7974
    [Tags]
    When the applicant fills the lead organisation fields    Florian    florian21@florian.com
    Then the user cannot see a validation error in the page

Autosaved works (in cookie)
    [Documentation]    INFUND-1039
    [Tags]    HappyPath    Pending
    #Pending Infund 8709
    #When The user clicks the button/link    jQuery=a:contains('Add partner organisation')
    #And the applicant can enter Organisation name, Name and E-mail
    When the user reloads the page
    Then the applicant's inputs should be visible

Lead Adds/Removes partner organisation
    [Documentation]    INFUND-1039
    [Tags]    HappyPath
    When The user clicks the button/link    jQuery=a:contains('Add partner organisation')
    #And the applicant inputs details    1
    The user enters text to a text field    name=organisationName    Fannie May
    The user enters text to a text field    name=applicants[0].name    Collaborator 2
    The user enters text to a text field    name=applicants[0].email    ewan+10@hiveit.co.uk
    The user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")
    And the user clicks the button/link    jQuery=a:contains("Update Fannie May")
    When The user clicks the button/link    jQuery=button:contains('Remove')
    And the user clicks the button/link    jQuery=button:contains("Update organisation")
    Then The user should not see the text in the page    Fannie May
    And the user should see the text in the page    Application team

Partner organisation Server-side validations
    [Documentation]    INFUND-896
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains('Add partner organisation')
    When the applicant fills the Partner organisation fields    1    ${EMPTY}    ${EMPTY}    ${EMPTY}
    And browser validations have been disabled
    And the user clicks the button/link    jQuery=.button:contains("Add organisation and invite applicants")
    Then the user should see an error    An organisation name is required.
    And the user should see an error    Please enter a name.
    And the user should see an error    Please enter an email address.

Partner organisation Client-side validations
    When The user enters text to a text field    name=organisationName    Fannie May
    And The user enters text to a text field    name=applicants[0].name    Adrian Booth
    And The user enters text to a text field    name=applicants[0].email    ${test_mailbox_one}+inviteorg${unique_email_number}@gmail.com
    Then the user cannot see a validation error in the page

Valid invitation submit
    [Documentation]    INFUND-901
    [Tags]    HappyPath    SmokeTest
    [Setup]    Delete the emails from both test mailboxes
    When The user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")
    #Then the user should see the element    jQuery=.table-overflow:eq(1) td:nth-child(3):contains("Pending")
    Then the user should see the element    jQuery=.table-overflow tr:contains("Steve Smith") td:nth-child(3):contains("Lead")
    And the user should see the element    jQuery=.table-overflow tr:contains("Adrian Booth") td:nth-child(3):contains("Pending")

The Lead's inputs should not be visible in other application invites
    [Documentation]    INFUND-901
    [Tags]
    When the user navigates to the page    ${INVITE_COLLABORATORS2_PAGE}
    Then the user should not see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input

Pending users visible in the assign list but not clickable
    [Documentation]    INFUND-928
    ...
    ...    INFUND-1962
    [Tags]    HappyPath
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
    When the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+inviteorg1@gmail.com    Invitation to collaborate in ${OPEN_COMPETITION_NAME}    participate in their application
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user enters text to a text field    id=organisationSearchName    Nomensa
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=NOMENSA LTD
    And the user selects the checkbox    address-same
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Confirm and continue")
    And the user fills the create account form    Adrian    Booth
    And the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+inviteorg1@gmail.com    Please verify your email address    If you did not request an account with us
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}

Partner should be able to log-in and see the new company name
    [Documentation]    INFUND-2083
    [Tags]    Email    HappyPath    SmokeTest
    Given the user clicks the button/link    jQuery=.button:contains("Sign in")
    When guest user log-in    ${test_mailbox_one}+inviteorg${unique_email_number}@gmail.com    ${correct_password}
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    And the user can see the updated company name throughout the application

Partner can invite others to his own organisation
    [Documentation]    INFUND-2335
    [Tags]    Email
    When the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    And the user clicks the button/link    link=view and add participants to your application
    And the user clicks the button/link    jQuery=a:contains("Update NOMENSA LTD")
    Then the user can invite another person to their own organisation

Partner cannot invite others to other organisations
    [Documentation]    INFUND-2335
    [Tags]    Email
    Then the user cannot invite another person to a different organisation
    [Teardown]    the user closes the browser

Lead should see the accepted partner in the assign list
    [Documentation]    INFUND-1779
    [Tags]    HappyPath    Email
    [Setup]    Log in as user    &{lead_applicant_credentials}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    And the user clicks the button/link    link=Project summary
    When the user clicks the button/link    css=.assign-button
    Then the user should see the element    jQuery=button:contains("Adrian Booth")
    [Teardown]    Logout as user

Lead should not be able to edit Partners
    [Documentation]    INFUND-929
    [Tags]
    Given guest user log-in    &{lead_applicant_credentials}
    And the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    When the user clicks the button/link    link=view and add participants to your application
    When the user clicks the button/link    jQuery=a:contains("Update Empire Ltd")
    Then the user should see the text in the page    Update Empire Ltd
    #And the invited collaborators are not editable

Lead applicant invites a non registered user in the same organisation
    [Documentation]    INFUND-928, INFUND-1463
    ...    This test checks if the invited partner who are in the same organisation they can go directly to the create account and they don't have to create an organisation first.
    [Tags]
    [Setup]    Delete the emails from both test mailboxes
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    When the user clicks the button/link    link=view and add participants to your application
    When the user clicks the button/link    jQuery=a:contains("Update Empire Ltd")
    Then the user should see the text in the page    Update Empire Ltd
    And the user clicks the button/link    jQuery=button:contains("Add new applicant")
    When the user adds new collaborator
    #And the user clicks the button/link    jQuery=a:contains("Update organisation")
    And the user clicks the button/link    jQuery=button:contains("Update organisation")
    Then the user should see the text in the page    Application team
    And the user should see the text in the page    View and manage your contributors and partners in the application
    [Teardown]    the user closes the browser

Registered partner should not create new org but should follow the create account flow
    [Documentation]    INFUND-1463
    ...    This test checks if the invited partner who are in the same organisation they can go directly to the create account and they don't have to create an organisation first.
    [Tags]    Email
    [Setup]    The guest user opens the browser
    When the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com    Invitation to collaborate in ${OPEN_COMPETITION_NAME}    participate in their application
    And the user should see the text in the page    Join an application
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user should see the text in the page    Your organisation
    And the user should see the text in the page    Business organisation
    And the user should see the element    link=email the lead applicant
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user fills the create account form    Roger    Axe
    And the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com    Please verify your email address    If you did not request an account with us
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}

*** Keywords ***
The lead applicant should have the correct status
    the user should see the element    jQuery=h2:contains("Empire Ltd, Lead organisation")
    the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(1):contains("Steve Smith")
    the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(2):contains("steve.smith@empire.com")
    the user should see the element    jQuery=.table-overflow tr:nth-child(1) td:nth-child(3):contains("Lead")

the user adds new collaborator
    The user enters text to a text field    name= applicants[0].name    Roger Axe
    The user enters text to a text field    name=applicants[0].email    ${test_mailbox_one}+inviteorg2@gmail.com
    focus    jQuery=button:contains('Add new applicant')
    wait for autosave

the applicant cannot assign to pending invitees
    the user clicks the button/link    jQuery=button:contains("Assigned to")
    the user should not see the element    jQuery=button:contains("Adrian Booth")

the status of the people should be correct in the Manage contributors page
    the user should see the element    jQuery=.table-overflow tr:contains("Steve Smith") td:nth-child(3):contains("Lead")
    the user should see the element    jQuery=.table-overflow tr:contains("Adrian Booth") td:nth-child(3):contains("Pending")

the user can see the updated company name throughout the application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${application_name}
    And the user clicks the button/link    link=Your finances
    And the user should see the element    link=Your project costs
    And the user should see the element    link=Your organisation
    And the user should see the element    jQuery=h3:contains("Your funding")
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${application_name}
    When the user clicks the button/link    link=view and add participants to your application
    Then the user should see the element    jQuery=h2:contains("NOMENSA LTD")

the user can invite another person to their own organisation
    the user clicks the button/link    jQuery=button:contains("Add new applicant")
    the user should see the element    jQuery=button:contains("Update organisation")

the user cannot invite another person to a different organisation
    the user should not see the element    jQuery=li:nth-child(1) button:contains("Add another person")

the user navigates to the next question
    The user clicks the button/link    css=.next .pagination-label
    Run Keyword And Ignore Error Without Screenshots    confirm action

the user fills the name and email field and reloads the page
    [Arguments]    ${group_number}
    The user should see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    The user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    The user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    ewan+8@hiveit.co.uk
    wait for autosave
    the user reloads the page

the lead applicant cannot be removed
    the user should see the text in the element    jQuery=tr:nth-of-type(1) td:nth-of-type(3)    Lead
    the user should not see the element    jQuery=#applicant-table tbody > tr:nth-child(1) button:contains("Remove")

the applicant fills the lead organisation fields
    [Arguments]    ${LEAD_NAME}    ${LEAD_EMAIL}
    The user enters text to a text field    jQuery=tr:nth-of-type(2) td:nth-of-type(1) input    ${LEAD_NAME}
    The user enters text to a text field    jQuery=tr:nth-of-type(2) td:nth-of-type(2) input    ${LEAD_EMAIL}
    # the following keyword disables the browser's validation
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Focus    jQuery=.button:contains("Update organisation")
    browser validations have been disabled
    #The user clicks the button/link    jQuery=.button:contains("Update organisation")
    The user clicks the button/link    jQuery=button:contains("Update organisation")

the applicant can enter Organisation name, Name and E-mail
    The user enters text to a text field    name=organisationName    Fannie May
    The user enters text to a text field    name=applicants[0].name    Collaborator 2
    The user enters text to a text field    name=applicants[0].email    ewan+10@hiveit.co.uk
    Focus    jQuery=button:contains('Add new applicant')
    The user clicks the button/link    jQuery=button:contains('Add new applicant')
    The user enters text to a text field    name=applicants[1].name    Collaborator 3
    The user enters text to a text field    name=applicants[1].email    ewan+11@hiveit.co.uk
    Focus    jquery=button:contains("Save changes")
    wait for autosave
    the user reloads the page

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

the applicant inputs details
    [Arguments]    ${group_number}
    The user enters text to a text field    name=organisationName    Fannie May
    The user enters text to a text field    name=applicants[0].name    Collaborator 2
    The user enters text to a text field    name=applicants[0].email    ewan+10@hiveit.co.uk
    The user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")

the applicant fills the Partner organisation fields
    [Arguments]    ${group_number}    ${PARTNER_ORG_NAME}    ${ORG_NAME}    ${EMAIL_NAME}
    browser validations have been disabled
    The user enters text to a text field    name=organisationName    ${PARTNER_ORG_NAME}
    The user enters text to a text field    name=applicants[0].name    ${ORG_NAME}
    The user enters text to a text field    name=applicants[0].email    ${EMAIL_NAME}
    # the following keyword disables the browser's validation
    Focus    jQuery=button:contains("Add organisation and invite applicants")
    The user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")

a validation error is shown on organisation name
    [Arguments]    ${group_number}
    The user should see the element    css=input[name='organisations[${group_number}].organisationName'].field-error

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

The user navigates to the invitation page of the test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invitation page test
    And the user clicks the button/link    link=view and add participants to your application

the invited collaborators are not editable
    the user should see the element    jQuery=li:nth-child(1) tr:nth-of-type(1) td:nth-child(1) [readonly]
    the user should see the element    jQuery=li:nth-child(1) tr:nth-of-type(1) td:nth-child(2) [readonly]
    the user should see the element    jQuery=li:nth-child(2) tr:nth-of-type(1) td:nth-child(1) [readonly]
    the user should see the element    jQuery=li:nth-child(2) tr:nth-of-type(1) td:nth-child(2) [readonly]
