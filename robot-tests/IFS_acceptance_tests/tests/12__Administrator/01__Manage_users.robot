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

Administrator can see the 'Add a new internal user' link
    [Documentation]  IFS-27
    [Tags]
    When the user clicks the button/link  link=Manage users
    Then the user should see the element   link=Add a new internal user

Administrator can navigate to 'Add a new internal user' link
    [Documentation]  IFS-27
    [Tags]
    When the user clicks the button/link  link=Add a new internal user
    Then the user should see the text in the page    Add a new internal user
    and the user should see the text in the page    Enter the new internal user's details below to add them to your invite list.

Validations for invite new user
    [Documentation]  IFS-27
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Send invite")
    Then the user should see an error    Please enter a first name.
    And the user should see an error    Your first name should have at least 2 characters.
    And the user should see an error    Please enter a last name.
    And the user should see an error    Your last name should have at least 2 characters.
    And the user should see an error    Please enter an email address.

Client side validations for invite new user
    [Documentation]  IFS-27
    [Tags]
    When the user enters text to a text field    id=firstName    A
    Then the user should not see the validation error    Please enter a first name.
    And the user should see an error    Your first name should have at least 2 characters.
    When the user enters text to a text field    id=lastName    D
    Then the user should not see the validation error    Please enter a last name.
    And the user should see an error    Your last name should have at least 2 characters.
    When the user enters text to a text field    id=emailAddress    astle
    Then the user should not see the validation error    Please enter an email address.
    And the user should see an error    Please enter a valid email address.
    When the user enters text to a text field    id=firstName    Astle
    Then the user should not see the validation error    Please enter a first name.
    And the user should not see the validation error    Your first name should have at least 2 characters.
    When the user enters text to a text field    id=lastName    Pimenta
    Then the user should not see the validation error    Please enter a last name.
    And the user should not see the validation error    Your last name should have at least 2 characters.
    When the user enters text to a text field    id=emailAddress    astle.pimenta@innovateuk.test
    Then the user should not see the validation error    Please enter an email address.
    And the user should not see the validation error    Please enter a valid email address.

Clicking the 'Manage users' link take the Administration back to the 'Manage Users' page
    [Documentation]  IFS-27
    [Tags]
    When the user clicks the button/link  link=Manage users
    Then the user should see the element  jQuery=h1:contains("Manage users")
    And the user should see the element   jQuery=.selected:contains("Active")

Administrator can successfully invite a new user
    [Documentation]  IFS-27
    [Tags]
    When the user clicks the button/link  link=Add a new internal user
    And the user enters text to a text field    id=firstName    Astle
    And the user enters text to a text field    id=lastName    Pimenta
    And the user enters text to a text field    id=emailAddress    astle.pimenta@innovateuk.gov.uk
    And the user selects the option from the drop-down menu    IFS Support User    id=role
    And the user clicks the button/link    jQuery=.button:contains("Send invite")
    Then the user should see the element  jQuery=h1:contains("Manage users")    #The Admin is redirected to the Manage Users page on Success
    And the user should see the element   jQuery=.selected:contains("Active")

Inviting the same user for the same role again should give an error
    [Documentation]  IFS-27
    [Tags]
    When the user clicks the button/link  link=Add a new internal user
    And the user enters text to a text field    id=firstName    Astle
    And the user enters text to a text field    id=lastName    Pimenta
    And the user enters text to a text field    id=emailAddress    astle.pimenta@innovateuk.gov.uk
    And the user selects the option from the drop-down menu    IFS Support User    id=role
    And the user clicks the button/link    jQuery=.button:contains("Send invite")
    Then the user should see the text in the page    This user has a pending invite. Please check.

Inviting the same user for the different role again should also give an error
    [Documentation]  IFS-27
    [Tags]
    When the user clicks the button/link  link=Manage users
    And the user clicks the button/link  link=Add a new internal user
    And the user enters text to a text field    id=firstName    Astle
    And the user enters text to a text field    id=lastName    Pimenta
    And the user enters text to a text field    id=emailAddress    astle.pimenta@innovateuk.gov.uk
    And the user selects the option from the drop-down menu    Project Finance    id=role
    And the user clicks the button/link    jQuery=.button:contains("Send invite")
    Then the user should see the text in the page    This user has a pending invite. Please check.

Project finance user cannot navigate to manage users page
    [Documentation]  INFUND-604
    [Tags]
    User cannot see manage users page   &{Comp_admin1_credentials}
    User cannot see manage users page   &{internal_finance_credentials}

# TODO: Add ATs for IFS-605 with pagination when IFS-637 is implemented

*** Keywords ***
User cannot see manage users page
    [Arguments]  ${email}  ${password}
    Log in as a different user  ${email}  ${password}
    the user should not see the element   link=Manage users
    the user navigates to the page and gets a custom error message  ${USER_MGMT_URL}  ${403_error_message}

the user should not see the validation error
    [Arguments]    ${ERROR_TEXT}
    Run Keyword And Ignore Error Without Screenshots    mouse out    css=input
    Focus    jQuery=.button:contains("Send invite")
    Wait for autosave
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Wait Until Element Does Not Contain Without Screenshots    css=.error-message    ${ERROR_TEXT}
    Run Keyword If    '${status}' == 'FAIL'    Page Should not Contain    ${ERROR_TEXT}