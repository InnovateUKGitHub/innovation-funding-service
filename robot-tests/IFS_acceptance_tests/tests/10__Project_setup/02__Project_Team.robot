*** Settings ***
Documentation   IFS-5700 - Create new project team page to manage roles in project setup
...
...             IFS-5719 - Add team members in Project setup
...
...             IFS-5718 - Remove team members in Project setup
...
...             IFS-5723 - Remove a pending invitation
...
...             IFS-5722 - Resend invitation to add new members (partners)
...
...             IFS-5720 - Add team members (internal)
...
...             IFS-5721 - Resend invitation to add new members (internal)
...
...             IFS-5721 - Remove a pending invitation (internal)
...
...             IFS-5901 - Change access permisions to update project team members in project setup
...
...             IFS-5710 - Add project team section to setup your project page
...
...             IFS-6485 - Add Partner
...
...             IFS-6505 - Accept invite as new partner and register
...
...             IFS-6525 - Invited new partner to project setup - pending state
...
...             IFS-6484 - Remove Partner
...
...             IFS-6502 - Update status of sections in project setup after partner change
...
...             IFS-6486 - Activity Logs for Partner Changes
...
...             IFS-6492 - Accept Terms & Conditions for New Partners in Project Setup
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          PS_Common.robot
Resource          ../04__Applicant/Applicant_Commons.robot


*** Variables ***
${newProjecTeamPage}         ${server}/project-setup/project/${PS_PD_Project_Id}/team
${moProjectID}               ${project_ids["Super-EFFY - Super Efficient Forecasting of Freight Yields"]}
${addPartnerOrgCompId}       ${competition_ids["Project Setup Comp 7"]}
${addNewPartnerOrgProjID}    ${project_ids["PSC application 7"]}
${addNewPartnerOrgAppID}     ${application_ids["PSC application 7"]}
${addNewPartnerOrgAppID6}    ${application_ids["PSC application 6"]}
${addPartnerOrgCompId6}      ${competition_ids["Project Setup Comp 6"]}
${addNewPartnerOrgProjID6}   ${project_ids["PSC application 6"]}
${addNewPartnerOrgAppID6}    ${application_ids["PSC application 6"]}
${leadApplicantEmail}        troy.ward@gmail.com
${partnerApplicantEmail}     belle.smith@gmail.com
${reserachApplicantEmail}    nicole.brown@gmail.com
${addNewPartnerOrgProjPage}  ${server}/project-setup-management/competition/${addPartnerOrgCompId}/project/${addNewPartnerOrgProjID}/team/partner
${steakHolderCompId}         ${competition_ids["Rolling stock future developments"]}
${steakHolderProjectId}      ${project_ids["High-speed rail and its effects on water quality"]}
${leadNewMemberEmail}        test@test.nom
${nonLeadNewMemberEmail}     testerina@test.nom
${removeInviteEmail}         remove@test.nom
${internalViewTeamPage}      ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/project/${PS_PD_Project_Id}/team
${internalInviteeEmail}      internal@invitee.com
${ifsAdminAddOrgEmail}       admin@addorg.com
${ifsPendingAddOrgEmail}     pending@pending.com
${intFinanceAddOrgEmail}     finance@addorg.com
${applicationName}           PSC application 7
${orgInviterName}            Ward Ltd
${PSCapplicationTeamPage}    ${server}/project-setup-management/competition/${competition_ids["Project Setup Comp 5"]}/project/${project_ids["PSC application 5"]}/team

*** Test Cases ***
Monitoring Officers has a read only view of the Project team page
    [Documentation]  IFS-5901
    Given the user logs-in in new browser   &{monitoring_officer_one_credentials}
    When the user navigates to the page     ${server}/project-setup/project/${moProjectID}/team
    Then the user should see the read only view of Project team page

Innovation lead has a read only view of the Project team page
    [Documentation]  IFS-5901
    Given log in as a different user      &{innovation_lead_one}
    When the user navigates to the page   ${internalViewTeamPage}
    Then the user should see the read only view of Project team page

