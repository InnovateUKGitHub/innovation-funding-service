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
...
...               INFUND-8590 Lead applicant can Delete a partner Organisation
...
...               IFS-951  Display 'Organisation type' against user
...
...               IFS-1841 Basic view of all 'external' IFS users
#create new competition to test the new application team view.
Suite Setup       Custom Suite Setup
Suite Teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot

*** Variables ***
${application_name}    Invite robot test application
${newLeadApplicant}    kevin@worth.systems
${newCollaborator}     jerry@worth.systems
${organisation}        org2

*** Test Cases ***
Application team page
    [Documentation]    INFUND-928
    ...
    ...    INFUND-7973
    [Tags]
    [Setup]    The user navigates to the page      ${APPLICANT_DASHBOARD_URL}
    Given the user clicks the button/link          link = Invite robot test application
    When the user clicks the button/link           link = Application team
    Then the user should see the element           jQuery = h1:contains("Application team")
    And the lead applicant should have the correct org status
    And the user should see the element            link = Application overview

Lead Adds/Removes rows
    [Documentation]    INFUND-901  INFUND-7974  INFUND-8590
    [Tags]  HappyPath
    [Setup]    The user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    Given the user clicks the button/link         link = Invite robot test application
    When the user clicks the button/link          link = Application team
    And The user clicks the button/link           jquery = button:contains("Add person to ${organisation}")
    And The user should not see the element       jQuery = .modal-delete-organisation button:contains('Delete organisation')
    Then The user should see the element          jQuery = .govuk-table button:contains(Remove)
    And The user clicks the button/link           jQuery = .govuk-table button:contains(Remove)

Lead cannot be removed
    [Documentation]    INFUND-901  INFUND-7974
    [Tags]
    Then the lead applicant cannot be removed

Lead organisation server-side validations
    [Documentation]    INFUND-901  INFUND-7974
    [Tags]
    Given the user clicks the button/link               jQuery = button:contains("Add person to ${organisation}")
    the user invites a person to the same organisation  ${EMPTY}  @test.co.uk
    Then The user should see a field and summary error  Enter an email address in the right format.
    And The user should see a field and summary error   Please enter a name.

Lead organisation client-side validations
    [Documentation]    INFUND-901  INFUND-7974
    [Tags]
    Given the user invites a person to the same organisation  Florian  florian21@florian.com
    Then the user cannot see a validation error in the page

Lead organisation already used email
    [Documentation]  IFS-3361
    Given the user clicks the button/link      jQuery = button:contains("Add person to ${organisation}")
    When the user invites a person to the same organisation  Steve  steve.smith@empire.com
    Then The user should see a field and summary error  This email is already in use.

Lead Adds/Removes partner organisation
    [Documentation]    INFUND-1039
    ...
    ...    INFUND-7973
    ...
    ...    INFUND-7979
    ...
    ...    INFUND-8590
    [Tags]  HappyPath
    Given the user clicks the button/link              link = Add a partner organisation
    And the user adds a partner organisation           Fannie May  Collaborator 2  ewan+10@hiveit.co.uk
    And the user clicks the button/link                jQuery = button:contains("Invite partner organisation")
    When the user clicks the button/link               jQuery = td:contains("ewan") ~ td a:contains("Remove organisation")
    Then the user clicks the button/link               jQuery = tr:contains("ewan") .warning-modal button:contains("Remove organisation")
    And the user should not see the element            jQuery = td:contains("Fannie May")
    And the user should see the element                jQuery = h1:contains("Application team")
    [Teardown]  the user clicks the button/link        link = Add a partner organisation

Partner organisation Server-side validations
    [Documentation]    INFUND-896
    ...
    ...    INFUND-7979
    [Tags]
    Given the user adds a partner organisation            ${EMPTY}  ${EMPTY}  ${EMPTY}
    And the user clicks the button/link                   jQuery = button:contains("Invite partner organisation")
    Then the user should see a field and summary error    Enter an organisation name.
    And the user should see a field and summary error     Please enter a name.
    And the user should see a field and summary error     Enter an email address.

