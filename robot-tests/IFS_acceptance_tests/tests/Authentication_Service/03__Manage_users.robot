*** Settings ***
Documentation     IFS-604: IFS Admin user navigation to Manage users section
...
...               IFS-606: Manage internal users: Read only view of internal user profile
...
...               IFS-27:  Invite new internal user
...
...               IFS-642: Email to new internal user inviting them to register
...
...               IFS-643: Complete internal user registration
...
...               IFS-644: Disable or reenable user profile
...
...               IFS-983: Manage users: Pending registration tab
...
...               IFS-2412: Internal users resend invites
...
...               IFS-2842: Add modals to the resending of invites to internal users
...
...               IFS-1944: Internal - Invite internal user - error field is missing
...
...               IFS-7160  CSS & Admins cannot amend email addresses if there are pending invites in the service
...
...               IFS-7483 Inactive innovation lead appearing in list of available innovation leads
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Administrator  CompAdmin  ATS2020
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/PS_Common.robot
# NOTE: Please do not use hard coded email in this suite. We always need to check local vs remote for the difference in the domain name !!!

*** Variables ***
${localEmailInvtedUser}      ifs.innovationLead@innovateuk.ukri.test
${remoteEmailInvtedUser}     ifs.innovationLead@innovateuk.ukri.org
${invalidEmail}              test@test.com
${adminChangeEmailOld}       aaron.powell@example.com
${adminChangeEmailNew}       aaron.powell2@example.com
${supportChangeEmailOld}     jacqueline.white@gmail.com
${supportChangeEmailNew}     jacqueline.white2@gmail.com
${newPendingEmail}           gintare@tester.com
${emailToChange}             steve.smith@empire.com

*** Test Cases ***
Admin cannot change email to an existing email
    [Documentation]  IFS-6380
    [Setup]  The user logs-in in new browser           &{ifs_admin_user_credentials}
    Given the user clicks the button/link              link = Manage users
    When the user enters a different email address     steve.smith@empire.com
    And the user clicks the button/link                jQuery = button:contains("Save and return")
    Then the user confirms email change
    And the user should see the element                 jQuery = li:contains("This email address is already in use")

Admin can change email
    [Documentation]  IFS-6380
    Given the user clicks the button/link        link = Manage users
    When the user clicks the button/link         jQuery = .user-profile:contains("${adminChangeEmailOld}") a:contains("Edit")
    And the user enters text to a text field     id = email  ${adminChangeEmailNew}
    And the user clicks the button/link          jQuery = button:contains("Save and return")
    Then the user confirms email change

User cannot sign in with old email
    [Documentation]  IFS-6380
    [Setup]  Logout as user
    Given the guest user inserts user email and password     ${adminChangeEmailOld}  ${short_password}
    When The guest user clicks the log-in button
    Then the user should see the element                     jQuery = .govuk-error-summary:contains("${unsuccessful_login_message}")

User can sign in with new email
    [Documentation]  IFS-6380
    Given Logging in and Error Checking      ${adminChangeEmailNew}  ${short_password}
    Then the user should see the element     link = Office Chair for Life

Admin cannot change email to an email which is a pending team member invitation to an organisation in the application
    [Documentation]   IFS-7160
    [Setup]  log in as a different user                                                         &{lead_applicant_credentials}
    Given the user adds a pending partner to the organisation                                   ${server}/application/${OPEN_COMPETITION_APPLICATION_6_NUMBER}/form/question/507/team  Add person to ${EMPIRE_LTD_NAME}
    When log in as a different user                                                             &{ifs_admin_user_credentials}
    Then the internal user isnt able to update an existing users email with a pending email     ${emailToChange}
    [Teardown]  the external user removes the pending parter invitation                         ${server}/application/${OPEN_COMPETITION_APPLICATION_6_NUMBER}/form/question/507/team

