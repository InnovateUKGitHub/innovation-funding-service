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
Suite Setup       log in and create new application if there is not one already  Invite robot test application
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${application_name}    Invite robot test application

*** Test Cases ***
Application team page
    [Documentation]    INFUND-928
    ...
    ...    INFUND-7973
    [Tags]    HappyPath
    [Setup]    The user navigates to the page      ${DASHBOARD_URL}
    Given the user clicks the button/link          link=Invite robot test application
    When the user clicks the button/link           link=view and manage contributors and collaborators
    Then the user should see the text in the page  Application team
    And the user should see the text in the page   View and manage your contributors or collaborators in the application.
    And the lead applicant should have the correct status
    And the user should see the element            link=Application overview

Lead Adds/Removes rows
    [Documentation]    INFUND-901  INFUND-7974  INFUND-8590
    [Tags]    HappyPath
    When The user clicks the button/link      jquery=a:contains("Update and add contributors from ${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}")
    And the user clicks the button/link       jQuery=button:contains("Add another contributor")
    And The user should not see the element   jQuery=.modal-delete-organisation button:contains('Delete organisation')
    Then The user should see the element      css=.table-overflow tr:nth-of-type(2) td:nth-of-type(1)
    And The user clicks the button/link       jQuery=button:contains('Remove')
    Then The user should not see the element  css=.table-overflow tr:nth-of-type(2) td:nth-of-type(1)

Lead cannot be removed
    [Documentation]    INFUND-901  INFUND-7974
    [Tags]
    Then the lead applicant cannot be removed

Lead organisation server-side validations
    [Documentation]    INFUND-901  INFUND-7974
    [Tags]    HappyPath
    When The user clicks the button/link      jQuery=button:contains("Add another contributor")
    And The user enters text to a text field  css=tr:nth-of-type(2) td:nth-of-type(1) input    ${EMPTY}
    And The user enters text to a text field  css=tr:nth-of-type(2) td:nth-of-type(2) input    @test.co.uk
    And browser validations have been disabled
    And the user clicks the button/link       jQuery=.button:contains("Invite")
    Then the user should see an error         Please enter a valid email address.
    And the user should see an error          Please enter a name.

Lead organisation client-side validations
    [Documentation]    INFUND-901  INFUND-7974
    [Tags]    HappyPath
    When The user enters text to a text field      css=tr:nth-of-type(2) td:nth-of-type(1) input    Florian
    And The user enters text to a text field       css=tr:nth-of-type(2) td:nth-of-type(2) input    florian21@florian.com
    Then the user cannot see a validation error in the page
    [Teardown]    The user clicks the button/link  link=Application team

Lead Adds/Removes partner organisation
    [Documentation]    INFUND-1039
    ...
    ...    INFUND-7973
    ...
    ...    INFUND-7979
    ...
    ...    INFUND-8590
    [Tags]    HappyPath
    When The user clicks the button/link               jQuery=a:contains('Add a collaborator organisation')
    And The user enters text to a text field           name=organisationName    Fannie May
    And The user enters text to a text field           name=applicants[0].name    Collaborator 2
    And The user enters text to a text field           name=applicants[0].email    ewan+10@hiveit.co.uk
    And The user clicks the button/link                jQuery=button:contains("Add organisation and invite applicants")
    And the user clicks the button/link                jQuery=a:contains("Update and add contributors from Fannie May")
    Then The user clicks the button/link               jQuery=a:contains('Delete organisation')
    And The user clicks the button/link                jQuery=.modal-delete-organisation button:contains('Delete organisation')
    Then The user should not see the text in the page  Fannie May
    And the user should see the text in the page       Application team

Partner organisation Server-side validations
    [Documentation]    INFUND-896
    ...
    ...    INFUND-7979
    [Tags]    HappyPath
    Given the user clicks the button/link      jQuery=a:contains('Add a collaborator organisation')
    When The user enters text to a text field  name=organisationName    ${EMPTY}
    And The user enters text to a text field   name=applicants[0].name    ${EMPTY}
    And The user enters text to a text field   name=applicants[0].email    ${EMPTY}
    And browser validations have been disabled
    And the user clicks the button/link        jQuery=.button:contains("Add organisation and invite applicants")
    Then the user should see an error          An organisation name is required.
    And the user should see an error           Please enter a name.
    And the user should see an error           Please enter an email address.

Partner organisation Client-side validations
    [Documentation]    INFUND-7979
    [Tags]    HappyPath
    When The user enters text to a text field  name=organisationName    Fannie May
    And The user enters text to a text field   name=applicants[0].name    Adrian Booth
    And The user enters text to a text field   name=applicants[0].email    ${invite_email}
    Then the user cannot see a validation error in the page

Valid invitation submit
    [Documentation]    INFUND-901
    [Tags]  HappyPath  SmokeTest
    When The user clicks the button/link  jQuery=button:contains("Add organisation and invite applicants")
    Then the user should see the element  jQuery=.table-overflow tr:contains("Steve Smith") td:nth-child(3):contains("Lead")
    And the user should see the element   jQuery=.table-overflow tr:contains("Adrian Booth") td:nth-child(3):contains("Invite pending")