Partner organisation Client-side validations
    [Documentation]    INFUND-7979
    [Tags]  HappyPath
    Given the user adds a partner organisation  Fannie May  Adrian Booth  ${invite_email}
    Then the user cannot see a validation error in the page

Valid invitation submit
    [Documentation]    INFUND-901
    [Tags]  HappyPath
    Given the user clicks the button/link  jQuery = button:contains("Invite partner organisation")
    Then the user should see the element   jQuery = td:contains("Steve") ~ td:contains("Lead")
    And the user should see the element    jQuery = td:contains("Adrian Booth (pending for")

Cannot mark as complete with pending invites
    [Documentation]  IFS-3088
    [Tags]  HappyPath
    Given the user clicks the button/link                 id = application-question-complete
    Then The user should see a field and summary error    You cannot mark as complete until Adrian Booth has either accepted the invitation or is removed

Partner is still marked as pending after accepting invitation but not completing
    [Documentation]  IFS-6589
    [Setup]  Logout as user
    Given the user reads his email and clicks the link   ${invite_email}  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation  2
    When the user accepts invitation
    And the user clicks the button/link     link = Sign in
    And Logging in and Error Checking       &{lead_applicant_credentials}
    Then the user still sees pending user
    [Teardown]  the user clicks the button/link     jQuery = td:contains("Adrian") ~ td button:contains("Resend invite")

The Lead's inputs should not be visible in other application invites
    [Documentation]    INFUND-901
    [Tags]
    Then the user should not see the element  css = li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input

Supprot user should the pending invite on application team
    [Documentation]  IFS-6152
    Given log in as a different user       &{support_user_credentials}
    And the user navigates to the page     ${server}/management/competition/${openCompetitionBusinessRTO}/applications/all
    ${applicationId} =  get application id by name   ${application_name}
    When the user clicks the button/link   link = ${applicationId}
    Then the user navigates to the page    ${server}/management/competition/${openCompetitionBusinessRTO}/application/${applicationId}
    And the user clicks the button/link    id = accordion-questions-heading-1-1
    Then the user should see the element   jQuery = td:contains("Adrian Booth (pending for")

Business organisation (partner accepts invitation)
    [Documentation]  INFUND-1005 INFUND-2286 INFUND-1779 INFUND-2336
    [Tags]  HappyPath
    Given log in as a different user                    ${invite_email}  ${short_password}
    When the user reads his email and clicks the link   ${invite_email}  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation  2
    And the user accepts invitation
    And the invited user fills the create account form  Adrian  Booth
    And the user reads his email                        ${invite_email}  Please verify your email address  Once verified you can sign into your account

Partner requests new verification email via password reset
    [Documentation]  IFS-52
    [Tags]  HappyPath
    Given the user navigates to the page           ${LOGIN_URL}
    When the user clicks the forgot psw link
    And the user enters text to a text field       id = email    ${invite_email}
    And the user clicks the button/link            jQuery = #forgotten-password-cta
    Then the user should see the element           jQuery = p:contains("If your email address is recognised and valid, you’ll receive a notification")

Complete account verification
    [Documentation]    INFUND-1005  INFUND-2286  INFUND-1779  INFUND-2336
    [Tags]  HappyPath
    When the user reads his email and clicks the link       ${invite_email}    Please verify your email address    Once verified you can sign into your account  1
    Then the user should be redirected to the correct page  ${REGISTRATION_VERIFIED}

Partner should be able to log-in and see the new company name
    [Documentation]    INFUND-2083  IFS-951
    ...
    ...    INFUND-7976
    [Tags]  HappyPath
    Given the user clicks the button/link                   link = Sign in
    When the user logs-in in new browser                    ${invite_email}    ${correct_password}
    Then the user should be redirected to the correct page  ${APPLICANT_DASHBOARD_URL}
    And the user can see the updated company name throughout the application
    And the user reads his email and clicks the link        ${invite_email}    Innovate UK applicant questionnaire    diversity survey
    [Teardown]    the user navigates to the page            ${APPLICANT_DASHBOARD_URL}

