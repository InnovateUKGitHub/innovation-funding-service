*** Settings ***
Documentation     IFS-604: IFS Admin user navigation to Manage users section
...               IFS-605: Manage internal users: List of all internal users and roles
...               IFS-606: Manage internal users: Read only view of internal user profile
...               IFS-27:  Invite new internal user
...               IFS-642: Email to new internal user inviting them to register
...               IFS-643: Complete internal user registration
...               IFS-644: Disable or reenable user profile
...               IFS-983: Manage users: Pending registration tab
Suite Teardown    the user closes the browser
Force Tags        Administrator  CompAdmin
Resource          ../../resources/defaultResources.robot
# NOTE: Please do not use hard coded email in this suite. We always need to check local vs remote for the difference in the domain name !!!

*** Variables ***
${localEmailInvtedUser}   ifs.administrator@innovateuk.test
${remoteEmailInvtedUser}  ifs.administrator@innovateuk.gov.uk

*** Test Cases ***
Administrator can navigate to manage users page
    [Documentation]    INFUND-604
    [Tags]  HappyPath
    [Setup]  The user logs-in in new browser  &{ifs_admin_user_credentials}
    When the user clicks the button/link      link=Manage users
    Then the user should see the element      jQuery=h1:contains("Manage users")
    And the user should see the element       jQuery=.selected:contains("Active")

Administrator can see the read only view of internal user profile
    [Documentation]  INFUND-606
    [Tags]
    When the user clicks the button/link  link=John Doe
    Then the user should see the element  jQuery=h1:contains("View internal user's details")
    And the user should see the element   jQuery=dt:contains("Email address") + dd:contains("${Comp_admin1_credentials["email"]}")
    And the user should see the element   jQuery=dt:contains("Role") + dd:contains("Competition Administrator")
    And the user should see the element   jQuery=.form-footer__info:contains("Created by IFS Web System User")

Project finance user cannot navigate to manage users page
    [Documentation]  INFUND-604
    [Tags]
    User cannot see manage users page   &{Comp_admin1_credentials}
    User cannot see manage users page   &{internal_finance_credentials}

Administrator can invite a new Internal User
    [Documentation]  IFS-27, IFS-803
    [Tags]  HappyPath
    [Setup]  Log in as a different user   &{ifs_admin_user_credentials}
    Given the user navigates to the page  ${server}/management/admin/users/active
    And the user clicks the button/link   link=Invite a new internal user
    Then the user should see the element  jQuery=h1:contains("Invite a new internal user")
    And the user should see the element   jQuery=option:contains("Innovation Lead")

Server side validation for invite new internal user
    [Documentation]  IFS-27
    [Tags]
    Given the user clicks the button/link               jQuery=button:contains("Send invite")
    Then The user should see a field and summary error  Please enter a first name.
    And The user should see a field and summary error   Please enter a last name.
    And The user should see a field and summary error   Please enter an email address.
    [Teardown]  the user clicks the button/link         link=Cancel

Client side validations for invite new internal user
    [Documentation]  IFS-27
    [Tags]
    Given the user navigates to the page       ${server}/management/admin/invite-user
    When the user enters text to a text field  id=firstName  A
    Then the user should not see the element   jQuery=.error-message:contains("Please enter a first name.")
    And the user should see the element        jQuery=.error-message:contains("Your first name should have at least 2 characters.")
    When the user enters text to a text field  id=lastName  D
    Then the user should not see the element   jQuery=.error-message:contains("Please enter a last name.")
    And the user should see the element        jQuery=.error-message:contains("Your last name should have at least 2 characters.")
    When the user enters text to a text field  id=emailAddress  astle
    Then the user should not see the element   jQuery=.error-message:contains("Please enter an email address.")
    And the user should see the element        jQuery=.error-message:contains("Please enter a valid email address.")

