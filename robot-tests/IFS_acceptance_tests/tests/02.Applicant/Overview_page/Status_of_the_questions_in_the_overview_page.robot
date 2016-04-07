*** Settings ***
Documentation     INFUND-39: As an applicant and I am on the application overview, I can select a section of the application, so I can see the status of each subsection in this section
...
...               INFUND-1072: As an Applicant I want to see the Application Overview page redesigned so that they meet the agreed style
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Status changes when we assign a question to the collaborator
    [Documentation]    INFUND-39
    [Tags]    Applicant    Overview    HappyPath
    Given the user navigates to the page    ${project_summary_url}
    When the Applicant edits the "Project summary" question
    And the applicant assigns the "Project Summary" question    Jessica Doe
    Then the "assign to" should be correct for the "Project summary" question
    And the blue flag should not be visible

The applicant can assign re-assign a question from the overview page
    [Documentation]    INFUND-39
    [Tags]    Applicant    Overview
    Given the user navigates to the page    ${application_overview_url}
    When the applicant assigns the "Project Summary" question      Steve Smith
    Then the applicant should see a blue flag for the Project Summary question (overview page)
    And the assign button should say Assigned to:You

*** Keywords ***
the Applicant edits the "Project summary" question
    Clear Element Text    css=#form-input-11 .editor
    Input Text    css=#form-input-11 .editor    Check last updated date@#$
    Focus    css=.app-submit-btn
    Sleep    2s

the "assign to" should be correct for the "Project Summary" question
    Go To    ${APPLICATION_OVERVIEW_URL}
    Page Should Contain Element    jQuery=#section-1 .section:nth-child(2) .column-third button strong
    Element Should Contain   jQuery=#section-1 .section:nth-child(2) .column-third button strong    Jessica Doe

the applicant assigns the "Project Summary" question
    [Arguments]    ${assignee_name}
    Click Element    jQuery=#section-1 .section:nth-child(2) .assign-button button
    Click Element     jQuery=button:contains("${assignee_name}")

the applicant should see a blue flag for the Project Summary question (overview page)
    #Reload Page
    Wait Until Page Contains Element    jQuery=#section-1 .section:nth-child(2) .assigned

the blue flag should not be visible
    Element Should Not Be Visible    jQuery=#section-1 .section:nth-child(2) .assigned

the assign button should say Assigned to:You
    Element Should Contain    jQuery=#section-1 .section:nth-child(2) .column-third button strong    You