Stakeholder has a read only view of the Project team page
    [Documentation]  IFS-5901
    Given log in as a different user      &{stakeholder_user}
    When the user navigates to the page   ${server}/project-setup-management/competition/${steakHolderCompId}/project/${steakHolderProjectId}/team
    Then the user should see the read only view of Project team page

Comp admin has a read only view of the Project team page
    [Documentation]  IFS-5901
    Given log in as a different user      &{Comp_admin1_credentials}
    When the user navigates to the page   ${internalViewTeamPage}
    Then the user should see the read only view of Project team page

Project finance isn't able to add team members on Project team page
    [Documentation]  IFS-5901  IFS-6484
    Given log in as a different user      &{internal_finance_credentials}
    When the user navigates to the page   ${internalViewTeamPage}
    Then the user should not see the element   jQuery = button:contains("Add team member")
    And the user should not see the element   jQuery = button:contains("Resend invite")

The lead partner is able to access project team page
    [Documentation]  IFS-5700
    Given log in as a different user       &{lead_applicant_credentials}
    When the user navigates to the page    ${newProjecTeamPage}
    Then the user should see the element   jQuery = h1:contains("Project team")

Verify add new team member field validation
    [Documentation]  IFS-5719
    Given the user clicks the button/link               jQuery = button:contains("Add team member")
    When the user clicks the button/link                jQuery = button:contains("Invite to project")
    Then the user should see a field and summary error  Please enter a name.
    And the user should see a field and summary error   Enter an email address.
    [Teardown]  the user clicks the button/link         jQuery = td:contains("Name")~ td button:contains("Remove")

The lead partner is able to add a new team member
    [Documentation]  IFS-5719
    Given the user clicks the button/link    jQuery = button:contains("Add team member")
    When The user adds a new team member     Tester   ${leadNewMemberEmail}
    Then the user should see the element     jQuery = td:contains("Tester (pending for 0 days)") ~ td:contains("${leadNewMemberEmail}")
    [Teardown]   Logout as user

A new team member is able to accept the invitation from lead partner and see project set up
    [Documentation]  IFS-5719
    Given the user reads his email and clicks the link   ${leadNewMemberEmail}  New designs for a circular economy: Magic material: Invitation for project 112.  You have been invited to join the project Magic material by Empire Ltd.  1
    When the user creates a new account                  Tester   Testington   ${leadNewMemberEmail}
    Then the user is able to access the project          ${leadNewMemberEmail}

Non Lead partner is able to add a new team member
    [Documentation]  IFS-5719
    [Setup]  log in as a different user               &{collaborator1_credentials}
    Given the user navigates to the page              ${newProjecTeamPage}
    And the user clicks the button/link               jQuery = button:contains("Add team member")
    When The user adds a new team member              Testerina   ${nonLeadNewMemberEmail}
    Then the user should see the element              jQuery = td:contains("Testerina (pending for 0 days)") ~ td:contains("${nonLeadNewMemberEmail}")
    [Teardown]   the user logs out if they are logged in

A new team member is able to accept the invitation from non lead partner and see projec set up
    [Documentation]  IFS-5719
    Given the user reads his email and clicks the link      ${nonLeadNewMemberEmail}  New designs for a circular economy: Magic material: Invitation for project 112.  You have been invited to join the project Magic material by Ludlow.  1
    When the user creates a new account                     Testerina   Testington   ${nonLeadNewMemberEmail}
    Then the user is able to access the project             ${nonLeadNewMemberEmail}

A user is able to remove a team member
    [Documentation]  IFS-5718
    [Setup]  log in as a different user        &{collaborator1_credentials}
    Given the user navigates to the page       ${newProjecTeamPage}
    When the user clicks the button/link       jQuery = td:contains("Testerina Testington")~ td a:contains("Remove")
    And the user clicks the button/link        jQuery = td:contains("Testerina Testington")~ td button:contains("Remove user")
    Then the user should not see the element   jQuery = td:contains("Testerina Testington")~ td:contains("Remove")

A user who has been removed is no longer able to access the project
    [Documentation]  IFS-5718
    Given log in as a different user           ${nonLeadNewMemberEmail}   ${short_password}
    Then the user should not see the element   jQuery = li:contains("Project number") h3:contains("Magic material")