Administrator can successfully invite a new user
    [Documentation]  IFS-27 IFS-983
    [Tags]  HappyPath
    Given the user navigates to the page                     ${server}/management/admin/invite-user
    When the user enters text to a text field                id=firstName  Support
    And the user enters text to a text field                 id=lastName  User
    And the user fills in the email address for the invitee
    And the user selects the option from the drop-down menu  IFS Administrator  id=role
    And the user clicks the button/link                      jQuery=.button:contains("Send invite")
    Then the user cannot see a validation error in the page
    Then the user should see the element                     jQuery=h1:contains("Manage users")
    #The Admin is redirected to the Manage Users page on Success
    And the user should see the element                      jQuery=.selected:contains("Pending")
    Then the user verifies pending tab content
    When the user clicks the button/link                     jQuery=a:contains("Active")
    Then the user should not see the element                 jQuery=td:contains("Support User") ~ td:contains("IFS Administrator")
    When the user clicks the button/link                     jQuery=a:contains("Inactive")
    Then the user should not see the element                 jQuery=td:contains("Support User") ~ td:contains("IFS Administrator")
    [Teardown]  close any open browsers

Invited user can receive the invitation
    [Documentation]  IFS-642
    [Tags]  Email  HappyPath
    [Setup]  the guest user opens the browser
    The invitee reads his email and clicks the link  Invitation to Innovation Funding Service  Your Innovation Funding Service account has been created.

Account creation validation checks
    [Documentation]  IFS-643
    [Tags]
    Given the user clicks the button/link   jQuery=.button:contains("Create account")
    Then the user should see a field error  Please enter a first name.
    And the user should see a field error   Your first name should have at least 2 characters.
    When the user should see a field error  Please enter a last name.
    Then the user should see a field error  Your last name should have at least 2 characters.
    And the user should see the element     jQuery=li[data-valid="false"]:contains("be at least 8 characters long")

New user account is created and verified
    [Documentation]  IFS-643 IFS-983
    [Tags]   HappyPath
    When the user enters text to a text field  css=#firstName  New
    And the user enters text to a text field   css=#lastName  Administrator
    And the user should see the element        jQuery=h3:contains("Email") + p:contains("ifs.administrator@innovateuk")
    And the user enters text to a text field   css=#password  ${correct_password}
    And the user clicks the button/link        jQuery=.button:contains("Create account")
    Then the user should see the element       jQuery=h1:contains("Your account has been created")
    When the user clicks the button/link       jQuery=.button:contains("Sign into your account")
    Then the invited user logs in
    And the user clicks the button/link        jQuery=a:contains("Manage users")
    And the user clicks the button/link        jQuery=a:contains("New Administrator")
    Then the user should see the element       jQuery=dt:contains("Full name") + dd:contains("New Administrator")
    And the user should see the element        jQuery=dt:contains("Email") + dd:contains("ifs.administrator@innovateuk")
    And the user should see the element        jQuery=dt:contains("Role") + dd:contains("IFS Administrator")
    When the user clicks the button/link       jQuery=a:contains("Manage users")
    And the user clicks the button/link        jQuery=a:contains("Pending")
    Then the user should see the element       jQuery=span:contains("0") + span:contains("pending internal users")
    And the user should not see the element    css=.table-overflow ~ td
    And the user clicks the button/link        jQuery=a:contains("Active")

Inviting the same user for the same role again should give an error
    [Documentation]  IFS-27
    [Tags]
    [Setup]  log in as a different user            &{ifs_admin_user_credentials}
    Given the user navigates to the page           ${server}/management/admin/invite-user
    When the user enters text to a text field      id=firstName  New
    And the user enters text to a text field       id=lastName  Administrator
    And the user fills in the email address for the invitee
    And the user selects the option from the drop-down menu  IFS Administrator  id=role
    And the user clicks the button/link            jQuery=.button:contains("Send invite")
    Then the user should see the element           jQuery=.error-summary:contains("This email address is already in use.")

