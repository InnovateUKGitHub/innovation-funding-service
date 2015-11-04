*** Settings ***
Documentation     -INFUND-46: As a lead applicant and I am on the application form on an open application, I can review & submit the application, so I can see an overview of the application and the status of each section.
Test Setup       Login as User    &{lead_applicant_credentials}
Test Teardown    TestTeardown User closes the browser
Resource          ../GLOBAL_LIBRARIES.robot
Resource          ../GLOBAL_VARIABLES.robot
Resource          ../Login_actions.robot
Resource          ../USER_CREDENTIALS.robot
Resource          ../Applicant_actions.robot

*** Test Cases ***
Verify all sections present in the summary page
    [Documentation]    -INFUND-193
    [Tags]    Applicant
    Given the Applicant is in the Summary page
    Then all the sections should be visible

Verify that clicking the edit link in the summary redirects the applicant to the application
    [Documentation]    -INFUND-193
    [Tags]    Applicant
    Given the Applicant is in the Summary page
    When the user clicks the edit link in the summary page
    Then the applicant should redirect to the Application form

*** Keywords ***
the Applicant is in the Summary page
    Go To    ${SUMMARY_URL}

all the sections should be visible
    Page Should Contain Element    css=#content > div.collapsible.section-overview > h2:nth-child(1) > button
    Page Should Contain Element    css=#content > div.collapsible.section-overview > h2:nth-child(3) > button
    Page Should Contain Element    css=#content > div.collapsible.section-overview > h2:nth-child(5) > button
    Page Should Contain Element    css=#content > div.collapsible.section-overview > h2:nth-child(7) > button
    Page Should Contain Element    css=#content > div.collapsible.section-overview > h2:nth-child(9) > button
    Page Should Contain Element    css=#content > div.collapsible.section-overview > h2:nth-child(11) > button

the user clicks the edit link in the summary page
    Click Element    css=#content > div.collapsible.section-overview > h2:nth-child(1) > button
    Click Element    css=#question-11 > div > div > div > a

the applicant should redirect to the Application form
    Location Should Be    ${QUESTION11_URL}
