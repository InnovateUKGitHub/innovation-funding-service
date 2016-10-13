*** Settings ***
Documentation     INFUND-39: As an applicant and I am on the application overview, I can select a section of the application, so I can see the status of each subsection in this section
...
...               INFUND-1072: As an Applicant I want to see the Application Overview page redesigned so that they meet the agreed style
Suite Setup       Log in create a new invite application invite academic collaborators and accept the invite
Suite Teardown    TestTeardown User closes the browser
Force Tags        Email    Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot
Resource          ../../../resources/keywords/EMAIL_KEYWORDS.robot

*** Test Cases ***
Status changes when we assign a question
    [Documentation]    INFUND-39
    [Tags]    HappyPath
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user clicks the button/link    link=Project summary
    When the Applicant edits the Project summary
    And the applicant assigns the Project Summary    Arsene Wenger
    Then the assign status should be correct for the Project Summary
    And the blue flag should not be visible

Re-assign is possible from the overview page
    [Documentation]    INFUND-39
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    When the applicant assigns the Project Summary question from the overview page    Steve Smith
    Then a blue flag should be visible for the Project Summary in overview page
    And the assign button should say Assigned to:You

*** Keywords ***
the Applicant edits the Project summary
    Clear Element Text    css=#form-input-11 .editor
    The user enters text to a text field    css=#form-input-11 .editor    Check last updated date@#$
    Focus    css=.app-submit-btn
    Sleep    1s

the assign status should be correct for the Project Summary
    the user navigates to the page    ${DASHBOARD_URL}
    the user clicks the button/link    link=Academic robot test application
    the user should see the element    jQuery=#section-1 .section:nth-child(2) .column-third button strong
    Element Should Contain    jQuery=#section-1 .section:nth-child(2) .column-third button strong    Arsene Wenger

the applicant assigns the Project Summary question from the overview page
    [Arguments]    ${assignee_name}
    the user clicks the button/link    jQuery=#section-1 .section:nth-child(2) .assign-button button
    the user clicks the button/link    jQuery=#section-1 .section:nth-child(2) button:contains("${assignee_name}")
    Sleep    500ms    # otherwise it stops while Assigning..

the applicant assigns the Project Summary
    [Arguments]    ${assignee_name}
    the user clicks the button/link    css=#form-input-11 .assign-button button
    the user clicks the button/link    jQuery=button:contains("${assignee_name}")

a blue flag should be visible for the Project Summary in overview page
    Wait Until Page Does Not Contain    Assigning to Steve Smith...    10s
    The user should see the element    jQuery=#section-1 .section:nth-child(2) .assigned

the blue flag should not be visible
    the user should not see the element    jQuery=#section-1 .section:nth-child(2) .assigned

the assign button should say Assigned to:You
    Element Should Contain    jQuery=#section-1 .section:nth-child(2) .column-third button strong    You