Inviting the same user for the different role again should also give an error
    [Documentation]  IFS-27
    [Tags]
    Given the user navigates to the page       ${server}/management/admin/invite-user
    When the user enters text to a text field  id=firstName  Project
    And the user enters text to a text field   id=lastName  Finance
    And the user fills in the email address for the invitee
    And the user selects the option from the drop-down menu  Project Finance  id=role
    And the user clicks the button/link        jQuery=.button:contains("Send invite")
    Then The user should see a summary error   This email address is already in use.

Administrator can navigate to edit page to edit the internal user's details
    [Documentation]  IFS-18
    [Tags]
    [Setup]  the user navigates to the View internal users details  New Administrator  active
    Given the user clicks the button/link         link=Edit
    And the user should see the text in the page  Edit internal user's details
    And the user should see the element           css=#firstName[value="New"]
    And the user should see the element           css=#lastName[value="Administrator"]
    And the user should see the element           jQuery=dt:contains("Email address") ~ dd:contains("ifs.administrator")
    And the user should see the dropdown option selected  IFS Administrator  id=role

Server side validation for edit internal user's details
    [Documentation]  IFS-18
    [Tags]
    Given the user enters text to a text field  id=firstName  ${empty}
    And the user enters text to a text field    id=lastName  ${empty}
    When the user clicks the button/link        jQuery=button:contains("Save and return")
    Then the user should see a field error      Please enter a first name.
    And the user should see a field error       Your first name should have at least 2 characters.
    And the user should see a field error       Please enter a last name.
    And the user should see a field error       Your last name should have at least 2 characters.

Client side validations for edit internal user's details
    [Documentation]  IFS-18
    [Tags]
    Given the user enters text to a text field  id=firstName  A
    Then the user should not see the element   jQuery=.error-message:contains("Please enter a first name.")
    And the user should see the element        jQuery=.error-message:contains("Your first name should have at least 2 characters.")
    When the user enters text to a text field  id=lastName  D
    Then the user should not see the element   jQuery=.error-message:contains("Please enter a last name.")
    And the user should see the element        jQuery=.error-message:contains("Your last name should have at least 2 characters.")

Administrator can successfully edit internal user's details
    [Documentation]  IFS-18
    [Tags]  HappyPath
    [Setup]  log in as a different user                      &{ifs_admin_user_credentials}
    Given the user navigates to the View internal users details  New Administrator  active
    And the user clicks the button/link                      link=Edit
    When the user enters text to a text field                id=firstName  Edited
    Then the user enters text to a text field                id=lastName  Admin
    And the user selects the option from the drop-down menu  IFS Support User  id=role
    And the user clicks the button/link                      jQuery=.button:contains("Save and return")
    Then the user cannot see a validation error in the page
    When the user should see the element                     jQuery=h1:contains("Manage users")
    #The Admin is redirected to the Manage Users page on Success
    And the user should see the element                      jQuery=.selected:contains("Active")
    And the user should see the element                      jQuery=td:contains("Edited Admin") + td:contains("IFS Support User")

Administrator is able to disable internal users
    [Documentation]  IFS-644
    [Tags]  HappyPath
    Given the user navigates to the View internal users details  Edited Admin  active
    And the user clicks the button/link   link=Edit
    Then the user should see the element  css=.form-group input
    When the user clicks the button/link  jQuery=button:contains("Deactivate user")
    Then the user clicks the button/link  jQuery=button:contains("Cancel")
    When the user clicks the button/link  jQuery=button:contains("Deactivate user")
    And the user clicks the button/link   jQuery=button:contains("Yes, deactivate")
    Then the user should see the element  jQuery=.form-footer *:contains("Reactivate user") + *:contains("Deactivated by Arden Pimenta on")
    #TODO Pending due to IFS-1191 add ${today}
    When the user navigates to the page   ${server}/management/admin/users/inactive
    Then the user should see the element  jQuery=tr:contains("Edited Admin")  #Checking the user swapped tab