The Lead's inputs should not be visible in other application invites
    [Documentation]    INFUND-901
    [Tags]
    Then the user should not see the element  css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input

Pending users visible in the assign list but not clickable
    [Documentation]    INFUND-928  INFUND-1962
    [Tags]  HappyPath
    Given the user navigates to the page          ${DASHBOARD_URL}
    And the user clicks the button/link           link=Invite robot test application
    And the user clicks the button/link           link=Project summary
    Then the applicant cannot assign to pending invitees
    And the user should see the text in the page  Adrian Booth (pending)
    [Teardown]  logout as user

Business organisation (partner accepts invitation)
    [Documentation]  INFUND-1005 INFUND-2286 INFUND-1779 INFUND-2336
    [Tags]  HappyPath  Email  SmokeTest
    When the user reads his email and clicks the link  ${invite_email}  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation  2
    And the user clicks the button/link                jQuery=.button:contains("Yes, accept invitation")
    And the user selects the radio button              organisationType    1
    And the user clicks the button/link                jQuery=.button:contains("Continue")
    And the user enters text to a text field           id=organisationSearchName    Nomensa
    And the user clicks the button/link                id=org-search
    And the user clicks the button/link                link=NOMENSA LTD
    And the user selects the checkbox                  address-same
    And the user clicks the button/link                jQuery=.button:contains("Continue")
    And the user clicks the button/link                jQuery=.button:contains("Save and continue")
    And the user fills the create account form         Adrian  Booth
    And the user reads his email                       ${invite_email}  Please verify your email address  Once verified you can sign into your account

Partner requests new verification email via password reset
    [Documentation]  IFS-52
    [Tags]  HappyPath  Email
    Given the user navigates to the page           ${LOGIN_URL}
    When the user clicks the forgot psw link
    And the user enters text to a text field       id=id_email    ${invite_email}
    And the user clicks the button/link            css=input.button
    Then the user should see the text in the page  If your email address is recognised and valid, you’ll receive a notification

Complete account verification
    [Documentation]    INFUND-1005  INFUND-2286  INFUND-1779  INFUND-2336
    [Tags]  HappyPath  Email  SmokeTest
    When the user reads his email and clicks the link       ${invite_email}    Please verify your email address    Once verified you can sign into your account  1
    Then the user should be redirected to the correct page  ${REGISTRATION_VERIFIED}

Partner should be able to log-in and see the new company name
    [Documentation]    INFUND-2083  IFS-951
    ...
    ...    INFUND-7976
    [Tags]    Email    HappyPath    SmokeTest
    Given the user clicks the button/link                   jQuery=.button:contains("Sign in")
    When the user logs-in in new browser                    ${invite_email}    ${correct_password}
    Then the user should be redirected to the correct page  ${DASHBOARD_URL}
    And the user can see the updated company name throughout the application
    And the user reads his email and clicks the link        ${invite_email}    Innovate UK applicant questionnaire    diversity survey
    [Teardown]    the user navigates to the page            ${DASHBOARD_URL}

Parner can see the Application team
    [Documentation]    INFUND-7976
    Given the user clicks the button/link    link=Invite robot test application
    And the user clicks the button/link      link=view and manage contributors and collaborators
    Then the user should see the element     jQuery=.table-overflow tr:nth-child(1) td:nth-child(1):contains("Steve Smith")
    And the user should see the element      jQuery=.table-overflow tr:nth-child(1) td:nth-child(2):contains("${lead_applicant}")
    And the user should see the element      jQuery=.table-overflow tr:nth-child(1) td:nth-child(3):contains("Lead")
    And The user should see the element      link=Application overview
    And The user should not see the element  link=Update and add contributors from ${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}

Partner can invite others to his own organisation
    [Documentation]    INFUND-2335  INFUND-7977
    [Tags]    Email
    When the user clicks the button/link      jQuery=a:contains("Update and add contributors from NOMENSA LTD")
    And the user clicks the button/link       jQuery=button:contains("Add another contributor")
    And The user enters text to a text field  css=tr:nth-of-type(2) td:nth-of-type(1) input    Mark
    And The user enters text to a text field  css=tr:nth-of-type(2) td:nth-of-type(2) input    mark21@innovateuk.com
    And the user clicks the button/link       jQuery=button:contains("Invite")
    Then The user should see the element      jQuery=td:contains("mark21@innovateuk.com") + td:contains("Invite pending")

Lead should see the accepted partner in the assign list
    [Documentation]    INFUND-1779
    [Tags]    HappyPath  Email
    [Setup]    Log in as a different user  &{lead_applicant_credentials}
    Given the user navigates to the page   ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invite robot test application
    And the user clicks the button/link    link=Project summary
    When the user clicks the button/link   css=.assign-button > button
    Then the user should see the element   jQuery=button:contains("Adrian Booth")

