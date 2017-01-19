*** Settings ***
Documentation     INFUND-7042 As a member of the competitions team I can see list of applications with assessor statistics on the 'Manage Applications' dashboard so...
...
...               INFUND-7046  As a member of the competitions team I can view the application progress dashboard for an application so that I can see the application details
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
    [Documentation]    INFUND-7042, INFUND-7046
    When The user clicks the button/link    jQuery=tr:nth-child(7) a:contains(View progress)
    Then The user should see the text in the page    00000021: Intelligent water system
    And the user should see the text in the page    Mo Juggling Mo Problems Ltd
    And the user should see the text in the page    University of Bath
    And the user should see the text in the page    Cardiff University

The user can click the review applicaton button
    [Documentation]    INFUND-7046
    [Tags]
    When the user clicks the button/link    link=Review application
    Then the user should see the text in the page     Application Overview
    [Teardown]    The user navigates to the page    ${Application_management_dashboard}

The Application number should navigate to the Application Overview
    [Documentation]    INFUND-7042
    When the user clicks the button/link    link=00000015
    Then The user should see the text in the page    00000015: Rainfall