Partner can see the Application team
    [Documentation]    INFUND-7976
    Given the user clicks the button/link    link = Invite robot test application
    And the user clicks the button/link      link = Application team
    Then the user should see the element     jQuery = td:contains("Steve Smith") ~ td:contains("${lead_applicant}") ~ td:contains("Lead")
    And The user should see the element      link = Application overview
    And The user should not see the element  link = Update and add contributors from ${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}

Partner can invite others to his own organisation
    [Documentation]    INFUND-2335  INFUND-7977
    Given the user clicks the button/link      jQuery = button:contains("Add person to NOMENSA LTD")
    When the user invites a person to the same organisation  Mark  mark21@innovateuk.com
    Then The user should see the element       jQuery = td:contains("Mark (pending for")

Lead should see the accepted partner in the assign list
    [Documentation]    INFUND-1779
    [Setup]    Log in as a different user  &{lead_applicant_credentials}
    Given the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link    link = Invite robot test application
    And the user clicks the button/link    link = Project summary
    And the user clicks the button/link    jQuery = a:contains("Assign to someone else")
    Then the user should see the element   jQuery = label:contains("Adrian Booth")

Lead applicant invites a non registered user in the same organisation
    [Documentation]    INFUND-928  INFUND-1463  INFUND-7979
    [Tags]
    Given the user navigates to the page           ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link            link = Invite robot test application
    And the user clicks the button/link           link = Application team
    And the user clicks the button/link           jQuery = button:contains("Add person to ${organisation}")
    When the user invites a person to the same organisation  Roger Axe  ${test_mailbox_one}+inviteorg2@gmail.com
    Then the user should see the element           jQuery = td:contains("Roger Axe (pending for 0 days)") ~ td:contains("${test_mailbox_one}+inviteorg2@gmail.com")

Lead is able to resend invitation
    [Documentation]  IFS-5960
    [Tags]
    Given the user clicks the button/link    jQuery = td:contains("Roger Axe (pending for 0 days)") ~ td button:contains("Resend invite")
    Then the user should see the element     jQuery = td:contains("Roger Axe (pending for 0 days)") ~ td:contains("${test_mailbox_one}+inviteorg2@gmail.com")
    [Teardown]    Logout as user

Registered partner should not create new org but should follow the create account flow
    [Documentation]    INFUND-1463
    [Tags]
    When the user reads his email and clicks the link      ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com    Invitation to contribute in ${openCompetitionBusinessRTO_name}    You will be joining as part of the organisation    2
    And the user should see the element                    jQuery = h1:contains("Invitation to contribute")
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Yes, accept invitation")
    And the user should see the element                    jQuery = h1:contains("Confirm your organisation")
    And the user should see the element                    link = email the lead applicant
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Confirm and continue")
    And the invited user fills the create account form     Roger  Axe
    And the user reads his email and clicks the link       ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com    Please verify your email address    Once verified you can sign into your account
    And the user should be redirected to the correct page  ${REGISTRATION_VERIFIED}

Lead should not see pending status or resend invite for accepted invite
    [Documentation]    IFS-68  IFS-5960
    [Tags]
    Given the user clicks the button/link       jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    And Logging in and Error Checking           &{lead_applicant_credentials}
    When the user clicks the button/link        link = Invite robot test application
    And the user clicks the button/link         link = Application team
    Then the user should see the element        jQuery = td:contains("${test_mailbox_one}+inviteorg2@gmail.com") ~ td:contains("Remove")
    And The user should not see the element     jQuery = td:contains("Roger Axe (pending for 0 days)") ~ td button:contains("Resend invite")
    [Teardown]  logout as user

The guest user applies to a competition and creates account
    [Documentation]  IFS-2440
    [Tags]
    # Business organisation type - Competition:Aerospace technology investment sector
    Given the user applies to competition and enters organisation type link  ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS}  radio-1
    Then the user creates an account and signs in

New Lead Applicant invites new user as collaborator on his application
    [Documentation]  IFS-2440
    [Tags]
    # Business organisation type for the collaborator as well.
    Given the lead applicant invites the collaborator
    Then the collaborator accepts the invite and is able to see the application without any errors
    And the lead applicant is no longer directed to the team page

