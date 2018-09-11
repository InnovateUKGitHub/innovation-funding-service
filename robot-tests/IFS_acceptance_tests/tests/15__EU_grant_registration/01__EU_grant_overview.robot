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

User navigates to and selects business on the Organisation details page
    [Documentation]  IFS-4072
    [Tags]
    When The user clicks the button/link    link = Your organisation
    Then the user clicks the button/link    jQuery = span:contains("Business")
    And the user clicks the button/link     jQuery = button:contains("Save and continue")

Companies House: Enter manually Valid company name
    [Documentation]
    [Tags]
    When the user clicks the button/link         jQuery = summary:contains("Enter details manually")
    Then the user enters text to a text field    id = organisationSearchName    Hive IT
    And the user clicks the button/link          jQuery = button:contains("Search")
    And the user should see the element          jQuery = button:contains("HIVE IT LIMITED")

Companies House: User can choose the organisation and same operating address
    [Documentation]
    [Tags]
    When the user clicks the button/link            jQuery = button:contains("HIVE IT LIMITED")
    Then the user should see the text in the page   Registered name
    And the user should see the text in the page    Registered Address
    And the user should see the text in the page    Registration number

Companies House: Invalid company name
    [Documentation]
    [Tags]
    When the user clicks the button/link           Link = Edit your organisation details
    And the user clicks the button/link            jQuery = span:contains("Business")
    And the user clicks the button/link            jQuery = button:contains("Save and continue")
    Then the user enters text to a text field      id = organisationSearchName    innoavte
    And the user clicks the button/link            id = org-search
    Then the user should see the text in the page  No results found.

Companies House: Empty company name field
    [Documentation]
    [Tags]
    When the user enters text to a text field      id = organisationSearchName    ${EMPTY}
    Then the user clicks the button/link           id = org-search
    And the user should see an error               Please enter an organisation name to search

Companies House: Valid registration number
    [Documentation]
    [Tags]
    When the user enters text to a text field      id = organisationSearchName    05493105
    Then the user clicks the button/link           id = org-search
    And the user should see the element            jQuery = button:contains("HIVE IT LIMITED")

Dashboard should reflect the updates
    [Documentation]  IFS-4231
    Given the user navigates to the page            ${EU_grant}
    When the user should see the element            jQuery = li:contains("Your organisation") + li:contains("Incomplete")
    Then the user should see the element            jQuery = li:contains("Contact details") + li:contains("Incomplete")

