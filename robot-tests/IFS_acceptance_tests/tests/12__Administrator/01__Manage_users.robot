*** Settings ***
Documentation     IFS-604: IFS Admin user navigation to Manage users section
...               IFS-605: Manage internal users: List of all internal users and roles
...               IFS-606: Manage internal users: Read only view of internal user profile
Suite Setup       The user logs-in in new browser  &{ifs_admin_user_credentials}
Suite Teardown    the user closes the browser
Force Tags        Administrator  CompAdmin
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Administrator can navigate to manage users page
    [Documentation]    INFUND-604
    [Tags]  HappyPath
    When the user clicks the button/link  link=Manage users
    Then the user should see the element  jQuery=h1:contains("Manage users")
    And the user should see the element   jQuery=.selected:contains("Active")

Administrator can see the read only view of internal user profile
    [Documentation]  INFUND-606
    [Tags]
    When the user clicks the button/link  link=John Doe
    Then the user should see the element  jQuery=h1:contains("View internal user's details")
    And the user should see the element   jQuery=dt:contains("Email") + dd:contains("${Comp_admin1_credentials["email"]}")
    And the user should see the element   jQuery=dt:contains("Job role") + dd:contains("Competition Administrator")
    And the user should see the element   jQuery=.form-footer__info:contains("Created by IFS System Maintenance User")

Project finance user cannot navigate to manage users page
    [Documentation]  INFUND-604
    [Tags]
    User cannot see manage users page   &{Comp_admin1_credentials}
    User cannot see manage users page   &{internal_finance_credentials}

Administrator can invite a new Internal User
    [Documentation]  IFS-27
    [Tags]  HappyPath
    [Setup]  Log in as a different user   &{ifs_admin_user_credentials}
    Given the user navigates to the page  ${server}/management/admin/users/active
    And the user clicks the button/link   link=Add a new internal user
    Then the user should see the element  jQuery=h1:contains("Add a new internal user")

Server side validation for invite new internal user
    [Documentation]  IFS-27
    [Tags]
    Given the user clicks the button/link               jQuery=button:contains("Send invite")
    Then The user should see a field and summary error  Please enter a first name.
    And The user should see a field and summary error   Your first name should have at least 2 characters.
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
    [Documentation]  IFS-27
    [Tags]  Happypath
    Given the user navigates to the page       ${server}/management/admin/invite-user
    When the user enters text to a text field  id=firstName  Astle
    And the user enters text to a text field   id=lastName  Pimenta
    And the user enters text to a text field   id=emailAddress  astle.pimenta@innovateuk.test
    And the user selects the option from the drop-down menu  IFS Support User  id=role
    And the user clicks the button/link        jQuery=.button:contains("Send invite")
    Then the user cannot see a validation error in the page
    Then the user should see the element       jQuery=h1:contains("Manage users")
    #The Admin is redirected to the Manage Users page on Success
    And the user should see the element        jQuery=.selected:contains("Active")

Inviting the same user for the same role again should give an error
    [Documentation]  IFS-27
    [Tags]  
    When the user clicks the button/link           link=Add a new internal user
    And the user enters text to a text field       id=firstName  Astle
    And the user enters text to a text field       id=lastName  Pimenta
    And the user enters text to a text field       id=emailAddress  astle.pimenta@innovateuk.test
    And the user selects the option from the drop-down menu  IFS Support User  id=role
    And the user clicks the button/link            jQuery=.button:contains("Send invite")
    Then the user should see the text in the page  This user has a pending invite. Please check.

# TODO: Add ATs for IFS-605 with pagination when IFS-637 is implemented

Admin user invites internal user
    [Documentation]  IFS-27, IFS-642
    [Tags]  Email  HappyPath
    [Setup]  The user logs-in in new browser  &{ifs_admin_user_credentials}
    When the user clicks the button/link  link=Manage users
    And the user clicks the button/link  link=Add a new internal user
    Then the user should see the element  jQuery=h1:contains("Add a new internal user")
    And the user should see the element  jQuery=p:contains("Enter the new internal user's details below to add them to your invite list.")
    And the user should see the element  jQuery=form input#firstName
    And the user should see the element  jQuery=form input#lastName
    And the user should see the element  jQuery=form input#emailAddress
    And the user should see the element  jQuery=form select#role
    Then the user enters text to a text field  id=firstName  Aaron
    And the user enters text to a text field  id=lastName  Aaronson
    And the user enters text to a text field  id=emailAddress  test@innovateuk.gov.uk
    And the user selects the option from the drop-down menu  IFS_ADMINISTRATOR  id=role
    And the user clicks the button/link  jQuery=button:contains("Send invite")
    And Logout as user
    Then the user reads his email and clicks the link   test@innovateuk.gov.uk   Invitation to Innovation Funding Service    An Innovation Funding Service account was created for you

Account creation validation checks
    [Documentation]  IFS-643
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Create account")
    When The user should see a field error  Please enter a first name.
    When the user should see a field error  Your first name should have at least 2 characters.
    When the user should see a field error  Please enter a last name.
    When the user should see a field error  Your last name should have at least 2 characters.

New user account is created and verified
    [Documentation]  IFS-643
    [Tags]   HappyPath
    When the user enters text to a text field        css=#firstName  Aaron
    And the user enters text to a text field         css=#lastName  Aaronson
    And the user should see the element              jQuery=h3:contains("Email") + p:contains("test@innovateuk.gov.uk")
    And the user enters text to a text field         css=#password  ${short_password}
    And the user clicks the button/link              jQuery=.button:contains("Create account")
    Then the user should see the text in the page    Your account has been created
    When the user clicks the button/link             jQuery=.button:contains("Sign into your account")
    And Logging in and Error Checking                test@innovateuk.gov.uk  ${short_password}
    And the user clicks the button/link              jQuery=a:contains("Manage users")
    And the user clicks the button/link              jQuery=a:contains("Aaron Aaronson")
    Then the user should see the element             jQuery=#content dl dd:nth-of-type(1):contains("Aaron Aaronson")
    And the user should see the element              jQuery=#content dl dd:nth-of-type(2):contains("test@innovateuk.gov.uk")
    And the user should see the element              jQuery=#content dl dd:nth-of-type(3):contains("IFS Administrator")


*** Keywords ***
User cannot see manage users page
    [Arguments]  ${email}  ${password}
    Log in as a different user  ${email}  ${password}
    the user should not see the element   link=Manage users
    the user navigates to the page and gets a custom error message  ${USER_MGMT_URL}  ${403_error_message}