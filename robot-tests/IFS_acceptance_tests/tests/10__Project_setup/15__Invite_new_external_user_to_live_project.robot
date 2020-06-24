*** Settings ***
Documentation     IFS-7316 Internal user can invite new external user when project is Live
...
...               IFS-7317 User accepts the invite sent in IFS 7316
...
...               IFS-7318 Internal user can now see a link to manage external users once the project is live and in ACC

Suite Setup       The user logs-in in new browser  &{ifs_admin_user_credentials}
Suite Teardown    The user closes the browser
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${competitionTitle}                         Enhanced energy saving competition
${competitionId}                            ${competition_ids["${competitionTitle}"]}
${applicationTitle}                         Energy saver- nano tech
${applicationNumber}                        ${application_ids["${applicationTitle}"]}
${liveProjectInIFSPA}                       ${server}/project-setup-management/competition/${competitionId}/status/all
${leadApplicant}                            troy.ward@gmail.com
${newExternalUserFCEmail}                   external_user_fc@example.com
${newExternalUserMOEmail}                   external_user_mo@example.com
${existingUser}                             daniel.tan@example.com
${orgName}                                  Ward Ltd
${externalUserEmailInviteSubject}           invitation to participate in a project
${externalUserEmailVerificationSubject}   	Please verify your email address
${emailInviteContentPattern}                You have been invited to participate in this Innovation Funding Service project:
${emailVerificationContentPattern}          You have recently set up an account with the Innovation Funding Service.
${invitationPageMessage}                    You can invite people to create a new external user account, so they can be added to this live project.
${validationHeader}                         There is a problem.
${firstNameValidation}                      Please enter their first name.
${firstNameLengthValidation}                Their first name must have at least 2 characters.
${lastNameValidation}                       Please enter their last name.
${lastNameValidationLength}                 Their last name must have at least 2 characters.
${emailAddressValidation}                   Please enter an email address.
${externalUserRoleValidation}               Please select a role.
${createAccountValidationError}             We were unable to create your account.
${phoneNumberValidation}                    Please enter a valid phone number between 8 and 20 digits.
${tAndCValidation}                          To create a new account you must agree to the website terms and conditions.

*** Test Cases ***
IFS Admin can see the link to manage invitations to external users
    [Documentation]  IFS-7318
    Given the grant offer letter is accepted and project is live in IFS-PA
    When the user clicks the button/link     link = View only completed projects for this competition
    And the user clicks the button/link      jQuery = button:contains("Open all")
    Then the user should see the element     link = Manage invitations to external users

IFS Admin can see the link to invite new external users
    [Documentation]  IFS-7318, IF-7316
    Given the user clicks the button/link     link = Manage invitations to external users
    And the user should see the element       jQuery = p:contains("${invitationPageMessage}")
    When the user clicks the button/link      link = Invite a new external user
    Then the user should see the element      jQuery = button:contains("Send invitation")

IFS Admin gets validation error messages on not completing the fields
    [Documentation]  IFS-7316
    When the user clicks the button/link                   jquery = button:contains("Send invitation")
    Then the user should see validation error messages

IFS Admin is able to invite new external user as finance contact to a live project
    [Documentation]  IFS-7316
    Given ifs admin invites a new external user                 FFCname  LFCName  ${newExternalUserFCEmail}  GRANTS_PROJECT_FINANCE_CONTACT
    And the user should see the element                         link = Please select an organisation.
    When the user selects the option from the drop-down menu    ${orgName}  id = organisationId
    And the user clicks the button/link                         jQuery = button:contains("Send invitation")
    Then the user should see the element                        jQuery = span:contains("1") + span:contains("Pending invitations")
    [Teardown]  logout as user

The new external user can accept the email invite as FC and create an account
    [Documentation]  IFS-7317
    Given the user reads his email and clicks the link        ${newExternalUserFCEmail}  ${externalUserEmailInviteSubject}  ${emailInviteContentPattern}  1
    When the user clicks the button/link                      link = Create an account
    Then the user provides details and creates an account     FFCname  LFCName
    And the user reads his email and clicks the link          ${newExternalUserFCEmail}  ${externalUserEmailVerificationSubject}  ${emailVerificationContentPattern}  1

The new external user(as FC) signs in and can see multiple dashboard
    [Documentation]  IFS-7317
    Given the user clicks the button/link             link = Sign in
    When logging in and error checking                ${newExternalUserFCEmail}  ${short_password}
    Then the user can see multiple user dashboard

IFS Admin is able to invite an existing user as an external project manager to a live project
    [Documentation]  IFS-7316, IFS-7318
    [Setup]  log in as a different user            &{ifs_admin_user_credentials}
    Given the user navigates to the page           ${liveProjectInIFSPA}
    When the user clicks the button/link           link = Manage invitations to external users
    And the user clicks the button/link            link = Invite a new external user
    Then ifs admin invites a new external user     Daniel  Tan  ${existingUser}  GRANTS_PROJECT_MANAGER
    [Teardown]  logout as user