Admin cannot change email to an email which is a pending invitation to an organisation in the application
    [Documentation]   IFS-7160
    [Setup]  log in as a different user                                                         &{lead_applicant_credentials}
    Given the user adds a new partner organisation in application                               ${server}/application/${OPEN_COMPETITION_APPLICATION_6_NUMBER}/form/question/507/team  Testing Pending Organisation  Name Surname  ${newPendingEmail}
    When log in as a different user                                                             &{ifs_admin_user_credentials}
    Then the internal user isnt able to update an existing users email with a pending email     ${emailToChange}
    And log in as a different user                                                              &{lead_applicant_credentials}
    [Teardown]  the user removes the pending organisation invitation                            ${server}/application/${OPEN_COMPETITION_APPLICATION_6_NUMBER}/form/question/507/team

Admin cannot change email to an email when there is a pending team member invitation to an organisation in project setup
    [Documentation]   IFS-7160
    [Setup]  log in as a different user                                                         &{lead_applicant_credentials}
    Given the user adds a pending partner to the organisation                                   ${server}/project-setup/project/1/team  Add team member
    When log in as a different user                                                             &{ifs_admin_user_credentials}
    Then the internal user isnt able to update an existing users email with a pending email     ${emailToChange}
    And the external user removes the pending parter invitation                                 ${server}/project-setup/project/1/team

Support can change email address
    [Documentation]  IFS-6380  IFS-6928
    Given Log in as a different user         &{support_user_credentials}
    When The user clicks the button/link     link = Manage users
    And the user clicks the button/link      jQuery = .pagination-links a:contains("6")
    And the user changes email address
    Then the user confirms email change

Administrator can successfully invite a new user
    [Documentation]  IFS-27 IFS-983
    [Tags]  HappyPath
     [Setup]  Log in as a different user                        &{ifs_admin_user_credentials}
    Given the user navigates to the page                        ${server}/management/admin/invite-user
    And the IFS admin send invite to internal user              Support  User  IFS Administrator
    Then the user cannot see a validation error in the page
    And the user should see the element                         jQuery = h1:contains("Manage users")
    #The Admin is redirected to the Manage Users page on Success
    And the user should see the element                         jQuery = .govuk-tabs__list-item--selected:contains("Pending")

Administrator can successfully finish the rest of the invitation
    [Documentation]  IFS-27  IFS-983  IFS-2412  IFS-2842
    [Tags]  HappyPath
    Given the user resends the invite
    When the user should see the element                jQuery = td:contains("Support User") ~ td:contains("IFS Administrator") ~ td:contains("${email}")
    Then the IFS admin mark user as Active/Inactive
    [Teardown]  Logout as user

Account creation validation checks - Blank
    [Documentation]  IFS-643  IFS-642
    Given the user reads his email and clicks the link              ${email}  Invitation to Innovation Funding Service  Your Innovation Funding Service account has been created.
    And the user clicks the button/link                             jQuery = .govuk-button:contains("Create account")
    And the use should see the validation error summary             Password must be at least 8 characters
    When the internal user enters the details to create account
    And Set Focus To Element                                        css = #lastName
    Then the user cannot see a validation error in the page

Account creation validation checks - Lowercase password
    [Documentation]  IFS-3554
    Given the user enters text to a text field             id = password  PASSWORD123
    When The user clicks the button/link                   jQuery = .govuk-button:contains("Create account")
    Then The user should see a field and summary error     Password must contain at least one lower case letter.
    [Teardown]  the user enters text to a text field       css = #password  ${short_password}

New user account is created and verified
    [Documentation]  IFS-643 IFS-983
    Given the user clicks the button/link                          jQuery = .govuk-button:contains("Create account")
    And the user should see the element                            jQuery = h1:contains("Your account has been created")
    When the user clicks the button/link                           jQuery = .govuk-button:contains("Sign into your account")
    Then the new internal user logs in and checks user details

Inviting the same user for the same role again should give an error
    [Documentation]  IFS-27
    Given log in as a different user                    &{ifs_admin_user_credentials}
    When the IFS admin send invite to internal user     New  Administrator  IFS Administrator
    Then the user should see a summary error            This email address is already in use.

