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
...               IFS-9103 Companies House API: Return more relevant results
...
...               IFS-9156 update existing orgs at point of application
...
Suite Setup       Applicant goes to the organisation search page
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
&{WebTestUserCredentials}          email=Test@hampshire.co.uk    password=${short_password}
${business_type}            Partnership
${organisation_name}        Best Test Company
${organisation_number}      1234567890
${sic_code}                 54321
${executive_officer}        Phil Mitchell
${address_line_1}           123
${address_line_2}           Test Street
${address_line_3}           Paradise
${address_town}             London
${address_county}           Middlesex
${address_postcode}         NW11 8AJ
${applicant_first_name}     Sherlock
${applicant_last_name}      Holmes
${applicant_email}          sherlock@holmes.com
${comp_title}               Improved organisation search performance competition

*** Test Cases ***
Companies House: Valid company name
    [Documentation]    INFUND-887  IFS-7723
    [Tags]  HappyPath
    When the user searches for organisation     ROYAL MAIL
    Then the user should see the element        link = ${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}

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
    Given the user clicks the button/link         link = Back to companies house search results
    When the user searches for organisation       innoavte
    When the user enters text to a text field     id = organisationSearchName    innoavte
    And the user clicks the button/link           id = org-search
    Then the user should see the element          jQuery = p:contains("matching the search") span:contains("0") + span:contains("Companies") + span:contains("innoavte")

Companies House: Valid registration number
    [Documentation]    INFUND-8870  IFS-7723
    [Tags]  HappyPath
    When the user searches for organisation      00445790
    Then the user clicks the button/link         jQuery = a:contains("Next")
    And the user should see the element          link = TESCO PLC

Companies House: Empty company name field
    [Documentation]    INFUND-887  IFS-7723
    [Tags]
    When the user searches for organisation     ${EMPTY}
    Then the user should see the element        jQuery = p:contains("matching the search") span:contains("0") + span:contains("Companies")

Companies House: Empty company name field validation message
    [Documentation]    IFS-7723  IFS-7722
    [Tags]
    Given the user clicks the button/link                  link = Back to enter your organisation's details
    When the user searches for organisation                ${EMPTY}
    Then the user should see a field and summary error     You must enter an organisation name or company registration number.

Companies House: Search for a dissolved company and the result should be disabled
    [Documentation]    IFS-9103
    When the user searches for organisation       UNIACE
    And the user clicks the button/link           jQuery = a:contains("Next")
    Then the user should not see the element      link = UNIACE LIMITED
    And the user should see the element           jQuery = span p:contains("UNIACE LIMITED")

Companies House: Search for a liquidated company and the result should be disabled
    [Documentation]    IFS-9103
    When the user searches for organisation       TESCO AQUA
    Then the user should not see the element      link = TESCO AQUA (FINCO1) LIMITED
    And the user should see the element           jQuery = span p:contains("TESCO AQUA (FINCO1) LIMITED")

Companies House: No content message should be displayed when the search results are less than 400
    [Documentation]    IFS-9103
    When the user clicks the button/link         jQuery = a:contains("Next")
    Then the user should not see the element     jQuery = p:contains("This is the last page of search results and we have shown you the closest 400 matches.")

Not in Companies House: Enter details manually link
    [Documentation]    INFUND-888  IFS-7724
    Given the user searches for organisation                    Not exist
    When the user clicks link to find out what to do
    Then the user clicks link to enter its details manually

Not in Companies House: Return to the original search page
     [Documentation]    INFUND-888  IFS-7724
     Given the user clicks the button/link     link = Back to organisations not registered on Companies House
     When the user clicks the button/link      link = Back to Companies House search results
     Then the user clicks the button/link      link = Back to enter your organisation's details
     And the user should see the element       jQuery = h1:contains("Enter your organisation's details")

Not in Companies House: Manually add the details validation message
    [Documentation]    INFUND-888  IFS-7724
    Given the user navigates to the page                   ${server}/organisation/create/organisation-type/manually-enter-organisation-details
    When the user clicks the button/link                   jQuery = button:contains("Save and continue")
    And the user should see a field and summary error      You must enter your organisation's name.
    Then the user should see a field and summary error     Search using a valid postcode or enter the address manually.

Not in Companies House: Manually add the details as a new user and pass to the confirmation page
    [Documentation]    INFUND-888  IFS-7724
    Given the user manually adds company details                         ${organisation_name}  ${organisation_number}  ${business_type}  ${sic_code}  ${executive_officer}
    And the user enters address manually                                 ${address_line_1}  ${address_line_2}  ${address_line_3}  ${address_town}  ${address_county}  ${address_postcode}
    When the user clicks the button/link                                 jQuery = button:contains("Save and continue")
    Then the user confirms and saves company details                     Business  ${business_type}  ${organisation_name}  ${organisation_number}  ${sic_code}  ${executive_officer}  ${address_line_1}  ${address_line_2}  ${address_line_3}  ${address_town}  ${address_county}  ${address_postcode}  true
    And user checks back link and click save and continue
    And the user verifies his email and checks his organisation name     ${applicant_first_name}  ${applicant_last_name}  ${applicant_email}

Companies House: Get Date of incorporation, SIC codes, address and directors details for existing companies that do not have these details
    [Documentation]    IFS-9156
    Given log in as a different user                               &{WebTestUserCredentials}
    And the user select the competition and starts application     ${comp_title}
    When the user selects the radio button                         createNewApplication  true
    And the user clicks the button/link                            jQuery = .govuk-button:contains("Continue")
    Then the user clicks the button/link                           jQuery = button:contains("Save and continue")
    And the user can see organisation details in db

*** Keywords ***
Applicant goes to the organisation search page
    the guest user opens the browser
    the user navigates to the page                            ${frontDoor}
    the user clicks the button/link in the paginated list     link = ${createApplicationOpenInternationalCompetition}
    the user clicks the button/link                           link = Start new application
    the user clicks the button/link                           link = Continue and create an account
    user selects where is organisation based
    the user clicks the button/link                           jQuery = span:contains("Business")
    the user clicks the button/link                           jQuery = button:contains("Save and continue")

the user can see organisation details in db
    Connect to Database    @{database}
    ${result} =  get details of existing organisation
    log   ${result}
    Should not Be Empty     ${result}

user selects where is organisation based
    the user selects the radio button     international  isNotInternational
    the user clicks the button/link       id = international-organisation-cta

user checks back link and click save and continue
    the user clicks the button/link     link = Back to confirm your organisation
    the user clicks the button/link     jQuery = button:contains("Save and continue")