Deactivated user cannot login until he is activated
    [Documentation]  IFS-644
    [Tags]
    [Setup]  the user logs out if they are logged in
    Given the deactivated user is not able to login
    When Logging in and Error Checking                  &{ifs_admin_user_credentials}
    Then the user navigates to the View internal users details  Edited Admin  inactive
    When the user clicks the button/link                jQuery=button:contains("Reactivate user")
    Then the user clicks the button/link                jQuery=button:contains("Yes, reactivate")
    When the user navigates to the page                 ${server}/management/admin/users/active
    Then the user should see the element                jQuery=tr:contains("Edited Admin")  #Checking the user swapped tab
    When the re-activated user tries to login
    Then the user should not see an error in the page

# TODO: Add ATs for IFS-605 with pagination when IFS-637 is implemented

*** Keywords ***
User cannot see manage users page
    [Arguments]  ${email}  ${password}
    Log in as a different user  ${email}  ${password}
    the user should not see the element   link=Manage users
    the user navigates to the page and gets a custom error message  ${USER_MGMT_URL}  ${403_error_message}

the user fills in the email address for the invitee
    # Locally the accepted domain is innovateuk.test
    run keyword if  ${docker}==1  the user enters text to a text field  id=emailAddress  ${localEmailInvtedUser}
    # On production the accepted domain is innovateuk.gov.uk
    run keyword if  ${docker}!=1  the user enters text to a text field  id=emailAddress  ${remoteEmailInvtedUser}

The invitee reads his email and clicks the link
    [Arguments]  ${title}  ${pattern}
    # Locally the accepted domain is innovateuk.test
    run keyword if  ${docker}==1  The user reads his email and clicks the link  ${localEmailInvtedUser}  ${title}  ${pattern}
    # On production the accepted domain is innovateuk.gov.uk
    run keyword if  ${docker}!=1  The user reads his email and clicks the link  ${remoteEmailInvtedUser}  ${title}  ${pattern}

the invited user logs in
    # Locally the accepted domain is innovateuk.test
    run keyword if  ${docker}==1  Logging in and Error Checking  ${localEmailInvtedUser}  ${correct_password}
    # On production the accepted domain is innovateuk.gov.uk
    run keyword if  ${docker}!=1  Logging in and Error Checking  ${remoteEmailInvtedUser}  ${correct_password}

the user verifies pending tab content
    # Locally the accepted domain is innovateuk.test
    run keyword if  ${docker}==1  the user should see the element  jQuery=td:contains("Support User") ~ td:contains("IFS Administrator") ~ td:contains("${localEmailInvtedUser}")
    # On production the accepted domain is innovateuk.gov.uk
    run keyword if  ${docker}!=1  the user should see the element  jQuery=td:contains("Support User") ~ td:contains("IFS Administrator") ~ td:contains("${remoteEmailInvtedUser}")

the user navigates to the View internal users details
    [Arguments]  ${user}  ${status}
    the user navigates to the page   ${server}/management/admin/users/${status}
    the user clicks the button/link  link=${user}

the deactivated user is not able to login
    # Locally the accepted domain is innovateuk.test
    run keyword if  ${docker}==1  the user cannot login with their new details  ${localEmailInvtedUser}  ${correct_password}
    # On production the accepted domain is innovateuk.gov.uk
    run keyword if  ${docker}!=1  the user cannot login with their new details  ${remoteEmailInvtedUser}  ${correct_password}

the re-activated user tries to login
    # Locally the accepted domain is innovateuk.test
    run keyword if  ${docker}==1  log in as a different user  ${localEmailInvtedUser}  ${correct_password}
    # On production the accepted domain is innovateuk.gov.uk
    run keyword if  ${docker}!=1  log in as a different user  ${remoteEmailInvtedUser}  ${correct_password}

