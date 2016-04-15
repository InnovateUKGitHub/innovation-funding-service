*** Settings ***
Documentation     -INFUND-46: As a lead applicant and I am on the application form on an open application, I can review & submit the application, so I can see an overview of the application and the status of each section.
...
...               -INFUND-1075: As an Applicant I want to see the Application Summary page redesigned so that they meet the agreed style
Suite Setup       Run Keywords    Guest user log-in    &{lead_applicant_credentials}
...               AND    The user navigates to the page    ${SUMMARY_URL}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
All sections present in the summary page
    [Documentation]    INFUND-193
    ...
    ...    INFUND-1075
    [Tags]    Summary    HappyPath
    Then all the sections should be visible

All questions are present in the summary page
    [Documentation]    INFUND-1075
    [Tags]    Summary
    Then all the questions should be visible

When Applicant clicks the "Scope" this section is expanded
    [Documentation]    INFUND-1075
    [Tags]  Summary
    When the user clicks the button/link    jQuery=button:contains("Scope")
    Then the Scope section should be expanded

Edit link navigates to the application form
    [Documentation]    INFUND-193
    [Tags]  Summary
    When the user clicks the button/link    jQuery=button:contains("Project summary")
    And the user clicks the button/link    css=#form-input-11 .textarea-footer button.button
    Then the user is on the page    ${PROJECT_SUMMARY_EDIT_URL}

Application overview button
    [Documentation]    INFUND-1075
    ...
    ...    INFUND-841
    [Tags]  Summary
    Given the user navigates to the page    ${SUMMARY_URL}
    When the user clicks the button/link    link=Application Overview
    Then the user is on the page    ${APPLICATION_OVERVIEW_URL}

*** Keywords ***
all the sections should be visible
    Page Should Contain Element    css=.section-overview section:nth-of-type(1)
    Page Should Contain Element    css=.section-overview section:nth-of-type(2)
    Page Should Contain Element    css=.section-overview section:nth-of-type(3)

all the questions should be visible
    [Documentation]    What this test is doing:
    ...
    ...    Checking if there are 3 main sections (Details, Application Questions and Finances) and then counting if the first section has 4 subsections, the second 10 and the third 1.
    Page Should Contain Element    css=.section-overview section:nth-of-type(1) .collapsible:nth-of-type(4)
    Page Should Contain Element    css=.section-overview section:nth-of-type(2) .collapsible:nth-of-type(10)
    Page Should Contain Element    css=.section-overview section:nth-of-type(3) .collapsible:nth-of-type(1)

the Scope section should be expanded
    Page Should Contain Element    css=.section-overview > section:first-child .collapsible:nth-of-type(4) > h3 button[aria-expanded="true"]
    Page Should Contain Element    css=.section-overview > section:first-child .collapsible:nth-of-type(4) > div[aria-hidden="false"]
