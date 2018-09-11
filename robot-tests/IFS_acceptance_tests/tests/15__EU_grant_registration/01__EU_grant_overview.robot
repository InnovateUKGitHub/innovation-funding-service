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
    And the user should see the element     link = Contact details
    And the user should see the element     link = Funding details

User navigates to and selects business on the Organisation details page
    [Documentation]  IFS-4072
    [Tags]
    When The user clicks the button/link    link = Your organisation
    Then the user clicks the button/link    jQuery = span:contains("Business")
    And the user clicks the button/link     jQuery = button:contains("Save and continue")

Companies House: Enter manually Valid company name
    [Documentation]  IFS-4072
    [Tags]
    Given the user clicks the button/link          jQuery = summary:contains("Enter details manually")
    When the user enters text to a text field      id = organisationSearchName    Hive IT
    Then the user clicks the button/link           jQuery = button:contains("Search")
    And the user clicks the button/link            jQuery = button:contains("HIVE IT LIMITED")
    Then the user should see the element           jQuery = h3:contains("Registered name")
    And the user should see the element            jQuery = h3:contains("Registered Address")
    And the user should see the element            jQuery = h3:contains("Registration number")

Companies House: Invalid company name
    [Documentation]  IFS-4072
    [Tags]
    When the user clicks the button/link           link = Edit your organisation details
    And the user clicks the button/link            jQuery = span:contains("Business")
    And the user clicks the button/link            jQuery = button:contains("Save and continue")
    Then the user enters text to a text field      id = organisationSearchName    innoavte
    And the user clicks the button/link            id = org-search
    Then the user should see the element           jQuery = p:contains("No results found.")

Companies House: Empty company name field
    [Documentation]  IFS-4072
    [Tags]
    When the user enters text to a text field      id = organisationSearchName    ${EMPTY}
    Then the user clicks the button/link           id = org-search
    And the user should see an error               Please enter an organisation name to search

Companies House: Valid registration number
    [Documentation]  IFS-4072
    [Tags]
    When the user enters text to a text field      id = organisationSearchName    05493105
    Then the user clicks the button/link           id = org-search
    And the user clicks the button/link            jQuery = button:contains("HIVE IT LIMITED")
    And the user clicks the button/link            link = Save and return

Contact details validation
    [Documentation]  IFS-4231
    [Tags]
    When the user clicks the button/link           link = Contact details
    Then the user clicks the button/link           jQuery = button:contains("Continue")
    And the user clicks the button/link            jQuery = a:contains("Enter your full name")
    And the user clicks the button/link            jQuery = a:contains("Your full name should have at least 2 characters.")
    And the user clicks the button/link            jQuery = a:contains("Enter your job title.")
    And the user clicks the button/link            jQuery = a:contains("Please enter a valid email.")
    And the user clicks the button/link            jQuery = a:contains("Enter your telephone number.")
    And the user clicks the button/link            jQuery = a:contains("Please enter a valid phone number between 8 and 20 digits.")

Contact details Enter details and save
    [Documentation]  IFS-4231
    [Tags]
    When the user enters text to a text field      id = name        Name
    Then the user enters text to a text field      id = jobTitle    Job title
    And the user enters text to a text field       id = email       test@test.com
    And the user enters text to a text field       id = telephone   012345678901
    Then the user clicks the button/link           jQuery = button:contains("Continue")
    And the user should see the element            jQuery = dl:contains("Name")
    And the user should see the element            jQuery = dl:contains("Job title")
    And the user should see the element            jQuery = dl:contains("test@test.com")
    And the user should see the element            jQuery = dl:contains("012345678901")
    And the user clicks the button/link            link = Save and return

Dashboard should reflect the updates
    [Documentation]  IFS-4231
    Given the user navigates to the page            ${EU_grant}
    When the user should see the element            jQuery = li:contains("Your organisation") .task-status-complete
    When the user should see the element            jQuery = li:contains("Contact details") .task-status-complete