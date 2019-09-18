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
...               IFS-50 Change an existing unsuccessful application into a successful project in setup
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Administrator  CompAdmin
Resource          ../../resources/defaultResources.robot

# NOTE: Please do not use hard coded email in this suite. We always need to check local vs remote for the difference in the domain name !!!

*** Variables ***
${localEmailInvtedUser}   ifs.innovationLead@innovateuk.ukri.test
${remoteEmailInvtedUser}  ifs.innovationLead@innovateuk.ukri.org
${invalidEmail}           test@test.com

*** Test Cases ***
Administrator can navigate to manage users page
    [Documentation]    INFUND-604
    [Tags]  HappyPath
    [Setup]  The user logs-in in new browser  &{ifs_admin_user_credentials}
    Given the user clicks the button/link     link = Manage users
    Then the user should see the element      jQuery = h1:contains("Manage users")
    And the user should see the element       jQuery = .govuk-tabs__tab--selected:contains("Active")

Administrator can see the read only view of internal user profile
    [Documentation]  INFUND-606
    [Tags]
    Given the user clicks the button/link  link = John Doe
    Then the user should see the internal user details

Project finance user cannot navigate to manage users page
    [Documentation]  INFUND-604
    [Tags]  HappyPath
    Given User cannot see manage users page   &{Comp_admin1_credentials}
    And User cannot see manage users page     &{internal_finance_credentials}

Server side validation for invite new internal user
    [Documentation]  IFS-27
    [Tags]
    [Setup]  Log in as a different user                 &{ifs_admin_user_credentials}
    Given the user navigates to the page                ${server}/management/admin/users/active
    When the user clicks the button/link                link = Invite a new internal user
    And the user clicks the button/link                 jQuery = button:contains("Send invite")
    Then the use should see the validation error summary    Please enter an email address.

The user must use an Innovate UK email
    [Documentation]  IFS-1944
    [Tags]  HappyPath
    [Setup]  Log in as a different user                   &{ifs_admin_user_credentials}
    Given the user navigates to the page                  ${server}/management/admin/users/active
    When the IFS admin invites a new internal user
    Then the user should see a field and summary error    Users cannot be registered without an Innovate UK email address.
    [Teardown]  the user clicks the button/link           link = Cancel

Client side validations for invite new internal user
    [Documentation]  IFS-27
    [Tags]
    Given the user navigates to the page       ${server}/management/admin/invite-user
    Then the user enters the text and checks for validation message   firstName  A  ${enter_a_first_name}  Your first name should have at least 2 characters.
    And the user enters the text and checks for validation message    lastName  D  ${enter_a_last_name}  Your last name should have at least 2 characters.
    And the user enters the text and checks for validation message    emailAddress  astle  Please enter an email address.  ${enter_a_valid_email}

Administrator can successfully invite a new user
    [Documentation]  IFS-27 IFS-983
    [Tags]  HappyPath
    Given the IFS admin send invite to internal user         Support  User  IFS Administrator
    Then the user cannot see a validation error in the page
    And the user should see the element                     jQuery = h1:contains("Manage users")
    #The Admin is redirected to the Manage Users page on Success
    And the user should see the element                     jQuery = .govuk-tabs__tab--selected:contains("Pending")

Administrator can successfully finish the rest of the invitation
    [Documentation]  IFS-27  IFS-983  IFS-2412  IFS-2842
    [Tags]  HappyPath
    Given the user resends the invite
    When the user verifies pending tab content
    Then the IFS admin mark user as Active/Inactive
    [Teardown]  close any open browsers

Invited user can receive the invitation
    [Documentation]  IFS-642
    [Tags]  HappyPath
    Given the guest user opens the browser
    Then The invitee reads his email and clicks the link     Invitation to Innovation Funding Service  Your Innovation Funding Service account has been created.

Account creation validation checks - Blank
    [Documentation]  IFS-643
    [Tags]
    Given the user clicks the button/link                 jQuery = .govuk-button:contains("Create account")
    And the use should see the validation error summary   Password must be at least 8 characters
    When the internal user enters the details to create account
    Set Focus To Element                                        css = #lastName
    Then the user cannot see a validation error in the page

Account creation validation checks - Lowercase password
    [Documentation]  IFS-3554
    [Tags]
    Given the user enters text to a text field  id = password  PASSWORD123
    When The user clicks the button/link        jQuery = .govuk-button:contains("Create account")
    Then The user should see a field and summary error  Password must contain at least one lower case letter.
    [Teardown]  the user enters text to a text field   css = #password  ${correct_password}

