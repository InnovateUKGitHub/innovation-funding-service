*** Settings ***
Documentation     -INFUND-888 As an applicant I want to be able to manually add an unverified company as part of registration as I am not yet registered with Companies House so that I can enter a competition as a Start-up company
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${find_org_on_company_house_url}    ${SERVER}/organisation/create/find-business
${organisation_name}    Top of the Popps

*** Test Cases ***
Not in Companies House company link
    [Documentation]    INFUND-888
    [Tags]    HappyPath
    Given the user navigates to the page    ${find_org_on_company_house_url}
    And the user should see the text in the page    Not on Companies House?
    When the user clicks the button/link    name=not-in-company-house
    Then the user should see the text in the page    Organisation name
    And the user should see the text in the page    Postcode

The address can be manually added and this persists on refresh
    [Documentation]    INFUND-888
    [Tags]    HappyPath
    When the user clicks the button/link    name=manual-address
    Then the user should see the text in the page    Street
    And the user should see the text in the page    Town
    And the user should see the text in the page    County
    And the user should see the text in the page    Postcode
    And the user reloads the page
    And the user should see the text in the page    Street

The address can be manually added and the details pass to the confirmation page
    [Documentation]    INFUND-888
    [Tags]    HappyPath
    When the user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    The East Wing
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine2    Popple Manor
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine3    1, Popple Boulevard
    And the user enters text to a text field    id=addressForm.selectedPostcode.town    Poppleton
    And the user enters text to a text field    id=addressForm.selectedPostcode.county    Poppleshire
    And the user enters text to a text field    id=addressForm.selectedPostcode.postcode    POPPS123
    And the user enters text to a text field    name=organisationName    Top of the Popps
    And the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see the text in the page    The East Wing
    And the user should see the text in the page    Popple Manor
    And the user should see the text in the page    1, Popple Boulevard
    And the user should see the text in the page    Poppleton
    And the user should see the text in the page    POPPS123

*** Keywords ***
