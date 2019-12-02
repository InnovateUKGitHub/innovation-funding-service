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
    Given the user clicks the button/link  jQuery = button:contains("Add team member")
    When the user adds a new team member   Tester   ${leadNewMemberEmail}
    Then the user should see the element   jQuery = td:contains("Tester (pending for 0 days)") ~ td:contains("${leadNewMemberEmail}")
    [Teardown]   Logout as user

A new team member is able to accept the invitation from lead partner and see project set up
    [Documentation]  IFS-5719
    Given the user reads his email and clicks the link   ${leadNewMemberEmail}  New designs for a circular economy: Magic material: Invitation for project 112.  You have been invited to join the project Magic material by Empire Ltd.  1
    When the user creates a new account                  Tester   Testington   ${leadNewMemberEmail}
    Then the user is able to access the project          ${leadNewMemberEmail}

Non Lead partner is able to add a new team member
    [Documentation]  IFS-5719
    [Setup]  log in as a different user    &{collaborator1_credentials}
    Given the user navigates to the page   ${newProjecTeamPage}
    And the user clicks the button/link    jQuery = button:contains("Add team member")
    When the user adds a new team member   Testerina   ${nonLeadNewMemberEmail}
    Then the user should see the element   jQuery = td:contains("Testerina (pending for 0 days)") ~ td:contains("${nonLeadNewMemberEmail}")
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
    When the user adds a new team member                   Removed   ${removeInviteEmail}
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
    Given the user navigates to the page                   ${server}/project-setup-management/competition/${addPartnerOrgCompId}/project/${addNewPartnerOrgProjID}/team
    When the user removes a partner organisation           Red Planet
    Then the relevant users recieve an email notification  Red Planet

Applicants completes the project setup details
    [Documentation]  IFS-6502
    Given the applicants completes the project setup details
    Then the internal user approves the project setup details
    And the internal user checks for completed status

Ifs Admin is able to add a new partner organisation
    [Documentation]  IFS-6485  IFS-6505
    [Setup]  log in as a different user                        &{ifs_admin_user_credentials}
    Given the user navigates to the page                       ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation              Testing Admin Organisation  Name Surname  ${ifsAdminAddOrgEmail}
    Then a new organisation is able to accept project invite   Name  Surname  ${ifsAdminAddOrgEmail}  innovate  INNOVATE LTD  ${addNewPartnerOrgAppID}  ${applicationName}

Ifs Admin is able to remove a partner organisation
    [Documentation]  IFS-6485
    [Setup]  log in as a different user                    &{ifs_admin_user_credentials}
    Given the user navigates to the page                   ${server}/project-setup-management/competition/${addPartnerOrgCompId}/project/${addNewPartnerOrgProjID}/team
    When the user removes a partner organisation           SmithZone
    Then the user reads his email                          troy.ward@gmail.com  Partner removed from ${addNewPartnerOrgAppID}: PSC application 7  Innovate UK has removed SmithZone from this project.
    And the internal user checks for status after new org added/removed

Ifs Admin is able to remove a pending partner organisation
    [Documentation]  IFS-6485  IFS-6505
    [Setup]  log in as a different user                                    &{ifs_admin_user_credentials}
    Given the user navigates to the page                                   ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation                          Testing Pending Organisation  Name Surname  ${ifsPendingAddOrgEmail}
    Then the user is able to remove a pending partner organisation         Testing Pending Organisation

New org enter project setup details and lead resubmit the documents
    [Documentation]  IFS-6502
    Given applicant submits the project setup details
    Then PM removes the rejected documents and resubmit
    And the internal user approves the project setup details after new org added
    And the internal user checks for completed status

Project finance is able to add a new partner organisation
    [Documentation]  IFS-6485  IFS-6505
    [Setup]  log in as a different user                        &{internal_finance_credentials}
    Given the user navigates to the page                       ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation             Testing Finance Organisation  FName Surname  ${intFinanceAddOrgEmail}
    Then a new organisation is able to accept project invite  FName  Surname  ${intFinanceAddOrgEmail}  Nomensa  NOMENSA LTD   ${addNewPartnerOrgAppID}  ${applicationName}
    And log in as a different user                            &{internal_finance_credentials}
    And the internal user checks for status after new org added/removed

Project finance is able to remove a pending partner organisation
    [Documentation]  IFS-6485  IFS-6505
    Given the user navigates to the page                                   ${addNewPartnerOrgProjPage}
    When the user adds a new partner organisation                          Testing Pending Organisation  Name Surname  ${ifsPendingAddOrgEmail}
    Then the user is able to remove a pending partner organisation         Testing Pending Organisation

The new partner cannot complete funding without organisation
    [Documentation]  IFS-6491
    Given log in as a different user                              ${intFinanceAddOrgEmail}  ${short_password}
    And the user clicks the button/link                          link = ${applicationName}
    When the user clicks the button/link     link = Your funding
    Then the user should see the element     link = your organisation

The new partner can complete Your organisation
    [Documentation]  IFS-6491
    Given the user clicks the button/link    link = your organisation
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

