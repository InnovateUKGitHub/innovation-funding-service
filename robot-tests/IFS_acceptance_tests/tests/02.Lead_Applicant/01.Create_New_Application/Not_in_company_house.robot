*** Settings ***
Documentation     -INFUND-888 As an applicant I want to be able to manually add an unverified company as part of registration as I am not yet registered with Companies House so that I can enter a competition as a Start-up company
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${find_org_on_company_house_url}    ${SERVER_BASE}/organisation/create/find-business
${organisation_name}    Top of the Popps

*** Test Cases ***
Applicant can see the Not in Companies House company link
    [Documentation]    INFUND-888
    [Tags]    Applicant    Company    Companies House    HappyPath
    Given the user navigates to the page    ${find_org_on_company_house_url}
    And The user should see the text in the page    Not on Companies House?
    When The user clicks the button/link    name=not-in-company-house
    Then The user should see the text in the page    Organisation name
    And The user should see the text in the page    Postcode

Applicant can manually add the address and this persists on refresh
    [Documentation]    INFUND-888
    [Tags]    Applicant    Company    Companies House   Pending
    # Pending due to INFUND-2019
    When The user clicks the button/link    name=manual-address
    Then The user should see the text in the page    Street
    And The user should see the text in the page    Town
    And The user should see the text in the page    County
    And The user should see the text in the page    Postcode
    And the applicant can reload the page
    And The user should see the text in the page    Street

Applicant can manually can enter and see details pass to the confirmation page
    [Documentation]    INFUND-888
    [Tags]    Applicant    Company    Companies House    Pending
    # Pending because of the INFUND-1816
    When The user enters text to a text field    id=street    The East Wing
    And The user enters text to a text field    id=street-2    Popple Manor
    And The user enters text to a text field    id=street-3    1, Popple Boulevard
    And The user enters text to a text field    id=town    Poppleton
    And The user enters text to a text field    id=county    Poppleshire
    And The user enters text to a text field    id=postcode    POPPS123
    And The user enters text to a text field    name=organisationName    Top of the Popps
    And The user clicks the button/link    jQuery=button:contains("Continue")
    Then The user should see the text in the page    The East Wing
    And The user should see the text in the page    Popple Manor
    And The user should see the text in the page    1, Popple Boulevard
    And The user should see the text in the page    Poppleton
    And The user should see the text in the page    POPPS123

*** Keywords ***
the applicant can reload the page
    Reload Page
    #Alert Should Be Present

user selects the checkbox
    Select Checkbox    name=manual-address
