*** Settings ***
Documentation     INFUND-887 : As an applicant I want the option to look up my business organisation's details using Companies House lookup so I don’t have to type it in manually as part of the registration process
Suite Setup       Applicant goes to the create organisation page
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Valid company name
    [Documentation]    INFUND-887
    [Tags]    HappyPath
    When the user enters text to a text field    id=organisationSearchName    innovate
    And the user clicks the button/link    id=org-search
    Then the search criteria should be displayed
    [Teardown]    The user goes back to the previous page

Second company house search
    [Documentation]    INFUND-2691
    When the user enters text to a text field    id=organisationSearchName    Test Test
    And the user clicks the button/link    id=org-search
    Then the user should see the text in the page    09382669

Invalid company name
    [Documentation]    INFUND-887
    [Tags]
    When the user enters text to a text field    id=organisationSearchName    innoavte
    And the user clicks the button/link    id=org-search
    Then the user should see the text in the page    Sorry we couldn't find any results

Valid registration number
    [Documentation]    INFUND-887
    [Tags]    HappyPath
    When the user enters text to a text field    id=organisationSearchName    05493105
    And the user clicks the button/link    id=org-search
    Then the search criteria should be displayed
    [Teardown]    The user goes back to the previous page

Invalid registration number
    [Documentation]    INFUND-887
    [Tags]
    When the user enters text to a text field    id=organisationSearchName    64536
    And the user clicks the button/link    id=org-search
    Then the user should see the text in the page    Sorry we couldn't find any results

Company with spaces in the name
    [Documentation]    INFUND-1757
    [Tags]
    When the user enters text to a text field    id=organisationSearchName    Hive IT
    And the user clicks the button/link    id=org-search
    Then the user should see the element    Link=HIVE IT LIMITED

Invalid characters
    [Documentation]    INFUND-887
    [Tags]
    When the user enters text to a text field    id=organisationSearchName    {}
    And the user clicks the button/link    id=org-search
    Then the user should see an error    Please enter valid characters

Empty company name field
    When the user enters text to a text field    id=organisationSearchName    ${EMPTY}
    And the user clicks the button/link    id=org-search
    Then the user should see an error    ${empty_field_warning_message}

*** Keywords ***
the search criteria should be displayed
    the user should see the text in the page    05493105 - Incorporated on 28 June 2005
    the user clicks the button/link    link=INNOVATE LTD
    the user should see the text in the page    Business
    the user should see the text in the page    Business name
    Element Should Contain    css=.form-block p:nth-child(2)    INNOVATE LTD
    the user should see the text in the page    Registration number
    the user should see the text in the page    05493105

Applicant goes to the create organisation page
    Given the guest user opens the browser
    And the user navigates to the page    ${COMPETITION_DETAILS_URL}
    and the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Sign in to apply")
    And the user clicks the button/link    jQuery=.button:contains("Create")