A user is able to re-send an invitation
    [Documentation]  IFS-5723
    [Setup]    log in as a different user                  &{lead_applicant_credentials}
    Given the user navigates to the page                   ${newProjecTeamPage}
    And the user clicks the button/link                    jQuery = button:contains("Add team member")
    When The user adds a new team member                   Removed   ${removeInviteEmail}
    Then the user is able to re-send an invitation
    And the user reads his email                            ${removeInviteEmail}  New designs for a circular economy: Magic material: Invitation for project 112.  You have been invited to join the project Magic material by Empire Ltd.

A partner is able to remove a pending invitation
    [Documentation]  IFS-5723
    Given the user is abe to remove the pending invitation
    Then Removed invitee is not able to accept the invite    ${removeInviteEmail}

An internal user is able to access the project team page
    [Documentation]  IFS-5720
    [Setup]  log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page   ${internalViewTeamPage}
    Then the user should see the element   jQuery = h1:contains("Project team")

Css user is able to add a new team member to all partners
    [Documentation]  IFS-5901
    [Setup]  log in as a different user    &{support_user_credentials}
    Given the user navigates to the page   ${internalViewTeamPage}
    Then the user is able to add team memebers to all partner organisations

Dashboard status updates correctly for internal and external users
    [Documentation]  IFS-5710
    [Setup]  log in as a different user    &{internal_finance_credentials}
    Given the Project team status for internal user is incomplete
    When all partners complete the Project team section
    Then the Project team status appears as complete for the internal user

New user is able to respond to a query
    [Documentation]  IFS-6421
    Given the internal user posts a query
    When the new user posts a response
    Then the user should not see an error in the page

Project finance is able to remove a partner organisation
    [Documentation]  IFS-6485
    [Setup]  log in as a different user                    &{internal_finance_credentials}
    Given the user navigates to the page                   ${PSCapplicationTeamPage}
    When the user removes a partner organisation           Red Planet
    Then the relevant users recieve an email notification  Red Planet

Ifs Admin is able to add a new partner organisation
    [Documentation]  IFS-6485  IFS-6505
    [Setup]  log in as a different user                        &{ifs_admin_user_credentials}
    Given the user navigates to the page                       ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation              Testing Admin Organisation  Name Surname  ${ifsAdminAddOrgEmail}
    Then a new organisation is able to accept project invite   Name  Surname  ${ifsAdminAddOrgEmail}  innovate  INNOVATE LTD

IFS admin checks for staus update after new org added
    [Documentation]  IFS-6783
    Given log in as a different user                        &{ifs_admin_user_credentials}
    Then the internal user checks for status after new org added/removed

Two organisations with the same name are not able to join
    [Documentation]  IFS-6485  IFS-6505  IFS-6724
    [Setup]  log in as a different user                        &{ifs_admin_user_credentials}
    Given the user navigates to the page                       ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation              Testing pOne Organisation  Name Surname  tesTwoOrgs@test.nom
    Then the same organisation isnt able to join the project   Name  Surname  tesTwoOrgs@test.nom  innovate  INNOVATE LTD
    [Teardown]  the user navigates to the page                 ${LOGIN_URL}

Ifs Admin is able to remove a partner organisation
    [Documentation]  IFS-6485
    [Setup]  Logging in and Error Checking                 &{ifs_admin_user_credentials}
    Given the user navigates to the page                   ${server}/project-setup-management/competition/${addPartnerOrgCompId6}/project/${addNewPartnerOrgProjID6}/team
    When the user removes a partner organisation           SmithZone
    Then the user reads his email                          troy.ward@gmail.com  Partner removed from ${addNewPartnerOrgAppID6}: PSC application 6  Innovate UK has removed SmithZone from this project.
    And log in as a different user                         &{ifs_admin_user_credentials}
    And the internal user checks for status after new org added/removed

