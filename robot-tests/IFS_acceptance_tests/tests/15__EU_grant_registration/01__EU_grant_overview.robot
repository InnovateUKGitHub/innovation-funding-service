*** Settings ***
Documentation    IFS-4231 EU2020 - Create webservice & Landing page
...
...              IFS-4232 EU2020 - Contact details
...
...              IFS-4072 EU2020 - Organisation selection
Resource         ../../resources/defaultResources.robot
Resource         ../10__Project_setup/PS_Common.robot

*** Variables ***
${EU_grant}    ${server}/eu-grant/overview
*** Test Cases ***
User navigate to EU grant registration page
    [Documentation]  IFS-4231
    Given the guest user opens the browser
    When the user navigates to the page     ${EU_grant}
    Then the user should see the element    jQuery = h1:contains("Horizon 2020: UK government underwrite guarantee")
    And the user should see the element     link = Your organisation
    And the user should see the element     link = Contact details
    And the user should see the element     link = Funding details

User navigates to the Organisation type details page
    [Documentation]  IFS-4072
    [Tags]
    Then The user clicks the button/link    link = Your organisation
    And the user clicks the button/link     jQuery = span:contains("Business")
    And the user clicks the button/link     jQuery = button:contains("Save and continue")

Not in Companies House: Enter details manually link
    [Documentation]
    [Tags]
    When the user clicks the button/link    jQuery = summary:contains("Enter details manually")

Companies House: Valid company name
    [Documentation]
    [Tags]
    When the user enters text to a text field    id = organisationSearchName    Hive IT
    Then the user clicks the button/link         jQuery=button:contains("Search")
    Then the user should see the element         jQuery=button:contains("HIVE IT LIMITED")

Companies House: User can choose the organisation and same operating address
    [Documentation]
    [Tags]
    When the user clicks the button/link            jQuery=button:contains("HIVE IT LIMITED")
    And the user should see the text in the page    Registered name
    And the user should see the text in the page    Registered Address
    And the user should see the text in the page    Registration number

Companies House: Invalid company name
    [Documentation]
    [Tags]
    When the user clicks the button/link           Link = Edit your organisation details
    And the user clicks the button/link            jQuery = span:contains("Business")
    And the user clicks the button/link            jQuery = button:contains("Save and continue")
    When the user enters text to a text field      id = organisationSearchName    innoavte
    And the user clicks the button/link            id = org-search
    Then the user should see the text in the page  No results found.

Companies House: Valid registration number
    [Documentation]
    [Tags]
    When the user enters text to a text field      id = organisationSearchName    05493105
    And the user clicks the button/link            id = org-search
    Then the user should see the element           jQuery=button:contains("HIVE IT LIMITED")

Companies House: Empty company name field
    [Documentation]
    [Tags]
    When the user enters text to a text field         id = organisationSearchName    ${EMPTY}
    And the user clicks the button/link               id = org-search
    Then the user should see an error    Please enter an organisation name to search
