*** Settings ***
Documentation     INFUND-887 : As an applicant I want the option to look up my business organisation's details using Companies House lookup so...
...
...               INFUND-890 : As an applicant I want to use UK postcode lookup function to look up and enter my business address details as they won't necessarily be the same as the address held by Companies House, so ...
...
...               IFS-7723 Improvement to company search results
...
...               IFS-7722 Improvement to company search journey
...
...               IFS-7724 Input organisation details manually
...
Suite Setup       Applicant goes to the organisation search page
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot

*** Test Cases ***
Companies House: Valid company name
    [Documentation]    INFUND-887  IFS-7723
    [Tags]  HappyPath
    When the applicant searches for organisation     ROYAL
    Then the user should see the element             link = ${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}

Companies House: User can choose the organisation address
    [Documentation]    INFUND-887  IFS-7723
    [Tags]  HappyPath
    When the user clicks the button/link     link = ${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}
    Then the user should see the element     jQuery = dt:contains("Organisation type")
    And the user should see the element      jQuery = dt:contains("Organisation name")
    And the user should see the element      jQuery = dt:contains("Registered address")
    And the user should see the element      jQuery = dt:contains("Registration number")
    And the user should see the element      jQuery = dt:contains("Registered address")

Companies House: Invalid company name
    [Documentation]    INFUND-887  IFS-7723
    [Tags]
    Given the user clicks the button/link            link = Back to companies house search results
    When the applicant searches for organisation     innoavte
    When the user enters text to a text field        id = organisationSearchName    innoavte
    And the user clicks the button/link              id = org-search
    Then the user should see the element             jQuery = p:contains("matching the search") span:contains("0") + span:contains("Companies") + span:contains("innoavte")

Companies House: Valid registration number
    [Documentation]    INFUND-8870  IFS-7723
    [Tags]  HappyPath
    When the applicant searches for organisation     00445790
    Then the user should see the element             link = TESCO PLC

Companies House: Empty company name field
    [Documentation]    INFUND-887  IFS-7723
    [Tags]
    When the applicant searches for organisation     ${EMPTY}
    Then the user should see the element             jQuery = p:contains("matching the search") span:contains("0") + span:contains("Companies")

Companies House: Empty company name field validation message
    [Documentation]    IFS-7723  IFS-7722
    [Tags]
    Given the user clicks the button/link                  link = Back to enter your organisation's details
    When the applicant searches for organisation           ${EMPTY}
    Then the user should see a field and summary error     You must enter an organisation name or company registration number.

Not in Companies House: Enter details manually link
    [Documentation]    INFUND-888  IFS-7724
    [Tags]
    Given the applicant searches for organisation     Not exist
    When the user clicks the button/link              link = Find out what to do
    Then the user clicks the button/link              link = enter its details manually
    And the user should see the element               jQuery = h1:contains("Manually enter your organisation's details")

Manually add the details validation message
    [Documentation]    INFUND-888  IFS-7724
    [Tags]
    When the user clicks the button/link                   jQuery = button:contains("Save and continue")
    And the user should see a field and summary error      You must enter your organisation's name.
    Then the user should see a field and summary error     You must enter your organisation's postcode.

Manually add the details and pass to the confirmation page
    [Documentation]    INFUND-888  IFS-7724
    [Tags]
    Given the user enters text to a text field     name = organisationName       Best Test Company
    When the user enters text to a text field      name = organisationNumber     1234567890
    And the user enters text to a text field       name = businessType           Partnership
    And the user enters text to a text field       id = sicCode                  54321
    And the user enters text to a text field       id = execOfficer              Phil Mitchell

*** Keywords ***
Applicant goes to the organisation search page
    the guest user opens the browser
    the user navigates to the page                            ${frontDoor}
    the user clicks the button/link in the paginated list     link = ${createApplicationOpenCompetition}
    the user clicks the button/link                           link = Start new application
    the user clicks the button/link                           link = Continue and create an account
    the user clicks the button/link                           jQuery = span:contains("Business")
    the user clicks the button/link                           jQuery = button:contains("Save and continue")

the applicant searches for organisation
    [Arguments]  ${searchTerm}
    the user enters text to a text field     id = organisationSearchName     ${searchTerm}
    the user clicks the button/link          id = org-search