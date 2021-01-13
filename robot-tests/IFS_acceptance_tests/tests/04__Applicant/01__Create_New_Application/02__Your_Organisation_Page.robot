*** Settings ***
Documentation     INFUND-887 : As an applicant I want the option to look up my business organisation's details using Companies House lookup so...
...
...               INFUND-890 : As an applicant I want to use UK postcode lookup function to look up and enter my business address details as they won't necessarily be the same as the address held by Companies House, so ...
...
...               IFS-7723 Improvement to company search results
...
Suite Setup       Applicant goes to the organisation search page
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot

*** Test Cases ***
# TODO  should be implemented with IFS-7724
#Not in Companies House: Enter details manually link
#    [Documentation]    INFUND-888
#    [Tags]
#    When the user clicks the button/link    jQuery = summary:contains("Enter details manually")
#    Then the user should see the element    jQuery = .govuk-label:contains("Organisation name")

Companies House: Valid company name
    [Documentation]    INFUND-887  IFS-7723
    [Tags]  HappyPath
    When the user enters text to a text field    id = organisationSearchName    ROYAL
    And the user clicks the button/link          id = org-search
    Then the user should see the element         Link = ${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}

Companies House: User can choose the organisation address
    [Documentation]    INFUND-887  IFS-7723
    [Tags]  HappyPath
    When the user clicks the button/link     Link = ${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}
    Then the user should see the element     jQuery = dt:contains("Organisation type")
    And the user should see the element      jQuery = dt:contains("Organisation name")
    And the user should see the element      jQuery = dt:contains("Address")
    And the user should see the element      jQuery = dt:contains("Registration number")
    And the user should see the element      jQuery = dt:contains("Registered Address")

Companies House: Invalid company name
    [Documentation]    INFUND-887  IFS-7723
    [Tags]
    Given the user clicks the button/link         link = Back to companies house search results
    When the user enters text to a text field     id = organisationSearchName    innoavte
    And the user clicks the button/link           id = org-search
    Then the user should see the element          jQuery = p:contains("matching the search") span:contains("0") + span:contains("Companies") + span:contains("innoavte")

Companies House: Valid registration number
    [Documentation]    INFUND-8870  IFS-7723
    [Tags]  HappyPath
    When the user enters text to a text field    id = organisationSearchName    00445790
    And the user clicks the button/link          id = org-search
    Then the user should see the element         Link = TESCO PLC

Companies House: Empty company name field
    [Documentation]    INFUND-887  IFS-7723
    [Tags]
    When the user enters text to a text field     id = organisationSearchName    ${EMPTY}
    And the user clicks the button/link           id = org-search
    Then the user should see the element          jQuery = p:contains("matching the search") span:contains("0") + span:contains("Companies")

# TODO uncomment on ifs-7722
#Companies House: Empty company name field validation message
#    [Documentation]    IFS-7723
#    [Tags]
#    Given the user clicks the button/link                  link = Back to enter your organisation's details
#    When the user enters text to a text field              id = organisationSearchName    ${EMPTY}
#    And the user clicks the button/link                    id = org-search
#    Then the user should see a field and summary error     You must enter an organisation name or company registration number.

# TODO should be implemented on ifs-7724
#Manually add the details and pass to the confirmation page
#    [Documentation]    INFUND-888
#    [Tags]  HappyPath
#    [Setup]  the user expands enter details manually
#    Given the user enters text to a text field    name = organisationName    Top of the Popps
#    When the user clicks the button/link          jQuery = button:contains("Continue")
#    Then the user should see the element          jQuery = dt:contains("Organisation type")~ dd:contains("Business")
#    And the user should see the element           jQuery = dt:contains("Organisation name")~ dd:contains("Top of the Popps")

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
