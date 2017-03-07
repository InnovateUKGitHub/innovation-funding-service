*** Settings ***
Documentation     INFUND-7058 As an assessor I can read the terms and conditions so that I know what I have agreed to
Suite Setup       guest user log-in    ${test_mailbox_one}+jeremy.alufson@gmail.com    Passw0rd
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor    Pending
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Terms and conditions
    [Documentation]    INFUND-7058
    [Tags]
    Given The user should see the element    link=read terms and conditions
    When the user clicks the button/link    jQuery=a:contains("read terms and conditions")
    Then the user should see the text in the page    Terms and conditions
    And the user should see the text in the page    Overview
    And the user should see the text in the page    General terms of use
    And the user should see the text in the page    Acceptable use policy
    And the user should see the text in the page    Privacy policy
    And the user should see the text in the page    Information Management Policy

Link from terms and conditions to Information management policy
    [Documentation]    INFUND-7058
    [Tags]
    Given The user clicks the button/link    link=Information Management Policy
    Then the user should see the text in the page    Personal information charter
