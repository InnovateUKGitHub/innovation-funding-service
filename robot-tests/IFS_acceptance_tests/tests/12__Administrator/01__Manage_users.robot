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
    Then the user reads his email and clicks the link   test@innovateuk.gov.uk   Invitation to Innovation Funding Service    An Innovation Funding Service account was created for you





*** Keywords ***
User cannot see manage users page
    [Arguments]  ${email}  ${password}
    Log in as a different user  ${email}  ${password}
    the user should not see the element   link=Manage users
    the user navigates to the page and gets a custom error message  ${USER_MGMT_URL}  ${403_error_message}