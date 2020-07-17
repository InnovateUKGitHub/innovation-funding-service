*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so...
...
...               INFUND-1463: As a user with an invitation to collaborate on an application but not registered with IFS I want to be able to confirm my organisation ...
...
...               INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application...
...
...               INFUND-2375: Error message needed on contributors invite if user tries to add duplicate email address
...
...               INFUND-4807 As an applicant (lead) I want to be able to remove an invited collaborator who is still pending registration...
...
Suite Setup       Custom Suite Setup
Suite Teardown
Force Tags        Applicant  ATS2020
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${application_name}    Invite robot test application
${newLeadApplicant}    kevin@worth.systems
${newCollaborator}     jerry@worth.systems
${organisation}        org2

*** Test Cases ***
Lead organisation already used email
    [Documentation]  IFS-3361
    [Setup]    The user navigates to the page                   ${APPLICANT_DASHBOARD_URL}
    Given the user clicks the button/link                       link = Invite robot test application
    When the user clicks the button/link                        link = Application team
    And the user clicks the button/link                         jQuery = button:contains("Add person to ${organisation}")
    And the user invites a person to the same organisation      Steve  steve.smith@empire.com
    Then The user should see a field and summary error          This email is already in use.

Valid invitation submit
    [Documentation]    INFUND-901
    [Tags]  HappyPath
    Given the user clicks the button/link         link = Add a partner organisation
    When the user adds a partner organisation     Fannie May  Adrian Booth  ${invite_email}
    And the user clicks the button/link           jQuery = button:contains("Invite partner organisation")
    Then the user should see the element          jQuery = td:contains("Steve") ~ td:contains("Lead")
    And the user should see the element           jQuery = td:contains("Adrian Booth (pending for")

Partner is still marked as pending after accepting invitation but not completing
    [Documentation]  IFS-6589
    [Setup]  Logout as user
    Given the user reads his email and clicks the link     ${invite_email}  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation  2
    When the user accepts invitation
    And the user clicks the button/link                    link = Sign in
    And Logging in and Error Checking                      &{lead_applicant_credentials}
    Then the user still sees pending user
    [Teardown]  the user clicks the button/link            jQuery = td:contains("Adrian") ~ td button:contains("Resend invite")

Business organisation (partner accepts invitation)
    [Documentation]  INFUND-1005 INFUND-2286 INFUND-1779 INFUND-2336
    [Tags]  HappyPath
    Given log in as a different user                       ${invite_email}  ${short_password}
    When the user reads his email and clicks the link      ${invite_email}  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation  2
    And the user accepts invitation
    Then the invited user fills the create account form     Adrian  Booth
    And the user reads his email                           ${invite_email}  Please verify your email address  Once verified you can sign into your account

Partner requests new verification email via password reset
    [Documentation]  IFS-52
    [Tags]  HappyPath
    Given the user navigates to the page         ${LOGIN_URL}
    When the user clicks the forgot psw link
    And the user enters text to a text field     id = email    ${invite_email}
    And the user clicks the button/link          jQuery = #forgotten-password-cta
    Then the user should see the element         jQuery = p:contains("If your email address is recognised and valid, you’ll receive a notification")

Complete account verification
    [Documentation]    INFUND-1005  INFUND-2286  INFUND-1779  INFUND-2336
    [Tags]  HappyPath
    When the user reads his email and clicks the link          ${invite_email}    Please verify your email address    Once verified you can sign into your account  1
    Then the user should be redirected to the correct page     ${REGISTRATION_VERIFIED}

Partner should be able to log-in and see the new company name
    [Documentation]    INFUND-2083  IFS-951
    ...
    ...    INFUND-7976
    [Tags]  HappyPath
    Given the user clicks the button/link                                        link = Sign in
    When the user logs-in in new browser                                         ${invite_email}    ${correct_password}
    Then the user should be redirected to the correct page                       ${APPLICANT_DASHBOARD_URL}
    And the user can see the updated company name throughout the application
    And the user reads his email and clicks the link                             ${invite_email}    Innovate UK applicant questionnaire    diversity survey
    [Teardown]    the user navigates to the page                                 ${APPLICANT_DASHBOARD_URL}

Partner can invite others to his own organisation
    [Documentation]    INFUND-2335  INFUND-7977
    Given the user clicks the button/link                      link = Invite robot test application
    And the user clicks the button/link                        link = Application team
    When the user clicks the button/link                       jQuery = button:contains("Add person to NOMENSA LTD")
    And the user invites a person to the same organisation     Mark  mark21@innovateuk.com
    Then The user should see the element                       jQuery = td:contains("Mark (pending for")