Ifs Admin is able to remove a pending partner organisation
    [Documentation]  IFS-6485  IFS-6505
    [Setup]  log in as a different user                                    &{ifs_admin_user_credentials}
    Given the user navigates to the page                                   ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation                          Testing Pending Organisation  Name Surname  ${ifsPendingAddOrgEmail}
    Then the user is able to remove a pending partner organisation         Testing Pending Organisation

Project finance is able to add a new partner organisation
    [Documentation]  IFS-6485  IFS-6505
    [Setup]  log in as a different user                        &{internal_finance_credentials}
    Given the user navigates to the page                       ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation              Testing Finance Organisation  FName Surname  ${intFinanceAddOrgEmail}
    Then a new organisation is able to accept project invite   FName  Surname  ${intFinanceAddOrgEmail}  Nomensa  NOMENSA LTD
    And log in as a different user                             &{internal_finance_credentials}
    And the internal user checks for status after new org added/removed

Project finance is able to remove a pending partner organisation
    [Documentation]  IFS-6485  IFS-6505
    Given the user navigates to the page                                   ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation                          Testing Pending Organisation  Name Surname  ${ifsPendingAddOrgEmail}
    Then the user is able to remove a pending partner organisation         Testing Pending Organisation

The new partner cannot complete funding without organisation
    [Documentation]  IFS-6491
    Given log in as a different user         ${intFinanceAddOrgEmail}  ${short_password}
    And the user clicks the button/link      link = ${applicationName}
    When the user clicks the button/link     link = Your funding
    Then the user should see the element     link = your organisation

The new partner can complete Your organisation
    [Documentation]  IFS-6491
    Given the user clicks the button/link    link = your organisation
    And the user should see the element      jQuery = p:contains("If we decide to award you funding you must be eligible to receive State aid at the point of the award.")
    When the user completes your organisation
    Then the user should see the element     jQuery = li div:contains("Your organisation") ~ .task-status-complete

The new partner completes your funding
    [Documentation]  IFS-6491  IFS-6725
    Given The user clicks the button/link   link = Your funding
    When the user completes your funding
    Then the user should see the element    jQuery = li div:contains("Your funding") ~ .task-status-complete

The new organisation partner accept terms and conditions
    [Documentation]  IFS-6492
    Given the user accept the competition terms and conditions      Return to join project
    Then the user should see the element                            jQuery = li div:contains("Award terms and conditions") ~ .task-status-complete

Editing org size resets your funding
    [Documentation]  IFS-6491
    Given the user clicks the button/link      link = Your organisation
    When the user edits the org size
    Then the user should not see the element   jQuery = li div:contains("Your funding") ~ .task-status-complete

New partner can join project
    [Documentation]  IFS-6558
    Given The user clicks the button/link   link = Your funding
    When the user completes your funding
    Then the user can join the project

New partner can provide bank details
    [Documentation]  IFS-6871
    ${organisationId} =  get organisation id by name  NOMENSA LTD
    Given navigate to external finance contact page, choose finance contact and save  ${organisationId}  financeContact1  28
    When the applicant fills in bank details
    Then internal and external users see correct status

Internal does not see change finances link for new partner
    [Documentation]  IFS-6770
    Given Log in as a different user          &{internal_finance_credentials}
    When the internal partner does not see link for added partner
    Then the internal patner does see link for existing partner

Comp Admin isn't able to add or remove a partner organisation
    [Documentation]  IFS-6485 IFS-6485
    [Setup]  log in as a different user            &{Comp_admin1_credentials}
    Given the user navigates to the page           ${server}/project-setup-management/competition/${addPartnerOrgCompId}/project/${addNewPartnerOrgProjID}/team
    Then the user should not see the element       link = Add a partner organisation
    And the user should not see the element        jQuery = h2:contains("Red Planet") ~ button:first:contains("Remove organisation")

The internal users checks for activity logs after partner added/removed
    [Documentation]  IFS-6486
    Given internal user should see entries in activity log after partner org added/removed
    When log in as a different user       &{internal_finance_credentials}
    Then internal user should see entries in activity log after partner org added/removed
    And log in as a different user       &{ifs_admin_user_credentials}
    And internal user should see entries in activity log after partner org added/removed