New user account is created and verified
    [Documentation]  IFS-643 IFS-983
    [Tags]
    Given the user clicks the button/link      jQuery = .govuk-button:contains("Create account")
    Then the user should see the element       jQuery = h1:contains("Your account has been created")
    When the user clicks the button/link       jQuery = .govuk-button:contains("Sign into your account")
    Then the new internal user logs in and checks user details

Inviting the same user for the same role again should give an error
    [Documentation]  IFS-27
    [Tags]
    Given log in as a different user                  &{ifs_admin_user_credentials}
    When the IFS admin send invite to internal user   New  Administrator  IFS Administrator
    Then the user should see a summary error          This email address is already in use.

Inviting the same user for the different role again should also give an error
    [Documentation]  IFS-27
    [Tags]
    Given the IFS admin send invite to internal user   Project  Finance  Project Finance
    Then the user should see a summary error          This email address is already in use.

Administrator can navigate to edit page to edit the internal user details
    [Documentation]  IFS-18
    [Tags]
    Given the user navigates to the View internal user details  New Administrator  active
    When the user clicks the button/link         link = Edit
    Then the IFS admin should see the user details

Server side validation for edit internal user details
    [Documentation]  IFS-18
    [Tags]
    Given the user enters text to a text field  id = firstName  ${empty}
    And the user enters text to a text field    id = lastName  ${empty}
    When the user clicks the button/link        jQuery = button:contains("Save and return")
    Then the user should see a field and summary error      ${enter_a_first_name}
    And the user should see a field and summary error       Your first name should have at least 2 characters.
    And the user should see a field and summary error       ${enter_a_last_name}
    And the user should see a field and summary error       Your last name should have at least 2 characters.

Client side validations for edit internal user details
    [Documentation]  IFS-18
    [Tags]
    Given the user enters the text and checks for validation message    firstName  A  ${enter_a_first_name}  Your first name should have at least 2 characters.
    And the user enters the text and checks for validation message      lastName  D  ${enter_a_last_name}  Your last name should have at least 2 characters.

Administrator can successfully edit internal user details
    [Documentation]  IFS-18
    [Tags]  InnovationLead
    [Setup]  log in as a different user                      &{ifs_admin_user_credentials}
    Given the user navigates to the View internal user details  New Administrator  active
    And the user clicks the button/link                      link = Edit
    When the IFS admin edit internal user details
    Then the user cannot see a validation error in the page
    And the IFS admin is redirected to the Manage Users page on Success
    [Teardown]  the user logs out if they are logged in

The internal user can login with his new role and sees no competitions assigned
    [Documentation]  IFS-1305  IFS-1308
    [Tags]  InnovationLead
    Given the invited user logs in
    Then the user should see the element           jQuery = p:contains("There are no competitions assigned to you.")
    And the user clicks the button/link            css = #section-4 a  #Project setup tab

Administrator is able to disable internal users
    [Documentation]  IFS-644
    [Tags]
    Given log in as a different user     &{ifs_admin_user_credentials}
    When the user navigates to the View internal user details  Innovation Lead  active
    Then the IFS admin deactivate the internal user
    When the user navigates to the page   ${server}/management/admin/users/inactive
    Then the user should see the element  jQuery = tr:contains("Innovation Lead")  #Checking the user swapped tab

Deactivated user cannot login until he is activated
    [Documentation]  IFS-644
    [Tags]
    [Setup]  the user logs out if they are logged in
    Given the deactivated user is not able to login
    When Logging in and Error Checking                  &{ifs_admin_user_credentials}
    Then the user navigates to the View internal user details  Innovation Lead  inactive
    And the IFS admin reactivate the internal user
    When the re-activated user tries to login
    Then the user should not see an error in the page

Administrator is able to mark as successful an unsuccessful application
    [Documentation]  IFS-50
    [Tags]
    [Setup]  log in as a different user      &{ifs_admin_user_credentials}
    Given the user navigates to the page     ${server}/management/competition/${PROJECT_SETUP_COMPETITION}/previous
    Then the user should be allowed to only reinstate Unsuccessful applications
    When the user clicks the button/link     jQuery = td:contains("Cleaning Product packaging") ~ td a:contains("Mark as successful")
    And the user clicks the button/link      css = .govuk-button[name="mark-as-successful"]  # I'm sure button
    Then the user should no longer see the application is capable of being marked as successful

*** Keywords ***
Custom suite setup
    ${today} =  get today
    set suite variable  ${today}