*** Keywords ***
The lead applicant should have the correct status
    the user should see the element  jQuery = h2:contains("${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}"):contains("(Lead)")+h3:contains("Organisation type")+p:contains("Business")
    the user should see the element  jQuery = .table-overflow tr:nth-child(1) td:nth-child(1):contains("Steve Smith")
    the user should see the element  jQuery = .table-overflow tr:nth-child(1) td:nth-child(2):contains("${lead_applicant}")
    the user should see the element  jQuery = .table-overflow tr:nth-child(1) td:nth-child(3):contains("Lead")

The lead applicant should have the correct org status
    the user should see the element  jQuery = h2:contains("org2"):contains("(Lead)")+h3:contains("Organisation type")+p:contains("Business")
    the user should see the element  jQuery = td:contains("Steve Smith") ~ td:contains("${lead_applicant}") ~ td:contains("Lead")

the status of the people should be correct in the Manage contributors page
    the user should see the element  jQuery = .table-overflow tr:contains("Steve Smith") td:nth-child(3):contains("Lead")
    the user should see the element  jQuery = .table-overflow tr:contains("Adrian Booth") td:nth-child(3):contains("Invite pending")

the user can see the updated company name throughout the application
    Given the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link   link = ${application_name}
    And the user clicks the button/link   link = Your project finances
    And the user should see the element   link = Your project costs
    And the user should see the element   link = Your organisation
    And the user should see the element   jQuery = h3:contains("Your funding")
    Given the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link   link = ${application_name}
    When the user clicks the button/link  link = Application team
    Then the user should see the element  jQuery = h2:contains("NOMENSA LTD")+h3:contains("Organisation type")+p:contains("Business")

the lead applicant cannot be removed
    the user should see the text in the element  css = tr:nth-of-type(1) td:nth-of-type(3)    Lead
    the user should not see the element          jQuery = #applicant-table tbody > tr:nth-child(1) button:contains("Remove")

the user creates an account and signs in
    The user enters the details and clicks the create account  Kevin  FamName  ${newLeadApplicant}  ${correct_password}
    The user reads his email and clicks the link               ${newLeadApplicant}  Please verify your email address  You have recently set up an account
    The user should be redirected to the correct page          ${REGISTRATION_VERIFIED}
    The user clicks the button/link                            jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")

the lead applicant invites the collaborator
    Logging in and error checking    ${newLeadApplicant}  ${correct_password}
    The user clicks the button/link  link = Untitled application (start here)
    the user fills in the inviting steps no Edit  ${newCollaborator}
    The user logs out if they are logged in

the collaborator accepts the invite and is able to see the application without any errors
    The user reads his email and clicks the link  ${newCollaborator}  Invitation to collaborate in ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}  You are invited by  2
    The user clicks the button/link               jQuery = a:contains("Yes, accept invitation")
    The user should see the element               jQuery = h1:contains("Choose organisation type")
    The user completes the new account creation   ${newCollaborator}  ${BUSINESS_TYPE_ID}
    The user clicks the button/link               jQuery = .progress-list a:contains("Untitled application (start here)")
    The user should not see an error in the page

the lead applicant is no longer directed to the team page
    Log in as a different user       ${newLeadApplicant}  ${correct_password}
    The user clicks the button/link  jQuery = .progress-list a:contains("Untitled application (start here)")
    The user should see the element  jQuery = h1:contains("Application overview")
    # Added the above check, to see that the user doesn't get directed to the team page (since he has not clicked on the Begin application button)

Custom Suite Setup
    Connect to database  @{database}
    log in and create new application if there is not one already  Invite robot test application

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user accepts invitation
    the user clicks the button/link                       jQuery = .govuk-button:contains("Yes, accept invitation")
    the user selects the radio button                     organisationType    1
    the user clicks the button/link                       jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House  Nomensa  NOMENSA LTD

the user still sees pending user
    the user clicks the button/link    link = Invite robot test application
    the user clicks the button/link    link = Application team
    the user should see the element    jQuery = td:contains("Adrian Booth (pending for")