lead able to submit only exploitation plan when all partners removed from project
    [Documentation]  IFS-6891
    Given lead submits project documents          ${project_ids["PSC application 20"]}
    When the internal user removed all partners
    And lead uploads the exploitation plan
    Then the internal user approves the exploitation plan

*** Keywords ***
the same organisation isnt able to join the project
    [Arguments]  ${fname}  ${sname}  ${email}  ${orgId}  ${orgName}
    logout as user
    the user reads his email and clicks the link                  ${email}  Invitation to join project ${addNewPartnerOrgAppID}: PSC application 7  You have been invited to join the project ${applicationName} by Ward Ltd .
    the user accepts invitation and selects organisation type     ${orgId}  ${orgName}
    the user fills in account details                             ${fname}  ${sname}
    the user clicks the button/link                               jQuery = button:contains("Create account")
    the user should see the element                               jQuery = h1:contains("Contact our Customer Support team")

a new organisation is able to accept project invite
    [Arguments]  ${fname}  ${sname}  ${email}  ${orgId}  ${orgName}
    logout as user
    the user reads his email and clicks the link                  ${email}  Invitation to join project ${addNewPartnerOrgAppID}: PSC application 7  You have been invited to join the project ${applicationName} by Ward Ltd .
    the user accepts invitation and selects organisation type     ${orgId}  ${orgName}
    the user fills in account details                             ${fname}  ${sname}
    the user clicks the button/link                               jQuery = button:contains("Create account")
    the user verifies their account                               ${email}
    a new organisation logs in and sees the project               ${email}
    the user should see the element                               jQuery = ul:contains("PSC application 7") .status:contains("Ready to join project")
    the user clicks the button/link                               link = PSC application 7
    the user should see the element                               jQuery = h1:contains("Join project")

A new organisation logs in and sees the project
    [Arguments]  ${email}
    the user clicks the button/link   link = Sign in
    Logging in and Error Checking     ${email}  ${short_password}

The user accepts invitation and selects organisation type
    [Arguments]   ${orgId}  ${orgName}
    the user clicks the button/link                       jQuery = .govuk-button:contains("Yes, create an account")
    the user selects the radio button                     organisationType    1
    the user clicks the button/link                       jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House  ${orgId}  ${orgName}

the relevant users recieve an email notification
    [Arguments]  ${orgName}
    the user reads his email       troy.ward@gmail.com  Partner removed from ${application_ids["PSC application 5"]}: PSC application 5  Innovate UK has removed ${orgName} from this project.
    the user reads his email       sian.ward@gmail.com  Partner removed from ${application_ids["PSC application 5"]}: PSC application 5  Innovate UK has removed ${orgName} from this project.
    the user reads his email       megan.rowland@gmail.com  Partner removed from ${application_ids["PSC application 5"]}: PSC application 5  Innovate UK has removed ${orgName} from this project.

the internal user posts a query
    the user clicks the button/link        jQuery = tr:contains("Magic") td:contains("Review")
    the user clicks the button/link        jQuery = tr:contains("Empire") td:nth-child(6):contains("View")
    the user clicks the button/link        id = post-new-query
    the user enters text to a text field   id = queryTitle  a viability query's title
    the user enters text to a text field   css = .editor    another query body
    the user clicks the button/link        css = .govuk-grid-column-one-half button[type = "submit"]  # Post query
    the user should not see an error in the page

the new user posts a response
    log in as a different user                 ${leadNewMemberEmail}   ${short_password}
    the user clicks the button/link            link = Magic material
    the user clicks the button/link            link = Finance checks
    the user clicks the button/link            link = Respond
    the user enters text to a text field       css = .editor    one more response to the eligibility query
    the user clicks the button/link            jQuery = .govuk-button:contains("Post response")

The Project team status for internal user is incomplete
    the user navigates to the page    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/all
    the user should see the element   jQuery = th:contains("Magic material")~ ~ td:contains("Incomplete")

All partners complete the Project team section
    non lead partners complete the Project team section
    lead partner completes the Project team section

