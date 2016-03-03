*** Settings ***
Documentation     INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes.
...
...               INFUND-1815: Small text changes to registration journey following user testing
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot


*** Test Cases ***
Finance sub-sections
    [Documentation]    INFUND-192
    [Tags]    Applicant    Finance
    When the user navigates to the page      ${YOUR_FINANCES_URL}
    Then the Applicant should see all the "Your Finance" Sections

Organisation name visible in the Finance section
    [Documentation]    INFUND-1815
    [Tags]    Applicant    Finance
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    Then the Organisation name should be seen in the Finance section

Guidance in the 'Your Finances' section
    [Documentation]    INFUND-192
    [Tags]    Applicant    Finance
    Given the user navigates to the page      ${YOUR_FINANCES_URL}
    When the Applicant opens the Labour sub-section
    And the Applicant clicks the "Labour costs guidance"
    Then the guidance text should be visible

*** Keywords ***
the Applicant should see all the "Your Finance" Sections
    Page Should Contain Element    css=.question section:nth-of-type(1) button
    Page Should Contain Element    css=.question section:nth-of-type(2) button
    Page Should Contain Element    css=.question section:nth-of-type(3) button
    Page Should Contain Element    css=.question section:nth-of-type(4) button
    Page Should Contain Element    css=.question section:nth-of-type(5) button
    Page Should Contain Element    css=.question section:nth-of-type(6) button
    Page Should Contain Element    css=.question section:nth-of-type(7) button

the Applicant opens the Labour sub-section
    Click Element    css=.question section:nth-of-type(1) button

the Applicant clicks the "Labour costs guidance"
    Click Element    css=#collapsible-1 summary

the guidance text should be visible
    Element Should Be Visible    css=#details-content-0 p

the Organisation name should be seen in the Finance section
    page should contain    Provide the project costs for 'Empire Ltd'
    page should contain    'Empire Ltd' Total project costs