Inviting the same user for the different role again should also give an error
    [Documentation]  IFS-27
    Given the IFS admin send invite to internal user     Project  Finance  Project Finance
    Then the user should see a summary error             This email address is already in use.

Administrator can successfully edit internal user details
    [Documentation]  IFS-18
    [Tags]  InnovationLead
    [Setup]  log in as a different user                                     &{ifs_admin_user_credentials}
    Given the user navigates to the View internal user details              ${email}  active
    When the IFS admin edit internal user details
    Then the user cannot see a validation error in the page
    And the IFS admin is redirected to the Manage Users page on Success

The internal user can login with his new role and sees no competitions assigned
    [Documentation]  IFS-1305  IFS-1308
    [Tags]  InnovationLead
    Given Log in as a different user         ${email}  ${short_password}
    Then the user should see the element     jQuery = p:contains("There are no competitions assigned to you.")
    And the user clicks the button/link      css = #section-4 a  #Project setup tab

Administrator is able to disable internal users
    [Documentation]  IFS-644
    Given log in as a different user                              &{ifs_admin_user_credentials}
    When the user navigates to the View internal user details     ${email}  active
    And the IFS admin deactivate the user
    And the user navigates to the page                           ${server}/management/admin/users/inactive
    Then the user should see the element                          jQuery = p:contains("${email}")  #Checking the user swapped tab

Deactivate external user
    [Documentation]  IFS-6380
    Given the user navigates to the View internal user details     ${adminChangeEmailNew}  active
    And the IFS admin deactivate the user
    When the user navigates to the page                            ${server}/management/admin/users/inactive
    Then the user should see the element                           jQuery = p:contains("${adminChangeEmailNew}")
    [Teardown]  Logout as user

Deactivated internal user cannot login until activated
    [Documentation]  IFS-6380
    Given the user cannot login with their new details            ${adminChangeEmailNew}  Passw0rd
    When Logging in and Error Checking                            &{ifs_admin_user_credentials}
    Then the user navigates to the View internal user details     ${adminChangeEmailNew}  inactive
    And the IFS admin reactivate the user                         ${adminChangeEmailNew}
    And Log in as a different user                                ${adminChangeEmailNew}   ${short_password}
    Then The user should see the element                          link = Office Chair for Life

Deactivated external user cannot login until activated
    [Documentation]  IFS-644
    [Setup]  the user logs out if they are logged in
    Given the user cannot login with their new details            ${email}  ${short_password}
    When Logging in and Error Checking                            &{ifs_admin_user_credentials}
    Then the user navigates to the View internal user details     ${email}  inactive
    And the IFS admin reactivate the user                         ${email}
    When log in as a different user                               ${email}  ${short_password}
    Then the user should not see an error in the page

Deactivated innovation lead cannot be selected on manage innovation page
    [Documentation]  IFS-7483
    [Setup]  log in as a different user          &{ifs_admin_user_credentials}
    Given the user navigates to the page         ${server}/management/competition/setup/${openCompetitionBusinessRTO}/manage-innovation-leads/overview
    And the user should not see the element      jQuery = tr:contains("Ralph Nunes")
    When the user clicks the button/link         jQuery = a:contains("Added to competition")
    Then the user should not see the element     jQuery = tr:contains("Ralph Nunes")

Deactivated innovation lead cannot be selected on initial details
    [Documentation]  IFS-7483
    Given the user navigates to the page         ${server}/management/competition/setup/${openCompetitionBusinessRTO}/section/initial
    When the user clicks the button/link         css = button[type=submit]
    Then the user should not see the element     jQuery = option:contains("Ralph Nunes")

*** Keywords ***
the user adds a new partner organisation in application
    [Arguments]  ${navigateTo}  ${partnerOrgName}  ${persFullName}  ${email}
    the user navigates to the page             ${navigateTo}
    the user clicks the button/link            link=Add a partner organisation
    the user enters text to a text field       id = organisationName  ${partnerOrgName}
    the user enters text to a text field       id = name  ${persFullName}
    the user enters text to a text field       id = email  ${email}
    the user clicks the button/link            jQuery = .govuk-button:contains("Invite partner organisation")
    the user should see the element            jQuery = h2:contains(${partnerOrgName})