Non lead partners complete the Project team section
    log in as a different user                &{collaborator2_alternative_user_credentials}
    the user navigates to the Project team page from the dashboard
    the user selects their finance contact    financeContact2
    the user clicks the button/link           link = Set up your project
    the user should see the element           jQuery = .progress-list li:nth-child(2):contains("Completed")
    log in as a different user                &{collaborator1_credentials}
    the user navigates to the Project team page from the dashboard
    the user selects their finance contact    financeContact2
    the user clicks the button/link           link = Set up your project
    the user should see the element           jQuery = .progress-list li:nth-child(2):contains("Completed")

Lead partner completes the Project team section
    log in as a different user               &{lead_applicant_credentials}
    the user clicks the button/link          link = ${PS_PD_Application_Title}
    the user should see the element          jQuery = ul li:contains("Project team") span:contains("To be completed")
    the user clicks the button/link          link = Project team
    the user selects their finance contact   financeContact2
    the user clicks the button/link          link = Project manager
    the user should see project manager/finance contact validations    Save project manager   You need to select a Project Manager before you can continue.
    the user selects the radio button        projectManager   projectManager2
    the user clicks the button/link          jQuery = button:contains("Save project manager")
    the user clicks the button/link          link = Set up your project
    the user should see the element          jQuery = .progress-list li:nth-child(2):contains("Completed")

The Project team status appears as complete for the internal user
    log in as a different user        &{internal_finance_credentials}
    the user navigates to the page    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/all
    the user should see the element   jQuery = th:contains("Magic material")~ ~ td:contains("Complete")

The user navigates to the Project team page from the dashboard
    the user clicks the button/link   link = ${PS_PD_Application_Title}
    the user clicks the button/link   link = Project team

The user is able to add team memebers to all partner organisations
    the user clicks the button/link        jQuery = h2:contains("Empire Ltd")~ button:first:contains("Add team member")
    the user enters text to a text field   jQuery = h2:contains("Empire Ltd")~ table[id*="invite-user"]:first [name=name]  cssAdded1
    the user enters text to a text field   jQuery = h2:contains("Empire Ltd")~ table[id*="invite-user"]:first [name=email]  1${removeInviteEmail}
    the user clicks the button/link        jQuery = h2:contains("Empire Ltd")~ table[id*="invite-user"]:first button:contains("Invite to project")
    the user should see the element        jQuery = td:contains("cssAdded1 (pending for 0 days)")
    the user clicks the button/link        jQuery = h2:contains("EGGS")~ button:first:contains("Add team member")
    the user enters text to a text field   jQuery = h2:contains("EGGS")~ table[id*="invite-user"]:first [name=name]  cssAdded2
    the user enters text to a text field   jQuery = h2:contains("EGGS")~ table[id*="invite-user"]:first [name=email]  2${removeInviteEmail}
    the user clicks the button/link        jQuery = h2:contains("EGGS")~ table[id*="invite-user"]:first button:contains("Invite to project")
    the user should see the element        jQuery = td:contains("cssAdded2 (pending for 0 days)")
    the user clicks the button/link        jQuery = h2:contains("Ludlow")~ button:first:contains("Add team member")
    the user enters text to a text field   jQuery = h2:contains("Ludlow")~ table[id*="invite-user"]:first [name=name]  cssAdded3
    the user enters text to a text field   jQuery = h2:contains("Ludlow")~ table[id*="invite-user"]:first [name=email]  3${removeInviteEmail}
    the user clicks the button/link        jQuery = h2:contains("Ludlow")~ table[id*="invite-user"]:first button:contains("Invite to project")
    the user should see the element        jQuery = td:contains("cssAdded3 (pending for 0 days)")

The user should see the read only view of Project team page
    the user should see the element       jQuery = h1:contains("Project team")
    the user should not see the element   jQuery = button:contains("Add team member")
    the user should not see the element   jQuery = button:contains("Remove")
    the user should not see the element   jQuery = button:contains("Resend invite")

The user is able to re-send an invitation
    the user should see the element   jQuery = td:contains("Removed (pending for 0 days)")~ td button:contains("Resend invite")
    the user clicks the button/link   jQuery = td:contains("Removed (pending for 0 days)")~ td button:contains("Resend invite")

