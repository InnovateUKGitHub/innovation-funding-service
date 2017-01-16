*** Settings ***
Documentation     INFUND-7042 As a member of the competitions team I can see list of applications with assessor statistics on the 'Manage Applications' dashboard so...
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
View the list off the applications
    [Documentation]    INFUND-7042
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    When The user clicks the button/link    jQuery=.button:contains("Manage applications")
    Then the user should see the element    jQuery=tr:nth-child(1) td:contains(Wessex University)
    And The user should see the element    jQuery=tr:nth-child(1) td:contains(Rainfall)
    And The user should see the element    jQuery=tr:nth-child(1) td:contains(1)
    And The user should see the element    jQuery=tr:nth-child(1) td:contains(0)

The user can click the View Progress button
    [Documentation]    INFUND-7042
    When The user clicks the button/link    jQuery=tr:nth-child(1) a:contains(View progress)
    Then The user should see the text in the page    00000015: Rainfall
    [Teardown]    The user clicks the button/link    link=Allocate applications

The Application number should navigate to the Application Overview
    [Documentation]    INFUND-7042
    When the user clicks the button/link    link=00000015
    Then The user should see the text in the page    00000015: Rainfall