The existing user can accept the email invite
    [Documentation]  IFS-7317
    Given the user reads his email and clicks the link     ${existingUser}  ${externalUserEmailInviteSubject}  ${emailInviteContentPattern}  1
    And the user clicks the button/link                    link = Sign in to your account
    When logging in and error checking                     ${existingUser}  ${short_password}
    Then the user can see multiple user dashboard

Project Finance is able to invite new external user as monitoring officer to a live project
    [Documentation]  IFS-7316, IFS-7318
    [Setup]  log in as a different user            &{internal_finance_credentials}
    Given the user navigates to the page           ${liveProjectInIFSPA}
    When the user clicks the button/link           link = Manage invitations to external users
    And the user clicks the button/link            link = Invite a new external user
    Then ifs admin invites a new external user     FMOname  LMOName  ${newExternalUserMOEmail}  GRANTS_MONITORING_OFFICER
    [Teardown]  logout as user

The new external user can accept the email invite as MO and create an account
    [Documentation]  IFS-7316
    Given the user reads his email and clicks the link        ${newExternalUserMOEmail}  ${externalUserEmailInviteSubject}  ${emailInviteContentPattern}  1
    When the user clicks the button/link                      link = Create an account
    Then the user provides details and creates an account     FMOname  LMOName
    And the user reads his email and clicks the link          ${newExternalUserMOEmail}  ${externalUserEmailVerificationSubject}  ${emailVerificationContentPattern}  1

The new external user(as MO) signs in and can see multiple dashboard
    [Documentation]  IFS-7317
    Given the user clicks the button/link             link = Sign in
    When logging in and error checking                ${newExternalUserMOEmail}  ${short_password}
    Then the user can see multiple user dashboard

The internal user can see the external users invited in activity log but not in project team
    [Documentation]  IFS-7316
    [Setup]  log in as a different user      &{internal_finance_credentials}
    Given the user navigates to the page     ${liveProjectInIFSPA}
    When the user clicks the button/link     link = View only completed projects for this competition
    And the user clicks the button/link      link = View activity log
    Then the user should see the element     jQuery = strong:contains("Monitoring officer invited to live project")
    And the user should see the element      jQuery = strong:contains("Project manager invited to live project")
    And the user should see the element      jQuery = strong:contains("Finance contact invited to live project")

The internal user cannot see any pending invites to external users
    [Documentation]  IFS-7318
    Given the user clicks the button/link     link = Back to competition overview
    When the user clicks the button/link      link = Manage invitations to external users
    Then the user should see the element      jQuery = span:contains("0") + span:contains("Pending invitations")

*** Keywords ***
the grant offer letter is accepted and project is live in IFS-PA
    the user navigates to the page        ${liveProjectInIFSPA}
    the user clicks the button/link       jQuery = tr:contains("${applicationTitle}") td:nth-child(9)
    the user selects the radio button     approvalType  APPROVED
    the user clicks the button/link       jQuery = button:contains("Submit")
    the user clicks the button/link       jQuery = button:contains("Accept signed grant offer letter")
    the user clicks the button/link       link = Back to project setup
    the project is sent to acc

the project is sent to acc
    Connect to database  @{database}
    execute sql string   UPDATE `${database_name}`.`grant_process` SET `pending`='1' WHERE `application_id`='${applicationNumber}';
    #The sleep is necessary as the grant table is read as part of a cron job which runs every 1 min
    sleep  60s
    Disconnect from database

the user should see validation error messages
    the user should see the element     jQuery = h2:contains("${validationHeader}")
    the user should see the element     link = ${firstNameValidation}
    the user should see the element     jQuery = span:contains("${firstNameLengthValidation}")
    the user should see the element     link = ${lastNameValidation}
    the user should see the element     jQuery = span:contains("${lastNameValidationLength}")
    the user should see the element     link = ${emailAddressValidation}
    the user should see the element     link = ${externalUserRoleValidation}

ifs admin invites a new external user
    [Arguments]  ${firstName}  ${lastName}  ${email}  ${user_role}
    the user enters text to a text field                   id = firstName  ${firstName}
    the user enters text to a text field                   id = lastName  ${lastName}
    the user enters text to a text field                   id = email  ${email}
    the user selects the value from the drop-down menu     ${user_role}  id = role
    the user clicks the button/link                        jquery = button:contains("Send invitation")

the user can see multiple user dashboard
     the user should see the element     jQuery = h2:contains("Projects")
     the user should see the element     jQuery = h2:contains("Applications")

the user provides details and creates an account
     [Arguments]  ${firstName}  ${lastname}
     validation error messages check
     The user fills in account details     ${firstName}  ${lastname}
     the user clicks the button/link       jQuery = button:contains("Create account")
     the user should see the element       jQuery = h1:contains("${externalUserEmailVerificationSubject}")

validation error messages check
    the user clicks the button/link     jQuery = button:contains("Create account")
    the user should see the element     jQuery = h2:contains("${createAccountValidationError}")
    the user should see the element     link = ${phoneNumberValidation}
    the user should see the element     link = ${tAndCValidation}