Removed invitee is not able to accept the invite
    [Arguments]    ${email}
    the user reads his email and clicks the link   ${email}  New designs for a circular economy: Magic material: Invitation for project 112.  You have been invited to join the project Magic material by Empire Ltd.  1
    the user should see the element                jQuery = h1:contains("Sorry, you are unable to accept this invitation.")

The user is abe to remove the pending invitation
    the user clicks the button/link       jQuery = td:contains("Removed (pending for 0 days)")~ td button:contains("Remove")
    the user should not see the element   jQuery = td:contains("Removed (pending for 0 days)")~ td button:contains("Remove")

The user is able to access the project
    [Arguments]  ${email}
    Logging in and Error Checking     ${email}   ${short_password}
    the user should see the element   link = ${PS_PD_Application_Title}

The user creates a new account
    [Arguments]   ${firstName}   ${lastName}   ${email}
    the user should see the element     jQuery = h1:contains("Join a project")
    the user clicks the button/link     jQuery = a:contains("Create account")
    the user fills in account details   ${firstName}   ${lastName}
    the user clicks the button/link     jQuery = button:contains("Create account")
    the user verifies their account     ${email}
    the user clicks the button/link     link = Sign in

The user verifies their account
    [Arguments]  ${email}
    the user should see the element                jQuery = h1:contains("Please verify your email address")
    the user reads his email and clicks the link   ${email}  Please verify your email address  You have recently set up an account with the Innovation Funding Service.  1
    the user should see the element                jQuery = h1:contains("Account verified")

The user fills in account details
    [Arguments]  ${firstName}  ${lastName}
    the user enters text to a text field   id = firstName     ${firstName}
    the user enters text to a text field   id = lastName      ${lastName}
    the user enters text to a text field   id = phoneNumber   07123456789
    the user enters text to a text field   id = password      ${short_password}
    the user selects the checkbox          termsAndConditions

lead submits project documents
    [Arguments]  ${projectID}
    log in as a different user          ${leadApplicantEmail}   ${short_password}
    the user navigates to the page      ${server}/project-setup/project/${projectID}/details/project-address
    the user enter the Correspondence address
    the user clicks the button/link     link = Return to set up your project
    the user completes the project team details
    PM uploads the project documents    ${projectID}
    the user navigates to the page      ${server}/project-setup/project/${projectID}/document/all
    PM submits both documents           ${projectID}

the internal user checks for status after new org added/removed
    the user navigates to the page     ${server}/project-setup-management/competition/${addPartnerOrgCompId}/status/all
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(4) a:contains("Pending")
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(5) a:contains("Assigned")
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(6) a:contains("Complete")
    the user clicks the button/link    jQuery = th:contains("${applicationName}") ~ td:nth-child(7) a:contains("Review")
    the user should see the element    jQuery = table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Approved")
    the user should see the element    jQuery = table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")

internal user should see entries in activity log after partner org added/removed
    the user navigates to the page        ${server}/project-setup-management/competition/${addPartnerOrgCompId}/project/${addNewPartnerOrgProjID}/activity-log
    the user should see the element       jQuery = li div span:contains("NOMENSA LTD") strong:contains("Organisation added:")
    the user navigates to the page        ${server}/project-setup-management/competition/${addPartnerOrgCompId6}/project/${addNewPartnerOrgProjID6}/activity-log
    the user should not see the element   jQuery = li div:contains("for SmithZone") ~ div a:contains("View bank details")
    the user should not see the element   jQuery = li div:contains("for SmithZone") ~ div a:contains("View finance viability")
    the user should not see the element   jQuery = li div:contains("for SmithZone") ~ div a:contains("View finance eligibiliy")
    the user should see the element       jQuery = li div span:contains("SmithZone") strong:contains("Organisation removed:")

the user completes your funding
    the user selects the radio button          requestingFunding   true
    the user should see the element            jQuery = .govuk-hint:contains("based on your organisation size and project research category.")
    the user enters text to a text field       css = [name^="grantClaimPercentage"]  35
    the user selects the radio button          otherFunding   false
    the user clicks the button/link            jQuery = button:contains("Mark as complete")