the user adds a pending partner to the organisation
     [Arguments]  ${navigateTo}   ${teamMemberSelector}
     the user navigates to the page      ${navigateTo}
     the user clicks the button/link     jQuery = button:contains("${teamMemberSelector}")
     The user adds a new team member     Tester   ${newPendingEmail}
     the user should see the element     jQuery = td:contains("Tester (pending for 0 days)") ~ td:contains("${newPendingEmail}")

the external user removes the pending parter invitation
    [Arguments]  ${pageToRemoveFrom}
    log in as a different user          &{lead_applicant_credentials}
    the user navigates to the page      ${pageToRemoveFrom}
    the user clicks the button/link     jQuery = td:contains("(pending for 0 days)")~ td button:contains("Remove")

the user removes the pending organisation invitation
    [Arguments]  ${pageToRemoveFrom}
    the user navigates to the page      ${pageToRemoveFrom}
    the user clicks the button/link     jQuery = td:contains("(pending for 0 days)")~ td a:contains("Remove organisation")
    the user clicks the button/link     jQuery = .warning-modal[aria-hidden=false] button:contains("Remove organisation")

the internal user isnt able to update an existing users email with a pending email
    [Arguments]   ${emailToBeChanged}
    the user navigates to the page           ${server}/management/admin/users/active
    the user enters text to a text field     id = filter   ${emailToBeChanged}
    the user clicks the button/link          css = input[type="submit"]
    the user clicks the button/link          jQuery = .user-profile:contains("${emailToBeChanged}") a:contains("Edit")
    the user enters text to a text field     id = email   ${newPendingEmail}
    the user clicks the button/link          jQuery = button:contains("Save and return")
    the user confirms email change
    the user should see a summary error      The new email address has already been used to invite a partner to application

Requesting Project ID of this Project
    ${ProjectIDs} =  get project id by name    Mobile Phone Data for Logistics Analytics
    Set suite variable    ${ProjectIDs}

Custom suite setup
    ${today} =  get today
    set suite variable  ${today}
    ${email} =  Set variable if  ${docker} == 1     ${localEmailInvtedUser}
    ...  ELSE  ${email} = ${remoteEmailInvtedUser}
    set suite variable   ${email}

the user navigates to the View internal user details
    [Arguments]  ${user}  ${status}
    the user navigates to the page           ${server}/management/admin/users/${status}
    the user enters text to a text field     id = filter  ${user}
    the user clicks the button/link          css = input[type="submit"]
    the user clicks the button/link          jQuery = .user-profile:contains("${user}") a:contains("Edit")

the user resends the invite
    the user clicks the button/link     jQuery = button:contains("Resend invite")     #Resend invite
    the user clicks the button/link     jQuery = button:contains("Resend")
    the user reads his email            ${email}  Invitation to Innovation Funding  Your Innovation Funding Service

the use should see the validation error summary
    [Arguments]  ${error_message}
    The user should see a field and summary error     ${enter_a_first_name}
    The user should see a field and summary error     ${enter_a_last_name}
    The user should see a field and summary error     ${error_message}

the IFS admin send invite to internal user
    [Arguments]  ${first_name}  ${last_name}  ${user_role}
    the user navigates to the page                          ${server}/management/admin/invite-user
    the user enters text to a text field                    id = firstName  ${first_name}
    the user enters text to a text field                    id = lastName  ${last_name}
    the user enters text to a text field                    id = emailAddress  ${email}
    the user selects the option from the drop-down menu     ${user_role}  id = role
    the user clicks the button/link                         jQuery = .govuk-button:contains("Send invite")

the IFS admin edit internal user details
    the user enters text to a text field                    id = firstName  Innovation
    the user enters text to a text field                    id = lastName  Lead
    the user selects the option from the drop-down menu     Innovation Lead  id = role
    the user clicks the button/link                         jQuery = .govuk-button:contains("Save and return")

