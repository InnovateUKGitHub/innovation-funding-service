*** Settings ***
Documentation     IFS-604: IFS Admin user navigation to Manage users section
...               IFS-605: Manage internal users: List of all internal users and roles
...               IFS-606: Manage internal users: Read only view of internal user profile
Suite Setup       Guest user log-in    &{ifs_admin_user_credentials}
Suite Teardown    the user closes the browser
Force Tags        administrator
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Administrator can navigate to manage users page
    [Documentation]    INFUND-604
    When the user clicks the button/link    link=Manage users
    Then the user should see the element    jQuery=h1:contains("Find internal users")
    And the user should see the text in the page    Active

Administrator can see read only view of internal user profile
    [Documentation]    INFUND-606
    When the user clicks the button/link    link=John Doe
    Then the user should see the element    jQuery=h1:contains("View internal users details")
    And the user should see the text in the page    john.doe@innovateuk.test
    And the user should see the text in the page    Competition Administrator
    And the user should see the text in the page    Created by IFS System Maintenance User on 12 June 2017

Comp admin cannot navigate to manage users page
    [Documentation]    INFUND-604
    Given log in as a different user      &{Comp_admin1_credentials}
    Then the user should not see the element    link=Manage users
    And the user navigates to the page and gets a custom error message  ${USER_MGMT_URL}  ${403_error_message}

Project finance user cannot navigate to manage users page
    [Documentation]    INFUND-604
    Given log in as a different user      &{internal_finance_credentials}
    Then the user should not see the element    link=Manage users
    And the user navigates to the page and gets a custom error message  ${USER_MGMT_URL}  ${403_error_message}

# TODO: Add ATs for IFS-605 with pagination when IFS-637 is implemented

*** Keywords ***
