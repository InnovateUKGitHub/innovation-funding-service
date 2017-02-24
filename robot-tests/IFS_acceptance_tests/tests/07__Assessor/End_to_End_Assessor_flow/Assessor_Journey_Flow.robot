*** Settings ***
Documentation     INFUND-8092 E2E for the Assessor Journey Flow
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin    Assessor    Pending
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Invite a new user
    [Documentation]    INFUND-8092
    Given the user clicks the button/link    link=Invite
    When the user clicks the button/link    jQuery=span:contains("Add a non-registered assessor to your list")
    When The user enters text to a text field    css=#invite-table tr:nth-of-type(1) td:nth-of-type(1) input
    And The user enters text to a text field    css=#invite-table tr:nth-of-type(1) td:nth-of-type(2) input
    And the user selects the option from the drop-down menu    Emerging and enabling technologies    css=.js-progressive-group-select
    And the user selects the option from the drop-down menu    Data    id=grouped-innovation-area
    And the user clicks the button/link    jQuery=.button:contains("Add assessors to list")
    Then the user should see the element    css=.no
    And The user should see the element
    When the user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Invite individual)
    And the user clicks the button/link    jQuery=.button:contains(Send invite)
    And The user clicks the button/link    link=Overview
    And the user should see the text in the page