the IFS admin mark user as Active/Inactive
    the user clicks the button/link          jQuery = a:contains("Active")
    the user should not see the element      jQuery = td:contains("Support User") ~ td:contains("IFS Administrator")
    the user clicks the button/link          jQuery = a:contains("Inactive")
    the user should not see the element      jQuery = td:contains("Support User") ~ td:contains("IFS Administrator")

the internal user enters the details to create account
    the user enters text to a text field     css = #firstName  New
    the user enters text to a text field     css = #lastName  Administrator
    the user enters text to a text field     css = #password  ${short_password}
    the user should see the element          jQuery = h3:contains("Email") + p:contains("${email}")

the new internal user logs in and checks user details
    Logging in and Error Checking            ${email}  ${short_password}
    the user clicks the button/link          jQuery = a:contains("Manage users")
    the user enters text to a text field     id = filter  ${email}
    the user clicks the button/link          css = input[type="submit"]
    the user clicks the button/link          jQuery = .user-profile:contains("New Administrator") a:contains("Edit")
    the user should see the element          jQuery = label:contains("First name") ~ input[value="New"]
    the user should see the element          jQuery = label:contains("Last name") ~ input[value="Administrator"]
    the user should see the element          jQuery = label:contains("Email") + input[value^="${email}"]
    the user should see the element          jQuery = option[value="IFS_ADMINISTRATOR"][selected="selected"]
    the user clicks the button/link          link = Manage users
    the user clicks the button/link          jQuery = a:contains("Pending")
    the user should see the element          jQuery = span:contains("0") + span:contains("pending internal users")
    the user should not see the element      css = .table-overflow ~ td
    the user clicks the button/link          jQuery = a:contains("Active")

the IFS admin is redirected to the Manage Users page on Success
    the user should see the element     jQuery = h1:contains("Manage users")
    the user should see the element     jQuery = .govuk-tabs__list-item--selected:contains("Active")

the IFS admin deactivate the user
    the user should see the element     css = .govuk-form-group input
    the user clicks the button/link     jQuery = button:contains("Deactivate user")
    the user clicks the button/link     jQuery = button:contains("Cancel")
    the user clicks the button/link     jQuery = button:contains("Deactivate user")
    the user clicks the button/link     jQuery = button:contains("Yes, deactivate")
    the user should see the element     jQuery = .form-footer *:contains("Reactivate user") + *:contains("Deactivated by Arden Pimenta on ${today}")

the IFS admin reactivate the user
    [Arguments]   ${user}
    the user clicks the button/link          jQuery = button:contains("Reactivate user")
    the user clicks the button/link          jQuery = button:contains("Yes, reactivate")
    the user navigates to the page           ${server}/management/admin/users/active
    the user enters text to a text field     id = filter  ${user}
    the user clicks the button/link          css = input[type="submit"]
    the user should see the element          jQuery = p:contains("${user}")  #Checking the user swapped tab

the administrator sees the external user details
    the user should see the element     jQuery = h1:contains("View user details")
    the user should see the element     jQuery = dd:contains("Aaron") ~ dd:contains("Powell")
    the user should see the element     jQuery = td:contains("Applicant") ~ td:contains("Active")

the user confirms email change
    the user selects the checkbox       confirmation
    the user clicks the button/link     id = confirm-email-change

the user enters a different email address
    [Arguments]   ${email_address}
    the user enters text to a text field     id = filter  doe
    the user clicks the button/link          css = input[type="submit"]
    the user clicks the button/link          jQuery = .user-profile:contains("John") a:contains("Edit")
    the user enters text to a text field     id = email  ${email_address}

the user changes email address
    the user clicks the button/link          jQuery = .user-profile:contains("${supportChangeEmailOld}") a:contains("Edit")
    the user enters text to a text field     id = email  ${supportChangeEmailNew}
    the user clicks the button/link          jQuery = button:contains("Save and return")