User cannot see manage users page
    [Arguments]  ${email}  ${password}
    Log in as a different user  ${email}  ${password}
    the user should not see the element   link = Manage users
    the user navigates to the page and gets a custom error message  ${USER_MGMT_URL}  ${403_error_message}

the user fills in the email address for the invitee
    # Locally the accepted domain is innovateuk.ukri.test
    run keyword if  ${docker} == 1  the user enters text to a text field  id = emailAddress  ${localEmailInvtedUser}
    # On production the accepted domain is innovateuk.ukri.org
    run keyword if  ${docker}!= 1  the user enters text to a text field  id = emailAddress  ${remoteEmailInvtedUser}

The invitee reads his email and clicks the link
    [Arguments]  ${title}  ${pattern}
    # Locally the accepted domain is innovateuk.ukri.test
    run keyword if  ${docker} == 1  The user reads his email and clicks the link  ${localEmailInvtedUser}  ${title}  ${pattern}
    # On production the accepted domain is innovateuk.ukri.org
    run keyword if  ${docker}!= 1  The user reads his email and clicks the link  ${remoteEmailInvtedUser}  ${title}  ${pattern}

the invited user logs in
    # Locally the accepted domain is innovateuk.ukri.test
    run keyword if  ${docker} == 1  Logging in and Error Checking  ${localEmailInvtedUser}  ${correct_password}
    # On production the accepted domain is innovateuk.ukri.org
    run keyword if  ${docker}!= 1  Logging in and Error Checking  ${remoteEmailInvtedUser}  ${correct_password}

the user verifies pending tab content
    # Locally the accepted domain is innovateuk.ukri.test
    run keyword if  ${docker} == 1  the user should see the element  jQuery = td:contains("Support User") ~ td:contains("IFS Administrator") ~ td:contains("${localEmailInvtedUser}")
    # On production the accepted domain is innovateuk.ukri.org
    run keyword if  ${docker}!= 1  the user should see the element  jQuery = td:contains("Support User") ~ td:contains("IFS Administrator") ~ td:contains("${remoteEmailInvtedUser}")

the user navigates to the View internal user details
    [Arguments]  ${user}  ${status}
    the user navigates to the page   ${server}/management/admin/users/${status}
    the user clicks the button/link  link = ${user}

the deactivated user is not able to login
    # Locally the accepted domain is innovateuk.ukri.test
    run keyword if  ${docker} == 1  the user cannot login with their new details  ${localEmailInvtedUser}  ${correct_password}
    # On production the accepted domain is innovateuk.ukri.org
    run keyword if  ${docker}!= 1  the user cannot login with their new details  ${remoteEmailInvtedUser}  ${correct_password}

the re-activated user tries to login
    # Locally the accepted domain is innovateuk.ukri.test
    run keyword if  ${docker} == 1  log in as a different user  ${localEmailInvtedUser}  ${correct_password}
    # On production the accepted domain is innovateuk.ukri.org
    run keyword if  ${docker}!= 1  log in as a different user  ${remoteEmailInvtedUser}  ${correct_password}

the user resends the invite
    the user clicks the button/link    css = .button-secondary[type = "submit"]     #Resend invite
    the user clicks the button/link    jQuery = button:contains("Resend")
    the user reads his email           ${localEmailInvtedUser}  Invitation to Innovation Funding  Your Innovation Funding Service

the user should see the internal user details
    the user should see the element   jQuery = h1:contains("View internal user details")
    the user should see the element   jQuery = dt:contains("Email address") + dd:contains("${Comp_admin1_credentials["email"]}")
    the user should see the element   jQuery = dt:contains("Role") + dd:contains("Competition Administrator")
    the user should see the element   jQuery = .form-footer__info:contains("Created by IFS Web System User")

the use should see the validation error summary
    [Arguments]  ${error_message}
    The user should see a field and summary error   ${enter_a_first_name}
    The user should see a field and summary error   ${enter_a_last_name}
    The user should see a field and summary error   ${error_message}

the IFS admin invites a new internal user
    the user clicks the button/link             link = Invite a new internal user
    the user enters text to a text field        id = firstName  Support
    the user enters text to a text field        id = lastName  User
    the user enters text to a text field        id = emailAddress  ${invalidEmail}
    the user clicks the button/link             jQuery = button:contains("Send invite")

the user enters the text and checks for validation message
    [Arguments]  ${field_id}  ${text}  ${error_message1}  ${error_message2}
    the user enters text to a text field    id = ${field_id}  ${text}
    the user should not see the element     jQuery = .govuk-error-message:contains("${error_message1}")
    the user should see the element         jQuery = .govuk-error-message:contains("${error_message2}")