the user edits the org size
    the user clicks the button/link                         id = mark_as_incomplete
    the user selects the radio button                       organisationSize  SMALL
    the user clicks the button/link                         jQuery = button:contains("Mark as complete")

Custom suite setup
    Connect to database  @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the internal partner does not see link for added partner
    the user navigates to the page        ${server}/project-setup-management/competition/${addPartnerOrgCompId}/status/all
    the user clicks the button/link       css = .action ~ .action a
    the user clicks the button/link       jQuery = tr:contains("NOMENSA LTD") td:nth-child(4)
    the user should not see the element   link = Review all changes to project finances

the internal patner does see link for existing partner
    the user clicks the button/link       link = Finance checks
    the user clicks the button/link       jQuery = tr:contains("Ward Ltd") td:nth-child(4)
    the user should see the element       link = Review all changes to project finances

the user can join the project
    the user should see the element   css = .message-alert
    the user clicks the button/link   id = submit-join-project-button
    the user should see the element   jQuery = h1:contains("Set up your project")
    the user clicks the button/link   link = Dashboard
    the user should see the element   jQuery = li:contains("${applicationName}") .msg-progress

the applicant fills in bank details
    the user clicks the button/link                      link = Return to setup your project
    the user clicks the button/link                      link = Bank details
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    applicant user enters bank details

internal and external users see correct status
    the user should see the element                     jQuery = p:contains("The bank account details below are being")
    the user navigates to the page                      ${server}/project-setup/project/${addNewPartnerOrgProjID}
    the user should see the element                     jQuery = ul li.waiting:nth-child(5)
    the user clicks the button/link                     link = View the status of partners
    the user navigates to the page                      ${server}/project-setup/project/${addNewPartnerOrgProjID}/team-status
    the user should see the element                     jQuery = h1:contains("Project team status")
    the user should see the element                     css = #table-project-status tr:nth-of-type(3) td.status.waiting:nth-of-type(5)
    log in as a different user                          &{internal_finance_credentials}
    the user navigates to the page                      ${server}/project-setup-management/competition/${addPartnerOrgCompId}/status
    the user should see the element                     css = #table-project-status tr:nth-of-type(1) td:nth-of-type(5).status.action

the user removes a partner organisation
    [Arguments]  ${orgName}
    the user clicks the button/link             jQuery = h2:contains("${orgName}")~ button:contains("Remove organisation"):first
    the user clicks the button/link             jQuery = .warning-modal[aria-hidden=false] button:contains("Remove organisation")
    the user should not see the element         jQuery = h2:contains(${orgName})

the internal user removed all partners
    log in as a different user                       &{internal_finance_credentials}
    the user navigates to the page                   ${server}/project-setup-management/competition/${competition_ids["Project Setup Comp 20"]}/project/${project_ids["PSC application 20"]}/team
    the user removes a partner organisation           Red Planet
    the user removes a partner organisation           SmithZone

lead uploads the exploitation plan
    log in as a different user          ${leadApplicantEmail}   ${short_password}
    the user navigates to the page      ${server}/project-setup/project/${project_ids["PSC application 20"]}/document/all
    the user clicks the button/link     link = Exploitation plan
    the user can remove the uploaded file  deleteDocument  ${valid_pdf}
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm
    the user clicks the button/link     link = Return to documents
    the user clicks the button/link     link = Set up your project
    the user should see the element     jQuery = li:contains("Documents") span:contains("Awaiting review")

the internal user approves the exploitation plan
    log in as a different user          &{internal_finance_credentials}
    the user navigates to the page      ${server}/project-setup-management/project/${project_ids["PSC application 20"]}/document/all
    the user clicks the button/link     link = Exploitation plan
    internal user approve uploaded documents
    the user clicks the button/link     link = Return to documents
    the user navigates to the page      ${server}/project-setup-management/competition/${competition_ids["Project Setup Comp 20"]}/status
    the user should see the element     css = #table-project-status tr:nth-of-type(1) td:nth-of-type(3).status.ok