*** Keywords ***
the user is able to remove a pending partner organisation
    [Arguments]  ${orgName}
    the user clicks the button/link             jQuery = h2:contains("${orgName}")~ button:contains("Remove organisation"):first
    the user should not see the element         jQuery = h2:contains(${orgName})

the relevant users recieve an email notification
    [Arguments]  ${orgName}
    the user reads his email       troy.ward@gmail.com  Partner removed from ${addNewPartnerOrgAppID}: PSC application 7  Innovate UK has removed ${orgName} from this project.
    the user reads his email       sian.ward@gmail.com  Partner removed from ${addNewPartnerOrgAppID}: PSC application 7  Innovate UK has removed ${orgName} from this project.
    the user reads his email       megan.rowland@gmail.com  Partner removed from ${addNewPartnerOrgAppID}: PSC application 7  Innovate UK has removed ${orgName} from this project.

the user removes a partner organisation
    [Arguments]  ${orgName}
    the user clicks the button/link             jQuery = h2:contains("${orgName}")~ button:contains("Remove organisation"):first
    the user clicks the button/link             jQuery = .warning-modal[aria-hidden=false] button:contains("Remove organisation")
    the user should not see the element         jQuery = h2:contains(${orgName})

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

the user should see project manager/finance contact validations
    [Arguments]   ${save_CTA}  ${errormessage}
    the user clicks the button/link                  jQuery = button:contains("${save_CTA}")
    the user should see a field and summary error    ${errormessage}

The user selects their finance contact
    [Arguments]  ${financeContactName}
    the user clicks the button/link     link = Your finance contact
    the user should see project manager/finance contact validations    Save finance contact   You need to select a finance contact before you can continue.
    the user selects the radio button   financeContact   ${financeContactName}
    the user clicks the button/link     jQuery = button:contains("Save finance contact")

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

the applicants completes the project setup details
    log in as a different user          ${leadApplicantEmail}   ${short_password}
    the user navigates to the page      ${server}/project-setup/project/${addNewPartnerOrgProjID}/details/project-address
    the user enter the Correspondence address
    the user clicks the button/link     link = Return to set up your project
    the user completes the project team details
    PM uploads the project documents    ${addNewPartnerOrgProjID}
    the user navigates to the page      ${server}/project-setup/project/${addNewPartnerOrgProjID}//document/all
    PM submits both documents           ${addNewPartnerOrgProjID}
    applicant submits the bank details
    log in as a different user          ${partnerApplicantEmail}   ${short_password}
    the user navigates to the page      ${server}/project-setup/project/${addNewPartnerOrgProjID}/team
    the user clicks the button/link     link = Your finance contact
    the user selects the radio button   financeContact   financeContact1
    the user clicks the button/link     jQuery = button:contains("Save finance contact")
    the user clicks the button/link     link = Set up your project
    applicant submits the bank details

applicant submits the bank details
    the user navigates to the page                       ${server}/project-setup/project/${addNewPartnerOrgProjID}/bank-details
    the user enters text to a text field                 name = accountNumber  ${Account_Two}
    the user enters text to a text field                 name = sortCode  ${Sortcode_two}
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    the user clicks the button/link                      jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link                      id = submit-bank-details

the internal user approves the project setup details
    log in as a different user               &{internal_finance_credentials}
    the user navigates to the page           ${server}/project-setup-management/project/${addNewPartnerOrgProjID}/document/all
    the user clicks the button/link          link = Collaboration agreement
    internal user approve uploaded documents
    the user goes to documents page          Return to documents  Exploitation plan
    internal user approve uploaded documents
    internal user assign MO to project
    internal user approve the viability and eligibility
    internal user approves the bank details

the internal user approves the project setup details after new org added
    log in as a different user                                        &{internal_finance_credentials}
    the user navigates to the page                                    ${server}/project-setup-management/project/${addNewPartnerOrgProjID}/document/all
    the user clicks the button/link                                   link = Collaboration agreement
    internal user approve uploaded documents
    the user goes to documents page                                   Return to documents  Exploitation plan
    internal user approve uploaded documents
    the user navigates to the page                                    ${server}/project-setup-management/project/${addNewPartnerOrgProjID}/finance-check
    the rag rating updates on the finance check page for viability    2  Green
    the rag rating updates on the finance check page for eligibility  1  Green
    the rag rating updates on the finance check page for eligibility  2  Green

internal user approve the viability and eligibility
    the user navigates to the page     ${server}/project-setup-management/project/${addNewPartnerOrgProjID}/finance-check
    the rag rating updates on the finance check page for viability    1  Green
    the rag rating updates on the finance check page for viability    2  Green
    the rag rating updates on the finance check page for eligibility  1  Green
    the rag rating updates on the finance check page for eligibility  2  Green

the rag rating updates on the finance check page for viability
   [Arguments]    ${row_id}  ${rag_rating}
   the user clicks the button/link                jQuery = table.table-progress tr:nth-child(${row_id}) td:nth-child(2) a:contains("Review")
   the user selects the checkbox                  project-viable
   the user selects the option from the drop-down menu    ${rag_rating}    id = rag-rating
   the user clicks the button/link                jQuery = .govuk-button:contains("Confirm viability")
   the user clicks the button/link                name = confirm-viability
   the user clicks the button/link                link = Return to finance checks
   the user should see the text in the element    css = table.table-progress tr:nth-child(${row_id}) td:nth-child(3)    ${rag_rating}

