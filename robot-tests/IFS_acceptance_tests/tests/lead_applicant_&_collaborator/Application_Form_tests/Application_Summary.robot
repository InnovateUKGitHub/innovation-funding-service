*** Settings ***
Documentation     -INFUND-46: As a lead applicant and I am on the application form on an open application, I can review & submit the application, so I can see an overview of the application and the status of each section.
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Verify all sections present in the summary page
    [Documentation]    -INFUND-193
    [Tags]    Applicant
    Given the Applicant is in the Summary page
    Then all the sections should be visible

Verify all questions are present in the summary page
    Given the Applicant is in the Summary page
    Then all the questions should be visible

Verify that when Applicant clicks the "Scope" this section is expanded
    [Tags]    Applicant    Overview
    Given the Applicant is in the Summary page
    When the Applicant clicks the "Scope" section
    Then the "Scope" section should be expanded

Verify that clicking the edit link in the summary redirects the applicant to the application
    [Documentation]    -INFUND-193
    [Tags]    Applicant
    Given the Applicant is in the Summary page
    When the Applicant clicks the "Project Summary" section
    And the user clicks the edit link in the summary section
    Then the applicant should redirect to the project summary

*** Keywords ***
the Applicant is in the Summary page
    Go To    ${SUMMARY_URL}

all the sections should be visible
    Page Should Contain Element    css=.section-overview section:nth-of-type(1)
    Page Should Contain Element    css=.section-overview section:nth-of-type(2)
    Page Should Contain Element    css=.section-overview section:nth-of-type(3)

all the questions should be visible
    Page Should Contain Element    css=.section-overview section:nth-of-type(1) .collapsible:nth-of-type(4)
    Page Should Contain Element    css=.section-overview section:nth-of-type(2) .collapsible:nth-of-type(9)
    Page Should Contain Element    css=.section-overview section:nth-of-type(3) .collapsible:nth-of-type(1)

the user clicks the edit link in the summary section
    Click Element    css=#form-input-11 .textarea-footer a

the applicant should redirect to the project summary
    Location Should Be    ${PROJECT_SUMMARY_URL}

the Applicant clicks the "Scope" section
    [Documentation]    1. click second section
    Click Element    css=.section-overview > section:first-child .collapsible:nth-of-type(4) > h3 button

the Applicant clicks the "Project Summary" section
    Click Element    css=.section-overview > section:first-child .collapsible:nth-of-type(2) > h3 button

the "Scope" section should be expanded
    Page Should Contain Element    css=.section-overview > section:first-child .collapsible:nth-of-type(4) > h3 button[aria-expanded="true"]
    Page Should Contain Element    css=.section-overview > section:first-child .collapsible:nth-of-type(4) > div[aria-hidden="false"]
