*** Settings ***
Documentation     INFUND-887 : As an applicant I want the option to look up my business organisation's details using Companies House lookup so...
...
...               INFUND-890 : As an applicant I want to use UK postcode lookup function to look up and enter my business address details as they won't necessarily be the same as the address held by Companies House, so ...
Suite Setup       Applicant goes to the organisation search page
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../10__Project_setup/PS_Common.robot

*** Test Cases ***
Not in Companies House: Enter details manually link
    [Documentation]    INFUND-888
    [Tags]
    When the user clicks the button/link    jQuery = summary:contains("Enter details manually")
    Then the user should see the element    jQuery = .govuk-label:contains("Organisation name")

Companies House: Valid company name
    [Documentation]    INFUND-887
    [Tags]  HappyPath
    When the user enters text to a text field    id = organisationSearchName    Hive IT
    And the user clicks the button/link          id = org-search
    Then the user should see the element         Link = ${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}
    [Teardown]    The user goes back to the previous page

Companies House: User can choose the organisation address
    [Documentation]    INFUND-887
    [Tags]  HappyPath
    When the user clicks the button/link    Link = ${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}
    And the user should see the element     jQuery = h2:contains("Registered name")
    And the user should see the element     jQuery = h2:contains("Registered address")
    And the user should see the element     jQuery = h2:contains("Registration number")
    [Teardown]    the user goes back to the previous page

Companies House: Invalid company name
    [Documentation]    INFUND-887
    [Tags]
    When the user enters text to a text field        id = organisationSearchName    innoavte
    And the user clicks the button/link              id = org-search
    Then the user should see the element             jQuery = li p:contains("No results found.")

Companies House: Valid registration number
    [Documentation]    INFUND-8870
    [Tags]  HappyPath
    When the user enters text to a text field    id = organisationSearchName    05493105
    And the user clicks the button/link          id = org-search
    Then the user should see the element         Link = INNOVATE LTD
    [Teardown]    The user goes back to the previous page

Companies House: Empty company name field
    [Documentation]    INFUND-887
    [Tags]
    Given the user enters text to a text field     id = organisationSearchName    ${EMPTY}
    When the user clicks the button/link           id = org-search
    Then the user should see a field error         Please enter an organisation name to search

Manually add the details and pass to the confirmation page
    [Documentation]    INFUND-888
    [Tags]  HappyPath
    [Setup]  the user expands enter details manually
    Given the user enters text to a text field    name = organisationName    Top of the Popps
    When the user clicks the button/link          jQuery = button:contains("Continue")
    Then the user should see the element          jQuery = h2:contains("Organisation type")~ p:contains("Business")
    And the user should see the element           jQuery = h2:contains("Registered name")~ p:contains("Top of the Popps")

*** Keywords ***
Applicant goes to the organisation search page
    Given the guest user opens the browser
    the user navigates to the page    ${frontDoor}
    Given the user clicks the button/link in the paginated list     link = ${createApplicationOpenCompetition}
    When the user clicks the button/link    link = Start new application
    And the user clicks the button/link     link = Continue and create an account
    And the user clicks the button/link     jQuery = span:contains("Business")
    And the user clicks the button/link     jQuery = button:contains("Save and continue")

the backslash doesnt give errors
    ${STATUS}    ${VALUE} =     Run Keyword And Ignore Error Without Screenshots    the user should see the element    id = addressForm.selectedPostcodeIndex
    Run Keyword If    '${status}' == 'FAIL'    Wait Until Page Contains Without Screenshots    No results were found

the user expands enter details manually
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element   css = .govuk-details__summary[aria-expanded="false"]
    run keyword if  '${status}'=='PASS'  the user clicks the button/link                                       css = .govuk-details__summary[aria-expanded="false"]
