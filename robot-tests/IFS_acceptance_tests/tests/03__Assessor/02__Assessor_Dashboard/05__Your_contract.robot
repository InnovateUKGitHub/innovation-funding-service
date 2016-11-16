*** Settings ***
Documentation     INFUND-1481 As an assessor I need to review and accept the Innovate UK Assessor contract so that I am able to assess a competition
...
...               INFUND-5628 As an assessor I want to be able to monitor my contract expiry so that I can be sure that I am eligible to assess competitions
...
...               INFUND-5645 As an assessor I want to be able to review annexes to the contract from the same screen so that I have all the information I need about assessing competitions
...
...               INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
Suite Setup       guest user log-in    worth.email.test+assessor1@gmail.com    Passw0rd
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Server-side validations
    [Documentation]    INFUND-1481
    ...
    ...    INFUND-5432
    [Tags]
    Given The user should see the element    link=your contract    #his checks the alert message on the top od the page
    And the user clicks the button/link    jQuery=a:contains("Your contract")
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see an error    Please agree to the terms and conditions

Terms and Conditions
    [Documentation]    INFUND-1481
    When the user clicks the button/link    link=Download terms of contract
    Then the user should be redirected to the correct page without the usual headers    ${Server}/assessment/documents/AssessorServicesAgreementContractIFSAug2016.pdf
    And The user goes back to the previous page
    [Teardown]    The user navigates to the page    ${Server}/assessment/profile/terms

Review Annexes
    [Documentation]    INFUND-5645
    When the user clicks the button/link    link=Annex A
    Then the user should see the text in the page    Technology programme: Assessor services
    And the user clicks the button/link    link=Back to your terms of contract
    And the user clicks the button/link    link=Annex B
    And the user should see the text in the page    Travel and subsistence rates for non-civil service contracted personnel
    And the user clicks the button/link    link=Back to your terms of contract
    And the user clicks the button/link    link=Annex C
    And the user should see the text in the page    Information management
    And the user clicks the button/link    link=Back to your terms of contract

Client-side validations and redirect to dashboard
    [Documentation]    INFUND-1481
    ...
    ...
    ...    INFUND-5432
    [Tags]
    When the user selects the checkbox    id=agreesToTerms1
    And the user should not see an error in the page
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}
    And The user should not see the element    link=your contract    #his checks the alert message on the top od the page

Agreement Confirmation
    [Documentation]    INFUND-5628
    Then the user clicks the button/link    jQuery=a:contains("Your contract")
    And the user should see the text in the page    You signed the contract on
