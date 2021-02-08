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

*** Variables ***
${business_type}           Partnership
${organisation_name}       Best Test Company
${organisation_number}     1234567890
${sic_code}                54321
${executive_officer}       Phil Mitchell
${address_line_1}          123
${address_line_2}          Test Street
${address_line_3}          Paradise
${address_town}            London
${address_county}          Middlesex
${address_postcode}        NW11 8AJ

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
    Given the applicant searches for organisation                    Not exist
    When the applicant clicks link to find out what to do
    Then the applicant clicks link to enter its details manually

Not in Companies House: Return to the original search page
     [Documentation]    INFUND-888  IFS-7724
     [Tags]
     Given the user clicks the button/link     link = Back to organisations not registered on Companies House
     When the user clicks the button/link      link = Back to Companies House search results
     Then the user clicks the button/link      link = Back to enter your organisation's details
     And the user should see the element       jQuery = h1:contains("Enter your organisation's details")

Not in Companies House: Manually add the details validation message
    [Documentation]    INFUND-888  IFS-7724
    [Tags]
    Given The user navigates to the page                   ${server}/organisation/create/organisation-type/manually-enter-organisation-details
    When the user clicks the button/link                   jQuery = button:contains("Save and continue")
    And the user should see a field and summary error      You must enter your organisation's name.
    Then the user should see a field and summary error     Search using a valid postcode or enter the address manually.

Not in Companies House: Manually add the details and pass to the confirmation page
    [Documentation]    INFUND-888  IFS-7724
    [Tags]
    Given the applicant manually adds company details
    When the user clicks the button/link                      jQuery = button:contains("Save and continue")
    Then the applicant confirms and saves company details

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

the applicant clicks link to find out what to do
    the user clicks the button/link     link = Find out what to do
    the user should see the element     jQuery = h1:contains("Organisations not registered on Companies House")

the applicant clicks link to enter its details manually
    the user clicks the button/link     link = enter its details manually
    the user should see the element     jQuery = h1:contains("Manually enter your organisation's details")

the applicant manually adds company details
    the user enters text to a text field       name = organisationName              ${organisation_name}
    the user enters text to a text field       name = organisationNumber            ${organisation_number}
    the user enters text to a text field       name = businessType                  ${business_type}
    the user enters text to a text field       name = sicCodes[0].sicCode           ${sic_code}
    the applicant adds SIC code
    the applicant removes SIC code
    the user enters text to a text field       name = executiveOfficers[0].name     ${executive_officer}
    the applicant adds Executive officer
    the applicnt removes Executive officer
    the applicant enters address manually

the applicant adds SIC code
    the user clicks the button/link          jQuery = button:contains("+ Add SIC code")
    the user enters text to a text field     name = sicCodes[1].sicCode                     44444

the applicant removes SIC code
    the user clicks the button/link          jQuery = #sic-code-row-1 button:contains("Remove")
    the user should not see the element      id = sic-code-row-1

the applicant adds Executive officer
    the user clicks the button/link          jQuery = button:contains("+ Add executive officer")
    the user enters text to a text field     name = executiveOfficers[1].name                        Barrington Levy

the applicnt removes Executive officer
    the user clicks the button/link          jQuery = #exec-officer-row-1 button:contains("Remove")
    the user should not see the element      id = exec-officer-row-1

the applicant enters address manually
    the user clicks the button/link          jQuery = button:contains("Enter address manually")
    the user enters text to a text field     id = addressForm.manualAddress.addressLine1            ${address_line_1}
    the user enters text to a text field     id = addressForm.manualAddress.addressLine2            ${address_line_2}
    the user enters text to a text field     id = addressForm.manualAddress.addressLine3            ${address_line_3}
    the user enters text to a text field     id = addressForm.manualAddress.town                    ${address_town}
    the user enters text to a text field     id = addressForm.manualAddress.county                  ${address_county}
    the user enters text to a text field     id = addressForm.manualAddress.postcode                ${address_postcode}

the applicant confirms and saves company details
    the user should see the element     jQuery = h1:contains("Confirm your organisation")
    the user should see the element     jQuery = dt:contains("Organisation type")
    the user should see the element     jQuery = dd:contains("Business")
    the user should see the element     jQuery = dt:contains("Business type")
    the user should see the element     jQuery = dd:contains("${business_type}")
    the user should see the element     jQuery = dt:contains("Organisation name")
    the user should see the element     jQuery = dd:contains("${organisation_name}")
    the user should see the element     jQuery = dt:contains("Organisation number")
    the user should see the element     jQuery = dd:contains("${organisation_number}")
    the user should see the element     jQuery = dt:contains("SIC code")
    the user should see the element     jQuery = dd div:contains("${sic_code}")
    the user should see the element     jQuery = dt:contains("Executive officers")
    the user should see the element     jQuery = dd div:contains("${executive_officer}")
    the user should see the element     jQuery = dt:contains("Registered address")
    the user should see the element     jQuery = dd div:contains("${address_line_1}")
    the user should see the element     jQuery = dd div:contains("${address_line_2}")
    the user should see the element     jQuery = dd div:contains("${address_line_3}")
    the user should see the element     jQuery = dd div:contains("${address_town}")
    the user should see the element     jQuery = dd div:contains("${address_county}")
    the user should see the element     jQuery = dd div:contains("${address_postcode}")
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user should see the element     jQuery = h1:contains("Application overview")