the IFS admin send invite to internal user
    [Arguments]  ${first_name}  ${last_name}  ${user_role}
    the user navigates to the page              ${server}/management/admin/invite-user
    the user enters text to a text field                 id = firstName  ${first_name}
    the user enters text to a text field                 id = lastName  ${last_name}
    the user fills in the email address for the invitee
    the user selects the option from the drop-down menu  ${user_role}  id = role
    the user clicks the button/link                      jQuery = .govuk-button:contains("Send invite")

the IFS admin edit internal user details
    the user enters text to a text field                 id = firstName  Innovation
    the user enters text to a text field                 id = lastName  Lead
    the user selects the option from the drop-down menu  Innovation Lead  id = role
    the user clicks the button/link                      jQuery = .govuk-button:contains("Save and return")

the IFS admin mark user as Active/Inactive
    the user clicks the button/link          jQuery = a:contains("Active")
    the user should not see the element      jQuery = td:contains("Support User") ~ td:contains("IFS Administrator")
    the user clicks the button/link          jQuery = a:contains("Inactive")
    the user should not see the element      jQuery = td:contains("Support User") ~ td:contains("IFS Administrator")

the internal user enters the details to create account
    the user enters text to a text field   css = #firstName  New
    the user enters text to a text field   css = #lastName  Administrator
    the user enters text to a text field   css = #password  ${correct_password}
    the user should see the element        jQuery = h3:contains("Email") + p:contains("ifs.innovationLead@innovateuk")

the new internal user logs in and checks user details
    the invited user logs in
    the user clicks the button/link        jQuery = a:contains("Manage users")
    the user clicks the button/link        jQuery = a:contains("New Administrator")
    the user should see the element        jQuery = dt:contains("Full name") + dd:contains("New Administrator")
    the user should see the element        jQuery = dt:contains("Email") + dd:contains("ifs.innovationLead@innovateuk")
    the user should see the element        jQuery = dt:contains("Role") + dd:contains("IFS Administrator")
    the user clicks the button/link        jQuery = a:contains("Manage users")
    the user clicks the button/link        jQuery = a:contains("Pending")
    the user should see the element        jQuery = span:contains("0") + span:contains("pending internal users")
    the user should not see the element    css = .table-overflow ~ td
    the user clicks the button/link        jQuery = a:contains("Active")

the IFS admin should see the user details
    the user should see the element           jQuery = h1:contains("Edit internal user details")
    the user should see the element           css = #firstName[value = "New"]
    the user should see the element           css = #lastName[value = "Administrator"]
    the user should see the element           jQuery = dt:contains("Email address") ~ dd:contains("ifs.innovationLead")
    the user should see the dropdown option selected  IFS Administrator  id = role

the IFS admin is redirected to the Manage Users page on Success
    the user should see the element    jQuery = h1:contains("Manage users")
    the user should see the element    jQuery = .govuk-tabs__tab--selected:contains("Active")
    the user should see the element    jQuery = td:contains("Innovation Lead") + td:contains("Innovation Lead")

the IFS admin deactivate the internal user
    the user clicks the button/link    link = Edit
    the user should see the element    css = .govuk-form-group input
    the user clicks the button/link    jQuery = button:contains("Deactivate user")
    the user clicks the button/link    jQuery = button:contains("Cancel")
    the user clicks the button/link    jQuery = button:contains("Deactivate user")
    the user clicks the button/link    jQuery = button:contains("Yes, deactivate")
    the user should see the element    jQuery = .form-footer *:contains("Reactivate user") + *:contains("Deactivated by Arden Pimenta on ${today}")

the IFS admin reactivate the internal user
    the user clicks the button/link     jQuery = button:contains("Reactivate user")
    the user clicks the button/link     jQuery = button:contains("Yes, reactivate")
    the user navigates to the page      ${server}/management/admin/users/active
    the user should see the element     jQuery = tr:contains("Innovation Lead")  #Checking the user swapped tab

The user should be allowed to only reinstate Unsuccessful applications
    the user clicks the button/link     jQuery = button:contains("Applications")
    the user should see the element     jQuery = td:contains("Unsuccessful") ~ td a:contains("Mark as successful")

the user should no longer see the application is capable of being marked as successful
    the user should not see the element  jQuery = td:contains("Unsuccessful") ~ td a:contains("Mark as successful")
    the user navigates to the page       ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/all
    the user should see the element      jQuery = th:contains("Cleaning Product packaging")