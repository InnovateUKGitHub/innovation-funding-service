*** Settings ***
Documentation     INFUND-1481 As an assessor I need to review and accept the Innovate UK Assessor contract so that I am able to assess a competition
...
...               INFUND-5628 As an assessor I want to be able to monitor my contract expiry so that I can be sure that I am eligible to assess competitions.
Suite Setup       guest user log-in    &{assessor2_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Server-side validations
    [Documentation]    INFUND-1481
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("Your contract")
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see an error    Please agree to the terms and conditions

Download terms and conditions
    [Documentation]    INFUND-1481
    When the user clicks the button/link    link=Download terms of contract
    Then the user should be redirected to the correct page without the usual headers    ${Server}/assessment/documents/AssessorServicesAgreementContractIFSAug2016.pdf
    And The user goes back to the previous page
    [Teardown]    The user navigates to the page    ${Server}/assessment/profile/terms

Client-side validations and redirect to dashboard
    [Documentation]    INFUND-1481
    [Tags]
    When the user selects the checkbox    id=agreesToTerms1
    And the user should not see an error in the page
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}

Agreement Confirmation
    [Documentation]    INFUND-5628
    Then the user clicks the button/link    jQuery=a:contains("Your contract")
    Then the user should see the text in the page    You signed the contract on
