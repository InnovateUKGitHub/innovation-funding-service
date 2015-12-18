*** Settings ***
Documentation     -INFUND-888 As an applicant I want to be able to manually add an unverified company as part of registration as I am not yet registered with Companies House so that I can enter a competition as a Start-up company
Suite Teardown    User closes the browser
Test Setup        Login as user    &{lead_applicant_credentials}
Test Teardown     User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot




*** Variables ***

${find_org_on_company_house_url}    ${SERVER_BASE}/application/create/find-business
${address_street_line_one}          The East Wing
${address_street_line_two}          Popple Manor
${address_street_line_three}        1, Popple Boulevard
${address_town}                     Poppleton
${address_county}                   Poppleshire
${address_postcode}                 POPPS123
${organisation_name}                Top of the Popps



*** Test Cases ***


Applicant can see the option to add a company that isn't on the Companies House List
    [Tags]  Applicant   Company     Companies House
    Given the applicant is on the "Find your organisation on companies House" page
    And the applicant can see an option to say their company is "Not on Companies House"
    When the applicant clicks the option to say their company is "Not on Companies House"
    Then the applicant can see the option to enter their organisation's name and postcode


Applicant can see the option to manually add the address for a company that isn't on the Companies House list, and this persists on refresh
    [Tags]  Applicant   Company     Companies House
    Given the applicant is on the "Find your organisation on companies House" page
    And the applicant can see an option to say their company is "Not on Companies House"
    When the applicant clicks the option to say their company is "Not on Companies House"
    Then the applicant should have the option to enter an address manually
    And the applicant clicks the option to enter the address manually
    And the applicant can see the fields to enter their address
    And the applicant can reload the page
    And the applicant can still see the address fields




Applicant can manually enter an address for a company that isn't on the Companies House list, and these details pass to the confirmation page
    [Tags]  Applicant   Company     Companies House
    Given the applicant is on the "Find your organisation on companies House" page
    And the applicant clicks the option to say their company is "Not on Companies House"
    And the applicant clicks the option to enter the address manually
    When the applicant enters an address manually
    And the applicant adds in an organisation name and size
    And the applicant submits the manual address form
    Then the confirmation page should contain the applicant's correct details




*** Keywords ***

the applicant is on the "Find your organisation on companies House" page
    Go To      ${find_org_on_company_house_url}

the applicant can see an option to say their company is "Not on Companies House"
    Page Should Contain     Not on Companies House?

the applicant clicks the option to say their company is "Not on Companies House"
    Click Element      name=not-in-company-house

the applicant can see the option to enter their organisation's name and postcode
    Page Should Contain     Organisation name
    Page Should Contain     Postcode

the applicant should have the option to enter an address manually
    Page Should Contain     Enter address manually

the applicant clicks the option to enter the address manually
    Click Element   name=manual-address

the applicant can see the fields to enter their address
    Page Should Contain     Street
    Page Should Contain     Town
    Page Should Contain     County
    Page Should Contain     Postcode


the applicant can reload the page
    Reload Page
    Alert Should Be Present

the applicant can still see the address fields
    the applicant can see the fields to enter their address

the applicant enters an address manually
    Input Text  id=street        ${address_street_line_one}
    Input Text  id=street-2      ${address_street_line_two}
    Input Text  id=street-3      ${address_street_line_three}
    Input Text  id=town          ${address_town}
    Input Text  id=county        ${address_county}
    Input Text  id=postcode      ${address_postcode}


the applicant adds in an organisation name and size
    Input Text  name=companyHouseName     ${organisation_name}
    Input Text  name=organisationName       ${organisation_name}
    Select Radio Button     organisationSize   SMALL

the applicant submits the manual address form
    Click Button    Continue

the confirmation page should contain the applicant's correct details
    Page Should Contain     ${address_street_line_one}
    Page Should Contain     ${address_street_line_two}
    Page Should Contain     ${address_street_line_three}
    Page Should Contain     ${address_town}
    Page Should Contain     ${address_postcode}