Lead should see the accepted partner in the assign list
    [Documentation]    INFUND-1779
    [Setup]    Log in as a different user    &{lead_applicant_credentials}
    Given the user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    When the user clicks the button/link     link = Invite robot test application
    And the user clicks the button/link      link = Project summary
    And the user clicks the button/link      jQuery = a:contains("Assign to someone else")
    Then the user should see the element     jQuery = label:contains("Adrian Booth")

Lead applicant invites a non registered user in the same organisation
    [Documentation]    INFUND-928  INFUND-1463  INFUND-7979
    [Tags]
    Given the user navigates to the page                        ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link                         link = Invite robot test application
    And the user clicks the button/link                         link = Application team
    And the user clicks the button/link                         jQuery = button:contains("Add person to ${organisation}")
    When the user invites a person to the same organisation     Roger Axe  ${test_mailbox_one}+inviteorg2@gmail.com
    Then the user should see the element                        jQuery = td:contains("Roger Axe (pending for 0 days)") ~ td:contains("${test_mailbox_one}+inviteorg2@gmail.com")

Lead is able to resend invitation
    [Documentation]  IFS-5960
    [Tags]
    Given the user clicks the button/link    jQuery = td:contains("Roger Axe (pending for 0 days)") ~ td button:contains("Resend invite")
    Then the user should see the element     jQuery = td:contains("Roger Axe (pending for 0 days)") ~ td:contains("${test_mailbox_one}+inviteorg2@gmail.com")
    [Teardown]    Logout as user

Registered partner should not create new org but should follow the create account flow
    [Documentation]    INFUND-1463
    [Tags]
    Given team member accepts the invite to join organisation     ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com  ${openCompetitionBusinessRTO_name}  Roger  Axe

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
    Then partner organisation accepts the invite to collaborate     ${newCollaborator}  ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}  ${BUSINESS_TYPE_ID}
    And the lead applicant is no longer directed to the team page

*** Keywords ***
the user can see the updated company name throughout the application
    Given the user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link      link = ${application_name}
    And the user clicks the button/link      link = Your project finances
    And the user should see the element      link = Your project costs
    And the user should see the element      link = Your organisation
    And the user should see the element      jQuery = h3:contains("Your funding")
    Given the user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link      link = ${application_name}
    When the user clicks the button/link     link = Application team
    Then the user should see the element     jQuery = h2:contains("NOMENSA LTD")
    And the user should see the element      jQuery = td:contains("Type")+td:contains("Business")

the user creates an account and signs in
    The user enters the details and clicks the create account     Kevin  FamName  ${newLeadApplicant}  ${correct_password}
    The user reads his email and clicks the link                  ${newLeadApplicant}  Please verify your email address  You have recently set up an account
    The user should be redirected to the correct page             ${REGISTRATION_VERIFIED}
    The user clicks the button/link                               jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")

the lead applicant invites the collaborator
    Logging in and error checking                    ${newLeadApplicant}  ${correct_password}
    The user clicks the button/link                  link = Untitled application (start here)
    the user fills in the inviting steps no Edit     ${newCollaborator}
    The user logs out if they are logged in

the lead applicant is no longer directed to the team page
    Log in as a different user          ${newLeadApplicant}  ${correct_password}
    The user clicks the button/link     jQuery = .progress-list a:contains("Untitled application (start here)")
    The user should see the element     jQuery = h1:contains("Application overview")
    # Added the above check, to see that the user doesn't get directed to the team page (since he has not clicked on the Begin application button)

Custom Suite Setup
    Connect to database  @{database}
    Get competition id and open the competition to live
    log in and create new application if there is not one already     Invite robot test application

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user accepts invitation
    the user clicks the button/link                          jQuery = .govuk-button:contains("Yes, accept invitation")
    the user selects the radio button                        organisationTypeId    1
    the user clicks the button/link                          jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House     Nomensa  NOMENSA LTD

the user still sees pending user
    the user clicks the button/link    link = Invite robot test application
    the user clicks the button/link    link = Application team
    the user should see the element    jQuery = td:contains("Adrian Booth (pending for")

Get competition id and open the competition to live
    #Get competitions id and set it as suite variable  ${openCompetitionBusinessRTO_name}
    ${yesterday} =    get yesterday
    execute sql string  UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`=' ${openCompetitionBusinessRTO}' and type IN ('OPEN_DATE');