the rag rating updates on the finance check page for eligibility
   [Arguments]    ${row_id}  ${rag_rating}
   the user clicks the button/link                jQuery = table.table-progress tr:nth-child(${row_id}) td:nth-child(4) a:contains("Review")
   the user selects the checkbox                  project-eligible
   the user selects the option from the drop-down menu    ${rag_rating}    id = rag-rating
   the user clicks the button/link                jQuery = .govuk-button:contains("Approve eligible costs")
   the user clicks the button/link                name = confirm-eligibility
   the user clicks the button/link                link = Return to finance checks
   the user should see the text in the element    css = table.table-progress tr:nth-child(${row_id}) td:nth-child(5)    ${rag_rating}

the internal user checks for completed status
    the user navigates to the page     ${server}/project-setup-management/competition/${addPartnerOrgCompId}/status/all
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(2) a:contains("Complete")
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(3) a:contains("Complete")
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(4) a:contains("Complete")
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(5) a:contains("Assigned")
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(6) a:contains("Complete")
    the user clicks the button/link    jQuery = th:contains("${applicationName}") ~ td:nth-child(7) a:contains("Review")
    the user should see the element    jQuery = table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Approved")
    the user should see the element    jQuery = table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Approved")

the internal user checks for status after new org added/removed
    the user navigates to the page     ${server}/project-setup-management/competition/${addPartnerOrgCompId}/status/all
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(4) a:contains("Rejected")
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(5) a:contains("Assigned")
    the user should see the element    jQuery = th:contains("${applicationName}") ~ td:nth-child(6) a:contains("Complete")
    the user clicks the button/link    jQuery = th:contains("${applicationName}") ~ td:nth-child(7) a:contains("Review")
    the user should see the element    jQuery = table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Approved")
    the user should see the element    jQuery = table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")

applicant submits the project setup details
    log in as a different user    	admin@addorg.com   ${short_password}
    the user enter project details and project team details

the user enter project details and project team details
    the user navigates to the page         ${server}/project-setup/project/${addNewPartnerOrgProjID}/details
    the user clicks the button/link        link = Edit
    the user enters text to a text field   id = postcode   AB12 3CD
    the user clicks the button/link        jQuery = button:contains("Save project location")
    the user navigates to the page         ${server}/project-setup/project/${addNewPartnerOrgProjID}/team
    the user clicks the button/link        link = Your finance contact
    the user selects the radio button      financeContact   financeContact1
    the user clicks the button/link        jQuery = button:contains("Save finance contact")
    the user clicks the button/link        link = Set up your project

PM removes the rejected documents and resubmit
    log in as a different user          ${leadApplicantEmail}   ${short_password}
    the user navigates to the page      ${server}/project-setup/project/${addNewPartnerOrgProjID}//document/all
    the user clicks the button/link     link = Collaboration agreement
    the user clicks the button/link     jQuery = button:contains("Remove")
    the user clicks the button/link     link = Back to document overview
    the user clicks the button/link     link = Exploitation plan
    the user clicks the button/link     jQuery = button:contains("Remove")
    PM uploads the project documents    ${addNewPartnerOrgProjID}
    the user navigates to the page      ${server}/project-setup/project/${addNewPartnerOrgProjID}//document/all
    PM submits both documents           ${addNewPartnerOrgProjID}

internal user assign MO to project
    the user navigates to the page           ${server}/project-setup-management/monitoring-officer/view-all
    search for MO   Orvill  Orville Gibbs
    the internal user assign project to MO   ${addNewPartnerOrgAppID}  ${applicationName}

internal user approves the bank details
    log in as a different user        &{internal_finance_credentials}
    the user navigates to the page     ${server}/management/dashboard/project-setup
    Project finance is able to approve the bank details   	Ward Ltd
    the user navigates to the page     ${server}/management/dashboard/project-setup
    Project finance is able to approve the bank details   	SmithZone

internal user should see entries in activity log after partner org added/removed
    the user navigates to the page        ${server}/project-setup-management/competition/${addPartnerOrgCompId}/project/${addNewPartnerOrgProjID}/activity-log
    the user should not see the element   jQuery = li div:contains("for SmithZone") ~ div a:contains("View bank details")
    the user should not see the element   jQuery = li div:contains("for SmithZone") ~ div a:contains("View finance viability")
    the user should not see the element   jQuery = li div:contains("for SmithZone") ~ div a:contains("View finance eligibiliy")
    the user should see the element       jQuery = li div span:contains("NOMENSA LTD") strong:contains("Organisation added:")
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
    the user selects the checkbox                           stateAidAgreed
    the user clicks the button/link                         jQuery = button:contains("Mark as complete")

Custom suite setup
    The guest user opens the browser

Custom suite teardown
    The user closes the browser

the internal partner does not see link for added partner
    the user navigates to the page        ${server}/project-setup-management/competition/${addPartnerOrgCompId}/status/all
    the user clicks the button/link       css = .action a
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