Lead applicant invites a non registered user in the same organisation
    [Documentation]    INFUND-928  INFUND-1463  INFUND-7979
    [Tags]
    Given the user navigates to the page           ${DASHBOARD_URL}
    And the user clicks the button/link            link=Invite robot test application
    When the user clicks the button/link           link=view and manage contributors and collaborators
    When the user clicks the button/link           jQuery=a:contains("Update and add contributors from ${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}")
    Then the user should see the text in the page  Update ${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}
    And the user clicks the button/link            jQuery=button:contains("Add another contributor")
    When The user enters text to a text field      name=stagedInvite.name    Roger Axe
    And The user enters text to a text field       name=stagedInvite.email    ${test_mailbox_one}+inviteorg2@gmail.com
    And the user clicks the button/link            jQuery=button:contains("Invite")
    Then the user should see the element           jQuery=.table-overflow td:contains(${test_mailbox_one}+inviteorg2@gmail.com)+td:contains("Invite pending for 0 days")
   [Teardown]    Logout as user

Registered partner should not create new org but should follow the create account flow
    [Documentation]    INFUND-1463
    [Tags]    Email
    When the user reads his email and clicks the link      ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com    Invitation to collaborate in ${openCompetitionBusinessRTO_name}    You will be joining as part of the organisation    2
    And the user should see the text in the page           Join an application
    And the user clicks the button/link                    jQuery=.button:contains("Yes, accept invitation")
    And the user should see the text in the page           Confirm your organisation
    And the user should see the element                    link=email the lead applicant
    And the user clicks the button/link                    jQuery=.button:contains("Confirm and continue")
    And the user fills the create account form             Roger    Axe
    And the user reads his email and clicks the link       ${TEST_MAILBOX_ONE}+inviteorg2@gmail.com    Please verify your email address    Once verified you can sign into your account
    And the user should be redirected to the correct page  ${REGISTRATION_VERIFIED}

Lead should not see pending status for accepted invite
    [Documentation]    IFS-68
    [Tags]  Email
    [Setup]
    Given the user clicks the button/link       jQuery=a:contains("Sign in")
    Logging in and Error Checking               &{lead_applicant_credentials}
    When the user clicks the button/link        link=Invite robot test application
    And the user clicks the button/link         link=view and manage contributors and collaborators
    And the user clicks the button/link         link=Update and add contributors from ${EMPIRE_LTD_NAME}
    Then the user should see the element         jQuery=.table-overflow td:contains("${test_mailbox_one}+inviteorg2@gmail.com") ~ td:contains("Remove")

*** Keywords ***
The lead applicant should have the correct status
    the user should see the element  jQuery=h2:contains("${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}"):contains("(Lead)")+h3:contains("Organisation type")+p:contains("Business")
    the user should see the element  jQuery=.table-overflow tr:nth-child(1) td:nth-child(1):contains("Steve Smith")
    the user should see the element  jQuery=.table-overflow tr:nth-child(1) td:nth-child(2):contains("${lead_applicant}")
    the user should see the element  jQuery=.table-overflow tr:nth-child(1) td:nth-child(3):contains("Lead")

the applicant cannot assign to pending invitees
    the user clicks the button/link      jQuery=button:contains("Assign this question to someone else")
    the user should not see the element  jQuery=button:contains("Adrian Booth")

the status of the people should be correct in the Manage contributors page
    the user should see the element  jQuery=.table-overflow tr:contains("Steve Smith") td:nth-child(3):contains("Lead")
    the user should see the element  jQuery=.table-overflow tr:contains("Adrian Booth") td:nth-child(3):contains("Invite pending")

the user can see the updated company name throughout the application
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=${application_name}
    And the user clicks the button/link   link=Your finances
    And the user should see the element   link=Your project costs
    And the user should see the element   link=Your organisation
    And the user should see the element   jQuery=h3:contains("Your funding")
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=${application_name}
    When the user clicks the button/link  link=view and manage contributors and collaborators
    Then the user should see the element  jQuery=h2:contains("NOMENSA LTD")+h3:contains("Organisation type")+p:contains("Business")

the lead applicant cannot be removed
    the user should see the text in the element  css=tr:nth-of-type(1) td:nth-of-type(3)    Lead
    the user should not see the element          jQuery=#applicant-table tbody > tr:nth-child(1) button:contains("Remove")

the applicant's inputs should be visible
    Textfield Value Should Be      name=organisations[1].organisationName    Fannie May
    ${input_value} =    Get Value  name=organisationName
    Should Be Equal As Strings     ${input_value}    Fannie May
    Textfield Value Should Be      name=applicants[0].name    Collaborator 2
    ${input_value} =    Get Value  name=applicants[0].name
    Should Be Equal As Strings     ${input_value}    Collaborator 2
    Textfield Value Should Be      name=applicants[1].name    Collaborator 3
    ${input_value} =    Get Value  name=applicants[1].name
    Should Be Equal As Strings     ${input_value}    Collaborator 3