*** Settings ***
Documentation     INFUND-39: As an applicant and I am on the application overview, I can select a section of the application, so I can see the status of each subsection in this section
...
...               INFUND-1072: As an Applicant I want to see the Application Overview page redesigned so that they meet the agreed style
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Status changes when we assign a question to the collaborator
    [Documentation]    INFUND-39
    [Tags]    Applicant    Overview
    Given Applicant goes to the 'project summary' question
    When the Applicant edits the "Project summary" question
    And the applicant assigns the "Project Summary" question to Jessica Doe    Jessica Doe
    Then the "assign to" should be correct for the "Project summary" question
    And the blue flag should not be visible

The applicant can assign re-assign a question from the overview page
    [Documentation]    INFUND-39
    [Tags]    Applicant    Overview
    Given Applicant goes to the overview page
    When the applicant assigns the "Project summary" question to "Steve Smith"    Steve Smith
    Then the applicant should see a blue flag in the Public description (overview page)
    And the assign button should say Assign to:You

*** Keywords ***
the Applicant edits the "Project summary" question
    Clear Element Text    css=#form-input-11 .editor
    Input Text    css=#form-input-11 .editor    Check last updated date@#$
    Focus    css=.app-submit-btn
    Sleep    2s

the applicant assigns the "Project Summary" question to Jessica Doe
    [Arguments]    ${assignee_name}
    Click Element    css=#form-input-11 .assign-button button
    Click Element    xpath=//*[@id="0"]//button[contains(text(),"${assignee_name}")]

the "assign to" should be correct for the "Project summary" question
    Go To    ${APPLICATION_OVERVIEW_URL}
    Page Should Contain Element    css=#form-input-11 .column-third button strong
    Element Should Contain    css=#form-input-11 .column-third button strong    Jessica Doe

the applicant assigns the "Project summary" question to "Steve Smith"
    [Arguments]    ${assignee_name}
    Click Element    css=#form-input-11 .assign-button button
    Click Element    xpath=//*[@id="collapsible-1"]//button[contains(text(),"${assignee_name}")]

the applicant should see a blue flag in the Public description (overview page)
    Wait Until Page Contains Element    css=#form-input-11 .assigned

the blue flag should not be visible
    Element Should Not Be Visible    css=#form-input-11 .assigned

the assign button should say Assign to:You
    Element Should Contain    css=#form-input-11 .column-third button strong    You
